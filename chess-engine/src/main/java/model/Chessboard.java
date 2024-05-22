package model;

import engine.util.ZobristHashing;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Scanner;


@Getter
@Setter
@NoArgsConstructor
public class Chessboard {
    //Fen String generator

    char[][] board;
    private long hash;

    public void initilizeBoard() {

        this.board = new char[][] {
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };

//        this.board = new char[][] {
//                {'k', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', 'p', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', ' ', ' ', ' ', 'K', ' ', ' ', ' '}
//        };

//        this.board = new char[][] {
//                {'r', ' ', ' ', ' ', 'k', ' ', ' ', 'r'},
//                {' ', 'p', 'p', ' ', 'p', 'p', 'p', 'p'},
//                {'p', 'n', ' ', ' ', ' ', ' ', ' ', 'P'},
//                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', 'n', 'P', ' ', ' ', ' ', ' ', 'N'},
//                {' ', ' ', ' ', 'B', ' ', ' ', ' ', 'R'},
//                {'P', 'P', 'K', 'B', ' ', 'q', ' ', ' '},
//                {'R', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
//        };
    }

    public boolean makeMove(String move, boolean isWhiteTurn) {
        if (move.equals("O-O") || move.equals("O-O-O")) {
            return handleCastling(move, isWhiteTurn);
        }

        if (move.length() != 5 || move.charAt(2) != ' ') {
            System.out.println("Invalid move format. Please use the format 'e2 e4' or 'O-O'/'O-O-O' for castling.");
            return false;
        }

        int fromChar = move.charAt(0) - 'a';
        int fromNum = 8 - (move.charAt(1) - '0');
        int toChar = move.charAt(3) - 'a';
        int toNum = 8 - (move.charAt(4) - '0');

        if (fromChar < 0 || fromChar >= 8 || toChar < 0 || toChar >= 8 ||
                fromNum < 0 || fromNum >= 8 || toNum < 0 || toNum >= 8) {
            System.out.println("Invalid move: Coordinates out of board range.");
            return false;
        }

        char piece = board[fromNum][fromChar];

        if (isWhiteTurn && Character.isLowerCase(piece) ||
                !isWhiteTurn && Character.isUpperCase(piece)) {
            System.out.println("Invalid move: Cannot move opponent's piece.");
            return false;
        }

        // Update the hash for the move
        hash ^= ZobristHashing.getPieceHash(fromNum, fromChar, piece);
        hash ^= ZobristHashing.getPieceHash(toNum, toChar, board[toNum][toChar]);
        hash ^= ZobristHashing.getPieceHash(toNum, toChar, piece);

        // Handle pawn promotion
        if (piece == 'P' && toNum == 0) {
            piece = handlePromotion(isWhiteTurn);
        } else if (piece == 'p' && toNum == 7) {
            piece = handlePromotion(isWhiteTurn);
        }

        // Move the piece
        board[fromNum][fromChar] = ' ';
        board[toNum][toChar] = piece;

        return true;
    }

    private boolean handleCastling(String move, boolean isWhiteTurn) {
        if (isWhiteTurn) {
            if (move.equals("O-O")) { // White king-side castling
                if (board[7][4] == 'K' && board[7][7] == 'R' && board[7][5] == ' ' && board[7][6] == ' ') {
                    board[7][4] = ' ';
                    board[7][7] = ' ';
                    board[7][6] = 'K';
                    board[7][5] = 'R';
                    return true;
                }
            } else if (move.equals("O-O-O")) { // White queen-side castling
                if (board[7][4] == 'K' && board[7][0] == 'R' && board[7][1] == ' ' && board[7][2] == ' ' && board[7][3] == ' ') {
                    board[7][4] = ' ';
                    board[7][0] = ' ';
                    board[7][2] = 'K';
                    board[7][3] = 'R';
                    return true;
                }
            }
        } else {
            if (move.equals("O-O")) { // Black king-side castling
                if (board[0][4] == 'k' && board[0][7] == 'r' && board[0][5] == ' ' && board[0][6] == ' ') {
                    board[0][4] = ' ';
                    board[0][7] = ' ';
                    board[0][6] = 'k';
                    board[0][5] = 'r';
                    return true;
                }
            } else if (move.equals("O-O-O")) { // Black queen-side castling
                if (board[0][4] == 'k' && board[0][0] == 'r' && board[0][1] == ' ' && board[0][2] == ' ' && board[0][3] == ' ') {
                    board[0][4] = ' ';
                    board[0][0] = ' ';
                    board[0][2] = 'k';
                    board[0][3] = 'r';
                    return true;
                }
            }
        }
        System.out.println("Invalid castling move.");
        return false;
    }

    private char handlePromotion(boolean isWhiteTurn) {
        Scanner scanner = new Scanner(System.in);
        char piece;
        while (true) {
            System.out.println("Pawn promotion! Choose piece to promote to (Q, R, B, N): ");
            String input = scanner.nextLine().toUpperCase();
            if (input.length() == 1 && "QRBN".contains(input)) {
                piece = input.charAt(0);
                break;
            } else {
                System.out.println("Invalid input. Please enter Q, R, B, or N.");
            }
        }
        return isWhiteTurn ? piece : Character.toLowerCase(piece);
    }





}
