package com.m2comm.ktas.module;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.m2comm.ktas.MainActivity;
import com.m2comm.ktas.TestActivity;


public class CustomHandler extends Handler {

    public static final int MAIN_CHANGE = 0;
    public static final int FINISH_QUESTION = 1;
    public static final int TEST_MAIN_CHANGE = 2;
    public static final int TEST_FINISH_QUESTION = 3;


    private Context c;

    public CustomHandler(Context c) {
        this.c = c;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {

            case MAIN_CHANGE:
                ((MainActivity)this.c).timeTextChanger();
                break;

            case TEST_MAIN_CHANGE:
                ((TestActivity)this.c).timeTextChanger();
                break;

            case TEST_FINISH_QUESTION:
                ((TestActivity)this.c).nextView(false);
                break;

            case FINISH_QUESTION:
                ((MainActivity)this.c).nextView();
                break;



        }
    }

}
