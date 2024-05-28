package game;

import engine.Engine;
import engine.move_generation.MoveGenerator;
import lombok.NoArgsConstructor;
import model.Chessboard;
import model.Move;
import model.MoveResult;

import java.util.List;
import java.util.Scanner;

@NoArgsConstructor
public final class GameEngine {

    public static void printBoard(char[][] board) {
        System.out.println("       A    B    C    D    E    F    G    H");
        System.out.println("    +----+----+----+----+----+----+----+----+");
        for (int i = 0; i < board.length; i++) {
            System.out.print("  " + (8 - i) + " |");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print("  " + getPieceSymbol(board[i][j]) + " |");
            }
            System.out.println("  " + (8 - i) + "th rank");
            System.out.println("    +----+----+----+----+----+----+----+----+");
        }
        System.out.println("       A    B    C    D    E    F    G    H");
    }


    private static String getPieceSymbol(char piece) {
        return switch (piece) {
            case 'r' -> "r";
            case 'n' -> "n";
            case 'b' -> "b";
            case 'q' -> "q";
            case 'k' -> "k";
            case 'p' -> "p";
            case 'R' -> "R";
            case 'N' -> "N";
            case 'B' -> "B";
            case 'Q' -> "Q";
            case 'K' -> "K";
            case 'P' -> "P";
            default -> " ";
        };
    }


    public static void playChess(Chessboard chessboard) {
        System.out.println("Welcome to Chess-Awesome! Let's begin an exciting game of strategy and skill.");
        Scanner scanner = new Scanner(System.in);

        // Choose sides
        String side = chooseSide(scanner);
        boolean isWhiteSide = side.equals("WHITE");

        // Game loop
        boolean exit = false;
        while (!exit) {
            printBoard(chessboard.getBoard());

            if (isWhiteSide) {
                exit = playerTurn(scanner, chessboard, true);
                printBoard(chessboard.getBoard());
                if (!exit) {
                    computerTurn(chessboard, false);
                }
            } else {
                computerTurn(chessboard, true);
                printBoard(chessboard.getBoard());
                System.out.println("-------------------------------------------");
                exit = playerTurn(scanner, chessboard, false);
            }
        }

        scanner.close();
    }

    private static String chooseSide(Scanner scanner) {
        String side;
        while (true) {
            System.out.println("Please choose your side. Type 'White' if you want to play as White, or 'Black' if you want to play as Black:");
            side = scanner.nextLine().trim().toUpperCase();
            if (side.equals("WHITE") || side.equals("BLACK")) {
                break;
            } else {
                System.out.println("Invalid input. Make sure to type 'White' or 'Black' to select your side.");
            }
        }
        return side;
    }

    private static boolean playerTurn(Scanner scanner, Chessboard chessboard, boolean isWhiteTurn) {
        boolean validMove = false;
        boolean exit = false;

        // Check if castling is available
        List<Move> castlingMoves = MoveGenerator.generateCastlingMoves(isWhiteTurn, chessboard.getBoard());
        boolean canCastle = !castlingMoves.isEmpty();

        if (canCastle) {
            System.out.println("Do you want to castle? (Y/N):");
            String response = scanner.nextLine().trim().toUpperCase();
            if (response.equals("Y")) {
                System.out.println("Choose castling type: 'O-O' for king-side or 'O-O-O' for queen-side:");
                String castlingType = scanner.nextLine().trim().toUpperCase();
                if (castlingType.equals("O-O") || castlingType.equals("O-O-O")) {
                    if (chessboard.makeMove(castlingType, isWhiteTurn)) {
                        return exit;
                    } else {
                        System.out.println("Invalid castling move.");
                    }
                } else {
                    System.out.println("Invalid castling type.");
                }
            }
        }

        while (!validMove) {
            System.out.println(isWhiteTurn ? "Whites turn. Please enter your move in the format example: e7 e5." :
                    "Blacks turn. Please enter your move in the format example: e7 e5.");
            String move = scanner.nextLine();
            if (move.equalsIgnoreCase("End")) {
                System.out.println("Closing the game!");
                exit = true;
                break;
            }
            validMove = chessboard.makeMove(move, isWhiteTurn);
            if (!validMove) {
                System.out.println("The move you entered is invalid. Please enter a valid move in the correct format:");
            }
        }

        // Handle promotion after a valid move
        if (MoveGenerator.pawnPromotionFlag) {
            MoveGenerator.handlePawnPromotion(chessboard.getBoard(), isWhiteTurn, scanner);
        }
        return exit;
    }



    private static void computerTurn(Chessboard chessboard, boolean isWhiteTurn) {
        System.out.println("-------------------------------------------");
        System.out.println("The computer is thinking about its move. Hold up...");
        Engine engine = new Engine(chessboard.getBoard(), 5);
        chessboard.setBoard(engine.bestMove(isWhiteTurn));
        System.out.println("The computer has completed its move, board updated: ");
        printBoard(chessboard.getBoard());
        // Handle promotion after a valid move
        if (MoveGenerator.pawnPromotionFlag) {
            resetPawnPromotionFlag();
        }
    }

    private static void resetPawnPromotionFlag() {
        MoveGenerator.pawnPromotionFlag = false;
    }

}
