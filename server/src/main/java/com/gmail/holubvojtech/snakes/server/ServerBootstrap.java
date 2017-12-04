package com.gmail.holubvojtech.snakes.server;

public class ServerBootstrap {

    public static void main(String[] args) {
        System.out.println("starting server");

        try {
            new SnakesServer(25565).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
