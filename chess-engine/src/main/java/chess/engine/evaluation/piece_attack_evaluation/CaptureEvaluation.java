package chess.engine.evaluation.piece_attack_evaluation;

import chess.board.Board;
import chess.board.Move;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.pre_computations.PreComputationHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CaptureEvaluation {

    private static final Map<Long, Integer> transpositionTable = new ConcurrentHashMap<>();


    public static int staticExchangeEvaluation(Board board, Move move, PieceColor side) {
        long boardHash = board.getHash();

        if (transpositionTable.containsKey(boardHash)) {
            return transpositionTable.get(boardHash);
        }
        if (move.getCapturedPieceType() == null) {
            return 0; // No capturing move
        }

        LinkedList<Integer> gains = new LinkedList<>();
        gains.add(move.getCapturedPieceType().getMidGameValue()); // Initial value of the captured piece

        board.makeMove(move);

        int depth = 0;
        List<Move> movesMade = new ArrayList<>();
        movesMade.add(move);

        while (true) {
            Move response = findLeastValuableAttacker(board, move.getToSquare(), board.getCurrentPlayer());
            if (response == null) {
                //System.out.println("No more attackers at depth " + depth);
                break;
            }

            board.makeMove(response);

            movesMade.add(response);
            depth++;
            int value = response.getCapturedPieceType().getMidGameValue() - gains.get(depth - 1);
            gains.add(value);

            if (-gains.get(depth - 1) > gains.get(depth)) {
                gains.set(depth, -gains.get(depth - 1));
            }
        }

        for (int i = movesMade.size() - 1; i >= 0; i--) {
            board.undoMove(movesMade.get(i));
        }

        while (depth > 0) {
            gains.set(depth - 1, -Math.max(-gains.get(depth - 1), gains.get(depth)));
            depth--;
        }

        int finalValue = gains.getFirst();
        transpositionTable.put(boardHash, finalValue);

        return gains.getFirst(); // Return the net gain from the root of the capture sequence
    }




    private static long getAttackers(Board board, int square, PieceColor side) {
        long occupancies = board.getBitboard().getOccupancies(side);
        long opponentOccupancies = board.getBitboard().getOccupancies(side.opposite());

        long pawnAttackers = (side == PieceColor.WHITE ? PreComputationHandler.WHITE_PAWN_ATTACKS[square] : PreComputationHandler.BLACK_PAWN_ATTACKS[square]) & board.getPieceBitboard(PieceType.PAWN, side);
        long knightAttackers = PreComputationHandler.KNIGHT_ATTACKS[square] & board.getPieceBitboard(PieceType.KNIGHT, side);
        long bishopAttackers = PreComputationHandler.getBishopAttacks(square, occupancies) & board.getPieceBitboard(PieceType.BISHOP, side);
        long rookAttackers = PreComputationHandler.getRookAttacks(square, occupancies) & board.getPieceBitboard(PieceType.ROOK, side);
        long queenAttackers = PreComputationHandler.getQueenAttacks(square, occupancies) & board.getPieceBitboard(PieceType.QUEEN, side);
        long kingAttackers = PreComputationHandler.KING_ATTACKS[square] & board.getPieceBitboard(PieceType.KING, side);

        long attackers = pawnAttackers | knightAttackers | bishopAttackers | rookAttackers | queenAttackers | kingAttackers;
        attackers &= opponentOccupancies; // Only consider attackers that are attacking an opponent's piece

        //System.out.println("Attackers: " + Long.toBinaryString(attackers));
        return attackers;
    }

    private static Move findLeastValuableAttacker(Board board, int toSquare, PieceColor side) {
        long attackers = getAttackers(board, toSquare, side);
        if (attackers == 0) {
            return null;
        }

        for (PieceType type : PieceType.values()) { // Assume PieceType values are ordered by value
            if ((attackers & board.getPieceBitboard(type, side)) != 0) {
                int fromSquare = Long.numberOfTrailingZeros(board.getPieceBitboard(type, side));
                return new Move(fromSquare, toSquare, type, board.getPieceTypeAtSquare(toSquare), side);
            }
        }
        return null;
    }

}
