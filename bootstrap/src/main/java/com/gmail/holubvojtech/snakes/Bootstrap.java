package com.gmail.holubvojtech.snakes;

import com.gmail.holubvojtech.snakes.client.Snakes;
import org.newdawn.slick.AppGameContainer;

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

        AppGameContainer container = new AppGameContainer(new Snakes());
        container.setDisplayMode(720, 560, false);
        container.start();
    }

}
