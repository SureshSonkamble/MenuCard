package com.sonkamble.invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {
    LinearLayout lin_table_bill,lin_add_fg ,lin_manage_fg,lin_add_item,lin_manage_item,lin_add_cottage,lin_manage_cottage;
    String user,str_waiter,tab_user_code;
    SessionManager sessionManager;
    SharedPreferences sp;
    int m_TAB_CODE;
    GridLayout grid_menu;
    Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        SharedPreferences s = getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        str_waiter = s.getString("tab_user_name", "");
        tab_user_code = s.getString("tab_user_code", "");
        m_TAB_CODE=Integer.parseInt(tab_user_code);

        //--------------------------------------------------------------------------------
        sessionManager = new SessionManager(this);
        //------------------------User Session------------------------------------------
        Bundle b=getIntent().getExtras();
        try
        {
            user=b.getString("email");
        }
        catch (Exception e) { }
        if(user==null)
        {
            // Toast.makeText(getApplicationContext(),"User Id Null...",Toast.LENGTH_LONG).show();
        }
        else
        {
            sp=this.getSharedPreferences("PI", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("email",user);
            editor.commit();
        }

        grid_menu=(GridLayout)findViewById(R.id.grid_menu);
        if(tab_user_code.equals("0"))
        {
            grid_menu.setVisibility(View.INVISIBLE);
        }
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView txt_user = (TextView) toolbar.findViewById(R.id.txt_user);//title
        ImageView toolbar_img = (ImageView) toolbar.findViewById(R.id.img_logout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("My Dashboard");
        txt_user.setText(str_waiter);
        toolbar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.exit);
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.logoutUser();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        lin_table_bill=(LinearLayout)findViewById(R.id.lin_table_bill);

        lin_add_fg=(LinearLayout)findViewById(R.id.lin_add_fg);
        lin_manage_fg=(LinearLayout)findViewById(R.id.lin_manage_fg);
        lin_add_item=(LinearLayout)findViewById(R.id.lin_add_item);
        lin_manage_item=(LinearLayout)findViewById(R.id.lin_manage_item);
        lin_add_fg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),Add_Food_Group.class);
                startActivity(i);
                finish();
            }
        });
        lin_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),Add_Item.class);
                startActivity(i);
                finish();
            }
        });
        lin_manage_fg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),Food_Group_Reports.class);
                startActivity(i);
                finish();
            }
        });
        lin_manage_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),Item_Reports.class);
                startActivity(i);
                finish();
            }
        });

        lin_table_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),Table_Bill.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void check_permission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                back_up();
               /* if(action.equals("r"))
                {
                    restore_back_up();
                }
                else {
                    back_up();
                }*/
                // exportDatabase();

            } else {
                requestPermission(); // Code for permission
            }
        } else {
            back_up();
           /* if(action.equals("r"))
            {
                restore_back_up();
            }
            else {
                back_up();
            }*/
            // backup_database();
            //  exportDatabase();
            // Toast.makeText(Scan_Master_Reports.this, "Below 23 API Oriented Device....", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getApplicationContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    void back_up()
    {
        Toast.makeText(getApplicationContext(), "Backup Start", Toast.LENGTH_SHORT).show();
        try {
            try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String currentDBPath = "//data/com.sonkamble.offline_menucard.database/databases/menucard.db";
                    String backupDBPath = "menucard.db";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        Toast.makeText(getApplicationContext(), "Backup is successful", Toast.LENGTH_SHORT).show();
                      /*  AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Alert");
                        builder.setIcon(R.drawable.check);
                        builder.setMessage("Backup is successful..!");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.setCancelable(false);
                        builder.show();*/
                    }
                }
            } catch (Exception e) {
            }
        }catch (Exception e){}
    }
    void restore_back_up()
    {
        Toast.makeText(getApplicationContext(), "Restoring Database..", Toast.LENGTH_SHORT).show();
        try {
            try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String currentDBPath = "//data/com.sonkamble.offline_online/databases/contacts.db";
                    String backupDBPath = "contacts.db";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(backupDB).getChannel();
                        FileChannel dst = new FileOutputStream(currentDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        Toast.makeText(getApplicationContext(), "Restored successful.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
            }
        }catch (Exception e){}
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            check_permission();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {

                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}