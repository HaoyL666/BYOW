package byow.Core;

import java.io.Serializable;

public class Position implements Comparable<Position>, Serializable {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }

    public static int compareX(Position a, Position b) {
        return a.getX() - b.getX();
    }
    public static int compareY(Position a, Position b) {
        return a.getY() - b.getY();
    }

    @Override
    public int compareTo(Position o) {
        if (this.getX() == o.getX()) {
            return this.getY() - o.getY();
        } else {
            return this.getX() - o.getX();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this.x == ((Position) o).x && this.y == ((Position) o).y) {
            return true;
        } else{
            return false;
        }
    }
}
