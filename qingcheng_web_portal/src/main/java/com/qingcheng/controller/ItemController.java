package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("item")
public class ItemController {

    @Reference
    private SpuService spuService;
    @Reference
    private CategoryService categoryService;
    @Value("${pagePath}")
    private String pagePath;
    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("createPage")
    public void createPage(String spuId) {
        //1.查询商品信息
        Goods goods = spuService.findGoodsById(spuId);
        //获取spu信息
        Spu spu = goods.getSpu();
        //获取sku列表
        List<Sku> skuList = goods.getSkuList();
        //查询商品分类
        List<String> categoryList = new ArrayList<>();
        //一级分类
        categoryList.add(categoryService.findById(spu.getCategory1Id()).getName());
        //二级分类
        categoryList.add(categoryService.findById(spu.getCategory2Id()).getName());
        //三级分类
        categoryList.add(categoryService.findById(spu.getCategory3Id()).getName());

        //2.批量生成sku页面
        skuList.forEach(sku -> {
            //(1)创建大下文和数据模型
            Context context = new Context();
            Map<String,Object> dataModel = new HashMap<>();
            dataModel.put("spu", spu);
            dataModel.put("sku", sku);
            dataModel.put("categoryList", categoryList);
            //sku图片列表
            dataModel.put("skuImages", sku.getImages().split(","));
            dataModel.put("spuImages", spu.getImages().split(","));

            Map paraItems = JSON.parseObject(spu.getParaItems(), Map.class);//参数列表
            dataModel.put("paraItems", paraItems);

            Map specItems = JSON.parseObject(sku.getSpec(), Map.class);//规格列表
            dataModel.put("specItems", specItems);

            context.setVariables(dataModel);
            //(2)准备文件
            File dir = new File(pagePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(dir,sku.getId() + ".html");
            //(3)生成页面
            try {
                PrintWriter printWriter = new PrintWriter(dest,"UTF-8");
                templateEngine.process("item", context, printWriter);
                System.out.println("生成页面" + sku.getId() + ".html");
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

    }
}
