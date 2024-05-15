package model;

import engine.Engine;
import engine.evaluations.Evaluator;
import engine.move_generation.MoveGenerator;
import lombok.Getter;
import lombok.Setter;
import model.enums.CurrentPlayer;
import model.enums.PieceColor;
import model.enums.PieceType;
import util.pst.PSTHandler;

import java.util.*;

@Getter
@Setter
public class Board {

    private Player[] players;
    private CurrentPlayer currentPlayer;
    private int fullMoveCounter;
    private int halfMoveClock;
    private Bitboards bitboard;
    private boolean isGameOver;

    private static final long[][][] zobristTable = new long[2][6][64];
    private long zobristHash;

    static {
        Random random = new Random();
        for (int color = 0; color < 2; color++) {
            for (int piece = 0; piece < 6; piece++) {
                for (int square = 0; square < 64; square++) {
                    zobristTable[color][piece][square] = random.nextLong();
                }
            }
        }
    }


    public Board() {
        this.players = new Player[2];
        this.players[0] = new Player(CurrentPlayer.WHITE, false, false, false, false);
        this.players[1] = new Player(CurrentPlayer.BLACK, false, false, false, false);
        this.currentPlayer = CurrentPlayer.WHITE;
        this.fullMoveCounter = 1;
        this.halfMoveClock = 0;
        this.bitboard = new Bitboards();
        this.bitboard.setupInitialPositions();
        this.zobristHash = computeZobristHash();
    }

    public Board deepCopy() {
        Board copy = new Board();
        copy.setPlayers(this.players.clone());
        copy.setCurrentPlayer(this.currentPlayer);
        copy.setFullMoveCounter(this.fullMoveCounter);
        copy.setHalfMoveClock(this.halfMoveClock);
        copy.setBitboard(this.bitboard.deepCopy());
        copy.setGameOver(this.isGameOver);
        return copy;
    }


    private long computeZobristHash() {
        long hash = 0;
        // Compute the initial hash based on the board position
        // Assuming you have methods to get piece type and color at a square
        for (int square = 0; square < 64; square++) {
            Piece piece = bitboard.getPieceBySquare(square);
            if (piece != null) {
                int colorIndex = piece.getPieceColor() == PieceColor.WHITE ? 0 : 1;
                int pieceIndex = piece.getPieceType().ordinal();
                hash ^= zobristTable[colorIndex][pieceIndex][square];
            }
        }
        return hash;
    }


    public Player getPlayer(CurrentPlayer player) {
        return this.players[player.ordinal()];
    }


    public void makeMove(Move move) {
        Piece piece = move.getPiece();
        int fromSquare = move.getFromSquare();
        int toSquare = move.getToSquare();

        synchronized (this) {
            bitboard.clearPiece(piece.getPieceType(), piece.getPieceColor(), fromSquare);
            bitboard.placePiece(piece, toSquare);

            if (move.isCapture() && move.getCapturedPiece() != null) {
                bitboard.clearPiece(move.getCapturedPiece().getPieceType(), move.getCapturedPiece().getPieceColor(), toSquare);
            }

            if (piece.getPieceType() == PieceType.PAWN || move.isCapture()) {
                this.halfMoveClock = 0;
            } else {
                this.halfMoveClock++;
            }

            this.currentPlayer = this.currentPlayer.getOppositePlayer();
            if (this.currentPlayer == CurrentPlayer.WHITE) {
                this.fullMoveCounter++;
            }
        }

        bitboard.updateAttackVectorsAfterMove(fromSquare, toSquare, piece);
    }

    public void undoMove(Move move) {
        Piece piece = move.getPiece();
        int fromSquare = move.getFromSquare();
        int toSquare = move.getToSquare();

        synchronized (this) {
            if (piece != null) {
                bitboard.placePiece(piece, fromSquare);
                bitboard.clearPiece(piece.getPieceType(), piece.getPieceColor(), toSquare);
            }

            if (move.isCapture() && move.getCapturedPiece() != null) {
                Piece capturedPiece = move.getCapturedPiece();
                bitboard.placePiece(capturedPiece, toSquare);
            }

            this.halfMoveClock = move.getHalfMoveClockBeforeMove();

            this.currentPlayer = this.currentPlayer.getOppositePlayer();
            if (this.currentPlayer == CurrentPlayer.BLACK) {
                this.fullMoveCounter--;
            }
        }

        assert piece != null;
        bitboard.updateAttackVectorsAfterMove(toSquare, fromSquare, piece);
    }


    public long getHash() {
        long hash = 0L;
        // XOR all bitboards to combine their hashes
        hash ^= bitSetToLong(bitboard.getWhitePawns());
        hash ^= bitSetToLong(bitboard.getWhiteKnights());
        hash ^= bitSetToLong(bitboard.getWhiteBishops());
        hash ^= bitSetToLong(bitboard.getWhiteRooks());
        hash ^= bitSetToLong(bitboard.getWhiteQueens());
        hash ^= bitSetToLong(bitboard.getWhiteKing());
        hash ^= bitSetToLong(bitboard.getBlackPawns());
        hash ^= bitSetToLong(bitboard.getBlackKnights());
        hash ^= bitSetToLong(bitboard.getBlackBishops());
        hash ^= bitSetToLong(bitboard.getBlackRooks());
        hash ^= bitSetToLong(bitboard.getBlackQueens());
        hash ^= bitSetToLong(bitboard.getBlackKing());

        // Optionally, include the current player to move in the hash
        hash ^= currentPlayer.equals(CurrentPlayer.WHITE) ? 0 : 1;

        return hash;
    }

