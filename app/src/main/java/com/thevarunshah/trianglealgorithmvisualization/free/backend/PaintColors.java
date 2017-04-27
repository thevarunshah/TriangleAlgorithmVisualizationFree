package com.thevarunshah.trianglealgorithmvisualization.free.backend;

import android.graphics.Color;
import android.graphics.Paint;

public class PaintColors {

    private static Paint getBasePaint(){
        Paint base = new Paint(Paint.ANTI_ALIAS_FLAG);
        base.setStyle(Paint.Style.FILL);
        return base;
    }

    public static Paint getCustomColor(int red, int green, int blue){
        Paint custom = getBasePaint();
        custom.setColor(Color.rgb(red, green, blue));
        return custom;
    }

    public static Paint getBlack() {
        Paint black = getBasePaint();
        black.setColor(Color.BLACK);
        return black;
    }

    public static Paint getWhite(){
        Paint white = getBasePaint();
        white.setColor(Color.WHITE);
        return white;
    }

    public static Paint getRed() {
        Paint red = getBasePaint();
        red.setColor(Color.RED);
        return red;
    }

    public static Paint getGreen(){
        Paint green = getBasePaint();
        green.setColor(Color.GREEN);
        return green;
    }

    public static Paint getBlue(){
        Paint blue = getBasePaint();
        blue.setColor(Color.BLUE);
        return blue;
    }

    public static Paint getYellow(){
        Paint yellow = getBasePaint();
        yellow.setColor(Color.YELLOW);
        return yellow;
    }
}
