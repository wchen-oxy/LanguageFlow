package com.example.myapplication.BroadcastRecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.DataDownload.DictionaryClientUsage;

public class myServerStatusReciever extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Successfully Connected to Server!", Toast.LENGTH_SHORT).show();
    }
}
