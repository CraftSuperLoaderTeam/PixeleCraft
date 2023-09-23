package api.pixelecraft;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.regex.Pattern;

public enum ChatColor {

    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    MAGIC('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),
    RESET('r')
    ;

    public static final char COLOR_CHAR = '\u00A7';
    private final static Map<Integer, ChatColor> BY_ID = Maps.newHashMap();
    private final static Map<Character, ChatColor> BY_CHAR = Maps.newHashMap();
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");
    private final String toString;
    private char code;

    ChatColor(char code){
        this.code = code;
        this.toString = new String(new char[]{COLOR_CHAR,code});
    }

    public char getChar(){
        return code;
    }

    public static ChatColor getByChar(char code) {
        return BY_CHAR.get(code);
    }

    public static ChatColor getByChar(String code) {
        Validate.notNull(code, "Code cannot be null");
        Validate.isTrue(!code.isEmpty(), "Code must have at least one char");

        return BY_CHAR.get(code.charAt(0));
    }

    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    @Override
    public String toString() {
        return toString;
    }

    static {
        int i = 0;
        for (ChatColor color : values()) {
            BY_ID.put(i++, color);
            BY_CHAR.put(color.code, color);
        }
    }
}
