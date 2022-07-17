package com.itheima.flink;

import com.alibaba.fastjson.JSON;
import com.itheima.pojo.PulsarTopicPojo;
import com.itheima.pojo.WebChatEms;
import com.itheima.pojo.WebChatTextEms;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarSink;
import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarSource;
import org.apache.flink.streaming.connectors.pulsar.config.RecordSchemaType;
import org.apache.flink.streaming.connectors.pulsar.internal.JsonSer;
import org.apache.flink.streaming.util.serialization.PulsarDeserializationSchema;
import org.apache.flink.streaming.util.serialization.PulsarSerializationSchemaWrapper;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

// 此类用于对Pulsar中消息数据进行预处理操作:
// 需求1: 完成两个表的join操作, 抽取核心字段
// 需求2: 对create_time字段进行拉宽:  年 月 天 小时
public class ItcastEmsFlink {

    public static void main(String[] args) throws Exception {

        //1. 创建Flink流式处理核心类对象
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2. 构建Flink Table 核心API对象
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);


        //3. 设置Source组件: 用于对接pulsar 从pulsar中消费消息数据
        Properties props = new Properties();
        props.setProperty("topic","persistent://public/default/itcast_canal_collect");
        props.setProperty("partition.discovery.interval-millis","5000");
        FlinkPulsarSource<String> pulsarSource = new FlinkPulsarSource<String>(
                "pulsar://node1:6650,node2:6650,node3:6650","http://node1:8080,node2:8080,node3:8080",
                PulsarDeserializationSchema.valueOnly(new SimpleStringSchema()),props);

        //3.1 设置pulsarSource组件在消费数据的时候, 默认从什么位置开始消费
        pulsarSource.setStartFromLatest();

        DataStreamSource<String> dataStreamSource = env.addSource(pulsarSource);

        //3.2: 对数据进行清洗操作: 过滤掉 "message"为"[]"的数据
        SingleOutputStreamOperator<String> filterDataStream = dataStreamSource.filter(new FilterFunction<String>() {
            // 如果返回false, 表示这个数据需要过滤掉, 如果返回true, 表示要留下这个数据
            @Override
            public boolean filter(String msg) throws Exception {
                Map<String, Object> msgMap = JSON.parseObject(msg, Map.class);
                return !"[]".equals(msgMap.get("message"));
            }
        });

        //3.3 抽取各个数据集中核心字段: webchatems 表中核心字段
        SingleOutputStreamOperator<WebChatEms> webChatEmsDataSteam = filterDataStream.flatMap(new FlatMapFunction<String, WebChatEms>() {
            @Override
            public void flatMap(String canalJson, Collector<WebChatEms> collector) throws Exception {

                //1. 解析Json数据
                Map canalMsgMap = JSON.parseObject(canalJson, Map.class);

                String dataMsg = (String) canalMsgMap.get("message");
                // 说明: List中一个Map集合的数据, 就是一行数据, 需要将一行数据转换为 webChatEms 对象
                List<Map<String, Object>> dataRows = (List<Map<String, Object>>) JSON.parse(dataMsg);

                //2. 遍历数据
                for (Map<String, Object> dataRow : dataRows) {

                    String type = (String) dataRow.get("type");

                    if ("INSERT".equals(type)) {
                        // 只有数据出现新增的操作, 才需要进行处理
                        //2.1: 获取当前这个消息是属于哪一个表的

                        String tableName = (String) dataRow.get("table");

                        if ("web_chat_ems".equals(tableName)) {
                            // 抽取核心字段
                            List<Map<String, String>> data = (List<Map<String, String>>) dataRow.get("data");

                            WebChatEms webChatEms = new WebChatEms();

                            for (Map<String, String> colAndVal : data) {
                                String columnName = colAndVal.get("columnName");
                                String columnValue = colAndVal.get("columnValue");

                                if ("id".equals(columnName)) {
                                    webChatEms.setId(Integer.parseInt(columnValue));
                                }

                                if ("session_id".equals(columnName)) {
                                    webChatEms.setSession_id(columnValue);
                                }

                                if ("ip".equals(columnName)) {
                                    webChatEms.setIp(columnValue);
                                }

                                if ("create_time".equals(columnName)) {
                                    webChatEms.setCreate_time(columnValue);
                                }

                                if ("area".equals(columnName)) {
                                    webChatEms.setArea(columnValue);
                                }

                                if ("origin_channel".equals(columnName)) {
                                    webChatEms.setOrigin_channel(columnValue);
                                }

                                if ("seo_source".equals(columnName)) {
                                    webChatEms.setSeo_source(columnValue);
                                }

                                if ("sid".equals(columnName)) {
                                    webChatEms.setSid(columnValue);
                                }

                                if ("msg_count".equals(columnName) && columnValue != null ) {
                                    webChatEms.setMsg_count(Integer.parseInt(columnValue));
                                }
                            }
                            // 将封装好的数据. 返回去即可
                            collector.collect(webChatEms);


                        }
                    }

                }
            }
        });

