package com.gmail.holubvojtech.snakes.protocol;

import com.gmail.holubvojtech.snakes.protocol.packet.*;

public class AbstractPacketHandler {

    /*
        Server bound
     */

    public void handle(Handshake packet) throws Exception {
    }

    public void handle(Login packet) throws Exception {
    }

    public void handle(RespawnRequest packet) throws Exception {
    }

    public void handle(Chat packet) throws Exception {
    }

    /*
        Client bound
     */

    public void handle(ServerStatus packet) throws Exception {
    }

    public void handle(Disconnect packet) throws Exception {
    }

    public void handle(LoginSuccess packet) throws Exception {
    }

    public void handle(ChatMessage packet) throws Exception {
    }

    public void handle(PlayerLeave packet) throws Exception {
    }

    public void handle(PlayerJoin packet) throws Exception {
    }

    public void handle(SnakeMove packet) throws Exception {
    }
}
