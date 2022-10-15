package com.sonkamble.invoice;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.sonkamble.invoice.database.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Table_Bill extends AppCompatActivity {
    //Spinner sp_fg;

    ProgressBar pbbar;
    Connection con;
    TextView txt_date,txt_save,txt_enquiry;
    PreparedStatement ps1;
    int m_loct_code=0;
    SearchView searchView;
    int mYear, mMonth, mDay;
    EditText txt_bar_code,txt_type,edt_paid_amt,edt_bal_amt,txt_sale_pr;
    String  str_item_code,str_type,SubCodeStr,str_sale_pr,str_qty,str_value;
    TransparentProgressDialog pd;
    ConnectionClass connectionClass;
    ArrayList<HashMap<String, String>> data_list;
    ArrayList<HashMap<String, String>> menu_card_arryList;
    ArrayList<HashMap<String, String>> print_arryList;
    static RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    static RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    EditText edit_qty,edit_value;
    int qty=0;
    String Temp_date,Query_date,str_bill_no="0";
    AlertDialog dialog;
    ImageView btn_cancel;
    Button btn_save,btn_operation_cancle,btn_pdf_export;
    int doc_no=0;
    int doc_slno=0;
    Double m_sgst=0.0;
    Double m_cgst=0.0;
    Double m_gst=0.0;
    Double m_ttl=0.0;
    int disc=0;
    Double gstp=0.00;
    Double ttl=0.00;
    double total=0.00;
    Double m_dallyrcp_doc_no=0.00;
    String formattedDate, str_month="",str_day,systemDate,cur_time;
    String str_id,item_name,item_rate,type,str_doc_srno;
    //================Recyclerview swap======================
    ArrayList<HashMap<String, String>> swap_arryList;
    private RecyclerView.LayoutManager layoutManager_swap;
    tswap_recyclerAdapter swap_recyclerAdapter;
    private RecyclerView recycler_swap_list;
    HashMap<String, String> smap;
    List<String> Cust_Name;
    List<String> Cust_Id;
    DecimalFormat formatter ;
    //------Bluetooth---------------------------
    // will show the statuses like bluetooth open, close or data sent
    TextView myLabel,txt_proceed,txt_print;
    // will enable user to enter any text to be printed
    EditText myTextbox;
    EditText edt_dis_amt,edt_gst_amt,edt_cgst_amt,edt_sgst_amt;
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    TextView txt_ttl;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    LinearLayout lin_print;
    Spinner sp_customer;
    String srno,slno,str_gst_per,str_discount,str_paid_amt;
    private DatabaseHelper databaseHelper;
    //=====PDF=========================
    PdfPTable table = new PdfPTable(7);
    PdfPCell cell1, cell2,cell3,cell4, cell5,cell6,cell7, cell8,cell9,cell10,cell11,cell12,cell13,cell14;
    File cacheDir;
    final Context context = Table_Bill.this;
    private static final int PERMISSION_REQUEST_CODE = 1;
    //------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_bill);
       // edt_bal_amt = (EditText) findViewById(R.id.edt_bal_amt);
       // edt_paid_amt = (EditText) findViewById(R.id.edt_paid_amt);
        edt_dis_amt = (EditText) findViewById(R.id.edt_dis_amt);
        edt_gst_amt = (EditText) findViewById(R.id.edt_gst_amt);
        edt_cgst_amt = (EditText) findViewById(R.id.edt_cgst_amt);
        edt_sgst_amt = (EditText) findViewById(R.id.edt_sgst_amt);
        formatter = new DecimalFormat("0.00");
        //==============GST======================
        edt_gst_amt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                str_gst_per = edt_gst_amt.getText().toString();
                str_gst_per = str_gst_per.replaceAll("\\s", "");
            }
            @Override
            public void afterTextChanged(Editable editable) {
                DecimalFormat formatter = new DecimalFormat("0.00");
                if (str_gst_per.equals("")) {
                    str_gst_per = "0";
                    txt_ttl.setText(""+formatter.format(ttl));
                } else {
                    discount();
                  //  add_amt();
                }

            }
        });

        //==============DISCOUNT======================
        edt_dis_amt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                str_discount = edt_dis_amt.getText().toString();
                str_discount = str_discount.replaceAll("\\s", "");
            }
            @Override
            public void afterTextChanged(Editable editable) {
                DecimalFormat formatter = new DecimalFormat("0.00");
                if (str_discount.equals("")) {
                    str_discount = "0";
                    edt_dis_amt.setText(str_discount);
                } else {
                    discount_amt();
                    //  add_amt();
                }
            }
        });

        txt_ttl=(TextView) findViewById(R.id.txt_ttl);
       // sp_customer=(Spinner)findViewById(R.id.sp_customer);
        Cust_Name=new ArrayList<>();
        Cust_Id=new ArrayList<>();
        //--------------Search Items----------------
        searchView = (SearchView) findViewById(R.id.grid_searchView);
        //------------------------------------------------------------------------------------------
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >= 0) {
                    SubCodeStr = newText;
                    SubCodeStr = SubCodeStr.replaceAll(" ", "%" + " ").toLowerCase();
                    Log.d("ssss", SubCodeStr);
                    menu(SubCodeStr);
                } else if (TextUtils.isEmpty(newText)) {
                    menu("");
                } else {
                    menu("");
                }
                return false;
            }
        });
        //-------------Current Time------------------------------
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss  a");
        cur_time = dateFormat.format(new Date()).toString();
        ///System.out.println(cur_time);
        Log.d("sss",cur_time);
        //-----------------------------------
        Date cc = Calendar.getInstance().getTime();
        SimpleDateFormat dff = new SimpleDateFormat("dd/MM/yyyy");
        systemDate = dff.format(cc);
        //date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        formattedDate = df.format(c);
        System.out.println("Today Date => " + formattedDate);
        txt_date=(TextView)findViewById(R.id.txt_date);
      /*  txt_proceed=(TextView)findViewById(R.id.txt_proceed);
        txt_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cottage_edit();
                sp_customer.setEnabled(false);
                txt_proceed.setEnabled(false);
            }
        });*/
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Temp_date=out.format(d);
        Query_date=Temp_date;
        txt_date.setText(Query_date);
        //--------DatePicker-----------------------
        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Table_Bill.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                txt_date.setText(""+str_day + "/" + str_month + "/" + year);
                                Temp_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                Query_date=Temp_date;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        //-----------------------------------------*/

       // sp_fg=(Spinner)findViewById(R.id.sp_fg);
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(Table_Bill.this, R.drawable.load);

        //---------------------Recyclerview 1-----------------------------------------
        data_list = new ArrayList<HashMap<String, String>>();
        print_arryList = new ArrayList<HashMap<String, String>>();
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(Table_Bill.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(Table_Bill.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
       //---------------------Recyclerview swap-----------------------------------------
        swap_arryList = new ArrayList<HashMap<String, String>>();
        recycler_swap_list = (RecyclerView)findViewById(R.id.recycler_swap_list);
        layoutManager_swap = new LinearLayoutManager(Table_Bill.this, RecyclerView.VERTICAL, false);
        recycler_swap_list.setLayoutManager(layoutManager_swap);
        swap_recyclerAdapter=new tswap_recyclerAdapter(Table_Bill.this,swap_arryList);
        recycler_swap_list.setAdapter(swap_recyclerAdapter);
        //-------------------------------------------------
        txt_enquiry=(TextView)findViewById(R.id.txt_enquiry);
        txt_enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),TableBill_Enquiry.class);
                startActivity(i);
                finish();
            }
        });
        txt_save=(TextView)findViewById(R.id.txt_save);
        txt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swap_arryList.isEmpty()) {
                    Toast.makeText(Table_Bill.this, "No Value Found.", Toast.LENGTH_SHORT).show();
                } else {
                    //-----------------Alert Dialog---------------------------
                    AlertDialog.Builder builder = new AlertDialog.Builder(Table_Bill.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.bell_alert);
                    builder.setMessage("Do You Really Want To Save This Record ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pd.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    try {
                                        Cursor c = databaseHelper.show_Docno();
                                        if (c.getCount() != 0) {
                                            c.moveToFirst();
                                            do {
                                                srno = c.getString(0);
                                                slno = c.getString(1);
                                            } while (c.moveToNext());
                                            doc_slno = Integer.parseInt(slno);
                                            Log.d("slno", "" + doc_slno);
                                            databaseHelper.update_Doc_slno(doc_slno + 1);
                                            Cursor cc = databaseHelper.show_Docno();
                                            if (cc.getCount() != 0) {
                                                cc.moveToFirst();
                                                do {
                                                    srno = cc.getString(0);
                                                    slno = cc.getString(1);
                                                } while (cc.moveToNext());
                                                doc_slno = Integer.parseInt(slno);
                                                Log.d("slno", "" + doc_slno);
                                                databaseHelper.update_BillNo(doc_slno);
                                            }
                                        /*
                                            ps1 = con.prepareStatement("UPDATE DOC_NO SET SL_DOCNO=SL_DOCNO+1 where '" + Query_date + "' BETWEEN from_year and to_year");
                                        ps1.executeUpdate();
                                        ps1 = con.prepareStatement("SELECT SL_DOCNO FROM DOC_NO where '" + Query_date + "' BETWEEN from_year and to_year");
                                        ResultSet s = ps1.executeQuery();
                                        while (s.next()) {
                                            m_bill_no = Double.parseDouble(s.getString("SL_DOCNO"));
                                        }
                                        ps1 = con.prepareStatement("update SALEITEM set bill_no="+m_bill_no+" where cottage_code="+str_sp_cottage_code+" and BILL_NO=0");
                                        ps1.executeUpdate();*/
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(Table_Bill.this, ""+e, Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(getApplicationContext(), "Saved Success.", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                     print_popup();
                                    txt_ttl.setText("");
                                   /* edt_cgst_amt.setText("");
                                    edt_sgst_amt.setText("");
                                    edt_gst_amt.setText("");
                                    edt_dis_amt.setText("");*/
                                    swap_arryList.clear();

                                    swap_recyclerAdapter.notifyDataSetChanged();
                                }
                            }, 2000);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
            }
        });
        pd.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    //new load_cottage().execute();
                    menu("");
                } catch (Exception e) {
                }
                pd.dismiss();
            }
        }, 2000);
    }
    //-------------Edit Cottage Items------------------

    //==================================================
    public void menu(String SubCodeStr) {
        try {
            pbbar.setVisibility(View.VISIBLE);
            databaseHelper=new DatabaseHelper(this);
            menu_card_arryList.clear();
            Cursor c= databaseHelper.get_menu_search_Data(SubCodeStr);
            if (c.getCount() != 0) {
                c.moveToFirst();
                do {
                    map = new HashMap<String, String>();
                    String ITEM_CODE  =c.getString(0);
                    String MENU_CODE  =c.getString(1);
                    String ITEM_DESC  =c.getString(2);
                    String RATE  =c.getString(3);
                    String MENU_DESC  =c.getString(4);

                    map.put("ITEM_CODE", ITEM_CODE );
                    map.put("MENU_CODE", MENU_CODE );
                    map.put("ITEM_DESC", ITEM_DESC );
                    map.put("MENU_DESC", MENU_DESC );
                    map.put("RATE", RATE);

                    menu_card_arryList.add(map);

                } while (c.moveToNext());

            }
            pbbar.setVisibility(View.GONE);

            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }

    }
    //====================================================
    public class atnds_recyclerAdapter extends RecyclerView.Adapter<atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }
        @Override
        public atnds_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_list_, parent, false);
            atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(final atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {
            holder.list_d1.setText(attendance_list.get(position).get("ITEM_DESC"));
            holder.list_d2.setText(attendance_list.get(position).get("MENU_DESC"));
            // NumberFormat n = new DecimalFormat(".00");
            // holder.list_d3.setText(n.format(Double.parseDouble(attendance_list.get(position).get("computer_stock"))));
            holder.list_d3.setText(attendance_list.get(position).get("RATE"));
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    str_id=attendance_list.get(position).get("ITEM_CODE");
                    item_name=attendance_list.get(position).get("ITEM_DESC");
                    item_rate=attendance_list.get(position).get("RATE");
                    type=attendance_list.get(position).get("MENU_DESC");
                    table_popup_form();
                }
            });
        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }
        public int getItemViewType(int position) {  return position; }
        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d3,list_d4,list_d2, list_d6,list_d7,list_d8, list_amt;
            EditText l,list_d5;
            LinearLayout lin;
            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1= (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
              //  this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                //   this.list_d5 = (EditText) itemView.findViewById(R.id.list_d5);

            }
        }
    }
    //---------------------------------------------------------
    public class tswap_recyclerAdapter extends RecyclerView.Adapter<tswap_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public tswap_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swap_table_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {
             holder.list_d1.setText(attendance_list.get(position).get("item_desc"));
             holder.list_d2.setText(attendance_list.get(position).get("BILL_NO"));
            // NumberFormat n = new DecimalFormat(".00");
            // holder.list_d3.setText(n.format(Double.parseDouble(attendance_list.get(position).get("computer_stock"))));
            holder.list_d3.setText(attendance_list.get(position).get("qty"));
            holder.list_d4.setText(attendance_list.get(position).get("mrp"));
            holder.list_d5.setText(attendance_list.get(position).get("value"));

            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    str_doc_srno=attendance_list.get(position).get("DOC_SRNO");
                   /* ac_head_id=attendance_list.get(position).get("ac_head_id");
                    bar_code=attendance_list.get(position).get("item_code");
                    type=attendance_list.get(position).get("liqr_desc");
                    size=attendance_list.get(position).get("size_desc");
                    brand=attendance_list.get(position).get("brnd_desc");
                    sale_pr=attendance_list.get(position).get("mrp");*/

                    AlertDialog.Builder builder = new AlertDialog.Builder(Table_Bill.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.warn);
                    builder.setMessage("Are You Sures ? Do You Want To Delete The Record.");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Log.d("dddd",str_doc_srno);
                            Toast.makeText(getApplicationContext(), ""+str_doc_srno, Toast.LENGTH_SHORT).show();
                            try {
                                databaseHelper.delete_SaleData(str_doc_srno);
                               // ps1 = con.prepareStatement("delete from SALEITEM where doc_srno='"+str_doc_srno+"' and doc_dt='"+Query_date+"'");
                               // ps1.executeUpdate();
                                swap_arryList.remove(position);
                                swap_recyclerAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d6,list_d7,list_d8, list_d3,list_d4,list_d5;

            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1= (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);

            }
        }
    }
    //--------------------------------------------------------------
    //============popup=========================
    public void table_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_table, null);

        txt_bar_code = (EditText) alertLayout.findViewById(R.id.txt_bar_code);
        txt_bar_code.setText(str_id);
        txt_type = (EditText) alertLayout.findViewById(R.id.txt_type);
        txt_type.setText(item_name);
        txt_sale_pr = (EditText) alertLayout.findViewById(R.id.txt_sale_pr);
        txt_sale_pr.setText(item_rate);
        edit_value = (EditText) alertLayout.findViewById(R.id.edit_value);
        edit_qty = (EditText) alertLayout.findViewById(R.id.edit_qty);
        txt_sale_pr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(txt_sale_pr.length() != 0)
                {
                    try {
                        int sp=Integer.parseInt(txt_sale_pr.getText().toString());
                        // Double sp=Double.parseDouble(sale_pr);
                        int edt = Integer.parseInt(edit_qty.getText().toString());
                        edit_value.setText(""+sp * edt);
                    }catch (Exception e){
                        //Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    edit_value.setText("00");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edit_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                qty=0;
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edit_qty.length() != 0)
                {
                    qty=Integer.parseInt(edit_qty.getText().toString());
                    // Double sp=Double.parseDouble(sale_pr);
                    int sp=Integer.parseInt(txt_sale_pr.getText().toString());
                    edit_value.setText(""+qty*sp);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        btn_save = (Button) alertLayout.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //ttl=0.00;
                        str_item_code = txt_bar_code.getText().toString();
                        str_type = txt_type.getText().toString();
                        str_sale_pr = txt_sale_pr.getText().toString();
                        if (edit_value.getText().toString().equals("") || edit_qty.getText().toString().equals("")) {
                            Toast.makeText(Table_Bill.this, "Value Can Not Be Null", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        } else {
                            if (edit_qty.getText().toString().equals("")) {
                                str_qty = "0";
                            } else {
                                str_qty = edit_qty.getText().toString();
                            }
                            if (edit_value.getText().toString().equals("")) {
                                str_value = "0.00";
                            } else {
                                str_value = edit_value.getText().toString();
                            }
                            Log.d("ssss", str_item_code);
                            Log.d("ssss", str_type);
                            Log.d("ssss", str_sale_pr);
                            Log.d("ssss", str_qty);
                            Log.d("ssss", str_value);
                      //================INSERT SALEITEM VALUE=======================
                            try {
                                Cursor c = databaseHelper.show_Docno();
                                    c.moveToFirst();
                                    do {
                                        srno = c.getString(0);
                                        slno = c.getString(1);

                                    } while (c.moveToNext());
                                    Log.d("doc_no", "" + doc_no);

                            }catch (Exception e)
                            {
                                boolean result = databaseHelper.insert_DocData(0, 0);
                                if (result) {
                                    //refresh();
                                    Toast.makeText(getApplicationContext(), "DocNo Updated.", Toast.LENGTH_LONG).show();
                                }
                            }
                            try {
                                    Cursor c = databaseHelper.show_Docno();
                                    if (c.getCount() != 0) {
                                        c.moveToFirst();
                                        do {
                                            srno = c.getString(0);
                                            slno = c.getString(1);

                                        } while (c.moveToNext());
                                        doc_no = Integer.parseInt(srno);
                                        Log.d("doc_no", "" + doc_no);
                                        databaseHelper.update_Doc_srno(doc_no + 1);
                                    }
                                Cursor cc = databaseHelper.show_Docno();
                                if (cc.getCount() != 0) {
                                    cc.moveToFirst();
                                    do {
                                        srno = cc.getString(0);
                                        slno = cc.getString(1);

                                    } while (cc.moveToNext());
                                    doc_no = Integer.parseInt(srno);
                                    Log.d("doc_no", "" + doc_no);
                                }

                             /*   ps1 = con.prepareStatement("UPDATE DOC_NO SET DOC_SRNO=DOC_SRNO+1 where '" + Query_date + "' BETWEEN from_year and to_year");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("SELECT DOC_SRNO FROM DOC_NO where '" + Query_date + "' BETWEEN from_year and to_year");
                                ResultSet s = ps1.executeQuery();
                                while (s.next()) {
                                    doc_no = Double.parseDouble(s.getString("DOC_SRNO"));
                                }
                                ps1 = con.prepareStatement("INSERT INTO SALEITEM (DOC_DT,ITEM_CODE,RATE,QTY,ITEM_VALUE,DOC_SRNO,COTTAGE_CODE) VALUES('" + Query_date + "','" +str_item_code +"'," + str_sale_pr + "," + str_qty + "," + str_value + ",'" + doc_no + "','" + str_sp_cottage_code + "')");
                                ps1.executeUpdate();*/

                                boolean result = databaseHelper.insert_SaleData(Query_date, str_item_code, str_sale_pr, str_qty, str_value, doc_no, 0);
                                if (result) {
                                    //refresh();
                                    Toast.makeText(getApplicationContext(), "Success.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error..", Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();
                            }
                   //-------------------------------------------------------------
                            smap = new HashMap<String, String>();
                            smap.put("BILL_NO", "0");
                            smap.put("DOC_SRNO", ""+doc_no);
                            smap.put("type", type);
                            smap.put("item_code", str_item_code);
                            smap.put("item_desc", str_type);
                            smap.put("mrp", str_sale_pr);
                            smap.put("value", str_value);
                            smap.put("qty", str_qty);
                            swap_arryList.add(smap);
                            data_list.add(smap);
                            ttl=ttl+Double.parseDouble(str_value);
                            txt_ttl.setText(""+ttl);
                            swap_recyclerAdapter.notifyDataSetChanged();
                            pd.dismiss();
                            //save_data();
                        }
                    }
                }, 1000);
                dialog.dismiss();
            }

        });

        btn_cancel = (ImageView) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        AlertDialog.Builder alert = new AlertDialog.Builder(Table_Bill.this);
        alert.setView(alertLayout);
        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }
    //---------------------------------------------------------
    public void print_popup() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.print_popup, null);
        //------------Blutooth------------------------
       /* try {
            // more codes will be here
            // we are going to have three buttons for specific functions
            Button openButton = (Button) alertLayout.findViewById(R.id.open);
            Button sendButton = (Button) alertLayout.findViewById(R.id.send);
            Button closeButton = (Button) alertLayout.findViewById(R.id.close);

            // text label and input box
            txt_print = (TextView) alertLayout.findViewById(R.id.txt_print);
            txt_print.setText(""+doc_slno);
            myLabel = (TextView) alertLayout.findViewById(R.id.label);
            //  myTextbox = (EditText) findViewById(R.id.entry);

            // open bluetooth connection
            openButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        findBT();
                        openBT();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            // send data typed by the user to be printed
            sendButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        sendData();
                        dialog.dismiss();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // close bluetooth connection
            closeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        closeBT();
                        dialog.dismiss();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }catch(Exception e) {
            e.printStackTrace();
        }*/
        //---------------------------------------------
        btn_pdf_export = (Button) alertLayout.findViewById(R.id.btn_pdf_export);
        btn_pdf_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_pdf_permission();
                dialog.dismiss();
            }
        });
        btn_operation_cancle = (Button) alertLayout.findViewById(R.id.btn_operation_cancle);
        btn_operation_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
      //  check_pdf_permission();
        AlertDialog.Builder alert = new AlertDialog.Builder(Table_Bill.this);
        alert.setView(alertLayout);
        dialog = alert.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }
    public  void refresh()
    {
        Intent i=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
        Toast.makeText(this, "Success.", Toast.LENGTH_LONG).show();
    }
    //------pdf--------------------
    public void check_pdf_permission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                //==============PDF=======
                save_pdf();

            } else {
                requestPermission(); // Code for permission
            }
        } else {

            save_pdf();
            // Toast.makeText(Scan_Master_Reports.this, "Below 23 API Oriented Device....", Toast.LENGTH_SHORT).show();
        }

    }
    //-----------------------------
    public void save_pdf()
    {
        //-------------PDF-------------------

        // String FILE = Environment.getExternalStorageDirectory().toString() + "/Menu Report/" + "menu.pdf";
        //File FILE= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/menurpt.pdf";
        // Create New Blank Document
        Document document = new Document(PageSize.A4);

        // Create Pdf Writer for Writting into New Created Document
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            // Open Document for Writting into document
            document.open();
            // User Define Method
            addTitlePage(document);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (DocumentException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Close Document after writting all content
        document.close();

        Toast.makeText(getApplicationContext(), "PDF File is Created."+FILE, Toast.LENGTH_LONG).show();
        //-----------------------------------
        File outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS)+"/menurpt.pdf");
   //     Toast.makeText(getApplicationContext(), "PDF File is Created."+FILE, Toast.LENGTH_LONG).show();

        try {
            File filePath = new File(""+outputFile);
            final ComponentName name = new ComponentName("com.whatsapp", "com.whatsapp.ContactPicker");
            Intent oShareIntent = new Intent();
            oShareIntent.setComponent(name);
            oShareIntent.setType("application/pdf");
            oShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Website : www.sonkamble.com");
            oShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePath));
            oShareIntent.setType("*/*");
            oShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(oShareIntent);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "WhatsApp require..!!", Toast.LENGTH_SHORT).show();
        }
    }

    //=============================PDF====================================
