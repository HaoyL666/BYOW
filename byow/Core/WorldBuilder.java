package byow.Core;

import byow.Core.Path;
import byow.Core.Position;
import byow.Core.Room;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.*;

import static java.lang.Math.abs;


public class WorldBuilder implements Serializable {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;

    private TETile[][] world;
    private Random rand;
    private List<Room> rooms;
    private Set<Position> floors;
    private Path path;



    public WorldBuilder(Random rand) {
        this.world = new TETile[WIDTH][HEIGHT];
        this.rand = rand;
        this.rooms = new ArrayList<>();
        this.path = new Path();
        this.floors = getFloors();

        // initialize
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }

        int numRooms = 10 + rand.nextInt(5);
        makeNRooms(numRooms);
        removeAll();

//        Collections.sort(rooms, new Comparator<Room>() {
//            @Override
//            public int compare(Room o1, Room o2) {
//                int o1_center_x = (int) (o1.bottomLeftPos.getX() + o1.getWidth() * 0.5);
//                int o1_center_y = (int) (o1.bottomLeftPos.getY() + o1.getHeight() * 0.5);
//                int o2_center_x = (int) (o2.bottomLeftPos.getX() + o2.getWidth() * 0.5);
//                int o2_center_y = (int) (o2.bottomLeftPos.getY() + o2.getHeight() * 0.5);
//                if (o1_center_x == o2_center_x) {
//                    return o2_center_y - o1_center_y;
//                } else {
//                    return o1_center_x - o2_center_x;
//                }
//            }
//        });

