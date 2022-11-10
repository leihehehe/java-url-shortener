package com.leih.app.func;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leih.app.model.ShortLinkDetail;
import com.leih.app.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

@Slf4j
public class AsyncIpLocationFunction extends RichAsyncFunction<ShortLinkDetail, String> {
  private static final String IP_LOOKUP_URL = "https://ipapi.co/%s/json/";
  private CloseableHttpAsyncClient httpClient;

  @Override
  public void open(Configuration parameters) throws Exception {
    this.httpClient = createAsyncHttpClient();
  }

  @Override
  public void close() throws Exception {
    if (httpClient != null) {
      httpClient.close();
    }
  }

  @Override
  public void timeout(ShortLinkDetail input, ResultFuture<String> resultFuture) throws Exception {
    resultFuture.complete(null);
  }

  @Override
  public void asyncInvoke(ShortLinkDetail shortLinkDetail, ResultFuture<String> resultFuture)
      throws Exception {
    String ipAddress = shortLinkDetail.getIp();
    String lookupUrl = String.format(IP_LOOKUP_URL, ipAddress);
    HttpGet httpGet = new HttpGet(lookupUrl);
    Future<HttpResponse> future = httpClient.execute(httpGet, null);
    CompletableFuture<ShortLinkDetail> completableFuture =
        CompletableFuture.supplyAsync(
            new Supplier<ShortLinkDetail>() {
              @Override
              public ShortLinkDetail get() {
                try {
                  HttpResponse response = future.get();
                  int statusCode = response.getStatusLine().getStatusCode();
                  if (statusCode == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    String result = EntityUtils.toString(entity, "UTF-8");
                    ObjectNode objectNode = JsonUtil.json2Obj(result, ObjectNode.class);
                    String country = objectNode.get("country_name").textValue();
                    shortLinkDetail.setCountry(country);
                    return shortLinkDetail;
                  }
                } catch (InterruptedException | ExecutionException | IOException e) {
                  log.error("Failed to reverse ip address: {}", e.getMessage());
                }
                return null;
              }
            });
    completableFuture.thenAccept(
        (dbResult) -> {
          resultFuture.complete(Collections.singleton(JsonUtil.obj2Json(shortLinkDetail)));
        });
  }

  private CloseableHttpAsyncClient createAsyncHttpClient() {
    try {
      RequestConfig requestConfig =
          RequestConfig.custom()
              .setSocketTimeout(20000)
              .setConnectTimeout(10000)
              .setConnectionRequestTimeout(1000)
              .build();
      ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
      PoolingNHttpClientConnectionManager connManager =
          new PoolingNHttpClientConnectionManager(ioReactor);
      connManager.setMaxTotal(500);
      connManager.setDefaultMaxPerRoute(300);
      CloseableHttpAsyncClient httpClient =
          HttpAsyncClients.custom()
              .setConnectionManager(connManager)
              .setDefaultRequestConfig(requestConfig)
              .build();
      httpClient.start();
      return httpClient;
    } catch (IOReactorException e) {
      log.error("Failed to initialize CloseableHttpAsyncClient:{}", e.getMessage());
      return null;
    }
  }
}
