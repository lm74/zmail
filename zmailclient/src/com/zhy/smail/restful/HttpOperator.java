package com.zhy.smail.restful;

import com.zhy.smail.common.json.JsonResult;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.NoRouteToHostException;

/**
 * Created by wenliz on 2017/1/22.
 */
public class HttpOperator {
    public static ObjectMapper mapper = new ObjectMapper();

    public static void get(String url, RestfulResult result){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try{
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try{
                if(response.getStatusLine().getStatusCode() == 200){
                    doResult(response, result);
                }
                else{
                    doFault(response, result);
                }

            }
            finally {
                response.close();
            }
        }
        catch (HttpHostConnectException e){
            RfFaultEvent event = new RfFaultEvent();
            event.setErrorNo(-2);
            event.setMessage("连接服务器("+ e.getHost().getHostName()+")失败(1001). ");
            result.doFault(event);
        }
        catch (NoRouteToHostException e){
            RfFaultEvent event = new RfFaultEvent();
            event.setErrorNo(-3);
            event.setMessage("网络错误(1002). ");
            result.doFault(event);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                httpClient.close();
            }
            catch(IOException e){

            }
        }
    }

    public static void post(String value, String url, RestfulResult result){
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try{
            HttpPost httpPost = new HttpPost(url);
            //httpPost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            StringEntity se = new StringEntity(value, "UTF-8");
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");

            httpPost.setEntity(se);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            try{
                if(response.getStatusLine().getStatusCode() == 200){
                    doResult(response, result);
                }
                else{
                    doFault(response, result);
                }

            }
            finally {
                response.close();
            }
        }
        catch (UnsupportedEncodingException e){

        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try {
                httpClient.close();
            }
            catch (IOException e){

            }
        }
    }

    public static void delete( String url, RestfulResult result){
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try{
            HttpDelete httpDelete = new HttpDelete(url);

            //StringEntity se = new StringEntity(value);
            //se.setContentType("application/json");
            //httpPost.setEntity(se);
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            try{
                if(response.getStatusLine().getStatusCode() == 200){
                    doResult(response, result);
                }
                else{
                    doFault(response, result);
                }

            }
            finally {
                response.close();
            }
        }
        catch (UnsupportedEncodingException e){

        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try {
                httpClient.close();
            }
            catch (IOException e){

            }
        }
    }

    private static void doResult(CloseableHttpResponse response, RestfulResult result){
       RfResultEvent event = new RfResultEvent();
        HttpEntity entity = response.getEntity();
        try {
            InputStream in = entity.getContent();
            JsonResult jsonResult = mapper.readValue(in, JsonResult.class);
            event.setMessage(jsonResult.getMessage());
            event.setResult(jsonResult.getResult());
            event.setData(jsonResult.getData());
            result.doResult(event);
        }
        catch (IOException e){

        }


    }

    private static void doFault(CloseableHttpResponse response, RestfulResult result){
        RfFaultEvent event = new RfFaultEvent();
        event.setErrorNo(response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        if(entity != null){
            try {
                event.setMessage(EntityUtils.toString(entity));
            }
            catch(Exception e){

            }
        }
        else {
            event.setMessage("");
        }
        result.doFault(event);

    }
}
