package org.Utils;

import org.json.JSONObject;

/**
 * Created by liangwenchang on 2018/6/3.
 */
public  class JsonTrans {
    public static String GetString(JSONObject object,String key){
        if (object != null && !object.isNull(key)){
            return object.getString(key);
        }
        return null;
    }

    public static int GetInt(JSONObject object,String key){
        if (object != null && !object.isNull(key)){
            return object.getInt(key);
        }
        return Integer.MIN_VALUE;
    }
    public static double GetDouble(JSONObject object,String key){
        if (object != null && !object.isNull(key)){
            return object.getDouble(key);
        }
        return Double.MIN_VALUE;
    }
}
