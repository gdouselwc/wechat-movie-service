package org.douban.Web;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.javassist.bytecode.stackmap.BasicBlock;
import org.apache.ibatis.jdbc.Null;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liangwenchang on 2018/5/15.
 */
public class WebRequest {

    public String sendGet(String url){
        if(url == null || url==""){
            return "";
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        String res = null;

        try{

            HttpGet httpGet = new HttpGet(url);

            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if(entity != null){
                res = EntityUtils.toString(entity);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (response != null){
                try{
                    response.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(httpclient != null){
                try{
                    httpclient.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
        return res;
    }

    public String sendPost(String url, Map<String,String> params){
        if(url == null || url==""){
            return "";
        }

        CloseableHttpResponse response = null;
        String res = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try{
            URL url1 = new URL(url);
            URI uri = new URI(url1.getProtocol(), url1.getHost(), url1.getPath(), url1.getQuery(), null);
            HttpPost httpPost = new HttpPost(uri);

            //有参数
            if(params.size() > 0){
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                for (Map.Entry<String,String> entry : params.entrySet()) {
                    formparams.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(formparams, Consts.UTF_8));
            }
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (response != null){
                try{
                    response.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(httpclient != null){
                try{
                    httpclient.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return res;
    }
}
