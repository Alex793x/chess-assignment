package chess.state;

import chess.ai_player.move_generation.KingMoveGenerator;
import chess.board.Bitboard;
import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private Board board;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        board = new Board();
        gameState = new GameState(board.getGameStateData());
        clearBoard();
    }

    private void clearBoard() {
        Bitboard bitboard = board.getBitboard();
        bitboard.setWhiteKing(0L);
        bitboard.setWhiteQueens(0L);
        bitboard.setWhiteRooks(0L);
        bitboard.setWhiteBishops(0L);
        bitboard.setWhiteKnights(0L);
        bitboard.setWhitePawns(0L);
        bitboard.setBlackKing(0L);
        bitboard.setBlackQueens(0L);
        bitboard.setBlackRooks(0L);
        bitboard.setBlackBishops(0L);
        bitboard.setBlackKnights(0L);
        bitboard.setBlackPawns(0L);
    }

    @Test
    void testKingInCheck() {
        // Place kings on the board
        board.getBitboard().placePieceOnSquare(4, PieceType.KING, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(28, PieceType.QUEEN, PieceColor.BLACK);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(gameState.isKingInCheck(), "White king should be in check from black queen.");
    }

    @Test
    void testKingNotInCheck() {
        board.getBitboard().placePieceOnSquare(4, PieceType.KING, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(27, PieceType.QUEEN, PieceColor.BLACK);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertFalse(gameState.isKingInCheck(), "White king should not be in check.");
    }

    @Test
    void testKingInCheckmate() {
        // Setting up a simple checkmate position
        board.getBitboard().placePieceOnSquare(0, PieceType.KING, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(2, PieceType.QUEEN, PieceColor.BLACK);
        board.getBitboard().placePieceOnSquare(16, PieceType.QUEEN, PieceColor.BLACK);

        KingMoveGenerator kingMoveGenerator = new KingMoveGenerator(board.getBitboard());
        System.out.println("King moves: " + kingMoveGenerator.generateMovesForKing(0, PieceColor.WHITE));

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(gameState.isKingInCheckmate(), "White king should be in checkmate.");
    }

    @Test
    void testKingNotInCheckmate() {
        board.getBitboard().placePieceOnSquare(0, PieceType.KING, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(2, PieceType.QUEEN, PieceColor.BLACK);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());
        assertFalse(gameState.isKingInCheckmate(), "White king should not be in checkmate, has an escape.");
    }

}