package com.duncan.nfctestv2;

/**
 * Created by Duncan on 01/11/2015.
 */
public class Student {
    //private variables
    int _id;
    String _name;
    String _matric_number;
    String _card_uid;
    String _module;

    public Student(){

    }

    public Student(int id, String name, String _matric_number, String _card_uid, String _module){
        this._id = id;
        this._name = name;
        this._matric_number = _matric_number;
        this._card_uid = _card_uid;
        this._module = _module;
    }

    public Student(String name, String _matric_number, String _card_uid, String _module){
        this._name = name;
        this._matric_number = _matric_number;
        this._card_uid = _card_uid;
        this._module = _module;
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

    public String get_matric_number() {
        return _matric_number;
    }

    public void set_matric_number(String _matric_number) {
        this._matric_number = _matric_number;
    }

    public String get_card_uid() {
        return _card_uid;
    }

    public void set_card_uid(String _card_uid) {
        this._card_uid = _card_uid;
    }

    public String get_module() {
        return _module;
    }

    public void set_module(String _module) {
        this._module = _module;
    }
}
