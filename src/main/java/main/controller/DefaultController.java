package main.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

public class DefaultController {
    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }
}
