package com.gateway.edge.service.services;

import com.gateway.edge.service.config.FeignConfiguration;
import com.gateway.edge.service.model.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Default on 22/04/2022.
 */
/*@FeignClient(name="account-service",url="http://localhost:1101", path="/account/acct")
@Service
public interface AccountService {

    @GetMapping(value = "/showAll")
    public List<Map> showAllAccounts();

    @GetMapping(value = "/findByUsername")
    public Optional<Map> findByUsername(@RequestParam(name="username") String username);

}*/

@FeignClient(name="account-service", fallback = FallbackReturn.class)
/*@FeignClient(name="account-service", fallbackFactory = FallbackReturnFactory.class)*/
@Service
public interface AccountService {

    @RequestMapping( value = "/account/acct/showAll")
    List<Map> getStores();

    @RequestMapping( value = "/account/acct/findByUsername")
    Optional<Map> findByUsername(@RequestParam("username") String username);

    @RequestMapping( value = "/main/showPort")
    String getMainPort();
}
