package chess.engine.evaluation;

import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.evaluation.piece_board_evaluation.MaterialBoardEvaluation;
import chess.engine.evaluation.piece_board_evaluation.PieceSquareEvaluation;
import chess.engine.evaluation.piece_board_evaluation.PieceSquareTables;
import chess.engine.evaluation.piece_mobility_evaluation.PieceMobilityEvaluation;
import chess.engine.pre_computations.PreComputationHandler;

public final class GameStateEvaluation {

    private static final int MATERIAL_WEIGHT = 100;
    private static final int MOBILITY_WEIGHT = 30;
    private static final int KING_SAFETY_WEIGHT = 20;
    private static final int PROTECTION_WEIGHT = 30;

    public static int FullGameStateEvaluationExpert(Board board) {
        // Focus solely on material and basic positional gains
        int score =  MaterialBoardEvaluation.eval(board);
        return board.getCurrentPlayer() == PieceColor.WHITE ? score : -score;
    }


    public static int FullGameStateEvaluation(Board board) {
        int materialAndPositionRating = MaterialBoardEvaluation.eval(board); // Assumed to include PST
        int mobilityScore = evaluateMobility(board);
        int kingSafetyScore = evaluateKingSafety(board);
        int protectionScore = evaluateProtection(board, board.getCurrentPlayer() == PieceColor.WHITE ? PieceColor.WHITE : PieceColor.BLACK);

        int totalScore = (materialAndPositionRating * MATERIAL_WEIGHT) +
                (mobilityScore * MOBILITY_WEIGHT) +
                (kingSafetyScore * KING_SAFETY_WEIGHT) +
                (protectionScore * PROTECTION_WEIGHT);

        // Adjust the score based on the current player - negate for Black to make it relative.
        return board.getCurrentPlayer() == PieceColor.WHITE ? totalScore : -totalScore;
    }

    public static int whiteScoreBoardPosition(Board board) {
        int whiteScore = 0;
        for (PieceType pieceType : PieceType.values()) {
            if (MaterialBoardEvaluation.isMidgamePhase(board)) {
                whiteScore += PieceSquareEvaluation.evaluatePiecePosition(board, PieceSquareTables.getMidGamePieceSquareTable(pieceType, PieceColor.WHITE), pieceType, PieceColor.WHITE);
            } else {
                whiteScore += PieceSquareEvaluation.evaluatePiecePosition(board, PieceSquareTables.getEndgamePieceSquareTable(pieceType, PieceColor.WHITE), pieceType, PieceColor.WHITE);
            }
        }
        return whiteScore;
    }

    public static int blackScoreBoardPosition(Board board) {
        int blackScore = 0;
        for (PieceType pieceType : PieceType.values()) {
            if (MaterialBoardEvaluation.isMidgamePhase(board)) {
                blackScore += PieceSquareEvaluation.evaluatePiecePosition(board, PieceSquareTables.getMidGamePieceSquareTable(pieceType, PieceColor.BLACK), pieceType, PieceColor.BLACK);
            } else {
                blackScore += PieceSquareEvaluation.evaluatePiecePosition(board, PieceSquareTables.getEndgamePieceSquareTable(pieceType, PieceColor.BLACK), pieceType, PieceColor.BLACK);
            }
        }
        return blackScore;
    }


    private static int evaluateMobility(Board board) {
        return PieceMobilityEvaluation.evaluateMobility(board, PieceColor.WHITE) -
                PieceMobilityEvaluation.evaluateMobility(board, PieceColor.BLACK);
    }

    private static int evaluateKingSafety(Board board) {
        return getKingSafetyScore(board, PieceColor.WHITE) -
                getKingSafetyScore(board, PieceColor.BLACK);
    }

    private static int getKingSafetyScore(Board board, PieceColor color) {
        int kingSquare = board.getKingPosition(color);
        // Basic king safety: count the number of attacking pieces
        long enemyAttacks = getThreatBitboard(board, kingSquare, color.opposite());
        int numAttackers = Long.bitCount(enemyAttacks);
        return -numAttackers; // More attackers = lower score
    }

