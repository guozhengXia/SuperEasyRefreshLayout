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
import android.widget.TextView;

import com.honor.fighting.supereasyrefreshlayout.R;


/**
 *
 * 加载更多的view，这个view的样式可以任意修改。
 */
public class SuperEasyRefreshFootView extends LinearLayout {

    public int footViewHeight;
    public  TextView textView;

    SuperEasyRefreshFootView(Context context) {
        super(context);
        View view = View.inflate(getContext(), R.layout.view_super_easy_refresh_foot, null);
        textView = (TextView) view.findViewById(R.id.super_easy_refresh_text_view);
        addView(view);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        footViewHeight = (int) (50 * metrics.density);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,MeasureSpec.makeMeasureSpec(footViewHeight, MeasureSpec.EXACTLY));
    }

}
