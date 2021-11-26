package com.ananda.todo;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.ananda.todo.Model.ToDoModel;
import com.ananda.todo.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private TextView newTaskDate;
    private TextView newTaskTime;
    private Button newTaskSaveButton;
    private DatabaseHandler db;
    private MaterialTimePicker picker;
    private MaterialDatePicker datePicker;

    private Calendar calendar;
    private AlarmManager alarmManager;
    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL,R.style.DialogStyle);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.next_task,container,false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        newTaskText = getView().findViewById(R.id.newTaskTest);
        newTaskDate = getView().findViewById(R.id.newTaskDate);
        newTaskTime = getView().findViewById(R.id.newTaskTime);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle !=null){
            isUpdate = true;
            String task = bundle.getString("task");
            String date = bundle.getString("date");
            String time = bundle.getString("time");
            newTaskText.setText(task);
            newTaskDate.setText(date);
            newTaskTime.setText(time);
            if(task.length()>0){
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
            }
        }
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setBackgroundColor(Color.GRAY);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                }
                else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.light_blue));
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        newTaskDate.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
//                if(s.toString().equals("")){
//                    newTaskSaveButton.setEnabled(false);
//                    newTaskSaveButton.setBackgroundColor(Color.GRAY);
//                }
//                else{
//                    newTaskSaveButton.setEnabled(true);
//                    newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.light_blue));
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
        newTaskTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showTimePicker();
            }

        });

        newTaskDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showDatePicker();
            }

        });


//        newTaskTime.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
//                if(s.toString().equals("")){
//                    newTaskSaveButton.setEnabled(false);
//                    newTaskSaveButton.setBackgroundColor(Color.GRAY);
//                }
//                else{
//                    newTaskSaveButton.setEnabled(true);
//                    newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.light_blue));
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });


        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                System.out.println("it works");
                String text = newTaskText.getText().toString();
                String date = newTaskDate.getText().toString();
                String time = newTaskTime.getText().toString();
                if(finalIsUpdate){
                    db.updateTask(bundle.getInt("id"), text,date,time);

                }
                else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    task.setDate(date);
                    task.setTime(time);
                    db.insertTask(task);

                }
                dismiss();
            }
        });

    }
    @Override
    public void onDismiss(DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener){
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }

    private void showTimePicker() {
        picker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select Appointment time")
                        .build();
        picker.show(getFragmentManager(), "Ananda");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecimalFormat format = new DecimalFormat("00");
                if (picker.getHour() > 12) {
                    newTaskTime.setText(
                            (format.format(picker.getHour() - 12) + " : " + format.format(picker.getMinute()) + " PM").toString());
                } else {
                    newTaskTime.setText(
                            (format.format(picker.getHour()) + " : " + format.format(picker.getMinute()) + " AM").toString());
                }
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getHour());
                calendar.set(Calendar.SECOND, 0);

            }
        });
//        picker.addOnNegativeButtonClickListener {
//            // call back code
//        }
//        picker.addOnCancelListener {
//            // call back code
//        }
//        picker.addOnDismissListener {
//            // call back code
//        }
    }

    private void showDatePicker(){
        datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .build();
        datePicker.show(getFragmentManager(), "Ananda");
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                newTaskDate.setText(datePicker.getHeaderText());
            }
        });


    }

}


