package com.xalanq.xthulib;

/**
 * CopyRight © 2018 by xalanq. All Rights Reserved.
 *
 * @author: xalanq
 * @email: xalanq@gmail.com
 * @version: v1.0.0
 */

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 获取gpa
 */
public class GPA {

    public static final String TAG = "GPA";

    /**
     * 获取gpa（前提是已经登录了info）
     *
     * @return gpa，如果获取失败则返回0
     *
     * @throws IOException 若连接失败，则抛出
     */
    public static double get() throws IOException {

        OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(CookieManager.getCookieJar())
            .build();

        Request request = new Request.Builder()
            .url(URL.info_gpa)
            .addHeader("User-Agent", URL.agent)
            .build();

        double gpa = 0;

        try {
            Response response = client.newCall(request).execute();
            String body = new String(response.body().bytes(), "utf-8");
            final String str[] = {"已修所有课程学分绩", "value=\"", "\""};
            int idx_0 = body.indexOf(str[0]);
            if (idx_0 == -1)
                return 0;
            int idx_1 = body.indexOf(str[1], idx_0 + str[0].length());
            if (idx_1 == -1)
                return 0;
            int idx_2 = body.indexOf(str[2], idx_1 + str[1].length());
            if (idx_2 == -1)
                return 0;
            return Double.parseDouble(body.substring(idx_1 + str[1].length(), idx_2));
        }
        catch (IOException e) {
            throw e;
        }
    }
}
