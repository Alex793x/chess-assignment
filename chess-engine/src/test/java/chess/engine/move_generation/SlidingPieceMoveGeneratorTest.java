package chess.engine.move_generation;

import chess.board.Bitboard;
import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.exception.IllegalMoveException;
import chess.engine.pre_computations.PreComputationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SlidingPieceMoveGeneratorTest {
    private Board board;
    private SlidingPieceMoveGenerator generator;

    @BeforeEach
    void setUp() {
        board = new Board();
        generator = new SlidingPieceMoveGenerator(board);
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
    void testRookMovesOnEmptyBoard() {
        board.getBitboard().placePieceOnSquare(0, PieceType.ROOK, PieceColor.WHITE); // Place rook at a1
        List<Integer> moves = generator.generateMovesForSlidingPiece(0, PieceColor.WHITE, PieceType.ROOK);

        System.out.println("Rook Attacks Bitboard for 0: " + Long.toBinaryString(PreComputationHandler.ROOK_ATTACKS[0]));
        System.out.println("Generated moves: " + moves);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        generator.moveSlidingPiece(0, 8, PieceColor.WHITE, PieceType.ROOK);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(List.of(1, 2, 3, 4, 5, 6, 7, 8, 16, 24, 32, 40, 48, 56)), "Rook should be able to move horizontally and vertically.");
        assertEquals(14, moves.size(), "Rook should have 14 possible moves from a1 on an empty board.");
    }

    @Test
    void testRookBlockedByPiece() {
        board.getBitboard().placePieceOnSquare(0, PieceType.ROOK, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(8, PieceType.PAWN, PieceColor.WHITE); // Blocking pawn at a2

        List<Integer> moves = generator.generateMovesForSlidingPiece(0, PieceColor.WHITE, PieceType.ROOK);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());
        System.out.println("Generated moves: " + moves);

        // Attempt an invalid move and expect an exception
        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> {
            generator.moveSlidingPiece(0, 8, PieceColor.WHITE, PieceType.ROOK);
        });

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertFalse(moves.contains(8), "Rook should not be able to move to a2 as it is blocked by a white pawn.");
        assertTrue(moves.contains(1), "Rook should be able to move to a1 (b1).");
        assertEquals("Invalid move: ROOK cannot move from 0 to 8", exception.getMessage(), "Expected exception message for invalid move.");
    }

    @Test
    void testRookPlacedInCorner() {
        board.getBitboard().placePieceOnSquare(0, PieceType.ROOK, PieceColor.WHITE);
        List<Integer> moves = generator.generateMovesForSlidingPiece(0, PieceColor.WHITE, PieceType.ROOK);

        System.out.println("Rook Attacks Bitboard for 0: " + Long.toBinaryString(PreComputationHandler.ROOK_ATTACKS[0]));
        System.out.println("Generated moves: " + moves);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        generator.moveSlidingPiece(0, 7, PieceColor.WHITE, PieceType.ROOK);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(List.of(1, 2, 3, 4, 5, 6, 7, 8, 16, 24, 32, 40, 48, 56)), "Rook should be able to move horizontally and vertically.");
        assertEquals(14, moves.size(), "Rook should have 14 possible moves from a1 on an empty board.");
    }

    @Test
    void testRookCaptureEnemy() {
        board.getBitboard().placePieceOnSquare(1, PieceType.ROOK, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(25, PieceType.PAWN, PieceColor.BLACK);

        List<Integer> moves = generator.generateMovesForSlidingPiece(1, PieceColor.WHITE, PieceType.ROOK);

        System.out.println("Rook Attacks Bitboard for 0: " + Long.toBinaryString(PreComputationHandler.ROOK_ATTACKS[1]));
        System.out.println("Generated moves: " + moves);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        generator.moveSlidingPiece(1, 25, PieceColor.WHITE, PieceType.ROOK);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(List.of(0, 2, 3, 4, 5, 6, 7, 9, 17, 25)), "Rook should be able to move horizontally and vertically.");
        assertEquals(10, moves.size(), "Rook should have 10 possible moves from b2 since there is a pawn to be captured");
    }

    @Test
    void testBishopMoves() {
        board.getBitboard().placePieceOnSquare(35, PieceType.BISHOP, PieceColor.BLACK);
        List<Integer> moves = generator.generateMovesForSlidingPiece(35, PieceColor.BLACK, PieceType.BISHOP);

        System.out.println("Bishop Attacks Bitboard for 35: " + Long.toBinaryString(PreComputationHandler.BISHOP_ATTACKS[35]));
        System.out.println("Generated moves: " + moves);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        generator.moveSlidingPiece(35, 28, PieceColor.BLACK, PieceType.BISHOP);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(List.of(28, 21, 14, 7, 42, 49, 56, 26, 17, 8, 44, 53, 62)), "Bishop should be able to move diagonally.");
        assertEquals(13, moves.size(), "Bishop should have 13 possible moves from d5 on an empty board.");
    }

    @Test
    void testBishopBlockedMoves() {
        board.getBitboard().placePieceOnSquare(35, PieceType.BISHOP, PieceColor.BLACK); // Place bishop at d4
        board.getBitboard().placePieceOnSquare(28, PieceType.PAWN, PieceColor.BLACK); // Blocking pawn at b7 (diagonally from d4)

        List<Integer> moves = generator.generateMovesForSlidingPiece(35, PieceColor.BLACK, PieceType.BISHOP);

        System.out.println("Bishop Attacks Bitboard for 35: " + Long.toBinaryString(PreComputationHandler.BISHOP_ATTACKS[35]));
        System.out.println("Generated moves: " + moves);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        // Attempt an invalid move and expect an exception
        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> {
            generator.moveSlidingPiece(35, 28, PieceColor.BLACK, PieceType.BISHOP);
        });

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertFalse(moves.contains(28), "Bishop should not be able to move to b7 as it is blocked by a black pawn.");
        assertEquals("Invalid move: BISHOP cannot move from 35 to 28", exception.getMessage(), "Expected exception message for invalid move.");
    }

    @Test
    void testBishopPlacedInCorner() {
        board.getBitboard().placePieceOnSquare(0, PieceType.BISHOP, PieceColor.WHITE);
        List<Integer> moves = generator.generateMovesForSlidingPiece(0, PieceColor.WHITE, PieceType.BISHOP);

        System.out.println("Bishop Attacks Bitboard for 0: " + Long.toBinaryString(PreComputationHandler.BISHOP_ATTACKS[0]));
        System.out.println("Generated moves: " + moves);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        generator.moveSlidingPiece(0, 45, PieceColor.WHITE, PieceType.BISHOP);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(List.of(9, 18, 27, 36, 45, 54, 63)), "Bishop should be able to move diagonally.");
        assertEquals(7, moves.size(), "Bishop should have 7 possible moves from a1 on an empty board.");
    }


    @Test
    void testBishopCaptureEnemy() {
        board.getBitboard().placePieceOnSquare(0, PieceType.BISHOP, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(45, PieceType.PAWN, PieceColor.BLACK);

        List<Integer> moves = generator.generateMovesForSlidingPiece(0, PieceColor.WHITE, PieceType.BISHOP);

        System.out.println("Bishop Attacks Bitboard for 35: " + Long.toBinaryString(PreComputationHandler.BISHOP_ATTACKS[0]));
        System.out.println("Generated moves: " + moves);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        generator.moveSlidingPiece(0, 45, PieceColor.WHITE, PieceType.BISHOP);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(List.of(9, 18, 27, 36, 45)), "Bishop should be able to move diagonally to capture black pawn.");
        assertEquals(5, moves.size(), "Bishop should have 5 possible moves");
    }

    @Test
    void testQueenMoves() {
        board.getBitboard().placePieceOnSquare(3, PieceType.QUEEN, PieceColor.WHITE);
        List<Integer> moves = generator.generateMovesForSlidingPiece(3, PieceColor.WHITE, PieceType.QUEEN);

        System.out.println("Queen Attacks Bitboard for 3: " + Long.toBinaryString(PreComputationHandler.QUEEN_ATTACKS[3]));
        System.out.println("Generated moves: " + moves);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        generator.moveSlidingPiece(3, 11, PieceColor.WHITE, PieceType.QUEEN);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(Arrays.asList(0, 1, 2, 4, 5, 6, 7, 10, 17, 24, 12, 21, 30, 39, 11, 19, 27, 35, 43, 51, 59)), "Queen should be able to move horizontally, vertically, and diagonally.");
        assertEquals(21, moves.size(), "Queen should have 21 possible moves from d1 on an empty board.");
    }

    @Test
    void testQueenBlockedMoves() {
        board.getBitboard().placePieceOnSquare(35, PieceType.QUEEN, PieceColor.WHITE); // Place bishop at d4
        board.getBitboard().placePieceOnSquare(28, PieceType.PAWN, PieceColor.WHITE); // Blocking pawn at b7 (diagonally from d4)

        List<Integer> moves = generator.generateMovesForSlidingPiece(35, PieceColor.WHITE, PieceType.QUEEN);

        System.out.println("Queen Attacks Bitboard for 35: " + Long.toBinaryString(PreComputationHandler.QUEEN_ATTACKS[35]));
        System.out.println("Generated moves: " + moves);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        // Attempt an invalid move and expect an exception
        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> {
            generator.moveSlidingPiece(35, 28, PieceColor.WHITE, PieceType.QUEEN);
        });

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertFalse(moves.contains(28), "Queen should not be able to move to b7 as it is blocked by a white pawn.");
        assertEquals("Invalid move: QUEEN cannot move from 35 to 28", exception.getMessage(), "Expected exception message for invalid move.");
    }

    @Test
    void testQueenPlacedInCorner() {
        board.getBitboard().placePieceOnSquare(56, PieceType.QUEEN, PieceColor.WHITE);
        List<Integer> moves = generator.generateMovesForSlidingPiece(56, PieceColor.WHITE, PieceType.QUEEN);

        System.out.println("Queen Attacks Bitboard for 0: " + Long.toBinaryString(PreComputationHandler.QUEEN_ATTACKS[56]));
        System.out.println("Generated moves: " + moves);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        generator.moveSlidingPiece(56, 60, PieceColor.WHITE, PieceType.QUEEN);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(List.of(57, 58, 59, 60, 61, 62, 63, 48, 40, 32, 24, 16, 8, 0, 49, 42, 35, 28, 21, 14, 7)), "Queen should be able to move horizontally, vertically, and diagonally.");
        assertEquals(21, moves.size(), "Queen should have 21 possible moves from a1 on an empty board.");
    }

    @Test
    void testQueenCaptureEnemy() {
        board.getBitboard().placePieceOnSquare(3, PieceType.QUEEN, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(30, PieceType.ROOK, PieceColor.BLACK);

        List<Integer> moves = generator.generateMovesForSlidingPiece(3, PieceColor.WHITE, PieceType.QUEEN);

        System.out.println("Queen Attacks Bitboard for 3: " + Long.toBinaryString(PreComputationHandler.QUEEN_ATTACKS[3]));
        System.out.println("Generated moves: " + moves);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        generator.moveSlidingPiece(3, 30, PieceColor.WHITE, PieceType.QUEEN);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(Arrays.asList(0, 1, 2, 4, 5, 6, 7, 10, 17, 24, 11, 19, 27, 35, 43, 51, 59, 12, 21, 30)), "Queen should be able to move horizontally, vertically, and diagonally.");
        assertEquals(20, moves.size(), "Queen should have 20 possible moves and capture Rook at 30");
    }

}
