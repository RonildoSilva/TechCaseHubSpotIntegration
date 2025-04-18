package com.codeone.hubspot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String getRedirectUrl() {
        return "redirect:swagger-ui/index.html#/";
    }
}

