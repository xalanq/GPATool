package com.xalanq.gpatool;

/**
 * CopyRight © 2018 by xalanq. All Rights Reserved.
 *
 * @author: xalanq
 * @email: xalanq@gmail.com
 * @version: v1.0.0
 */

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 关于窗口
 */
public class AboutDialog extends Dialog {

    @BindView(R.id.about_author) TextView about_author;
    @BindView(R.id.about_blog) TextView about_blog;
    @BindView(R.id.about_github) TextView about_github;

    public AboutDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.about_dialog);

        ButterKnife.bind(this);

        about_author.setText(R.string.about_author);
        about_author.setMovementMethod(LinkMovementMethod.getInstance());

        about_blog.setText(R.string.about_blog);
        about_blog.setMovementMethod(LinkMovementMethod.getInstance());

        about_github.setText(R.string.about_github);
        about_github.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
