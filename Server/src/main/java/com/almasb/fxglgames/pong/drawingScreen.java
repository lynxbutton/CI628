package com.almasb.fxglgames.pong;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class drawingScreen extends Component {
    private List<line> lines = new ArrayList<>();
    private int brushThickness = 1;
    // colour goes here
    private int currentLine = 0;
    private int lastMouseState = 0;

    private Point2D previousP;

    void calcPoints(int x, int y, int down, Entity p, Entity l)
    {
        if(lines.size() > 0)
        {
            if(p.getX() < x && p.getWidth() + p.getX() > x && p.getY() < y && p.getHeight() + p.getY() > y)
            { //p.getX() < x && p.getX() + p.getWidth() > x && p.getY() < y && p.getY() + p.getHeight() > y
                if(down == 1)
                {
                    Point2D point = new Point2D(x, y);
                    lines.get(currentLine).addToPoints(point);

                    if(lines.get(currentLine).getSize() > 2)
                    {
                        l.getViewComponent().addChild(new Line(previousP.getX(), previousP.getY(), x, y));
                    }
                    previousP = new Point2D(x,y);
                }
                else if(lastMouseState != down)
                {
                    currentLine += 1;
                    lines.add(new line());
                }
            }
            else {
                currentLine += 1;
                lines.add(new line());
            }
        }
        else {
            lines.add(new line());
        }
        lastMouseState = down;
    }
}
