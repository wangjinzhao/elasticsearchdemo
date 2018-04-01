package com.wangjinzhao.es.vo.request;

import com.wangjinzhao.es.model.GoodsInfo;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
@Getter
@Setter
public class GoodsRequest {

    private Integer cityId;//城市id
    private Long goodId;//商品id
    private Long shopId;//店铺id
    private String goodPicUrl;//产品图片
    private String goodName;//产品名称
    private String goodDesc;//产品描述
    private String specification;//规格
    private Integer price;//单价
    private Integer salesVolume;//销量

    public static GoodsInfo transfer(GoodsRequest goodsRequest) {
        GoodsInfo target = new GoodsInfo();
        BeanUtils.copyProperties(goodsRequest, target);
        return target;
    }

    public Map<String, Object> generateIndexMap() {
        Map<String, Object> json = new HashMap<>();
        json.put("cityId", this.getCityId());
        json.put("goodId", this.getGoodId());
        json.put("shopId", this.getShopId());
        json.put("goodPicUrl", this.getGoodPicUrl());
        json.put("goodName", this.getGoodName());
        json.put("goodDesc", this.getGoodDesc());
        json.put("specification", this.getSpecification());
        json.put("price", this.getPrice());
        json.put("salesVolume", this.getSalesVolume());
        return json;
    }

    public XContentBuilder createIndexObject() throws IOException {
        XContentBuilder builder = jsonBuilder()
                .startObject()
                .field("cityId", this.getCityId())
                .field("goodId", this.getGoodId())
                .field("shopId", this.getShopId())
                .field("goodPicUrl", this.getGoodPicUrl())
                .field("goodName", this.getGoodName())
                .field("goodDesc", this.getGoodDesc())
                .field("specification", this.getSpecification())
                .field("price", this.getPrice())
                .field("salesVolume", this.getSalesVolume())
                .endObject();
        return builder;
    }


}
