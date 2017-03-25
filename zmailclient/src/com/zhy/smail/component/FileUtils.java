package com.zhy.smail.component;

import java.io.*;

/**
 * Created by wenliz on 2017/3/25.
 */
public class FileUtils {

    public static void writeMac(String mac){
        String fileName = System.getProperty("user.home")+"\\zz.s";
        writeFile(fileName, mac);
    }

    public static String readMac(){
        String fileName = System.getProperty("user.home")+"\\zz.s";
        return readFileByLines(fileName);
    }

    public static void writeFile(String fileName, String content) {
        File file = new File(fileName);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            try {
                bw.write(content);
            } finally {
                bw.close();
            }
        }
        catch (IOException e){

        }

    }

    public static String readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                buffer.append(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            return "";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return buffer.toString();
    }
}
