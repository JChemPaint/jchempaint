package org.openscience.jchempaint.renderer;

public class Style {

    enum Decoration {
        Plain,
        Bold,
        Underline,
        Subscript,
        Superscript,
        Formula
    };

    private String fontName;
    private int    fontSize;
    private int    beg, end;
}
