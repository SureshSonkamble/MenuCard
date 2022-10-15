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

public class Food_Group_Reports extends AppCompatActivity {

    TextView txt_name;
    Button btn_operation_cancle,btn_update,btn_del;
    AlertDialog dialog;
    ConnectionClass connectionClass;
    ProgressBar pbbar;
    Toolbar toolbar;
    String fgnm,fgid;
    String query="";
    String msg="";
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
        layoutManager_pe = new LinearLayoutManager(Food_Group_Reports.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(Food_Group_Reports.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        //------------------------------------------------------------------------------------------
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        ImageView toolbar_img = (ImageView) toolbar.findViewById(R.id.toolbar_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Food Group List");
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
            databaseHelper=new DatabaseHelper(this);
            menu_card_arryList.clear();
            Cursor c= databaseHelper.show_MenuData();
            if (c.getCount() != 0) {
                c.moveToFirst();
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    String id  =c.getString(0);
                    String name  =c.getString(1);

                    map.put("MENU_CODE", id );
                    map.put("MENU_DESC", name );
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

    public class atnds_recyclerAdapter extends RecyclerView.Adapter<Food_Group_Reports.atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Food_Group_Reports.atnds_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_fg, parent, false);
            Food_Group_Reports.atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Food_Group_Reports.atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {


            holder.list_fg.setText(attendance_list.get(position).get("MENU_DESC"));


            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fgnm=attendance_list.get(position).get("MENU_DESC");
                    fgid=attendance_list.get(position).get("MENU_CODE");
                    fg_popup_form();
                }
            });
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_fg;
            LinearLayout lin;
            public Pex_ViewHolder(View itemView) {
                super(itemView);
                 this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_fg= (TextView) itemView.findViewById(R.id.list_d1);

            }
        }
    }
    public void fg_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.update_popup, null);

        txt_name = (EditText) alertLayout.findViewById(R.id.txt_name);
        txt_name.setText(fgnm);

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
                fgnm=txt_name.getText().toString();
                msg="Updated Successfully";
                upate();

                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(Food_Group_Reports.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();

    }

    public void upate() {

        try {
            pbbar.setVisibility(View.VISIBLE);
            databaseHelper.update_MenuData(Integer.parseInt(fgid),fgnm);
            refresh();
            pbbar.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }
    public void delete() {
        connectionClass = new ConnectionClass();
        try {
            pbbar.setVisibility(View.VISIBLE);
            databaseHelper.delete_MenuData(fgid);
            refresh();
            pbbar.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }
    public  void refresh()
    {
        Intent i=new Intent(getApplicationContext(), Food_Group_Reports.class);
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
