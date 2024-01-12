#include "MyGame.h"

void drawingScreen::calcPoints(int x, int y, int down)
{
    if (drawRect->x < x && drawRect->x + drawRect->w > x &&
        drawRect->y < y && drawRect->y + drawRect->h > y)
    {
        if (down == 1)
        {
            if (lines.size() <= 0)
            {
                line newl;
                lines.push_back(newl);
            }
            SDL_Point newPoint = { x, y };
            lines[currentLine].addToPoints(newPoint);
        }
        else if (lastMouseState != down)
        {
            currentLine += 1;
            line newl;
            lines.push_back(newl);
        }

        lastMouseState = down;
    }
    else
    {
        currentLine += 1;
        line newl;
        lines.push_back(newl);
    }

}

void drawingScreen::renderPoints(SDL_Renderer* renderer) {

    for (int i = 0; i < lines.size(); i++)
    {
        for (int p = 1; p < lines[i].getSize(); p++)
        {
            if (lines[i].getSize() > p + 1)
            {
                SDL_Point point1 = lines[i].getPoint(p);
                SDL_Point point2 = lines[i].getPoint(p + 1);
                SDL_RenderDrawLine(renderer, point1.x, point1.y, point2.x, point2.y);
            }
        }
    }

}

void MyGame::on_receive(std::string cmd, std::vector<std::string>& args) {
    if (cmd == "GAME_DATA") {
        // we should have exactly 5 arguments
        if (args.size() == 6) {
            game_data.totalPlayers = stoi(args.at(0));
            game_data.drawingPlayer = stoi(args.at(1));
            game_data.mouseX = stoi(args.at(2));
            game_data.mouseY = stoi(args.at(3));
            game_data.mouseDown = stoi(args.at(4));
            game_data.winningPlayers = stoi(args.at(5));
        }
    }
    else {
        std::cout << "Received: " << cmd << std::endl;
    }
}

void MyGame::send(std::string message) {
    messages.push_back(message);
}

void MyGame::input(SDL_Event& event) {
    switch (event.type) {
    case SDL_MOUSEBUTTONDOWN:
        mouseDown = true;
        send("M_DOWN");

        break;
    case SDL_MOUSEBUTTONUP:
        mouseDown = false;
        send("M_UP");
        break;
    }
}

void MyGame::update() {
    if (mouseDown == 1)
    {
        int x, y;
        SDL_GetMouseState(&x, &y);
        send("X_" + std::to_string(x));
        send("Y_" + std::to_string(y));
        draw.calcPoints(game_data.mouseX, game_data.mouseY, game_data.mouseDown);
    }
}

void MyGame::render(SDL_Renderer* renderer) {
    //background
    SDL_SetRenderDrawColor(renderer, 72, 75, 94, 255);
    SDL_RenderFillRect(renderer, &BGRect);


    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);
    SDL_RenderFillRect(renderer, draw.getRect());

    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);
    draw.renderPoints(renderer);
}