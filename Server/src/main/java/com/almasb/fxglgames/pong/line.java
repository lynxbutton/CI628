package com.almasb.fxglgames.pong;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class line {
    private List<Point2D> points = new ArrayList<>();

    //adds points to the line
    void addToPoints(Point2D p)
    {
        if(points.size() != 0) {
            points.add(points.size() - 1, p);
        }
        else
        {
            points.add(p);
        }
    }

    Point2D getPoint(int p)
    {
        return points.get(p);
    }
    int getSize()
    {
        return points.size();
    }
}
