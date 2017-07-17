package com.honor.fighting.supereasyrefreshlayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_list_view).setOnClickListener(this);
        findViewById(R.id.btn_recycler_view).setOnClickListener(this);
        findViewById(R.id.btn_scroll_view).setOnClickListener(this);
        findViewById(R.id.btn_grid_view).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_list_view:
                startActivity(new Intent(this,ListViewActivity.class));
                break;
            case R.id.btn_recycler_view:
                startActivity(new Intent(this,RecyclerViewActivity.class));
                break;
            case R.id.btn_scroll_view:
                startActivity(new Intent(this,ScrollViewActivity.class));
                break;
            case R.id.btn_grid_view:
                startActivity(new Intent(this,GridViewActivity.class));
                break;
        }
    }
}
