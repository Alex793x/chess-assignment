package chess.engine.evaluation.piece_board_evaluation.piece_square_board_rating;

public final class RookSquareBoardRating {


    /**
     *  Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
     *  Rook midgame
     */
    public static final int[] WHITE_ROOK_MID_GAME_SQUARE_RATING = {
            -19, -13,   1,  17, 16,  7, -37, -26,
            -44, -16, -20,  -9, -1, 11,  -6, -71,
            -45, -25, -16, -17,  3,  0,  -5, -33,
            -36, -26, -12,  -1,  9, -7,   6, -23,
            -24, -11,   7,  26, 24, 35,  -8, -20,
             -5,  19,  26,  36, 17, 45,  61,  16,
             27,  32,  58,  62, 80, 67,  26,  44,
             32,  42,  32,  51, 63,  9,  31,  43,
    };

    public static final int[] BLACK_ROOK_MID_GAME_SQUARE_RATING = {
             32,  42,  32,  51, 63,  9,  31,  43,
             27,  32,  58,  62, 80, 67,  26,  44,
             -5,  19,  26,  36, 17, 45,  61,  16,
            -24, -11,   7,  26, 24, 35,  -8, -20,
            -36, -26, -12,  -1,  9, -7,   6, -23,
            -45, -25, -16, -17,  3,  0,  -5, -33,
            -44, -16, -20,  -9, -1, 11,  -6, -71,
            -19, -13,   1,  17, 16,  7, -37, -26,
    };

    /**
     *  Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
     *  Rook endgame
     */
    public static final int[] WHITE_ROOK_END_GAME_SQUARE_RATING = {
            -9,  2,  3, -1, -5, -13,   4, -20,
            -6, -6,  0,  2, -9,  -9, -11,  -3,
            -4,  0, -5, -1, -7, -12,  -8, -16,
             3,  5,  8,  4, -5,  -6,  -8, -11,
             4,  3, 13,  1,  2,   1,  -1,   2,
             7,  7,  7,  5,  4,  -3,  -5,  -3,
            11, 13, 13, 11, -3,   3,   8,   3,
            13, 10, 18, 15, 12,  12,   8,   5,
    };


    public static final int[] BLACK_ROOK_END_GAME_SQUARE_RATING = {
            13, 10, 18, 15, 12,  12,   8,   5,
            11, 13, 13, 11, -3,   3,   8,   3,
             7,  7,  7,  5,  4,  -3,  -5,  -3,
             4,  3, 13,  1,  2,   1,  -1,   2,
             3,  5,  8,  4, -5,  -6,  -8, -11,
            -4,  0, -5, -1, -7, -12,  -8, -16,
            -6, -6,  0,  2, -9,  -9, -11,  -3,
            -9,  2,  3, -1, -5, -13,   4, -20,
    };





}
