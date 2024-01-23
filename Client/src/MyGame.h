#ifndef __MY_GAME_H__
#define __MY_GAME_H__

#include <iostream>
#include <vector>
#include <string>

#include "SDL.h"
#include <SDL_ttf.h>

static struct GameData {
    int totalPlayers = 0;
    int drawingPlayer = 0;
    int mouseX = 0;
    int mouseY = 0;
    int mouseDown = 0;
    int winningPlayers = 0;
    std::string answer = " ";
} game_data;

class line
{
private:
    std::vector<SDL_Point> points;
public:
    line() {};
    void addToPoints(SDL_Point p) { points.push_back(p); };
    SDL_Point getPoint(int n) { return points[n]; };
    int getSize() { return points.size(); }

};

class drawingScreen
{
private:
    SDL_Rect* drawRect = new SDL_Rect{ 25, 25, 500, 500 };
    SDL_Surface drawSurface;
    int brushThickness = 5;
    SDL_Colour brushColour = { 0, 0, 0, 255 };
    std::vector<line> lines;
    int currentLine = 0;
    int lastMouseState = 0;

public:
    drawingScreen() { line nl;  lines.push_back(nl); };
    SDL_Rect* getRect() { return drawRect; };
    void calcPoints(int x, int y, int down);
    void renderPoints(SDL_Renderer* renderer);

};

class MyGame {

    private:
        //text
        TTF_Font* font = nullptr;
        SDL_Color textColour = { 0, 0, 0 };
        SDL_Surface* textSurface = nullptr;
        SDL_Texture* textTexture = nullptr;

        SDL_Rect BGRect = { 0, 0, 800, 600 };
        SDL_Rect commStrip = { 565, 25, 200, 500 };
        SDL_Rect commSpace = { 565, 450, 200, 75 };
        SDL_Rect textRect;
        drawingScreen draw;
        int clientNum = 0;
        std::string comment;
        int comLength = 0;

    public:
        std::vector<std::string> messages;

        bool mouseDown = false;

        void on_receive(std::string message, std::vector<std::string>& args);
        void send(std::string message);
        void input(SDL_Event& event);
        void typing(std::string letter);
        void start();
        void update();
        void render(SDL_Renderer* renderer);
        void close();
};

#endif