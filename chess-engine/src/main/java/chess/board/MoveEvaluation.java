package chess.board;

import lombok.Getter;

@Getter
public final class MoveEvaluation {
        private final int move;
        private final int staticExchangeScore;
        private final int mobilityScore;

        public MoveEvaluation(int move, int staticExchangeScore, int mobilityScore) {
            this.move = move;
            this.staticExchangeScore = staticExchangeScore;
            this.mobilityScore = mobilityScore;
        }

}