/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.UIController;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.awt.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MainUIController implements UIController {

    @FXML
    private Label labelScoreEnemy;
    @FXML
    private Label typedComm;

    public Label getLabelScoreEnemy() {
        return labelScoreEnemy;
    }

    public void setTypingComm(String txt) {labelScoreEnemy.setText(txt);}

    @Override
    public void init() {
        labelScoreEnemy.setFont(FXGL.getUIFactory().newFont(22));

        labelScoreEnemy.layoutBoundsProperty().addListener((observable, oldValue, newBounds) -> {
            double width = newBounds.getWidth();
            labelScoreEnemy.setTranslateX(800 - 100 - width);
        });

        labelScoreEnemy.textProperty().addListener((observable, oldValue, newValue) -> {
            animateLabel(labelScoreEnemy);
        });

        labelScoreEnemy.setText("Hello");
        labelScoreEnemy.setStyle("-fx-text-fill: black;");
        labelScoreEnemy.setLayoutX(50);
        labelScoreEnemy.setLayoutY(350);
    }

    private void animateLabel(Label label) {
        FadeTransition ft = new FadeTransition(Duration.seconds(0.33), label);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}
