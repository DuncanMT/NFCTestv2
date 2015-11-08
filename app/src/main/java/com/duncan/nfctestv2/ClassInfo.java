package com.duncan.nfctestv2;

/**
 * Created by Duncan on 01/11/2015.
 */
public class ClassInfo {
    //private variables
    int _id;
    String _name;
    String _room;
    String _time;
    String _date;

    public ClassInfo(){

    }

    public ClassInfo(int id, String name, String _room, String _time, String _date){
        this._id = id;
        this._name = name;
        this._room = _room;
        this._time = _time;
        this._date = _date;
    }

    public ClassInfo(String name, String _room, String _time, String _date){
        this._name = name;
        this._room = _room;
        this._time = _time;
        this._date = _date;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public String get_room() {
        return _room;
    }

    public void set_room(String _room) {
        this._room = _room;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }
}
