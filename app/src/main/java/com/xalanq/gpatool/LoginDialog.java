package com.xalanq.gpatool;

/**
 * CopyRight © 2018 by xalanq. All Rights Reserved.
 *
 * @author: xalanq
 * @email: xalanq@gmail.com
 * @version: v1.0.0
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xalanq.base.CallBack;
import com.xalanq.xthulib.AuthInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 登录窗口
 */
public class LoginDialog extends Dialog {

    @BindView(R.id.title) TextView title;
    @BindView(R.id.username) TextView usernameView;
    @BindView(R.id.password) TextView passwordView;
    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.sign_in) Button button;

    public boolean isLogin = false;
    public String username;
    public String password;
    private CallBack callBack = null;

    public LoginDialog(@NonNull Context context, CallBack callBack, String username, String password) {
        super(context);
        setContentView(R.layout.login_dialog);

        ButterKnife.bind(this);

        this.callBack = callBack;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(false, null, null);
            }
        });

        if (username != null) {
            login(true, username, password);
        }
    }

    private void login(final boolean hidden, String default_username, String default_password) {
        final String username = hidden ? default_username : usernameView.getText().toString();
        final String password = hidden ? default_password : passwordView.getText().toString();
        this.username = username;
        this.password = password;
        View focusView = null;
        messageView.setVisibility(View.GONE);
        messageView.setText(null);
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getContext().getString(R.string.login_password_empty));
            focusView = passwordView;
        }

        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getContext().getString(R.string.login_username_empty));
            focusView = usernameView;
        }

        if (focusView != null) {
            focusView.requestFocus();
        } else {
            final ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setTitle(R.string.login_title);
            dialog.setMessage(getContext().getString(R.string.login_ing));
            dialog.setCancelable(false);
            new AsyncTask<Void, Integer, Boolean>() {

                private int successful;

                @Override
                protected void onPreExecute() {
                    dialog.show();
                }

                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        if (AuthInfo.auth(username, password)) {
                            successful = 1;
                        }
                        else
                            successful = 0;
                    } catch (Exception e) {
                        successful = -1;
                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (successful == 0) {
                        messageView.setVisibility(View.VISIBLE);
                        messageView.setText(R.string.login_fail);
                        passwordView.requestFocus();
                        if (hidden) {
                            Toast.makeText(getContext(), R.string.login_fail, Toast.LENGTH_SHORT).show();
                        }
                    } else if (successful == -1) {
                        messageView.setVisibility(View.VISIBLE);
                        messageView.setText(R.string.network_disabled);
                        if (hidden) {
                            Toast.makeText(getContext(), R.string.network_disabled, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        LoginDialog.this.cancel();
                        LoginDialog.this.isLogin = true;
                        Toast.makeText(getContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                        LoginDialog.this.callBack.call();
                    }
                    dialog.cancel();
                }

            }.execute();
        }
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        title.setText(titleId);
    }

}
