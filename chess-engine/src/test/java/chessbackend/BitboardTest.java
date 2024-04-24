package chessbackend;

import chess.board.Bitboard;
import org.junit.jupiter.api.Test;

public class BitboardTest {

    @Test
    void testReadFENString() {
        Bitboard bitboard = new Bitboard();
        String fen = "rn1qkbnr/ppp1pppp/8/1B3b2/3Pp3/2P5/PP3PPP/RNBQK1NR b KQkq - 1 4"; // Custom FEN for this board setup
        bitboard.readFEN_String(fen);
        System.out.println("Updated Bitboards: ");
        System.out.println(bitboard.convertBitboardToBinaryString());

    }

}
