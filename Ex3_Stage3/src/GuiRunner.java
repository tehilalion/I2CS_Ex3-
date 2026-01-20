public class GuiRunner {
    public static void main(String[] args) {
        exe.ex3.game.MyPacmanGame game = new exe.ex3.game.MyPacmanGame();
        game.init(0, "", true, 1, 0.1, 0, 0);
        game.play();

        Ex3Algo algo = new Ex3Algo();

        int[][] b = game.getGame(0);
        int w = b.length;
        int h = b[0].length;

        int cell = 30; // adjust if needed
        StdDraw.setCanvasSize(w * cell, h * cell);
        StdDraw.setXscale(0, w);
        StdDraw.setYscale(0, h);

        // main loop (simple)
        while (game.getStatus() == exe.ex3.game.PacmanGame.PLAY) {
            // move pacman by algo
            int dir = algo.move(game);
            game.applyPacMove(dir);
            game.move(0);

            // draw
            drawBoard(game);

            StdDraw.show();
            StdDraw.pause(60); // ~16 FPS
        }
    }

    private static void drawBoard(exe.ex3.game.MyPacmanGame g) {
        int[][] b = g.getGame(0);
        int w = b.length;
        int h = b[0].length;

        StdDraw.clear(StdDraw.WHITE);

        // tile codes: 0 empty, 1 wall, 2 food, 3 power
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

        // pacman
        String[] pp = g.getPos(0).split(",");
        int px = Integer.parseInt(pp[0].trim());
        int py = Integer.parseInt(pp[1].trim());
        StdDraw.setPenColor(StdDraw.YELLOW);
        StdDraw.filledCircle(px + 0.5, py + 0.5, 0.30);

        // ghosts
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
    }
}
