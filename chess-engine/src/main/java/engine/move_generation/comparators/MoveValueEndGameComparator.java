package engine.move_generation.comparators;

import model.Move;
import java.util.Comparator;

public class MoveValueEndGameComparator implements Comparator<Move> {
    @Override
    public int compare(Move m1, Move m2) {
        int m1Value = calculateMoveValue(m1);
        int m2Value = calculateMoveValue(m2);

        return Integer.compare(m2Value, m1Value); // Sort in descending order by default
    }

    public static int calculateMoveValue(Move move) {
        int captureValue = (move.getCapturedPiece() != null)
                ? move.getCapturedPiece().getPieceValue(false) * 5  // Give a higher weight to captures
                : 0;

        int positionalValue = move.getPositionGain();

        int protectionBonus = move.isProtected() ? 500 : 0;

        int attackPenalty = move.isAttacked() ? -move.getAttackPenalty() : 0;

        return captureValue + positionalValue + protectionBonus + attackPenalty;
    }
}
