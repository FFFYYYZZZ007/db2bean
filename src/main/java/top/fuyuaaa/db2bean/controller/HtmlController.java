package top.fuyuaaa.db2bean.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: fuyuaaaaa
 * @description: 页面跳转
 * @program: db2bean
 * @creat: 2018-10-25 16:06
 **/

@Controller
public class HtmlController {

    @GetMapping("/")
    public String index(){
        return "index";
    }
}
