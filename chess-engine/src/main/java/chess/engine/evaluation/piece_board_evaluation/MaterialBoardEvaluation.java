package chess.engine.evaluation.piece_board_evaluation;

import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;

import java.util.Arrays;
import java.util.Objects;

/**
 * Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
 * PeSTO's Evaluation Function Source: <a href="https://www.chessprogramming.org/PeSTO%27s_Evaluation_Function">ChessWiki</a>
 */

public final class MaterialBoardEvaluation {

    private static final int GAMEPHASEMINEG = 518;
    private static final int GAMEPHASEMAXMG = 6192;
    private static final int GAMEPHASERANGE = GAMEPHASEMAXMG - GAMEPHASEMINEG;

    public static boolean isMidgamePhase(Board board) {
        int gamePhaseValue = 0;

        // Calculate game phase value based on the presence and types of pieces on the board
        for (PieceType pieceType : PieceType.values()) {
            long whitePieceBitboard = board.getPieceBitboard(pieceType, PieceColor.WHITE);
            long blackPieceBitboard = board.getPieceBitboard(pieceType, PieceColor.BLACK);

            // This calculation assumes PieceSquareTables.getGamePhaseInc returns values suited for game phase calculation
            int whitePieceCount = Long.bitCount(whitePieceBitboard);
            int blackPieceCount = Long.bitCount(blackPieceBitboard);
            gamePhaseValue += (whitePieceCount + blackPieceCount) * PieceSquareTables.getGamePhaseInc(pieceType);
        }

        // Consider the game to be in the midgame if the game phase value is less than GAMEPHASEMAXMG
        // and greater than or equal to GAMEPHASEMINEG
        return gamePhaseValue >= GAMEPHASEMINEG && gamePhaseValue < GAMEPHASEMAXMG;
    }

    public static int eval(Board board) {
        int[] midGameScore = new int[PieceColor.values().length];
        int[] endGameScore = new int[PieceColor.values().length];
        int gamePhase = 0;

        // Calculate scores and game phase based on bitboards
        for (PieceType pieceType : PieceType.values()) {
            for (PieceColor pieceColor : PieceColor.values()) {
                long pieceBitboard = board.getPieceBitboard(pieceType, pieceColor);
                while (pieceBitboard != 0) {
                    int square = Long.numberOfTrailingZeros(pieceBitboard);
                    midGameScore[pieceColor.ordinal()] += PieceSquareTables.getMidGameValue(pieceType, pieceColor, square);
                    endGameScore[pieceColor.ordinal()] += PieceSquareTables.getEndgameValue(pieceType, pieceColor, square);
                    gamePhase += PieceSquareTables.getGamePhaseInc(pieceType);
                    pieceBitboard &= pieceBitboard - 1;  // Clear the least significant set bit
                }
            }
        }

        // Calculate the tapered evaluation
        return taperedEval(gamePhase, midGameScore, endGameScore, board.getCurrentPlayer());
    }

    /**
     * Computes the tapered evaluation based on the game phase.
     */
    private static int taperedEval(int gamePhase, int[] midGameScore, int[] endGameScore, PieceColor currentPlayer) {
        int currentPlayerIndex = currentPlayer.ordinal();
        int opponentIndex = currentPlayer.opposite().ordinal();
        int midGameScoreDifference = midGameScore[currentPlayerIndex] - midGameScore[opponentIndex];
        int endGameScoreDifference = endGameScore[currentPlayerIndex] - endGameScore[opponentIndex];

        int effectiveGamePhase = Math.max(GAMEPHASEMINEG, Math.min(GAMEPHASEMAXMG, gamePhase));
        double factorMG = (double) (effectiveGamePhase - GAMEPHASEMINEG) / GAMEPHASERANGE;
        double factorEG = 1 - factorMG;

        return (int) (midGameScoreDifference * factorMG + endGameScoreDifference * factorEG);
    }
}
