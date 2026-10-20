package org.openscience.jchempaint.renderer;

import javax.vecmath.Point2d;

// tag: Fixed/Formula/MW/Name/CIP/Id
// context:
// position/absolute:
// anchor:
public class Text {

    private Point2d  p;
    private String   text;
    private String   tag;
    private Style    s = new Style();

    public Text(Point2d position, String text, String tag) {
        this.p = position;
        this.text = text;
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public String getTag() {
        return tag;
    }

    public Point2d getPosition() {
        return p;
    }
}
