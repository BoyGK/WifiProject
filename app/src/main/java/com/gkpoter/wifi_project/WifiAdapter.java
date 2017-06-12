package com.gkpoter.wifi_project;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by "GKpoter" on 2017/6/10.
 */

public class WifiAdapter extends BaseAdapter {

    private List<ScanResult> scanResults;
    private Context context;
    private WifiManager wifiManager;

    public WifiAdapter(List<ScanResult> scanResults, Context context, WifiManager wifiManager) {
        this.scanResults = scanResults;
        this.context = context;
        this.wifiManager = wifiManager;
    }

    public void setScanResults(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    @Override
    public int getCount() {
        return scanResults.size();
    }

    @Override
    public Object getItem(int position) {
        return scanResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.wifi_list_item, null);
        TextView wifi_name = (TextView) convertView.findViewById(R.id.wifi_name);
        TextView wifi_str = (TextView) convertView.findViewById(R.id.wifi_str);
        TextView wifi_signal = (TextView) convertView.findViewById(R.id.wifi_signal);
        TextView wifi_frequency = (TextView) convertView.findViewById(R.id.wifi_frequency);
        TextView wifi_mac = (TextView) convertView.findViewById(R.id.wifi_mac);
        wifi_name.setText(scanResults.get(position).SSID);
        wifi_str.setText(wifiManager.calculateSignalLevel(scanResults.get(position).level, 5) + "");
        wifi_signal.setText(scanResults.get(position).level + "dBm");
        wifi_frequency.setText(scanResults.get(position).frequency + "MHz");
        wifi_mac.setText(scanResults.get(position).BSSID);
        return convertView;
    }
}
