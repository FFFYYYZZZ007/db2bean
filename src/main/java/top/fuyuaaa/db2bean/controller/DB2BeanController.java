package top.fuyuaaa.db2bean.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.fuyuaaa.db2bean.util.DB2BeanUtil;

/**
 * @author: fuyuaaaaa
 * @description:
 * @program: db2bean
 * @creat: 2018-10-25 16:42
 **/
@RestController
public class DB2BeanController {

    @PostMapping("/get")
    public String get(@RequestParam("dbType") String dbType,
                      @RequestParam("url") String url,
                      @RequestParam("user") String user,
                      @RequestParam("password") String password,
                      @RequestParam("tableName") String tableName,
                      @RequestParam("beanName") String beanName){
        return DB2BeanUtil.get(dbType, url, user, password, tableName, beanName);
    }
}
