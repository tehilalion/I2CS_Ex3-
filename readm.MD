Pacman Ex3 – Server Side Implementation

This project implements a full Pacman game with a clear separation
between client (GUI) and server (game logic).

Server Side:
- Implemented in exe.ex3.game.MyPacmanGame
- Manages the board, Pacman movement, ghost movement, collisions
- Tile encoding:
  0 = empty
  1 = wall
  2 = food
  3 = power pellet
- Game tick order:
    1. Pacman movement
    2. Eating food / power
    3. Ghost movement
    4. Collision detection
    5. Win / lose condition

Client Side:
- Implemented using StdDraw
- Renders the board, Pacman, ghosts
- Game starts only after pressing SPACE

The project is runnable via Ex3_3.jar.
