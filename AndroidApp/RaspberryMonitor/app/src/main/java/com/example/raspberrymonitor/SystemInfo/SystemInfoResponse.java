package com.example.raspberrymonitor.SystemInfo;

public class SystemInfoResponse {
    private Double temperature;
    private double memory;
    private double disk;  // Cambia int a double
    private String power;

    // Getter e Setter
    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public double getMemory() {
        return memory;
    }

    public void setMemory(double memory) {
        this.memory = memory;
    }

    public double getDisk() {
        return disk;
    }

    public void setDisk(double disk) {
        this.disk = disk;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }
}
