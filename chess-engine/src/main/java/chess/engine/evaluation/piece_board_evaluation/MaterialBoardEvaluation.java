package chess.engine.evaluation.piece_board_evaluation;


import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;

/**
 * Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
 */
public final class MaterialBoardEvaluation {

    public static int eval(Board board) {
        // Initialize arrays to store midgame and endgame scores for each side
        int[] midGameScore = new int[2];
        int[] endGameScore = new int[2];
        int gamePhase = 0;

        // Evaluate each piece on the board
        for (int square = 0; square < 64; ++square) {
            PieceType pieceType = board.getPieceTypeAtSquare(square);
            if (pieceType != null) {
                PieceColor pieceColor = board.getPieceColorAtSquare(square);
                if (pieceColor == PieceColor.WHITE) {
                    midGameScore[PieceColor.WHITE.ordinal()] += PieceSquareTables.getMidgameValue(pieceType, pieceColor, square);
                    endGameScore[PieceColor.WHITE.ordinal()] += PieceSquareTables.getEndgameValue(pieceType, pieceColor, square);
                } else {
                    midGameScore[PieceColor.BLACK.ordinal()] += PieceSquareTables.getMidgameValue(pieceType, pieceColor, square);
                    endGameScore[PieceColor.BLACK.ordinal()] += PieceSquareTables.getEndgameValue(pieceType, pieceColor, square);
                }
                gamePhase += PieceSquareTables.getGamePhaseInc(pieceType);
            }
        }

        // Calculate the tapered evaluation based on the game phase
        int midGameScoreDifference = midGameScore[board.getCurrentPlayer().ordinal()] - midGameScore[board.getCurrentPlayer().opposite().ordinal()];
        int endGameScoreDifference = endGameScore[board.getCurrentPlayer().ordinal()] - endGameScore[board.getCurrentPlayer().opposite().ordinal()];
        int midGamePhase = Math.min(gamePhase, 24); // In case of early promotion
        int endGamePhase = 24 - midGamePhase;
        return (midGameScoreDifference * midGamePhase + endGameScoreDifference * endGamePhase) / 24;
    }
}