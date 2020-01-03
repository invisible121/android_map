package com.luoma.map.util;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtil {

    public static String LocalIp = "http://127.0.0.1:8084";
    public static String ServerIp = "http://39.96.51.119:8084";

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public String post(String path, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(ServerIp + path)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    public String get(String url, JSONObject json) throws IOException {

        String param = json.toString().
                replace("{", "").
                replace("}", "").
                replace("\"", "").
                replace(",", "&").
                replace(":", "=");
        Request request = new Request.Builder()
                .url(ServerIp + url + "?" + param)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
