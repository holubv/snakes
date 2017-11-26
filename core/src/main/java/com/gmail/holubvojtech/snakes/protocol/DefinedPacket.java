package com.gmail.holubvojtech.snakes.protocol;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.UUID;

public abstract class DefinedPacket {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final int UTF_8_MAX_CHAR_LEN = 4;
    private static final int MAX_UNSIGNED_SHORT = 65535;
    private static final int MAX_STR_LEN = MAX_UNSIGNED_SHORT / UTF_8_MAX_CHAR_LEN;

    public static UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeUUID(UUID uuid, ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static void writeString(String s, ByteBuf buf) {

        if (s.length() > MAX_STR_LEN) {
            throw new IllegalArgumentException("Cannot send string with more than " + MAX_UNSIGNED_SHORT + " bytes");
        }

        byte[] b = s.getBytes(UTF_8);
        buf.writeShort(b.length);
        buf.writeBytes(b);
    }

    public static String readString(ByteBuf buf) {
        int len = buf.readUnsignedShort();

        if (len > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Cannot receive string with more than " + MAX_UNSIGNED_SHORT + " bytes");
        }

        byte[] b = new byte[len];
        buf.readBytes(b);
        return new String(b, UTF_8);
    }

    public void read(ByteBuf buf) {
        throw new UnsupportedOperationException("read method not implemented");
    }

    public void write(ByteBuf buf) {
        throw new UnsupportedOperationException("write method not implemented");
    }

    public abstract void handle(AbstractPacketHandler handler) throws Exception;

    public abstract String toString();
}
