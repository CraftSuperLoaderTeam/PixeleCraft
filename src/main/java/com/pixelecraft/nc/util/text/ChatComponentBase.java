package com.pixelecraft.nc.util.text;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.pixelecraft.nc.util.ChatStyle;
import com.pixelecraft.nc.util.IChatComponent;

import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentBase implements IChatComponent {
    protected List<IChatComponent> siblings = Lists.<IChatComponent>newArrayList();
    private ChatStyle style;

    public IChatComponent appendSibling(IChatComponent component) {
        component.getChatStyle().setParentStyle(this.getChatStyle());
        this.siblings.add(component);
        return this;
    }

    public List<IChatComponent> getSiblings() {
        return this.siblings;
    }

    public IChatComponent appendText(String text) {
        return this.appendSibling(new ChatComponentString(text));
    }

    public IChatComponent setChatStyle(ChatStyle style) {
        this.style = style;

        for (IChatComponent itextcomponent : this.siblings) {
            itextcomponent.getChatStyle().setParentStyle(this.getChatStyle());
        }

        return this;
    }

    public ChatStyle getChatStyle() {
        if (this.style == null) {
            this.style = new ChatStyle();

            for (IChatComponent itextcomponent : this.siblings) {
                itextcomponent.getChatStyle().setParentStyle(this.style);
            }
        }

        return this.style;
    }

    public Iterator<IChatComponent> iterator() {
        return Iterators.<IChatComponent>concat(Iterators.forArray(this), createDeepCopyIterator(this.siblings));
    }

    public final String getUnformattedText() {
        StringBuilder stringbuilder = new StringBuilder();

        for (IChatComponent itextcomponent : this) {
            stringbuilder.append(itextcomponent.getUnformattedComponentText());
        }

        return stringbuilder.toString();
    }

    public final String getFormattedText() {
        StringBuilder stringbuilder = new StringBuilder();

        for (IChatComponent itextcomponent : this) {
            String s = itextcomponent.getUnformattedComponentText();

            if (!s.isEmpty()) {
                stringbuilder.append(itextcomponent.getChatStyle().getFormattingCode());
                stringbuilder.append(s);
                stringbuilder.append((Object) TextFormatting.RESET);
            }
        }

        return stringbuilder.toString();
    }

    public static Iterator<IChatComponent> createDeepCopyIterator(Iterable<IChatComponent> components) {
        Iterator<IChatComponent> iterator = Iterators.concat(Iterators.transform(components.iterator(), new Function<IChatComponent, Iterator<IChatComponent>>() {
            public Iterator<IChatComponent> apply(IChatComponent p_apply_1_) {
                return p_apply_1_.iterator();
            }
        }));
        iterator = Iterators.transform(iterator, new Function<IChatComponent, IChatComponent>() {
            public IChatComponent apply(IChatComponent p_apply_1_) {
                IChatComponent itextcomponent = p_apply_1_.createCopy();
                itextcomponent.setChatStyle(itextcomponent.getChatStyle().createDeepCopy());
                return itextcomponent;
            }
        });
        return iterator;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof ChatComponentBase)) {
            return false;
        } else {
            ChatComponentBase textcomponentbase = (ChatComponentBase) p_equals_1_;
            return this.siblings.equals(textcomponentbase.siblings) && this.getChatStyle().equals(textcomponentbase.getChatStyle());
        }
    }

    public int hashCode() {
        return 31 * this.style.hashCode() + this.siblings.hashCode();
    }

    public String toString() {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }
}
