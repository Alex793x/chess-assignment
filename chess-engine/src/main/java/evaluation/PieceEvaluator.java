package evaluation;

import engine.move_generation.MoveGenerator;
import model.Move;
import model.MoveResult;

import java.util.ArrayList;
import java.util.List;

import static evaluation.pst.PSTHandler.getPiecePositionPSTValue;

public class PieceEvaluator {
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 300;
    private static final int BISHOP_VALUE = 300;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 12000;

    public boolean isSacrificeBeneficial(char[][] board, char piece, int targetRank, int targetFile) {
        // Get the value of the piece being sacrificed
        int pieceValue = getPieceValue(piece);

        // Get the value of the piece at the target position
        char targetPiece = board[targetRank][targetFile];
        int targetPieceValue = getPieceValue(targetPiece);

        // Consider pawn promotion
        if (targetPiece == 'P' && targetRank == 0 || targetPiece == 'p' && targetRank == 7) {
            targetPieceValue = QUEEN_VALUE; // Assume promotion to a queen
        }

        // Calculate the value difference
        int valueDifference = pieceValue - targetPieceValue;

        // More Flexible Logic:
        // 1. Allow sacrifices of minor pieces (Knight, Bishop) for pawns
        if (pieceValue <= BISHOP_VALUE && targetPiece == 'P' || pieceValue <= BISHOP_VALUE && targetPiece == 'p') {
            return true;
        }
        // 2. Allow sacrifices if the value difference is small
        return valueDifference > -200;  // Allow sacrifices up to 200 value difference
    }

    public boolean isTacticallySound(char[][] board, Move move) {
        // Apply the move to the board temporarily
        MoveGenerator.applyMove(move, board);

        // Check for forks
        boolean isFork = createsFork(board, move);

        // Check for pins
        boolean isPin = createsPin(board, move);

        // Check for discovered attacks
        boolean isDiscoveredAttack = createsDiscoveredAttack(board, move);

        // Check for skewers
        boolean isSkewer = createsSkewer(board, move);

        // Check for positional advantage
        boolean isPositionalAdvantage = isPositionalAdvantageGained(board, move);

        boolean isSacrificial = isSacrificeBeneficial(board, move.getPiece(), move.getDestinationPosition()[0], move.getDestinationPosition()[1]);

        // Undo the move
        MoveGenerator.undoMove(move, board);

        // Return true if any of the tactical elements are present
        return isFork || isPin || isDiscoveredAttack || isSkewer || isPositionalAdvantage && !isSacrificial;
    }

    private boolean createsFork(char[][] board, Move move) {
        int[] target = move.getDestinationPosition();
        char attackingPiece = move.getPiece();
        boolean attackerColor = Character.isUpperCase(attackingPiece);

        // Check for immediate threats to multiple pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == target[0] && j == target[1]) { // Don't check the target square
                    continue;
                }

                char piece = board[i][j];
                boolean defenderColor = Character.isUpperCase(piece);

