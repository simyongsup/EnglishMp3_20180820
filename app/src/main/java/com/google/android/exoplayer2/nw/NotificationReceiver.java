package com.google.android.exoplayer2.nw;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.exoplayer2.english_mp3.BleActivity;
import com.google.android.exoplayer2.english_mp3.MainActivity;

import java.util.List;


public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int noti_id = intent.getIntExtra("notiID", 0);

        String action = intent.getAction();
        //String cid = intent.getStringExtra("cid");
        String param = "";

        Log.d("MANAGER_LOG", "RCV action =" + action + " noti_id =" + noti_id);

        if (noti_id == 111 && action.equals("DELETE")) {
            NotificationManager notifiyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifiyMgr.cancelAll();
            closeBar(context);

            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(20);
            if (!tasks.isEmpty()) {
                int tasksSize = tasks.size();
                for (int i = 0; i < tasksSize; ++i) {
                    ActivityManager.RunningTaskInfo taskinfo = tasks.get(i);
                    if (taskinfo.topActivity.getPackageName().equals("com.google.android.exoplayer2.english_mp3")) {
                        am.moveTaskToFront(taskinfo.id, 0);
                    }
                }
            }
        }else   if (noti_id == 111 && action.equals("CLOSED")) {
            NotificationManager notifiyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifiyMgr.cancelAll();
            closeBar(context);

        }
    }

    private void closeBar(Context context) {

        Intent i = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(i);
    }


}
