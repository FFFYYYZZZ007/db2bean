package main;

/**
 * @author: fuyuaaaaa
 * @description: 封装要写入文件的内容
 * @program: db2bean
 * @creat: 2018-10-26 10:01
 **/
public class FileContentUtil {

    public static String bean(String[] colTypes, String[] colNames, String[] remarks, String beanName) {
        StringBuilder sb = new StringBuilder();
        sb.append("public class ").append(StringUtil.upperCaseFirstChar(beanName)).append("{\n");
        for (int i = 1; i < colNames.length; i++) {
            if (colTypes[i] != null) {
                String row = "private " + swapType(colTypes[i]) + " " + StringUtil.underline2Camel(colNames[i], true) + ";";
                sb.append(row);
                if (remarks[i] != null) {
                    sb.append(" //").append(remarks[i]);
                }
            }
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    public static String mapper(String[] colNames, String tableName, String beanName) {
        String camelCol[] = new String[colNames.length];
        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i] != null) {
                camelCol[i] = StringUtil.underline2Camel(colNames[i]);
            }
        }
        String lBeanName = StringUtil.lowerCaseFirstChar(beanName);
        String uBeanName = StringUtil.upperCaseFirstChar(beanName);
        String both = uBeanName + " " + lBeanName;
        StringBuilder sb = new StringBuilder();
        sb.append("public interface ").append(StringUtil.upperCaseFirstChar(beanName)).append("Mapper").append("{\n");

        //=====INSERT
        sb.append("@Insert(\" INSERT INTO ").append(tableName).append(" (");
        for (int i = 1; i < colNames.length; i++) {
            if (colNames[i] != null) {
                if (i == colNames.length - 1) {
                    sb.append(colNames[i]);
                } else {
                    sb.append(colNames[i]).append(",");
                }
            }
        }
        sb.append(") VALUES (");
        for (int i = 1; i < colNames.length; i++) {
            if (colNames[i] != null) {
                if (i == colNames.length - 1) {
                    sb.append("#{").append(camelCol[i]).append("}");
                } else {
                    sb.append("#{").append(camelCol[i]).append("}").append(",");
                }
            }
        }
        sb.append(" )\"）\n");
        if (colNames[1].equalsIgnoreCase("id")) {
            sb.append("@Options(useGeneratedKeys = true, keyProperty = \"id\", keyColumn = \"id\")");
        }
        sb.append("void add").append(uBeanName).append(brackets(both)).append(";");
        //=====INSERT END

        sb.append("\n");
        sb.append("\n");
        //=====DELETE
        sb.append("@Delete(\" DELETE FROM ").append(tableName).append(" WHERE id = #{id}\")\n");
        sb.append("void delete(int id)");

        sb.append("\n");
        sb.append("\n");
        //=====UPDATE
        sb.append("@Update(\" UPDATE ").append(tableName).append(" SET ");
        for (int i = 1; i < colNames.length; i++) {
            if (colNames[i] != null) {
                if (i == colNames.length - 1) {
                    sb.append(colNames[i]).append("=#{").append(camelCol[i]).append("}");
                } else {
                    sb.append(colNames[i]).append("=#{").append(camelCol[i]).append("},");
                }
            }
        }
        sb.append(" WHERE id=#{id} \")\n");
        sb.append("void update").append(uBeanName).append(brackets(both)).append(";");

        sb.append("\n");
        sb.append("\n");
        //=====detail
        sb.append("@Select(\" SELECT * FROM ").append(tableName).append(" WHERE id=#{id} LIMIT 1 \")\n");
        sb.append(uBeanName).append(" getById").append(brackets(both));

