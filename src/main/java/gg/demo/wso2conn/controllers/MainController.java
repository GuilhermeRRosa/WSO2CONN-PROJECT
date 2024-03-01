package gg.demo.wso2conn.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dev")
public class MainController {

    @GetMapping
    public String getIndex(){
        return "index";
    }
}
