package com.example.raspberrymonitor.Network;

import java.util.Map;

public class NetworkInfoResponse {
    private long total_bytes_sent;
    private long total_bytes_recv;
    private Map<String, InterfaceStats> interfaces;

    // Getter e Setter
    public long getTotal_bytes_sent() {
        return total_bytes_sent;
    }

    public void setTotal_bytes_sent(long total_bytes_sent) {
        this.total_bytes_sent = total_bytes_sent;
    }

    public long getTotal_bytes_recv() {
        return total_bytes_recv;
    }

    public void setTotal_bytes_recv(long total_bytes_recv) {
        this.total_bytes_recv = total_bytes_recv;
    }

    public Map<String, InterfaceStats> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Map<String, InterfaceStats> interfaces) {
        this.interfaces = interfaces;
    }

    public static class InterfaceStats {
        private long recv;
        private long sent;

        // Getter e Setter
        public long getRecv() {
            return recv;
        }

        public void setRecv(long recv) {
            this.recv = recv;
        }

        public long getSent() {
            return sent;
        }

        public void setSent(long sent) {
            this.sent = sent;
        }
    }
}
