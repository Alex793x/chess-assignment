package engine.move_generation;

import engine.move_generation.comparators.MoveValueComparator;
import model.Move;
import model.MoveResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MoveGenerator {

    public static boolean pawnPromotionFlag = false;
    private static int[] promotionPosition = new int[2];

    public synchronized static MoveResult generatePossibleMoves(boolean isWhite, char[][] board) {
        MoveResult moveResult = new MoveResult(new MoveValueComparator());

        for (int rank = 0; rank < board.length; rank++) {
            for (int file = 0; file < board[rank].length; file++) {
                char piece = board[rank][file];
                if (isWhite) {
                    switch (piece) {
                        case 'P' -> addMovesToResult(moveResult, generateWhitePawnsMove(rank, file, board));
                        case 'R' -> addMovesToResult(moveResult, generateRookMoves(rank, file, isWhite, board));
                        case 'N' -> addMovesToResult(moveResult, generateKnightMoves(rank, file, isWhite, board));
                        case 'B' -> addMovesToResult(moveResult, generateBishopMoves(rank, file, isWhite, board));
                        case 'Q' -> addMovesToResult(moveResult, generateQueenMoves(rank, file, isWhite, board));
                        case 'K' -> addMovesToResult(moveResult, generateKingMoves(rank, file, isWhite, board));
                    }
                } else {
                    switch (piece) {
                        case 'p' -> addMovesToResult(moveResult, generateBlackPawnsMove(rank, file, board));
                        case 'r' -> addMovesToResult(moveResult, generateRookMoves(rank, file, isWhite, board));
                        case 'n' -> addMovesToResult(moveResult, generateKnightMoves(rank, file, isWhite, board));
                        case 'b' -> addMovesToResult(moveResult, generateBishopMoves(rank, file, isWhite, board));
                        case 'q' -> addMovesToResult(moveResult, generateQueenMoves(rank, file, isWhite, board));
                        case 'k' -> addMovesToResult(moveResult, generateKingMoves(rank, file, isWhite, board));
                    }
                }
            }
        }

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

    public static List<Move> generateWhitePawnsMove(int rank, int file, char[][] board) {
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

    public static List<Move> generateBlackPawnsMove(int rank, int file, char[][] board) {
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

    public static List<Move> generateKnightMoves(int rank, int file, boolean isWhite, char[][] board) {
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
                if (target == ' ' || (isWhite && Character.isLowerCase(target)) || (!isWhite && Character.isUpperCase(target))) {
                    possibleKnightMoves.add(newPossibleMove(rank, file, newRankPosition, newFilePosition, board));
                }
            }
        }

        return possibleKnightMoves;

    }

    public static List<Move> generateBishopMoves(int rank, int file, boolean isWhite, char[][] board) {
        List<Move> possibleBishopMoves = new ArrayList<>();
        moveDiagonally(rank, file, 1, -1, isWhite, possibleBishopMoves, board);  // Left diagonal back
        moveDiagonally(rank, file, 1, 1, isWhite, possibleBishopMoves, board);   // Right diagonal back
        moveDiagonally(rank, file, -1, -1, isWhite, possibleBishopMoves, board); // Left diagonal forward
        moveDiagonally(rank, file, -1, 1, isWhite, possibleBishopMoves, board);  // Right diagonal forward
        return possibleBishopMoves;
    }

    public static List<Move> generateRookMoves(int rank, int file, boolean isWhite, char[][] board) {
        List<Move> possibleRookMoves = new ArrayList<>();
        moveInDirection(rank, file, 0, 1, isWhite, possibleRookMoves, board);  // Move right
        moveInDirection(rank, file, 0, -1, isWhite, possibleRookMoves, board); // Move left
        moveInDirection(rank, file, -1, 0, isWhite, possibleRookMoves, board); // Move forward
        moveInDirection(rank, file, 1, 0, isWhite, possibleRookMoves, board);  // Move back
        return possibleRookMoves;
    }

    public static List<Move> generateQueenMoves(int rank, int file, boolean isWhite, char[][] board) {
        List<Move> possibleQueenMoves = new ArrayList<>();
        moveDiagonally(rank, file, 1, -1, isWhite, possibleQueenMoves, board);  // Left diagonal back
        moveDiagonally(rank, file, 1, 1, isWhite, possibleQueenMoves, board);   // Right diagonal back
        moveDiagonally(rank, file, -1, -1, isWhite, possibleQueenMoves, board); // Left diagonal forward
        moveDiagonally(rank, file, -1, 1, isWhite, possibleQueenMoves, board);  // Right diagonal forward
        moveInDirection(rank, file, 0, 1, isWhite, possibleQueenMoves, board);  // Move right
        moveInDirection(rank, file, 0, -1, isWhite, possibleQueenMoves, board); // Move left
        moveInDirection(rank, file, -1, 0, isWhite, possibleQueenMoves, board); // Move forward
        moveInDirection(rank, file, 1, 0, isWhite, possibleQueenMoves, board);  // Move back
        return possibleQueenMoves;
    }

    private static void moveDiagonally(int rank, int file, int rankIncrement, int fileIncrement, boolean isWhite, List<Move> possibleMoves, char[][] board) {
        int newRankPosition = rank + rankIncrement;
        int newFilePosition = file + fileIncrement;
        while (newRankPosition >= 0 && newRankPosition < 8 && newFilePosition >= 0 && newFilePosition < 8) {
            char target = board[newRankPosition][newFilePosition];
            if (target == ' ') {
                possibleMoves.add(newPossibleMove(rank, file, newRankPosition, newFilePosition, board));
            } else if ((isWhite && Character.isLowerCase(target)) || (!isWhite && Character.isUpperCase(target))) {
                possibleMoves.add(newPossibleMove(rank, file, newRankPosition, newFilePosition, board));
                break;
            } else {
                break;
            }
            newRankPosition += rankIncrement;
            newFilePosition += fileIncrement;
        }
    }

    private static void moveInDirection(int rank, int file, int rankIncrement, int fileIncrement, boolean isWhite, List<Move> possibleMoves, char[][] board) {
        int newRankPosition = rank + rankIncrement;
        int newFilePosition = file + fileIncrement;
        while (newRankPosition >= 0 && newRankPosition < 8 && newFilePosition >= 0 && newFilePosition < 8) {
            char target = board[newRankPosition][newFilePosition];
            if (target == ' ') {
                possibleMoves.add(newPossibleMove(rank, file, newRankPosition, newFilePosition, board));
            } else if ((isWhite && Character.isLowerCase(target)) || (!isWhite && Character.isUpperCase(target))) {
                possibleMoves.add(newPossibleMove(rank, file, newRankPosition, newFilePosition, board));
                break;
            } else {
                break;
            }
            newRankPosition += rankIncrement;
            newFilePosition += fileIncrement;
        }
    }

    public static List<Move> generateKingMoves(int rank, int file, boolean isWhite, char[][] board) {
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
                if (target == ' ' || (isWhite && Character.isLowerCase(target)) || (!isWhite && Character.isUpperCase(target))) {
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
}
