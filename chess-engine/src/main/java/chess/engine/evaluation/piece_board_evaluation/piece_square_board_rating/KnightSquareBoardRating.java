package chess.engine.evaluation.piece_board_evaluation.piece_square_board_rating;

public final class KnightSquareBoardRating {


    /**
     * Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
     * //knight midgame
     */
    public static final int[] WHITE_KNIGHT_MID_GAME_SQUARE_RATING = {
            -105, -21, -58, -33, -17, -28, -19,  -23,
             -29, -53, -12,  -3,  -1,  18, -14,  -19,
             -23,  -9,  12,  10,  19,  17,  25,  -16,
             -13,   4,  16,  13,  28,  19,  21,   -8,
              -9,  17,  19,  53,  37,  69,  18,   22,
             -47,  60,  37,  65,  84, 129,  73,   44,
             -73, -41,  72,  36,  23,  62,   7,  -17,
            -167, -89, -34, -49,  61, -97, -15, -107,
    };


    public static final int[] BLACK_KNIGHT_MID_GAME_SQUARE_RATING = {
            -167, -89, -34, -49,  61, -97, -15, -107,
             -73, -41,  72,  36,  23,  62,   7,  -17,
             -47,  60,  37,  65,  84, 129,  73,   44,
              -9,  17,  19,  53,  37,  69,  18,   22,
             -13,   4,  16,  13,  28,  19,  21,   -8,
             -23,  -9,  12,  10,  19,  17,  25,  -16,
             -29, -53, -12,  -3,  -1,  18, -14,  -19,
            -105, -21, -58, -33, -17, -28, -19,  -23,
    };

    /**
     *  Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
     * //knight endgame
     */
    public static final int[] WHITE_KNIGHT_END_GAME_SQUARE_RATING = {
            -29, -51, -23, -15, -22, -18, -50, -64,
            -42, -20, -10,  -5,  -2, -20, -23, -44,
            -23,  -3,  -1,  15,  10,  -3, -20, -22,
            -18,  -6,  16,  25,  16,  17,   4, -18,
            -17,   3,  22,  22,  22,  11,   8, -18,
            -24, -20,  10,   9,  -1,  -9, -19, -41,
            -25,  -8, -25,  -2,  -9, -25, -24, -52,
            -58, -38, -13, -28, -31, -27, -63, -99,
    };


    public static final int[] BLACK_KNIGHT_END_GAME_SQUARE_RATING = {
            -58, -38, -13, -28, -31, -27, -63, -99,
            -25,  -8, -25,  -2,  -9, -25, -24, -52,
            -24, -20,  10,   9,  -1,  -9, -19, -41,
            -17,   3,  22,  22,  22,  11,   8, -18,
            -18,  -6,  16,  25,  16,  17,   4, -18,
            -23,  -3,  -1,  15,  10,  -3, -20, -22,
            -42, -20, -10,  -5,  -2, -20, -23, -44,
            -29, -51, -23, -15, -22, -18, -50, -64,
    };







}