        for (int j = 0; j < rooms.size() - 1; j++) {
            connectRooms(rooms.get(j), rooms.get(j + 1));
        }
////        connectRooms(rooms.get(rooms.size() - 1), firstRoom);
        draw();
    }


    public TETile[][] getWorld() {
        return world;
    }

    public void makeNRooms(int n) {
        int i = 0;
        while (i < n) {
            Room r = makeOneRoom();
            if (!isOverlap(r, world)) {
                drawRoom(r);
                rooms.add(r);
                i++;
            }
        }
    }

    public void connectRooms(Room r1, Room r2) {
        List<Position> floor1 = r1.getFloor();
        List<Position> floor1Edge = new ArrayList<>();
        for (Position floor : floor1) {
            if (floor.getX() == r1.bottomLeftPos.getX() + 1 ||
                    floor.getX() == r1.bottomLeftPos.getX() + r1.getWidth() - 2 ||
                    floor.getY() == r1.bottomLeftPos.getY() + 1 ||
                    floor.getY() == r1.bottomLeftPos.getY() + r1.getHeight() - 2) {
                floor1Edge.add(floor);
            }
        }
        List<Position> floor2 = r2.getFloor();
        List<Position> floor2Edge = new ArrayList<>();
        for (Position floor : floor2) {
            if (floor.getX() == r2.bottomLeftPos.getX() + 1 ||
                    floor.getX() == r2.bottomLeftPos.getX() + r2.getWidth() - 2 ||
                    floor.getY() == r2.bottomLeftPos.getY() + 1 ||
                    floor.getY() == r2.bottomLeftPos.getY() + r2.getHeight() - 2) {
                floor2Edge.add(floor);
            }
        }
//        for (Position floor : floor1) {
//            if (floor1.contains(new Position(floor.getX() - 1, floor.getY())) &&
//                    floor1.contains(new Position(floor.getX() + 1, floor.getY())) &&
//                    floor1.contains(new Position(floor.getX(), floor.getY() - 1)) &&
//                    floor1.contains(new Position(floor.getX(), floor.getY() + 1))) {
//                floor1.remove(floor);
//            }
//        }
//        List<Position> floor2 = r2.getFloor();
//        for (Position floor : floor2) {
//            if (floor2.contains(new Position(floor.getX() - 1, floor.getY())) &&
//                    floor2.contains(new Position(floor.getX() + 1, floor.getY())) &&
//                    floor2.contains(new Position(floor.getX(), floor.getY() - 1)) &&
//                    floor2.contains(new Position(floor.getX(), floor.getY() + 1))) {
//                floor2.remove(floor);
//            }
//        }

        Position p1 = floor1Edge.get(rand.nextInt(floor1Edge.size()));
        Position p2 = floor2Edge.get(rand.nextInt(floor2Edge.size()));
        path.connect(p1, p2, rand);
    }


    public Room makeOneRoom() {
        Position randpos = randomPosition();
        int x = randpos.getX();
        int y = randpos.getY();
//        int randlenX = 5 + rand.nextInt(6);
//        int randlenY = 5 + rand.nextInt(6);
        int randlenX = Math.min(60 - x, 4 + rand.nextInt(6));
        int randlenY = Math.min(40 - y, 4 + rand.nextInt(6));

        Room randRoom = new Room(randpos, randlenX, randlenY);
        return randRoom;
    }

    private static boolean isOverlap(Room room, TETile[][] world) {
        int x = room.bottomLeftPos.getX();
        int y = room.bottomLeftPos.getY();
        int width = room.getWidth();
        int height = room.getHeight();

        for (int i = x - 1; i <= x + width; i++) {
            for (int j = y - 1; j <= y + height; j++) {
                if (i >= WIDTH || j >= HEIGHT ||
                        world[i][j].equals(Tileset.FLOOR) ||
                        world[i][j].equals(Tileset.WALL) ||
                        world[i][j].equals(Tileset.LOCKED_DOOR)){
                    return true;
                }
            }
        }
        return false;
    }

    public Position randomPosition() {
        int x = rand.nextInt(56) + 1;
        int y = rand.nextInt(36) + 1;
        return new Position(x, y);
    }

    public Set<Position> getFloors() {
        Set<Position> floors = new TreeSet<>();
        for (Room r : rooms) {
            for (Position p : r.getFloor()) {
                floors.add(p);
            }
        }
        for (Position p : path.path) {
            floors.add(p);
        }
        return floors;
    }

    public void draw() {
        Set<Position> floors = new TreeSet<>();
        for (Room r : rooms) {
            for (Position p : r.getFloor()) {
                floors.add(p);
            }
        }
        for (Position p : path.path) {
            floors.add(p);
        }
//        for (Position p : r.getFloor()) {
//            floors.add(p);
//        }
//        for (Position p : path.path) {
//            floors.add(p);
//        }
        for (Position p : floors) {
            this.world[p.getX()][p.getY()] = Tileset.FLOOR;
        }
//        for (int row = 0; row < world.length; row++) {
//            for (int col = 0; col < world[row].length; col++) {
//                if (world[col][row] == Tileset.FLOOR) {
//                    floors.add(new Position(col, row));
//                }
//            }
//        }
        for (Position p : floors) {
            if (world[p.getX()][p.getY() + 1] == Tileset.NOTHING) {
                world[p.getX()][p.getY() + 1] = Tileset.WALL;
            }
            if (world[p.getX()][p.getY() - 1] == Tileset.NOTHING) {
                world[p.getX()][p.getY() - 1] = Tileset.WALL;
            }
            if (world[p.getX() - 1][p.getY()] == Tileset.NOTHING) {
                world[p.getX() - 1][p.getY()] = Tileset.WALL;
            }
            if (world[p.getX() + 1][p.getY()] == Tileset.NOTHING) {
                world[p.getX() + 1][p.getY()] = Tileset.WALL;
            }
            if (world[p.getX() - 1][p.getY() + 1] == Tileset.NOTHING) {
                world[p.getX() - 1][p.getY() + 1] = Tileset.WALL;
            }
            if (world[p.getX() + 1][p.getY() + 1] == Tileset.NOTHING) {
                world[p.getX() + 1][p.getY() + 1] = Tileset.WALL;
            }
            if (world[p.getX() - 1][p.getY() - 1] == Tileset.NOTHING) {
                world[p.getX() - 1][p.getY() - 1] = Tileset.WALL;
            }
            if (world[p.getX() + 1][p.getY() - 1] == Tileset.NOTHING) {
                world[p.getX() + 1][p.getY() - 1] = Tileset.WALL;
            }
        }
    }
    public void drawRoom(Room r) {
        for (Position p : r.getFloor()) {
            this.world[p.getX()][p.getY()] = Tileset.FLOOR;
        }
        for (Position p : r.getWalls()) {
            this.world[p.getX()][p.getY()] = Tileset.WALL;
        }
    }

    public void removeAll() {
        for (int row = 0; row < world.length; row++) {
            for (int col = 0; col < world[row].length; col++) {
                world[row][col] = Tileset.NOTHING;
            }
        }
    }



