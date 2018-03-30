package com.wangjinzhao.es.controller;

import com.wangjinzhao.es.service.GoodsSearchService;
import com.wangjinzhao.es.vo.request.GoodsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by WANGJINZHAO on 2018/3/30.
 */
@Slf4j
@RestController
@RequestMapping("/goods/serarch")
public class GoodsSearchController {


    @Autowired
    GoodsSearchService goodsSearchService;

    @RequestMapping(value = "/indexMap", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Boolean indexMap(@ModelAttribute("goodsRequest") GoodsRequest goodsRequest) {
        boolean flag = Boolean.FALSE;
        try {
            flag = this.goodsSearchService.createIndexByMap(goodsRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}
