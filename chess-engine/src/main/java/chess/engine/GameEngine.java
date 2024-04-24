package chess.engine;

import chess.board.Board;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GameEngine {

    protected final static Board chessBoard = new Board();
}
