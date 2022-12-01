package com.example.aarmodel;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.mojo.toolkit.base.BaseActivity;
import com.mojo.toolkit.base.BaseModel;
import com.mojo.toolkit.base.BasePresenter;
import com.mojo.toolkit.classes.Pop.PopSpinner;
import com.mojo.toolkit.datetime.DateRangePickDialog;
import com.mojo.toolkit.datetime.OnRangeSelectListener;
import com.mojo.toolkit.views.PopSpinnerView;
import com.mojo.toolkit.views.SwitchButton;

import java.util.Date;

public class MainActivity extends BaseActivity {
    String TAG = " MainActivity";
    PopSpinner<Integer> popSpinner;
    PopSpinnerView<Integer> popSpinnerView;

    @Override
    protected void initData() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        SwitchButton tb = findViewById(R.id.tb);
        tb.setOnStateChangeListener(on -> {
            Log.e(TAG, "initView: " + on);
        });
        tb.setState(true);
        /*tb.setState(true);
        tb.setState(false);
        tb.setState(false);
        tb.setState(true);*/
    }

    @Override
    public void initListener() {

    }

    @Override
    public BasePresenter<?, ?, ?> getPresenterInstance(Object v) {
        return null;
    }

    @Override
    public Object getContract() {
        return null;
    }

    @Override
    public BaseModel<?, ?> getModelInstance(BasePresenter basePresenter, Object o) {
        return null;
    }


    public void onClick(View view) {
        Log.e(TAG, "onClick: ");
        DateRangePickDialog dialog = DateRangePickDialog.build(this);
        dialog.setOnRangeSelectListener(new OnRangeSelectListener() {
            @Override
            public void onSelected(Date stDate, Date endDate) {
                Log.e(TAG, "stDate: " + stDate + "--endDate:" + endDate);
            }
        });
        dialog.show();
        /*Intent intent = new Intent("skip");  // 标志(要和目标APP的清单文件中的标志一样)
        intent.putExtra("name", "Liu xiang");
        intent.putExtra("birthday", "1983-7-13");
        startActivity(intent);*/
        // 通过包名获取要跳转的app，创建intent对象
       /* Intent intent = getPackageManager().getLaunchIntentForPackage("com.demo.skipdemo");
// 这里如果intent为空，就说名没有安装要跳转的应用嘛
        if (intent != null) {
            // 这里跟Activity传递参数一样的嘛，不要担心怎么传递参数，还有接收参数也是跟Activity和Activity传参数一样
            intent.putExtra("name", "Liu xiang");
            intent.putExtra("birthday", "1983-7-13");
            startActivity(intent);
        } else {
            // 没有安装要跳转的app应用，提醒一下
            Toast.makeText(getApplicationContext(), "哟，赶紧下载安装这个APP吧", Toast.LENGTH_LONG).show();
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            Log.e(TAG, "onActivityResult: " + data.getStringExtra("name"));
        }
    }
}