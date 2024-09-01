package com.ms.clab.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/chat")
    public String chat() {
        return "user/chat";
    }

}
