package com.sonkamble.invoice;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sonkamble.invoice.database.DatabaseHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TableBill_Enquiry extends AppCompatActivity {

    String item_nm,item_id,item_rate,item_fid,qty,value,code;
    ConnectionClass connectionClass;
    ProgressBar pbbar;
    Toolbar toolbar;
    String msg="";
    String query="";
    EditText txt_name,txt_rate,txt_value,txt_qty,txt_code;
    Button btn_operation_cancle,btn_update,btn_del,btn_excel_export,btn_pdf_export;
    AlertDialog dialog;
    Spinner sp_billno,sp_customer;
    String str_sp_cottage_code,str_sp_cottage_desc,str_sp_bill_no;
    TextView txt_proceed,txt_ttl;
    int qtyv=0;
    double total=0.00;
    //=====PDF=========================
    PdfPTable table = new PdfPTable(7);
    PdfPCell cell1, cell2,cell3,cell4, cell5,cell6,cell7, cell8,cell9,cell10,cell11,cell12,cell13,cell14;
    File cacheDir;
    final Context context = TableBill_Enquiry.this;
    private static final int PERMISSION_REQUEST_CODE = 1;
    //private static final int PERMISSION_REQUEST_CODE = 1;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> print_arryList;
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    Double ttl=0.00;
    private DatabaseHelper databaseHelper;
    //------Bluetooth---------------------------
    // will show the statuses like bluetooth open, close or data sent
    TextView myLabel;
    // will enable user to enter any text to be printed
    EditText myTextbox;
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    List<String> Cust_Name;
    List<String> Cust_Id;
    List<String> Bill_No;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    LinearLayout lin_print;

    //------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_report);
        lin_print = (LinearLayout) findViewById(R.id.lin_print);

        btn_pdf_export = (Button) findViewById(R.id.btn_pdf_export);
        btn_pdf_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_pdf_permission();
            }
        });
       /* btn_excel_export = (Button) findViewById(R.id.btn_excel_export);
        btn_excel_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excel_permission();
            }
        });*/
        txt_ttl = (TextView) findViewById(R.id.txt_ttl);
        txt_proceed = (TextView) findViewById(R.id.txt_proceed);
        // txt_bproceed = (TextView) findViewById(R.id.txt_bproceed);
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        connectionClass = new ConnectionClass();
        sp_billno=(Spinner)findViewById(R.id.sp_billno);
        //  sp_customer=(Spinner)findViewById(R.id.sp_customer);
        //---------------------Recyclerview 1-----------------------------------------
        print_arryList = new ArrayList<HashMap<String, String>>();
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(TableBill_Enquiry.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(TableBill_Enquiry.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        //------------------------------------------------------------------------------------------
        Cust_Id=new ArrayList<>();
        Cust_Name=new ArrayList<>();
        Bill_No=new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        ImageView toolbar_img = (ImageView) toolbar.findViewById(R.id.toolbar_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Table Bill List");
        // txt_user.setText(emp_name);
        toolbar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        txt_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sp_customer.setEnabled(false);
                txt_proceed.setVisibility(View.INVISIBLE);
                new load_bill_no().execute();
            }
        });
       /* txt_bproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sale_report(str_sp_cottage_desc);
            }
        });*/
        // new load_bill_no().execute();
        //  new load_cottage().execute();

        //------------Blutooth------------------------
        try {
            // more codes will be here
            // we are going to have three buttons for specific functions
            Button openButton = (Button) findViewById(R.id.open);
            Button sendButton = (Button) findViewById(R.id.send);
            Button closeButton = (Button) findViewById(R.id.close);

            // text label and input box
            myLabel = (TextView) findViewById(R.id.label);
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
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }catch(Exception e) {
            e.printStackTrace();
        }
        //---------------------------------------------

    }

    public void sale_report(String billno) {
        //SubCodeStr,m_ratetype
        // m_compcode,m_loctcode,m_maintainstockyn,m_gstreverseyn,
        try {
            // sp_data  = new ArrayList<Map<String, String>>();
            {
                ttl=0.0;
                menu_card_arryList.clear();
                lin_print.setVisibility(View.VISIBLE);
                Cursor rs= databaseHelper.show_PrintData(Integer.parseInt(billno));
                if (rs.getCount() != 0) {
                    rs.moveToFirst();
                    do {
                        map = new HashMap<String, String>();
                        map.put("DOC_SRNO", rs.getString(0));
                        map.put("BILL_NO", rs.getString(1));
                        /*map.put("ITEM_DESC", rs.getString(2));
                        map.put("RATE", rs.getString(3));
                        map.put("QTY", rs.getString(4));
                        map.put("ITEM_VALUE", rs.getString(5));*/
                        String desc = String.format("%-22s", rs.getString(2));
                        map.put("ITEM_DESC",desc);
                        String rate = String.format("%-4s", rs.getString(3));
                        map.put("RATE", rate);
                        String qty = String.format("%-3s", rs.getString(4));
                        map.put("QTY",qty);
                        String val = String.format("%-5s", rs.getString(5));
                        map.put("ITEM_VALUE", val);
                        map.put("DOC_DT", rs.getString(6));
                        ttl=ttl+Double.parseDouble(rs.getString(5));
                        txt_ttl.setText(""+ttl);
                        menu_card_arryList.add(map);

                    } while (rs.moveToNext());

                }
                if (attendance_recyclerAdapter != null) {
                    attendance_recyclerAdapter.notifyDataSetChanged();
                    System.out.println("Adapter " + attendance_recyclerAdapter.toString());
                }

                   /* query="select DOC_SRNO, BILL_NO,ITEM_DESC,SALEITEM.RATE,QTY,ITEM_VALUE,DOC_DT from SALEITEM,ITEMMAST WHERE SALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND BILL_NO="+m_bill_no+"";
                   // query="select DOC_SRNO,LTRIM(STR(BILL_NO)) AS  BILL_NO,left(ITEM_DESC+space(23),23)as item_desc,str(SALEITEM.RATE,8,2) as rate,STR(QTY,3) AS  QTY,str(ITEM_VALUE,9,2) as ITEM_VALUE,convert(varchar(10),DOC_DT,103) as doc_dt,COTTAGEMAST.COTTAGE_CODE,COTTAGE_DESC from SALEITEM,ITEMMAST,COTTAGEMAST WHERE SALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND COTTAGEMAST.COTTAGE_CODE=SALEITEM.COTTAGE_CODE AND BILL_NO="+m_bill_no+"";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    print_arryList.clear();
                    //lin_print.setVisibility(View.VISIBLE);
                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        map = new HashMap<String, String>();
                        map.put("DOC_SRNO", rs.getString("DOC_SRNO"));
                        map.put("BILL_NO", rs.getString("BILL_NO"));
                        map.put("ITEM_DESC", rs.getString("ITEM_DESC"));
                        map.put("RATE", rs.getString("RATE"));
                        map.put("QTY", rs.getString("QTY"));
                        map.put("ITEM_VALUE", rs.getString("ITEM_VALUE"));
                        map.put("DOC_DT", rs.getString("DOC_DT"));
                        map.put("COTTAGE_CODE", rs.getString("COTTAGE_CODE"));
                        map.put("COTTAGE_DESC", rs.getString("COTTAGE_DESC"));
                        ttl=ttl+Double.parseDouble(rs.getString("ITEM_VALUE"));
                        txt_ttl.setText(""+ttl);
                        print_arryList.add(map);
                    }       */     }


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }
    public class atnds_recyclerAdapter extends RecyclerView.Adapter<TableBill_Enquiry.atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public TableBill_Enquiry.atnds_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_table, parent, false);
            TableBill_Enquiry.atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final TableBill_Enquiry.atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {

            holder.list_nm.setText(attendance_list.get(position).get("ITEM_DESC"));
            holder.list_rt.setText(attendance_list.get(position).get("RATE"));
            holder.list_d3.setText(attendance_list.get(position).get("QTY"));
            holder.list_d4.setText(attendance_list.get(position).get("ITEM_VALUE"));
            holder.list_d5.setText(attendance_list.get(position).get("DOC_DT"));
            holder.list_code.setText(attendance_list.get(position).get("COTTAGE_CODE"));
            //  Toast.makeText(getApplicationContext(), ""+attendance_list.get(position).get("BILL_NO"), Toast.LENGTH_SHORT).show();

            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item_nm=attendance_list.get(position).get("ITEM_DESC");
                    item_rate=attendance_list.get(position).get("RATE");
                    qty=attendance_list.get(position).get("QTY");
                    value=attendance_list.get(position).get("ITEM_VALUE");
                    item_id=attendance_list.get(position).get("DOC_SRNO");
                    item_fid=attendance_list.get(position).get("BILL_NO");
                    code=attendance_list.get(position).get("COTTAGE_CODE");
                    item_popup_form();
                }
            });
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_nm,list_rt,list_d3,list_d4,list_d5,list_code;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_nm= (TextView) itemView.findViewById(R.id.list_d1);
                this.list_rt= (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 =(TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4= (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5= (TextView) itemView.findViewById(R.id.list_d5);
                this.list_code= (TextView) itemView.findViewById(R.id.list_code);

            }
        }
    }
    public void item_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.tbl_update_popup, null);

        txt_name = (EditText) alertLayout.findViewById(R.id.txt_name);
        txt_name.setText(item_nm);
        txt_code = (EditText) alertLayout.findViewById(R.id.txt_code);
        txt_code.setText(code);
        txt_rate = (EditText) alertLayout.findViewById(R.id.txt_rate);
        txt_rate.setText(item_rate);
        txt_qty = (EditText) alertLayout.findViewById(R.id.txt_qty);
        txt_qty.setText(qty);
        txt_value = (EditText) alertLayout.findViewById(R.id.txt_value);
        txt_value.setText(value);
        txt_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                qtyv=0;
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(txt_qty.getText().toString().trim().equals("")){
                    Toast.makeText(TableBill_Enquiry.this, "Quantity Can Not Be Null", Toast.LENGTH_SHORT).show();
                }
                else {
                    qtyv=Integer.parseInt(txt_qty.getText().toString().trim());
                    // Double sp=Double.parseDouble(sale_pr);
                    Double rt=Double.parseDouble(txt_rate.getText().toString().trim());
                    txt_value.setText(""+qtyv*rt);
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        btn_operation_cancle = (Button) alertLayout.findViewById(R.id.btn_operation_cancle);
        btn_operation_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_del = (Button) alertLayout.findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg="Deleted Successfully";
                delete();
                dialog.dismiss();
            }
        });

        btn_update = (Button) alertLayout.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item_nm=txt_name.getText().toString();
                item_rate=txt_rate.getText().toString();
                qty=txt_qty.getText().toString();
                value=txt_value.getText().toString();
                code=txt_code.getText().toString();
                msg="Updated Successfully";
                if(qty.equals("")){
                    Toast.makeText(TableBill_Enquiry.this, "Quantity Can Not Be Null", Toast.LENGTH_SHORT).show();
                }
                else {
                    upate();
                }
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(TableBill_Enquiry.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }
    public void upate() {
        try {
            pbbar.setVisibility(View.VISIBLE);
           {    databaseHelper.update_Sale_Doc_srno(item_id,qty,value);
                refresh();
            }
            pbbar.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }
    public void delete() {
        try {
            pbbar.setVisibility(View.VISIBLE);
           {
               databaseHelper.delete_SaleData(item_id);
                refresh();
            }
            pbbar.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }

    public  void refresh()
    {
        Intent i=new Intent(getApplicationContext(), TableBill_Enquiry.class);
        startActivity(i);
        finish();
        Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
    }

    public class load_bill_no extends AsyncTask<String, String, String> {
        List<Map<String, String>> sp_data = new ArrayList<Map<String, String>>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                {
                    try {
                        databaseHelper=new DatabaseHelper(getApplicationContext());
                        Bill_No.clear();
                        Cursor c= databaseHelper.show_BillData();
                        if (c.getCount() != 0) {
                            c.moveToFirst();
                            do {
                                HashMap<String, String> map = new HashMap<String, String>();
                                String bilno=c.getString(0);
                                Log.d("bbb",bilno);
                                map.put("A", bilno+""+c.getString(1));
                                Bill_No.add(bilno+" :  "+c.getString(1));

                                sp_data.add(map);
                            } while (c.moveToNext());
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                    }
                  /*//  String query = "SELECT DISTINCT LTRIM(STR(BILL_NO)) AS  BILL_NO ,convert(varchar(10),DOC_DT,103) as doc_dt FROM SALEITEM where cottage_code="+str_sp_cottage_code+" AND BILL_NO<>0 ORDER BY BILL_NO DESC ";
                    String query = "SELECT DISTINCT  BILL_NO ,convert(varchar(10),DOC_DT,103) as doc_dt FROM SALEITEM where cottage_code="+str_sp_cottage_code+" AND BILL_NO<>0 ORDER BY BILL_NO DESC ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    Bill_No.clear();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        //data.put("A", rs.getString(1));
                        String bilno=rs.getString(1);
                        bilno = bilno.substring(0,bilno.indexOf('.'));
                        Log.d("bbb",bilno);
                        data.put("A", bilno+""+rs.getString(2));
                        Bill_No.add(bilno+" :  "+rs.getString(2));
                       // Bill_No.add(rs.getString(1)+""+rs.getString(2));
                        sp_data.add(data);
                    }*/
                }  //z = "Success";
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(TableBill_Enquiry.this, android.R.layout.simple_spinner_dropdown_item, Bill_No);
            sp_billno.setAdapter(adapter);
            sp_billno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    menu_card_arryList.clear();
                    attendance_recyclerAdapter.notifyDataSetChanged();
                    str_sp_bill_no= Bill_No.get(i);
                    str_sp_bill_no= str_sp_bill_no.substring(0, str_sp_bill_no.indexOf(" :  "));
                    sale_report(str_sp_bill_no);
                    Toast.makeText(getApplicationContext(), "bill_no: "+str_sp_bill_no, Toast.LENGTH_SHORT).show();
                    Log.d("m_customer_code",str_sp_bill_no);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            super.onPostExecute(s);
        }
    }
    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device//BP03R
                    // we got this name from the list of paired devices //
                    if (device.getName().equals("BTP-3AA2475")) {
                        mmDevice = device;
                        break;
                    }
                }
            }

            myLabel.setText("Bluetooth device found.");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            myLabel.setText("Ready To Print");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {
            NumberFormat n = new DecimalFormat(".00");

            String final_string="";
            String data="";
            String footer;
            String msg = "";
            msg +=" "+
                    "\n"+"|----------------------------------------------|"+
                    "\n"+"|"+" Shop Name : "+"Test"+"                             |"+
                    "\n"+"|----------------------------------------------|"+
                    "\n"+"|"+" Bill No : "+map.get("BILL_NO") +"               Date : "+map.get("DOC_DT")+" |"+
                    "\n"+"|----------------------------------------------|"+
                    "\n"+"|         Name          |Qty|  Rate  |  Value  |"+
                    "\n"+"|----------------------------------------------|";
            for (int k = 0; k < menu_card_arryList.size(); k++) {
                map = (HashMap) menu_card_arryList.get(k);
                try {
                    data+= "\n"+"|"+map.get("ITEM_DESC") +" |"+map.get("QTY") +"| "+map.get("RATE") +"   |  "+map.get("ITEM_VALUE") +"  |";
                } catch (Exception e) {
                }
            }
            footer= "\n"+"|----------------------------------------------|"+
                    "\n"+"|                            Total ==>  "+n.format(ttl)+"|"+
                    "\n"+"|----------------------------------------------|"+
                    "\n"+" Thank You"+
                    "\n"+" "+
                    "\n"+" ";
            final_string=msg+data+footer;
            Log.d("Print",final_string);
            mmOutputStream.write(final_string.getBytes());

            // tell the user data were sent
            myLabel.setText("Data sent.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        Intent i=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
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
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Close Document after writting all content
        document.close();

        Toast.makeText(getApplicationContext(), "PDF File is Created."+FILE, Toast.LENGTH_LONG).show();
        //-----------------------------------
    }

    //=============================PDF====================================
// Set PDF document Properties
    public void addTitlePage(Document document) throws DocumentException
    {
        // Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD| Font.UNDERLINE, BaseColor.GRAY);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // Start New Paragraph
        Paragraph prHead = new Paragraph();
        // Set Font in this Paragraph
        prHead.setFont(titleFont);
        // Add item into Paragraph
        prHead.add("ITEMS REPORT\n");
        prHead.add("\n");
        prHead.setAlignment(Element.ALIGN_CENTER);

       /* Paragraph cat = new Paragraph();
        cat.setFont(catFont);
        cat.add("\n");
        cat.add("CUSTOMER REPORT \n");
        cat.add("\n");
        cat.add("Customer Name: "+name+"   "+"Mobile: "+number);
        cat.add("\n");
        cat.add("\n");
        cat.setAlignment(Element.ALIGN_CENTER);*/

        // Add all above details into Document
        document.add(prHead);
        // document.add(cat);
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
        cell1.setPadding(5);

        cell2.setBorder(Rectangle.BOX);
        cell2.setPadding(5);

        cell3.setBorder(Rectangle.BOX);
        cell3.setPadding(5);

        cell4.setBorder(Rectangle.BOX);
        cell4.setPadding(5);

        cell5.setBorder(Rectangle.BOX);
        cell5.setPadding(5);

        cell1.setBackgroundColor(BaseColor.ORANGE);
        cell2.setBackgroundColor(BaseColor.ORANGE);
        cell3.setBackgroundColor(BaseColor.ORANGE);
        cell4.setBackgroundColor(BaseColor.ORANGE);
        cell5.setBackgroundColor(BaseColor.ORANGE);

        /*//Table values*//**//*
    cell5 = new PdfPCell(new Phrase(b));
    cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
    cell5.setBorder(Rectangle.NO_BORDER);
    cell5.setPadding(5);*/
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        //=================================================================
        total=0.00;
        Cursor c= databaseHelper.show_ExcelData(Integer.parseInt(str_sp_bill_no));
        if (c.getCount() != 0) {
            c.moveToFirst();
            do {

                HashMap<String, String> map = new HashMap<String, String>();

                String id  =c.getString(0);
                String name  =c.getString(2);
                String rate  =c.getString(3);
                String qty  =c.getString(4);
                String val  =c.getString(5);
                String srno  =c.getString(6);
                String dt  =c.getString(7);
                String blno  =c.getString(8);

                total=total+Double.parseDouble(c.getString(5));


                cell6 = new PdfPCell(new Phrase(id));
                cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell6.setBorder(Rectangle.BOX);
                cell6.setPadding(5);

                cell7 = new PdfPCell(new Phrase(name));
                cell7.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell7.setBorder(Rectangle.BOX);
                cell7.setPadding(5);

                cell8 = new PdfPCell(new Phrase(rate));
                cell8.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell8.setBorder(Rectangle.BOX);
                cell8.setPadding(5);

                cell9 = new PdfPCell(new Phrase(qty));
                cell9.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell9.setBorder(Rectangle.BOX);
                cell9.setPadding(5);

                cell10 = new PdfPCell(new Phrase(val));
                cell10.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell10.setBorder(Rectangle.BOX);
                cell10.setPadding(5);

                table.addCell(cell6);
                table.addCell(cell7);
                table.addCell(cell8);
                table.addCell(cell9);
                table.addCell(cell10);

            /*table.addCell(cell11);
			table.addCell(cell12);*/

                // add table into document
            } while (c.moveToNext());

        }

        document.add(table);
        c.close();
        Paragraph p = new Paragraph();
        p.setFont(catFont);
        p.add("                                                         Total : "+total);
        p.add("\n");

        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);

        //===================================================================
        // Create new Page in PDF
        document.newPage();
        //Toast.makeText(this, "PDF File is Created.", Toast.LENGTH_LONG).show();
    }
    //===================================================================
    public void excel_permission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                exportDatabase();
                // restore_back_up();
            } else {
                requestPermission(); // Code for permission
            }
        } else {

            exportDatabase();
            // Toast.makeText(Scan_Master_Reports.this, "Below 23 API Oriented Device....", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean exportDatabase() {
        // DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        } else {
            //We use the Download directory for saving our .csv file.
            // File exportDir = Environment.getExternalStorageDirectory();
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;
            try {
                file = new File(exportDir, "Menu.csv");
                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));


                // Cursor curCSV = getApplicationContext().getContentResolver().query(Provider.CONTENT_URI3, null, null, null, null);
                // Cursor curCSV=getApplicationContext().getContentResolver().query(Provider.CONTENT_URI3,null,null,null,null);
                // Cursor curCSV=getActivity().getContentResolver().query(Provider.CONTENT_URI3,null,null,null,null);
                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
                printWriter.println("___________________________Menu Report_____________________________________________");
              /*  printWriter.println("Event Name:   "+event_name);
                printWriter.println("Event Date:   "+event_date);
                printWriter.println("Volunteer Name:  "+vlnte_name);
                printWriter.println("Check point Name:  "+chk_pnt_name);
                printWriter.println("Check Point Type:  "+chk_pt_type);*/
                printWriter.println("___________________________________________________________________________________");
                printWriter.println("Id,Item Name,Rate,Qty,Value,DocSrno,Date,Bill No");
                printWriter.println("___________________________________________________________________________________");
                databaseHelper=new DatabaseHelper(this);
              //  SALE_CODE,ITEM_CODE ,ITEM_RATE ,QTY ,ITEM_VALUE , DOC_SRNO,DOC_DT  ,BILL_NO
                Cursor c= databaseHelper.show_ExcelData(Integer.parseInt(str_sp_bill_no));
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    do {
                        String id  =c.getString(0);
                        String code  =c.getString(2);
                        String rate  =c.getString(3);
                        String qty  =c.getString(4);
                        String val  =c.getString(5);
                        String srno  =c.getString(6);
                        String dt  =c.getString(7);
                        String blno  =c.getString(8);
                        // String record = df.format(new Date(date)) + "," + item + "," + amount.toString() + "," + currency;
                        // String record =  id + "," + name +","+number+","+date+","+mlk_typ+","+mlk_lit+","+mlt_total+","+tot_lit+","+tot_amt;
                        String record =  id + "," + code +","+rate+","+qty+","+val+","+srno+","+dt+","+blno;

                        printWriter.println(record); //write the record in the .csv file
                    } while (c.moveToNext());
                    printWriter.println("\n");
                    printWriter.println("____________________________________________________________________________________");


                }

                c.close();
            } catch (Exception exc) {
                //if there are any exceptions, return false
                Toast.makeText(getApplicationContext(), "Exception occure"+exc, Toast.LENGTH_SHORT).show();
                return false;
            } finally {
                if (printWriter != null) printWriter.close();
            }

           /* new SweetAlertDialog(Event_Attendance.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success..!")
                    .setContentText("Report Exported into Excel Successfully..!!!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            show_alert();
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .show();*/

            Toast.makeText(getApplicationContext(),"file exported to .csv file", Toast.LENGTH_SHORT).show();
            //If there are no errors, return true.
            return true;
        }
    }
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

        if (ActivityCompat.shouldShowRequestPermissionRationale(TableBill_Enquiry.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)&& ActivityCompat.shouldShowRequestPermissionRationale(TableBill_Enquiry.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getApplicationContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(TableBill_Enquiry.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
}
