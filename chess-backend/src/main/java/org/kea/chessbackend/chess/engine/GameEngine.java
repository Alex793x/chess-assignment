package org.kea.chessbackend.chess.engine;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.kea.chessbackend.chess.board.Board;

@Data
@RequiredArgsConstructor
public class GameEngine {

    protected final static Board chessBoard = new Board();
}
