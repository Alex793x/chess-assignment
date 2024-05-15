package engine.evaluations;

import engine.evaluations.material_board.PeSTOEvaluation;
import engine.evaluations.protection_board.ProtectionEvaluator;
import model.Bitboards;
import model.Board;
import model.enums.PieceColor;

public class Evaluator {

    // Define weights for each component of the evaluation
    private static final double BASE_EVALUATION_WEIGHT = 0.5;
    private static final double PROTECTION_SCORE_WEIGHT = 0.3;
    private static final double MOBILITY_SCORE_WEIGHT = 0.2;
    private static final double KING_SAFETY_WEIGHT = 0.2;

    public static int evaluateStaticBoard(Board board) {
        int baseEvaluation = PeSTOEvaluation.eval(board, board.getBitboard());
//        System.out.println("Base Evaluation (PeSTO): " + baseEvaluation);

        int protectionScore = ProtectionEvaluator.calculateProtectionScore(board.getBitboard());
//        System.out.println("Protection Score: " + protectionScore);

        int mobilityScore = calculateMobilityScore(board.getBitboard());
//        System.out.println("Mobility Score: " + mobilityScore);

        int kingSafetyScore = calculateKingSafetyScore(board.getBitboard());
//        System.out.println("King Safety Score: " + kingSafetyScore);

        // Calculate the total score using the weights
        double totalScore =
                (BASE_EVALUATION_WEIGHT * baseEvaluation) +
                        (PROTECTION_SCORE_WEIGHT * protectionScore) +
                        (MOBILITY_SCORE_WEIGHT * mobilityScore) +
                        (KING_SAFETY_WEIGHT * kingSafetyScore);

//        System.out.println("Total Evaluation Score: " + totalScore);

        return (int) totalScore;
    }

    private static int calculateMobilityScore(Bitboards bitboards) {
        int whiteMobility = bitboards.calculateMobility(PieceColor.WHITE);
        int blackMobility = bitboards.calculateMobility(PieceColor.BLACK);

        return whiteMobility - blackMobility; // Higher mobility for white is positive, for black is negative
    }

    private static int calculateKingSafetyScore(Bitboards bitboards) {
        int[] kingSafetyLevels = bitboards.calculateKingSafetyLevels();
        int whiteKingSafety = kingSafetyLevels[PieceColor.WHITE.ordinal()];
        int blackKingSafety = kingSafetyLevels[PieceColor.BLACK.ordinal()];

        return whiteKingSafety - blackKingSafety; // Positive if white's king is safer, negative if black's king is safer
    }
}
