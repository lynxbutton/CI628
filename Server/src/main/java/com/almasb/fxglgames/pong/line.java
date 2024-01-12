package com.almasb.fxglgames.pong;

public class line {
    private int[] pointX;
    private int[] pointY;

    //adds points to the line
    void addToPoints(int x, int y)
    {
        if(pointX.length != 0 && pointY.length != 0) {
            pointX[pointX.length - 1] = x;
            pointY[pointY.length - 1] = y;
        }
        else
        {
            pointX[0] = x;
            pointY[0] = y;
        }
    }

    int getPointX(int p)
    {
        return pointX[p];
    }
    int getPointY(int p)
    {
        return pointY[p];
    }

    int getSize()
    {
        return pointX.length;
    }
}
