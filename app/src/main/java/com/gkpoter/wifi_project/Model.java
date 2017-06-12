package com.gkpoter.wifi_project;

import java.util.List;

/**
 * Created by "GKpoter" on 2017/6/10.
 */

public class Model {
    private List<WifiInfo> data;

    public List<WifiInfo> getData() {
        return data;
    }

    public void setData(List<WifiInfo> data) {
        this.data = data;
    }

    public void addData(List<WifiInfo> data) {
        this.data.addAll(data);
    }

    public static class WifiInfo {
        /**
         * wifi_mac :
         * wifi_str :
         * wifi_signal :
         * wifi_frequency :
         */

        private String wifi_mac;
        private String wifi_str;
        private String wifi_signal;
        private String wifi_frequency;

        public String getWifi_mac() {
            return wifi_mac;
        }

        public void setWifi_mac(String wifi_mac) {
            this.wifi_mac = wifi_mac;
        }

        public String getWifi_str() {
            return wifi_str;
        }

        public void setWifi_str(String wifi_str) {
            this.wifi_str = wifi_str;
        }

        public String getWifi_signal() {
            return wifi_signal;
        }

        public void setWifi_signal(String wifi_signal) {
            this.wifi_signal = wifi_signal;
        }

        public String getWifi_frequency() {
            return wifi_frequency;
        }

        public void setWifi_frequency(String wifi_frequency) {
            this.wifi_frequency = wifi_frequency;
        }
    }
}
