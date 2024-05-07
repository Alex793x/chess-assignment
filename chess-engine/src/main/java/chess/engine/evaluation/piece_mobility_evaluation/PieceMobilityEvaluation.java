package chess.engine.evaluation.piece_mobility_evaluation;

import chess.ai_player.move_generation.PawnMoveGenerator;
import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.pre_computations.PreComputationHandler;

import static chess.engine.pre_computations.PreComputationHandler.getQueenAttacks;
import static chess.engine.pre_computations.PreComputationHandler.getRookAttacks;

public final class PieceMobilityEvaluation {


    public static int evaluateMobility(Board board, PieceColor color) {
        int mobilityScore = 0;

        // Evaluate mobility for each piece type
        int pawnMobility = evaluatePawnMobility(board, color);
        int knightMobility = evaluateKnightMobility(board, color);
        int bishopMobility = evaluateBishopMobility(board, color);
        int rookMobility = evaluateRookMobility(board, color);
        int queenMobility = evaluateQueenMobility(board, color);
        int kingMobility = evaluateKingMobility(board, color);

        mobilityScore += pawnMobility;
        mobilityScore += knightMobility;
        mobilityScore += bishopMobility;
        mobilityScore += rookMobility;
        mobilityScore += queenMobility;
        mobilityScore += kingMobility;

        // Log the mobility scores for each piece type
        System.out.println(color + " Pawn Mobility: " + pawnMobility);
        System.out.println(color + " Knight Mobility: " + knightMobility);
        System.out.println(color + " Bishop Mobility: " + bishopMobility);
        System.out.println(color + " Rook Mobility: " + rookMobility);
        System.out.println(color + " Queen Mobility: " + queenMobility);
        System.out.println(color + " King Mobility: " + kingMobility);

        return mobilityScore;
    }

    /**
     * Evaluates the mobility of pawns for a given color on the board.
     *
     * @param board the current state of the chess board
     * @param color the color of the pawns to evaluate mobility for
     * @return the mobility score for pawns of the given color
     *
     * This method evaluates the mobility of pawns for a given color. It retrieves the bitboard
     * representation of the pawns using the {@link Board#getPieceBitboard} method. It then iterates
     * over each pawn position using bitboard manipulation techniques. For each pawn position, it
     * calls the {@link #evaluatePawnMobilityForSquare} method to calculate the mobility score for
     * that specific pawn. The mobility scores of all pawns are summed up and returned as the total
     * mobility score for pawns of the given color.
     */

    public static int evaluatePawnMobility(Board board, PieceColor color) {
        int mobilityScore = 0;
        long pawnBitboard = board.getPieceBitboard(PieceType.PAWN, color);

        while (pawnBitboard != 0) {
            int square = Long.numberOfTrailingZeros(pawnBitboard);
            mobilityScore += evaluatePawnMobilityForSquare(board, square, color);
            pawnBitboard &= pawnBitboard - 1;
        }

        return mobilityScore;
    }

    /**
     * Evaluates the mobility of a pawn on a specific square.
     *
     * @param board the current state of the chess board
     * @param square the square index of the pawn to evaluate mobility for
     * @param color the color of the pawn
     * @return the mobility score for the pawn on the given square
     *
     * This method evaluates the mobility of a pawn on a specific square. It considers two factors:
     * the ability to move forward and the ability to capture diagonally. If the pawn can move forward
     * (i.e., the square in front of the pawn is empty), a mobility score of 10 is added. If the pawn
     * can capture diagonally (i.e., there is an enemy piece on either of the diagonally adjacent squares),
     * a mobility score of 5 is added. The total mobility score for the pawn on the given square is returned.
     */
    public static int evaluatePawnMobilityForSquare(Board board, int square, PieceColor color) {
        int mobilityScore = 0;
        long allOccupancies = board.getBitboard().getOccupancies(PieceColor.WHITE) | board.getBitboard().getOccupancies(PieceColor.BLACK);
        long enemyOccupancies = board.getBitboard().getOccupancies(color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);
        long pawnBitboard = 1L << square;

        int direction = color == PieceColor.WHITE ? 8 : -8;
        long singleMoveTarget = color == PieceColor.WHITE ? pawnBitboard << 8 : pawnBitboard >>> 8;
        long doubleMoveTarget = color == PieceColor.WHITE ? pawnBitboard << 16 : pawnBitboard >>> 16;
        long startRankMask = color == PieceColor.WHITE ? PawnMoveGenerator.WHITE_START_RANK_MASK_FOR_DOUBLE_FORWARD_MOVE : PawnMoveGenerator.BLACK_START_RANK_MASK_FOR_DOUBLE_FORWARD_MOVE;

        // Single step forward move
        if ((singleMoveTarget & allOccupancies) == 0) {
            mobilityScore += 20;
        }

        // Double step forward move
        if ((pawnBitboard & startRankMask) != 0 && (singleMoveTarget & allOccupancies) == 0 && (doubleMoveTarget & allOccupancies) == 0) {
            mobilityScore += 10;
        }

        // Capture moves to the left and right
        long leftCaptureTarget = color == PieceColor.WHITE ? pawnBitboard << 7 : pawnBitboard >>> 9;
        long rightCaptureTarget = color == PieceColor.WHITE ? pawnBitboard << 9 : pawnBitboard >>> 7;

        if ((leftCaptureTarget & enemyOccupancies) != 0) {
            mobilityScore += 30;
        }
        if ((rightCaptureTarget & enemyOccupancies) != 0) {
            mobilityScore += 30;
        }

        return mobilityScore;
    }

