package com.itheima.flink;

import com.itheima.pojo.PulsarTopicPojo;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.io.jdbc.JDBCAppendTableSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarSource;
import org.apache.flink.streaming.connectors.pulsar.internal.JsonDeser;
import org.apache.flink.types.Row;

import java.sql.Types;
import java.util.Properties;

// 基于Flink完成读取Pulsar中数据将消息数据写入到clickhouse中
public class ItcastFlinkToClickHouse {

    public static void main(String[] args) throws Exception {
        //1. 创建Flinnk流式处理核心环境类对象 和 Table API 核心环境类对象
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

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


        //2.2  转换数据操作: 将 PulsarTopicPojo 转换为ROW对象
        SingleOutputStreamOperator<Row> rowDataSteam = dataStreamSource.map(new MapFunction<PulsarTopicPojo, Row>() {
            @Override
            public Row map(PulsarTopicPojo pulsarTopicPojo) throws Exception {

                return Row.of(pulsarTopicPojo.getId(), pulsarTopicPojo.getSid(), pulsarTopicPojo.getIp(), pulsarTopicPojo.getCreate_time(),
                        pulsarTopicPojo.getSession_id(), pulsarTopicPojo.getYearInfo(), pulsarTopicPojo.getMonthInfo(), pulsarTopicPojo.getDayInfo(),
                        pulsarTopicPojo.getHourInfo(), pulsarTopicPojo.getSeo_source(), pulsarTopicPojo.getArea(), pulsarTopicPojo.getOrigin_channel(),
                        pulsarTopicPojo.getMsg_count(), pulsarTopicPojo.getFrom_url());
            }
        });


        //2.3: 设置sink操作写入到CK操作
        String insertSql = "insert into itcast_ck.itcast_ck_ems (id,sid,ip,create_time,session_id,yearInfo,monthInfo,dayInfo,hourInfo,seo_source,area,origin_channel,msg_count,from_url) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        JDBCAppendTableSink tableSink = JDBCAppendTableSink.builder()
                .setDrivername("ru.yandex.clickhouse.ClickHouseDriver")
                .setDBUrl("jdbc:clickhouse://node2:8123/itcast_ck")
                .setQuery(insertSql)
                .setBatchSize(1)
                .setParameterTypes(Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.INTEGER,Types.VARCHAR)
                .build();

        tableSink.emitDataStream(rowDataSteam);


        //3. 提交执行
        env.execute("itcast_to_ck");

    }
}
