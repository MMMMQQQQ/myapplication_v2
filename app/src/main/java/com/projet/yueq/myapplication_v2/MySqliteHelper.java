package com.projet.yueq.myapplication_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKitUser;

/**
 * Created by delll on 2017/4/4.
 */


public class MySqliteHelper extends SQLiteOpenHelper {
    //自定义访问sqlite
    private final static int version = 3;
    private final static String DB_NAME = "users.db";
    private final static String TABLE_NAME = "users";
    private final static String CREATE_TBL = "create table users(_id int primary key ,userId text,name text,pwd text,avatarUrl text)";
    //private final static String DB_FRI_NAME = "friends.db";
    private final static String TABLE_FRI_NAME = "friends";
    private final static String CREATE_FRI_TBL = "create table friends(_id int primary key ,userId text, friId text)";
    private SQLiteDatabase db;


    public MySqliteHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        db.execSQL(CREATE_TBL);
        db.execSQL(CREATE_FRI_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRI_NAME);

        // create new tables
        onCreate(db);
    }

    public void insert(LCChatKitUser user){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("insert into users(userId,name,pwd,avatarUrl) values ('"+user.getUserId()+"','"+user.getUserName()+"','"+user.getUserName()+"','"+user.getAvatarUrl()+"')");
        db.close();
    }


    public void insert(LCChatKitUser user,String pwd){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("insert into users(userId,name,pwd,avatarUrl) values ('"+user.getUserId()+"','"+user.getUserName()+"','"+pwd+"','"+user.getAvatarUrl()+"')");
        db.close();
    }

    public boolean insert(String userId, String friId){
        SQLiteDatabase db=getWritableDatabase();
        boolean f=true;
        Cursor cursor=query(TABLE_FRI_NAME);
        while(cursor.moveToNext()){
            if(cursor.getString(1).equals(userId) && cursor.getString(2).equals(friId)) {
                f=false;
                break;
            }
        }
        if(f){
            db.execSQL("insert into friends(userId,friId) values ('"+userId+"','"+friId+"')");
        }
        db.close();
        return f;
    }

    public Cursor query(String tablename){
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+ tablename,null);
        return cursor;
    }

    public boolean isEmpty(String tablename){
        boolean f=true;
        Cursor cursor=query(tablename);
        while(cursor.moveToNext()){
            if(cursor.isNull(0) && cursor.isNull(1))continue;
            else f=false;
        }
        return f;
    }

    public LCChatKitUser getUser(Cursor cursor){
        return new LCChatKitUser(cursor.getString(1),cursor.getString(2),cursor.getString(4));
    }


    public LCChatKitUser getUser(String id){
        Cursor cursor=query(TABLE_NAME);
        while(cursor.moveToNext()) {
            if (cursor.getString(1).equals(id)) {
                return new LCChatKitUser(cursor.getString(1),cursor.getString(2),cursor.getString(4));
            }
        }
        return null;
    }

    public List<LCChatKitUser> getAllFri(String userId){
        List<LCChatKitUser> frilist = new ArrayList<LCChatKitUser>();
        MySqliteHelper helper=CustomUserProvider.getInstance().getHelper();
        Cursor cursor=helper.query(TABLE_FRI_NAME);
        while(cursor.moveToNext()){
            if(cursor.getString(1).equals(userId)) {
                frilist.add(helper.getUser(cursor.getString(2)));
            }
        }
        return frilist;
    }

    public void delete(String id){
        SQLiteDatabase db=getWritableDatabase();
        //db.execSQL("DELETE FROM users");
        db.delete(TABLE_NAME,"userId=?",new String[]{id});
        db.delete(TABLE_FRI_NAME,"userId=?",new String[]{id});
        db.delete(TABLE_FRI_NAME,"friId=?",new String[]{id});
    }

    public void delete(String userId, String friId){
        SQLiteDatabase db=getWritableDatabase();
//        Cursor cursor=query(TABLE_FRI_NAME);
//        while(cursor.moveToNext()){
        db.execSQL( "DELETE FROM " + TABLE_FRI_NAME + " WHERE userId =" + "'"+ userId+ "'"+ " AND friId ="+ "'"+ friId+ "'");
        // friList.delete(TABLE_FRI_NAME,"userId=?",new String[]{String.valueOf(cursor.getPosition())});
//            if(cursor.getString(1).equals(userId) && cursor.getString(2).equals(friId)) {
//                friList.delete(TABLE_FRI_NAME,"_id=?",new String[]{String.valueOf(cursor.getPosition())});
//            }
        //    }
    }

    public void update(String tablename,ContentValues values,String whereClause,String[]whereArgs){
        SQLiteDatabase db=getWritableDatabase();
        db.update(tablename,values,whereClause,whereArgs);
    }

   /* public void integrate(){
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor1=query();
        Cursor cursor2=query();
        while(cursor1.moveToNext()){
            if(cursor1.getString(1).equals("abc")) {
                delete(cursor1.getInt(0));
                continue;
            }
           /* while (cursor2.moveToNext()){
                if(cursor1.getString(1).equals(cursor2.getString(1)))
                    delete(cursor2.getInt(0));
            }
        }
    }*/

    public void close(){
        if(db!=null){
            db.close();
        }
    }
}