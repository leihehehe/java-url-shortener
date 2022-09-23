package com.leih.url.common.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/***
 * Optimized RestTemplate Config
 */
@Configuration
public class RestTemplateConfig {
  @Bean
  public RestTemplate restTemplate(ClientHttpRequestFactory requestFactory) {
    return new RestTemplate(requestFactory);
  }

  @Bean
  public ClientHttpRequestFactory httpRequestFactory() {
    return new HttpComponentsClientHttpRequestFactory(httpClient());
  }

  @Bean
  public HttpClient httpClient() {
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
