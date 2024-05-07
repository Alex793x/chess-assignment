package chess.engine.evaluation.piece_board_evaluation;


import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;

/**
 * Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
 */

/**
 * The MaterialBoardEvaluation class provides methods for evaluating the current position of the board
 * based on the material balance and piece-square tables using bitboards.
 *
 * The isMidgamePhase method provides a binary classification of the game phase,
 * while the tapered evaluation formula calculates a continuous score that reflects the gradual transition between the midgame and endgame phases.
 *
 */
public final class MaterialBoardEvaluation {

    private static final int MID_GAME_THRESHOLD = 10;

    public static boolean isMidgamePhase(Board board) {
        int gamePhaseValue = 0;

        for (PieceType pieceType : PieceType.values()) {
            long whitePieceBitboard = board.getPieceBitboard(pieceType, PieceColor.WHITE); // Get whites piece bitboards
            long blackPieceBitboard = board.getPieceBitboard(pieceType, PieceColor.BLACK); // Get blacks piece bitboards
            int pieceCount = Long.bitCount(whitePieceBitboard | blackPieceBitboard); // Count the number of pieces of a specific type
            gamePhaseValue += pieceCount * PieceSquareTables.getGamePhaseInc(pieceType);
        }

        return gamePhaseValue >= MID_GAME_THRESHOLD;
    }
    /**
     * Evaluates the current position of the board and returns a numerical score.
     * PeSTO's Evaluation Function Source: <a href="https://www.chessprogramming.org/PeSTO%27s_Evaluation_Function">ChessWiki</a>
     * @param board the Board object representing the current state of the chess board
     * @return an integer value representing the evaluation score of the current position
     */
    public static int eval(Board board) {
        int[] midGameScore = new int[2];
        int[] endGameScore = new int[2];
        int gamePhase = 0;

        /**
         * Iterate over each piece type and color using nested loops.
         */
        for (PieceType pieceType : PieceType.values()) {
            for (PieceColor pieceColor : PieceColor.values()) {
                /**
                 * Retrieve the corresponding bitboard for the current piece type and color.
                 */
                long pieceBitboard = board.getPieceBitboard(pieceType, pieceColor);

                /**
                 * Use a while loop to iterate over the set bits in the bitboard.
                 * The loop continues as long as there are set bits remaining.
                 */
                while (pieceBitboard != 0) {
                    /**
                     * Find the index of the least significant set bit, which represents the square
                     * where the piece is located.
                     */
                    int square = Long.numberOfTrailingZeros(pieceBitboard);

                    /**
                     * Update the midGameScore and endGameScore arrays based on the piece color
                     * and the values obtained from PieceSquareTables.
                     */
                    if (pieceColor == PieceColor.WHITE) {
                        midGameScore[PieceColor.WHITE.ordinal()] += PieceSquareTables.getMidGameValue(pieceType, pieceColor, square);
                        endGameScore[PieceColor.WHITE.ordinal()] += PieceSquareTables.getEndgameValue(pieceType, pieceColor, square);
                    } else {
                        midGameScore[PieceColor.BLACK.ordinal()] += PieceSquareTables.getMidGameValue(pieceType, pieceColor, square);
                        endGameScore[PieceColor.BLACK.ordinal()] += PieceSquareTables.getEndgameValue(pieceType, pieceColor, square);
                    }

                    /**
                     * Increment the gamePhase variable by the value obtained from PieceSquareTables.getGamePhaseInc(pieceType).
                     */
                    gamePhase += PieceSquareTables.getGamePhaseInc(pieceType);

                    /**
                     * Clear the least significant set bit in the bitboard to move on to the next set bit
                     * in the next iteration.
                     */
                    pieceBitboard &= pieceBitboard - 1;
                }
            }
        }

        /**
         * Calculate the tapered evaluation based on the game phase.
         */
        int midGameScoreDifference = midGameScore[board.getCurrentPlayer().ordinal()] - midGameScore[board.getCurrentPlayer().opposite().ordinal()];
        int endGameScoreDifference = endGameScore[board.getCurrentPlayer().ordinal()] - endGameScore[board.getCurrentPlayer().opposite().ordinal()];
        int midGamePhase = Math.min(gamePhase, 24);
        int endGamePhase = 24 - midGamePhase;

        /**
         * This formula calculates a weighted average of the midgame and endgame score differences based on the game phase.
         * The division by 24 normalizes the result to a value between the midgame and endgame evaluations.
         * The tapered evaluation approach is used to balance the importance of piece activity and king safety in different
         * stages of the game. It takes into account the fact that certain positional factors may be more important in the midgame compared to the endgame, and vice versa.
         */
        return (midGameScoreDifference * midGamePhase + endGameScoreDifference * endGamePhase) / 24;
    }
}

