package francishamzagic.c.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class SelectDevices extends AppCompatActivity {

    ListView deviceList;
    Button showDevices;
    public static String EXTRA_ADDRESS = "device_address";

    //create a BT adapter, a devices array and a device address variable
    BluetoothAdapter btAdapter = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String DEVICE_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_devices);

        deviceList = (ListView) findViewById(R.id.devices);
        showDevices = (Button) findViewById(R.id.dev_btn);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) {
            showToast("Bluetooth device not available");
        } else {
            if(!btAdapter.isEnabled()) {
                Intent turnBTOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTOn, 1);
            }
        }

        showDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
    }

    private void pairedDevicesList() {
        pairedDevices = btAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        if(pairedDevices.size() > 0) {
            for(BluetoothDevice bt: pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            showToast("No paired devices found");
        }
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);

        deviceList.setOnItemClickListener(handleListener);
    }

    private AdapterView.OnItemClickListener handleListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent i = new Intent(SelectDevices.this, ControlSection.class);
            i.putExtra(EXTRA_ADDRESS, address);
            startActivity(i);
        }
    };
}
