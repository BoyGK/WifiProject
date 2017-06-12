package com.gkpoter.wifi_project;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WIFI = 1;
    private boolean THREAD_KEY = true;
    Button button;
    ListView listView;

    WifiManager wifiManager = null;
    WifiAdapter adapter;
    List<ScanResult> scanResults = null;

    Model model = null;

    Thread thread;

    FinishScan finishScan = new FinishScan() {
        @Override
        public void success() {
            listView.setAdapter(adapter);
            upWifiInformation(scanResults);
        }

        @Override
        public void update() {
            adapter.notifyDataSetChanged();
            upWifiInformation(scanResults);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取WiFi管理
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        model = new Model();

        button = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.listview);

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (THREAD_KEY) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                testWifi();
                                //Log.i("information", "continue");
                            }
                        });
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    THREAD_KEY = false;
                    e.printStackTrace();
                }
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeLocation();
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
                if (!thread.isAlive()) {
                    thread.start();
                }
            }
        });

    }

    //输入相对坐标
    public void writeLocation() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_content, null);
        final EditText x = (EditText) textEntryView.findViewById(R.id.editTextName);
        final EditText y = (EditText) textEntryView.findViewById(R.id.editTextNum);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(MainActivity.this);
        ad1.setTitle("确定采集位置:");
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                if (!"".equals(x.getText() + "") && !"".equals(y.getText() + "")) {
                    Log.i("information", "x:" + x.getText() + ",y:" + y.getText());
                }
            }
        });
        ad1.setNegativeButton("否", null);
        ad1.show();
    }

    public void testWifi() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_WIFI);
        } else {
            wifiManager.startScan();
            if (scanResults != null) {
                scanResults = wifiManager.getScanResults();//搜索到的设备列表
                if (scanResults != null) {
                    adapter.setScanResults(scanResults);
                    finishScan.update();
                }
            } else {
                scanResults = wifiManager.getScanResults();//搜索到的设备列表
                if (scanResults != null) {
                    adapter = new WifiAdapter(scanResults, getApplicationContext(), wifiManager);
                    finishScan.success();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_WIFI) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                wifiManager.startScan();
                if (scanResults != null) {
                    scanResults = wifiManager.getScanResults();//搜索到的设备列表
                    if (scanResults != null) {
                        adapter.setScanResults(scanResults);
                        finishScan.update();
                    }
                } else {
                    scanResults = wifiManager.getScanResults();//搜索到的设备列表
                    if (scanResults != null) {
                        adapter = new WifiAdapter(scanResults, getApplicationContext(), wifiManager);
                        finishScan.success();
                    }
                }
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "权限获取失败", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void upWifiInformation(List<ScanResult> scanRs) {
        List<Model.WifiInfo> wifiInfos = new ArrayList<>();
        for (ScanResult scanResult : scanRs) {
            Model.WifiInfo wifiInfo = new Model.WifiInfo();
            wifiInfo.setWifi_mac(scanResult.BSSID);
            wifiInfo.setWifi_str(wifiManager.calculateSignalLevel(scanResult.level, 5) + "");
            wifiInfo.setWifi_signal(scanResult.level + "");
            wifiInfo.setWifi_frequency(scanResult.frequency + "");
            wifiInfos.add(wifiInfo);
        }

        //减少请求次数，集中数据
        if (model.getData() != null && model.getData().size() >= 100) {
            Log.i("information", new Gson().toJson(model));
            //doUpWifiInformation(model);
            model = new Model();
        } else {
            if (model.getData() != null) {
                model.addData(wifiInfos);
            } else {
                model.setData(wifiInfos);
            }
        }
    }

    private void doUpWifiInformation(Model model) {
        RequestParams params = new RequestParams();
        params.put("json", new Gson().toJson(model));
        HttpRequest.post(getApplicationContext(), "", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) {
                    Log.i("information", new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                THREAD_KEY = false;
            }
        });
    }

    private interface FinishScan {
        //获取成功
        void success();

        //更新
        void update();
    }

    @Override
    protected void onDestroy() {
        THREAD_KEY = false;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        THREAD_KEY = false;
        super.onBackPressed();
    }
}
