package model;

import lombok.Getter;

@Getter
public class MoveWithBoard {
        private final Move move;
        private final char[][] board;

        public MoveWithBoard(Move move, char[][] board) {
            this.move = move;
            this.board = board;
        }

}