package com.sonkamble.invoice;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Suresh on 7/11/2020.
 */
public class ConnectionClass {

  //  String ip = "115.124.127.24:2222";
    String ip = "192.168.29.151:2222";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "MENUCARD";
    String un = "SA";
    String password = "PIMAGIC";
   /* String ip = "P3NWPLSK12SQL-v05.shr.prod.phx3.secureserver.net";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "ScarletIndia_yppunde";
    String un = "yppundeuser";
    String password = "klbS119@";
*/
    @SuppressLint("NewApi")

    public Connection CONN() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                .permitAll().build();

        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;

        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"

                    + "databaseName=" + db + ";user=" + un + ";password="

                    + password + ";";

            conn = DriverManager.getConnection(ConnURL);

        } catch (SQLException se) {

            Log.e("ERRO", se.getMessage());

        } catch (ClassNotFoundException e) {

            Log.e("ERRO", e.getMessage());

        } catch (Exception e) {

            Log.e("ERRO", e.getMessage());

        }

        return conn;

    }

}