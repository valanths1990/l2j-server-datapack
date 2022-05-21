package com.l2jserver.datapack.autobots.ui.html;

public enum HtmlAlignment {
    Center("center"),
    Left("left"),
    Right("right");

    private final String alignment;
    HtmlAlignment(String alignment){
        this.alignment = alignment;
    }

    @Override
    public String toString() {
        return alignment;
    }
}
