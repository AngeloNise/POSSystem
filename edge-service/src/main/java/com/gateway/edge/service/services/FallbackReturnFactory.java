package com.gateway.edge.service.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * Created by Default on 05/05/2022.
 */
@Component
@Slf4j
public class FallbackReturnFactory implements FallbackFactory<AccountService> {
    @Override
    public AccountService create(Throwable throwable) {
        //log.error("An exception occurred when calling the Account Micro", throwable);

        return new AccountService() {
            Map map = new HashMap();
            List<Map> mapList = new ArrayList<>();
            String msg = "Microservice:Account is offline";

            @Override
            public List<Map> getStores() {
                map.put("ErrorMessage", msg);
                mapList.add(map);
                return mapList;
            }

            @Override
            public Optional<Map> findByUsername(@RequestParam("username") String username) {
                map.put("ErrorMessage", msg);
                Optional<Map> map1 = Optional.ofNullable(map);
                return map1;
            }

            @Override
            public String getMainPort() {
                return msg;
            }
        };
    }
}
