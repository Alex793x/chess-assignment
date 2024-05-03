package chessbackend;

import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.evaluation.piece_board_evaluation.piece_square_board_rating.RookSquareBoardRating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static chess.engine.evaluation.Rating.*;

class BitboardTest {
    Board newBoard = new Board();

    @BeforeEach
    public void printInitialBoard() {
        System.out.println("""
                /**
                     * The initialization of the boards ensures each position of
                     *       A    B    C    D    E    F    G    H
                     *    +----+----+----+----+----+----+----+----+
                     *  8 | 56 | 57 | 58 | 59 | 60 | 61 | 62 | 63 |  8th rank
                     *    +----+----+----+----+----+----+----+----+
                     *  7 | 48 | 49 | 50 | 51 | 52 | 53 | 54 | 55 |  7th rank
                     *    +----+----+----+----+----+----+----+----+
                     *  6 | 40 | 41 | 42 | 43 | 44 | 45 | 46 | 47 |  6th rank
                     *    +----+----+----+----+----+----+----+----+
                     *  5 | 32 | 33 | 34 | 35 | 36 | 37 | 38 | 39 |  5th rank
                     *    +----+----+----+----+----+----+----+----+
                     *  4 | 24 | 25 | 26 | 27 | 28 | 29 | 30 | 31 |  4th rank
                     *    +----+----+----+----+----+----+----+----+
                     *  3 | 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 |  3rd rank
                     *    +----+----+----+----+----+----+----+----+
                     *  2 |  8 |  9 | 10 | 11 | 12 | 13 | 14 | 15 |  2nd rank
                     *    +----+----+----+----+----+----+----+----+
                     *  1 |  0 |  1 |  2 |  3 |  4 |  5 |  6 |  7 |  1st rank
                     *    +----+----+----+----+----+----+----+----+
                     *       A    B    C    D    E    F    G    H - file(s)
                     * */
                """);
        System.out.println("Initial Board!");
        System.out.println(newBoard.getBitboard().convertBitboardToBinaryString());
        System.out.println("Initial Board Rating is: " + rating(newBoard));
    }


    @Test
    void testReadFENString() {
        String fen = "r1bqk1nr/ppp2ppp/2n5/4b3/8/8/PPP1PPPP/RNB1KBNR w KQkq - 0 6"; // Custom FEN for this board setup
        newBoard.getBitboard().readFEN_String(fen);

        System.out.println("whiteboard rating is: " + whiteScoreBoardPosition(newBoard));
        System.out.println("blackboard rating is: " + blackScoreBoardPosition(newBoard));
        System.out.println("total score board rating is: " + rating(newBoard));


        //TODO Mikkel Test it! IT WOOOORKS!!! Moving pieces are aligned now!!!
        newBoard.getBitboard().removePieceFromSquare(2, PieceType.BISHOP, PieceColor.WHITE);
        newBoard.getBitboard().placePieceOnSquare(11, PieceType.BISHOP, PieceColor.WHITE);

        System.out.println("Updated Bitboards: ");
        System.out.println(newBoard.getBitboard().convertBitboardToBinaryString());
    }

    @Test
    void fixedRatingValueForFilesShouldMatchBoardRating() {
        System.out.println(RookSquareBoardRating.BLACK_ROOK_MID_GAME_SQUARE_RATING[0]);
    }



    @Test
    void testConvertBitboardToFEN() {
        String fen = "rn2kb1r/pp2pp1p/3p2p1/2pP1b2/8/qP1P4/P1QBPPPP/R3KBNR w KQkq - 0 9"; // Custom FEN for this board setup
        newBoard.getBitboard().readFEN_String(fen);
        System.out.println("BOARD STATE READ FROM FEN");
        System.out.println(newBoard.getBitboard().convertBitboardToBinaryString());

        System.out.println("BITBOARD CONVERTED TO FEN String: " + newBoard.getBitboard().convertBitboardToFEN());

    }

}
