package com.sonkamble.invoice;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sonkamble.invoice.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Add_Item extends AppCompatActivity {
    Spinner sp_fg;
    String str_item_name,str_item_rate,str_sp_fg,str_sp_fg_id;
    EditText txt_item_name,txt_item_rate;
    Button btn_add_item,btn_manage_item;
    ProgressBar pbbar;
    int code,rate;
    TransparentProgressDialog pd;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        databaseHelper=new DatabaseHelper(this);
        sp_fg=(Spinner)findViewById(R.id.sp_fg);
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(Add_Item.this, R.drawable.load);
        txt_item_name=(EditText)findViewById(R.id.txt_item_name);
        txt_item_rate=(EditText)findViewById(R.id.txt_item_rate);


        btn_manage_item=(Button)findViewById(R.id.btn_manage_item);
        btn_manage_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),Item_Reports.class);
                startActivity(i);
                 finish();
            }
        });
        btn_add_item=(Button)findViewById(R.id.btn_add_item);
        btn_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_item_name=txt_item_name.getText().toString();
                str_item_rate=txt_item_rate.getText().toString();

                try{
                    if(txt_item_name.getText().toString().equals("")||txt_item_rate.getText().toString().equals(""))
                    {    Toast.makeText(Add_Item.this, "Value Can Not Be Null", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        code=Integer.parseInt(str_sp_fg_id);
                        rate=Integer.parseInt(str_item_rate);
                        boolean result = databaseHelper.insert_ItemData(code,str_item_name,rate);
                        if (result) {
                            refresh();
                        } else
                            Toast.makeText(getApplicationContext(), "Error..", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    Toast.makeText(Add_Item.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        pd.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                try {
                    new load_food_group().execute();

                } catch (Exception e) {
                }
                pd.dismiss();
            }
        }, 2000);

    }

    public  void refresh()
    {
        txt_item_name.setText("");
        txt_item_rate.setText("");
        Toast.makeText(this, "Success.", Toast.LENGTH_LONG).show();
    }
    public class load_food_group extends AsyncTask<String, String, String> {
        List<Map<String, String>> hw_data = new ArrayList<Map<String, String>>();
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                databaseHelper=new DatabaseHelper(getApplicationContext());
                hw_data.clear();
                Cursor c= databaseHelper.show_MenuData();
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();
                        String id  =c.getString(0);
                        String name  =c.getString(1);

                        map.put("B", id );
                        map.put("A", name );
                        hw_data.add(map);
                    } while (c.moveToNext());

                }

            } catch (Exception e) {
              //  Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getApplicationContext(), hw_data, R.layout.spin, from, views);
            sp_fg.setAdapter(spnr_data);
            sp_fg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    str_sp_fg = (String) obj.get("A");
                    str_sp_fg_id = (String) obj.get("B");
                    Toast.makeText(Add_Item.this, ""+str_sp_fg+"\n"+str_sp_fg_id, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}