                // Check for an immediate threat to an opponent's piece
                if (defenderColor != attackerColor) {
                    // Check for knight fork
                    if ((attackingPiece == 'N' || attackingPiece == 'n') && isKnightAttack(i, j, target[0], target[1])) {
                        return true;
                    }

                    // Check for bishop or queen fork
                    if ((attackingPiece == 'B' || attackingPiece == 'b' || attackingPiece == 'Q' || attackingPiece == 'q') &&
                            (isDiagonalAttack(i, j, target[0], target[1]))) {
                        return true;
                    }

                    // Check for rook or queen fork
                    if ((attackingPiece == 'R' || attackingPiece == 'r' || attackingPiece == 'Q' || attackingPiece == 'q') &&
                            (isHorizontalOrVerticalAttack(i, j, target[0], target[1]))) {
                        return true;
                    }
                }
            }
        }

        return false; // No immediate fork found
    }

    private boolean createsPin(char[][] board, Move move) {
        int[] target = move.getDestinationPosition();
        char attackingPiece = move.getPiece();
        boolean attackerColor = Character.isUpperCase(attackingPiece);

        // Check for pins by bishop or queen
        if (attackingPiece == 'B' || attackingPiece == 'b' || attackingPiece == 'Q' || attackingPiece == 'q') {
            if (isPinnedByBishopOrQueen(board, target[0], target[1], attackerColor)) {
                return true;
            }
        }

        // Check for pins by rook or queen
        if (attackingPiece == 'R' || attackingPiece == 'r' || attackingPiece == 'Q' || attackingPiece == 'q') {
            if (isPinnedByRookOrQueen(board, target[0], target[1], attackerColor)) {
                return true;
            }
        }

        return false;
    }

    private boolean isPinnedByBishopOrQueen(char[][] board, int targetRank, int targetFile, boolean attackerColor) {
        // Check diagonals
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] direction : directions) {
            int i = targetRank + direction[0];
            int j = targetFile + direction[1];
            boolean foundPinnedPiece = false;
            while (i >= 0 && i < 8 && j >= 0 && j < 8) {
                char piece = board[i][j];
                if (piece != ' ') {
                    if (Character.isUpperCase(piece) != attackerColor) {
                        if (foundPinnedPiece) {
                            if (piece == (attackerColor ? 'K' : 'k')) {
                                return true;
                            }
                            break;
                        } else {
                            foundPinnedPiece = true;
                        }
                    } else {
                        break;
                    }
                }
                i += direction[0];
                j += direction[1];
            }
        }
        return false;
    }

    private boolean isPinnedByRookOrQueen(char[][] board, int targetRank, int targetFile, boolean attackerColor) {
        // Check horizontal and vertical lines
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] direction : directions) {
            int i = targetRank + direction[0];
            int j = targetFile + direction[1];
            boolean foundPinnedPiece = false;
            while (i >= 0 && i < 8 && j >= 0 && j < 8) {
                char piece = board[i][j];
                if (piece != ' ') {
                    if (Character.isUpperCase(piece) != attackerColor) {
                        if (foundPinnedPiece) {
                            if (piece == (attackerColor ? 'K' : 'k')) {
                                return true;
                            }
                            break;
                        } else {
                            foundPinnedPiece = true;
                        }
                    } else {
                        break;
                    }
                }
                i += direction[0];
                j += direction[1];
            }
        }
        return false;
    }

    private boolean createsDiscoveredAttack(char[][] board, Move move) {
        int[] source = move.getSourcePosition();
        int[] target = move.getDestinationPosition();
        char attackingPiece = move.getPiece();
        boolean attackerColor = Character.isUpperCase(attackingPiece);

        // Check for discovered attacks
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == source[0] && j == source[1]) { // Don't check the original square
                    continue;
                }
                if (i == target[0] && j == target[1]) { // Don't check the target square
                    continue;
                }

                char piece = board[i][j];
                boolean defenderColor = Character.isUpperCase(piece);

                // Check if the piece is an opponent's piece
                if (defenderColor != attackerColor) {
                    // Check for discovered attacks by bishop or queen
                    if ((attackingPiece == 'B' || attackingPiece == 'b' || attackingPiece == 'Q' || attackingPiece == 'q') &&
                            (isDiagonalAttack(i, j, source[0], source[1]) &&
                                    isDiagonalAttack(i, j, target[0], target[1]))) {
                        return true;
                    }

                    // Check for discovered attacks by rook or queen
                    if ((attackingPiece == 'R' || attackingPiece == 'r' || attackingPiece == 'Q' || attackingPiece == 'q') &&
                            (isHorizontalOrVerticalAttack(i, j, source[0], source[1]) &&
                                    isHorizontalOrVerticalAttack(i, j, target[0], target[1]))) {
                        return true;
                    }
                }
            }
        }

        return false; // No discovered attack found
    }

    private boolean createsSkewer(char[][] board, Move move) {
        int[] source = move.getSourcePosition();
        int[] target = move.getDestinationPosition();
        char attackingPiece = move.getPiece();
        boolean attackerColor = Character.isUpperCase(attackingPiece);

        // Check for skewers
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == source[0] && j == source[1]) { // Don't check the original square
                    continue;
                }
                if (i == target[0] && j == target[1]) { // Don't check the target square
                    continue;
                }

                char piece = board[i][j];
                boolean defenderColor = Character.isUpperCase(piece);

                // Check if the piece is an opponent's piece
                if (defenderColor != attackerColor) {
                    // Check for skewers by bishop or queen
                    if ((attackingPiece == 'B' || attackingPiece == 'b' || attackingPiece == 'Q' || attackingPiece == 'q') &&
                            (isDiagonalAttack(i, j, source[0], source[1]) &&
                                    isDiagonalAttack(i, j, target[0], target[1]))) {
                        return true;
                    }

                    // Check for skewers by rook or queen
                    if ((attackingPiece == 'R' || attackingPiece == 'r' || attackingPiece == 'Q' || attackingPiece == 'q') &&
                            (isHorizontalOrVerticalAttack(i, j, source[0], source[1]) &&
                                    isHorizontalOrVerticalAttack(i, j, target[0], target[1]))) {
                        return true;
                    }
                }
            }
        }
        return false; // No skewer found
    }

    private boolean isPositionalAdvantageGained(char[][] board, Move move) {
        // Check for control of key squares
        if (controlsKeySquare(board, move)) {
            return true;
        }

        // Check if the sacrifice opens lines for rooks
        if (opensLinesForRooks(board, move)) {
            return true;
        }

        // Check for king safety implications
        if (improvesKingSafety(board, move)) {
            return true;
        }

        // More complex positional evaluations can be added here
        return false;
    }

    private boolean controlsKeySquare(char[][] board, Move move) {
        int[] target = move.getDestinationPosition();
        // Simplified check for control of key squares like the center
        if (target[0] >= 3 && target[0] <= 4 && target[1] >= 3 && target[1] <= 4) {
            return true;
        }
        return false;
    }

    private boolean opensLinesForRooks(char[][] board, Move move) {
        // Simplified check if the move opens lines for rooks
        int[] target = move.getDestinationPosition();
        if ((target[0] == 0 || target[0] == 7) && (move.getPiece() == 'B' || move.getPiece() == 'b' || move.getPiece() == 'N' || move.getPiece() == 'n' || move.getPiece() == 'P' || move.getPiece() == 'p')) {
            return true;
        }
        return false;
    }

    private boolean improvesKingSafety(char[][] board, Move move) {
        // Simplified check for king safety
        int[] target = move.getDestinationPosition();
        boolean attackerColor = Character.isUpperCase(move.getPiece());
        int kingRank = -1, kingFile = -1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (attackerColor && board[i][j] == 'K' || !attackerColor && board[i][j] == 'k') {
                    kingRank = i;
                    kingFile = j;
                    break;
                }
            }
            if (kingRank != -1) {
                break;
            }
        }
        if (kingRank != -1) {
            int distance = Math.abs(target[0] - kingRank) + Math.abs(target[1] - kingFile);
            return distance >= 3; // Consider move if it moves the attacking piece further away
        }
        return false;
    }

    public int evaluate(char[][] board, boolean isWhiteTurn) {
        int whiteScore = 0;
        int blackScore = 0;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                char piece = board[row][col];
                if (piece != ' ') {
                    int pieceValue = getPieceValue(piece);
                    int positionValue = getPiecePositionPSTValue(row, col, piece);

                    if (Character.isUpperCase(piece)) {
                        whiteScore += pieceValue + positionValue;
                    } else {
                        blackScore += pieceValue + positionValue;
                    }
                }
            }
        }

        MoveResult moveResult = MoveGenerator.generateAllPossibleMovesWithCheckPrevention(isWhiteTurn, board);
        List<Move> allMoves = new ArrayList<>(moveResult.getValidMoves());
        allMoves.addAll(moveResult.getValidCaptures());
        allMoves.addAll(moveResult.getPromotionMoves());

        // Find the best move based on the isTacticallySound criteria
        Move bestTacticalMove = findBestTacticalMove(board, allMoves, isWhiteTurn);

        // If a tactically sound move is found, evaluate its potential gain
        if (bestTacticalMove != null) {
            // Calculate the value difference for the best tactical move
            int valueDifference = getPieceValue(bestTacticalMove.getPiece()) -
                    getPieceValue(board[bestTacticalMove.getDestinationPosition()[0]][bestTacticalMove.getDestinationPosition()[1]]);

            // Adjust the score based on the value difference
            if (isWhiteTurn) {
                whiteScore += valueDifference;
            } else {
                blackScore += valueDifference;
            }
        }

        return whiteScore - blackScore;
    }

    private Move findBestTacticalMove(char[][] board, List<Move> allMoves, boolean isWhiteTurn) {
        Move bestMove = null;
        int bestValue = isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Move move : allMoves) {
            if (isTacticallySound(board, move)) {
                // Evaluate the move's value
                int moveValue = getPieceValue(move.getPiece()) - getPieceValue(board[move.getDestinationPosition()[0]][move.getDestinationPosition()[1]]);

                // Update the best move if it's more advantageous
                if (isWhiteTurn && moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                } else if (!isWhiteTurn && moveValue < bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    public static int getPieceValue(char piece) {
        return switch (Character.toLowerCase(piece)) {
            case 'p' -> PAWN_VALUE;
            case 'n' -> KNIGHT_VALUE;
            case 'b' -> BISHOP_VALUE;
            case 'r' -> ROOK_VALUE;
            case 'q' -> QUEEN_VALUE;
            case 'k' -> KING_VALUE;
            default -> 0;
        };
    }


    private boolean isKnightAttack(int attackRank, int attackFile, int targetRank, int targetFile) {
        int rankDifference = Math.abs(attackRank - targetRank);
        int fileDifference = Math.abs(attackFile - targetFile);
        return (rankDifference == 2 && fileDifference == 1) || (rankDifference == 1 && fileDifference == 2);
    }

    private boolean isDiagonalAttack(int attackRank, int attackFile, int targetRank, int targetFile) {
        return Math.abs(attackRank - targetRank) == Math.abs(attackFile - targetFile);
    }

    private boolean isHorizontalOrVerticalAttack(int attackRank, int attackFile, int targetRank, int targetFile) {
        return attackRank == targetRank || attackFile == targetFile;
    }

}