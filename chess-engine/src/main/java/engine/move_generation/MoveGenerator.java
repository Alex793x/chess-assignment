package engine.move_generation;

import engine.evaluations.material_board.PeSTOEvaluation;
import engine.move_generation.comparators.MoveValueEndGameComparator;
import engine.move_generation.comparators.MoveValueMidGameComparator;
import engine.move_generation.model.MoveResult;
import model.*;
import model.enums.CurrentPlayer;
import model.enums.PieceColor;
import model.enums.PieceType;
import util.precompuation.PreComputedData;
import util.pst.PSTHandler;

import java.util.*;

import static util.precompuation.PreComputedData.getBishopAttacks;
import static util.precompuation.PreComputedData.getQueenAttacks;

public class MoveGenerator {

    public MoveResult generateActualKnightMoves(int square, Board board) {
        BitSet potentialMoves = PreComputedData.knightMoves[square];
        return generateMoves(square, potentialMoves, board);
    }

    public MoveResult generateActualBishopMoves(int square, Board board) {
        BitSet occupied = board.getBitboard().getOccupiedSquares();
        BitSet bishopAttacks = getBishopAttacks(occupied, square);
        return generateMoves(square, bishopAttacks, board);
    }

    public MoveResult generateActualRookMoves(int square, Board board) {
        BitSet occupied = board.getBitboard().getOccupiedSquares();
        BitSet rookAttacks = PreComputedData.getRookAttacks(occupied, square);
        return generateMoves(square, rookAttacks, board);
    }

    public MoveResult generateActualQueenMoves(int square, Board board) {
        BitSet occupied = board.getBitboard().getOccupiedSquares();
        BitSet queenAttacks = getQueenAttacks(occupied, square);
        return generateMoves(square, queenAttacks, board);
    }

    public MoveResult generateActualKingMoves(int square, Board board) {
        BitSet potentialMoves = PreComputedData.kingMoves[square];
        MoveResult result = generateMoves(square, potentialMoves, board);
        List<Move> castlingMoves = generateCastlingMoves(square, board);
        result.getCastlingMoves().addAll(castlingMoves);
        return result;
    }

