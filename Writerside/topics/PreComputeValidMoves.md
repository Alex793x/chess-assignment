# PreComputeValidMoves

To optimize the AlphaBeta Pruning and Depth, we have taken advantage of PreComputing
certain valid moves, and represent them through as long 64 bit, so we can perform
bitwise operations, to faster validate, if for instances some moves are valid
or any good at all, this also allows us to not iterate through certain move checks.

## PreComputing Squares Example


### Pawn pre-computation

```java
    private static void calculatePawnAttacks(PieceColor playerColor, int[] attackOffsets, long[] attacksArray) {
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            for (int offset : attackOffsets) {
                int destSquare = square + offset;
                if ((playerColor == PieceColor.WHITE && destSquare >= 0 && destSquare < 56) ||
                        (playerColor == PieceColor.BLACK && destSquare >= 8 && destSquare < 64)) {
                    attacks |= (1L << destSquare);
                }
            }
            attacksArray[square] = attacks;
        }
    }
```

### Sliding pieces pre-computation
For the sliding pieces (Rooks/Bishops/Queens) we can do something like this
```java
    private static void slidingPieceAttackComputation(int[] pieceOffsets, long[] attacksArray) {
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            for (int offset : pieceOffsets) {
                int destSquare = square;
                while (true) {
                    destSquare += offset;
                    if (!PieceValidator.isWithinBoardBounds(destSquare) || Math.abs(destSquare % 8 - square % 8) > 1) {
                        break;
                    }
                    attacks |= (1L << destSquare);
                }
            }
            attacksArray[square] = attacks;
        }
    }
```

### King pre-computation
For the King pieces we can do something like this

```java
    private static void calculateKingAttacks() {
        // Pre-compute the attacks for each square
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            for (int offset : KingValidator.KING_OFFSETS) {
                int destSquare = square + offset;

                // Stop iterating when one square away in any direction
                if (Math.abs(destSquare / 8 - square / 8) > 1 ||
                        Math.abs(destSquare % 8 - square % 8) > 1) {
                    break;
                }

                // Mark attackable square within bounds
                if (PieceValidator.isWithinBoardBounds(destSquare)) {
                    attacks |= (1L << destSquare);
                }
            }
            PreComputationHandler.KING_ATTACKS[square] = attacks;
        }
    }
```

### Knight pre-computation
For the Knight, we have done following PreComputation:
```java
    private static void calculateKnightAttacks() {
        // Pre-compute the attacks for each square
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            for (int offset : KnightValidator.KNIGHT_OFFSETS) {
                int destSquare = square + offset;

                // Check if the destination square is within bounds and the move does not wrap around the board edges
                int absoluteBoundary = Math.abs((destSquare % 8) - (square % 8));
                if (PieceValidator.isWithinBoardBounds(destSquare) &&
                        absoluteBoundary != 0 && // Ensure it does not wrap around
                        Math.abs((destSquare / 8) - (square / 8)) <= 2 && // Within 2 rows
                        absoluteBoundary <= 2) { // Within 2 columns
                    attacks |= (1L << destSquare);
                }
            }
            PreComputationHandler.KNIGHT_ATTACKS[square] = attacks;
        }
    }
```

This Pre-Computes all possible valid KNIGHT_MOVES or every square, represented through a long(64),
Then we simply give the square we want to validate certain available moves from
```java         
long knightAttacks = Bitboard.KNIGHT_ATTACKS[from];
```

This pre-computed work, allows us to check the destination square 'to' is set in the knight attack bitboard.
The way to go through this, is by creating a bitmask, with a single bit set at 'to' position (1L << to)
```java
    public static boolean isValidKnightMove(Board board, int from, int to, PieceColor playerColor) {
        if (!isKnightOnSquare(board, from, playerColor)) return false;
        long knightAttacks = Bitboard.KNIGHT_ATTACKS[from];
        return (knightAttacks & (1L << to)) != 0 &&
                (board.getPieceColorAtSquare(to) == null || board.getPieceColorAtSquare(to) != playerColor);
    } 
```

In more details whats happening under the hood is as follows - example Knight Calculation
: Bitboard.KNIGHT_ATTACKS[from] is the pre-computed bitboard that represents all the valid knight moves 
from the 'from' square. 
: This bitboard has bits set at the positions that the knight can move to.
: (1L << to) is a bitmask that has a single bit set at the to position. This represents the destination square for the knight's move.
: (knightAttacks & (1L << to)) performs a bitwise AND operation between the knightAttacks bitboard and the bitmask. 
: The result will be non-zero (i.e., != 0) only if the bit at the to position is set in both the knightAttacks bitboard and the bitmask. 
: If the bit at the to position is set in both bitboards, the result of the bitwise AND will be 1 (true), because 1 & 1 = 1. 
: If the bit at the to position is not set in either bitboard, the result of the bitwise AND will be 0 (false), because 0 & 0 = 0 and 0 & 1 = 0. 
: The final check (knightAttacks & (1L << to)) != 0 evaluates to true if the destination square to is set in the knightAttacks 
: bitboard, which means the knight can move to that square. 
: So, the bitwise AND operation effectively checks if the destination square is a valid move for the knight, by 
: comparing the pre-computed knight attack bitboard with a bitmask representing the destination square. 
: This approach is extremely efficient, as it avoids the need for complex calculations or loops, and instead relies on fast 
: bitwise operations to determine the validity of the knight's move.