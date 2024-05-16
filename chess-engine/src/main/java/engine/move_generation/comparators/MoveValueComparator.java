package engine.move_generation.comparators;

import evaluation.PieceEvaluator;
import evaluation.pst.PSTHandler;
import model.Move;

import java.util.Comparator;

public class MoveValueComparator implements Comparator<Move> {
    @Override
    public int compare(Move m1, Move m2) {
        int m1Value = calculateMoveValue(m1);
        int m2Value = calculateMoveValue(m2);

        return Integer.compare(m2Value, m1Value); // Sort in descending order by default
    }

    public static int calculateMoveValue(Move move) {
        int promotionBonus = isPromotionMove(move) ? 2000 : 0;
        int attackerValue = PieceEvaluator.getPieceValue(move.getPiece());
        int captureValue = (move.getCapturedPiece() != '\u0000')
                ? PieceEvaluator.getPieceValue(move.getCapturedPiece())  // Give a higher weight to captures
                : 0;

        int positionalValue = PSTHandler.getPiecePositionPSTValue(move.getDestinationPosition()[0], move.getDestinationPosition()[1], move.getPiece());

        // Additional weights
        int captureWeight = 1000; // High weight to prioritize captures
        int positionalWeight = 1;  // Weight for positional values
        int tradeWeight = 100;     // Weight to penalize bad trades

        // Calculate trade value to penalize bad trades
        int tradeValue = (captureValue - attackerValue) * tradeWeight;


        // Total value calculation

        return (captureValue * captureWeight) + (positionalValue * positionalWeight) + tradeValue + promotionBonus;
    }

    public static boolean isPromotionMove(Move move) {
        return (move.getPiece() == 'P' || move.getPiece() == 'p') && (move.getDestinationPosition()[0] == 0 || move.getDestinationPosition()[0] == 7);
    }
}
