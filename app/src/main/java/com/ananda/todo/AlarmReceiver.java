package com.ananda.todo;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ananda.todo.Model.ToDoModel;
import com.ananda.todo.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    private DatabaseHandler db;
    private List<ToDoModel> taskList;
    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DatabaseHandler(context);
        db.openDatabase();
        taskList = new ArrayList<>();
        taskList = db.getAllTask();

        Intent i = new Intent(context,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Ananda")
                .setSmallIcon(R.drawable.ic_baseline_add)
                .setContentTitle("Todo")
                .setContentText(taskList.get(0).getTask())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());
    }
}
