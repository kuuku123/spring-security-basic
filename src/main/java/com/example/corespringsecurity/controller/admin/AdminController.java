package com.example.corespringsecurity.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;

public class AdminController {

    @GetMapping(value="/admin")
    public String home() throws Exception {
        return "admin/home";
    }
}
