package com.mid.list;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RVListAdapter extends RecyclerView.Adapter<RVListAdapter.ToDoListViewHolder> {
    List<Info> InfoArrayList = new ArrayList<Info>();
    Context context;
    final static String EMPTY_STRNG = "";

    public RVListAdapter(String details) {
        Info Info = new Info();
        Info.setToDoTaskDetails(details);
        InfoArrayList.add(Info);
    }

    public RVListAdapter(ArrayList<Info> InfoArrayList, Context context) {
        this.InfoArrayList = InfoArrayList;
        this.context = context;
    }

    @Override
    public ToDoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cardlayout, parent, false);
        ToDoListViewHolder toDoListViewHolder = new ToDoListViewHolder(view, context);
        return toDoListViewHolder;
    }

    @Override
    public void onBindViewHolder(ToDoListViewHolder holder, final int position) {
        final Info td = InfoArrayList.get(position);
        holder.todoDetails.setText(td.getToDoTaskDetails());
        if (td.getToDoDate() != null) {
            holder.todoNotes.setText(td.getToDoDate());
        }
        else
        {
            holder.todoNotes.setText("");
        }
        String tdStatus = td.getToDoTaskStatus();
        if (tdStatus != null && tdStatus.matches("Complete")) {
            holder.todoDetails.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else
        {
            holder.todoDetails.setPaintFlags(holder.todoDetails.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }
        String type = td.getToDoTaskPrority();
        if (type.contains("High")) {
            holder.todoPriority.setText("HIGH");
            holder.todoPriority.setTextColor(Color.RED);
        }
        else if (type.contains("Normal"))
        {
            holder.todoPriority.setText("NORMAL");
            holder.todoPriority.setTextColor(Color.GREEN);
        }
        else
        {
            holder.todoPriority.setText("LOW");
            holder.todoPriority.setTextColor(Color.LTGRAY);
        }

    }


    @Override
    public int getItemCount() {
        return InfoArrayList.size();
    }

    public class ToDoListViewHolder extends RecyclerView.ViewHolder {
        TextView todoDetails, todoNotes, todoPriority;
        ImageButton proprityColor;
        ImageView edit, deleteButton;
        Info Info;

        public ToDoListViewHolder(View view, final Context context) {
            super(view);
            final Context mContext = context;
            todoDetails = (TextView) view.findViewById(R.id.toDoTextDetails);
            todoNotes = (TextView) view.findViewById(R.id.toDoTextNotes);
            todoPriority = (TextView) view.findViewById(R.id.toDoTextPriority);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    final Info td = InfoArrayList.get(position);
                    final Dialog dialog = new Dialog(view.getContext(), android.R.style.Theme_Material_Light_NoActionBar_TranslucentDecor );
                    dialog.setContentView(R.layout.custom_dailog);
                    dialog.show();
                    EditText todoText = (EditText) dialog.findViewById(R.id.input_task_desc);
                    EditText todoNote = (EditText) dialog.findViewById(R.id.input_task_notes);
                    CheckBox cb = (CheckBox) dialog.findViewById(R.id.checkbox);
                    RadioButton rbHigh = (RadioButton) dialog.findViewById(R.id.high);
                    RadioButton rbNormal = (RadioButton) dialog.findViewById(R.id.normal);
                    RadioButton rbLow = (RadioButton) dialog.findViewById(R.id.low);
                    LinearLayout lv = (LinearLayout) dialog.findViewById(R.id.todoReminderAndDateContainerLayout);
                    lv.setVisibility(View.GONE);
                    if (td.getToDoTaskPrority().matches("Normal")) {
                        rbNormal.setChecked(true);
                    } else if (td.getToDoTaskPrority().matches("Low")) {
                        rbLow.setChecked(true);
                    } else {
                        rbHigh.setChecked(true);
                    }
                    if (td.getToDoTaskStatus() != null && td.getToDoTaskStatus().matches("Complete")) {
                        cb.setChecked(true);
                    }
                    todoText.setText(td.getToDoTaskDetails());
                    todoNote.setText(td.getToDoNotes());
                    Button save = (Button) dialog.findViewById(R.id.btn_save);
                    Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                    Button delete = (Button) dialog.findViewById(R.id.btn_delete);

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id = td.getToDoID();
                            SQLiteHelper mysqlite = new SQLiteHelper(view.getContext());
                            Cursor b = mysqlite.deleteTask(id);
                            if (b.getCount() == 0) {
                                Toast.makeText(view.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Code here will run in UI thread
                                        InfoArrayList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,InfoArrayList.size());
                                        notifyDataSetChanged();
                                    }
                                });
                                dialog.hide();
                            } else {
                                Toast.makeText(view.getContext(), "Deleted else", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
                            EditText todoNote = (EditText) dialog.findViewById(R.id.input_task_notes);
                            CheckBox cb = (CheckBox) dialog.findViewById(R.id.checkbox);
                         //   TextView todoDate = (TextView) dialog.findViewById(newToDoDateTimeReminderTextView);
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
                                Info updateTd = new Info();
                                updateTd.setToDoID(td.getToDoID());
                                updateTd.setToDoTaskDetails(todoText.getText().toString());
                                updateTd.setToDoTaskPrority(RadioSelection);
                                updateTd.setToDoNotes(todoNote.getText().toString());
                                updateTd.setToDoDate(EMPTY_STRNG);
                                if (cb.isChecked()) {
                                    updateTd.setToDoTaskStatus("Complete");
                                } else {
                                    updateTd.setToDoTaskStatus("Incomplete");
                                }
                                SQLiteHelper mysqlite = new SQLiteHelper(view.getContext());
                                Cursor b = mysqlite.updateTask(updateTd);
                                InfoArrayList.set(position, updateTd);
                                if (b.getCount() == 0) {
                                    //Toast.makeText(view.getContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show();
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Code here will run in UI thread
                                            notifyDataSetChanged();
                                        }
                                    });
                                    dialog.hide();
                                } else {


                                    dialog.hide();

                                }

                            } else {
                                Toast.makeText(view.getContext(), "Please enter To Do Task", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}