// Set PDF document Properties
    public void addTitlePage(Document document) throws DocumentException, IOException {
        // Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 15);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 40, Font.BOLD| Font.UNDERLINE, BaseColor.GRAY);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);


        Paragraph prHead = new Paragraph();
        // Set Font in this Paragraph
        prHead.setFont(titleFont);
        // Add item into Paragraph
        prHead.add("INVOICE");
        prHead.add("\n");
        prHead.add("\n");

        prHead.setAlignment(Element.ALIGN_CENTER);

        document.add(prHead);

        Drawable d = getResources().getDrawable(R.drawable.menureport);
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bmp = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image image = Image.getInstance(stream.toByteArray());
        image.scaleToFit(100, 100);
        //============grand Total===========================
        PdfPTable  hwtbl = new PdfPTable(5);
        PdfPCell  hwcl1 = new PdfPCell(new Phrase(""));
      //  long wll = (new Double(m_ttl)).longValue();
      //  PdfPCell  hwcl2 = new PdfPCell(new Phrase(convertNumberToWord(wll)));
        PdfPCell  hwcl2 = new PdfPCell(new Phrase("Info Technology Collage Road\nNashik\nPin-422101\n Mob-8485070453\nsuresh@gmail.com"));
        PdfPCell  hwcl3 = new PdfPCell(new Phrase(""));
        PdfPCell  hwcl4 = new PdfPCell(new Phrase(""));
        PdfPCell  hwcl5 = new PdfPCell(new Phrase(""));
        hwcl1.setVerticalAlignment(Element.ALIGN_LEFT);
        // cl1.setHorizontalAlignment(Element.ALIGN_LEFT);
        // cl1.setBorder(Rectangle.BOX);
       hwcl1.setPadding(10);
       hwcl2.setPadding(10);
       hwcl3.setPadding(10);
        //  cl1.setColspan(3);
        hwcl1.setColspan(2);
        hwcl1.setBackgroundColor(new BaseColor(255, 204, 0));
        hwcl2.setBackgroundColor(new BaseColor(255, 204, 0));
        hwcl2.setColspan(3);
        hwcl1.addElement(image);
        hwtbl.addCell(hwcl1);
        hwtbl.addCell(hwcl2);
        hwtbl.addCell(hwcl3);
        hwtbl.addCell(hwcl4);
        hwtbl.addCell(hwcl5);
        hwtbl.setTotalWidth(PageSize.A4.getWidth());
        hwtbl.setLockedWidth(true);

        document.add(hwtbl);

        //-------------------------------------------------------
        PdfPTable  chwtbl = new PdfPTable(5);
        PdfPCell  chwcl1 = new PdfPCell(new Phrase("To,\nSuresh Sonkamble,\nCollage road nashik\nPin-422101"));
        PdfPCell  chwcl2 = new PdfPCell(new Phrase("Invoice No: #12345454\nInvoice Date: 01/13/2022"));
        PdfPCell  chwcl3 = new PdfPCell(new Phrase(""));
        PdfPCell  chwcl4 = new PdfPCell(new Phrase(""));
        PdfPCell  chwcl5 = new PdfPCell(new Phrase(""));
        chwcl1.setVerticalAlignment(Element.ALIGN_RIGHT);
        chwcl2.setVerticalAlignment(Element.ALIGN_LEFT);
        // cl1.setHorizontalAlignment(Element.ALIGN_LEFT);
        // cl1.setBorder(Rectangle.BOX);
        chwcl1.setPadding(10);
        chwcl2.setPadding(10);
        chwcl3.setPadding(10);
        //  cl1.setColspan(3);
        chwcl1.setColspan(2);
        chwcl2.setColspan(3);
        chwtbl.addCell(chwcl1);
        chwtbl.addCell(chwcl2);
        chwtbl.addCell(chwcl3);
        chwtbl.addCell(chwcl4);
        chwtbl.addCell(chwcl5);
        chwtbl.setTotalWidth(PageSize.A4.getWidth());
        chwtbl.setLockedWidth(true);
        document.add(chwtbl);
        //-------------------------------------------------------


      /*  Paragraph c = new Paragraph();
        c.setFont(catFont);
        c.add("\n");
        c.add("Invoice No: #12345454");
        c.add("\n");
        c.add("Invoice Date: 1/7/2022");
        c.add("\n");
        c.add("\n");
        c.setAlignment(Element.ALIGN_RIGHT);



        Paragraph cat = new Paragraph();
        cat.setFont(catFont);
        cat.add("\n");
        cat.add("To, \n");
        cat.add("\n");
        cat.add("Suresh Sonkamble");
        cat.add("\n");
        cat.add("Collage Road");
        cat.add("\n");
        cat.add("Nashik");
        cat.add("\n");
        cat.add("Pin-422005");
        cat.add("\n");
        cat.add("\n");
        cat.setAlignment(Element.ALIGN_LEFT);


        // Add all above details into Document

        document.add(c);
        document.add(cat);*/
        document.add(table);

        /* Header values*/
        table = new PdfPTable(5);
        cell1 = new PdfPCell(new Phrase("ID"));
        cell2 = new PdfPCell(new Phrase("ITEM NAME"));
        cell3 = new PdfPCell(new Phrase("RATE."));
        cell4 = new PdfPCell(new Phrase("QTY"));
        cell5 = new PdfPCell(new Phrase("VALUE"));

        cell1.setVerticalAlignment(Element.ALIGN_LEFT);
        cell2.setVerticalAlignment(Element.ALIGN_LEFT);
        cell3.setVerticalAlignment(Element.ALIGN_LEFT);
        cell4.setVerticalAlignment(Element.ALIGN_LEFT);
        cell5.setVerticalAlignment(Element.ALIGN_LEFT);

        cell1.setBorder(Rectangle.BOX);
        cell1.setPadding(10);

        cell2.setBorder(Rectangle.BOX);
        cell2.setPadding(10);

        cell3.setBorder(Rectangle.BOX);
        cell3.setPadding(10);

        cell4.setBorder(Rectangle.BOX);
        cell4.setPadding(10);

        cell5.setBorder(Rectangle.BOX);
        cell5.setPadding(10);


        cell1.setBackgroundColor(BaseColor.ORANGE);
        cell2.setBackgroundColor(BaseColor.ORANGE);
        cell3.setBackgroundColor(BaseColor.ORANGE);
        cell4.setBackgroundColor(BaseColor.ORANGE);
        cell5.setBackgroundColor(BaseColor.ORANGE);

        /*//Table values*//**//*
    cell5 = new PdfPCell(new Phrase(b));
    cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
    cell5.setBorder(Rectangle.NO_BORDER);
    cell5.setPadding(10);*/
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        //=================================================================
        total=0.00;
        for (int k = 0; k < data_list.size(); k++) {
            map = (HashMap) data_list.get(k);

                String id  = map.get("item_code");
                String name  = map.get("item_desc");
                String rate  = map.get("mrp");
                String qty  = map.get("qty");
                String val  = map.get("value");

                total=total+Double.parseDouble(map.get("value"));

                cell6 = new PdfPCell(new Phrase(id));
                cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell6.setBorder(Rectangle.BOX);
                cell6.setPadding(10);

                cell7 = new PdfPCell(new Phrase(name));
                cell7.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell7.setBorder(Rectangle.BOX);
                cell7.setPadding(10);

                cell8 = new PdfPCell(new Phrase(rate));
                cell8.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell8.setBorder(Rectangle.BOX);
                cell8.setPadding(10);

                cell9 = new PdfPCell(new Phrase(qty));
                cell9.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell9.setBorder(Rectangle.BOX);
                cell9.setPadding(10);

                cell10 = new PdfPCell(new Phrase(val));
                cell10.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell10.setBorder(Rectangle.BOX);
                cell10.setPadding(10);

                table.addCell(cell6);
                table.addCell(cell7);
                table.addCell(cell8);
                table.addCell(cell9);
                table.addCell(cell10);
                table.setTotalWidth(PageSize.A4.getWidth());
                table.setLockedWidth(true);

            /*table.addCell(cell11);
			table.addCell(cell12);*/

                // add table into document

        }
        data_list.clear();
        document.add(table);

        //============Total===========================
        PdfPTable  table = new PdfPTable(5);
        PdfPCell  cell1 = new PdfPCell(new Phrase(""));
       // PdfPCell  cell2 = new PdfPCell(new Phrase(""));
       // PdfPCell  cell3 = new PdfPCell(new Phrase(""));
        PdfPCell  cell4 = new PdfPCell(new Phrase("Sub Total"));
        PdfPCell  cell5 = new PdfPCell(new Phrase(""+total));
        cell4.setBackgroundColor(BaseColor.ORANGE);
        cell5.setBackgroundColor(BaseColor.ORANGE);
        cell1.setVerticalAlignment(Element.ALIGN_LEFT);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
       // cell1.setBorder(Rectangle.BOX);
        cell4.setPadding(10);
        cell5.setPadding(10);
        cell1.setColspan(3);
        table.addCell(cell1);
        //table.addCell(cell2);
        //table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setLockedWidth(true);
        document.add(table);


        // double gstper=Double.parseDouble(str_gst_per)/2;
        //============SGST===========================
        PdfPTable  stble = new PdfPTable(5);
        PdfPCell  scel1 = new PdfPCell(new Phrase(""));
      //  PdfPCell  scel2 = new PdfPCell(new Phrase(""));
       // PdfPCell  scel3 = new PdfPCell(new Phrase(""));
        PdfPCell  scel4 = new PdfPCell(new Phrase("SGST"));
        PdfPCell  scel5 = new PdfPCell(new Phrase(""+formatter.format(m_sgst)));
      //  scel4.setBackgroundColor(BaseColor.LIGHT_GRAY);
      //  scel5.setBackgroundColor(BaseColor.LIGHT_GRAY);
        scel1.setVerticalAlignment(Element.ALIGN_LEFT);
        scel1.setHorizontalAlignment(Element.ALIGN_LEFT);
        //cel1.setBorder(Rectangle.BOX);
        scel4.setPadding(10);
        scel5.setPadding(10);
        scel1.setColspan(3);
        stble.addCell(scel1);
       // stble.addCell(scel2);
       // stble.addCell(scel3);
        stble.addCell(scel4);
        stble.addCell(scel5);
        stble.setTotalWidth(PageSize.A4.getWidth());
        stble.setLockedWidth(true);
        document.add(stble);

        //============CGST===========================
        PdfPTable  ctble = new PdfPTable(5);
        PdfPCell  ccel1 = new PdfPCell(new Phrase(""));
       // PdfPCell  ccel2 = new PdfPCell(new Phrase(""));
      //  PdfPCell  ccel3 = new PdfPCell(new Phrase(""));
        PdfPCell  ccel4 = new PdfPCell(new Phrase("CGST "));
        PdfPCell  ccel5 = new PdfPCell(new Phrase(""+formatter.format(m_cgst)));
      //  ccel4.setBackgroundColor(BaseColor.LIGHT_GRAY);
     //   ccel5.setBackgroundColor(BaseColor.LIGHT_GRAY);
        ccel1.setVerticalAlignment(Element.ALIGN_LEFT);
        ccel1.setHorizontalAlignment(Element.ALIGN_LEFT);
        //cel1.setBorder(Rectangle.BOX);
        ccel4.setPadding(10);
        ccel5.setPadding(10);
        ccel1.setColspan(3);
        ctble.addCell(ccel1);
       // ctble.addCell(ccel2);
       // ctble.addCell(ccel3);
        ctble.addCell(ccel4);
        ctble.addCell(ccel5);
        ctble.setTotalWidth(PageSize.A4.getWidth());
        ctble.setLockedWidth(true);
        document.add(ctble);
        //===============Total=============================
        //============GST===========================
        PdfPTable  tble = new PdfPTable(5);
        PdfPCell  cel1 = new PdfPCell(new Phrase(""));
     //   PdfPCell  cel2 = new PdfPCell(new Phrase(""));
      //  PdfPCell  cel3 = new PdfPCell(new Phrase(""));
        PdfPCell  cel4 = new PdfPCell(new Phrase("GST "+edt_gst_amt.getText().toString()+"%"));
        PdfPCell  cel5 = new PdfPCell(new Phrase(""+formatter.format(m_gst)));
        //   cel4.setBackgroundColor(BaseColor.LIGHT_GRAY);
        //   cel5.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cel1.setVerticalAlignment(Element.ALIGN_LEFT);
        cel1.setHorizontalAlignment(Element.ALIGN_LEFT);
        //cel1.setBorder(Rectangle.BOX);
        cel4.setPadding(10);
        cel5.setPadding(10);
        cel1.setColspan(3);
        tble.addCell(cel1);
      //  tble.addCell(cel2);
      //  tble.addCell(cel3);
        tble.addCell(cel4);
        tble.addCell(cel5);
        tble.setTotalWidth(PageSize.A4.getWidth());
        tble.setLockedWidth(true);
        document.add(tble);
        //============grand Total===========================
        PdfPTable  tbl1 = new PdfPTable(5);
        PdfPCell  cl11 = new PdfPCell(new Phrase(""));
      //  PdfPCell  cl21 = new PdfPCell(new Phrase(""));
      //  PdfPCell  cl31 = new PdfPCell(new Phrase(""));
        PdfPCell  cl41 = new PdfPCell(new Phrase("Total"));
        int ttl= (int) (m_ttl+disc);
        PdfPCell  cl51 = new PdfPCell(new Phrase(""+ttl));
        cl41.setBackgroundColor(BaseColor.ORANGE);
        cl51.setBackgroundColor(BaseColor.ORANGE);
        cl11.setVerticalAlignment(Element.ALIGN_LEFT);
        // cl1.setHorizontalAlignment(Element.ALIGN_LEFT);
        // cl1.setBorder(Rectangle.BOX);
        cl41.setPadding(10);
        cl51.setPadding(10);
        //  cl1.setColspan(3);
        cl11.setColspan(3);
        tbl1.addCell(cl11);
      //  tbl1.addCell(cl21);
      //  tbl1.addCell(cl31);
        tbl1.addCell(cl41);
        tbl1.addCell(cl51);
        tbl1.setTotalWidth(PageSize.A4.getWidth());
        tbl1.setLockedWidth(true);
        document.add(tbl1);
        //============DISSCOUNT===========================
        PdfPTable  Dtble = new PdfPTable(5);
        PdfPCell  Dcel1 = new PdfPCell(new Phrase(""));
      //  PdfPCell  Dcel2 = new PdfPCell(new Phrase(""));
       // PdfPCell  Dcel3 = new PdfPCell(new Phrase(""));
        PdfPCell  Dcel4 = new PdfPCell(new Phrase("DISCOUNT "));
        PdfPCell  Dcel5 = new PdfPCell(new Phrase(""+str_discount));
       // Dcel4.setBackgroundColor(BaseColor.PINK);
       // Dcel5.setBackgroundColor(BaseColor.PINK);
        Dcel1.setVerticalAlignment(Element.ALIGN_LEFT);
        Dcel1.setHorizontalAlignment(Element.ALIGN_LEFT);
        //cel1.setBorder(Rectangle.BOX);
        Dcel4.setPadding(10);
        Dcel5.setPadding(10);
        Dcel1.setColspan(3);
        Dtble.addCell(Dcel1);
     //   Dtble.addCell(Dcel2);
     //   Dtble.addCell(Dcel3);
        Dtble.addCell(Dcel4);
        Dtble.addCell(Dcel5);
        Dtble.setTotalWidth(PageSize.A4.getWidth());
        Dtble.setLockedWidth(true);
        document.add(Dtble);
     //============grand Total===========================
        PdfPTable  tbl = new PdfPTable(5);
        PdfPCell  cl1 = new PdfPCell(new Phrase(""));
      //  PdfPCell  cl2 = new PdfPCell(new Phrase(""));
      //  PdfPCell  cl3 = new PdfPCell(new Phrase(""));
        PdfPCell  cl4 = new PdfPCell(new Phrase("Grand Total"));
        PdfPCell  cl5 = new PdfPCell(new Phrase(""+formatter.format(m_ttl)));
        cl4.setBackgroundColor(BaseColor.ORANGE);
        cl5.setBackgroundColor(BaseColor.ORANGE);
        cl1.setVerticalAlignment(Element.ALIGN_LEFT);
       // cl1.setHorizontalAlignment(Element.ALIGN_LEFT);
       // cl1.setBorder(Rectangle.BOX);
        cl4.setPadding(10);
        cl5.setPadding(10);
       // cl1.setColspan(3);
        cl1.setColspan(3);
        tbl.addCell(cl1);
       // tbl.addCell(cl2);
       // tbl.addCell(cl3);
        tbl.addCell(cl4);
        tbl.addCell(cl5);
        tbl.setTotalWidth(PageSize.A4.getWidth());
        tbl.setLockedWidth(true);
        document.add(tbl);
//-------------------------------------------------------
        //============grand Total===========================
        PdfPTable  wtbl = new PdfPTable(5);
        PdfPCell  wcl1 = new PdfPCell(new Phrase("In Word: "));
        long wl = (new Double(m_ttl)).longValue();
        PdfPCell  wcl2 = new PdfPCell(new Phrase(convertNumberToWord(wl)));
        PdfPCell  wcl3 = new PdfPCell(new Phrase(""));
        PdfPCell  wcl4 = new PdfPCell(new Phrase(""));
        PdfPCell  wcl5 = new PdfPCell(new Phrase(""));
        wcl1.setVerticalAlignment(Element.ALIGN_LEFT);
        // cl1.setHorizontalAlignment(Element.ALIGN_LEFT);
        // cl1.setBorder(Rectangle.BOX);
        wcl1.setPadding(10);
        //  cl1.setColspan(3);
        wcl2.setColspan(4);
        wtbl.addCell(wcl1);
        wtbl.addCell(wcl2);
        wtbl.addCell(wcl3);
        wtbl.addCell(wcl4);
        wtbl.addCell(wcl5);
        wtbl.setTotalWidth(PageSize.A4.getWidth());
        wtbl.setLockedWidth(true);
        document.add(wtbl);
//==========================================================
        //============grand Total===========================
        PdfPTable  wtb = new PdfPTable(5);
        PdfPCell  wcl = new PdfPCell(new Phrase("Terms & Condition "));
        PdfPCell  wcll1 = new PdfPCell(new Phrase("Goods sold are not returnable and exchangeable."));
        PdfPCell  wcll2 = new PdfPCell(new Phrase(""));
        PdfPCell  wcll3 = new PdfPCell(new Phrase(""));
        PdfPCell  wcll4 = new PdfPCell(new Phrase(""));
        wcl.setVerticalAlignment(Element.ALIGN_LEFT);
        // cl1.setHorizontalAlignment(Element.ALIGN_LEFT);
        // cl1.setBorder(Rectangle.BOX);
        wcl.setPadding(10);
        //  cl1.setColspan(3);
        wcll1.setColspan(4);
        wcl.setBackgroundColor(new BaseColor(255, 204, 0));
        wcll1.setBackgroundColor(new BaseColor(255, 204, 0));
        wtb.addCell(wcl);
        wtb.addCell(wcll1);
        wtb.addCell(wcll2);
        wtb.addCell(wcll3);
        wtb.addCell(wcll4);
        wtb.setTotalWidth(PageSize.A4.getWidth());
        wtb.setLockedWidth(true);
        document.add(wtb);

        //===================================================================
        // Create new Page in PDF
        document.newPage();
        //Toast.makeText(this, "PDF File is Created.", Toast.LENGTH_LONG).show();
    }

    //===================================================================
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Table_Bill.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)&& ActivityCompat.shouldShowRequestPermissionRationale(Table_Bill.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getApplicationContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Table_Bill.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    //===================================================================
    void discount() {
        gstp = 0.00;
        m_ttl = 0.00;
        //--------------------Disscount Calculation-----------
        m_ttl = ttl;
        str_gst_per = edt_gst_amt.getText().toString();
        if (str_gst_per.equals("") || str_gst_per.equals("0.00")) {
            // edt_sgst_amt.setText("0.00");
           //  edt_cgst_amt.setText("0.00");
             Toast.makeText(Table_Bill.this, "Field can not be null", Toast.LENGTH_SHORT).show();
        } else {
            if (str_gst_per.isEmpty()) {
                //edt_sgst_amt.setText("0.00");
               // edt_cgst_amt.setText("0.00");
                str_gst_per = "0";
            } else {
                gstp = Double.parseDouble(str_gst_per);
            }
        }
         m_gst = (gstp / 100) * m_ttl;
       // dr = dt - commission;
        m_cgst=  (m_gst/2);
        m_sgst=  (m_gst/2);

        //System.out.println(formatter .format(a));
        // edt_disc.setText("6");
      //  edt_dis_amt.setText("" + formatter.format(dt));
        //-----------------Call Recursivally---------------------------ERROR
   //     edt_gst_amt.setText("" + formatter.format(gstp));
        edt_sgst_amt.setText("" + formatter.format(m_sgst));
        edt_cgst_amt.setText("" + formatter.format(m_cgst));
        m_ttl=ttl+m_gst;
        txt_ttl.setText(""+formatter.format(m_ttl));
        //----------------------------------------------------

    }
    void discount_amt() {

        m_ttl = 0.00;
        //--------------------Disscount Calculation-----------
        m_ttl=ttl+m_gst;
       // m_ttl = Double.parseDouble(txt_ttl.getText().toString());
        str_discount = edt_dis_amt.getText().toString();
        if (str_discount.equals("") || str_discount.equals("0.00")) {
           // edt_dis_amt.setText("0.00");
            txt_ttl.setText(""+m_ttl);
            Toast.makeText(Table_Bill.this, "Field can not be null", Toast.LENGTH_SHORT).show();
        } else {
            if (str_discount.isEmpty()) {
                str_discount = "0";
                txt_ttl.setText(""+m_ttl);
            } else {
                disc = Integer.parseInt(str_discount);
            }
        }
        m_ttl=m_ttl-disc;
        DecimalFormat formatter = new DecimalFormat("0.00");
        txt_ttl.setText(""+formatter.format(m_ttl));
    }
    //-------------------------------------------------------------------
//==================Number to word======================
    //string type array for one digit numbers
    private static final String[] twodigits = {"", " Ten", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty", " Ninety"};
    //string type array for two digits numbers
    private static final String[] onedigit = {"", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten", " Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen", " Nineteen"};
    //defining constructor of the class

    //user-defined method that converts a number to words (up to 1000)
    private static String convertUptoThousand(int number)
    {
        String soFar;
        if (number % 100 < 20)
        {
            soFar = onedigit[number % 100];
            number = number/ 100;
        }
        else
        {
            soFar = onedigit[number % 10];
            number = number/ 10;
            soFar = twodigits[number % 10] + soFar;
            number = number/ 10;
        }
        if (number == 0)
            return soFar;
        return onedigit[number] + " Hundred " + soFar;
    }
    public static String convertNumberToWord(long number)
    {
//checks whether the number is zero or not
        if (number == 0)
        {
//if the given number is zero it returns zero
            return "zero";
        }
//the toString() method returns a String object that represents the specified long
        String num = Long.toString(number);
//for creating a mask padding with "0"
        String pattern = "000000000000";
//creates a DecimalFormat using the specified pattern and also provides the symbols for the default locale
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
//format a number of the DecimalFormat instance
        num = decimalFormat.format(number);
//format: XXXnnnnnnnnn
//the subString() method returns a new string that is a substring of this string
//the substring begins at the specified beginIndex and extends to the character at index endIndex - 1
//the parseInt() method converts the string into integer
        int billions = Integer.parseInt(num.substring(0,3));
//format: nnnXXXnnnnnn
        int millions  = Integer.parseInt(num.substring(3,6));
//format: nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(num.substring(6,9));
//format: nnnnnnnnnXXX
        int thousands = Integer.parseInt(num.substring(9,12));
        String tradBillions;
        switch (billions)
        {
            case 0:
                tradBillions = "";
                break;
            case 1 :
                tradBillions = convertUptoThousand(billions)+ " Billion ";
                break;
            default :
                tradBillions = convertUptoThousand(billions)+ " Billion ";
        }
        String result =  tradBillions;
        String tradMillions;
        switch (millions)
        {
            case 0:
                tradMillions = "";
                break;
            case 1 :
                tradMillions = convertUptoThousand(millions)+ " Million ";
                break;
            default :
                tradMillions = convertUptoThousand(millions)+ " Million ";
        }
        result =  result + tradMillions;
        String tradHundredThousands;
        switch (hundredThousands)
        {
            case 0:
                tradHundredThousands = "";
                break;
            case 1 :
                tradHundredThousands = "One Thousand ";
                break;
            default :
                tradHundredThousands = convertUptoThousand(hundredThousands)+ " Thousand ";
        }
        result =  result + tradHundredThousands;
        String tradThousand;
        tradThousand = convertUptoThousand(thousands);
        result =  result + tradThousand;
//removing extra space if any
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

    //--------------------------------------------------
    @Override
    public void onBackPressed() {
        Intent i=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}