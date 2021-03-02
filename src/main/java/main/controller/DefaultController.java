package main.controller;



import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;



public class DefaultController {
    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }
}
