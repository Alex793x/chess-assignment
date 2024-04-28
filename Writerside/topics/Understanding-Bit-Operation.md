# Understanding &amp;~ Bit Operation
Understanding the Bitboard Clearing Operation: `bitboard &= ~SQUARE_MASKS[square]`

The expression `bitboard &= ~SQUARE_MASKS[square]` is used to clear a piece from a specific square on the bitboard. 
Let's break down how this operation works:

1. **SQUARE_MASKS Array**:
    - The `SQUARE_MASKS` array is a precomputed array containing the bit masks for each square on the chessboard.
    - Each bit mask has a single bit set to 1 at the position corresponding to the square index.
    - For example, `SQUARE_MASKS[0]` represents the bit mask for square 0 (a1) and has the value 
      `0000000000000000000000000000000000000000000000000000000000000001`.
    - `SQUARE_MASKS[1]` represents the bit mask for square 1 (b1) and has the value 
      `0000000000000000000000000000000000000000000000000000000000000010`.

2. **Bitwise Complement (`~`)**:
    - The `~` operator is the bitwise complement operator, which inverts all the bits in the bit mask.
    - So, `~SQUARE_MASKS[square]` will have all bits set to 1 except for the bit corresponding to the specified square, 
      which will be set to 0.

3. **Bitwise AND (`&=`)**:
    - The `&=` operator is the bitwise AND assignment operator, which performs a bitwise AND operation between the 
      bitboard and the complement of the bit mask (`~SQUARE_MASKS[square]`), and assigns the result back to the bitboard.
    - The bitwise AND operation compares each bit of the bitboard with the corresponding bit of the complement of the 
      bit mask. The resulting bit is 1 only if both bits are 1; otherwise, it is 0.
    - This operation effectively clears the bit corresponding to the specified square on the bitboard, 
      while preserving all other bits.

In summary, the expression `bitboard &= ~SQUARE_MASKS[square]` clears the piece from the specified square on the 
bitboard by setting the corresponding bit to 0 while preserving all other bits. This bitwise operation is efficient 
because it directly manipulates the bits of the bitboard using basic bitwise operations, avoiding the need for additional
method calls or object creations.