package org.bigmouth.senon.commom.utils;

import com.alibaba.fastjson.JSONObject;


public final class JsonHelper {

    private JsonHelper() {
    }
    
    public static String convert(Object object) {
        return JSONObject.toJSONString(object);
    }
    
    public static byte[] convert2bytes(Object object) {
        return StringHelper.convert(convert(object));
    }
    
    public static <T> T convert(String string, Class<T> clazz) {
        return JSONObject.parseObject(string, clazz);
    }
    
    public static <T> T convert(byte[] array, Class<T> clazz) {
        return JSONObject.parseObject(StringHelper.convert(array), clazz);
    }
}
