package com.leih.url.app.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class KafkaUtil {
    private static String  KAFKA_SERVER=null;
    static {
        Properties properties = new Properties();
        //change application properties here
        InputStream in = KafkaUtil.class.getClassLoader().getResourceAsStream("application.properties");

        try {
            properties.load(in);
        } catch (IOException e) {
            log.error("Failed to get kafka config,{}",e.getMessage());
        }
        String kafka_host= System.getenv().get("KAFKA_HOST");
        KAFKA_SERVER= properties.getProperty("kafka.servers").replace("${KAFKA_HOST}",kafka_host);
    }

    /**
     * Get flink kafka consumer
     * @param topic
     * @param groupId
     * @return
     */
    public static FlinkKafkaConsumer<String> getKafkaConsumer(String topic, String groupId){
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,KAFKA_SERVER);
        return new FlinkKafkaConsumer<String>(topic,new SimpleStringSchema(),properties);
    }

    /**
     * Get flink kafka producer
     * @param topic
     * @return
     */
    public static FlinkKafkaProducer<String> getKafkaProducer(String topic){
        return new FlinkKafkaProducer<String>(KAFKA_SERVER,topic,new SimpleStringSchema());
    }
}
