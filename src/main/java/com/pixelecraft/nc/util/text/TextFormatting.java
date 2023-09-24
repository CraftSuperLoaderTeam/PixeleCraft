package com.pixelecraft.nc.util.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public enum TextFormatting {
    BLACK("BLACK", '0', 0),
    DARK_BLUE("DARK_BLUE", '1', 1),
    DARK_GREEN("DARK_GREEN", '2', 2),
    DARK_AQUA("DARK_AQUA", '3', 3),
    DARK_RED("DARK_RED", '4', 4),
    DARK_PURPLE("DARK_PURPLE", '5', 5),
    GOLD("GOLD", '6', 6),
    GRAY("GRAY", '7', 7),
    DARK_GRAY("DARK_GRAY", '8', 8),
    BLUE("BLUE", '9', 9),
    GREEN("GREEN", 'a', 10),
    AQUA("AQUA", 'b', 11),
    RED("RED", 'c', 12),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
    YELLOW("YELLOW", 'e', 14),
    WHITE("WHITE", 'f', 15),
    OBFUSCATED("OBFUSCATED", 'k', true),
    BOLD("BOLD", 'l', true),
    STRIKETHROUGH("STRIKETHROUGH", 'm', true),
    UNDERLINE("UNDERLINE", 'n', true),
    ITALIC("ITALIC", 'o', true),
    RESET("RESET", 'r', -1);

    private static final Map<String, TextFormatting> NAME_MAPPING = Maps.<String, TextFormatting>newHashMap();

    private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");

    private final String name;

    private final char formattingCode;
    private final boolean fancyStyling;

    private final String controlString;

    private final int colorIndex;

    private static String lowercaseAlpha(String p_175745_0_) {
        return p_175745_0_.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private TextFormatting(String formattingName, char formattingCodeIn, int colorIndex) {
        this(formattingName, formattingCodeIn, false, colorIndex);
    }

    private TextFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn) {
        this(formattingName, formattingCodeIn, fancyStylingIn, -1);
    }

    private TextFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn, int colorIndex) {
        this.name = formattingName;
        this.formattingCode = formattingCodeIn;
        this.fancyStyling = fancyStylingIn;
        this.colorIndex = colorIndex;
        this.controlString = "\u00a7" + formattingCodeIn;
    }

    public int getColorIndex() {
        return this.colorIndex;
    }

    public boolean isFancyStyling() {
        return this.fancyStyling;
    }

    public boolean isColor() {
        return !this.fancyStyling && this != RESET;
    }

    public String getFriendlyName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.controlString;
    }

    public static String getTextWithoutFormattingCodes(String text) {
        return text == null ? null : FORMATTING_CODE_PATTERN.matcher(text).replaceAll("");
    }

    public static TextFormatting getValueByName(String friendlyName) {
        return friendlyName == null ? null : (TextFormatting) NAME_MAPPING.get(lowercaseAlpha(friendlyName));
    }

    public static TextFormatting fromColorIndex(int index) {
        if (index < 0) {
            return RESET;
        } else {
            for (TextFormatting textformatting : values()) {
                if (textformatting.getColorIndex() == index) {
                    return textformatting;
                }
            }

            return null;
        }
    }

    public static Collection<String> getValidValues(boolean p_96296_0_, boolean p_96296_1_) {
        List<String> list = Lists.<String>newArrayList();

        for (TextFormatting textformatting : values()) {
            if ((!textformatting.isColor() || p_96296_0_) && (!textformatting.isFancyStyling() || p_96296_1_)) {
                list.add(textformatting.getFriendlyName());
            }
        }

        return list;
    }

    static {
        for (TextFormatting textformatting : values()) {
            NAME_MAPPING.put(lowercaseAlpha(textformatting.name), textformatting);
        }
    }
}
