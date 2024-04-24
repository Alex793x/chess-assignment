package org.kea.chessbackend.chess_test_engine.engine.evaluation.piece_board_evaluation.piece_square_board_rating;


public final class PawnSquareBoardRating {

    /**
     *  Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
     *  pawn midgame
     */
    public static final int[] WHITE_PAWN_MID_GAME_SQUARE_RATING = {
              0,   0,   0,   0,   0,   0,  0,   0,
            -35,  -1, -20, -23, -15,  24, 38, -22,
            -26,  -4,  -4, -10,   3,   3, 33, -12,
            -27,  -2,  -5,  12,  17,   6, 10, -25,
            -14,  13,   6,  21,  23,  12, 17, -23,
             -6,   7,  26,  31,  65,  56, 25, -20,
             98, 134,  61,  95,  68, 126, 34, -11,
              0,   0,   0,   0,   0,   0,  0,   0,
    };

    public static final int[] BLACK_PAWN_MID_GAME_SQUARE_RATING = {
              0,   0,   0,   0,   0,   0,  0,   0,
             98, 134,  61,  95,  68, 126, 34, -11,
             -6,   7,  26,  31,  65,  56, 25, -20,
            -14,  13,   6,  21,  23,  12, 17, -23,
            -27,  -2,  -5,  12,  17,   6, 10, -25,
            -26,  -4,  -4, -10,   3,   3, 33, -12,
            -35,  -1, -20, -23, -15,  24, 38, -22,
              0,   0,   0,   0,   0,   0,  0,   0,
    };


    /**
     *  Source: <a href="https://www.talkchess.com/forum3/viewtopic.php?f=2&t=68311&start=19">talkchess</a>
     *  pawn endgame
     */
    public static final int[] WHITE_PAWN_END_GAME_SQUARE_RATING = {
              0,   0,   0,   0,   0,   0,   0,   0,
             13,   8,   8,  10,  13,   0,   2,  -7,
              4,   7,  -6,   1,   0,  -5,  -1,  -8,
             13,   9,  -3,  -7,  -7,  -8,   3,  -1,
             32,  24,  13,   5,  -2,   4,  17,  17,
             94, 100,  85,  67,  56,  53,  82,  84,
            178, 173, 158, 134, 147, 132, 165, 187,
              0,   0,   0,   0,   0,   0,   0,   0,
    };


    public static final int[] BLACK_PAWN_END_GAME_SQUARE_RATING = {
              0,   0,   0,   0,   0,   0,   0,   0,
            178, 173, 158, 134, 147, 132, 165, 187,
             94, 100,  85,  67,  56,  53,  82,  84,
             32,  24,  13,   5,  -2,   4,  17,  17,
             13,   9,  -3,  -7,  -7,  -8,   3,  -1,
              4,   7,  -6,   1,   0,  -5,  -1,  -8,
             13,   8,   8,  10,  13,   0,   2,  -7,
              0,   0,   0,   0,   0,   0,   0,   0,
    };




}
