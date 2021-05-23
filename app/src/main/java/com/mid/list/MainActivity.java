package com.mid.list;

import android.animation.Animator;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        DatePickerDialog.OnDateSetListener ,
        TimePickerDialog.OnTimeSetListener
{
    FloatingActionButton addTask;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Info> tdd = new ArrayList<>();
    SQLiteHelper mysqlite;
    SwipeRefreshLayout swipeRefreshLayout;
    int Year, Month, Day, Hour, Minute;
    ImageView empty_mage;
    TextView empty_text;
    Button btnDate;
    Button btnTime;
    LinearLayout mContainerLayout;
    LinearLayout mUserDateSpinnerContainingLinearLayout;
    SwitchCompat mToDoDateSwitch;
    TextView mReminderTextView;
    Date mUserReminderDate;
    boolean mUserHasReminder, timeSet;
    EditText mDateEditText;
    EditText mTimeEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         empty_mage = findViewById(R.id.empty_mage);
         empty_text = findViewById(R.id.empty_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_s);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        addTask = (FloatingActionButton) findViewById(R.id.imageButton);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        adapter = new RVListAdapter(tdd, getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.accent), getResources().getColor(R.color.divider));
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                updateCardView();
            }
        });
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Light_NoActionBar_TranslucentDecor);
                dialog.setContentView(R.layout.custom_dailog);
                dialog.show();

                mUserHasReminder = false;
                mUserReminderDate = null;
                mContainerLayout = (LinearLayout)dialog.findViewById(R.id.todoReminderAndDateContainerLayout);
                mUserDateSpinnerContainingLinearLayout = (LinearLayout)dialog.findViewById(R.id.toDoEnterDateLinearLayout);
                mToDoDateSwitch = (SwitchCompat)dialog.findViewById(R.id.toDoHasDateSwitchCompat);
                mReminderTextView = (TextView)dialog.findViewById(R.id.newTodoTimeEditText);
                Button save = (Button) dialog.findViewById(R.id.btn_save);
                Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                Button delete = (Button) dialog.findViewById(R.id.btn_delete);
                CheckBox cb = (CheckBox) dialog.findViewById(R.id.checkbox);
                TextView tvstatus = (TextView) dialog.findViewById(R.id.status);
                cb.setVisibility(View.GONE);
                tvstatus.setVisibility(View.GONE);

                mContainerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard(v);
                    }
                });

                if(mUserHasReminder && (mUserReminderDate!=null)){

                    setReminderTextView();
                    setEnterDateLayoutVisibleWithAnimations(true);
                }
                if(mUserReminderDate==null){
                    mToDoDateSwitch.setChecked(false);
                    mReminderTextView.setVisibility(View.INVISIBLE);
                }

                setEnterDateLayoutVisible(mToDoDateSwitch.isChecked());

                mToDoDateSwitch.setChecked(mUserHasReminder && (mUserReminderDate != null));
                mToDoDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (!isChecked) {
                            mUserReminderDate = null;
                        }
                        mUserHasReminder = isChecked;
                        setEnterDateLayoutVisibleWithAnimations(isChecked);
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(buttonView.getWindowToken(), 0);
                    }
                });

