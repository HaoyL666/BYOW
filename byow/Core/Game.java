package byow.Core;

import byow.Core.Engine;
import byow.Core.Position;
import byow.Core.WorldBuilder;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class Game implements Serializable{
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;

    public Random rand;
    public TETile[][] world;
    public TETile[][] surfaceWorld;
    public Avatar avatar;
    private WorldBuilder wb;
    private Position door;
    public Position flower;
    public boolean hasAccess;
    public int level;
    public boolean newWorld;
    public String name = " ";

    public Game(Long seed) {
        this.rand = new Random(seed);
        this.wb = new WorldBuilder(rand);
        this.world = wb.getWorld();
        this.avatar = createAvatar();
        this.door = createDoor();
        this.flower = createFlower();
        this.level = 1;
        this.hasAccess = false;
        this.newWorld = false;
    }

    public Avatar createAvatar() {
        int x = rand.nextInt(WIDTH);
        int y = rand.nextInt(HEIGHT);
        if (world[x][y].description().equals(Tileset.FLOOR.description())) {
            return new Avatar(x, y);
        }
        return createAvatar();
    }

    public Position createDoor() {
        int x = rand.nextInt(WIDTH);
        int y = rand.nextInt(HEIGHT);
        if (world[x][y].description().equals(Tileset.WALL.description()) && world[x + 1][y].description().equals(Tileset.NOTHING.description()) && world[x - 1][y].description().equals(Tileset.FLOOR.description())|| world[x][y].description().equals(Tileset.WALL.description()) && world[x][y - 1].description().equals(Tileset.NOTHING.description()) && world[x][y + 1].description().equals(Tileset.FLOOR.description())) {
            world[x][y] = Tileset.LOCKED_DOOR;
            return new Position(x, y);
        }
        return createDoor();
    }

    public Position createFlower() {
        int x = rand.nextInt(WIDTH );
        int y = rand.nextInt(HEIGHT);
        if (world[x][y].description().equals(Tileset.FLOOR.description())) {
            world[x][y] = Tileset.FLOWER;
            hasAccess = true;
            return new Position(x, y);
        }
        return createFlower();
    }

    public class Avatar implements Serializable {
        int x;
        int y;

        public Avatar(int x, int y) {
            this.x = x;
            this.y = y;
            world[x][y] = Tileset.AVATAR;
        }

        public void move(int toX, int toY) {
            world[x][y] = Tileset.FLOOR;
            this.x = toX;
            this.y = toY;
            world[x][y] = Tileset.AVATAR;
        }
    }

    public void action(char act) {
        switch (act) {
            case 'W':
            case 'w':
                moveUp();
                break;
            case 'S' :
            case 's':
                moveDown();
                break;
            case 'A':
            case 'a':
                moveLeft();
                break;
            case 'D' :
            case 'd':
                moveRight();
                break;
        }
    }

    public void makeNewWorld(Long seed) {
        this.rand = new Random(rand.nextInt());
        this.wb = new WorldBuilder(rand);
        this.world = wb.getWorld();
        this.avatar = createAvatar();
        this.door = createDoor();
        this.flower = createFlower();
        this.hasAccess = false;
    }

    public void moveUp() {
        TETile t = world[avatar.x][avatar.y + 1];
        if (t.description().equals(Tileset.UNLOCKED_DOOR.description())) {
            level += 1;
            newWorld = true;
            StdDraw.clear(Color.BLACK);
            makeNewWorld(rand.nextLong());
        } else if (t.description().equals(Tileset.FLOWER.description())) {
            world[door.getX()][door.getY()] = Tileset.UNLOCKED_DOOR;
            avatar.move(avatar.x, avatar.y + 1);
        } else if (t.description().equals(Tileset.FLOOR.description())) {
            avatar.move(avatar.x, avatar.y + 1);
        }
    }


    public void moveDown() {
        TETile t = world[avatar.x][avatar.y - 1];
        if (t.description().equals(Tileset.UNLOCKED_DOOR.description())) {
            level += 1;
            newWorld = true;
            StdDraw.clear(Color.BLACK);
            makeNewWorld(rand.nextLong());
        } else if (t.description().equals(Tileset.FLOWER.description())) {
            world[door.getX()][door.getY()] = Tileset.UNLOCKED_DOOR;
            avatar.move(avatar.x, avatar.y - 1);
        } else if (t.description().equals(Tileset.FLOOR.description())) {
            avatar.move(avatar.x, avatar.y - 1);
        }
    }

    public void moveRight() {
        TETile t = world[avatar.x + 1][avatar.y];
        if (t.description().equals(Tileset.UNLOCKED_DOOR.description())) {
            level += 1;
            newWorld = true;
            StdDraw.clear(Color.BLACK);
            makeNewWorld(rand.nextLong());
        } else if (t.description().equals(Tileset.FLOWER.description())) {
            world[door.getX()][door.getY()] = Tileset.UNLOCKED_DOOR;
            avatar.move(avatar.x + 1, avatar.y);
        } else if (t.description().equals(Tileset.FLOOR.description())) {
            avatar.move(avatar.x + 1, avatar.y);
        }
    }

    public void moveLeft() {
        TETile t = world[avatar.x - 1][avatar.y];
        if (t.description().equals(Tileset.UNLOCKED_DOOR.description())) {
            level += 1;
            newWorld = true;
            StdDraw.clear(Color.BLACK);
            makeNewWorld(rand.nextLong());
        } else if (t.description().equals(Tileset.FLOWER.description())) {
            world[door.getX()][door.getY()] = Tileset.UNLOCKED_DOOR;
            avatar.move(avatar.x - 1, avatar.y);
        } else if (t.description().equals(Tileset.FLOOR.description())) {
            avatar.move(avatar.x - 1, avatar.y);
        }
    }


    protected TETile[][] renderWorld() {
        this.surfaceWorld = new TETile[Engine.WIDTH][Engine.HEIGHT];
        if (level < 2) {
            canSee(-10000);
        } else if (level == 2){
            canSee(8);
        }
        return surfaceWorld;
    }

    private void canSee(int viewLimit) {
        int xLeft   = viewLimit < 0 ? 0 : avatar.x - viewLimit;
        int xRight  = viewLimit < 0 ? Engine.WIDTH - 1 : avatar.x + viewLimit;
        int yUp     = viewLimit < 0 ? Engine.HEIGHT - 1 : avatar.y + viewLimit;
        int yBottom = viewLimit < 0 ? 0: avatar.y - viewLimit;

        for (int i = 0; i < Engine.WIDTH; i++) {
            for (int j = 0; j < Engine.HEIGHT; j++) {
                if (i >= xLeft && i <= xRight && j <= yUp && j >= yBottom) {
                    surfaceWorld[i][j] = world[i][j];
                } else {
                    surfaceWorld[i][j] = Tileset.NOTHING;
                }
            }
        }
    }

    public void createTrap() {
        int num;
        if (level == 1) {
            num = 5;
        } else if (level == 2){
            num = 10;
        } else {
            num = 3;
        }

        int j = 0;
        while (j < num) {
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT);
            if (world[x][y].description().equals(Tileset.FLOOR.description())) {
//                enemies.add(new laserEnemy(new Point(x, y), 2));
                world[x][y] = Tileset.WATER;
                j++;
            }
        }
    }
}
