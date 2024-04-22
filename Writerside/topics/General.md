# General

Creating a chess game involves representing the board, pieces, and game state in a computationally efficient manner. 
Bitboards offer a powerful and compact approach to achieve this. This section delves into the choices Mikkel and Alex 
made for representing the chessboard using BitBoards with BitSet and Square fields through Hexadecimals.

## Background and Context
In the Artificial Intelligence (Kunstig Intelligens) course at KEA (Københavns ErhversAkademi), 
Mikkel and Alex were tasked with creating a chess game. They opted for BitBoards as the primary data structure for 
representing the chessboard due to its efficiency in handling large amounts of data and performing bitwise operations


## Understanding a little bit about BitSet, Square fields & Hexadecimal representation

### BitSet
A BitSet is a built-in Java class that efficiently manages a set of bits.
Each bit in the BitSet represents a square on the chessboard.
Setting, clearing, or checking individual bits allows for efficient manipulation of piece positions.
Square fields are a custom data structure or enumeration that represents individual squares on the chessboard.

### Square fields
Each square field stores information about the piece type occupying the square and its color 
(e.g., white pawn, black knight). Square fields are separate from the bitboard representation itself.

### Hexadecimal representation
Hexadecimal is a base-16 number system used to represent the 64-bit bitboard in a more compact and visually intuitive way.
Each pair of hexadecimal digits (0-9, A-F) corresponds to a single row on the chessboard.

The position of non-zero values within the hexadecimal string indicates the occupied squares on the board.
Hexadecimal representation helps visualize the piece positions on the board but doesn't directly represent the specific 
piece types.

Example
: In the bitboard representation, the most significant bit (leftmost bit) corresponds to the square A8 on the chessboard, and the least significant bit (rightmost bit) corresponds to the square H1
: So, the hexadecimal representation:
: 0000000000000010
: Represents the following: 
: The leftmost digit 0 indicates that squares A8 to D8 are all empty
: The second digit from the left 0 indicates that squares E8 to H8 are all empty
: The third digit from the left 0 indicates that squares A7 to D7 are all empty
: The fourth digit from the left 0 indicates that squares E7 to H7 are all empty
: The fifth digit from the left 0 indicates that squares A6 to D6 are all empty
: The sixth digit from the left 0 indicates that squares E6 to H6 are all empty
: The seventh digit from the left 0 indicates that squares A5 to D5 are all empty
: The eighth digit from the left 1 indicates that the second square from the right, which corresponds to the square D1, is occupied by a piece
: Important to remember 00 is representing 4 digits
: If A1, B1, C1, D1 where occupied then the end hexadecimal could be presented as - 00 00 00 00 00 00 00 f0
: If all rank one where occupied it would be - 00 00 00 00 00 00 00 ff (since 1 + 2 + 4 + 8 = f)

### Relationship between Square Fields and Hexadecimal: 
Hexadecimal representation provides a visual clue about occupied squares based on the positions of non-zero values.
To identify the specific piece type occupying a square, we need to access the corresponding square field.
Square fields contain information about the piece type and color, which is not directly encoded in the hexadecimal 
representation.

#### Example
Consider the hexadecimal representation 0x0000008100000000. This shows occupied squares at A8 and H1.
To determine whether these occupied squares are rooks, pawns, or other pieces, we need to access the corresponding 
square fields. Based on the piece type information in the square fields, we can then identify the specific pieces 
occupying A8 and H1.


## Glossary
A definition list or a glossary:

BitBoard
:  A data structure representing the chessboard where each bit corresponds to a square.

BitSet
: A Java class for managing sets of bits.

Square field
: A custom data structure or enumeration representing a square on the chessboard.

Hexadecimal representation
: A base-16 number system used to represent BitBoards concisely.

FEN (Forsyth-Edwards Notation)
: A standard format for representing the state of a chess game.
