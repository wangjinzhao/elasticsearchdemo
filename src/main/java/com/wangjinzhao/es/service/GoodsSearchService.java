package com.wangjinzhao.es.service;

import com.wangjinzhao.es.vo.request.GoodsRequest;

import java.io.IOException;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
public interface GoodsSearchService {

    Boolean createIndexByMap(GoodsRequest goodsRequest);

    Boolean createIndexByObject(GoodsRequest goodsRequest) throws IOException;

    Boolean createIndexByJson(GoodsRequest goodsRequest) throws IOException;
}