    public MoveResult generateActualPawnMoves(int square, Board board) {
        boolean isMidgame = PeSTOEvaluation.isMidgamePhase(board.getBitboard());
        CurrentPlayer currentPlayer = board.getCurrentPlayer();
        Bitboards bitboard = board.getBitboard();

        BitSet[] pieceMoves = currentPlayer.equals(CurrentPlayer.WHITE)
                ? PreComputedData.whitePawnMoves
                : PreComputedData.blackPawnMoves;

        MoveResult moveResult = new MoveResult(isMidgame ? new MoveValueMidGameComparator() : new MoveValueEndGameComparator());

        BitSet pawnPositions = bitboard.getPieceBitboard(PieceType.PAWN, currentPlayer.equals(CurrentPlayer.WHITE) ? PieceColor.WHITE : PieceColor.BLACK);
        BitSet allies = bitboard.getAllPiecesByColor(currentPlayer);
        BitSet enemies = bitboard.getAllPiecesByColor(currentPlayer.getOppositePlayer());
        BitSet alliesAttackVectors = board.getBitboard().getAttackVectorsByColor(currentPlayer.equals(CurrentPlayer.WHITE) ? PieceColor.WHITE : PieceColor.BLACK);
        BitSet enemiesAttackVectors = board.getBitboard().getAttackVectorsByColor(currentPlayer.equals(CurrentPlayer.WHITE) ? PieceColor.BLACK : PieceColor.WHITE);
        BitSet occupied = bitboard.getOccupiedSquares();
        BitSet pins = currentPlayer == CurrentPlayer.WHITE ? bitboard.getWhitePins() : bitboard.getBlackPins();

        for (int pawnSquare = pawnPositions.nextSetBit(0); pawnSquare >= 0; pawnSquare = pawnPositions.nextSetBit(pawnSquare + 1)) {
            BitSet potentialMoves = (BitSet) pieceMoves[pawnSquare].clone();

            for (int pos = potentialMoves.nextSetBit(0); pos >= 0; pos = potentialMoves.nextSetBit(pos + 1)) {
                boolean isCapture = enemies.get(pos);
                boolean isPromotion = (currentPlayer == CurrentPlayer.WHITE && pos / 8 == 0) || (currentPlayer == CurrentPlayer.BLACK && pos / 8 == 7);
                boolean isProtected = hasAdequateProtection(pos, bitboard, currentPlayer.getOppositePlayer(), isMidgame);
                boolean isAttacked = enemiesAttackVectors.get(pos);
                Piece protectedByPiece = bitboard.findLowestValueAttacker(pos, currentPlayer.getOppositePlayer(), isMidgame, square);

                // Check for diagonal moves (captures) and straight moves (non-captures)
                if (isCapture || (!occupied.get(pos) && pos % 8 == pawnSquare % 8)) {
                    if (!allies.get(pos) && (!pins.get(pawnSquare) || moveAlongPinLine(pawnSquare, pos)) && !isSquareAttackedAndNotProtected(pos, alliesAttackVectors, enemiesAttackVectors)) {
                        Piece piece = bitboard.getPieceBySquare(pawnSquare);

                        if (piece == null) {
                            System.err.println("Null piece encountered at square: " + pawnSquare);
                            continue;
                        }

                        Piece capturedPiece = isCapture ? bitboard.getPieceBySquare(pos) : null;
                        int positionalPSTValue = isMidgame
                                ? PSTHandler.getMidGameValue(piece.getPieceType(), piece.getPieceColor(), pos)
                                : PSTHandler.getEndgameValue(piece.getPieceType(), piece.getPieceColor(), pos);
                        int attackPenalty = calculateAttackPenalty(piece, isProtected, pos, enemiesAttackVectors, bitboard, currentPlayer, isMidgame);

                        Move move = new Move(
                                pawnSquare,
                                pos,
                                isPromotion ? 1000 : positionalPSTValue,
                                piece,
                                capturedPiece,
                                isCapture,
                                isPromotion,
                                board.getHalfMoveClock(),
                                isProtected,
                                isAttacked,
                                attackPenalty,
                                protectedByPiece
                        );

                        if (isMoveLegal(move, board)) {
                            if (isPromotion) {
                                moveResult.getPromotionMoves().add(move);
                            } else if (isCapture) {
                                moveResult.getValidCaptures().add(move);
                            } else {
                                moveResult.getValidMoves().add(move);
                            }
                            if (movePutsOpponentInCheck(move, board, currentPlayer)) {
                                moveResult.getCheckMoves().add(move);
                            }
                        }
                    }
                }
            }
        }

        return moveResult;
    }

