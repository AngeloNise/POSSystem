package com.microservice.systemservice.controllers;

import com.code.share.codesharing.model.Employee;
import com.code.share.codesharing.tools.Utils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/system/mobile")
@CrossOrigin
public class MobileAppController {

    @GetMapping("/show")
    public String showString(){

        Employee employee = new Employee();

        Utils.xo().syst().testPost();

        //return "SYSTEM-SERVICE IS IN THE HOUSE";
        return employee.Output();
    }

    @PostMapping("/onLoginProcess")
    public Map OnLoginProcess(@RequestParam String username, @RequestParam String password) {

        String qwe = "123";

        Map feedback = Utils.xo().sync().GetFeedback(
                ()->{
                    String qwe2 = "123";
                }
        );

        return feedback;
    }
}
