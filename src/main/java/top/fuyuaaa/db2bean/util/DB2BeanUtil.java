package top.fuyuaaa.db2bean.util;

import java.sql.*;

/**
 * @author: fuyuaaaaa
 * @description:
 * @program: db2beanutil
 * @creat: 2018-10-25 10:29
 **/
public class DB2BeanUtil {
    public static void main(String[] args) {
        String dbType = "postgresql";
        String url = "jdbc:postgresql://10.32.168.131:5432/gms";
        String user = "postgres";
        String password = "postgres";
        String tableName = "tbl_enterprise";
        String beanName = "enterprise";
        System.out.println(get(dbType, url, user, password, tableName, beanName));
    }

    public static Connection getConnection(String dbType, String url, String user, String password) {
        try {
            if (dbType.equalsIgnoreCase("mysql")) {
                Class.forName("com.mysql.jdbc.Driver");
            } else if (dbType.equalsIgnoreCase("postgresql")) {
                Class.forName("org.postgresql.Driver");
            }

            Connection connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get(String dbType, String url, String user, String pass, String tableName, String beanName) {
        try {
            Connection connection = getConnection(dbType, url, user, pass);

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = preparedStatement.getMetaData();

            //数据库的字段个数
            int len = metaData.getColumnCount();
            //字段名称
            String[] colNames = new String[len + 1];
            //字段类型 --->已经转化为java中的类名称了
            String[] colTypes = new String[len + 1];


            for (int i = 1; i <= len; i++) {
                colNames[i] = metaData.getColumnName(i); //获取字段名称
                colTypes[i] = metaData.getColumnTypeName(i); //获取字段类型
            }

            //===字段备注
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet rs1 = databaseMetaData.getColumns(null, "%", tableName, "%");
            String[] remarks = new String[len + 1];
            int i = 1;
            while (rs1.next()) {
                remarks[i] = rs1.getString("REMARKS");
                i++;
            }
            rs1.close();
            //===

            preparedStatement.close();
            connection.close();

            return results(colNames, colTypes, remarks, tableName, beanName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String swapType(String sqlType) {
        if (sqlType.equalsIgnoreCase("int") || sqlType.contains("INT") || sqlType.contains("int") || sqlType.equalsIgnoreCase("serial")) {
            return "int".toLowerCase();
        }
        if (sqlType.equalsIgnoreCase("varchar")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("date")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("timestamp")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "float";
        }
        return sqlType;
    }

    public static String results(String[] colNames, String[] colTypes, String[] remarks, String tableName, String beanName) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < colNames.length; i++) {

            if (colTypes[i] != null) {
                String row = "private " + swapType(colTypes[i]) + " " + UnderLine2CamelUtils.underline2Camel(colNames[i], true) + ";";
                sb.append(row);
                if (remarks[i] != null) {
                    sb.append(" //").append(remarks[i]);
                }
            }
            sb.append("\n");
        }

        //===========insert
        sb.append("======INSERT=====\nINSERT INTO " + tableName + " (");

        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i] != null) {
                if (i != colNames.length - 1) {
                    sb.append(colNames[i] + ",");
                } else {
                    sb.append(colNames[i]);
                }
            }
        }
        sb.append(") \nVALUES (");
        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i] != null) {
                sb.append("#{");
                if (i != colNames.length - 1) {
                    sb.append(UnderLine2CamelUtils.underline2Camel(colNames[i]) + "},");
                } else {
                    sb.append(UnderLine2CamelUtils.underline2Camel(colNames[i]) + "}");
                }
            }
        }
        sb.append(")");
        //==========end insert


        //====update
        sb.append("\n=====UPDATE=====\nUPDATE " + tableName + " SET ");
        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i] != null) {
                if (i != colNames.length - 1) {
                    sb.append(colNames[i] + "=#{" + UnderLine2CamelUtils.underline2Camel(colNames[i]) + "},");
                } else {
                    sb.append(colNames[i] + "=#{" + UnderLine2CamelUtils.underline2Camel(colNames[i]) + "}");
                }
            }
        }
        //====end update

        sb.append("\n=====add requestParam =====\n");
        for (int i = 0; i < colNames.length; i++) {
            if (colTypes[i] != null) {
                String defaultValue = "";
                if (swapType(colTypes[i]).equalsIgnoreCase("int") || swapType(colTypes[i]).equalsIgnoreCase("float")) {
                    defaultValue = "-1";
                }
                sb.append("@RequestParam(value = \"" + UnderLine2CamelUtils.underline2Camel(colNames[i]) + "\", required = false, defaultValue = \"" + defaultValue + "\") ")
                        .append(swapType(colTypes[i])).append(" ").append(UnderLine2CamelUtils.underline2Camel(colNames[i]))
                        .append(",\n");
            }
        }

        sb.append("=====add set=====\n");
        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i] != null) {
                sb.append(beanName).append(".set").append(UnderLine2CamelUtils.underline2Camel(colNames[i], false))
                        .append("(").append(UnderLine2CamelUtils.underline2Camel(colNames[i], true))
                        .append(");\n");

            }
        }

        sb.append("=====update set=====\n");
        for (int i = 0; i < colNames.length; i++) {
            if (colTypes[i] != null) {
                sb.append("if(");
                if (swapType(colTypes[i]).equalsIgnoreCase("int")) {
                    sb.append("-1!=").append(UnderLine2CamelUtils.underline2Camel(colNames[i])).append("){");
                } else {
                    sb.append("StringUtils.isNotEmpty(").append(UnderLine2CamelUtils.underline2Camel(colNames[i]))
                            .append(")){");
                }
                sb.append(beanName).append(".set").append(UnderLine2CamelUtils.underline2Camel(colNames[i], false))
                        .append("(").append(UnderLine2CamelUtils.underline2Camel(colNames[i], true))
                        .append(");}\n");

            }
        }

        sb.append("=====excel export=====\n");
        for (int i = 1; i < colNames.length; i++) {
            sb.append("\"").append(UnderLine2CamelUtils.underline2Camel(colNames[i], true)).append("\",");
        }
        for (int i = 1; i < remarks.length; i++) {
            if (i == remarks.length - 1) {
                sb.append("\"").append(remarks[i]).append("\"");
            } else {
                sb.append("\"").append(remarks[i]).append("\",");
            }
        }
        return sb.toString();
    }
}
