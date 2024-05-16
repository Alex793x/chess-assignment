import game.GameEngine;
import model.Chessboard;

public class Main {
    public static void main(String[] args) {
        Chessboard chessboard = new Chessboard();
        chessboard.initilizeBoard();
        GameEngine.playChess(chessboard);

    }
}

