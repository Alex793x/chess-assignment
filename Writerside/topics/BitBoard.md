# Bitboard: A Compact Representation of Chess Pieces

The `Bitboard` class in our code is a compact and efficient way to represent the state of a chess game board. 
It uses a 2D array of `BitSet` objects to store the positions of the chess pieces on the board.

## Bitboard Structure

The `Bitboard` class has the following key components:

1. `bitSets`: a 2D array of `BitSet` objects, where the first dimension represents the piece color (0 for white, 1 for black) and the second dimension represents the piece type (0 for pawn, 1 for knight, and so on).
2. `placePieceOnSquare(int square, PieceType, PieceColor)`: a method to place a piece of a specific type and color on a given square of the chessboard.
3. `removePieceFromSquare(int square, PieceType, PieceColor)`: a method to remove a piece of a specific type and color from a given square of the chessboard.
4. `isSquareOccupiedByPiece(int square, PieceType, PieceColor)`: a method to check if a specific square is occupied by a piece of a given type and color.
5. `convertBitboardToBinaryString()`: a method to convert the bitboard representation to a human-readable binary string format, where each square is represented by a 6-bit binary value (one bit for each piece type).

## How the Bitboard Works
The bitboard representation is a space-efficient way to store the positions of chess pieces on the board. Each square 
on the chessboard is represented by a single bit in the `BitSet` objects. The position of a piece is determined by 
setting the corresponding bit in the `BitSet` that represents its color and type.

For example, if a white pawn is placed on square 12 (counting from 0), the bit at index 12 in 
the `bitSets[0][0]` (`BitSet`) would be set to 1. This allows for quick lookups and manipulations of the piece positions, 
as bitwise operations can be used to perform common chess-related tasks, such as determining the possible moves for a 
piece.

The `convertBitboardToBinaryString()` method is useful for visualizing the current state of the bitboard, as it 
generates a human-readable representation of the board, with each square represented by a 6-bit binary value.

## Conclusion

The `Bitboard` class in our code is a way to create a compact and efficient way to represent the state of a chess game board. 
By using a 2D array of `BitSet` objects, it can quickly and easily manipulate the positions of chess pieces on the 
board, making it a powerful tool for implementing chess-related algorithms and applications.