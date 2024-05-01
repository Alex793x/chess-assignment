package chess.engine.move_generation;

import chess.ai_player.move_generation.KnightMoveGenerator;
import chess.board.Bitboard;
import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.exception.IllegalMoveException;
import chess.engine.pre_computations.PreComputationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnightMoveGeneratorTest {

    private Board board;
    private KnightMoveGenerator knightMoveGenerator;

    @BeforeEach
    void setUp() {
        board = new Board();
        knightMoveGenerator = new KnightMoveGenerator(board);
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
    void testKnightMovesFromCenter() {

        board.getBitboard().placePieceOnSquare(27, PieceType.KNIGHT, PieceColor.WHITE);
        List<Integer> moves = knightMoveGenerator.generateMovesForKnight(27, PieceColor.WHITE);
        System.out.println("Knight Attacks Bitboard for 27: " + Long.toBinaryString(PreComputationHandler.KING_ATTACKS[27]));
        System.out.println("Generated moves: " + moves);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        knightMoveGenerator.moveKnight(27, 42, PieceColor.WHITE);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertEquals(8, moves.size(), "Knight on d4 should have 8 possible moves.");
        assertTrue(moves.containsAll(Arrays.asList(10, 12, 17, 21, 33, 37, 42, 44)), "Knight should move to all valid L-shape positions from d4.");
    }


    @Test
    void testKnightMovesFromEdge() {

        board.getBitboard().placePieceOnSquare(0, PieceType.KNIGHT, PieceColor.WHITE);
        List<Integer> moves = knightMoveGenerator.generateMovesForKnight(0, PieceColor.WHITE);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        System.out.println("Knight Attacks Bitboard for 0: " + Long.toBinaryString(PreComputationHandler.KING_ATTACKS[0]));
        System.out.println("Generated moves: " + moves);

        knightMoveGenerator.moveKnight(0, 10, PieceColor.WHITE);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertEquals(2, moves.size(), "Knight on a1 should have 2 possible moves.");
        assertTrue(moves.containsAll(Arrays.asList(10, 17)), "Knight should only move to b3 and c2 from a1.");
    }


    @Test
    void testKnightJumpsOverPieces() {

        board.getBitboard().placePieceOnSquare(36, PieceType.KNIGHT, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(35, PieceType.PAWN, PieceColor.BLACK);
        board.getBitboard().placePieceOnSquare(37, PieceType.PAWN, PieceColor.BLACK);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        List<Integer> moves = knightMoveGenerator.generateMovesForKnight(36, PieceColor.WHITE);


        System.out.println("Knight Attacks Bitboard for 0: " + Long.toBinaryString(PreComputationHandler.KING_ATTACKS[36]));
        System.out.println("Generated moves: " + moves);

        knightMoveGenerator.moveKnight(36, 30, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(Arrays.asList(19, 26, 42, 51, 53, 46, 30, 21)));
    }

    @Test
    void moveKnightInvalidMoveTest() {

        board.getBitboard().placePieceOnSquare(12, PieceType.KNIGHT, PieceColor.WHITE);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        // Attempt an invalid move
        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> {
            knightMoveGenerator.moveKnight(12, 28, PieceColor.WHITE);
        });


        assertEquals("Invalid move: Knight cannot move from 12 to 28", exception.getMessage());
    }


    @Test
    void testKnightCapture() {

        board.getBitboard().placePieceOnSquare(27, PieceType.KNIGHT, PieceColor.WHITE);
        board.getBitboard().placePieceOnSquare(10, PieceType.PAWN, PieceColor.BLACK);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        List<Integer> moves = knightMoveGenerator.generateMovesForKnight(27, PieceColor.WHITE);

        knightMoveGenerator.moveKnight(27, 10, PieceColor.WHITE);

        System.out.println(board.getBitboard().convertBitboardToBinaryString());

        assertTrue(moves.containsAll(Arrays.asList(10, 17, 33, 42, 44, 37, 21, 12)), "Knight should be able to capture the enemy pawn on c6.");
    }
}

