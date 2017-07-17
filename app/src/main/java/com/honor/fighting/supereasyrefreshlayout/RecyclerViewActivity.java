package com.honor.fighting.supereasyrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.honor.fighting.supereasyrefreshlayout.view.SuperEasyRefreshLayout;

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private SuperEasyRefreshLayout swipe_refresh_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        initRecycler();

        swipe_refresh_layout = (SuperEasyRefreshLayout) findViewById(R.id.swipe_refresh_layout);//找到刷新对象
        initListener();
    }

    private void initRecycler() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        swipe_refresh_layout.setOnRefreshListener(new SuperEasyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_refresh_layout.setRefreshing(false);
                        Toast.makeText(RecyclerViewActivity.this,"刷新 成功",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RecyclerViewActivity.this,"加载更多成功",Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(RecyclerViewActivity.this);
            textView.setText("  SuperEasyRefreshLayout                                       ");
            textView.setPadding(30,30,300,30);
            textView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            return new MyViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }



}
