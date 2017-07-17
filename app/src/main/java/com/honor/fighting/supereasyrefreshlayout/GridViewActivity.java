package com.honor.fighting.supereasyrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.honor.fighting.supereasyrefreshlayout.view.SuperEasyRefreshLayout;

public class GridViewActivity extends AppCompatActivity {

    private SuperEasyRefreshLayout swipe_refresh_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gride_view);
        GridView grid_view = (GridView) findViewById(R.id.grid_view);
        grid_view.setAdapter(new MyAdapter());
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
                        Toast.makeText(GridViewActivity.this,"刷新 成功",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(GridViewActivity.this,"加载更多成功",Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 148;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(GridViewActivity.this);
            textView.setText("SuperEasyRefreshLayout");
            textView.setPadding(30,30,30,30);
            textView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            return textView;
        }
    }
}
