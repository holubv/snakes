package com.gmail.holubvojtech.snakes.client;

import com.gmail.holubvojtech.snakes.AbstractRenderer;
import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Direction;
import com.gmail.holubvojtech.snakes.entity.Entity;
import com.gmail.holubvojtech.snakes.entity.FoodEntity;
import com.gmail.holubvojtech.snakes.entity.SnakeEntity;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.util.List;

public class GameRenderer extends AbstractRenderer {

    private Camera camera;

    private float foodScaleAni = 1;
    private int foodScaleMod = 1;

    public GameRenderer(Camera camera) throws SlickException {
        this.camera = camera;
    }

    public void update(int delta) {
        foodScaleAni += delta * 0.0025 * foodScaleMod;
        if (foodScaleAni > 1.25) {
            foodScaleAni = 1.25f;
            foodScaleMod = -1;
        } else if (foodScaleAni < 0.8) {
            foodScaleAni = 0.8f;
            foodScaleMod = 1;
        }
    }

    public void render(Graphics g) {
        g.setFont(Snakes.font);
        g.setColor(Color.white);
        g.fillRect(0, 0, camera.width, camera.height);

        //render background cues
        g.setColor(Color.lightGray);
        float cx = (float) camera.coords.getX() % 128;
        float cy = (float) camera.coords.getY() % 128;

        for (int x = 0; x < camera.width + 128; x += 128) {
            for (int y = 0; y < camera.height + 128; y += 128) {
                g.fillRect(x - cx, y - cy, 2, 2);
            }
        }

        //render entities
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

        g.setColor(new Color(entity.getColor().getRGB()));

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

            g.fillRect((float) (c.getX()), (float) (c.getY()), camera.size, camera.size);
        }

        coords = entity.getCoords();
        c = camera.transform(coords);

        g.setColor(Color.black);
        g.fillRect((float) c.getX(), (float) c.getY(), camera.size, camera.size);

        // debug rendering... vvv

        //g.setColor(Color.blue);
        //g.drawString(coords.getBlockX() + "," + coords.getBlockY(), (float) c.getX(), (float) c.getY() + 1);

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

    @Override
    public void render(FoodEntity entity, Object context) {
        Graphics g = (Graphics) context;

        Coords c = camera.transform(entity.getCoords());
        //food.draw((float) c.getX(), (float)c.getY(), camera.size, camera.size);
        if (entity.getFoodType() == FoodEntity.Type.GROW) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.red);
        }

        g.pushTransform();

        float size = camera.size * 0.7f * foodScaleAni;
        float off = (camera.size - size) / 2f;

        g.rotate((float) c.getX() + camera.size / 2, (float) c.getY() + camera.size / 2, 45);
        g.fillRect((float) c.getX() + off, (float) c.getY() + off, size, size);

        g.popTransform();
    }

    public Camera getCamera() {
        return camera;
    }
}
