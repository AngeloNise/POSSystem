package com.microservice.systemservice;

import com.microservice.systemservice.dto.OrganizationResponseDTO;
import com.microservice.systemservice.helper.CategoriesUtil;
import com.microservice.systemservice.helper.ConfigUtils;
import com.microservice.systemservice.services.CategoryService;
import com.microservice.systemservice.utils.OrganizationBean;
import com.microservice.systemservice.utils.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class SystemServiceApplication {

    @Autowired
    private static ApplicationContext context;
    @Autowired
    public void context(ApplicationContext context) { SystemServiceApplication.context = context; }

    public static void main(String[] args) {
        SpringApplication.run(SystemServiceApplication.class, args);

        try {
            ConfigUtils.initCacheValues();
            CategoryService categoryService = (CategoryService) context.getBean("categoryService");
            CategoriesUtil.AllCATEGORIES_LIST = categoryService.queryCategoriesAllList();
            CategoriesUtil.CATEGORIES_LIST = categoryService.queryCategoriesList();
            CategoriesUtil.TOOLSANDEQUIPMENT_LIST = categoryService.queryToolsAndEquipmentList();
            CategoriesUtil.BRANDANDMODEL_LIST = categoryService.queryBrandAndModelList();
            CategoriesUtil.MODEL_LIST = categoryService.queryModelList();
            CategoriesUtil.LVT_LIST = categoryService.queryLVTList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public CommandLineRunner getRunner(ApplicationContext ctx){
        return (args) -> {
            ctx.getBean(OrganizationBean.class).init();

            ctx.getBean(UserBean.class).init();
        };
    }

}