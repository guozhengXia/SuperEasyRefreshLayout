/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.honor.fighting.supereasyrefreshlayout.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.honor.fighting.supereasyrefreshlayout.R;


/**
 *
 * 下拉刷新的头部view，这个view可以任意修改样式
 */
public class SuperEasyRefreshHeadView extends LinearLayout {

    private ProgressBar progressBar;
    public int headViewHeight;
    public  TextView textView;

    SuperEasyRefreshHeadView(Context context) {
        super(context);
        View view = View.inflate(getContext(), R.layout.view_super_easy_refresh_head, null);
        textView = (TextView) view.findViewById(R.id.super_easy_refresh_text_view);
        progressBar = (ProgressBar) view.findViewById(R.id.super_easy_refresh_head_progress_bar);
        addView(view);
        hideProgressBar();
        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        headViewHeight = (int) (40 * metrics.density);//注意高度的设置。
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,MeasureSpec.makeMeasureSpec(headViewHeight, MeasureSpec.EXACTLY));
    }

    /**
     * 设置刷新的文本
     * */
    public void setRefreshText(String text){
        textView.setText(text);
    }
    /**
     * 隐藏ProgressBar
     * */
    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    /**
     * 显示ProgressBar
     * */
    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }


}
