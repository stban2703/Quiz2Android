package com.example.quiz2android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class DrawView extends View {
    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int width;
    private int height;
    private ArrayList<Punto> puntos;
    private DatagramSocket socket;
    private InetAddress eclipse;

    // Control + O
    // Sucede cuando la aplicacion inicia
    // Equivalente a settings
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        setup();
    }

    // Equivalente al setup en processing
    private void setup() {
        puntos = new ArrayList<Punto>();
        // Hilo de recepcion
        new Thread(
                () -> {
                    try {
                        eclipse = InetAddress.getByName("192.168.0.3");
                        socket = new DatagramSocket(5000);

                        while (true) {
                            //Si es un Datagram de recepcipn, solo le ponemos dos parametros
                            byte[] buffer = new byte[2000]; //-> Entero que puede representar 256, ASCII, 8 bit, bit -> -0 o 1
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                            //Esta linea, se queda esperando por paquetes
                            socket.receive(packet);

                            //El mensaje queda en el paquete, despues de la recepcion
                            String json = new String(packet.getData()).trim();

                            // Limpiar arreglo
                            puntos.clear();

                            //Recibir jugada
                            Gson gson = new Gson();
                            Punto[] puntosRecibidos = gson.fromJson(json, Punto[].class);

                            for (int i = 0; i < puntosRecibidos.length; i++) {
                                // Ajustar posicion
                                float dobleX = puntosRecibidos[i].getPosX() * 2;
                                float dobleY = puntosRecibidos[i].getPosY() * 2;

                                puntosRecibidos[i].setPosX(dobleX);
                                puntosRecibidos[i].setPosY(dobleY);
                                puntos.add(puntosRecibidos[i]);
                            }

                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    // Equivalente al draw
    // Control + O
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(255, 255, 255, 255);

        //Log.e("Tamaño", "Ancho: " + width + "/" + "Alto: " + height);

        // Pintar puntos
        for (int i = 0; i < puntos.size(); i++) {
            float x = puntos.get(i).getPosX();
            float y = puntos.get(i).getPosY();
            int tipo = puntos.get(i).getTipo();

            if (tipo == 0) {
                Paint pUno = new Paint();
                pUno.setStyle(Paint.Style.FILL);
                pUno.setColor(Color.rgb(255, 155, 0));
                canvas.drawCircle(x, y, 50, pUno);

            } else if (tipo == 1) {
                Paint pDos = new Paint();
                pDos.setStyle(Paint.Style.FILL);
                pDos.setColor(Color.rgb(155, 0, 255));
                canvas.drawCircle(x, y, 50, pDos);
            }

        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;

            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:
                // Agregar puntos
                Punto punto = new Punto(event.getX(), event.getY(), 1);
                puntos.add(punto);
                Gson gson = new Gson();
                String json = gson.toJson(puntos);

                enviarMensaje(json);
                break;
        }
        return true;
    }

    public void enviarMensaje(String mensaje) {
        // Hilo de envio
        new Thread(
                () -> {
                    try {
                        byte[] buffer = mensaje.getBytes();
                        // El paquete tiene 4 parametros
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, eclipse, 5000);
                        socket.send(packet);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
        ).start();
    }
}
