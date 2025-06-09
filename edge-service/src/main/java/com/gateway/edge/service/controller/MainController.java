package com.gateway.edge.service.controller;

import com.gateway.edge.service.model.Account;
import com.gateway.edge.service.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Default on 21/04/2022.
 */
@RestController
@CrossOrigin
public class MainController {


    @Autowired
    private AccountService accountService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/MainSample")
    public String MainShow(){
        return "Main Gateway Sample";
    }

    @GetMapping("/MainFeinPost")
    public Optional<Map> MainShowFeignPost() {
        Optional<Map> res = accountService.findByUsername("benkuramax");
        return res;
    }

    @GetMapping("/MainFeinGet")
    public List<Map> MainShowFeignGet() {
        List<Map> res = accountService.getStores();
        return res;
    }

    @GetMapping("/MainPort")
    public String getMainPort(){
        return accountService.getMainPort();
    }

    @GetMapping("/MainShow")
    public String MaiShowDiscover(){

        //String str = accountService.showString();

        List<ServiceInstance> instances= discoveryClient.getInstances("account-service");
        ServiceInstance serviceInstance=instances.get(0);

        String baseUrl=serviceInstance.getUri().toString();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response=null;
        try{
            response=restTemplate.exchange("http://localhost:1101/account/acct/show",
                    HttpMethod.GET, getHeaders(),String.class);
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
        //System.out.println(response.getBody());

        return response.getBody();
    }

    private static HttpEntity<?> getHeaders() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }


}
