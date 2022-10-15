package com.sonkamble.invoice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sonkamble.invoice.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class Item_Reports extends AppCompatActivity {

    String item_nm,item_id,item_rate,item_fid;
    ConnectionClass connectionClass;
    ProgressBar pbbar;
    Toolbar toolbar;
    String msg="";
    String query="";
    int sale_item_code=0;
    TextView txt_name,txt_rate;
    Button btn_operation_cancle,btn_update,btn_del;
    AlertDialog dialog;
    private DatabaseHelper databaseHelper;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_group_report);
      //  searchView = (SearchView) findViewById(R.id.report_searchView);

        pbbar = (ProgressBar) findViewById(R.id.pgb);
        connectionClass = new ConnectionClass();

        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(Item_Reports.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(Item_Reports.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        //------------------------------------------------------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        ImageView toolbar_img = (ImageView) toolbar.findViewById(R.id.toolbar_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Item List");
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

        sale_report();

    }

    public void sale_report() {

        try {
            pbbar.setVisibility(View.VISIBLE);
            databaseHelper=new DatabaseHelper(this);
            menu_card_arryList.clear();
            Cursor c= databaseHelper.show_ItemData();
            if (c.getCount() != 0) {
                c.moveToFirst();
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    String id  =c.getString(0);
                    String mid  =c.getString(1);
                    String desc  =c.getString(2);
                    String rate  =c.getString(3);

                    map.put("ITEM_CODE", id );
                    map.put("MENU_CODE", mid );
                    map.put("ITEM_DESC", desc );
                    map.put("RATE", rate );
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

    public class atnds_recyclerAdapter extends RecyclerView.Adapter<Item_Reports.atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Item_Reports.atnds_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_menu, parent, false);
            Item_Reports.atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Item_Reports.atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {


            holder.list_nm.setText(attendance_list.get(position).get("ITEM_DESC"));
            holder.list_rt.setText(attendance_list.get(position).get("RATE"));

            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item_nm=attendance_list.get(position).get("ITEM_DESC");
                    item_rate=attendance_list.get(position).get("RATE");
                    item_id=attendance_list.get(position).get("ITEM_CODE");
                    item_fid=attendance_list.get(position).get("MENU_CODE");
                    item_popup_form();
                }
            });
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_nm,list_rt;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                 this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_nm= (TextView) itemView.findViewById(R.id.list_d1);
                this.list_rt= (TextView) itemView.findViewById(R.id.list_d2);

            }
        }
    }

    public void item_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.item_update_popup, null);

        txt_name = (EditText) alertLayout.findViewById(R.id.txt_name);
        txt_name.setText(item_nm);

        txt_rate = (EditText) alertLayout.findViewById(R.id.txt_rate);
        txt_rate.setText(item_rate);

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
                msg="Updated Successfully";
                upate();
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(Item_Reports.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();

    }
    public void upate() {

        try {
            pbbar.setVisibility(View.VISIBLE);
            databaseHelper.update_ItemData(Integer.parseInt(item_id),item_nm,item_rate);
            refresh();
            pbbar.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }
    public void delete() {

        try {
            pbbar.setVisibility(View.VISIBLE);
            databaseHelper.delete_ItemData(item_id);
            refresh();
            // sp_data  = new ArrayList<Map<String, String>>();
           /*
                PreparedStatement   ps1 = con.prepareStatement("SELECT DISTINCT ITEM_CODE FROM SALEITEM where ITEM_CODE="+item_id+"");
                ResultSet s = ps1.executeQuery();
                while (s.next()) {
                    sale_item_code = Integer.parseInt(s.getString("ITEM_CODE"));
                }
                if(sale_item_code==0)
                {
                  //  PreparedStatement ps = con.prepareStatement("delete from ITEMMAST where ITEM_CODE="+item_id+"");
                    ps.executeUpdate();
                    refresh();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Item_Reports.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.warn);
                    builder.setMessage("Can Not Delete This Item? Found In SaleItem Table.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                            dialog.dismiss();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();

                }*/


            pbbar.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }
    public  void refresh()
    {
        Intent i=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
        Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        Intent i=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}
