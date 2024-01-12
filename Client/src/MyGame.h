#ifndef __MY_GAME_H__
#define __MY_GAME_H__

#include <iostream>
#include <vector>
#include <string>

#include "SDL.h"

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
    std::vector<SDL_Point> drawing;
    //std::vector<SDL_Point> lines;
    std::vector<line> lines;
    std::vector<int> stopIndex;
    int currentLine = 0;
    int lastMouseState = 0;

public:
    SDL_Rect* getRect() { return drawRect; };
    std::vector<SDL_Point> getDrawing() { return drawing; };
    void calcPoints(int x, int y, int down);
    void renderPoints(SDL_Renderer* renderer);

};

class MyGame {

    private:
        SDL_Rect player1 = { 0, 0, 20, 60 };
        SDL_Rect BGRect = { 0, 0, 800, 600 };
        drawingScreen draw;

    public:
        std::vector<std::string> messages;

        bool mouseDown = false;

        void on_receive(std::string message, std::vector<std::string>& args);
        void send(std::string message);
        void input(SDL_Event& event);
        void update();
        void render(SDL_Renderer* renderer);
};

#endif