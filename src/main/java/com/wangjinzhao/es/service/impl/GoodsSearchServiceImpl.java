package com.wangjinzhao.es.service.impl;

import com.wangjinzhao.es.constant.SearchTypeEnum;
import com.wangjinzhao.es.service.GoodsSearchService;
import com.wangjinzhao.es.vo.request.GoodsRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
@Component
public class GoodsSearchServiceImpl implements GoodsSearchService {

    @Autowired
    private TransportClient transportClient;


    @Override
    public Boolean createIndexByMap(GoodsRequest goodsRequest) {
        IndexResponse response = transportClient.prepareIndex(SearchTypeEnum.GOODS_COLD.getIndex(), SearchTypeEnum.GOODS_COLD.getType(), goodsRequest.getGoodId() + "")
                .setSource(goodsRequest.generateIndexMap())
                .get();
        return response.status().getStatus() == RestStatus.CREATED.getStatus();
    }

    @Override
    public Boolean createIndexByObject(GoodsRequest goodsRequest) throws IOException {
        IndexResponse response = transportClient.prepareIndex(SearchTypeEnum.GOODS_COLD.getIndex(), SearchTypeEnum.GOODS_COLD.getType(), goodsRequest.getGoodId() + "")
                .setSource(goodsRequest.createIndexObject())
                .get();
        return response.status().getStatus() == RestStatus.CREATED.getStatus();

    }

    @Override
    public Boolean createIndexByJson(GoodsRequest goodsRequest) throws IOException {
        IndexResponse response = transportClient.prepareIndex(SearchTypeEnum.GOODS_COLD.getIndex(), SearchTypeEnum.GOODS_COLD.getType(), goodsRequest.getGoodId() + "")
                .setSource(goodsRequest.createIndexObject().toString())
                .get();
        return response.status().getStatus() == RestStatus.CREATED.getStatus();

    }
}
