package com.github.goldberg.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    String[] strings = new String[]{
            "1", "2", "3", "for Android", "和平"
    };
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        final MView mView = (MView) findViewById(R.id.mTextView);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.setText(strings[i++ % strings.length]);
                mView.postDelayed(this, 2000);
            }
        }, 2000);


    }
}
