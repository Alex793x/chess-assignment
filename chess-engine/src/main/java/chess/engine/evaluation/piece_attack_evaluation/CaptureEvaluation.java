package chess.engine.evaluation.piece_attack_evaluation;

import chess.board.Board;
import chess.board.Move;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.pre_computations.PreComputationHandler;

import java.util.Objects;

public class CaptureEvaluation {

    public static int staticExchangeEvaluation(Board board, Move move, PieceColor side) {
        int netValue = 0;
        int square = move.getToSquare();
        PieceColor opponentColor = side.opposite();

        // Verify if the move is a capturing move and the opponent piece is present
        if (move.getCapturedPieceType() == null) {
            return 0;  // No capturing move
        }

        long attackers = getAttackers(board, square, side);

        while (attackers != 0) {
            PieceType smallestAttacker = getSmallestAttacker(board, attackers, side);
            if (smallestAttacker == null) {
                break;  // No more attackers
            }

            // Calculate the value of the opponent's piece at the square
            int capturedValue = move.getCapturedPieceType().getMidGameValue();

            // Simulate capture: remove the smallest attacker
            board.getBitboard().removePieceFromSquare(square, smallestAttacker, side);
            attackers = updateAttackers(board, square, side);

            // Recursively evaluate opponent's best recapture sequence
            int counterValue = staticExchangeEvaluation(board, new Move(square, square, smallestAttacker, null, opponentColor), opponentColor);

            // Undo the capture
            board.getBitboard().placePieceOnSquare(square, smallestAttacker, side);

            // Update net value considering this capture sequence
            netValue = capturedValue - counterValue;

            if (netValue <= 0) {
                break;  // Stop if the sequence is not favorable
            }
        }

        return netValue;
    }

    private static long updateAttackers(Board board, int square, PieceColor side) {
        // This method would recalculate all attackers after a change on the board.
        return getAttackers(board, square, side);
    }

    private static long getAttackers(Board board, int square, PieceColor side) {
        long occupancies = board.getBitboard().getOccupancies(side);
        long pawnAttackers = (side == PieceColor.WHITE ? PreComputationHandler.WHITE_PAWN_ATTACKS[square] : PreComputationHandler.BLACK_PAWN_ATTACKS[square]) & occupancies;
        long knightAttackers = PreComputationHandler.KNIGHT_ATTACKS[square] & occupancies;
        long bishopAttackers = PreComputationHandler.getBishopAttacks(square, occupancies);
        long rookAttackers = PreComputationHandler.getRookAttacks(square, occupancies);
        long queenAttackers = PreComputationHandler.getQueenAttacks(square, occupancies);
        long kingAttackers = PreComputationHandler.KING_ATTACKS[square] & occupancies;

        return pawnAttackers | knightAttackers | bishopAttackers | rookAttackers | queenAttackers | kingAttackers;
    }

    private static PieceType getSmallestAttacker(Board board, long attackers, PieceColor side) {
        if ((attackers & board.getPieceBitboard(PieceType.PAWN, side)) != 0) {
            return PieceType.PAWN;
        } else if ((attackers & board.getPieceBitboard(PieceType.KNIGHT, side)) != 0) {
            return PieceType.KNIGHT;
        } else if ((attackers & board.getPieceBitboard(PieceType.BISHOP, side)) != 0) {
            return PieceType.BISHOP;
        } else if ((attackers & board.getPieceBitboard(PieceType.ROOK, side)) != 0) {
            return PieceType.ROOK;
        } else if ((attackers & board.getPieceBitboard(PieceType.QUEEN, side)) != 0) {
            return PieceType.QUEEN;
        } else if ((attackers & board.getPieceBitboard(PieceType.KING, side)) != 0) {
            return PieceType.KING;
        }
        return null;
    }
}
