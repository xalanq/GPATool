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
import com.xalanq.xthulib.GPA;

import org.junit.Test;

public class GPATest {

    @Test
    public void test() throws Exception {
        AuthInfo.auth("test_name", "test_password");
        AuthInfo.auth("test_name", "test_password");
        Log.d("GPATest", "test: " + GPA.get());
    }

}
