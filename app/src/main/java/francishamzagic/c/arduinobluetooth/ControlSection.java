package francishamzagic.c.arduinobluetooth;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class ControlSection extends AppCompatActivity {

    // This class will list paired devices able to connect
    Button led1;
    Button led2;
    Button led3;
    String address = null;
    private ProgressDialog progress;
    Button disconnect;

    BluetoothAdapter btAdapter = null;
    BluetoothSocket btSocket = null;

    private boolean isBtConnected = false;
    static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_section);

        Intent getAddress = getIntent();
        address = getAddress.getStringExtra(SelectDevices.EXTRA_ADDRESS);

        led1 = (Button) findViewById(R.id.btn_led1);
        led2 = (Button) findViewById(R.id.btn_led2);
        led3 = (Button) findViewById(R.id.btn_led3);
        disconnect = (Button) findViewById(R.id.btn_disc);

        //put here the execute command to connect to BT//
        new BTConnect().execute();

        led1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(led1.getText() == "Red Led Off") {
                    led1.setText("Red Led On");
                } else {
                    led1.setText("Red Led Off");
                }

                toggleLed("A");
            }
        });

        led2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(led2.getText() == "Yellow Led Off") {
                    led2.setText("Yellow Led On");
                } else {
                    led2.setText("Yellow Led Off");
                }
                toggleLed("B");
            }
        });

        led3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(led3.getText() == "Green Led Off") {
                    led3.setText("Green Led On");
                } else {
                    led3.setText("Green Led Off");
                }
                toggleLed("C");
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });
    }


    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void toggleLed(String data)
    {
        if(btSocket != null) {
            try {
                btSocket.getOutputStream().write(data.toString().getBytes());

            } catch (IOException e) {
                showToast(e.getMessage());
            }


        }
    }

    private void Disconnect() {
        if(btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e ){
                showToast(e.getMessage());
            }
        }
        finish();
    }

    private class BTConnect extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ControlSection.this, "Connecting...", "Please wait");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(btSocket == null || !isBtConnected) {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice currentDevice = btAdapter.getRemoteDevice(address);
                    btSocket = currentDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (Exception e) {
                ConnectSuccess = false;
                showToast("Could not connect");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(!ConnectSuccess) {
                showToast("Connection failed. Please try again.");
                finish();
            } else {
                showToast("Connected");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

}
