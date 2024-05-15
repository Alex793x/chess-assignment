package engine.move_generation.comparators;

import model.Move;

import java.util.Comparator;

public class MoveValueMidGameComparator implements Comparator<Move> {
    public int compare(Move m1, Move m2) {
        int m1Value = calculateMoveValue(m1);
        int m2Value = calculateMoveValue(m2);

        return Integer.compare(m2Value, m1Value); // Sort in descending order by default
    }

    public static int calculateMoveValue(Move move) {
        int captureValue = 0;
        if (move.getCapturedPiece() != null) {
            // MVV-LVA (Most Valuable Victim - Least Valuable Attacker)
            int capturedValue = move.getCapturedPiece().getPieceValue(false);
            int attackerValue = move.getPiece().getPieceValue(false);
            captureValue = capturedValue * 10 - attackerValue; // Higher weight to more valuable captures
        }

        if (move.isPromotion()) {
            captureValue += 1000;
        }

        int positionalValue = move.getPositionGain();
        int protectionBonus = move.isProtected() ? 500 : 0;
        int attackPenalty = move.isAttacked() ? -move.getAttackPenalty() : 0;

        return captureValue + positionalValue + protectionBonus + attackPenalty;
    }

}

