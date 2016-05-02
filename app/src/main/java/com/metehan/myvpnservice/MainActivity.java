package com.metehan.myvpnservice;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    Button btn1;
    DatagramSocketThread mDatagramSocketThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Datagram socket at port 8087
                mDatagramSocketThread = new DatagramSocketThread();
                mDatagramSocketThread.start();
                Intent intent = VpnService.prepare(getApplicationContext());
                if (intent != null) {
                    startActivityForResult(intent, 0);
                } else {
                    onActivityResult(0, RESULT_OK, null);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, MyVpnService.class);
            startService(intent);
        }
    }
}
