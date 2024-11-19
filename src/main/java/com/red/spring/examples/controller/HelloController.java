package com.red.spring.examples.controller;

import com.red.spring.examples.mapper.primary.PrimaryMapper;
import com.red.spring.examples.mapper.second.SecondMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private PrimaryMapper primaryMapper;
    @Autowired
    private SecondMapper secondMapper;

    @GetMapping("/primary")
    @Transactional(transactionManager = "primaryTransactionManager")
    public String primary() {
        return primaryMapper.selectHello();
    }

    @GetMapping("/second")
    @Transactional(transactionManager = "secondTransactionManager")
    public String second() {
        return secondMapper.selectHello();
    }

}
