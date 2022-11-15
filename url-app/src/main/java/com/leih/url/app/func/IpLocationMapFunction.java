package com.leih.url.app.func;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leih.url.app.model.ShortLinkDetail;
import com.leih.url.app.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

@Slf4j
public class IpLocationMapFunction  extends RichMapFunction<ShortLinkDetail,String> {
    private CloseableHttpClient httpClient;
    private static final String IP_LOOKUP_URL="https://ipapi.co/%s/json/";

    @Override
    public String map(ShortLinkDetail shortLinkDetail) {
        String ipAddress = shortLinkDetail.getIp();
        String lookupUrl = String.format(IP_LOOKUP_URL, ipAddress);
        HttpGet httpGet = new HttpGet(lookupUrl);
        try(CloseableHttpResponse response = httpClient.execute(httpGet)){
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode== HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");
                ObjectNode objectNode = JsonUtil.json2Obj(result, ObjectNode.class);
                String country = objectNode.get("country_name").textValue();
                shortLinkDetail.setCountry(country);
            }
        }catch (Exception e){
            log.error("Failed to reverse ip address: {}",e.getMessage());
        }
        return JsonUtil.obj2Json(shortLinkDetail);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.httpClient = createHttpClient();
    }

    @Override
    public void close() throws Exception {
        if(httpClient!=null){
            httpClient.close();
        }
    }

    private CloseableHttpClient createHttpClient() {
        Registry<ConnectionSocketFactory> registry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build();
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager(registry);

        // connection pool -> max: 500 connections
        connectionManager.setMaxTotal(500);
        // max connections per route
        connectionManager.setDefaultMaxPerRoute(300);
        RequestConfig requestConfig =
                RequestConfig.custom()
                        .setSocketTimeout(20000)
                        // time to connect to server
                        .setConnectTimeout(10000)
                        .setConnectionRequestTimeout(1000)
                        .build();
        CloseableHttpClient closeableHttpClient =
                HttpClientBuilder.create()
                        .setDefaultRequestConfig(requestConfig)
                        .setConnectionManager(connectionManager)
                        .build();
        return closeableHttpClient;
    }
}
