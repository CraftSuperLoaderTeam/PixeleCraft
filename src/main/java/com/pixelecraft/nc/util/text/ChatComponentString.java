package com.pixelecraft.nc.util.text;

import com.pixelecraft.nc.util.ChatStyle;
import com.pixelecraft.nc.util.IChatComponent;

public class ChatComponentString extends ChatComponentBase {
    private final String text;
    ChatStyle chatStyle;

    public ChatComponentString(String msg) {
        this.text = msg;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public IChatComponent setChatStyle(ChatStyle style) {
        this.chatStyle = style;
        return this;
    }

    public String getChatComponentText_TextValue() {
        return this.text;
    }

    @Override
    public ChatStyle getChatStyle() {
        if(chatStyle == null) chatStyle = new ChatStyle();
        return chatStyle;
    }

    public String getUnformattedComponentText() {
        return this.text;
    }

    public ChatComponentString createCopy() {
        ChatComponentString textcomponentstring = new ChatComponentString(this.text);
        textcomponentstring.setChatStyle(this.getChatStyle().createShallowCopy());

        for (IChatComponent itextcomponent : this.getSiblings()) {
            textcomponentstring.appendSibling(itextcomponent.createCopy());
        }

        return textcomponentstring;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof ChatComponentString)) {
            return false;
        } else {
            ChatComponentString textcomponentstring = (ChatComponentString) p_equals_1_;
            return this.text.equals(textcomponentstring.getText()) && super.equals(p_equals_1_);
        }
    }

    public String toString() {
        return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }
}
