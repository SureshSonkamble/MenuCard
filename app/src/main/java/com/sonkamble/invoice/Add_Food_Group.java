package com.sonkamble.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sonkamble.invoice.database.DatabaseHelper;

public class Add_Food_Group extends AppCompatActivity {
    EditText txt_fg_name;
    Button btn_add_fg,btn_manage_fg;
    ProgressBar pbbar;
    ConnectionClass connectionClass;
    String fgnm;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food_group);
        databaseHelper=new DatabaseHelper(this);
        connectionClass = new ConnectionClass();
        txt_fg_name=(EditText)findViewById(R.id.txt_fg_name);
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        btn_manage_fg=(Button) findViewById(R.id.btn_manage_fg);
        btn_manage_fg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),Food_Group_Reports.class);
                startActivity(i);
                finish();
            }
        });
        btn_add_fg=(Button) findViewById(R.id.btn_add_fg);
        btn_add_fg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    fgnm=txt_fg_name.getText().toString();
                    if(txt_fg_name.getText().toString().equals(""))
                    {    Toast.makeText(Add_Food_Group.this, "Value Can Not Be Null", Toast.LENGTH_SHORT).show();
                    }
                    else {

                           boolean result = databaseHelper.insert_MenuData(fgnm);
                            if (result) {
                                refresh();

                            } else
                                Toast.makeText(getApplicationContext(), "Error..", Toast.LENGTH_LONG).show();
                    }


                }catch (Exception e){
                    Toast.makeText(Add_Food_Group.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public  void refresh()
    {
        Intent i=new Intent(getApplicationContext(), Add_Food_Group.class);
        startActivity(i);
        finish();
        Toast.makeText(this, "Success.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}