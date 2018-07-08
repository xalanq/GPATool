package com.xalanq.gpatool;

/**
 * CopyRight © 2018 by xalanq. All Rights Reserved.
 *
 * @author: xalanq
 * @email: xalanq@gmail.com
 * @version: v1.0.0
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.xalanq.base.BaseActivity;
import com.xalanq.base.CallBack;
import com.xalanq.xthulib.AuthInfo;
import com.xalanq.xthulib.CookieManager;
import com.xalanq.xthulib.ScoreBoard;
import com.xalanq.xthulib.ScoreBoardItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主要活动
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.main_layout_drawer) DrawerLayout drawerLayout;
    @BindView(R.id.main_layout_content) ScrollView contentLayout;
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.main_toolbar_title) TextView toolbarTitle;
    @BindView(R.id.card_content) WebView cardContent;
    @BindView(R.id.card_info) TextView cardInfo;
    @BindView(R.id.card_content_view) CardView contentView;
    @BindView(R.id.card_info_view) CardView infoView;

    private static final String TAG = "MainActivity";

    private boolean doubleClickToExitPressOnce = false;
    private LoginDialog loginDialog;
    private AboutDialog aboutDialog;
    private String contentText;
    private Menu menu;

    private Info currentInfo = new Info();

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);

        initUI();

        SharedPreferences sharedPre = getSharedPreferences("config", MODE_PRIVATE);
        username = sharedPre.getString("username", null);
        password = sharedPre.getString("password", null);

        refresh();
    }

    private void initUI() {
        setToolbar();
        contentView.setVisibility(View.INVISIBLE);
        infoView.setVisibility(View.INVISIBLE);
        cardContent.getSettings().setTextSize(WebSettings.TextSize.SMALLER);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            if (doubleClickToExitPressOnce) {
                super.onBackPressed();
                return;
            }
            doubleClickToExitPressOnce = true;
            Toast.makeText(this, R.string.main_double_click_to_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.doubleClickToExitPressOnce = false;
                }
            }, 2000);
        }
    }

    private void login() {
        if (loginDialog == null) {
            loginDialog = new LoginDialog(MainActivity.this, new CallBack() {
                @Override
                public void call() {
                    MainActivity.this.refresh();
                    MainActivity.this.username = loginDialog.username;
                    MainActivity.this.password = loginDialog.password;
                    SharedPreferences sharedPre = MainActivity.this.getSharedPreferences("config", MainActivity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPre.edit();
                    editor.putString("username", loginDialog.username);
                    editor.putString("password", loginDialog.password);
                    editor.apply();
                    if (MainActivity.this.menu != null)
                        MainActivity.this.menu.findItem(R.id.button_login).setTitle(isLogin() ? R.string.logout_title : R.string.login_title);
                }
            }, username, password);
            loginDialog.setTitle(R.string.login_title);
        }
        if (username == null)
            loginDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.button_login) {
            if (!isLogin()) {
                login();
            } else {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.logout_title)
                    .setMessage(R.string.logout_message)
                    .setPositiveButton(R.string.logout_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, R.string.logout_positive_message, Toast.LENGTH_SHORT).show();
                            contentView.setVisibility(View.INVISIBLE);
                            infoView.setVisibility(View.INVISIBLE);

                            loginDialog = null;
                            CookieManager.getCookieJar().clear();

                            menu.findItem(R.id.button_login).setTitle(isLogin() ? R.string.logout_title : R.string.login_title);

                            username = null;
                            password = null;
                            SharedPreferences sharedPre = MainActivity.this.getSharedPreferences("config", MainActivity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPre.edit();
                            editor.remove("username");
                            editor.remove("password");
                            editor.apply();

                            MainActivity.this.refresh();
                        }
                    })
                    .setNegativeButton(R.string.logout_negative, null)
                    .show();
            }
        } else if (id == R.id.button_refresh) {
            refresh();
        } else if (id == R.id.button_about) {
            about();
        }
        return super.onOptionsItemSelected(item);
    }

    private void about() {
        if (aboutDialog == null)
            aboutDialog = new AboutDialog(this);
        aboutDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            Drawable drawable = item.getIcon();
            if (drawable != null) {
                drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
        }
        this.menu = menu;
        menu.findItem(R.id.button_login).setTitle(isLogin() ? R.string.logout_title : R.string.login_title);
        return true;
    }

    public void updateView() {
        contentView.setVisibility(View.VISIBLE);
        infoView.setVisibility(View.VISIBLE);
        cardInfo.setText(String.format(getResources().getString(R.string.info_format), currentInfo.totalCredit, currentInfo.GPACredit, currentInfo.GPA));
        cardContent.loadData(contentText, "text/html", "utf-8");
    }

    public void refresh() {
        if (!isLogin()) {
            login();
            return;
        }
        try {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(R.string.get_title);
            dialog.setMessage(getString(R.string.get_ing));
            dialog.setCancelable(false);
            dialog.show();

            new AsyncTask<Void, Integer, Boolean>() {

                private int successful;
                private List<ScoreBoardItem> scoreList;
                double GPA;

                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        AuthInfo.auth(username, password);
                        scoreList = ScoreBoard.get();
                        GPA = com.xalanq.xthulib.GPA.get();
                        for (int i = 1; i <= 10 && GPA == 0; ++i) {
                            AuthInfo.auth(username, password);
                            GPA = com.xalanq.xthulib.GPA.get();
                        }
                        successful = 1;
                    } catch (Exception e) {
                        successful = -1;
                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (successful == 1) {
                        StringBuilder builder = new StringBuilder();
                        int totalCredit = 0;
                        int GPACredit = 0;
                        builder.append("<html><body><table align='center'>");
                        builder.append("<tr>");
                        builder.append("<td>").append("课程号").append("</td>");
                        builder.append("<td>").append("课程名").append("</td>");
                        builder.append("<td>").append("学分").append("</td>");
                        builder.append("<td>").append("成绩").append("</td>");
                        builder.append("<td>").append("绩点").append("</td>");
                        builder.append("<td>").append("学年/学期").append("</td>");
                        builder.append("</tr>");
                        for (ScoreBoardItem item : scoreList) {
                            double gpa = item.gpa;
                            int credit = item.credit;
                            String grade = item.grade;

                            builder.append("<tr>");
                            builder.append("<td>").append(item.id).append("</td>");
                            builder.append("<td>").append(item.name).append("</td>");
                            builder.append("<td>").append(item.credit).append("</td>");
                            builder.append("<td>").append(grade).append("</td>");
                            builder.append("<td>").append(gpa == -1 ? "***" : (gpa == 0 ? "P" : String.valueOf(gpa))).append("</td>");
                            builder.append("<td>").append(item.term).append("</td>");
                            builder.append("</tr>");
                            // builder.append("课程号: ").append(item.id).append('\n');
                            // builder.append("课程名: ").append(item.name).append('\n');
                            // builder.append("学分: ").append(item.credit).append('\n');
                            // builder.append("等级: ").append(item.grade).append('\n');
                            // builder.append("绩点: ").append(gpa == -1 ? "***" : (gpa == 0 ? "P" : String.valueOf(gpa))).append('\n');
                            // builder.append("学期: ").append(item.term).append('\n').append('\n');

                            if (grade.indexOf('W') == -1) {
                                totalCredit += credit;
                                if (gpa != 0)
                                    GPACredit += credit;
                            }
                            
                        }
                        builder.append("</table></body></html>");
                        MainActivity.this.currentInfo.totalCredit = totalCredit;
                        MainActivity.this.currentInfo.GPACredit = GPACredit;
                        MainActivity.this.currentInfo.GPA = GPA;
                        MainActivity.this.contentText = builder.toString();
                        MainActivity.this.updateView();
                    } else if (successful == -1)
                        Toast.makeText(MainActivity.this, R.string.network_disabled, Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }

            }.execute();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, R.string.network_disabled, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isLogin() {
        return !(loginDialog == null || !loginDialog.isLogin);
    }
}
