package com.sonkamble.invoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Login extends AppCompatActivity {

    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "WINESHOP";
    String un = "SA";
    String password = "PIMAGIC";
    Connection conn = null;
    String con_ipaddress,portnumber;
    String out_put = "";
    int login_yn;
    EditText edit_login_email,edit_login_pass;
    TextView btn_login;
    TransparentProgressDialog pd;
    ConnectionClass connectionClass;
    SessionManager sessionManager;
    SharedPreferences sp_pi_login,sp_edit;
    SharedPreferences.Editor editor_sp_pi_login;
    String tab_user_code,tab_user_name,imei;
    Toolbar toolbar;
    String lemail,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       /* SharedPreferences s = getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        tab_user_code = s.getString("tab_user_code", "");
        if(session.equals("0"))
        {
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
        }*/
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Login");
        toolbar_title.setTextColor(0xFFFFFFFF);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        sessionManager = new SessionManager(this);
        sp_pi_login = getSharedPreferences("PI", MODE_PRIVATE);
        editor_sp_pi_login = sp_pi_login.edit();
        if (sessionManager.isLoggedIn()) {
            String user=sp_pi_login.getString("email",null);
            Intent i=new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("email",user );
            startActivity(i);
            finish();
        }
        connectionClass = new ConnectionClass();
        edit_login_email = (EditText) findViewById(R.id.edit_login_email);
        edit_login_pass = (EditText) findViewById(R.id.edit_login_pass);
        pd = new TransparentProgressDialog(Login.this, R.drawable.load);
        btn_login = (TextView) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_login_email.getText().toString().length()==0) {
                    edit_login_email.setError("Input User name missing");
                    return;
                }
                else if (edit_login_pass.getText().toString().length()==0){
                    edit_login_pass.setError("Input Password missing");
                    return;
                }
                else {
                    lemail=edit_login_email.getText().toString();
                    pass=edit_login_pass.getText().toString();
                    if(lemail.equals("admin")&&pass.equals("12345")) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("TAB_DATA", MODE_PRIVATE); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("tab_user_name", "admin");
                        editor.putString("tab_user_code", "1");
                        editor.commit();
                        sessionManager.createLoginSession(edit_login_email.getText().toString(), edit_login_pass.getText().toString());
                        Toast.makeText(getApplicationContext(), "Login Successfull.", Toast.LENGTH_SHORT).show();
                        editor_sp_pi_login.putString("email", lemail);
                        editor_sp_pi_login.commit();
                        Intent i = new Intent(Login.this, MainActivity.class);
                        i.putExtra("email", lemail);
                        startActivity(i);
                        finish();
                    }
                   // new login().execute();
                }
            }
        });
    }

    public class login extends AsyncTask<String, String, String>
    {
        String z = "";
        Boolean isSuccess = false;
        String lemail = edit_login_email.getText().toString();
        String lpass = edit_login_pass.getText().toString();
        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            try
            {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                  //  String query = "select * from tabusermast WHERE TAB_DESC='"+lemail+"' AND PASS_WORD='"+lpass+"' AND TAB_MAC_ID='"+imei+"'";
                    String query = "select * from USERMAST WHERE USERNAME='"+lemail+"' AND PASS_WORD='"+lpass+"'";
                  //  String query = "select TABUSER_CODE,TABUSER_DESC,TABUSER_PASS_WORD from tabusermast WHERE TABUSER_DESC='"+lemail+"' AND TABUSER_PASS_WORD='"+lpass+"' AND IMEI_NO='"+imei+"'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                       // tab_user_code=rs.getString("TABUSER_CODE");
                       // tab_user_name=rs.getString("TABUSER_DESC");
                        tab_user_code=rs.getString("USER_CODE");
                        tab_user_name=rs.getString("USERNAME");
                        z = "Login successful";
                        isSuccess=true;
                        con.close();
                    }
                    else
                    {
                        z = "Invalid Credentials!";
                        isSuccess = false;
                    }
                }

            }catch(Exception e)
            {
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if(isSuccess==true)
            {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("TAB_DATA", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("tab_user_name",tab_user_name);
                editor.putString("tab_user_code",tab_user_code);
                editor.commit();
                sessionManager.createLoginSession(edit_login_email.getText().toString(), edit_login_pass.getText().toString());
                Toast.makeText(getApplicationContext(), "Login Successfull.", Toast.LENGTH_SHORT).show();
                editor_sp_pi_login.putString("email", lemail);
                editor_sp_pi_login.commit();
                Intent i = new Intent(Login.this, MainActivity.class);
                i.putExtra("email", lemail);
                startActivity(i);
                finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Invalid Credentials.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
    //========================Connection String===========================

}

