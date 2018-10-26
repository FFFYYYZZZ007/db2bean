package main;

import java.sql.*;

/**
 * @author: fuyuaaaaa
 * @description: 数据库工具类
 * @program: db2bean
 * @creat: 2018-10-26 09:57
 **/
public class DBUtil {
    public static Connection getConnection(String dbType, String url, String user, String password) {
        try {
            //mysql
            if (dbType.equalsIgnoreCase("mysql")) {
                Class.forName("com.mysql.jdbc.Driver");
            }
            //postgresql
            else if (dbType.equalsIgnoreCase("postgresql")) {
                Class.forName("org.postgresql.Driver");
            }
            Connection connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (SQLException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static String[] getColumns(Connection connection, String tableName, String type) {
        PreparedStatement preparedStatement = null;
        ResultSetMetaData metaData = null;
        DatabaseMetaData databaseMetaData = null;
        ResultSet resultSet = null;
        int len = 0;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM " + tableName);
            metaData = preparedStatement.getMetaData();
            //数据库的字段个数
            len = metaData.getColumnCount();
            databaseMetaData = connection.getMetaData();
            resultSet = databaseMetaData.getColumns(null, "%", tableName, "%");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String result[] = new String[len + 1];
        try {
            if (type.equalsIgnoreCase("type")) {
                for (int i = 1; i <= len; i++) {
                    result[i] = metaData.getColumnTypeName(i); //获取字段类型
                }
            } else if (type.equalsIgnoreCase("name")) {
                for (int i = 1; i <= len; i++) {
                    result[i] = metaData.getColumnName(i); //获取字段名称
                }
            } else if (type.equalsIgnoreCase("remark")) {
                int i = 1;
                if (resultSet != null) {
                    while (resultSet.next()) {
                        result[i] = resultSet.getString("REMARKS");
                        i++;
                    }
                }
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
