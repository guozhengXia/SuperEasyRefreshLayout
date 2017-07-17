package com.honor.fighting.supereasyrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.honor.fighting.supereasyrefreshlayout.view.SuperEasyRefreshLayout;


public class ScrollViewActivity extends AppCompatActivity {


    private SuperEasyRefreshLayout swipe_refresh_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view);

        swipe_refresh_layout = (SuperEasyRefreshLayout) findViewById(R.id.swipe_refresh_layout);//找到刷新对象

        initListener();
    }

    private void initListener() {
        swipe_refresh_layout.setOnRefreshListener(new SuperEasyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_refresh_layout.setRefreshing(false);
                        Toast.makeText(ScrollViewActivity.this,"刷新 成功",Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }
        });

        swipe_refresh_layout.setOnLoadMoreListener(new SuperEasyRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoad() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_refresh_layout.finishLoadMore();
                        Toast.makeText(ScrollViewActivity.this,"加载更多成功",Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }
        });
    }
}
