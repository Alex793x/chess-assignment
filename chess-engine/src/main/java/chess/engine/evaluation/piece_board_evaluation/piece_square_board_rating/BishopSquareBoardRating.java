package chess.engine.evaluation.piece_board_evaluation.piece_square_board_rating;

public final class BishopSquareBoardRating {

    /**
     *  Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
     *  Bishop midgame
     */

    public static final int[] WHITE_BISHOP_MID_GAME_SQUARE_RATING = {
            -33,  -3, -14, -21, -13, -12, -39, -21,
              4,  15,  16,   0,   7,  21,  33,   1,
              0,  15,  15,  15,  14,  27,  18,  10,
             -6,  13,  13,  26,  34,  12,  10,   4,
             -4,   5,  19,  50,  37,  37,   7,  -2,
            -16,  37,  43,  40,  35,  50,  37,  -2,
            -26,  16, -18, -13,  30,  59,  18, -47,
            -29,   4, -82, -37, -25, -42,   7,  -8,
    };


    public static final int[] BLACK_BISHOP_MID_GAME_SQUARE_RATING = {
            -29,   4, -82, -37, -25, -42,   7,  -8,
            -26,  16, -18, -13,  30,  59,  18, -47,
            -16,  37,  43,  40,  35,  50,  37,  -2,
             -4,   5,  19,  50,  37,  37,   7,  -2,
             -6,  13,  13,  26,  34,  12,  10,   4,
              0,  15,  15,  15,  14,  27,  18,  10,
              4,  15,  16,   0,   7,  21,  33,   1,
            -33,  -3, -14, -21, -13, -12, -39, -21,
    };


    /**
     *  Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
     *  Bishop endgame
     */
    public static final int[] WHITE_BISHOP_END_GAME_SQUARE_RATING = {
            -23,  -9, -23,  -5, -9, -16,  -5, -17,
            -14, -18,  -7,  -1,  4,  -9, -15, -27,
            -12,  -3,   8,  10, 13,   3,  -7, -15,
             -6,   3,  13,  19,  7,  10,  -3,  -9,
             -3,   9,  12,   9, 14,  10,   3,   2,
              2,  -8,   0,  -1, -2,   6,   0,   4,
             -8,  -4,   7, -12, -3, -13,  -4, -14,
            -14, -21, -11,  -8, -7,  -9, -17, -24,
    };


    public static final int[] BLACK_BISHOP_END_GAME_SQUARE_RATING = {
            -14, -21, -11,  -8, -7,  -9, -17, -24,
             -8,  -4,   7, -12, -3, -13,  -4, -14,
              2,  -8,   0,  -1, -2,   6,   0,   4,
             -3,   9,  12,   9, 14,  10,   3,   2,
             -6,   3,  13,  19,  7,  10,  -3,  -9,
            -12,  -3,   8,  10, 13,   3,  -7, -15,
            -14, -18,  -7,  -1,  4,  -9, -15, -27,
            -23,  -9, -23,  -5, -9, -16,  -5, -17,
    };






}
