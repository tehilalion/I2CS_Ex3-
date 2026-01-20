public class TestServer {
    public static void main(String[] args) {
        exe.ex3.game.MyPacmanGame game = new exe.ex3.game.MyPacmanGame();
        game.init(0, "", true, 1, 0.1, 0, 0);
        game.play();

        Ex3Algo algo = new Ex3Algo();

        for (int i = 0; i < 30; i++) {
            int dir = algo.move(game);
            game.applyPacMove(dir);
            game.move(0);
            System.out.println(game.getPos(0));
        }
    }
}