    private MoveResult generateMoves(int square, BitSet pieceAttacks, Board board) {
        boolean isMidgame = PeSTOEvaluation.isMidgamePhase(board.getBitboard());
        Bitboards bitboard = board.getBitboard();
        CurrentPlayer currentPlayer = board.getCurrentPlayer();
        BitSet allies = bitboard.getAllPiecesByColor(currentPlayer);
        BitSet enemies = bitboard.getAllPiecesByColor(currentPlayer.getOppositePlayer());
        BitSet alliesAttackVectors = board.getBitboard().getAttackVectorsByColor(currentPlayer.equals(CurrentPlayer.WHITE) ? PieceColor.WHITE : PieceColor.BLACK);
        BitSet enemiesAttackVectors = board.getBitboard().getAttackVectorsByColor(currentPlayer.equals(CurrentPlayer.WHITE) ? PieceColor.BLACK : PieceColor.WHITE);
        BitSet pins = currentPlayer == CurrentPlayer.WHITE ? bitboard.getWhitePins() : bitboard.getBlackPins();

        MoveResult moveResult = new MoveResult(isMidgame ? new MoveValueMidGameComparator() : new MoveValueEndGameComparator());

        for (int pos = pieceAttacks.nextSetBit(0); pos >= 0; pos = pieceAttacks.nextSetBit(pos + 1)) {
            if (!allies.get(pos) && !isSquareAttackedAndNotProtected(pos, alliesAttackVectors, enemiesAttackVectors)) {
                Piece piece = bitboard.getPieceBySquare(square);
                // Check if piece is null and handle the error
                if (piece == null) {
                    System.err.println("Null piece encountered at square: " + square);
                    continue; // Skip this move generation
                }
                Piece capturedPiece = enemies.get(pos) ? bitboard.getPieceBySquare(pos) : null;
                boolean isCapture = enemies.get(pos);
                int positionalPSTValue = isMidgame
                        ? PSTHandler.getMidGameValue(piece.getPieceType(), piece.getPieceColor(), pos)
                        : PSTHandler.getEndgameValue(piece.getPieceType(), piece.getPieceColor(), pos);
                boolean isProtected = hasAdequateProtection(square, bitboard, currentPlayer.getOppositePlayer(), isMidgame);
                boolean isAttacked = enemiesAttackVectors.get(pos);
                Piece protectedByPiece = bitboard.findLowestValueAttacker(pos, currentPlayer.getOppositePlayer(), isMidgame, square);
                int attackPenalty = calculateAttackPenalty(piece, isProtected, pos, enemiesAttackVectors, bitboard, currentPlayer, isMidgame);

                if (!pins.get(square) || moveAlongPinLine(square, pos)) {
                    Move move = new Move(
                            square,
                            pos,
                            positionalPSTValue,
                            piece,
                            capturedPiece,
                            isCapture,
                            false,
                            board.getHalfMoveClock(),
                            isProtected,
                            isAttacked,
                            attackPenalty,
                            protectedByPiece
                    );

                    if (isMoveLegal(move, board)) {
                        if (capturedPiece != null) {
                            moveResult.getValidCaptures().add(move);
                        } else {
                            moveResult.getValidMoves().add(move);
                        }

                        if (movePutsOpponentInCheck(move, board, currentPlayer)) {
                            moveResult.getCheckMoves().add(move);
                        }
                    }
                }
            }
        }

        return moveResult;
    }


    public boolean isMoveLegal(Move move, Board board) {
        // Simulate the move
        board.makeMove(move);
        // Check if the resulting position leaves the king in check
        boolean isLegal = !board.getBitboard().isKingExposed(board.getCurrentPlayer().getOppositePlayer());
        // Undo the move
        board.undoMove(move);
        return isLegal;
    }

    private boolean movePutsOpponentInCheck(Move move, Board board, CurrentPlayer currentPlayer) {
        board.makeMove(move);
        boolean inCheck = board.getBitboard().isKingExposed(board.getCurrentPlayer().getOppositePlayer());
        board.undoMove(move);
        return inCheck;
    }

    private boolean moveAlongPinLine(int from, int to) {
        return checkLine(from, to);
    }

