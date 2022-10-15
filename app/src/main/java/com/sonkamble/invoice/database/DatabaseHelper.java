package com.sonkamble.invoice.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="menucard.db";
    private static final String TABLE_MENUMAST="menumast";
    private static final String TABLE_ITEMMAST="itemmast";
    private static final String TABLE_DOCNO="docno";
    private static final String TABLE_SALEITEM="saleitem";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
       // SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_MENUMAST+ "(MENU_CODE INTEGER PRIMARY KEY AUTOINCREMENT,MENU_DESC TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_ITEMMAST+ "(ITEM_CODE INTEGER PRIMARY KEY AUTOINCREMENT,MENU_CODE INTEGER,ITEM_DESC TEXT,ITEM_RATE INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_DOCNO+ "(DOC_NO INTEGER PRIMARY KEY AUTOINCREMENT,DOC_SRNO INTEGER DEFAULT 0,DOC_SLNO INTEGER DEFAULT 0)");
        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_SALEITEM+ "(SALE_CODE INTEGER PRIMARY KEY AUTOINCREMENT,DOC_DT TEXT,ITEM_CODE INTEGER,ITEM_RATE INTEGER,QTY INTEGER,ITEM_VALUE  INTEGER, DOC_SRNO INTEGER,BILL_NO INTEGER DEFAULT 0)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +TABLE_MENUMAST);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +TABLE_ITEMMAST);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +TABLE_DOCNO);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +TABLE_SALEITEM);
            onCreate(sqLiteDatabase);
    }

    //===============Add Menu Data=======================
    public  boolean insert_MenuData(String desc)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("MENU_DESC",desc);
        Long result = sqLiteDatabase.insert(TABLE_MENUMAST,null,contentValues);
        if (result==-1)
            return  false;
        else
            return true;
    }
    //===============Add Item Data=======================
    public  boolean insert_ItemData(int id ,String desc,int rate)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("MENU_CODE",id);
        contentValues.put("ITEM_DESC",desc);
        contentValues.put("ITEM_RATE",rate);
        Long result = sqLiteDatabase.insert(TABLE_ITEMMAST,null,contentValues);
        if (result==-1)
            return  false;
        else
            return true;
    }
    public  boolean insert_DocData(int srno, int slno)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DOC_SRNO",srno);
        contentValues.put("DOC_SLNO",slno);
        Long result = sqLiteDatabase.insert(TABLE_DOCNO,null,contentValues);
        if (result==-1)
            return  false;
        else
            return true;
    }
    //===============Add Sale Data=======================
    public  boolean insert_SaleData(String dt ,String desc,String rate,String qty,String val,int srno,int billno)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DOC_DT",dt);
        contentValues.put("ITEM_CODE",desc);
        contentValues.put("ITEM_RATE",rate);
        contentValues.put("QTY",qty);
        contentValues.put("ITEM_VALUE",val);
        contentValues.put("DOC_SRNO",srno);
        contentValues.put("BILL_NO",billno);
        Long result = sqLiteDatabase.insert(TABLE_SALEITEM,null,contentValues);
        if (result==-1)
            return  false;
        else
            return true;
    }
    public Cursor show_MenuData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MENUMAST;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor show_BillData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT DISTINCT  BILL_NO ,DOC_DT FROM "+TABLE_SALEITEM+" where  BILL_NO !=0 ORDER BY BILL_NO DESC " ;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor show_PrintData(int billno) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "select DOC_SRNO, BILL_NO,ITEM_DESC,SALEITEM.ITEM_RATE,QTY,ITEM_VALUE,DOC_DT from "+TABLE_ITEMMAST+","+TABLE_SALEITEM+" WHERE SALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND BILL_NO="+billno+"";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor show_ExcelData(int billno) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "select SALE_CODE ,SALEITEM.ITEM_CODE ,ITEM_DESC,SALEITEM.ITEM_RATE ,QTY ,ITEM_VALUE , DOC_SRNO,DOC_DT ,BILL_NO  from "+TABLE_ITEMMAST+","+TABLE_SALEITEM+" WHERE SALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND BILL_NO="+billno+"";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }

    public Cursor show_ItemData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ITEMMAST;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor show_Docno() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT DOC_SRNO,DOC_SLNO FROM " + TABLE_DOCNO;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    //==========Delete data where===========================
    public void delete_MenuData(String menu_code)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
       sqLiteDatabase.delete(TABLE_MENUMAST, " MENU_CODE = " +menu_code+ " ",null);
    }

    public void delete_SaleData(String doc_srno)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_SALEITEM, " DOC_SRNO = " +doc_srno+ " ",null);
    }
    public void delete_ItemData(String item_code)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_ITEMMAST, " ITEM_CODE = " +item_code+ " ",null);
    }
    //========Update data============================
    public void update_Doc_srno(int srno)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("DOC_SRNO",srno);
        sqLiteDatabase.update(TABLE_DOCNO,contentValues, null,null);
    }
    public void update_Sale_Doc_srno(String srno,String qty,String val)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("QTY",qty);
        contentValues.put("ITEM_VALUE",val);
        sqLiteDatabase.update(TABLE_SALEITEM,contentValues, " DOC_SRNO = " +srno+ " ",null);
    }
    public void update_Doc_slno(int slno)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("DOC_SLNO",slno);
        sqLiteDatabase.update(TABLE_DOCNO,contentValues, null,null);
    }
    public void update_MenuData(Integer id,String desc)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("MENU_CODE",id);
        contentValues.put("MENU_DESC",desc);
        sqLiteDatabase.update(TABLE_MENUMAST,contentValues, " MENU_CODE = " +id+ " ",null);
    }
    public void update_BillNo(Integer bilno)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("BILL_NO",bilno);
        sqLiteDatabase.update(TABLE_SALEITEM,contentValues, " BILL_NO=0",null);
    }
    public void update_ItemData(Integer id,String desc,String rate)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("ITEM_CODE",id);
        contentValues.put("ITEM_DESC",desc);
        contentValues.put("ITEM_RATE",rate);
        sqLiteDatabase.update(TABLE_ITEMMAST,contentValues, " ITEM_CODE = " +id+ " ",null);
    }

    //==============Select Menu data Like Query============================
    public Cursor get_menu_search_Data(String searchText)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();

        String sql = "SELECT ITEM_CODE,MENUMAST.MENU_CODE,ITEM_DESC,ITEM_RATE,MENU_DESC FROM "+TABLE_MENUMAST+","+TABLE_ITEMMAST+" WHERE MENUMAST.MENU_CODE=ITEMMAST.MENU_CODE AND   ITEM_DESC " + " LIKE '%" + searchText + "%'";
       // String sql = "SELECT * FROM " +TABLE_COLLECTION_HISTORY + " WHERE NAME" + " LIKE '%" + searchText + "%'";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);

        return cursor;
    }
    //-----------------------------------------------------
    //==========Delete data all===========================
    public void delete_All_Data()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        // sqLiteDatabase.delete(TABLE_MENUMAST, " MOBILE_NUMBER = " +mobile_number+ " ",null);
        sqLiteDatabase.delete(TABLE_MENUMAST, null,null);

    }
    public Cursor show_cat_exp_Data(String cat) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //  String query =("SELECT * FROM " +TABLE_NAME+ );
    //    String query ="SELECT * FROM " +TABLE_EXP+ " WHERE CAT = " +cat+ " ";
        String sql = "SELECT * FROM " +TABLE_MENUMAST + " WHERE CAT" + " LIKE '%" + cat + "%'";
        //Cursor cursor = sqLiteDatabase.rawQuery(query,null);
      //  String query ="SELECT * FROM " +TABLE_DLVR+ " WHERE ID = " +cid+ " ";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);

        return cursor;

    }
    public Cursor show_date_exp_Data(String date) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //  String query =("SELECT * FROM " +TABLE_NAME+ );
      //  String query ="SELECT * FROM " +TABLE_EXP+ " WHERE DATE = " +date+ " ";
        String sql = "SELECT * FROM " +TABLE_MENUMAST + " WHERE DATE" + " LIKE '%" + date + "%'";
        //Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        //  String query ="SELECT * FROM " +TABLE_DLVR+ " WHERE ID = " +cid+ " ";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);

        return cursor;

    }
    public Cursor show_date_exp_Data_Wise(String date,String date2) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //  String query =("SELECT * FROM " +TABLE_NAME+ );
        //  String query ="SELECT * FROM " +TABLE_EXP+ " WHERE DATE = " +date+ " ";
        //String sql = "SELECT * FROM " +TABLE_EXP + " WHERE DATE" + " BETWEEN "+ date + "' AND '" + date2 "+
       // String sql = "select * from "+ TABLE_EXP + " where DATE BETWEEN " + date + "+ AND +" + date2 + "+ ORDER BY DATE ASC";
        String sql = "select * from "+ TABLE_MENUMAST + " where DATE BETWEEN " + "LIKE '%" + date + "%' + "+ "AND" +" LIKE '%" + date2 + "%'";
        //Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        //  String query ="SELECT * FROM " +TABLE_DLVR+ " WHERE ID = " +cid+ " ";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);

        return cursor;

    }

    }
