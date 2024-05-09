package chess.engine.evaluation.piece_attack_evaluation;

import chess.board.Board;
import chess.board.Move;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.pre_computations.PreComputationHandler;

public class CaptureEvaluation {

    public static int staticExchangeEvaluation(Board board, Move move, PieceColor side) {
        int netValue = 0;
        int square = move.getToSquare();
        PieceColor opponentColor = side.opposite();

        // Verify if the move is a capturing move and the opponent piece is present
        if (move.getCapturedPieceType() == null) {
            System.out.println("No capturing move");
            return 0;  // No capturing move
        }

        // Create a copy of the board to avoid modifying the original board state
        Board boardCopy = board.copy();
        System.out.println(boardCopy.getCurrentPlayer());

        // Make the initial capture
        int capturedValue = move.getCapturedPieceType().getMidGameValue();
        boardCopy.getBitboard().removePieceFromSquare(square, move.getCapturedPieceType(), opponentColor);
        boardCopy.getBitboard().placePieceOnSquare(square, move.getPieceType(), side);

        netValue = capturedValue;

        while (true) {
            long attackers = getAttackers(boardCopy, square, opponentColor);

            if (attackers == 0) break; // No more attackers

            PieceType smallestAttacker = getSmallestAttacker(boardCopy, attackers, opponentColor);
            int attackerSquare = getSquareOfSmallestAttacker(boardCopy, smallestAttacker, opponentColor);

            // Make the counter-capture
            int counterValue = smallestAttacker.getMidGameValue();
            boardCopy.getBitboard().removePieceFromSquare(square, move.getPieceType(), side);
            boardCopy.getBitboard().removePieceFromSquare(attackerSquare, smallestAttacker, opponentColor);
            boardCopy.getBitboard().placePieceOnSquare(square, smallestAttacker, opponentColor);

            netValue -= counterValue;

            if (netValue < 0) break; // Stop if the sequence is not favorable

            attackers = getAttackers(boardCopy, square, side);

            if (attackers == 0) break; // No more attackers

            smallestAttacker = getSmallestAttacker(boardCopy, attackers, side);
            attackerSquare = getSquareOfSmallestAttacker(boardCopy, smallestAttacker, side);

            // Make the re-capture
            capturedValue = smallestAttacker.getMidGameValue();
            boardCopy.getBitboard().removePieceFromSquare(square, smallestAttacker, opponentColor);
            boardCopy.getBitboard().placePieceOnSquare(square, smallestAttacker, side);

            netValue += capturedValue;
        }

        System.out.println("Final net value: " + netValue);
        return netValue;
    }


    private static int getSquareOfSmallestAttacker(Board board, PieceType pieceType, PieceColor side) {
        long bitboard = board.getPieceBitboard(pieceType, side);
        for (int square = 0; square < 64; square++) {
            if ((bitboard & (1L << square)) != 0) {
                return square;
            }
        }
        return -1; // or handle this case appropriately
    }


    private static long updateAttackers(Board board, int square, PieceColor side) {
        // This method would recalculate all attackers after a change on the board.
        long updatedAttackers = getAttackers(board, square, side);
        System.out.println("Updated attackers: " + Long.toBinaryString(updatedAttackers));
        return updatedAttackers;
    }

    private static long getAttackers(Board board, int square, PieceColor side) {
        long occupancies = board.getBitboard().getOccupancies(side);
        long pawnAttackers = (side == PieceColor.WHITE ? PreComputationHandler.WHITE_PAWN_ATTACKS[square] : PreComputationHandler.BLACK_PAWN_ATTACKS[square]) & occupancies;
        long knightAttackers = PreComputationHandler.KNIGHT_ATTACKS[square] & occupancies;
        long bishopAttackers = PreComputationHandler.getBishopAttacks(square, occupancies);
        long rookAttackers = PreComputationHandler.getRookAttacks(square, occupancies);
        long queenAttackers = PreComputationHandler.getQueenAttacks(square, occupancies);
        long kingAttackers = PreComputationHandler.KING_ATTACKS[square] & occupancies;

        long attackers = pawnAttackers | knightAttackers | bishopAttackers | rookAttackers | queenAttackers | kingAttackers;
        System.out.println("Attackers: " + Long.toBinaryString(attackers));
        return attackers;
    }

    private static PieceType getSmallestAttacker(Board board, long attackers, PieceColor side) {
        if ((attackers & board.getPieceBitboard(PieceType.PAWN, side)) != 0) {
            System.out.println("Smallest attacker:" + side + " PAWN");
            return PieceType.PAWN;
        } else if ((attackers & board.getPieceBitboard(PieceType.KNIGHT, side)) != 0) {
            System.out.println("Smallest attacker: " + side + " KNIGHT");
            return PieceType.KNIGHT;
        } else if ((attackers & board.getPieceBitboard(PieceType.BISHOP, side)) != 0) {
            System.out.println("Smallest attacker: " + side + " BISHOP");
            return PieceType.BISHOP;
        } else if ((attackers & board.getPieceBitboard(PieceType.ROOK, side)) != 0) {
            System.out.println("Smallest attacker: " + side + " ROOK");
            return PieceType.ROOK;
        } else if ((attackers & board.getPieceBitboard(PieceType.QUEEN, side)) != 0) {
            System.out.println("Smallest attacker: " + side + " QUEEN");
            return PieceType.QUEEN;
        } else if ((attackers & board.getPieceBitboard(PieceType.KING, side)) != 0) {
            System.out.println("Smallest attacker: " + side + " KING");
            return PieceType.KING;
        }
        System.out.println("No smallest attacker found");
        return null;
    }
}
