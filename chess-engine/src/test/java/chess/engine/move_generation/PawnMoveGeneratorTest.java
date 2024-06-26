package chess.engine.move_generation;

import chess.ai_player.move_generation.PawnMoveGenerator;
import chess.board.Bitboard;
import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.exception.IllegalMoveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PawnMoveGeneratorTest {


    private Board board;
    private PawnMoveGenerator pawnMoveGenerator;

    @BeforeEach
    void setUp() {
        board = new Board();
        pawnMoveGenerator = new PawnMoveGenerator(board);
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
    void testPawnSingleForwardMove() throws Exception {

        board.getBitboard().placePieceOnSquare(12, PieceType.PAWN, PieceColor.WHITE);
        List<Integer> moves = pawnMoveGenerator.generateMovesForPawn(12, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        pawnMoveGenerator.movePawn(12, 20, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertEquals(2, moves.size(), "Pawn should have 2 possible move.");
        assertTrue(moves.contains(20), "Pawn should be able to move forward to e3.");
        assertTrue(moves.contains(28), "Pawn should be able to move forward to e3.");
    }

    @Test
    void testPawnInitialDoubleForwardMove() {

        board.getBitboard().placePieceOnSquare(12, PieceType.PAWN, PieceColor.WHITE);
        List<Integer> moves = pawnMoveGenerator.generateMovesForPawn(12, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        pawnMoveGenerator.movePawn(12, 28, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.contains(28), "Pawn should be able to make an initial double step forward to e4.");
        assertTrue(moves.contains(20), "Pawn should be able to make an initial double step forward to e4.");
    }

    @Test
    void testPawnCaptureMoves() {

        board.getBitboard().placePieceOnSquare(28, PieceType.PAWN, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(37, PieceType.PAWN, PieceColor.BLACK);

        List<Integer> moves = pawnMoveGenerator.generateMovesForPawn(28, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        pawnMoveGenerator.movePawn(28, 37, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.contains(37), "Pawn should be able to capture on f5.");
    }


    @Test
    void testPawnBlockedMove() {

        board.getBitboard().placePieceOnSquare(12, PieceType.PAWN, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(20, PieceType.ROOK, PieceColor.BLACK);

        List<Integer> moves = pawnMoveGenerator.generateMovesForPawn(12, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        // Attempt to make the blocked move
        Exception exception = assertThrows(IllegalMoveException.class, () -> {
            pawnMoveGenerator.movePawn(12, 20, PieceColor.WHITE);
        });

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertFalse(moves.contains(20), "Pawn should not move forward to e3 as it is blocked.");

        assertEquals("Invalid move: Pawn cannot move from 12 to 20", exception.getMessage());
    }

    @Test
    void testPawnBlockedMoveOverEnemy() {

        board.getBitboard().placePieceOnSquare(12, PieceType.PAWN, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(20, PieceType.ROOK, PieceColor.BLACK);

        List<Integer> moves = pawnMoveGenerator.generateMovesForPawn(12, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        // Attempt to make the blocked move
        Exception exception = assertThrows(IllegalMoveException.class, () -> {
            pawnMoveGenerator.movePawn(12, 28, PieceColor.WHITE);
        });

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertFalse(moves.contains(28), "Pawn should not move forward to e3 as it is blocked.");

        assertEquals("Invalid move: Pawn cannot move from 12 to 28", exception.getMessage());
    }


}