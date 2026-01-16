package mygame.server;

public class GameBoard {
    private int[][] board;
    private int width;
    private int height;

    public GameBoard(int[][] board) {
        this.board = board;
        this.width = board.length;
        this.height = board[0].length;
    }

    public int getCell(int x, int y) {
        return board[x][y];
    }

    public void setCell(int x, int y, int value) {
        board[x][y] = value;
    }

    public boolean isWall(int x, int y) {
        return board[x][y] != 0; // adjust based on your color codes
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
