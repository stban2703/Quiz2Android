package com.example.quiz2android;

public class Punto {
    private float posX;
    private float posY;
    private int tipo;

    public Punto(float posX, float posY, int tipo) {
        this.posX = posX;
        this.posY = posY;
        this.tipo = tipo;
    }

    public Punto() {

    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
