package com.mid.list;

/**
 * Created by gkudva on 06/08/17.
 */

public class Info {
    private int ToDoID;
    private String ToDoTaskDetails;
    private String ToDoTaskPrority;
    private String ToDoTaskStatus;
    private String ToDoNotes;
    private String ToDoColor;

    static final String DEFAULT_COLOR = "-5319295";
    static final String DEFAULT_STATUS = "Incomplete";

    public void setToDoColor(String toDoColor) {
        ToDoColor = toDoColor;
    }

    public String getToDoColor() {

        return ToDoColor;
    }




    public void setToDoDate(String toDoDate) {
        ToDoDate = toDoDate;
    }

    public String getToDoDate() {

        return ToDoDate;
    }

    String ToDoDate;

    public int getToDoID() {
        return ToDoID;
    }

    public void setToDoID(int toDoID) {
        ToDoID = toDoID;
    }

    public String getToDoTaskDetails() {
        return ToDoTaskDetails;
    }

    public void setToDoTaskDetails(String toDoTaskDetails) {
        ToDoTaskDetails = toDoTaskDetails;
    }

    public String getToDoTaskPrority() {
        return ToDoTaskPrority;
    }

    public void setToDoTaskPrority(String toDoTaskPrority) {
        ToDoTaskPrority = toDoTaskPrority;
    }

    public String getToDoTaskStatus() {
        return ToDoTaskStatus;
    }

    public Info() {
        ToDoTaskStatus = DEFAULT_STATUS;
        ToDoColor = DEFAULT_COLOR;
    }

    public void setToDoTaskStatus(String toDoTaskStatus) {
        ToDoTaskStatus = toDoTaskStatus;
    }

    public String getToDoNotes() {
        return ToDoNotes;
    }

    public void setToDoNotes(String toDoNotes) {
        ToDoNotes = toDoNotes;
    }

    @Override
    public String toString() {
        return "Info {id-" + ToDoID + ", taskDetails-" + ToDoTaskDetails + ", propity-" + ToDoTaskPrority + ", status-" + ToDoTaskStatus + ", notes-" + ToDoNotes + "}";
    }

}
