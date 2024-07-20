package com.example.raspberrymonitor.Movements;

public class MovementRequest {
    private String movement;

    public MovementRequest(String movement) {
        this.movement = movement;
    }

    public String getMovement() {
        return movement;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }
}