        //3.4 抽取各个数据集中核心字段: webChatTextEms 表中核心字段
        SingleOutputStreamOperator<WebChatTextEms> webChatTextEmsDataSteam = filterDataStream.flatMap(new FlatMapFunction<String, WebChatTextEms>() {
            @Override
            public void flatMap(String canalJson, Collector<WebChatTextEms> collector) throws Exception {
                //1. 解析Json数据
                Map canalMsgMap = JSON.parseObject(canalJson, Map.class);

                String dataMsg = (String) canalMsgMap.get("message");
                // 说明: List中一个Map集合的数据, 就是一行数据, 需要将一行数据转换为 webChatEms 对象
                List<Map<String, Object>> dataRows = (List<Map<String, Object>>) JSON.parse(dataMsg);

                //2. 遍历数据
                for (Map<String, Object> dataRow : dataRows) {

                    String type = (String) dataRow.get("type");

                    if ("INSERT".equals(type)) {
                        // 只有数据出现新增的操作, 才需要进行处理
                        //2.1: 获取当前这个消息是属于哪一个表的

                        String tableName = (String) dataRow.get("table");

                        if ("web_chat_text_ems".equals(tableName)) {

                            // 抽取核心字段
                            List<Map<String, String>> data = (List<Map<String, String>>) dataRow.get("data");

                            WebChatTextEms webChatTextEms = new WebChatTextEms();

                            for (Map<String, String> colAndVal : data) {
                                String columnName = colAndVal.get("columnName");
                                String columnValue = colAndVal.get("columnValue");

                                if ("id".equals(columnName)) {
                                    webChatTextEms.setId(Integer.parseInt(columnValue));
                                }

                                if ("from_url".equals(columnName)) {
                                    webChatTextEms.setFrom_url(columnValue);
                                }

                            }
                            // 返回去即可
                            collector.collect(webChatTextEms);

                        }
                    }
                }
            }
        });


        //3.5: 需要将两个dataStream转换为表
        Schema web_chat_ems_schema = Schema.newBuilder()
                .column("id", DataTypes.INT())
                .column("session_id", DataTypes.STRING())
                .column("ip", DataTypes.STRING())
                .column("create_time", DataTypes.STRING())
                .column("area", DataTypes.STRING())
                .column("origin_channel", DataTypes.STRING())
                .column("seo_source", DataTypes.STRING())
                .column("sid", DataTypes.STRING())
                .column("msg_count", DataTypes.INT())
                .build();

        tableEnv.createTemporaryView("web_chat_ems",webChatEmsDataSteam,web_chat_ems_schema);

        Schema web_chat_text_ems_schema = Schema.newBuilder()
                .column("id", DataTypes.INT())
                .column("from_url", DataTypes.STRING())
                .build();

        tableEnv.createTemporaryView("web_chat_text_ems",webChatTextEmsDataSteam,web_chat_text_ems_schema);

        //3.6: 执行SQL, 对数据进行转换处理

        Table table = tableEnv.sqlQuery("select wce.id,wce.sid,wce.ip,wce.session_id,wce.create_time, year(to_timestamp(wce.create_time)) as yearinfo, month(to_timestamp(wce.create_time)) as monthinfo,dayofmonth(to_timestamp(wce.create_time)) as dayinfo,hour(to_timestamp(wce.create_time)) as hourinfo, wce.seo_source, wce.area,wce.origin_channel,wce.msg_count, wcte.from_url  from web_chat_ems wce join web_chat_text_ems wcte on wce.id = wcte.id");


        //3.7: 将table对象转换为dataSteam对象
        DataStream<Row> rowdataStream = tableEnv.toDataStream(table);


        //3.8: 将每一个ROW转换为一个个的PulsarTopicPojo
        SingleOutputStreamOperator<PulsarTopicPojo> pulsarTopicPojoDataSteam = rowdataStream.map(new MapFunction<Row, PulsarTopicPojo>() {
            @Override
            public PulsarTopicPojo map(Row row) throws Exception {
                Integer id = (Integer) row.getField("id");
                String sid = (String) row.getField("sid");
                String ip = (String) row.getField("ip");
                String session_id = (String) row.getField("session_id");
                String create_time = (String) row.getField("create_time");
                Long yearinfo = (Long) row.getField("yearinfo");
                Long monthinfo = (Long) row.getField("monthinfo");
                Long dayinfo = (Long) row.getField("dayinfo");
                Long hourinfo = (Long) row.getField("hourinfo");
                String seo_source = (String) row.getField("seo_source");
                String area = (String) row.getField("area");
                String origin_channel = (String) row.getField("origin_channel");
                Integer msg_count = (Integer) row.getField("msg_count");
                String from_url = (String) row.getField("from_url");

                PulsarTopicPojo pulsarTopicPojo = new PulsarTopicPojo();

                pulsarTopicPojo.setData(id, sid, ip, session_id, create_time, yearinfo+"", monthinfo+"", dayinfo+"", hourinfo+"", seo_source, area, origin_channel, msg_count, from_url);

                return pulsarTopicPojo;
            }
        });


        //4. 设置sink操作, 将数据写出到Pulsar中

        PulsarSerializationSchemaWrapper<PulsarTopicPojo> serializationSchema = new PulsarSerializationSchemaWrapper.Builder<>(JsonSer.of(PulsarTopicPojo.class))
                .usePojoMode(PulsarTopicPojo.class, RecordSchemaType.JSON)
                .build();

        FlinkPulsarSink<PulsarTopicPojo> pulsarSink = new FlinkPulsarSink<PulsarTopicPojo>(
                "pulsar://node1:6650,node2:6650,node3:6650","http://node1:8080,node2:8080,node3:8080",
                Optional.of("persistent://public/default/itcast_ems_tab"),new Properties(),serializationSchema
        );

        pulsarTopicPojoDataSteam.addSink(pulsarSink);

        //4. 提交任务
        env.execute("itcast_collect");
    }
}
