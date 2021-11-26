package com.ananda.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ananda.todo.Adapter.ToDoAdapter;
import com.ananda.todo.Model.ToDoModel;
import com.ananda.todo.Utils.DatabaseHandler;
//import com.ananda.todo.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
//    private ActivityMainBinding binding;
//    private FloatingActionButton fab;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar;

    private List<ToDoModel> taskList;
    private DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        TextView aboutTextView = findViewById(R.id.tasksText);
        registerForContextMenu(aboutTextView);

        db = new DatabaseHandler(this);
        db.openDatabase();

        taskList = new ArrayList<>();
        tasksRecyclerView = findViewById(R.id.tasksRecyclerview);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tasksAdapter = new ToDoAdapter(db,this);
        tasksRecyclerView.setAdapter(tasksAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        taskList = db.getAllTask();
        Collections.reverse(taskList);
        tasksAdapter.setTask(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(),AddNewTask.TAG);
            }
        });

        ImageView aboutButton = (ImageView) findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCalender.newInstance().show(getSupportFragmentManager(),TaskCalender.TAG);
            }
        });


    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        System.out.println("it works");
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu,menu);

    }
    @Override
    public boolean onContextItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.about:
                TaskCalender.newInstance().show(getSupportFragmentManager(),TaskCalender.TAG);
                Toast.makeText(this,"about is called",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.exit:
                Toast.makeText(this,"Exit is called",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);

        }

    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTask();
        Collections.reverse(taskList);
        tasksAdapter.setTask(taskList);
        tasksAdapter.notifyDataSetChanged();

    }

//    ==============================================================================================
    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "Ananda";
            String desc = "Todo notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Ananda",name,importance);
            channel.setDescription(desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    
    public void setAlarm() throws ParseException {
        String myDate = "2021/07/04 16:21:00";
//creates a formatter that parses the date in the given format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = sdf.parse(myDate);
        long timeInMillis = date.getTime();

        calendar = Calendar.getInstance();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeInMillis,AlarmManager.INTERVAL_DAY,pendingIntent);
        Toast.makeText(this,"Alarm set successfully",Toast.LENGTH_SHORT);

    }
    public void cancelAlarm(){
        Intent intent = new Intent(this,AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        if(alarmManager==null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT);
    }
//    public static void addWatermark(){
//        ImageView watermark = findViewById(R.id.watermark);
//        watermark.setImageResource(R.drawable.todolistwhite);
//        Toast.makeText(this,"image added",Toast.LENGTH_SHORT);
//        final LayoutInflater factory = getLayoutInflater();
//
//        final View textEntryView = factory.inflate(R.layout.activity_main, null);
//
//        landmarkEditNameView = (ImageView) textEntryView.findViewById(R.id.watermark);
//    }
//    public void removeWatermark(){
//        ImageView watermark = findViewById(R.id.watermark);
//        watermark.setImageResource(0);
//        Toast.makeText(this,"image removed",Toast.LENGTH_SHORT);
//    }


}