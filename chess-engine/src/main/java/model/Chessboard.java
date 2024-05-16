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

//        this.board = new char[][] {
//                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
//                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
//                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
//                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
//        };

        this.board = new char[][] {
                {'k', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', 'p', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', 'K', ' ', ' ', ' '}
        };
    }

    public boolean makeMove(String move, boolean isWhiteTurn) {
        if (move.length() != 5 || move.charAt(2) != ' ') {
            System.out.println("Invalid move format. Please use the format 'e2 e4'.");
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

        System.out.println(fromChar + " " + fromNum + " " + toChar + " " + toNum);

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
            if (isWhiteTurn) {
                piece = handlePromotion(isWhiteTurn);
            } else {
                piece = 'Q'; // Computer promotes to Queen by default
            }
        } else if (piece == 'p' && toNum == 7) {
            if (!isWhiteTurn) {
                piece = handlePromotion(isWhiteTurn);
            } else {
                piece = 'q'; // Computer promotes to Queen by default
            }
        }

        // Move the piece
        board[fromNum][fromChar] = ' ';
        board[toNum][toChar] = piece;

        return true;
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
