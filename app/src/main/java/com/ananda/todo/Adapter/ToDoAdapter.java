package com.ananda.todo.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.ananda.todo.AddNewTask;
import com.ananda.todo.MainActivity;
import com.ananda.todo.Model.ToDoModel;
import com.ananda.todo.R;
import com.ananda.todo.Utils.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private List<ToDoModel> todoList;
    private MainActivity activity;
    private DatabaseHandler db;

    public ToDoAdapter(DatabaseHandler db ,MainActivity activity){
        this.db= db;
        this.activity = activity;
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType ){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);

    }
    public void onBindViewHolder(ViewHolder holder, int position){
        db.openDatabase();
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));


        String strDate = item.getDate();
        String strMonth=item.getDate().replace(" ","");
        StringBuilder sb=new StringBuilder(strMonth);
        sb.reverse();

        if(!strDate.equals("")){
            strDate = strDate.substring(0,2);
            if(Integer.parseInt(strDate.replace(" ","")) < 10){
                strDate = "0"+strDate;
            }
            strMonth = sb.substring(4,7);
            StringBuilder sb1 =new StringBuilder(strMonth);
            sb1.reverse();
            strMonth = sb1.toString();
        }else{
            strDate = "No";
            strMonth = "Date";
        }
        String strTime = item.getTime();
        if(strTime.equals("")){
            strTime = "No Time Selected!";
        }
        holder.date.setText(strDate);
        holder.month.setText(strMonth);
        holder.time.setText(strTime);

        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.updateStatus(item.getId(),1);
                }
                else{
                    db.updateStatus(item.getId(),0);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    private boolean toBoolean(int n){
        return n!=0;
    }

    public void setTask(List<ToDoModel> todoList){
        this.todoList = todoList;
        notifyDataSetChanged();
    }
    public Context getContext(){
        return activity;
    }
    public void deleteItem(int position){
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }
    public void editItem(int position){
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id",item.getId());
        bundle.putString("task",item.getTask());
        bundle.putString("date",item.getDate());
        bundle.putString("time",item.getTime());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(),AddNewTask.TAG);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        TextView date;
        TextView time;
        TextView month;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheck);
            date = (TextView) view.findViewById(R.id.todoDate);
            time = (TextView) view.findViewById(R.id.todoTime);
            month = (TextView) view.findViewById(R.id.todoMonth);

        }

    }


}
