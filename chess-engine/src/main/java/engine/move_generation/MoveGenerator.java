package engine.move_generation;

import engine.move_generation.comparators.MoveValueComparator;
import model.Move;
import model.MoveResult;

import java.util.*;

public class MoveGenerator {

    public static boolean pawnPromotionFlag = false;
    private static final int[] promotionPosition = new int[2];

    public synchronized static List<Move> generateAllPossibleMoves(boolean isWhite, char[][] board, boolean forProtection) {
        List<Move> allMoves = new ArrayList<>();
        for (int rank = 0; rank < board.length; rank++) {
            for (int file = 0; file < board[rank].length; file++) {
                char piece = board[rank][file];
                if (isWhite) {
                    switch (piece) {
                        case 'P' -> allMoves.addAll(generateWhitePawnsMove(rank, file, board, forProtection));
                        case 'R' -> allMoves.addAll(generateRookMoves(rank, file, isWhite, board, forProtection));
                        case 'N' -> allMoves.addAll(generateKnightMoves(rank, file, isWhite, board, forProtection));
                        case 'B' -> allMoves.addAll(generateBishopMoves(rank, file, isWhite, board, forProtection));
                        case 'Q' -> allMoves.addAll(generateQueenMoves(rank, file, isWhite, board, forProtection));
                        case 'K' -> allMoves.addAll(generateKingMoves(rank, file, isWhite, board, forProtection));
                    }
                } else {
                    switch (piece) {
                        case 'p' -> allMoves.addAll(generateBlackPawnsMove(rank, file, board, forProtection));
                        case 'r' -> allMoves.addAll(generateRookMoves(rank, file, isWhite, board, forProtection));
                        case 'n' -> allMoves.addAll(generateKnightMoves(rank, file, isWhite, board, forProtection));
                        case 'b' -> allMoves.addAll(generateBishopMoves(rank, file, isWhite, board, forProtection));
                        case 'q' -> allMoves.addAll(generateQueenMoves(rank, file, isWhite, board, forProtection));
                        case 'k' -> allMoves.addAll(generateKingMoves(rank, file, isWhite, board, forProtection));
                    }
                }
            }
        }
        return allMoves;
    }

    // Modified to generate moves that don't lead to checkmate
    public synchronized static MoveResult generateAllPossibleMovesWithCheckPrevention(boolean isWhite, char[][] board) {
        MoveResult moveResult = new MoveResult(new MoveValueComparator());
        for (int rank = 0; rank < board.length; rank++) {
            for (int file = 0; file < board[rank].length; file++) {
                char piece = board[rank][file];
                if (isWhite) {
                    switch (piece) {
                        case 'P' -> addMovesToResult(moveResult, generateWhitePawnsMove(rank, file, board, false));
                        case 'R' -> addMovesToResult(moveResult, generateRookMoves(rank, file, isWhite, board, false));
                        case 'N' -> addMovesToResult(moveResult, generateKnightMoves(rank, file, isWhite, board, false));
                        case 'B' -> addMovesToResult(moveResult, generateBishopMoves(rank, file, isWhite, board, false));
                        case 'Q' -> addMovesToResult(moveResult, generateQueenMoves(rank, file, isWhite, board, false));
                        case 'K' -> addMovesToResult(moveResult, generateKingMoves(rank, file, isWhite, board, false));
                    }
                } else {
                    switch (piece) {
                        case 'p' -> addMovesToResult(moveResult, generateBlackPawnsMove(rank, file, board, false));
                        case 'r' -> addMovesToResult(moveResult, generateRookMoves(rank, file, isWhite, board, false));
                        case 'n' -> addMovesToResult(moveResult, generateKnightMoves(rank, file, isWhite, board, false));
                        case 'b' -> addMovesToResult(moveResult, generateBishopMoves(rank, file, isWhite, board, false));
                        case 'q' -> addMovesToResult(moveResult, generateQueenMoves(rank, file, isWhite, board, false));
                        case 'k' -> addMovesToResult(moveResult, generateKingMoves(rank, file, isWhite, board, false));
                    }
                }
            }
        }

        // Filter out moves that lead to checkmate
        moveResult.getAllQueues().forEach(queue -> {
            Iterator<Move> iterator = queue.iterator();
            while (iterator.hasNext()) {
                Move move = iterator.next();
                applyMove(move, board);
                if (isCheckmate(board, !isWhite)) {
                    iterator.remove();
                }
                undoMove(move, board);
            }
        });

        return moveResult;
    }

