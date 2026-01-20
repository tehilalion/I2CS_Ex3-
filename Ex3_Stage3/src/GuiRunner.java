public class GuiRunner {
    public static void main(String[] args) {
        // YOUR server-side implementation (the one we created in exe.ex3.game)
        exe.ex3.game.MyPacmanGame game = new exe.ex3.game.MyPacmanGame();
        game.init(0, "", true, 1, 0.1, 0, 0);


        // YOUR algo
        Ex3Algo algo = new Ex3Algo();

        int[][] b = game.getGame(0);
        int w = b.length;
        int h = b[0].length;

        int cell = 30; // change if you want bigger/smaller
        StdDraw.setCanvasSize(w * cell, h * cell);
        StdDraw.setXscale(0, w);
        StdDraw.setYscale(0, h);
// --- WAIT FOR SPACE TO START ---
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.text(w / 2.0, h / 2.0 + 0.5, "PACMAN");
        StdDraw.text(w / 2.0, h / 2.0 - 0.5, "Press SPACE to start:)");
        StdDraw.show();

// block here until SPACE is pressed
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == ' ') break;
            }
            StdDraw.pause(20);
        }

// now start the game
        game.play();

        // Stable FPS timing (prevents stutter/lag)
        final int FRAME_MS = 33; // ~30 FPS
        long next = System.currentTimeMillis();

        // Main loop
        while (game.getStatus() == exe.ex3.game.PacmanGame.PLAY) {
            // --- INPUT (optional): WASD overrides algo, otherwise algo runs ---
            int dir;
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                dir = switch (c) {
                    case 'w' -> exe.ex3.game.PacmanGame.UP;
                    case 's' -> exe.ex3.game.PacmanGame.DOWN;
                    case 'a' -> exe.ex3.game.PacmanGame.LEFT;
                    case 'd' -> exe.ex3.game.PacmanGame.RIGHT;
                    default -> algo.move(game);
                };
            } else {
                dir = algo.move(game);
            }

            // --- UPDATE (server-side tick) ---
            game.applyPacMove(dir);
            game.move(0);

            // --- DRAW (client-side) ---
            drawBoard(game);

            // --- FRAME PACING (stable) ---
            next += FRAME_MS;
            long sleep = next - System.currentTimeMillis();
            if (sleep > 0) StdDraw.pause((int) sleep);
            else next = System.currentTimeMillis(); // resync if we fell behind
        }

        // Final draw (so you see end state)
        drawBoard(game);
        StdDraw.show();
    }

    private static void drawBoard(exe.ex3.game.MyPacmanGame g) {
        int[][] b = g.getGame(0);
        int w = b.length;
        int h = b[0].length;

        StdDraw.clear(StdDraw.BLACK);

        // OUR server tile codes:
        // 0 empty, 1 wall, 2 food, 3 power
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int v = b[x][y];
                if (v == 1) {
                    StdDraw.setPenColor(StdDraw.BLUE);
                    StdDraw.filledSquare(x + 0.5, y + 0.5, 0.50);
                } else if (v == 2) {
                    StdDraw.setPenColor(StdDraw.PINK);
                    StdDraw.filledCircle(x + 0.5, y + 0.5, 0.10);
                } else if (v == 3) {
                    StdDraw.setPenColor(StdDraw.GREEN);
                    StdDraw.filledCircle(x + 0.5, y + 0.5, 0.18);
                }
            }
        }

        // Pacman
        String[] pp = g.getPos(0).split(",");
        int px = Integer.parseInt(pp[0].trim());
        int py = Integer.parseInt(pp[1].trim());
        StdDraw.setPenColor(StdDraw.YELLOW);
        StdDraw.filledCircle(px + 0.5, py + 0.5, 0.30);

        // Ghosts
        exe.ex3.game.GhostCL[] ghosts = g.getGhosts(0);
        if (ghosts != null) {
            for (exe.ex3.game.GhostCL gh : ghosts) {
                int gx = gh.getX();
                int gy = gh.getY();

                if (gh.remainTimeAsEatable(0) > 0) {
                    StdDraw.setPenColor(StdDraw.CYAN);
                    StdDraw.filledCircle(gx + 0.5, gy + 0.5, 0.22);
                } else {
                    StdDraw.setPenColor(StdDraw.RED);
                    StdDraw.filledCircle(gx + 0.5, gy + 0.5, 0.30);
                }
            }
        }

        // HUD (safe: uses getData + getStatus, no guessing score method names)
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(0.6, h - 0.3, g.getData(0) + "   status=" + g.getStatus());

        StdDraw.show();
    }
}
