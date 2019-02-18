package com.example.storescontrol.view.declaration;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.storescontrol.R;
import com.example.storescontrol.view.PrintActivity;
import com.qr285.sdk.OnPrinterListener;
import com.qr285.sdk.PrinterPort;

import java.util.Set;

public class ReportprintActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter = null;
    private PrinterPort printerPort;
    boolean isRegister=false;
    View viewPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportprint);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Button buttonPrint=findViewById(R.id.b_print);
        buttonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initbluetooth();
                printeData();
            }
        });
        viewPrint=findViewById(R.id.ll_form);

    }
    public void printeData() {
        viewPrint.setDrawingCacheEnabled(true);
        viewPrint.buildDrawingCache();
        Bitmap bitmap=Bitmap.createBitmap(viewPrint.getDrawingCache());
        viewPrint.destroyDrawingCache();

        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);
        matrix.postRotate(90);
        Bitmap bitmapnew=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        final Bitmap bmp = bitmapnew;

        new Thread(new Runnable() {
            @Override
            public void run() {
                printerPort.setDensity(0x02, 16);
                printerPort.printBitmap(bmp);
                //  printerPort.adjustPosition(0, 240);
                printerPort.printerLocation(0x20,10);
                if (printerPort.getSendResult(10000).equals("OK")) {
                    ReportprintActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(ReportprintActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    ReportprintActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(ReportprintActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }
    private void initbluetooth() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        isRegister=true;
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        isRegister=true;
        this.registerReceiver(mReceiver, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //If the Bluetooth adapter is not supported,programmer is over
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
        printerPort = new PrinterPort(ReportprintActivity.this, new OnPrinterListener() {
            @Override
            public void onConnected() {
                Toast.makeText(ReportprintActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectedFail() {

            }

            @Override
            public void ondisConnected() {
                Toast.makeText(ReportprintActivity.this, "连接断开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void ondisConnectedFail() {

            }

            @Override
            public void onAbnormaldisconnection() {
                Toast.makeText(ReportprintActivity.this, "打印机异常断开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onsateOFF() {
                Toast.makeText(ReportprintActivity.this, "蓝牙关闭", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onsateOn() {
                Toast.makeText(ReportprintActivity.this, "蓝牙开启", Toast.LENGTH_SHORT).show();
            }
        });
        Set<BluetoothDevice> pairedDevices =mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size()>0){
            for (BluetoothDevice device : pairedDevices) {
                Log.i("device-bind",device.getName());
                if(device.getName().contains("QR-285A")){
                    mBluetoothAdapter.cancelDiscovery();
                    printerPort.connect(device.getAddress());

                }
            }

        }else {
            Log.i("device-","no one");
        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Log.i("device-unbind",device.getName());
                    //  mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter!= null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if(printerPort!=null) {
            printerPort.disconnect();
        }
        // Unregister broadcast listeners
        if(isRegister) {
            isRegister=false;
            this.unregisterReceiver(mReceiver);
        }

    }

}
