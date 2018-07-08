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

import org.junit.Test;

public class AuthInfoTest {

    @Test
    public void test() throws Exception {
        Log.d("AuthInfoTest", "test: " + AuthInfo.auth("test_name", "test_password"));
    }

}
