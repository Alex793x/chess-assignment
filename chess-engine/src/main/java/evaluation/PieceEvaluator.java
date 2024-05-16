package evaluation;

import engine.move_generation.MoveGenerator;
import model.Move;

import java.util.List;

import static evaluation.pst.PSTHandler.getPiecePositionPSTValue;

public class PieceEvaluator {
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 300;
    private static final int BISHOP_VALUE = 300;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 12000;

    private static final int KING_SAFETY_WEIGHT = 5; // Increased weight for king safety
    private static final int PAWN_STRUCTURE_WEIGHT = 5; // Increased weight for pawn structure
    private static final int MOBILITY_WEIGHT = 3;
    private static final int PROTECTION_WEIGHT = 30; // Increased weight for protection

    public int evaluate(char[][] board) {
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

        int whiteProtection = evaluateProtection(board, true);
        int blackProtection = evaluateProtection(board, false);
        whiteScore += whiteProtection * PROTECTION_WEIGHT;
        blackScore += blackProtection * PROTECTION_WEIGHT;

        int whitePawnStructureAndDevelopment = evaluatePawnStructureAndDevelopment(board, true);
        int blackPawnStructureAndDevelopment = evaluatePawnStructureAndDevelopment(board, false);
        whiteScore += whitePawnStructureAndDevelopment * PAWN_STRUCTURE_WEIGHT;
        blackScore += blackPawnStructureAndDevelopment * PAWN_STRUCTURE_WEIGHT;

        return whiteScore - blackScore;
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

    public int evaluateBestMovesWithBoardState(char[][] board) {
        System.out.println("=====================================================================");
        int whiteScore = 0;
        int blackScore = 0;

        int whiteKingSafety = evaluateKingSafety(board, true);
        int blackKingSafety = evaluateKingSafety(board, false);
        whiteScore += whiteKingSafety * KING_SAFETY_WEIGHT;
        blackScore += blackKingSafety * KING_SAFETY_WEIGHT;
        System.out.println("King Safety: White = " + whiteKingSafety + ", Black = " + blackKingSafety);

        int whitePawnStructure = evaluatePawnStructure(board, true);
        int blackPawnStructure = evaluatePawnStructure(board, false);
        whiteScore += whitePawnStructure * PAWN_STRUCTURE_WEIGHT;
        blackScore += blackPawnStructure * PAWN_STRUCTURE_WEIGHT;
        System.out.println("Pawn Structure: White = " + whitePawnStructure + ", Black = " + blackPawnStructure);

        int whiteMobility = evaluateMobility(board, true);
        int blackMobility = evaluateMobility(board, false);
        whiteScore += whiteMobility * MOBILITY_WEIGHT;
        blackScore += blackMobility * MOBILITY_WEIGHT;
        System.out.println("Mobility: White = " + whiteMobility + ", Black = " + blackMobility);

        int whiteProtection = evaluateProtection(board, true);
        int blackProtection = evaluateProtection(board, false);
        whiteScore += whiteProtection * PROTECTION_WEIGHT;
        blackScore += blackProtection * PROTECTION_WEIGHT;
        System.out.println("Protection: White = " + whiteProtection + ", Black = " + blackProtection);

        // Add primary piece evaluation
        int primaryEvaluation = evaluate(board);
        whiteScore += primaryEvaluation;
        blackScore += primaryEvaluation;

        int finalScore = whiteScore - blackScore;
        System.out.println("Final Evaluation: White Score = " + whiteScore + ", Black Score = " + blackScore + ", Final Score = " + finalScore);

        System.out.println("=====================================================================");
        return finalScore;
    }

    private int evaluateKingSafety(char[][] board, boolean isWhite) {
        int kingSafety = 0;
        int kingRank = -1, kingFile = -1;
        char king = isWhite ? 'K' : 'k';

        // Find the king's position
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == king) {
                    kingRank = row;
                    kingFile = col;
                    break;
                }
            }
            if (kingRank != -1) break;
        }

        // Evaluate king safety
        if (kingRank != -1) {
            // Check the number of pieces around the king
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            for (int[] direction : directions) {
                int newRank = kingRank + direction[0];
                int newFile = kingFile + direction[1];
                if (newRank >= 0 && newRank < 8 && newFile >= 0 && newFile < 8) {
                    char piece = board[newRank][newFile];
                    if (isWhite) {
                        if (Character.isUpperCase(piece)) {
                            kingSafety += 5; // Friendly piece near king
                        } else if (Character.isLowerCase(piece)) {
                            kingSafety -= 20; // Enemy piece near king
                        }
                    } else {
                        if (Character.isLowerCase(piece)) {
                            kingSafety += 5; // Friendly piece near king
                        } else if (Character.isUpperCase(piece)) {
                            kingSafety -= 20; // Enemy piece near king
                        }
                    }
                }
            }

            // Check for open files near the king
            for (int file = Math.max(0, kingFile - 1); file <= Math.min(7, kingFile + 1); file++) {
                boolean openFile = true;
                for (int rank = 0; rank < 8; rank++) {
                    if (board[rank][file] != ' ') {
                        openFile = false;
                        break;
                    }
                }
                if (openFile) {
                    kingSafety -= 15; // Open file near king
                }
            }
        }

        return kingSafety;
    }

    private int evaluatePawnStructure(char[][] board, boolean isWhite) {
        int pawnStructure = 0;
        char pawn = isWhite ? 'P' : 'p';
        int[] pawnCountByFile = new int[8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == pawn) {
                    System.out.println("pawn found");
                    pawnCountByFile[col]++;
                    // Check for passed pawns
                    boolean passed = true;
                    if (isWhite) {
                        for (int i = row - 1; i >= 0; i--) {
                            if (board[i][col] == 'p') {
                                passed = false;
                                break;
                            }
                        }
                    } else {
                        for (int i = row + 1; i < 8; i++) {
                            if (board[i][col] == 'P') {
                                passed = false;
                                break;
                            }
                        }
                    }
                    if (passed) {
                        pawnStructure += 20;
                    }
                }
            }
        }

        for (int col = 0; col < 8; col++) {
            if (pawnCountByFile[col] > 1) {
                pawnStructure -= 10 * (pawnCountByFile[col] - 1); // Doubled pawns
            }
            if (pawnCountByFile[col] == 0) {
                boolean isolated = true;
                if (col > 0 && pawnCountByFile[col - 1] > 0) isolated = false;
                if (col < 7 && pawnCountByFile[col + 1] > 0) isolated = false;
                if (isolated) {
                    pawnStructure -= 10; // Isolated pawns
                }
            }
        }

        return pawnStructure;
    }

    private int evaluatePawnStructureAndDevelopment(char[][] board, boolean isWhite) {
        int score = 0;
        char pawn = isWhite ? 'P' : 'p';
        char[] minorPieces = isWhite ? new char[]{'N', 'B'} : new char[]{'n', 'b'};
        int initialPawnRow = isWhite ? 6 : 1;
        int[] pawnCountByFile = new int[8];
        boolean[] minorPieceDeveloped = {false, false};

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char piece = board[row][col];

                // Pawn structure evaluation
                if (piece == pawn) {
                    pawnCountByFile[col]++;
                    boolean passed = true;
                    if (isWhite) {
                        for (int i = row - 1; i >= 0; i--) {
                            if (board[i][col] == 'p') {
                                passed = false;
                                break;
                            }
                        }
                    } else {
                        for (int i = row + 1; i < 8; i++) {
                            if (board[i][col] == 'P') {
                                passed = false;
                                break;
                            }
                        }
                    }
                    if (passed) {
                        score += 20;
                    }
                }

                // Development evaluation
                if (row == initialPawnRow && piece == pawn) {
                    score -= 5; // Pawn hasn't moved
                } else if (row == initialPawnRow && piece == ' ') {
                    score += 5; // Pawn has moved
                }

                for (int i = 0; i < minorPieces.length; i++) {
                    if (piece == minorPieces[i]) {
                        if (isWhite && row < 6) {
                            minorPieceDeveloped[i] = true; // Piece is off the initial rank
                        } else if (!isWhite && row > 1) {
                            minorPieceDeveloped[i] = true; // Piece is off the initial rank
                        }
                    }
                }
            }
        }

        // Evaluate doubled and isolated pawns
        for (int col = 0; col < 8; col++) {
            if (pawnCountByFile[col] > 1) {
                score -= 10 * (pawnCountByFile[col] - 1); // Doubled pawns
            }
            if (pawnCountByFile[col] == 0) {
                boolean isolated = true;
                if (col > 0 && pawnCountByFile[col - 1] > 0) isolated = false;
                if (col < 7 && pawnCountByFile[col + 1] > 0) isolated = false;
                if (isolated) {
                    score -= 10; // Isolated pawns
                }
            }
        }

        // Evaluate minor pieces development
        for (boolean developed : minorPieceDeveloped) {
            if (developed) {
                score += 10;
            } else {
                score -= 10;
            }
        }

        return score;
    }



    private int evaluateMobility(char[][] board, boolean isWhite) {
        List<Move> allMoves = MoveGenerator.generateAllPossibleMoves(isWhite, board, false);
        return allMoves.size();
    }

    private int evaluateProtection(char[][] board, boolean isWhite) {
        int protection = 0;

        // Generate all possible moves for the current player
        List<Move> allMoves = MoveGenerator.generateAllPossibleMoves(isWhite, board, true);

        // Boolean array to keep track of protected positions
        boolean[][] isProtected = new boolean[8][8];

        // Iterate through all generated moves
        for (Move move : allMoves) {
            int destRow = move.getSourcePosition()[0];
            int destCol = move.getSourcePosition()[1];
            char destinationPiece = board[destRow][destCol];

            // Check if the destination position is occupied by an allied piece and not already marked as protected
            if (destinationPiece != ' ' && Character.isUpperCase(destinationPiece) == isWhite && !isProtected[destRow][destCol]) {
                protection += 5;
                isProtected[destRow][destCol] = true;
            }
        }

        return protection;
    }

}
