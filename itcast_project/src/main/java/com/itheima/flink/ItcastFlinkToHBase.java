package com.itheima.flink;

import com.itheima.pojo.PulsarTopicPojo;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarSource;
import org.apache.flink.streaming.connectors.pulsar.internal.JsonDeser;
import org.apache.flink.streaming.util.serialization.PulsarDeserializationSchema;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.Properties;

// 基于Flink消费Pulsar数据, 然后将数据灌入到HBase中, 完成数据备份, 以及后续即席查询和离线分析
public class ItcastFlinkToHBase {

    public static void main(String[] args) throws Exception {

        //1. 创建Flinnk流式处理核心环境类对象 和 Table API 核心环境类对象
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //2. 添加Source组件, 从Pulsar中读取消息数据
        Properties props = new Properties();
        props.setProperty("topic","persistent://public/default/itcast_ems_tab");
        props.setProperty("partition.discovery.interval-millis","5000");
        FlinkPulsarSource<PulsarTopicPojo> pulsarSource = new FlinkPulsarSource<PulsarTopicPojo>(
                "pulsar://node1:6650,node2:6650,node3:6650","http://node1:8080,node2:8080,node3:8080",
                JsonDeser.of(PulsarTopicPojo.class),props);
        //2.1 设置pulsarSource组件在消费数据的时候, 默认从什么位置开始消费
        pulsarSource.setStartFromLatest();

        DataStreamSource<PulsarTopicPojo> dataStreamSource = env.addSource(pulsarSource);


        //2.2 转换为Flink Table

        Schema schema = Schema.newBuilder()
                .column("id", DataTypes.INT())
                .column("sid", DataTypes.STRING())
                .column("ip", DataTypes.STRING())
                .column("session_id", DataTypes.STRING())
                .column("create_time", DataTypes.STRING())
                .column("yearInfo", DataTypes.STRING())
                .column("monthInfo", DataTypes.STRING())
                .column("dayInfo", DataTypes.STRING())
                .column("hourInfo", DataTypes.STRING())
                .column("seo_source", DataTypes.STRING())
                .column("area", DataTypes.STRING())
                .column("origin_channel", DataTypes.STRING())
                .column("msg_count", DataTypes.INT())
                .column("from_url", DataTypes.STRING())
                .build();


        tableEnv.createTemporaryView("itcast_ems",dataStreamSource,schema);


        //2.3: 定义HBase的目标表
        String hTable = "create table itcast_h_ems("+
                "rowkey int,"+
                "f1 ROW<sid STRING,ip STRING,session_id STRING,create_time STRING,yearInfo STRING,monthInfo STRING,dayInfo STRING,hourInfo STRING,seo_source STRING,area STRING,origin_channel STRING,msg_count INT,from_url STRING>,"+
                "primary key(rowkey) NOT ENFORCED" +
                ") WITH ("+
                "'connector'='hbase-2.2',"+
                "'table-name'='itcast_h_ems',"+
                "'zookeeper.quorum'='node1:2181,node2:2181,node3:2181'"+
                ")";
        //4. 执行操作
        tableEnv.executeSql(hTable);

        tableEnv.executeSql("insert into itcast_h_ems select id,ROW(sid,ip,session_id,create_time,yearInfo,monthInfo,dayInfo,hourInfo,seo_source,area,origin_channel,msg_count,from_url) from itcast_ems");

    }


}
