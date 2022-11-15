package com.leih.url.app.func;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leih.url.app.model.DeviceInfo;
import com.leih.url.app.model.ShortLinkDetail;
import com.leih.url.app.util.CommonUtil;
import com.leih.url.app.util.JsonUtil;
import org.apache.flink.api.common.functions.MapFunction;

public class DeviceMapFunction implements MapFunction<String, ShortLinkDetail> {
    @Override
    public ShortLinkDetail map(String s) throws Exception {
        ObjectNode objectNode = JsonUtil.json2Obj(s, ObjectNode.class);
        // get user agent
        String userAgent = objectNode.get("content").get("user-agent").textValue();
        // parse user agent to the device info
        DeviceInfo deviceInfo = CommonUtil.getDeviceInfo(userAgent);
        deviceInfo.setUid(objectNode.get("uid").asText());
        // get the detailed short link entity
        ShortLinkDetail shortLinkDetail =
                ShortLinkDetail.builder()
                        .accountNo(objectNode.get("content").get("accountNo").asLong())
                        .visitTime(objectNode.get("timestamp").asLong())
                        .code(objectNode.get("bizId").textValue())
                        .referer(objectNode.get("referer").asText())
                        .isNew(objectNode.get("isNew").asInt())
                        .ip(objectNode.get("ip").asText())
                        .browserName(deviceInfo.getBrowserName())
                        .deviceManufacturer(deviceInfo.getDeviceManufacturer())
                        .deviceType(deviceInfo.getDeviceType())
                        .os(deviceInfo.getOs())
                        .osVersion(deviceInfo.getOsVersion())
                        .uid(deviceInfo.getUid())
                        .build();
        return shortLinkDetail;
    }

}
