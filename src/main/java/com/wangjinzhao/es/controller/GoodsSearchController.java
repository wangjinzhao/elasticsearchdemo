package com.wangjinzhao.es.controller;

import com.wangjinzhao.es.service.GoodsSearchService;
import com.wangjinzhao.es.vo.request.GoodsRequest;
import com.wangjinzhao.es.vo.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
@Slf4j
@RestController
@RequestMapping("/es/goods")
public class GoodsSearchController {


    @Autowired
    GoodsSearchService goodsSearchService;

    /**
     * 创建document 如果index type不错在会自动创建
     */
    @RequestMapping(value = "/create/doc", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Boolean createDocByMap(@ModelAttribute("goodsRequest") GoodsRequest goodsRequest) {
        boolean flag = Boolean.FALSE;
        try {
            flag = this.goodsSearchService.createIndexByMap(goodsRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除document
     */
    @RequestMapping("/delete/doc")
    @ResponseBody
    public Boolean delete(@RequestParam("goodIds") String goodIds) {
        return this.goodsSearchService.deleteDoc(Arrays.asList(goodIds.split(",")));
    }


    @RequestMapping("/search/{goodId}")
    @ResponseBody
    public Map<String, Object> searchById(@PathVariable("goodId") String goodId) {
        return this.goodsSearchService.searchGoodById(goodId);
    }

    @RequestMapping("/update/doc")
    @ResponseBody
    public Boolean updateDoc(@ModelAttribute("goodRequest") GoodsRequest goodsRequest) throws InterruptedException, ExecutionException, IOException {
        return this.goodsSearchService.updateDocById(goodsRequest);
    }


    @RequestMapping("/match/all")
    @ResponseBody
    public List<Map<String, Object>> matchAll(@RequestParam("sortKey") String sortKey) {
        return this.goodsSearchService.matchAll(sortKey);
    }


}
