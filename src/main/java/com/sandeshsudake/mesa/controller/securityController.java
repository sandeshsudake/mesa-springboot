package com.sandeshsudake.mesa.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service; // <--- POTENTIAL PROBLEM HERE
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class securityController {


    @GetMapping("/logicsuccess/")
    public String loginSucess(HttpServletRequest request){


        if (request.isUserInRole("ROLE_ADMIN")){
            return "redirect:/admin/" ; // This will go to @RequestMapping("/admin") or @GetMapping("/admin")
        }


        // If not ADMIN, redirect to home
        return "redirect:/home/"; // This will go to @GetMapping("/home/")
    }
}