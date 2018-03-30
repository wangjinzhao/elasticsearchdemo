package com.wangjinzhao.es.constant;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
@Getter
public enum SearchTypeEnum {

    GOODS_COLD("goods", "cold");

    private String index;
    private String type;

    SearchTypeEnum(String index, String type) {
        this.index = index;
        this.type = type;
    }


}
