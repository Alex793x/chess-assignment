package engine.move_generation;

import engine.move_generation.comparators.MoveValueComparator;
import model.Move;
import model.MoveResult;

import java.util.*;

public class MoveGenerator {

    public synchronized static PriorityQueue<Move> generatePossibleMoves(boolean isWhite, char[][] board) {
        Comparator<Move> comparator = new MoveValueComparator();
        PriorityQueue<Move> allMovesQueue = new PriorityQueue<>(comparator);
        MoveResult moveResult = new MoveResult(comparator);

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

        for (PriorityQueue<Move> moveQueue : moveResult.getAllQueues()) {
            allMovesQueue.addAll(moveQueue);
        }

        return allMovesQueue;
    }

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

    public static List<Move> generateWhitePawnsMove(int rank, int file, char[][] board, boolean forProtection) {
        List<Move> possiblePawnMoves = new ArrayList<>();
        if (rank == 0) { // Promotion
            possiblePawnMoves.add(newPossibleMove(rank, file, rank, file, board));
        } else {
            if (rank > 0 && board[rank - 1][file] == ' ') {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank - 1, file, board));
            }

            if (rank == 6 && board[rank - 2][file] == ' ' && board[rank - 1][file] == ' ') {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank - 2, file, board));
            }

            if (file - 1 >= 0 && board[rank - 1][file - 1] != ' ' && (forProtection || Character.isLowerCase(board[rank - 1][file - 1]))) {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank - 1, file - 1, board));
            }

            if (file + 1 <= 7 && board[rank - 1][file + 1] != ' ' && (forProtection || Character.isLowerCase(board[rank - 1][file + 1]))) {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank - 1, file + 1, board));
            }
        }

        return possiblePawnMoves;
    }

    public static List<Move> generateBlackPawnsMove(int rank, int file, char[][] board, boolean forProtection) {
        List<Move> possiblePawnMoves = new ArrayList<>();
        if (rank == 7) { // Promotion
            possiblePawnMoves.add(newPossibleMove(rank, file, rank, file, board));
        } else {
            if (rank < 7 && board[rank + 1][file] == ' ') {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank + 1, file, board));
            }

            if (rank == 1 && board[rank + 2][file] == ' ' && board[rank + 1][file] == ' ') {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank + 2, file, board));
            }

            if (file - 1 >= 0 && board[rank + 1][file - 1] != ' ' && (forProtection || Character.isUpperCase(board[rank + 1][file - 1]))) {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank + 1, file - 1, board));
            }

            if (file + 1 <= 7 && board[rank + 1][file + 1] != ' ' && (forProtection || Character.isUpperCase(board[rank + 1][file + 1]))) {
                possiblePawnMoves.add(newPossibleMove(rank, file, rank + 1, file + 1, board));
            }
        }
        return possiblePawnMoves;
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
}
