package model;

import engine.Engine;
import engine.evaluations.Evaluator;
import engine.move_generation.MoveGenerator;
import engine.move_generation.model.MoveResult;
import lombok.Getter;
import lombok.Setter;
import model.enums.CurrentPlayer;
import model.enums.PieceColor;
import model.enums.PieceType;
import util.pst.PSTHandler;

import java.util.BitSet;
import java.util.Random;

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

    public static void main(String[] args) {
        Board board = new Board();
        board.getBitboard().setupInitialPositions();
        board.getBitboard().loadFENFlipped("rnb1k1nr/pppp1ppp/8/4pq2/P2bN3/8/1PPPPPPP/R1BQKB1R w Kkq - 2 7");
        System.out.println(PSTHandler.getMidGameValue(PieceType.KING, PieceColor.BLACK, 4));
        board.getBitboard().printBoardFlipped();
        board.setCurrentPlayer(CurrentPlayer.WHITE);
        Engine engine = new Engine(6);
        System.out.println(Evaluator.evaluateStaticBoard(board));
        System.out.println(engine.findBestMove(board, true));
        board.getBitboard().printNumberPositionFlipped();
        board.getBitboard().printBoardFlipped();
    }

}