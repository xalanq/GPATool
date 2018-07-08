package com.xalanq.gpatool;

/**
 * CopyRight © 2018 by xalanq. All Rights Reserved.
 *
 * @author: xalanq
 * @email: xalanq@gmail.com
 * @version: v1.0.0
 */

import android.util.Log;

import com.xalanq.xthulib.AuthInfo;
import com.xalanq.xthulib.ScoreBoard;
import com.xalanq.xthulib.ScoreBoardItem;

import org.junit.Test;

import java.util.List;

public class ScoreBoardTest {

    @Test
    public void test() throws Exception {
        AuthInfo.auth("test_name", "test_password");
        AuthInfo.auth("test_name", "test_password");
        StringBuilder builder = new StringBuilder();
        List<ScoreBoardItem> list = ScoreBoard.get();
        for (ScoreBoardItem item : list) {
            builder.append("课程号: ").append(item.id).append('\n');
            builder.append("课程名: ").append(item.name).append('\n');
            builder.append("学分: ").append(item.credit).append('\n');
            builder.append("等级: ").append(item.grade).append('\n');
            builder.append("绩点: ").append(item.gpa).append('\n');
            builder.append("学期: ").append(item.term).append('\n');
            builder.append('\n');
        }
        Log.d("ScoreBoardTest", "test: " + builder.toString());
    }

}
