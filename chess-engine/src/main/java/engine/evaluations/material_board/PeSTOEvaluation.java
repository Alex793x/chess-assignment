package engine.evaluations.material_board;

import model.Bitboards;
import model.Board;
import model.enums.CurrentPlayer;
import model.enums.PieceColor;
import model.enums.PieceType;
import util.pst.PSTHandler;

import java.util.BitSet;

 /**
     *  Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
     *  
     */

     
public class PeSTOEvaluation {
    

    // Constants for game phase calculation
    private static final int GAMEPHASEMINEG = 518;
    private static final int GAMEPHASEMAXMG = 6192;
    private static final int GAMEPHASERANGE = GAMEPHASEMAXMG - GAMEPHASEMINEG;

    /**
     * Determines if the current game phase is midgame based on the piece distribution.
     * @param bitboards The bitboards representing the current state of the board.
     * @return True if the game phase is midgame, false otherwise.
     */
    public static boolean isMidgamePhase(Bitboards bitboards) {
        int gamePhaseValue = 0;

        // Calculate game phase value based on the presence and types of pieces on the board
        for (PieceType pieceType : PieceType.values()) {
            BitSet whitePieceBitboard = bitboards.getPieceBitboard(pieceType, PieceColor.WHITE);
            BitSet blackPieceBitboard = bitboards.getPieceBitboard(pieceType, PieceColor.BLACK);

            int whitePieceCount = whitePieceBitboard.cardinality();
            int blackPieceCount = blackPieceBitboard.cardinality();
            gamePhaseValue += (whitePieceCount + blackPieceCount) * getGamePhaseInc(pieceType);
        }

        // Ensure gamePhaseValue is at least one less than GAMEPHASEMAXMG
        gamePhaseValue = Math.min(gamePhaseValue, GAMEPHASEMAXMG - 1);

        // Check if the current game phase value falls within the defined midgame range
        return gamePhaseValue >= GAMEPHASEMINEG;
    }


     /**
     * Evaluates the board state using piece-square tables and game phase information.
     * @param board The current board state.
     * @param bitboards The bitboards representing the current state of the board.
     * @return The evaluation score for the current player.
     */

    public static int eval(Board board, Bitboards bitboards) {
        int[] midGameScore = new int[PieceColor.values().length];
        int[] endGameScore = new int[PieceColor.values().length];
        int gamePhase = 0;

        // Calculate scores and game phase based on bitboards
        for (PieceType pieceType : PieceType.values()) {
            for (PieceColor pieceColor : PieceColor.values()) {
                BitSet pieceBitboard = bitboards.getPieceBitboard(pieceType, pieceColor);
                for (int square = pieceBitboard.nextSetBit(0); square != -1; square = pieceBitboard.nextSetBit(square + 1)) {
                    // Increment scores directly using the square, type, and color
                    midGameScore[pieceColor.ordinal()] += PSTHandler.getMidGameValue(pieceType, pieceColor, square);
                    endGameScore[pieceColor.ordinal()] += PSTHandler.getEndgameValue(pieceType, pieceColor, square);
                    gamePhase += getGamePhaseInc(pieceType);
                }
            }
        }

        // Check if the current game phase value falls within the defined midgame range
        return taperedEval(gamePhase, midGameScore, endGameScore, board.getCurrentPlayer());
    }

    /**
     * Computes the tapered evaluation based on the game phase.
     * @param gamePhase The current game phase value.
     * @param midGameScore The scores for the midgame phase.
     * @param endGameScore The scores for the endgame phase.
     * @param currentPlayer The current player.
     * @return The tapered evaluation score.
     */
    private static int taperedEval(int gamePhase, int[] midGameScore, int[] endGameScore, CurrentPlayer currentPlayer) {
        int currentPlayerIndex = currentPlayer == CurrentPlayer.WHITE ? 0 : 1;
        int opponentIndex = 1 - currentPlayerIndex;
        int midGameScoreDifference = midGameScore[currentPlayerIndex] - midGameScore[opponentIndex];
        int endGameScoreDifference = endGameScore[currentPlayerIndex] - endGameScore[opponentIndex];

        
        int effectiveGamePhase = Math.max(GAMEPHASEMINEG, Math.min(GAMEPHASEMAXMG, gamePhase));
        double factorMG = (double) (effectiveGamePhase - GAMEPHASEMINEG) / GAMEPHASERANGE;
        double factorEG = 1 - factorMG;

        return (int) (midGameScoreDifference * factorMG + endGameScoreDifference * factorEG);
    }

    /**
     * Returns incremental values for each piece type based on their influence on the game phase.
     * @param pieceType The type of the piece.
     * @return The incremental value for the piece type.
     */
    public static int getGamePhaseInc(PieceType pieceType) {
        return switch (pieceType) {
            case KNIGHT -> 337;
            case BISHOP -> 365;
            case ROOK -> 477;
            case QUEEN -> 1025;
            default -> 0;  // Pawns and Kings are not included
        };
    }
    
}
