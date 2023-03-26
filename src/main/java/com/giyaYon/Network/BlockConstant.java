package com.giyaYon.Network;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * Network: BLOCK_PROTOCOL
 * @author GiyaYon
 */
public class BlockConstant {
    public final static int QUERY_LATEST_BLOCK = 1;
    public final static int QUERY_BLOCKCHAIN = 2;
    public final static int QUERY_LATEST_TRANSACTION = 3;
    public final static int UPLOAD_MINED_BLOCK = 4;
    public final static int RETURN_LATEST_BLOCK = 5;
    public final static int RETURN_BLOCKCHAIN = 6;

    public static  <T> T simpleJsonToObj(String json, Class<T> cls) {
        Gson gson = new Gson();
        if (Objects.isNull(json)) return null;
        T obj = gson.fromJson(json, cls);
        if (Objects.isNull(obj)) {
            return null;
        } else {
            return obj;
        }
    }

    public static String simpleObjToJson(Object obj) {
        if (Objects.isNull(obj)) return "";
        try {
            Gson gson = new Gson();
            return gson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
