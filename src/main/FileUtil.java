package main;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author: fuyuaaaaa
 * @description: 字符串写入文件
 * @program: db2bean
 * @creat: 2018-10-26 09:43
 **/
public class FileUtil {

    public static void write(String str, String fileName,String path) {
        FileWriter writer;
        try {
            writer = new FileWriter(path + fileName);
            writer.write(str);
            writer.flush();
            System.out.println(fileName + " write successfully!");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
