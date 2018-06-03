package org.MySqlDBManager;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Created by liangwenchang on 2018/5/15.
 */
public class MySQLHelper {
    private Logger logger = Logger.getLogger(this.getClass());
    private String url = null;
    private String user = null;
    private String pwd = null;
    private Connection conn = null;
    private PreparedStatement preparedStatement = null;

    public MySQLHelper(){
        GetConfig();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url,user,pwd);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("数据库初始化失败."+ e.toString());
        }
    }
    public void Close(){
        try{
            if (conn != null){
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void GetConfig(){
        InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties");
        Properties ppt = new Properties();
        try{
            ppt.load(in);
            this.url = ppt.getProperty("mysqlUrl");
            this.user = ppt.getProperty("usrer");
            this.pwd = ppt.getProperty("pwd");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //执行操作
    public boolean ExecuteNonquery(String sql) throws Exception {
        if(sql == null || sql == ""){
            return false;
        }
        boolean flg = false;
        try{
            if(conn != null){
                preparedStatement = conn.prepareStatement(sql);
                int res = preparedStatement.executeUpdate();
                if(res > 0){
                    flg = true;
                }
            }else {
                logger.debug("数据库连接句柄没有实例化");
            }
        }catch (Exception e){
            logger.error("操作数据库异常："+e.toString());
            throw e;
        }
        return flg;
    }

    //查询
    public ResultSet ExecuteQuery(String sql){
        ResultSet set = null;
        try{
            if (conn != null){
                preparedStatement = conn.prepareStatement(sql);
                set = preparedStatement.executeQuery();
            }else {
                logger.debug("数据库连接句柄没有实例化");
            }
        }catch (Exception e){
            logger.error("查询数据异常：" + e.toString());
        }
        return set;
    }
}
