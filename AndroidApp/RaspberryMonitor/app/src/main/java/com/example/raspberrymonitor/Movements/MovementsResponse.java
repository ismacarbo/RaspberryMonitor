package com.example.raspberrymonitor.Movements;

import java.util.List;

public class MovementsResponse {
    private List<Movement> movements;

    public List<Movement> getMovements() {
        return movements;
    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }

    public static class Movement {
        private String timestamp;
        private String detail;
        private String ip;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        @Override
        public String toString() {
            return "Movement{" +
                    "timestamp='" + timestamp + '\'' +
                    ", detail='" + detail + '\'' +
                    ", ip='" + ip + '\'' +
                    '}';
        }
    }
}
