package com.wangjinzhao.es.service.impl;

import com.wangjinzhao.es.constant.SearchTypeEnum;
import com.wangjinzhao.es.service.GoodsSearchService;
import com.wangjinzhao.es.vo.request.GoodsRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

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

    @Override
    public Boolean deleteDoc(List<Object> ids) {
        //删除单条
        DeleteResponse deleteResponse = this.transportClient.
                prepareDelete(SearchTypeEnum.GOODS_COLD.getIndex(),
                        SearchTypeEnum.GOODS_COLD.getType(),
                        ids.get(0).toString())
                .execute()
                .actionGet();
        System.out.println(deleteResponse.status().getStatus());
        //使用bulk 批量删除
        QueryBuilder queryBuilder = QueryBuilders.termsQuery("goodId", ids);
        SearchResponse searchResponse = transportClient.prepareSearch(SearchTypeEnum.GOODS_COLD.getIndex())
                .setTypes(SearchTypeEnum.GOODS_COLD.getType())
                .setQuery(queryBuilder)
                .setSize(100)
                .execute().actionGet();

        SearchHit[] hits = searchResponse.getHits().getHits();
        BulkResponse responses = null;
        if (hits.length > 0) {
            // 开启批量删除
            BulkRequestBuilder bulkfresh = transportClient
                    .prepareBulk()
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
            for (SearchHit searchHit : hits) {
                DeleteRequest deleteRequest = new DeleteRequest(SearchTypeEnum.GOODS_COLD.getIndex()
                        , SearchTypeEnum.GOODS_COLD.getType(), searchHit.getId());
                bulkfresh.add(deleteRequest);
            }
            // 执行
            responses = bulkfresh.execute().actionGet();
        }
        return responses != null && responses.status().getStatus() == 200;
    }

    @Override
    public Map<String, Object> searchGoodById(String goodId) {
        GetResponse response = transportClient.prepareGet(SearchTypeEnum.GOODS_COLD.getIndex(), SearchTypeEnum.GOODS_COLD.getType(), goodId).get();
        return response.getSourceAsMap();
    }

    @Override
    public boolean updateDocById(GoodsRequest goodsRequest) throws IOException, ExecutionException, InterruptedException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(SearchTypeEnum.GOODS_COLD.getIndex());
        updateRequest.type(SearchTypeEnum.GOODS_COLD.getType());
        updateRequest.id(goodsRequest.getGoodId().toString());
        XContentBuilder builder = goodsRequest.createIndexObject();//创建bulider
        updateRequest.doc(builder);
        UpdateResponse updateResponse = transportClient.update(updateRequest).get();
        return updateResponse.status().getStatus() == 200;
    }

    @Override
    public List<Map<String, Object>> matchAll(String querySortKey) {
        List<Map<String, Object>> result = new ArrayList<>();
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SortBuilder sortBuilder = SortBuilders.fieldSort(querySortKey).order(SortOrder.DESC);
        SearchResponse searchResponse = this.transportClient.prepareSearch()
                .setQuery(queryBuilder)
                .addSort(sortBuilder)
                .execute().actionGet(1000L);
        if (searchResponse.status().getStatus() == 200) {
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] hits = searchHits.getHits();
            for (SearchHit hitIndex : hits) {
                System.out.println("getSourceAsString=" + hitIndex.getSourceAsString());
                System.out.println("遍历高亮集合，打印高亮片段:");
                Map<String, DocumentField> fiels = hitIndex.getFields();
                System.out.println("fiels=" + fiels);
                Map<String, Object> sourceAsMap = hitIndex.getSourceAsMap();
                System.out.println("sourceAsMap=" + sourceAsMap);
                result.add(sourceAsMap);
            }
        }
        return result;
    }


}
