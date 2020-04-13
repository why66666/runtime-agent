package com.weaver.check.agent.runtime.utils;

import com.weaver.check.agent.runtime.RunTimeData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author w
 * @date 2020-03-10 14:54
 */
public class HttpUtil {
    public static String sendGet(String url){
        HttpURLConnection conn = null;
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try {
            URL http = new URL(url);
            conn = (HttpURLConnection) http.openConnection();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String temp;
            while ((temp = br.readLine()) != null) {
                result.append(temp);
            }
            Map<String, List<String>> map = conn.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                System.out.println("Key : " + entry.getKey() +
                        " ,Value : " + entry.getValue());
            }
            System.out.println(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 断开与远程地址url的连接
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result.toString();
    }


    public static String sendPostOnJson(String url, String jsonString) {
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL http = new URL(url);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) http.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("POST");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);
            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
            connection.setDoInput(true);
            // 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
            connection.setRequestProperty("Content-Type", "application/json");
            // 设置鉴权信息：Authorization: Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0
            //connection.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            // 通过连接对象获取一个输出流
            os = connection.getOutputStream();
            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
            os.write(jsonString.getBytes());
            // 通过连接对象获取一个输入流，向远程读取
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 对输入流对象进行包装:charset根据工作项目组的要求来设置
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                // 循环遍历一行一行读取数据
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (Exception e) {
            System.out.println("runtimecheck无法连接dc");
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("runtimecheck无法连接dc");
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    System.out.println("runtimecheck无法连接dc");
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    System.out.println("runtimecheck无法连接dc");
                }
            }
            // 断开与远程地址url的连接
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
}
