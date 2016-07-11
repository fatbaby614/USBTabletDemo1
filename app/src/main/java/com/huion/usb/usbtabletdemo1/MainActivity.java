package com.huion.usb.usbtabletdemo1;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private UsbManager m_usbManager;
    private UsbDevice m_usbDevice;
    private Boolean m_bConnectStatus = false;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private PendingIntent pendingIntent;
    private ReadUSBThread m_USBReadThread;
    private LinearLayout m_TabletCanvas;
    public TabletView m_TabletView;
    Button m_btnConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_TabletCanvas = (LinearLayout)findViewById(R.id.TabletCanvas);
        m_TabletView = new TabletView(this);
        m_TabletCanvas.addView(m_TabletView);

        m_btnConnect = (Button) findViewById(R.id.buttonConnect);
        m_btnConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (m_bConnectStatus)
                {
                    m_bConnectStatus = false;
                    m_btnConnect.setText("Connect");
                    m_USBReadThread.stopThread();
                }
                else {
                    boolean forceClaim = true;
                    m_usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

                    HashMap<String, UsbDevice> deviceList = m_usbManager.getDeviceList();
                    Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                    while (deviceIterator.hasNext()) {

                        UsbDevice device = deviceIterator.next();
                        //your code
                        Log.i(TAG, device.getDeviceName());
                        int pid = device.getProductId();
                        int vid = device.getVendorId();

                        if ((vid == 9580) && (pid == 110)) {
                            Log.i(TAG, String.format("Vid:0x%4x,Pid:0x%4x", vid, pid));
                            m_usbDevice = device;
                            if (m_usbManager.hasPermission(m_usbDevice)) {
                                Log.i(TAG, String.format("USB permission got"));
                            } else {
                                //没有权限询问用户是否授予权限
                                Log.i(TAG, String.format("USB permission is requesting"));
                                m_usbManager.requestPermission(m_usbDevice, pendingIntent); //该代码执行后，系统弹出一个对话框，
                                //询问用户是否授予程序操作USB设备的权限
                            }
                            m_bConnectStatus = true;
                            m_btnConnect.setText("Disconnect");
                            //new Thread(new ReadUSBThread()).start();
                            m_USBReadThread = new ReadUSBThread();
                            m_USBReadThread.start();

                        }
//                    UsbInterface intf = device.getInterface(0);
//                    UsbEndpoint endpoint = intf.getEndpoint(0);
//                    UsbDeviceConnection connection = m_usbManager.openDevice(device);
//                    connection.claimInterface(intf, forceClaim);
                    }
                    usbConnection();
                }
            }
        });

    }

    private void usbConnection(){
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        registerReceiver(mUsbAttachReceiver,filter);

        filter = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbDetachReceiver,filter);

        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

                Toast.makeText(context, "push in", Toast.LENGTH_LONG).show();
            }
        }
    };

    BroadcastReceiver mUsbDetachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(context, "pull out", Toast.LENGTH_LONG).show();
                UsbDevice device = (UsbDevice) intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE);

                if (device != null) {

                }
            }
        }
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e( "action", action);

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                }
            }
        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mUsbDetachReceiver);
        unregisterReceiver(mUsbAttachReceiver);
        unregisterReceiver(mUsbReceiver);
    }

    class ReadUSBThread extends Thread {
        private boolean runstatus = true;
        public void stopThread( ) {
            this.runstatus = false;
        }
        @Override
       public void run() {
            super.run();
            Log.i(TAG,String.format("USB Read Thread Start"));


            UsbInterface usbInterface = m_usbDevice.getInterface(0);
            //USBEndpoint is the endpoint for read & write
            UsbEndpoint inEndpoint = usbInterface.getEndpoint(0);  //read endpoint
            UsbEndpoint outEndpoint = usbInterface.getEndpoint(1); //write endpoint
            UsbDeviceConnection connection = m_usbManager.openDevice(m_usbDevice);
            connection.claimInterface(usbInterface, true);
            byte[] usbbuf = new byte[12];
            try {
                while (runstatus) {
                    int ret = connection.bulkTransfer(inEndpoint, usbbuf, usbbuf.length, 100);
                    if (ret>0) {
                        String strUsbData = String.format("%x %x %x %x %x %x %x %x",
                                usbbuf[0], usbbuf[1], usbbuf[2], usbbuf[3], usbbuf[4], usbbuf[5], usbbuf[6], usbbuf[7]);
                        Log.i(TAG, String.format("get data:%d", ret) + strUsbData);
                        m_TabletView.clear();
                    }
                }
                Log.i(TAG, String.format("USB Read Thread Exit"));
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
