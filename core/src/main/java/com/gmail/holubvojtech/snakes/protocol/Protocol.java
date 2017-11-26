package com.gmail.holubvojtech.snakes.protocol;

import com.gmail.holubvojtech.snakes.protocol.packet.Handshake;

import java.util.HashMap;
import java.util.Map;

public enum Protocol {

    SERVER_BOUND {
        {
            registerPacket(0x00, Handshake.class);
        }
    },

    CLIENT_BOUND {
        {

        }
    };

    private Map<Class<? extends DefinedPacket>, Integer> packetMap = new HashMap<>(255);
    private Class[] packetClasses = new Class[255];

    public boolean hasPacket(int id) {
        return id < 255 && packetClasses[id] != null;
    }

    public DefinedPacket createPacket(int id) {
        if (hasPacket(id)) {
            try {
                return (DefinedPacket) packetClasses[id].newInstance();
            } catch (ReflectiveOperationException var3) {
                throw new RuntimeException("Could not construct packet with id " + id, var3);
            }
        } else {
            throw new IllegalArgumentException("Packet with id " + id + " not found");
        }
    }

    public int getId(Class<? extends DefinedPacket> packetClass) {

        if (!packetMap.containsKey(packetClass)) {
            throw new IllegalArgumentException("Packet id not found - packet not registered");
        }

        return packetMap.get(packetClass);
    }

    protected void registerPacket(int id, Class<? extends DefinedPacket> packetClass) {
        try {
            packetClass.getDeclaredConstructor();
        } catch (NoSuchMethodException var4) {
            throw new IllegalArgumentException("No default constructor found for packet class " + packetClass);
        }

        packetClasses[id] = packetClass;
        packetMap.put(packetClass, id);
    }
}
