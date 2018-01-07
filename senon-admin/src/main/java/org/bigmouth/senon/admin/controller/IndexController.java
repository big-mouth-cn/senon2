package org.bigmouth.senon.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class IndexController {

    @RequestMapping("/")
    public String index() {
       return "index";
    }


    @RequestMapping("/worker_manage")
    public String workers() {
        return "worker";
    }
}
