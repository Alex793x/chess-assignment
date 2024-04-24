package org.kea.chessbackend.chess_test_engine.engine;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.kea.chessbackend.chess_test_engine.board.Board;

@Data
@RequiredArgsConstructor
public class GameEngine {

    protected final static Board chessBoard = new Board();
}
