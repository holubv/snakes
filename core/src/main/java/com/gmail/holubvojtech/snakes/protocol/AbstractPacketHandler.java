package com.gmail.holubvojtech.snakes.protocol;

import com.gmail.holubvojtech.snakes.protocol.packet.Chat;
import com.gmail.holubvojtech.snakes.protocol.packet.Handshake;
import com.gmail.holubvojtech.snakes.protocol.packet.Login;
import com.gmail.holubvojtech.snakes.protocol.packet.RespawnRequest;

public class AbstractPacketHandler {

    public void handle(Handshake handshake) throws Exception {
    }

    public void handle(Login login) throws Exception {
    }

    public void handle(RespawnRequest respawnRequest) throws Exception {
    }

    public void handle(Chat chat) throws Exception {
    }
}