//    public boolean overlap(Room a, Room b) {
//        for (Position p1 : b.getFloor()) {
//            for (Position p2 : a.getFloor()) {
//                if (Math.abs(p1.getX() - p2.getX()) <= 3 || Math.abs(p1.getY() - p2.getY()) <= 3) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

//    public boolean outsideWorld(Room r) {
//        if (r.bottomLeftPos.getX() + r.getWidth() > WIDTH-2 || r.bottomLeftPos.getY() + r.getHeight() > HEIGHT-2) {
//            return true;
//        }
//        return false;
//    }

//    public void makeHallway(Room r1, Room r2) {
//        List<Position> wall1 = r1.getWalls();
//        List<Position> wall2 = r2.getWalls();
//
//        Position p1 = wall1.get(rand.nextInt(wall1.size()));
//        Position p2 = wall2.get(rand.nextInt(wall1.size()));
//    }
//
//    public void connect(Room roomA, Room roomB) {
//        List<Position> spanA = roomA.getWalls();
//        List<Position> spanB = roomB.getWalls();
//
//        Position pointA = spanA.get(rand.nextInt(spanA.size()));
//        Position pointB = spanB.get(rand.nextInt(spanB.size()));
//
//        Position start = Position.compareX(pointA, pointB) < 0 ? pointA : pointB;
//        Position end = start == pointA ? pointB : pointA;
//
//        for (int col = start.getX() - 1; col < end.getX() + 2; col++) {
//            for (int row = start.getY() - 1; row < start.getY() + 2; row++) {
//                if (row == start.getY() && col >= start.getX() && col <= end.getX()) {
//                    world[col][row] = Tileset.FLOOR;
//                } else if (world[col][row] != Tileset.FLOOR) {
//                    world[col][row] = Tileset.WALL;
//                }
//            }
//        }
//
//        Position corner = new Position(end.getX(), start.getY());
//        start = Position.compareY(corner, end) < 0 ? corner : end;
//        end = start == corner ? end : corner;
//
//        for (int row = start.getY() - 1; row < end.getY() + 2; row++) {
//            for (int col = start.getX() - 1; col < end.getX() + 2; col++) {
//                if (col == start.getX() && row >= start.getY() && row <= end.getY()) {
//                    world[col][row] = Tileset.FLOOR;
//                } else if (world[col][row] != Tileset.FLOOR) {
//                    world[col][row] = Tileset.WALL;
//                }
//            }
//        }
//
//    }
//
//    public void connectRooms(Room r1, Room r2) {
//        List<Position> wall1 = r1.getWalls();
//        List<Position> wall2 = r2.getWalls();
//
//        int numWall1 = wall1.size();
//        int numWall2 = wall2.size();
//
//        Position p1 = wall1.get(rand.nextInt(numWall1));
//        Position p2 = wall2.get(rand.nextInt(numWall2));
//
//        Position from = null;
//        Position to = null;
//        if (p1.getX() <= p2.getX()) {
//            from =  p1;
//            to = p2;
//        } else {
//            from = p2;
//            to = p1;
//        }
//
//        for (int col = from.getX() - 1; col < to.getX() + 2; col++) {
//            for (int row = from.getY() - 1; row < from.getY() + 2; row++) {
//                if (row == from.getY() && col >= from.getX() && col <= to.getX()) {
//                    world[col][row] = Tileset.FLOOR;
//                } else if (world[col][row] != Tileset.FLOOR) {
//                    world[col][row] = Tileset.WALL;
//                }
//            }
//        }
//
//        Position corner = new Position(to.getX(), from.getY());
//        from = Position.compareY(corner, end) < 0 ? corner : end;
//        end = start == corner ? end : corner;
//
//        for (int row = start.getY() - 1; row < end.getY() + 2; row++) {
//            for (int col = start.getX() - 1; col < end.getX() + 2; col++) {
//                if (col == start.getX() && row >= start.getY() && row <= end.getY()) {
//                    finalWorldFrame[col][row] = Tileset.FLOOR;
//                } else if (finalWorldFrame[col][row] != Tileset.FLOOR) {
//                    finalWorldFrame[col][row] = Tileset.WALL;
//                }
//            }
//        }
//
//    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);


        WorldBuilder world = new WorldBuilder(new Random(189756));

        ter.renderFrame(world.world);
    }

}