    private boolean checkLine(int from, int to) {
        int fromRow = from / 8;
        int fromCol = from % 8;
        int toRow = to / 8;
        int toCol = to % 8;

        // Check if moves are on the same file
        if (fromCol == toCol) {
            return true;
        }

        // Check if moves are on the same rank
        if (fromRow == toRow) {
            return true;
        }

        // Check if moves are on the same major diagonal (bottom left to top right)
        return Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol);
    }

    public List<Move> generateCastlingMoves(int kingSquare, Board board) {
        List<Move> castlingMoves = new ArrayList<>();
        CurrentPlayer currentPlayer = board.getCurrentPlayer();
        Player player = board.getPlayer(currentPlayer);
        Bitboards bitboard = board.getBitboard();

        // Ensure the king and the rook haven't moved and the king is not in check
        if (!player.isHasKingMoved() && !bitboard.isKingExposed(currentPlayer)) {

            // Kingside castling (O-O)
            if (!player.isHasKingSideRookMoved() && bitboard.isPathClear(kingSquare, kingSquare + 2)) {
                if (!bitboard.isSquareAttacked(kingSquare, currentPlayer) &&
                        !bitboard.isSquareAttacked(kingSquare + 1, currentPlayer) &&
                        !bitboard.isSquareAttacked(kingSquare + 2, currentPlayer)) {

                    Piece king = bitboard.getPieceBySquare(kingSquare);
                    if (king != null && king.getPieceType() == PieceType.KING) {
                        Move kingsideCastling = new Move(kingSquare, kingSquare + 2, 0, king, null, false, false, board.getHalfMoveClock(), true, false, 0, null);
                        castlingMoves.add(kingsideCastling);
                    }
                }
            }

            // Queenside castling (O-O-O)
            if (!player.isHasQueenSideRookMoved() && bitboard.isPathClear(kingSquare, kingSquare - 3)) {
                if (!bitboard.isSquareAttacked(kingSquare, currentPlayer) &&
                        !bitboard.isSquareAttacked(kingSquare - 1, currentPlayer) &&
                        !bitboard.isSquareAttacked(kingSquare - 2, currentPlayer) &&
                        !bitboard.isSquareAttacked(kingSquare - 3, currentPlayer)) {

                    Piece king = bitboard.getPieceBySquare(kingSquare);
                    if (king != null && king.getPieceType() == PieceType.KING) {
                        Move queensideCastling = new Move(kingSquare, kingSquare - 2, 0, king, null, false, false, board.getHalfMoveClock(), true, false, 0, null);
                        castlingMoves.add(queensideCastling);
                    }
                }
            }
        }
        return castlingMoves;
    }

    private int calculateAttackPenalty(Piece piece, boolean isProtected, int pos, BitSet attackVectors, Bitboards bitboards, CurrentPlayer currentPlayer, boolean isMidgame) {
        int penalty = 0;
        int pieceValue = piece.getPieceValue(isMidgame);

        if (attackVectors.get(pos)) {
            Piece attacker = bitboards.findLowestValueAttacker(pos, currentPlayer.getOppositePlayer(), isMidgame, piece.getSquare());

            if (attacker != null) {
                int attackerValue = attacker.getPieceValue(isMidgame);

                if (attackerValue < pieceValue) {
                    penalty += ((pieceValue - attackerValue) * 3);
                } else if (!isProtected) {
                    penalty += pieceValue * 1.5;
                }
            }
        } else if (!isProtected) {
            penalty = pieceValue * 3;
        }

        return -penalty;
    }

    private boolean hasAdequateProtection(int pos, Bitboards bitboards, CurrentPlayer currentPlayer, boolean isMidgame) {
        Piece protector = bitboards.findLowestValueAttacker(pos, currentPlayer, isMidgame, pos);
        return protector != null;
    }

    private boolean isSquareAttackedAndNotProtected(int square, BitSet alliesAttackVectors, BitSet enemiesAttackVectors) {
        return enemiesAttackVectors.get(square) && !alliesAttackVectors.get(square);
    }



    public PriorityQueue<Move> generateAllMoves(Board board) {
        Comparator<Move> comparator = PeSTOEvaluation.isMidgamePhase(board.getBitboard())
                ? new MoveValueMidGameComparator()
                : new MoveValueEndGameComparator();
        PriorityQueue<Move> allMovesQueue = new PriorityQueue<>(comparator);

        CurrentPlayer currentPlayer = board.getCurrentPlayer();
        PieceColor currentColor = currentPlayer == CurrentPlayer.WHITE ? PieceColor.WHITE : PieceColor.BLACK;

        for (PieceType pieceType : PieceType.values()) {
            BitSet piecePositions = board.getBitboard().getPieceBitboard(pieceType, currentColor);
            for (int position = piecePositions.nextSetBit(0); position >= 0; position = piecePositions.nextSetBit(position + 1)) {
                MoveResult moveResult = switch (pieceType) {
                    case KNIGHT -> generateActualKnightMoves(position, board);
                    case BISHOP -> generateActualBishopMoves(position, board);
                    case ROOK -> generateActualRookMoves(position, board);
                    case QUEEN -> generateActualQueenMoves(position, board);
                    case KING -> generateActualKingMoves(position, board);
                    case PAWN -> generateActualPawnMoves(position, board);
                };
                if (moveResult != null) {
                    for (PriorityQueue<Move> moveQueue : moveResult.getAllQueues()) {
                        allMovesQueue.addAll(moveQueue);
                    }
                }
            }
        }

        return allMovesQueue;
    }


}