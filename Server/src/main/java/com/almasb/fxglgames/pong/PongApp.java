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

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.*;
import com.almasb.fxgl.net.*;
import com.almasb.fxgl.ui.UI;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.pong.NetworkMessages.*;

/**
 * A simple clone of Pong.
 * Sounds from https://freesound.org/people/NoiseCollector/sounds/4391/ under CC BY 3.0.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PongApp extends GameApplication implements MessageHandler<String> {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Guess drawing");
        settings.setVersion("1.0");
        settings.setFontUI("pong.ttf");
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    private Entity lines;
    private Entity commSect;
    private Entity page;
    private drawingScreen draw;
    private MainUIController controller;

    private Server<String> server;

    private int totalPlayers;
    private int drawingPlayer;
    private int mouseDown;
    private int mouseX;
    private int mouseY;
    private int winningPlayers;
    private String comment;
    private String answer;
    private int comLength = 0;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("mouse") {
            @Override
            protected void onAction(){
                if(mouseDown == 0) {
                    mouseDown = 1;
                    System.out.println("Mouse down");
                    server.broadcast(MOUSE_DOWN);
                }
            }

            @Override
            protected void onActionEnd(){
                mouseDown = 0;
                System.out.println("Mouse up");
                server.broadcast(MOUSE_UP);
            }
        }, MouseButton.PRIMARY);

        getInput().addAction(new UserAction("enter") {
            @Override
            protected void onAction(){
                System.out.println(comment);
            }
        }, KeyCode.ENTER);

        getInput().addTriggerListener(new TriggerListener() {
            @Override
            protected void onActionBegin(Trigger trigger) {
                //System.out.println("Begin: " + trigger);
                if(trigger.getName().length() == 1)
                {
                    Character trig = trigger.getName().charAt(0);
                    if(Character.isLetter(trig))
                    {
                        if(comment != null)
                        {
                            if(comLength + 1 < 24)
                            {
                                if(comLength + 1 == 12)
                                {
                                    comment += trigger.getName().toLowerCase() + "\n";
                                }
                                else
                                {
                                    comment += trigger.getName().toLowerCase();
                                }
                                comLength += 1;
                                controller.setTypingComm(comment);
                            }
                        }
                        else
                        {
                            comLength += 1;
                            comment = trigger.getName().toLowerCase();
                            controller.setTypingComm(comment);
                        }
                    }
                }
                else if(trigger.getName() == "Backspace")
                {
                    if(comLength > 0)
                    {
                        comLength -= 1;
                        comment = comment.substring(0, comLength - 1);
                        controller.setTypingComm(comment);
                    }
                }
                else if(trigger.getName() == "Space")
                {
                    comLength += 1;
                    comment += " ";
                    controller.setTypingComm(comment);
                }
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        //vars.put("player1score", 0);
        vars.put("comment", "");
        totalPlayers = 1;
        drawingPlayer = 1;
        mouseDown = 0;
        mouseX = 0;
        mouseY = 0;
        winningPlayers = 0;
        answer = genAnswer();
    }

    protected String genAnswer()
    {
        String[] answers = {"apple", "car", "house", "chair", "table"};
        int index = random(0, answers.length - 1);
        //System.out.println(answers[index]);
        return answers[index];
    }

    @Override
    protected void initGame() {
        Writers.INSTANCE.addTCPWriter(String.class, outputStream -> new MessageWriterS(outputStream));
        Readers.INSTANCE.addTCPReader(String.class, in -> new MessageReaderS(in));

        server = getNetService().newTCPServer(55555, new ServerConfig<>(String.class));

        server.setOnConnected(connection -> {
            connection.addMessageHandlerFX(this);
        });

        getGameWorld().addEntityFactory(new PongFactory());
        getGameScene().setBackgroundColor(Color.rgb(72, 75, 94));

        initScreenBounds();
        initGameObjects();

        var t = new Thread(server.startTask()::run);
        t.setDaemon(true);
        t.start();
    }
    @Override
    protected void initUI() {
        controller = new MainUIController();
        UI ui = getAssetLoader().loadUI("main.fxml", controller);

        //controller.getLabelScoreEnemy().textProperty().bind(getip("comment").asString());

        controller.setTypingComm(comment);

        getGameScene().addUI(ui);
    }

    @Override
    protected void onUpdate(double tpf) {
        if(drawingPlayer == 1)
        {
            mouseX = (int) getInput().getMouseXWorld();
            mouseY = (int) getInput().getMouseYWorld();
        }
        else
        {

        }
        if (!server.getConnections().isEmpty()) {
            var message = "GAME_DATA," + totalPlayers + "," + drawingPlayer + "," + mouseX + "," + mouseY + "," + mouseDown + "," + winningPlayers + "," + answer;
            //var message = "GAME_DATA," + drawingPlayer + "," + mouseX + "," + mouseY + "," + mouseDown;
            server.broadcast(message);
        }

        draw.calcPoints(mouseX, mouseY, mouseDown, page, lines);
    }

    private void initScreenBounds() {
        Entity walls = entityBuilder()
                .type(EntityType.WALL)
                .collidable()
                .buildScreenBounds(150);

        getGameWorld().addEntity(walls);
    }

    private void initGameObjects() {
        //ball = spawn("ball", getAppWidth() / 2 - 5, getAppHeight() / 2 - 5);
        //player1 = spawn("bat", new SpawnData(getAppWidth() / 4, getAppHeight() / 2 - 30).put("isPlayer", true));
        //player2 = spawn("bat", new SpawnData(3 * getAppWidth() / 4 - 20, getAppHeight() / 2 - 30).put("isPlayer", false));

        page = spawn("page", 25, 25);
        draw = new drawingScreen();
        lines = spawn("lines",0,0);
        commSect = spawn("comment", 565, 25);

        //player1Bat = player1.getComponent(BatComponent.class);
        //player2Bat = player2.getComponent(BatComponent.class);
    }

    @Override
    public void onReceive(Connection<String> connection, String message) {
        var tokens = message.split(",");

        Arrays.stream(tokens).skip(1).forEach(key -> {
            if(key.startsWith("PLAY_")){
                var s = key.substring(5,key.length());
                if(Integer.parseInt(s) == totalPlayers + 1)
                {
                    totalPlayers += 1;
                    System.out.println(totalPlayers);
                }
                else
                {
                    server.broadcast("PLAY_" + Integer.parseInt(s) + "_" + (totalPlayers + 1));
                }
            }
            if (key.endsWith("_DOWN")) {
                //getInput().mockButtonPress(MouseButton.PRIMARY);
                mouseDown = 1;
                //getInput().mockKeyPress(KeyCode.valueOf(key.substring(0, 1)));
            } else if (key.endsWith("_UP")) {
                //getInput().mockButtonRelease(MouseButton.PRIMARY);
                mouseDown = 0;
            }else if(key.startsWith("X_")){
                mouseX = Integer.parseInt(key.substring(2, key.length()));
            }else if(key.startsWith("Y_")){
                mouseY = Integer.parseInt(key.substring(2, key.length()));
            }
        });
    }

    static class MessageWriterS implements TCPMessageWriter<String> {

        private OutputStream os;
        private PrintWriter out;

        MessageWriterS(OutputStream os) {
            this.os = os;
            out = new PrintWriter(os, true);
        }

        @Override
        public void write(String s) throws Exception {
            out.print(s.toCharArray());
            out.flush();
        }
    }

    static class MessageReaderS implements TCPMessageReader<String> {

        private BlockingQueue<String> messages = new ArrayBlockingQueue<>(50);

        private InputStreamReader in;

        MessageReaderS(InputStream is) {
            in =  new InputStreamReader(is);

            var t = new Thread(() -> {
                try {

                    char[] buf = new char[36];

                    int len;

                    while ((len = in.read(buf)) > 0) {
                        var message = new String(Arrays.copyOf(buf, len));

                        System.out.println("Recv message: " + message);

                        messages.put(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            t.setDaemon(true);
            t.start();
        }

        @Override
        public String read() throws Exception {
            return messages.take();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
