package com.github.goldberg.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.goldberg.customview.montage.MontageView;

public class MainActivity extends AppCompatActivity {


    String[] strings = new String[]{
            "1", "2", "3", "for Android", "trump is funny"
    };
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
      //  final MaterialProgressView materialProgressView = (MaterialProgressView) findViewById(R.id.materialProgressView);
//        initViews();
    }

    private void initViews() {
        final MontageView mView = (MontageView) findViewById(R.id.mTextView);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.setText(strings[i++ % strings.length]);
                mView.postDelayed(this, 3000);
            }
        }, 200);


    }
}
