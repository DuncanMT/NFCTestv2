package com.duncan.nfctestv2;

/**
 * Created by Duncan on 01/11/2015.
 */
public class StudentClassInfo {

    //private variables
    int _id;
    String _studentID;
    String _classID;


    public StudentClassInfo(){

    }

    public StudentClassInfo(int id, String _studentID, String _classID){
        this._id = id;
        this._studentID = _studentID;
        this._classID = _classID;
    }

    public StudentClassInfo(String _studentID, String _classID){
        this._studentID = _studentID;
        this._classID = _classID;
    }

    public int get_ID() {
        return _id;
    }

    public void set_ID(int _id) {
        this._id = _id;
    }

    public String get_studentID() {
        return _studentID;
    }

    public void set_studentID(String _studentID) {
        this._studentID = _studentID;
    }

    public String get_classID() {
        return _classID;
    }

    public void set_classID(String _classID) {
        this._classID = _classID;
    }
}
