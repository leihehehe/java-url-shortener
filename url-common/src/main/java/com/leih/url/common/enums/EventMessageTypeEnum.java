package com.leih.url.common.enums;

public enum EventMessageTypeEnum {
    /**
     * shorten url -> create short link
     */
    SHORT_LINK_ADD,
    SHORT_LINK_ADD_LINK,
    SHORT_LINK_ADD_MAPPING,
    SHORT_LINK_DELETE,
    SHORT_LINK_DELETE_LINK,
    SHORT_LINK_DELETE_MAPPING,
    SHORT_LINK_UPDATE,
    SHORT_LINK_UPDATE_LINK,
    SHORT_LINK_UPDATE_MAPPING,
    PRODUCT_ORDER_NEW,
    PRODUCT_ORDER_PAID,
    PLAN_FREE_NEW_ACCOUNT,
    LINK_CHECK_IF_CREATED,
}
