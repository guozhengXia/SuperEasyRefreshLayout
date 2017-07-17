# SuperEasyRefreshLayout

SuperEasyRefreshLayout是一个功能强大且易与使用的下拉刷新控件，参照Google提供的SwipeRefreshLayout的原理实现。SuperEasyRefreshLayout本质是一个容器，将可以滑动的View放入容器中即可实现下拉刷新和上拉加载更多的功能。支持的子view有：ListView，RecyclerView，GridView，ScrollView。

## 框架诞生的机缘

在Android开发中，有很多场合需要使用下拉刷新和上拉加载更多的功能，实现这个功能有两种使用广泛的途径，一种是使用网上开源的PulltoRefreshListView框架，另外一种使用Google的提供的SwipeRefreshLayout。但是在使用的过程中二者都有缺点。

PulltoRefreshListView的缺点：PulltoRefreshListView底层封装的是Listview，所以此时只能使用Listview的功能，而不能使用强大的RecyclerView。

SwipeRefreshLayout的缺点：SwipeRefreshLayout是Google提供，其实现思想很好，刷新View与展示内容的View实现了分离。但是其刷新UI却不敢让人恭维，入不了公司设计师的法眼。

综合以上两点，开始着手实现样式即美观，又能将刷新的View与展示内容的View分离，既能使用Listview又能使用RecyclerView。SuperEasyRefreshLayout就在这种机缘下应运而生。

## 使用方法

代码很简单，只有三个Java文件，建议下载后将Java文件拷贝到工程中使用。下拉刷新view和加载更多view单独抽取成一个类，使用者可以方便修改刷新样式。

## 使用示例

SuperEasyRefreshLayout本质是一个容器，将可以滑动的View放入容器中即可实现下拉刷新和上拉加载更多的功能。支持的view有：ListView，RecyclerView，GridView，ScrollView。下面以ListView为例说明使用方法。

#### 1，布局文件中

```
<com.honor.fighting.supereasyrefreshlayout.view.SuperEasyRefreshLayout
    android:id="@+id/swipe_refresh_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <ListView
        android:id="@+id/list_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</com.honor.fighting.supereasyrefreshlayout.view.SuperEasyRefreshLayout>
```

#### 2，在activity中

首先找到SuperEasyRefreshLayout对象：

```
swipe_refresh_layout = (SuperEasyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
```

然后设置刷新监听器：

```
swipe_refresh_layout.setOnRefreshListener(new SuperEasyRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipe_refresh_layout.setRefreshing(false);
                Toast.makeText(ListViewActivity.this,"刷新 成功",Toast.LENGTH_SHORT).show();
            }
        },2000);
    }
});
```

设置加载更多监听器：

```
swipe_refresh_layout.setOnLoadMoreListener(new SuperEasyRefreshLayout.OnLoadMoreListener() {
    @Override
    public void onLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipe_refresh_layout.finishLoadMore();
                Toast.makeText(ListViewActivity.this,"加载更多成功",Toast.LENGTH_SHORT).show();
            }
        },2000);
    }
});
```

## 显示效果如下

注：以下的刷新UI和加载更多的UI仅作为示例用。下拉刷新view和加载更多view已经单独抽取成一个类，使用者可以自行修改刷新样式。

#### 1，ListView和RecyclerView下拉刷新效果

```html
<img src="https://github.com/guozhengXia/SuperEasyRefreshLayout/blob/master/sample01.png" style="zoom:50%" />
```

#### 2，ListView和RecyclerView上拉加载更多效果

```html
<img src="https://github.com/guozhengXia/SuperEasyRefreshLayout/blob/master/sample02.png" style="zoom:50%" />
```

#### 1，ScrollView下拉刷新效果

```html
<img src="https://github.com/guozhengXia/SuperEasyRefreshLayout/blob/master/sample03.png" style="zoom:50%" />
```

#### 1，GridView下拉刷新效果

```html
<img src="https://github.com/guozhengXia/SuperEasyRefreshLayout/blob/master/sample04.png" style="zoom:50%" />
```









