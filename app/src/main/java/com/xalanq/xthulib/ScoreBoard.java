package com.xalanq.xthulib;

/**
 * CopyRight © 2018 by xalanq. All Rights Reserved.
 *
 * @author: xalanq
 * @email: xalanq@gmail.com
 * @version: v1.0.0
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 获取中文成绩单
 */
public class ScoreBoard {

    public static final String TAG = "ScoreBoard";

    /**
     * 获取中文成绩单（前提是已经登录了info）
     *
     * @return 成绩单，若成绩为***则返回-1，若成绩为p/f返回0
     *
     * @throws IOException 若连接失败则抛出
     */
    public static List<ScoreBoardItem> get() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(CookieManager.getCookieJar())
            .build();

        FormBody form = new FormBody.Builder()
            .add("m", "bks_cjdcx")
            .add("cjdlx", "zw")
            .build();

        Request request = new Request.Builder()
            .url(URL.info_chinese_score)
            .addHeader("User-Agent", URL.agent)
            .post(form)
            .build();

        List<ScoreBoardItem> list = new ArrayList<>();

        try {
            Response response = client.newCall(request).execute();
            String body = new String(response.body().bytes(), "gb2312");
            int idx_begin = body.indexOf("考试时间");
            int idx_end = body.indexOf("<table", idx_begin);
            while (idx_begin < idx_end) {
                int idx_l = 0, idx_r = 0;
                String[] str = new String[6];
                for (int i = 0; i < 6 && idx_begin <= idx_end; ++i) {
                    idx_l = body.indexOf("<td", idx_begin);
                    idx_l = body.indexOf(">", idx_l) + 1;
                    idx_r = body.indexOf("<", idx_l) - 1;
                    char ch = body.charAt(idx_l);
                    while (idx_l <= idx_r && (ch == ' ' || ch == '\r' || ch == '\t' || ch == '\n'))
                        ch = body.charAt(++idx_l);
                    ch = body.charAt(idx_r);
                    while (idx_l <= idx_r && (ch == ' ' || ch == '\r' || ch == '\t' || ch == '\n'))
                        ch = body.charAt(--idx_r);
                    str[i] = body.substring(idx_l, idx_r + 1);
                    idx_begin = idx_r + 1;
                }
                if (idx_begin >= idx_end)
                    break;

                ScoreBoardItem item = new ScoreBoardItem();
                item.id = str[0];
                item.name = str[1];
                item.credit = Integer.parseInt(str[2]);
                item.grade = str[3];
                String gpa = str[4];
                if (gpa.equals("***"))
                    gpa = "-1";
                else if (gpa.indexOf('P') != -1)
                    gpa = "0";
                try {
                    item.gpa = Double.parseDouble(gpa);
                } catch (Exception ee) {
                    item.gpa = 0;
                }
                item.term = str[5];

                list.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