    public long bitSetToLong(BitSet bitSet) {
        long value = 0L;
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            value |= (1L << i);
        }
        return value;
    }


    // Method converts the fromSquare and toSquare to a move string, that can be used
    // when a player writes the move prompt in the terminal
    private static Move parseMove(String moveInput, Board board, boolean isBoardFlipped) {
        String[] parts = moveInput.split(" ");
        if (parts.length != 2) {
            return null;
        }

        int fromSquare = convertSquare(parts[0], isBoardFlipped);
        int toSquare = convertSquare(parts[1], isBoardFlipped);
        if (fromSquare == -1 || toSquare == -1) {
            return null;
        }

        Piece piece = board.getBitboard().getPieceBySquare(fromSquare);
        if (piece == null || piece.getPieceColor() != board.currentPlayer.getPieceColor()) {
            System.out.println("Invalid piece selection.");
        }

        boolean isCapture = board.getBitboard().getPieceBySquare(toSquare) != null;
        Piece capturedPiece = isCapture ? board.getBitboard().getPieceBySquare(toSquare) : null;

        return new Move(fromSquare, toSquare, 0, piece, capturedPiece, isCapture, false, board.getHalfMoveClock(), false, false, 0, 0, 0, false);
    }

    private static int convertSquare(String square, boolean isBoardFlipped) {
        if (square.length() != 2) {
            return -1;
        }

        char file = square.charAt(0);
        char rank = square.charAt(1);

        int fileIndex = file - 'a';
        int rankIndex = rank - '1';

        if (fileIndex < 0 || fileIndex > 7 || rankIndex < 0 || rankIndex > 7) {
            return -1;
        }

        if (isBoardFlipped) {
            fileIndex = 7 - fileIndex;
            rankIndex = 7 - rankIndex;
        }

        return rankIndex * 8 + fileIndex;
    }

    // This method is only used in the gameloop to validate the player move. It is not used for the AI moves.
    private static boolean isValidPlayerMove(Move move, Board board, MoveGenerator moveGenerator) {
        PriorityQueue<Move> validMoves = moveGenerator.generateAllMoves(board);
        for (Move validMove : validMoves) {
            if (validMove.getFromSquare() == move.getFromSquare() && validMove.getToSquare() == move.getToSquare()) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Board board = new Board();
        MoveGenerator moveGenerator = new MoveGenerator();
        Engine engine = new Engine(4);

        System.out.println("================::CHESS GAME::===========================");
        System.out.println("Choose to play as White or Black: \nFor White = w, For Black = b");
        String choice = scanner.nextLine().toLowerCase();

        boolean isPlayerWhite;
        if (choice.equals("w")) {
            isPlayerWhite = true;
            board.setCurrentPlayer(CurrentPlayer.WHITE);
            System.out.println("You have chosen to play as White. Make the first move.");
        } else if (choice.equals("b")) {
            isPlayerWhite = false;
            board.setCurrentPlayer(CurrentPlayer.WHITE);  // Game always starts with White
            System.out.println("You have chosen to play as Black. Wait for White to make the first move.");
        } else {
            System.out.println("Invalid choice, please restart the game and choose again.");
            return;
        }

        board.getBitboard().setupInitialPositions();
        board.getBitboard().printBoardFlipped();

        while (!board.isGameOver()) {
            if ((board.getCurrentPlayer() == CurrentPlayer.WHITE && isPlayerWhite) ||
                    (board.getCurrentPlayer() == CurrentPlayer.BLACK && !isPlayerWhite)) {
                // -------------------- PLAYER MOVE ---------------------
                System.out.println("Enter your move (e.g., e2 e4): ");
                String moveInput = scanner.nextLine();
                if (moveInput.equalsIgnoreCase("exit")) {
                    break;
                }

                Move playerMove = parseMove(moveInput, board, true);
                if (playerMove != null && isValidPlayerMove(playerMove, board, moveGenerator)) {
                    board.makeMove(playerMove);
                    System.out.println("Player's move: " + playerMove);
                    board.getBitboard().printBoardFlipped();
                } else {
                    System.out.println("Invalid move, please try again.");

                }
            } else {
                // --------------- COMPUTER MOVE --------------------
                Move engineMove = engine.findBestMove(board, board.getCurrentPlayer() == CurrentPlayer.WHITE);
                if (engineMove != null) {
                    board.makeMove(engineMove);
                    System.out.println("Engine move: " + engineMove);
                    board.getBitboard().printBoardFlipped();
                }
            }

        }

        scanner.close();
    }

    }