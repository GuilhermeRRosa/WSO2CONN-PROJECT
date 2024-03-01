package gg.demo.wso2conn.controllers;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/dev")
public class MainController {

    @GetMapping
    public String getIndex(Principal principal, Model model){
        String username = principal.getName();

        model.addAttribute("username", username);
        return "index";
    }
}
