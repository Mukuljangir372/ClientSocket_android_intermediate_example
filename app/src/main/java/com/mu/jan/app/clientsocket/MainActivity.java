package com.mu.jan.app.clientsocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private EditText ip_edit,msg_edit;
    private Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip_edit = (EditText)findViewById(R.id.ip_edittext);
        msg_edit = (EditText)findViewById(R.id.msg_editext);
        send_btn = (Button)findViewById(R.id.send_btn);

        getIPFromDevice();

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    sendDataToServer(ip_edit.getText().toString(),msg_edit.getText().toString());
                }catch (Exception e){}

            }
        });


    }
    private void sendDataToServer(final String ipAddress,final String msg){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(ipAddress,8080);
                    //send msg
                    OutputStream outputStream = s.getOutputStream();
                    PrintWriter printWriter = new PrintWriter(outputStream);
                    printWriter.println(msg);
                    printWriter.flush();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));

                    showInfoToUser(bufferedReader.readLine().toString());

                    s.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void getIPFromDevice(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ip_edit.setText(""+inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    private void showInfoToUser(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,msg+"",Toast.LENGTH_SHORT).show();

            }
        });

    }
}
