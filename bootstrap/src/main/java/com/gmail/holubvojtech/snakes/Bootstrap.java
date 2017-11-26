package com.gmail.holubvojtech.snakes;

import java.io.File;

public class Bootstrap {

    public static void main(String[] args) throws Throwable {

        String natives = new File("./natives").getAbsolutePath();

        if (System.getProperty("org.lwjgl.librarypath") == null) {
            System.setProperty("org.lwjgl.librarypath", natives);
        }
        if (System.getProperty("java.library.path") == null) {
            System.setProperty("java.library.path", natives);
        }

        System.out.println("Hello, world!");

    }

}
