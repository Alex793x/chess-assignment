package chess.engine.move_generation;

import static org.junit.jupiter.api.Assertions.*;

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

class MoveGeneratorTest {
    private Board board;
    private KingMoveGenerator kingMoveGenerator;

    private PawnMoveGenerator pawnMoveGenerator;

    @BeforeEach
    void setUp() {
        board = new Board();
        kingMoveGenerator = new KingMoveGenerator(board);
        pawnMoveGenerator = new PawnMoveGenerator(board);
        clearBoard();
    }

    private void clearBoard() {
        // This method will clear all pieces from the board
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

    // Test knight's movement possibilities
    @Test
    void testKingMovesCenter() {
        // Place a knight at d4 (27)
        board.getBitboard().placePieceOnSquare(27, PieceType.KING, PieceColor.WHITE);
        List<Integer> moves = kingMoveGenerator.generateMovesForKing(27, PieceColor.WHITE);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        // Expect moves to be 18, 19, 20, 26, 28, 34, 35, 36
        assertEquals(8, moves.size(), "King on d4 should have 8 possible moves.");
        assertTrue(moves.contains(18));
        assertTrue(moves.contains(19));
        assertTrue(moves.contains(20));
        assertTrue(moves.contains(26));
        assertTrue(moves.contains(28));
        assertTrue(moves.contains(34));
        assertTrue(moves.contains(35));
        assertTrue(moves.contains(36));
    }


    @Test
    void testKingMovesE1() {
        board.getBitboard().placePieceOnSquare(4, PieceType.KING, PieceColor.WHITE);
        List<Integer> moves = kingMoveGenerator.generateMovesForKing(4, PieceColor.WHITE);
        System.out.println("King Attacks Bitboard for 4: " + Long.toBinaryString(PreComputationHandler.KING_ATTACKS[4]));
        System.out.println("Generated moves: " + moves);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertEquals(5, moves.size(), "King on e1 should have 5 possible moves.");
        assertTrue(moves.containsAll(Arrays.asList(3, 5, 11, 12, 13)), "King should move to d1, f1, d2, e2, and f2.");
    }

    @Test
    void testKingMovesH1() {
        board.getBitboard().placePieceOnSquare(7, PieceType.KING, PieceColor.WHITE);
        List<Integer> moves = kingMoveGenerator.generateMovesForKing(7, PieceColor.WHITE);
        System.out.println("King Attacks Bitboard for 7: " + Long.toBinaryString(PreComputationHandler.KING_ATTACKS[7]));
        System.out.println("Generated moves: " + moves);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertEquals(3, moves.size(), "King on h1 should have 3 possible moves.");
        assertTrue(moves.containsAll(Arrays.asList(6, 14, 15)), "King should move to g1, g2, and h2.");

    }

    @Test
    void testKingMovesG1WithRookAtH1() {
        board.getBitboard().placePieceOnSquare(6, PieceType.KING, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(7, PieceType.ROOK, PieceColor.WHITE);
        List<Integer> moves = kingMoveGenerator.generateMovesForKing(6, PieceColor.WHITE);
        System.out.println("King Attacks Bitboard for 6: " + Long.toBinaryString(PreComputationHandler.KING_ATTACKS[6]));
        System.out.println("Generated moves: " + moves);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertEquals(4, moves.size(), "King on g1 should have 4 possible moves.");
        assertTrue(moves.containsAll(Arrays.asList(5, 13, 14, 15)), "King should move to f1, f2, g2, and h2.");
    }

    @Test
    void testKingMovesG1WithEnemyRookAtH1() {
        board.getBitboard().placePieceOnSquare(6, PieceType.KING, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(7, PieceType.ROOK, PieceColor.BLACK);
        List<Integer> moves = kingMoveGenerator.generateMovesForKing(6, PieceColor.WHITE);
        System.out.println("King Attacks Bitboard for 6: " + Long.toBinaryString(PreComputationHandler.KING_ATTACKS[6]));
        System.out.println("Generated moves: " + moves);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertEquals(5, moves.size(), "King on g1 should have 5 possible moves.");
        assertTrue(moves.containsAll(Arrays.asList(5, 13, 14,15, 7)), "King should move to f1, f2, g2, h2, and capture at h1.");
    }


    @Test
    void moveKingInvalidMoveTest() {
        board.getBitboard().placePieceOnSquare(4, PieceType.KING, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(18, PieceType.ROOK, PieceColor.BLACK);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> {
            kingMoveGenerator.moveKing(4, 24, PieceColor.WHITE);
        });

        assertEquals("Invalid move: King cannot move from 4 to 24", exception.getMessage());

        System.out.println(board.getBitboard().convertBitboardToBinaryString());
    }



    @Test
    void testPawnSingleForwardMove() throws Exception {
        // Place a white pawn at e2 (index 12)
        board.getBitboard().placePieceOnSquare(12, PieceType.PAWN, PieceColor.WHITE);
        List<Integer> moves = pawnMoveGenerator.generateMovesForPawn(12, PieceColor.WHITE);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());
        pawnMoveGenerator.movePawn(12, 20, PieceColor.WHITE);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        // Expect the pawn to move to e3 (index 20)
        assertEquals(2, moves.size(), "Pawn should have 2 possible move.");
        assertTrue(moves.contains(20), "Pawn should be able to move forward to e3.");
        assertTrue(moves.contains(28), "Pawn should be able to move forward to e3.");
    }

    @Test
    void testPawnInitialDoubleForwardMove() throws Exception {
        // Place a white pawn at e2 (index 12)
        board.getBitboard().placePieceOnSquare(12, PieceType.PAWN, PieceColor.WHITE);
        List<Integer> moves = pawnMoveGenerator.generateMovesForPawn(12, PieceColor.WHITE);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());
        pawnMoveGenerator.movePawn(12, 28, PieceColor.WHITE);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        // Expect the pawn to also have the option to move to e4 (index 28)
        assertTrue(moves.contains(28), "Pawn should be able to make an initial double step forward to e4.");
        assertTrue(moves.contains(20), "Pawn should be able to make an initial double step forward to e4.");
    }

    @Test
    void testPawnCaptureMoves() throws Exception {
        // Place a white pawn at e4 (index 28) and a black pawn at f5 (index 37)
        board.getBitboard().placePieceOnSquare(28, PieceType.PAWN, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(37, PieceType.PAWN, PieceColor.BLACK);

        List<Integer> moves = pawnMoveGenerator.generateMovesForPawn(28, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());
        pawnMoveGenerator.movePawn(28, 37, PieceColor.WHITE);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        // Expect the pawn to be able to move to f5 (index 37)
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

        // Pawn should not be able to move forward to e3 as it is blocked
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

        // Pawn should not be able to move forward to e3 as it is blocked
        assertFalse(moves.contains(28), "Pawn should not move forward to e3 as it is blocked.");

        assertEquals("Invalid move: Pawn cannot move from 12 to 28", exception.getMessage());
    }





}
