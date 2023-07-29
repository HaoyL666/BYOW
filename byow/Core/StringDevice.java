package byow.Core;

public class StringDevice implements InputDevice {
    private String input;
    private int index;
    private int length;

    public StringDevice(String s) {
        index = 0;
        input = s;
        length = s.length();
    }

    @Override
    public char nextChar() {
        char character = input.charAt(index);
        index += 1;
        return character;
    }

    @Override
    public boolean hasNext() {
        return index < length;
    }
}
