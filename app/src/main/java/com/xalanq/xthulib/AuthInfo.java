package com.xalanq.xthulib;

/**
 * CopyRight © 2018 by xalanq. All Rights Reserved.
 *
 * @author: xalanq
 * @email: xalanq@gmail.com
 * @version: v1.0.0
 */

import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * info授权
 */
public class AuthInfo {

    private static final String TAG = "AuthInfo";

    /**
     * info授权
     *
     * @param username 用户名
     * @param password 密码
     *
     * @return 如果授权成功返回true，否则返回false
     *
     * @throws IOException 连接失败或超时则抛出
     */
    public static boolean auth(String username, String password) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(CookieManager.getCookieJar())
            .build();

        FormBody form = new FormBody.Builder()
            .add("userName", username)
            .add("password", password)
            .build();

        Request request = new Request.Builder()
            .url(URL.info_login_post)
            .addHeader("User-Agent", URL.agent)
            .post(form)
            .build();

        boolean is_auth = false;

        try {
            Response response = client.newCall(request).execute();
            if (URL.info_login_check.equals(response.request().url().toString())) {
                is_auth = true;

                // auth: zhjw
                String body = response.body().string();
                int idx = 0;
                while (idx != -1) {
                    int idx_0 = body.indexOf("http://zhjw.cic.tsinghua.edu.cn/j_acegi_login.do", idx);
                    if (idx_0 != -1) {
                        int idx_1 = body.indexOf("\"", idx_0);
                        if (idx_1 != -1) {
                            String url = body.substring(idx_0, idx_1);
                            url = url.replace("&amp;", "&");
                            idx = idx_1 + 1;

                            Request r = new Request.Builder()
                                .url(url)
                                .addHeader("User-Agent", URL.agent)
                                .build();

                            client.newCall(r).execute();
                        } else
                            idx = -1;
                    } else
                        idx = -1;
                }

                // auth:
                // 2002: finance.cic.tsinghua.edu.cn
                // 2003: kyxxxt.cic.tsinghua.edu.cn
                // 2005: jxgl.cic.tsinghua.edu.cn
                // 2006: meta.cic.tsinghua.edu.cn
                // String ID[] = {"2002", "2003", "2005", "2006"};
                String ID[] = {"2005"};
                for (String id : ID) {
                    FormBody form_roam = new FormBody.Builder()
                        .add("mode", "local")
                        .add("id", id)
                        .build();

                    Request request_roam = new Request.Builder()
                        .url("http://info.tsinghua.edu.cn/minichan/roamaction.jsp")
                        .addHeader("User-Agent", URL.agent)
                        .post(form_roam)
                        .build();
                    client.newCall(request_roam).execute();
                }
            }
        }
        catch (IOException e) {
            throw e;
        }

        Log.d(TAG, "auth: " + is_auth);

        return is_auth;
    }

}
