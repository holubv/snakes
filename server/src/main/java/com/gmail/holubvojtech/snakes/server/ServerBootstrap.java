package com.gmail.holubvojtech.snakes.server;

import com.gmail.holubvojtech.snakes.protocol.Protocol;

public class ServerBootstrap {

    public static void main(String[] args) {
        System.out.println("starting server");

        Protocol.values(); //initialize values

        try {
            new SnakesServer(25565).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
