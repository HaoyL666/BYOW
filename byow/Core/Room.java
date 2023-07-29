package byow.Core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Comparable<Room>, Serializable {
    public Position bottomLeftPos;
    private int width;
    private int height;
    private List<Position> floor;
    private List<Position> walls;

    public Room(Position p, int w, int h) {
        this.bottomLeftPos = p;
        this.width = w;
        this.height = h;
        this.floor = new ArrayList<>();
        this.walls = new ArrayList<>();

        int endRow = bottomLeftPos.getY() + getHeight();
        int endCol = bottomLeftPos.getX() + getWidth();
        for (int row = bottomLeftPos.getY() - 1; row < endRow + 1; row++) {
            for (int col = bottomLeftPos.getX() - 1; col < endCol + 1; col++) {
                if (row == bottomLeftPos.getY() - 1 || row == endRow || col == bottomLeftPos.getX() - 1 || col == endCol) {
                    walls.add(new Position(col, row));
                } else {
                    floor.add(new Position(col, row));
                }
            }
        }
    }

    public List<Position> getFloor() {
        return this.floor;
    }
    public List<Position> getWalls() {
        return this.walls;
    }
//    public int getX() {
//        return this.bottomLeftPos.getX();
//    }
//    public int getY() {
//        return this.bottomLeftPos.getY();
//    }
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }

    public int compare(Room a, Room b) {
        int a_center_x = (int) (a.bottomLeftPos.getX() + a.getWidth() * 0.5);
        int a_center_y = (int) (a.bottomLeftPos.getY() + a.getHeight() * 0.5);
        int b_center_x = (int) (b.bottomLeftPos.getX() + b.getWidth() * 0.5);
        int b_center_y = (int) (b.bottomLeftPos.getY() + b.getHeight() * 0.5);
        if (a_center_x == b_center_x) {
            return b_center_y - a_center_y;
        } else {
            return a_center_x - b_center_x;
        }
    }

    @Override
    public int compareTo(Room o) {
        int this_center_x = (int) (this.bottomLeftPos.getX() + this.getWidth() * 0.5);
        int this_center_y = (int) (this.bottomLeftPos.getY() + this.getHeight() * 0.5);
        int o_center_x = (int) (o.bottomLeftPos.getX() + o.getWidth() * 0.5);
        int o_center_y = (int) (o.bottomLeftPos.getY() + o.getHeight() * 0.5);
        if (this_center_x == o_center_x) {
            return o_center_y - this_center_y;
        } else {
            return this_center_x - o_center_x;
        }
    }
}
