package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Serverbound
public class Login extends DefinedPacket {

    private String username;
    private String version;

    public Login() {
    }

    public Login(String username, String version) {
        this.username = username;
        this.version = version;
    }

    @Override
    public void read(ByteBuf buf) {
        username = readString(buf);
        version = readString(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        writeString(username, buf);
        writeString(version, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "Login{" +
                "username='" + username + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public String getVersion() {
        return version;
    }
}
