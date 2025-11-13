package com.example.demo.model;

public class WaterMeasurement {
    private String id;
    private double valor;
    private long timestamp;

    public WaterMeasurement() {}

    public WaterMeasurement(double valor, long timestamp) {
        this.valor = valor;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