//                DateTimeButton();

                delete.setVisibility(View.GONE);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText todoText = (EditText) dialog.findViewById(R.id.input_task_desc);
                        EditText todoNotes = (EditText) dialog.findViewById(R.id.input_task_notes);
                        TextView todoDate = (TextView) dialog.findViewById(R.id.newTodoDateEditText);
                        if (todoText.getText().length() >= 2) {
                            RadioGroup proritySelection = (RadioGroup) dialog.findViewById(R.id.toDoRG);
                            String RadioSelection = new String();
                            if (proritySelection.getCheckedRadioButtonId() != -1) {
                                int id = proritySelection.getCheckedRadioButtonId();
                                View radiobutton = proritySelection.findViewById(id);
                                int radioId = proritySelection.indexOfChild(radiobutton);
                                RadioButton btn = (RadioButton) proritySelection.getChildAt(radioId);
                                RadioSelection = (String) btn.getText();
                            }

 //                           Log.d("Ganesh", todoDate.getText().toString().substring(17));
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("ToDoTaskDetails", todoText.getText().toString());
                            contentValues.put("ToDoTaskPrority", RadioSelection);
                            contentValues.put("ToDoTaskStatus", "Incomplete");
                            contentValues.put("ToDoNotes", todoNotes.getText().toString());
                            if (mUserHasReminder == true) {
                                contentValues.put("ToDoDate", todoDate.getText().toString().substring(17));
                            }
                            else
                            {
                                contentValues.put("ToDoDate", "");
                            }
                            int color = 0;
                            color = ColorGenerator.MATERIAL.getRandomColor();
                            contentValues.put("ToDoColor", Integer.toString(color));
                            mysqlite = new SQLiteHelper(getApplicationContext());
                            Boolean b = mysqlite.insertInto(contentValues);
                            if (b) {
                                dialog.hide();
                                updateCardView();
                            } else {
                                Toast.makeText(getApplicationContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter To Do Task", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });

    }

    public void updateCardView() {
        swipeRefreshLayout.setRefreshing(true);
        mysqlite = new SQLiteHelper(getApplicationContext());
        Cursor result = mysqlite.selectAllData();
        if (result.getCount() == 0) {
            tdd.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "No Tasks", Toast.LENGTH_SHORT).show();
            empty_mage.setVisibility(View.VISIBLE);
            empty_text.setVisibility(View.VISIBLE);

        } else {
            tdd.clear();
            adapter.notifyDataSetChanged();
            empty_mage.setVisibility(View.GONE);
            empty_text.setVisibility(View.GONE);
            while (result.moveToNext()) {
                Info tddObj = new Info();
                tddObj.setToDoID(result.getInt(0));
                tddObj.setToDoTaskDetails(result.getString(1));
                tddObj.setToDoTaskPrority(result.getString(2));
                tddObj.setToDoTaskStatus(result.getString(3));
                tddObj.setToDoNotes(result.getString(4));
                tddObj.setToDoDate(result.getString(5));
                tddObj.setToDoColor(result.getString(6));
                tddObj.setToDoTaskStatus(result.getString(7));
                tdd.add(tddObj);
            }
            adapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        updateCardView();
    }

    public void setReminderTextView(){
        android.text.format.DateFormat df = new android.text.format.DateFormat();

        if(mUserReminderDate!=null){
            mReminderTextView.setVisibility(View.VISIBLE);
            if(mUserReminderDate.before(new Date())){
                mReminderTextView.setText(this.getString(R.string.date_error_check_again));
                mReminderTextView.setTextColor(Color.RED);
                return;
            }
            Date date = mUserReminderDate;
            String dateString = df.format("d MMM, yyyy", date).toString() ;
            String timeString;
            String amPmString = "";

            if(DateFormat.is24HourFormat(this)){
                timeString = df.format("k:mm", date).toString();
            }
            else{
                timeString = df.format("h:mm", date).toString();
                amPmString = df.format("a", date).toString();
                if (!timeSet) {
                    if (amPmString.contains("AM")) {
                        amPmString = "PM";
                    } else {
                        amPmString = "AM";
                    }
                }
            }
            String finalString = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPmString);
            mReminderTextView.setTextColor(getResources().getColor(R.color.secondary_text));
            mReminderTextView.setText(finalString);
        }
        else{
            mReminderTextView.setVisibility(View.INVISIBLE);

        }
    }

    public void setEnterDateLayoutVisibleWithAnimations(boolean checked){
        if(checked){
            setReminderTextView();
            mUserDateSpinnerContainingLinearLayout.animate().alpha(1.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }
            );
        }
        else{
            mUserDateSpinnerContainingLinearLayout.animate().alpha(0.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }
            );
        }

    }

    public void setEnterDateLayoutVisible(boolean checked){
        if(checked){
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
        }
        else{
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void hideKeyboard(View v)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void setDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        int hour, minute;


        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);

        if(reminderCalendar.before(calendar)){
            return;
        }

        if(mUserReminderDate!=null){
            calendar.setTime(mUserReminderDate);
        }

        if(DateFormat.is24HourFormat(this)){
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        }
        else{

            hour = calendar.get(Calendar.HOUR);
        }
        minute = calendar.get(Calendar.MINUTE);

        calendar.set(year, month, day, hour, minute);
        mUserReminderDate = calendar.getTime();
        timeSet = false;
        setReminderTextView();

        setDateEditText();
    }

    public void setTime(int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        if(mUserReminderDate!=null){
            calendar.setTime(mUserReminderDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, hour, minute, 0);
        mUserReminderDate = calendar.getTime();

        timeSet = true;
        setReminderTextView();

        setTimeEditText();
    }


    public void  setDateEditText(){
        String dtFormat = "yyyy-MM-dd hh:mm:ss aa";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dtFormat);
        dateFormat.setTimeZone(TimeZone.getDefault());
        String date = dateFormat.format(mUserReminderDate);
        mDateEditText.setText(date.substring(0, 10));
    }

    public void  setTimeEditText(){
        SimpleDateFormat dateFormat;
        String dtFormat;
        if(DateFormat.is24HourFormat(this)){
            dtFormat = "k:mm";
        }
        else{
            dtFormat = "h:mm a";

        }
        dateFormat = new SimpleDateFormat(dtFormat);
        dateFormat.setTimeZone(TimeZone.getDefault());
        String timeStr = dateFormat.format(mUserReminderDate);

        mTimeEditText.setText(timeStr);
    }



    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "Date: "+Day+"/"+(Month+1)+"/"+Year;

        Toast.makeText(MainActivity.this, date, Toast.LENGTH_LONG).show();

        TextView mDateEditText = (TextView)findViewById(R.id.newTodoDateEditText);
        mDateEditText.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = "Time: "+hourOfDay+"h"+minute+"m"+second;

        Toast.makeText(MainActivity.this, time, Toast.LENGTH_LONG).show();


        TextView mTimePicker = (TextView)findViewById(R.id.newTodoTimeEditText);
        mTimePicker.setText(time);
    }
public void DateTimeButton()
{
    btnDate = findViewById(R.id.btnTodoDate);
    btnTime = findViewById(R.id.btnTodoTime);
    mDateEditText = findViewById(R.id.newTodoDateEditText);
    mTimeEditText = findViewById(R.id.newTodoTimeEditText);

    btnDate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Date date;
            hideKeyboard(v);

            date = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    MainActivity.this, year, month, day);

            datePickerDialog.show(getFragmentManager(), "DateFragment");

        }
    });


    btnTime.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Date date;
            hideKeyboard(v);

            date = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(MainActivity.this, hour, minute, DateFormat.is24HourFormat(getApplicationContext()));

            timePickerDialog.show(getFragmentManager(), "TimeFragment");
        }
    });

}

}

