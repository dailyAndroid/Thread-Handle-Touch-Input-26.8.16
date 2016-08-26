package com.example.hwhong.threadhandler;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private ImageView imageView;
    private TextView textView;

    private Button button;

    private float x,y = 0.0f;
    private boolean moving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnTouchListener(this);


        button = (Button) findViewById(R.id.run);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //never set the UI change in this thread, may cause crash
                        long time = System.currentTimeMillis() + 10000;
                        while(System.currentTimeMillis() < time) {
                            synchronized (this) {
                                try {
                                    wait(time - System.currentTimeMillis());
                                } catch (Exception e) {}
                            }
                        }
                        handler.sendEmptyMessage(0);
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            textView.setText("Thread Finished Running, and UI did'nt froze");
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                moving = true;

                break;
            case MotionEvent.ACTION_MOVE:
                if(moving) {
                    x = motionEvent.getRawX() - imageView.getWidth()/2;
                    y = motionEvent.getRawY() - imageView.getHeight()*3/2;
                    imageView.setX(x);
                    imageView.setY(y);
                }

                break;
            case MotionEvent.ACTION_UP:
                moving = false;

                break;
        }

        return true;
    }
}