    public static int evaluateKnightMobility(Board board, PieceColor color) {
        int mobilityScore = 0;
        long knightBitboard = board.getPieceBitboard(PieceType.KNIGHT, color);
        long allOccupancies = board.getBitboard().getOccupancies(PieceColor.WHITE) | board.getBitboard().getOccupancies(PieceColor.BLACK);
        long enemyOccupancies = board.getBitboard().getOccupancies(color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);

        while (knightBitboard != 0) {
            int square = Long.numberOfTrailingZeros(knightBitboard);
            long knightAttacks = PreComputationHandler.KNIGHT_ATTACKS[square];
            long captures = knightAttacks & enemyOccupancies;
            long legalMoves = knightAttacks & ~board.getBitboard().getOccupancies(color);

            legalMoves &= ~captures;

            mobilityScore += Long.bitCount(legalMoves) * 20; // Assign a higher score for legal moves
            mobilityScore += Long.bitCount(captures) * 30; // Assign an even higher score for captures

            knightBitboard &= knightBitboard - 1;
        }

        return mobilityScore;
    }

    public static int evaluateBishopMobility(Board board, PieceColor color) {
        int mobilityScore = 0;
        long bishopBitboard = board.getPieceBitboard(PieceType.BISHOP, color);
        long allOccupancies = board.getBitboard().getOccupancies(PieceColor.WHITE) | board.getBitboard().getOccupancies(PieceColor.BLACK);
        long enemyOccupancies = board.getBitboard().getOccupancies(color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);

        while (bishopBitboard != 0) {
            int square = Long.numberOfTrailingZeros(bishopBitboard);
            long bishopAttacks = PreComputationHandler.getBishopAttacks(square, allOccupancies);
            long legalMoves = bishopAttacks & ~board.getBitboard().getOccupancies(color);
            long captures = bishopAttacks & enemyOccupancies;

            mobilityScore += Long.bitCount(legalMoves) * 20; // Assign a higher score for legal moves
            mobilityScore += Long.bitCount(captures) * 30; // Assign an even higher score for captures

            bishopBitboard &= bishopBitboard - 1;
        }

        return mobilityScore;
    }

    public static int evaluateRookMobility(Board board, PieceColor color) {
        int mobilityScore = 0;
        long rookBitboard = board.getPieceBitboard(PieceType.ROOK, color);
        long allOccupancies = board.getBitboard().getOccupancies(PieceColor.WHITE) | board.getBitboard().getOccupancies(PieceColor.BLACK);
        long enemyOccupancies = board.getBitboard().getOccupancies(color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);

        while (rookBitboard != 0) {
            int square = Long.numberOfTrailingZeros(rookBitboard);
            long rookAttacks = getRookAttacks(square, allOccupancies);
            long legalMoves = rookAttacks & ~board.getBitboard().getOccupancies(color);
            long captures = rookAttacks & enemyOccupancies;

            legalMoves &= ~captures;
            mobilityScore += Long.bitCount(legalMoves) * 20;
            mobilityScore += Long.bitCount(captures) * 30;

            rookBitboard &= rookBitboard - 1;
        }

        return mobilityScore;
    }

    public static int evaluateQueenMobility(Board board, PieceColor color) {
        int mobilityScore = 0;
        long queenBitboard = board.getPieceBitboard(PieceType.QUEEN, color);
        long allOccupancies = board.getBitboard().getOccupancies(PieceColor.WHITE) | board.getBitboard().getOccupancies(PieceColor.BLACK);
        long enemyOccupancies = board.getBitboard().getOccupancies(color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);

        while (queenBitboard != 0) {
            int square = Long.numberOfTrailingZeros(queenBitboard);
            long queenAttacks = getQueenAttacks(square, allOccupancies);
            long legalMoves = queenAttacks & ~board.getBitboard().getOccupancies(color);
            long captures = queenAttacks & enemyOccupancies;

            legalMoves &= ~captures;
            mobilityScore += Long.bitCount(legalMoves) * 20;
            mobilityScore += Long.bitCount(captures) * 30;

            queenBitboard &= queenBitboard - 1;
        }

        return mobilityScore;
    }


    public static int evaluateKingMobility(Board board, PieceColor color) {
        int mobilityScore = 0;
        long kingBitboard = board.getPieceBitboard(PieceType.KING, color);

        while (kingBitboard != 0) {
            int square = Long.numberOfTrailingZeros(kingBitboard);
            mobilityScore += Long.bitCount(PreComputationHandler.KING_ATTACKS[square] & ~board.getBitboard().getOccupancies(color)) * 10;
            kingBitboard &= kingBitboard - 1;
        }

        return mobilityScore;
    }
}