    private synchronized static void addMovesToResult(MoveResult moveResult, List<Move> moves) {
        for (Move move : moves) {
            if (MoveValueComparator.isPromotionMove(move)) {
                moveResult.getPromotionMoves().add(move);
            } else if (move.getCapturedPiece() != '\u0000') {
                moveResult.getValidCaptures().add(move);
            } else {
                moveResult.getValidMoves().add(move);
            }
        }
    }

    private static Move newPossibleMove(int lastRankPosition, int lastFilePosition, int newRankPosition, int newFilePosition, char[][] board) {
        Move move = new Move();
        move.destinationPosition = new int[]{lastRankPosition, lastFilePosition};
        move.sourcePosition = new int[]{newRankPosition, newFilePosition};
        move.piece = board[lastRankPosition][lastFilePosition];
        return move;
    }

    private static Move newPossiblePromotionMove(int lastRankPosition, int lastFilePosition, int newRankPosition, int newFilePosition, char promotionPiece) {
        Move move = new Move();
        move.destinationPosition = new int[]{lastRankPosition, lastFilePosition};
        move.sourcePosition = new int[]{newRankPosition, newFilePosition};
        move.piece = promotionPiece;
        move.isPromotion = true;
        return move;
    }

    public static List<Move> generateWhitePawnsMove(int rank, int file, char[][] board, boolean isProtection) {
        List<Move> possiblePawnMoves = new ArrayList<>();
        if (rank == 0) { // Promotion
            possiblePawnMoves.addAll(generatePromotionMoves(rank, file, true));
            MoveGenerator.pawnPromotionFlag = true;
            promotionPosition[0] = rank;
            promotionPosition[1] = file;
        } else {
            if (rank > 0 && board[rank - 1][file] == ' ') {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank - 1, file, board));
            }

