package com.gmail.holubvojtech.snakes.client;

import com.gmail.holubvojtech.snakes.AbstractRenderer;
import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Direction;
import com.gmail.holubvojtech.snakes.entity.Entity;
import com.gmail.holubvojtech.snakes.entity.SnakeEntity;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.List;

public class GameRenderer extends AbstractRenderer {

    private Camera camera;

    public GameRenderer(Camera camera) {
        this.camera = camera;
    }

    public void render(Graphics g) {
        g.setFont(Snakes.font);
        g.setColor(Color.white);
        g.fillRect(0, 0, camera.width, camera.height);

        g.setColor(Color.red);
        for (int x = 0; x < camera.width; x += camera.size) {
            for (int y = 0; y < camera.height; y += camera.size) {
                g.drawRect((float) (x - camera.coords.getX()), (float) (y - camera.coords.getY()), camera.size, camera.size);

                if (x % 10 == 0) {
                    g.setColor(Color.blue);
                    g.drawString((x / camera.size) + "", (float) (x - camera.coords.getX()), 0);
                    g.setColor(Color.red);
                }
                if (y % 10 == 0) {
                    g.setColor(Color.blue);
                    g.drawString((y / camera.size) + "", 0, (float) (y - camera.coords.getY()));
                    g.setColor(Color.red);
                }
            }
        }

        List<Entity> entities = Snakes.inst.getEntities();
        for (Entity entity : entities) {
            entity.render(this, g);
        }
    }

    @Override
    public void render(SnakeEntity entity, Object context) {
        Graphics g = (Graphics) context;

        Direction snakeDir = entity.getDirection();
        Coords coords = entity.getCoords();
        Coords c;
        //todo viewport check

        g.setColor(Color.darkGray);

        double off = snakeDir.getRx() != 0 ? coords.getDecimalX() : coords.getDecimalY();
        if (off < 0) {
            off = 1 + off;
        }

        coords = entity.getTailPivot();
        for (Direction dir : entity.getTail()) {
            dir.set(coords);
            if (!snakeDir.isNegative()) {
                c = camera.transform(coords.blockCoords().add(off * dir.opposite().getRx(), off * dir.opposite().getRy()));
            } else {
                c = camera.transform(coords.blockCoords().add((1 - off) * -dir.getRx(), (1 - off) * -dir.getRy()));
            }

            g.setColor(Color.darkGray);
            g.fillRect((float) (c.getX()), (float) (c.getY()), camera.size, camera.size);
        }

        coords = entity.getCoords();
        c = camera.transform(coords);

        g.setColor(Color.black);
        g.fillRect((float) c.getX(), (float) c.getY(), camera.size, camera.size);

        // debug rendering... vvv

        g.setColor(Color.blue);
        g.drawString(coords.getBlockX() + "," + coords.getBlockY(), (float) c.getX(), (float) c.getY() + 1);

        /*g.setColor(Color.green);
        coords = entity.getTailPivot();
        for (Direction d : entity.getTail()) {
            d.set(coords);
            c = camera.transform(coords.blockCoords());

            g.setColor(Color.green);
            g.drawRect((float) (c.getX()), (float) (c.getY()), camera.size, camera.size);

            g.setColor(Color.lightGray);
            g.drawString(d.name().substring(0, 1), (float)c.getX(), (float)c.getY());
        }

        coords = entity.getTailPivot();
        c = camera.transform(coords);
        g.setColor(Color.yellow);
        g.drawRect((float) (c.getX()), (float) (c.getY()), camera.size, camera.size);*/
    }

    public Camera getCamera() {
        return camera;
    }
}
