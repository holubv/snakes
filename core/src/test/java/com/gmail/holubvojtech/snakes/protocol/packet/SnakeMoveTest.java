package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Direction;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SnakeMoveTest {

    @RepeatedTest(5)
    void tailEncodeTest() {

        Random r = new Random();

        int size = 50 + r.nextInt(50);
        List<Direction> tail = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            Direction dir = Direction.values()[r.nextInt(4)];
            //System.out.println(dir);
            tail.add(dir);
        }

        //System.out.println("------ reading ------");

        ByteBuf buf = Unpooled.buffer();
        new SnakeMove(5, new Coords(-4, 1), Direction.UP, tail).write(buf);

        SnakeMove received = new SnakeMove();
        received.read(buf);

        List<Direction> nTail = received.getTailAsList();

        /*for (Direction dir : nTail) {
            System.out.println(dir);
        }*/

        assertIterableEquals(tail, nTail);
    }

}