package byow.Core;


import java.io.Serializable;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Path implements Serializable {
    public Set<Position> path;

    public Path() {
        this.path = new TreeSet<>();
    }

    public void connect(Position p1, Position p2, Random rand) {
        int x_distance = p1.getX() - p2.getX();
        int y_distance = p1.getY() - p2.getY();
        int x_change = 0;
        int y_change = 0;

        if (x_distance < 0) {
            x_change = 1;
        } else if (x_distance == 0) {
            x_change = 0;
        } else {
            x_change = -1;
        }
        if (y_distance < 0) {
            y_change = 1;
        } else if (x_distance == 0) {
            y_change = 0;
        } else {
            y_change = -1;
        }
//        int x_change = x_distance == 0 ? 0 : x_distance / (-x_distance);
//        int y_change = y_distance == 0 ? 0 : y_distance / (-y_distance);

        int x = p1.getX();
        int y = p1.getY();

        if (x_change == 0) {
            while (y != p2.getY()) {
                y += y_change;
                path.add(new Position(x, y));
            }
        } else if (y_change == 0) {
            while (x != p2.getX()) {
                x += x_change;
                path.add(new Position(x, y));
            }
        } else {
//        if (Math.abs(x_distance) > Math.abs(y_distance)) {
            int x_turn = (int) (x + rand.nextInt(Math.abs(x_distance)) * x_change);
            while (x != x_turn) {
                x += x_change;
                path.add(new Position(x, y));
            }
            while (y != p2.getY()) {
                y += y_change;
                path.add(new Position(x, y));
            }
            while (x != p2.getX()) {
                x += x_change;
                path.add(new Position(x, y));
            }
//        } else {
//            int y_turn = (int) (y + rand.nextInt(Math.abs(y_distance)) * y_change);
//            while (y != y_turn) {
//                y += y_change;
//                path.add(new Position(x, y));
//            }
//            while (x != p2.getX()) {
//                x += x_change;
//                path.add(new Position(y_turn, y));
//            }
//            while (y != p2.getY()) {
//                y += y_change;
//                path.add(new Position(x, y));
//            }
        }
    }
}

