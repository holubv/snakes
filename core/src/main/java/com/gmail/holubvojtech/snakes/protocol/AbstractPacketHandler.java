package com.gmail.holubvojtech.snakes.protocol;

import com.gmail.holubvojtech.snakes.protocol.packet.*;

public class AbstractPacketHandler {

    /*
        Server bound
     */

    public void handle(Handshake handshake) throws Exception {
    }

    public void handle(Login login) throws Exception {
    }

    public void handle(RespawnRequest respawnRequest) throws Exception {
    }

    public void handle(Chat chat) throws Exception {
    }

    /*
        Client bound
     */

    public void handle(ServerStatus serverStatus) throws Exception {
    }

    public void handle(Disconnect disconnect) throws Exception {
    }

    public void handle(LoginSuccess loginSuccess) throws Exception {
    }

    public void handle(ChatMessage chatMessage) throws Exception {
    }

    public void handle(PlayerLeave playerLeave) throws Exception {
    }

    public void handle(PlayerJoin playerJoin) throws Exception {
    }
}
