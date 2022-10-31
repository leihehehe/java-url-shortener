package com.leih.app.dwd;

import com.leih.app.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

@Slf4j
public class DwdShortLinkLogApp {
    /**
     * Define a topic
     */
    public static final String SOURCE_TOPIC= "ods_link_visit_topic";
    /**
     * Define a group
     */
    public static final String GROUP_ID= "dwd_short_link_group";
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //DataStream<String> ds = env.socketTextStream("127.0.0.1", 8088, ',', 10000);
        FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC, GROUP_ID);
        //use kafka as a source
        DataStreamSource<String> ds = env.addSource(kafkaConsumer);
        ds.print();
        env.execute();
    }
}