        sb.append("\n");
        sb.append("\n");
        //=====find all
        sb.append("@Select(\" SELECT * FROM ").append(tableName).append(" \")\n");
        sb.append("List<").append(uBeanName).append("> findAll(Map<String,Object> param);");
        sb.append("\n}");
        return sb.toString();
    }

    public static String controller(String[] colTypes, String[] colNames,String remarks[], String beanName) {
        String camelCol[] = new String[colNames.length];
        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i] != null) {
                camelCol[i] = StringUtil.underline2Camel(colNames[i]);
            }
        }
        String lBeanName = StringUtil.lowerCaseFirstChar(beanName);
        String uBeanName = StringUtil.upperCaseFirstChar(beanName);
        String both = uBeanName + " " + lBeanName;

        StringBuilder sb = new StringBuilder();
        sb.append("public class ").append(uBeanName).append("Controller").append("{\n\n");

        //add
        sb.append("public ResponseEntity<").append(uBeanName).append("> add").append(uBeanName).append("(");
        for (int i = 1; i < colNames.length; i++) {
            if (colTypes[i] != null) {
                String defaultValue = "";
                if (swapType(colTypes[i]).equalsIgnoreCase("int") || swapType(colTypes[i]).equalsIgnoreCase("float")) {
                    defaultValue = "-1";
                }
                if (i == colNames.length - 1) {
                    sb.append("@RequestParam(value = \"" + camelCol[i] + "\", required = false, defaultValue = \"" + defaultValue + "\") ")
                            .append(swapType(colTypes[i])).append(" ").append(camelCol[i]);
                } else {
                    sb.append("@RequestParam(value = \"" + camelCol[i] + "\", required = false, defaultValue = \"" + defaultValue + "\") ")
                            .append(swapType(colTypes[i])).append(" ").append(camelCol[i])
                            .append(",\n");
                }
            }
        }
        sb.append("){\n");
        sb.append("ResponseEntity<").append(uBeanName).append("> response = new ResponseEntity<>()\n");
        sb.append(both).append("= new ").append(uBeanName).append("();");
        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i] != null) {
                sb.append(beanName).append(".set").append(camelCol[i])
                        .append("(").append(camelCol[i])
                        .append(");\n");

            }
        }

        sb.append("return response;\n}");
        //=====add end

        sb.append("\n");
        sb.append("\n");
        //update
        sb.append("public ResponseEntity<").append(uBeanName).append("> update").append(uBeanName).append("(");
        for (int i = 1; i < colNames.length; i++) {
            if (colTypes[i] != null) {
                String defaultValue = "";
                if (swapType(colTypes[i]).equalsIgnoreCase("int") || swapType(colTypes[i]).equalsIgnoreCase("float")) {
                    defaultValue = "-1";
                }
                if (i == colNames.length - 1) {
                    sb.append("@RequestParam(value = \"" + camelCol[i] + "\", required = false, defaultValue = \"" + defaultValue + "\") ")
                            .append(swapType(colTypes[i])).append(" ").append(camelCol[i]);
                } else {
                    sb.append("@RequestParam(value = \"" + camelCol[i] + "\", required = false, defaultValue = \"" + defaultValue + "\") ")
                            .append(swapType(colTypes[i])).append(" ").append(camelCol[i])
                            .append(",\n");
                }
            }
        }

        sb.append("){\n");
        for (int i = 0; i < colNames.length; i++) {
            if (colTypes[i] != null) {
                sb.append("if(");
                if (swapType(colTypes[i]).equalsIgnoreCase("int")) {
                    sb.append("-1!=").append(camelCol[i]).append("){");
                } else {
                    sb.append("StringUtil.isNotEmpty(").append(camelCol[i])
                            .append(")){");
                }
                sb.append(beanName).append(".set").append(StringUtil.underline2Camel(colNames[i], false))
                        .append("(").append(camelCol[i])
                        .append(");}\n");

            }
        }
        sb.append("return response;\n}");
        //=====update end

        sb.append("\n//===excel 导出的表头信息");
        sb.append("\n//");
        for (int i = 1; i < colNames.length; i++) {
            sb.append("\"").append(camelCol[i]).append("\",");
        }
        sb.append("\n//");
        for (int i = 1; i < remarks.length; i++) {
            if (i == remarks.length - 1) {
                sb.append("\"").append(remarks[i]).append("\"");
            } else {
                sb.append("\"").append(remarks[i]).append("\",");
            }
        }
        sb.append("\n}");
        return sb.toString();
    }

    public static String brackets(String str) {
        return "(" + str + ")";
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
}
