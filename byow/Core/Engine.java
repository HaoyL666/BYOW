package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Engine implements Serializable{
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;
    public Game g;
    public long s;
    public boolean std = true;
    public String name = " ";

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        displayMenu();
        processMenu();
    }

    public void displayMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);

        StdDraw.text(30, 30, "CS61B: The Game");
        StdDraw.text(30, 22, "New Game (N)");
        StdDraw.text(30, 20, "Load Game (L)");
        StdDraw.text(30, 18, "Quit (Q)");
        StdDraw.text(30, 16, "Options (O)");
        StdDraw.show();
    }


    public void processMenu() {
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                switch (Character.toString(key)) {
                    case "N":
                    case "n":
                        initiateGame();
                        g.name = this.name;
                        startGame();
                        checkWin();
                        break;

                    case "Q":
                    case "q":
                        quit();
                        break;

                    case "L":
                    case "l":
                        load();
                        ter.initialize(WIDTH, HEIGHT);
                        ter.renderFrame(g.renderWorld(), g.level, g.name);
                        if (this.name != " ") {
                            g.name = this.name;
                        }
                        startGame();
                        checkWin();
                        break;
                    case "O":
                    case "o":
                        StdDraw.clear(Color.BLACK);
                        StdDraw.text(30, 30, "Please enter your name: ");
                        StdDraw.show();
                        name = displayName();
                        displayMenu();
                        processMenu();
                }
            }
        }
    }

    public String displayName() {
        String name = "";

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                if (input != '!') {
                    name += input;
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(30, 30, "Please enter your name: ");
                    StdDraw.text(30, 20, name);
                    StdDraw.show();
                } else {
                    return name;
                }
            }
            StdDraw.pause(500);
        }
    }

    public void checkWin() {
        if (g.level == 3) {
            ter.initialize(WIDTH, HEIGHT);
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            Font font = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(font);
            StdDraw.text(30, 20, "Congratulations, you won!");
            StdDraw.show();
            StdDraw.pause(1000);
        }
    }


    public void mousePoint() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (0 <= x && 60 > x && 0 <= y && 40 > y) {

            if (!g.world[x][y].description().equals(Tileset.NOTHING.description())) {
                ter.renderFrame(g.renderWorld(), g.level, g.name);
                StdDraw.setPenColor(Color.pink);
                StdDraw.text(4, HEIGHT - 1, g.world[x][y].description());
            } else {
                ter.renderFrame(g.renderWorld(), g.level, g.name);
                StdDraw.setPenColor(Color.pink);
                StdDraw.text(5, HEIGHT - 1, " ");
            }
            StdDraw.show();
        }
    }

    private void initiateGame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(30, 30, "Enter a number: ");
        StdDraw.show();
        this.s = displaySeed();
        this.g = new Game(s);

        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        ter.renderFrame(g.renderWorld());
    }

    public Long displaySeed() {
        String seed = "";

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key != 's' && key != 'S') {
                    seed += key;
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(30, 30, "Enter a number: ");
                    StdDraw.text(30, 20, seed);
                    StdDraw.show();
                    s = Long.valueOf(seed);
                } else {
                    return s;
                }
            }
            StdDraw.pause(500);
        }
    }

    private void startGame() {
        while (true) {
            checkWin();
            mousePoint();
            if (StdDraw.hasNextKeyTyped()) {
                Character move = StdDraw.nextKeyTyped();
                movement(move);
            }
        }
    }

    public void quit() {
        System.exit(0);
    }

    public void load() {
        File file = new File("savefile.txt");
        if (file.exists()) {
            try {
                FileInputStream fos = new FileInputStream(file);
                ObjectInputStream oos = new ObjectInputStream(fos);
                g = (Game) oos.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, running both of these:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

//        StringBuilder b = new StringBuilder();
//        StringBuilder c = new StringBuilder();
//        StringDevice stringDevice = new StringDevice(input);
//        while (stringDevice.hasNext()) {
//            Character next = stringDevice.nextChar();
//            if (next.equals('L') || next.equals('l')) {

        List<Character> str = getSolicit(input);
        Character next = str.remove(0);

        if (next.equals('N') || next.equals('n')) {
            StringBuilder b = new StringBuilder();
            StringBuilder c = new StringBuilder();
            c.append(next);
            next = str.remove(0);
            while (!next.equals('s') && !next.equals('S')) {
                b.append(next);
                c.append(next);
                next = str.remove(0);
            }
            c.append(next);

            StringBuilder act = new StringBuilder();
            for (int i = 0; i < str.size(); i++) {
                next = str.remove(0);
                act.append(next);
                i -= 1;
            }

            try {
                File file = new File("./savefile.txt");
                PrintWriter writer = new PrintWriter(file);
                writer.write(c.toString() + act.toString());
                writer.flush();
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            s = Long.valueOf(b.toString()); //b.toString is the seed
            Random r = new Random(s);
            long y = Math.abs(r.nextLong());
            g = new Game(Math.abs(r.nextLong()));
            //ter.renderFrame(m.world);
            playWithString(act.toString());
            return g.world;
        } else if (next.equals(':')) {
            next = str.remove(0);
            if (next.equals('Q')) {
                // System.exit(0);
                return g.world;
            }
        } else if (next.equals('L') || next.equals('l')) {
            File file = new File("./savefile.txt");
            String st = " ";

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                st = br.readLine();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (st.charAt(st.length() - 1) == 'Q' && st.charAt(st.length() - 2) == ':' || st.charAt(st.length() - 1) == 'q' && st.charAt(st.length() - 2) == ':') {
                st = st.substring(0, st.length() - 2);
            }

            StringBuilder act = new StringBuilder();
            for (int i = 0; i < str.size(); i++) {
                next = str.remove(0);
                act.append(next);
                i -= 1;
            }

            interactWithInputString(st + act.toString());
            return g.world;
        }
        return g.world;
    }

    public List<Character> getSolicit(String input) {
        List<Character> lst = new LinkedList<>();
        for (char c : input.toCharArray()) {
            lst.add(c);
        }
        return lst;
    }

    public void playWithString(String str) {
        for (int i = 0; i < str.length(); i++) {
            char next = str.charAt(i);
            if (next == ':') {
                if (str.charAt(i + 1) == 'Q' || str.charAt(i + 1) == 'q') {
                    return;
                }
            } else {
                movement(str.charAt(i));
            }
        }
    }

    public void movement(char next) {
        if (next != ':') {
            g.action(next);
            if (std == true) {
                ter.renderFrame(g.renderWorld(), g.level, g.name);
            }
        } else if (next == ':') {
            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    char c = StdDraw.nextKeyTyped();
                    if (c == 'q' || c == 'Q') {
                        save();
                        quit();
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public void save() {
        File file = new File("savefile.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(g);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

}



