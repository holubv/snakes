package com.gmail.holubvojtech.snakes.netty;

import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;

import java.net.InetSocketAddress;

public interface Connection {

    InetSocketAddress getAddress();

    void disconnect();

    Connection.Unsafe unsafe();

    interface Unsafe {

        void sendPacket(DefinedPacket var1);
    }
}