    private static long getThreatBitboard(Board board, int square, PieceColor enemyColor) {
        if (square == -1) { // Handle missing king
            return 0L; // No threats if the king is not on the board (should not happen in a normal game)
        }
        long threatBitboard = 0L;
        long allOccupancies = board.getBitboard().getAllOccupancies();

        // Pawn attacks
        threatBitboard |= (enemyColor == PieceColor.WHITE ? PreComputationHandler.WHITE_PAWN_ATTACKS[square] :
                PreComputationHandler.BLACK_PAWN_ATTACKS[square]) &
                board.getBitboard().getOccupancies(enemyColor);

        // Knight attacks
        threatBitboard |= PreComputationHandler.KNIGHT_ATTACKS[square] &
                board.getBitboard().getOccupancies(enemyColor);

        // Bishop attacks
        threatBitboard |= PreComputationHandler.getBishopAttacks(square, allOccupancies) &
                board.getBitboard().getOccupancies(enemyColor);

        // Rook attacks
        threatBitboard |= PreComputationHandler.getRookAttacks(square, allOccupancies) &
                board.getBitboard().getOccupancies(enemyColor);

        // Queen attacks
        threatBitboard |= PreComputationHandler.getQueenAttacks(square, allOccupancies) &
                board.getBitboard().getOccupancies(enemyColor);

        threatBitboard |= PreComputationHandler.KING_ATTACKS[square] &
                board.getBitboard().getOccupancies(enemyColor);

        return threatBitboard;
    }

    public static int evaluateProtection(Board board, PieceColor color) {
        int protectionScore = 0;
        long allFriendlyPieces = board.getBitboard().getOccupancies(color);
        while (allFriendlyPieces != 0) {
            int square = Long.numberOfTrailingZeros(allFriendlyPieces);
            long protectors = getProtectorsBitboard(board, square, color);
            protectionScore += Long.bitCount(protectors);
            allFriendlyPieces &= allFriendlyPieces - 1; // Process the next piece
        }
        return protectionScore;
    }


    private static long getProtectorsBitboard(Board board, int square, PieceColor color) {
        long protectorsBitboard = 0L;
        long allOccupancies = board.getBitboard().getAllOccupancies();
        long friendlyPieces = board.getBitboard().getOccupancies(color);

        // Calculate potential protector pieces from pawns
        long pawnProtectors = (color == PieceColor.WHITE)
                ? PreComputationHandler.WHITE_PAWN_ATTACKS[square] & friendlyPieces
                : PreComputationHandler.BLACK_PAWN_ATTACKS[square] & friendlyPieces;
        protectorsBitboard |= pawnProtectors;

        // Calculate potential protector pieces from knights
        long knightProtectors = PreComputationHandler.KNIGHT_ATTACKS[square] & board.getBitboard().getBitboardForPieceTypeAndColor(PieceType.KNIGHT, color);
        protectorsBitboard |= knightProtectors;

        // Calculate potential protector pieces from bishops
        long bishopProtectors = PreComputationHandler.getBishopAttacks(square, allOccupancies) & board.getBitboard().getBitboardForPieceTypeAndColor(PieceType.BISHOP, color);
        protectorsBitboard |= bishopProtectors;

        // Calculate potential protector pieces from rooks
        long rookProtectors = PreComputationHandler.getRookAttacks(square, allOccupancies) & board.getBitboard().getBitboardForPieceTypeAndColor(PieceType.ROOK, color);
        protectorsBitboard |= rookProtectors;

        // Calculate potential protector pieces from queens
        long queenProtectors = PreComputationHandler.getQueenAttacks(square, allOccupancies) & board.getBitboard().getBitboardForPieceTypeAndColor(PieceType.QUEEN, color);
        protectorsBitboard |= queenProtectors;

        // Calculate potential protector pieces from the king
        long kingProtectors = PreComputationHandler.KING_ATTACKS[square] & board.getBitboard().getBitboardForPieceTypeAndColor(PieceType.KING, color);
        protectorsBitboard |= kingProtectors;

        return protectorsBitboard;
    }




}
