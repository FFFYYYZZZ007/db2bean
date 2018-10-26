package main;

import java.sql.Connection;
import java.util.Scanner;

/**
 * @author: fuyuaaaaa
 * @description:
 * @program: db2bean
 * @creat: 2018-10-26 10:10
 **/
public class MainUtil {
    public static void main(String[] args) {
        String dbType = "";
        String url = "";
        String user = "";
        String password = "";
        String tableName = "";
        String beanName = "";
        String path = "";

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请选择数据库类型：\n1、MySQL\n2、PostgreSQL");
            String typeNum = scanner.nextLine();
            if (typeNum.equals("1") || typeNum.equals("2")) {
                dbType = typeNum.equals("1") ? "mysql" : "postgresql";
                break;
            }
        }
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入数据库URL：");
            url = scanner.nextLine();
            System.out.println("正在校验URL合法性");
            if (url.contains("jdbc") && (url.contains("mysql") || url.contains("postgresql"))
                    && (url.contains("3306") || url.contains("5432"))) {
                System.out.println("验证成功！");
                break;
            }
            System.out.println("验证失败！URL不合法！");
        }

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入数据库用户名：");
            user = scanner.nextLine();
            System.out.println("请输入数据库密码：");
            password = scanner.nextLine();
            System.out.println("正在验证用户名和密码...");
            Connection connection = DBUtil.getConnection(dbType, url, user, password);
            if (null != connection) {
                System.out.println("连接成功！");
                break;
            }
            System.out.println("用户名或密码错误！");
        }

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入表名：");
            tableName = scanner.nextLine();
            if (tableName != null && !tableName.equals("")) {
                break;
            }
        }
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入类名：");
            beanName = scanner.nextLine();
            if (beanName != null && !beanName.equals("")) {
                break;
            }
        }

        System.out.println("正在输出到桌面：");
        path = "C:\\Users\\a\\Desktop\\";


        Connection connection = DBUtil.getConnection(dbType, url, user, password);

        assert connection != null;
        String types[] = DBUtil.getColumns(connection, tableName, "type");
        String names[] = DBUtil.getColumns(connection, tableName, "name");
        String remarks[] = DBUtil.getColumns(connection, tableName, "remark");

        //首字母大写Example: enterprise -> Enterprise
        String uBeanName = StringUtil.upperCaseFirstChar(beanName);
        /**
         * write bean
         */
        FileUtil.write(FileContentUtil.bean(types, names, remarks, beanName), uBeanName + ".java", path);

        /**
         * write mapper
         */
        FileUtil.write(FileContentUtil.mapper(names, tableName, beanName), uBeanName + "Mapper.java", path);

        /**
         * write controller
         */
        FileUtil.write(FileContentUtil.controller(types, names, remarks, beanName), uBeanName + "Controller.java", path);

        System.out.println("成功！");
    }
}