            if (rank == 6 && board[rank - 2][file] == ' ' && board[rank - 1][file] == ' ') {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank - 2, file, board));
            }

            if (file - 1 >= 0 && board[rank - 1][file - 1] != ' ' && Character.isLowerCase(board[rank - 1][file - 1])) {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank - 1, file - 1, board));
            }

            if (file + 1 <= 7 && board[rank - 1][file + 1] != ' ' && Character.isLowerCase(board[rank - 1][file + 1])) {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank - 1, file + 1, board));
            }
        }

        return possiblePawnMoves;
    }

    public static List<Move> generateBlackPawnsMove(int rank, int file, char[][] board, boolean isProtection) {
        List<Move> possiblePawnMoves = new ArrayList<>();
        if (rank == 7) { // Promotion
            possiblePawnMoves.addAll(generatePromotionMoves(rank, file, false));
            MoveGenerator.pawnPromotionFlag = true;
            promotionPosition[0] = rank;
            promotionPosition[1] = file;
        } else {
            if (rank < 7 && board[rank + 1][file] == ' ') {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank + 1, file, board));
            }

            if (rank == 1 && board[rank + 2][file] == ' ' && board[rank + 1][file] == ' ') {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank + 2, file, board));
            }

            if (file - 1 >= 0 && board[rank + 1][file - 1] != ' ' && Character.isUpperCase(board[rank + 1][file - 1])) {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank + 1, file - 1, board));
            }

            if (file + 1 <= 7 && board[rank + 1][file + 1] != ' ' && Character.isUpperCase(board[rank + 1][file + 1])) {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank + 1, file + 1, board));
            }
        }
        return possiblePawnMoves;
    }

    private static List<Move> generatePromotionMoves(int rank, int file, boolean isWhite) {
        List<Move> promotionMoves = new ArrayList<>();
        char[] promotionPieces = isWhite ? new char[]{'Q', 'R', 'B', 'N'} : new char[]{'q', 'r', 'b', 'n'};
        for (char promotionPiece : promotionPieces) {
            promotionMoves.add(newPossiblePromotionMove(rank, file, rank, file, promotionPiece));
        }
        return promotionMoves;
    }

    public static List<Move> generateKnightMoves(int rank, int file, boolean isWhite, char[][] board, boolean forProtection) {
        List<Move> possibleKnightMoves = new ArrayList<>();
        int[][] directions = {
                {-2, -1}, {-2, +1},
                {-1, -2}, {-1, +2},
                {+1, -2}, {+1, +2},
                {+2, -1}, {+2, +1}
        };

        for (int[] direction : directions) {
            int newRankPosition = rank + direction[0];
            int newFilePosition = file + direction[1];

            if (newRankPosition >= 0 && newRankPosition < 8 && newFilePosition >= 0 && newFilePosition < 8) {
                char target = board[newRankPosition][newFilePosition];
                if (target == ' ' || (isWhite && (forProtection || Character.isLowerCase(target))) || (!isWhite && (forProtection || Character.isUpperCase(target)))) {
                    possibleKnightMoves.add(newPossibleMove(rank, file, newRankPosition, newFilePosition, board));
                }
            }
        }

        return possibleKnightMoves;
    }

    public static List<Move> generateBishopMoves(int rank, int file, boolean isWhite, char[][] board, boolean forProtection) {
        List<Move> possibleBishopMoves = new ArrayList<>();
        moveDiagonally(rank, file, 1, -1, isWhite, possibleBishopMoves, board, forProtection);  // Left diagonal back
        moveDiagonally(rank, file, 1, 1, isWhite, possibleBishopMoves, board, forProtection);   // Right diagonal back
        moveDiagonally(rank, file, -1, -1, isWhite, possibleBishopMoves, board, forProtection); // Left diagonal forward
        moveDiagonally(rank, file, -1, 1, isWhite, possibleBishopMoves, board, forProtection);  // Right diagonal forward
        return possibleBishopMoves;
    }

    public static List<Move> generateRookMoves(int rank, int file, boolean isWhite, char[][] board, boolean forProtection) {
        List<Move> possibleRookMoves = new ArrayList<>();
        moveInDirection(rank, file, 0, 1, isWhite, possibleRookMoves, board, forProtection);  // Move right
        moveInDirection(rank, file, 0, -1, isWhite, possibleRookMoves, board, forProtection); // Move left
        moveInDirection(rank, file, -1, 0, isWhite, possibleRookMoves, board, forProtection); // Move forward
        moveInDirection(rank, file, 1, 0, isWhite, possibleRookMoves, board, forProtection);  // Move back
        return possibleRookMoves;
    }

    public static List<Move> generateQueenMoves(int rank, int file, boolean isWhite, char[][] board, boolean forProtection) {
        List<Move> possibleQueenMoves = new ArrayList<>();
        moveDiagonally(rank, file, 1, -1, isWhite, possibleQueenMoves, board, forProtection);  // Left diagonal back
        moveDiagonally(rank, file, 1, 1, isWhite, possibleQueenMoves, board, forProtection);   // Right diagonal back
        moveDiagonally(rank, file, -1, -1, isWhite, possibleQueenMoves, board, forProtection); // Left diagonal forward
        moveDiagonally(rank, file, -1, 1, isWhite, possibleQueenMoves, board, forProtection);  // Right diagonal forward
        moveInDirection(rank, file, 0, 1, isWhite, possibleQueenMoves, board, forProtection);  // Move right
        moveInDirection(rank, file, 0, -1, isWhite, possibleQueenMoves, board, forProtection); // Move left
        moveInDirection(rank, file, -1, 0, isWhite, possibleQueenMoves, board, forProtection); // Move forward
        moveInDirection(rank, file, 1, 0, isWhite, possibleQueenMoves, board, forProtection);  // Move back
        return possibleQueenMoves;
    }

    private static void moveDiagonally(int rank, int file, int rankIncrement, int fileIncrement, boolean isWhite, List<Move> possibleMoves, char[][] board, boolean forProtection) {
        int newRankPosition = rank + rankIncrement;
        int newFilePosition = file + fileIncrement;
        while (newRankPosition >= 0 && newRankPosition < 8 && newFilePosition >= 0 && newFilePosition < 8) {
            char target = board[newRankPosition][newFilePosition];
            if (target == ' ' || (isWhite && (forProtection || Character.isLowerCase(target))) || (!isWhite && (forProtection || Character.isUpperCase(target)))) {
                possibleMoves.add(newPossibleMove(rank, file, newRankPosition, newFilePosition, board));
                if (target != ' ') {
                    break; // Stop if capturing an enemy or protecting an ally
                }
            } else {
                break; // Stop if blocked by an ally
            }
            newRankPosition += rankIncrement;
            newFilePosition += fileIncrement;
        }
    }

    private static void moveInDirection(int rank, int file, int rankIncrement, int fileIncrement, boolean isWhite, List<Move> possibleMoves, char[][] board, boolean forProtection) {
        int newRankPosition = rank + rankIncrement;
        int newFilePosition = file + fileIncrement;
        while (newRankPosition >= 0 && newRankPosition < 8 && newFilePosition >= 0 && newFilePosition < 8) {
            char target = board[newRankPosition][newFilePosition];
            if (target == ' ' || (isWhite && (forProtection || Character.isLowerCase(target))) || (!isWhite && (forProtection || Character.isUpperCase(target)))) {
                possibleMoves.add(newPossibleMove(rank, file, newRankPosition, newFilePosition, board));
                if (target != ' ') {
                    break; // Stop if capturing an enemy or protecting an ally
                }
            } else {
                break; // Stop if blocked by an ally
            }
            newRankPosition += rankIncrement;
            newFilePosition += fileIncrement;
        }
    }

    public static List<Move> generateKingMoves(int rank, int file, boolean isWhite, char[][] board, boolean forProtection) {
        List<Move> possibleMoves = new ArrayList<>();
        int[][] directions = {
                {1, 0}, // Forward
                {-1, 0}, // Backward
                {0, -1}, // Left
                {0, 1}, // Right
                {1, -1}, // Diagonal left forward
                {1, 1}, // Diagonal right forward
                {-1, -1}, // Diagonal left backward
                {-1, 1} // Diagonal right backward
        };

        for (int[] direction : directions) {
            int newRankPosition = rank + direction[0];
            int newFilePosition = file + direction[1];

            if (newRankPosition >= 0 && newRankPosition < 8 && newFilePosition >= 0 && newFilePosition < 8) {
                char target = board[newRankPosition][newFilePosition];
                if (target == ' ' || (isWhite && (forProtection || Character.isLowerCase(target))) || (!isWhite && (forProtection || Character.isUpperCase(target)))) {
                    possibleMoves.add(newPossibleMove(rank, file, newRankPosition, newFilePosition, board));
                }
            }
        }

        return possibleMoves;
    }

    public static void handlePawnPromotion(char[][] board, boolean isWhite, Scanner scanner) {
        System.out.println("Pawn promotion! Choose piece to promote to (Q, R, B, N): ");
        char choice = scanner.nextLine().toUpperCase().charAt(0);

        char newPiece = switch (choice) {
            case 'R' -> isWhite ? 'R' : 'r';
            case 'B' -> isWhite ? 'B' : 'b';
            case 'N' -> isWhite ? 'N' : 'n';
            default -> isWhite ? 'Q' : 'q';
        };

        int rank = promotionPosition[0];
        int file = promotionPosition[1];
        board[rank][file] = newPiece;
        pawnPromotionFlag = false;
    }

    // Helper methods for check prevention
    public static void applyMove(Move move, char[][] board) {
        int[] destinationPosition = move.getDestinationPosition();
        int[] newSoutePosition = move.getSourcePosition();
        char piece = move.isPromotion() ? move.getPromotionPiece() : move.getPiece();

        move.setCapturedPiece(board[newSoutePosition[0]][newSoutePosition[1]]);

        board[newSoutePosition[0]][newSoutePosition[1]] = piece;
        board[destinationPosition[0]][destinationPosition[1]] = ' ';

    }


    public static void undoMove(Move move, char[][] board) {
        int[] destinationPosition = move.getDestinationPosition();
        int[] newSoutePosition = move.getSourcePosition();
        char piece = move.isPromotion() ? move.getPromotionPiece() : move.getPiece();

        board[newSoutePosition[0]][newSoutePosition[1]] = move.getCapturedPiece();
        board[destinationPosition[0]][destinationPosition[1]] = piece;

    }

    private static boolean isCheckmate(char[][] board, boolean isWhite) {
        // Check if the king is in check
        if (!isCheck(board, isWhite)) {
            return false; // Not in check, so no checkmate
        }

        // Generate all possible moves for the current player
        List<Move> allMoves = generateAllPossibleMoves(isWhite, board, false);

        // Check if any move removes the check
        for (Move move : allMoves) {
            applyMove(move, board);
            if (!isCheck(board, isWhite)) {
                undoMove(move, board);
                return false; // A valid move exists to remove the check, so no checkmate
            }
            undoMove(move, board);
        }

        // No valid move removes the check, so it's checkmate
        return true;
    }

    private static boolean isCheck(char[][] board, boolean isWhite) {
        // Find the position of the king
        int kingRank = -1;
        int kingFile = -1;
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (isWhite && board[rank][file] == 'K' || !isWhite && board[rank][file] == 'k') {
                    kingRank = rank;
                    kingFile = file;
                    break;
                }
            }
            if (kingRank != -1) {
                break;
            }
        }

        // Check for attacks on the king's position
        return isAttacked(board, kingRank, kingFile, !isWhite);
    }

    // Fixed isAttacked method
    private static boolean isAttacked(char[][] board, int rank, int file, boolean attackerColor) {
        // Use the existing move generation methods to check if the position is attacked

        // Generate all possible moves for the attacker
        List<Move> attackingMoves = generateAllPossibleMoves(attackerColor, board, true);

        // Check if any of the moves target the given position
        for (Move move : attackingMoves) {
            int[] destination = move.getSourcePosition();
            if (destination[0] == rank && destination[1] == file) {
                return true; // The position is attacked
            }
        }

        return false; // No attack found
    }


}