package com.example.consul.serviceregister.controller;

import com.example.consul.serviceregister.ServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * test
 *
 * @author geng_pool
 * @create 2018-08-01-18:59
 **/
@RestController
@RequestMapping("/")
public class TestController {
    @Autowired
    private ServiceRegister serviceRegister;

    /**
     * 提供一个接口，用于consul健康检查
     * @return
     */
    @RequestMapping("/health")
    public String health() {
        return "alive";
    }

    @RequestMapping("/s1")
    public String s1() {
        return "success from " + serviceRegister.getServiceName() + " ( " + serviceRegister.getServiceId() + " )";
    }

    @RequestMapping("/s2")
    public String s2() {
        return "success from " + serviceRegister.getServiceName() + " ( " + serviceRegister.getServiceId() + " )";
    }
}
