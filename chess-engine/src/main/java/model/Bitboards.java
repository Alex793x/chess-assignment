package model;

import lombok.Getter;
import lombok.Setter;
import model.enums.CurrentPlayer;
import model.enums.PieceColor;
import model.enums.PieceType;
import util.attack_vectors.AttackVectorsHandler;

import java.util.BitSet;

@Getter
@Setter
public class Bitboards {


    private BitSet whitePawns = new BitSet(64);
    private BitSet whiteKnights = new BitSet(64);
    private BitSet whiteBishops = new BitSet(64);
    private BitSet whiteRooks = new BitSet(64);
    private BitSet whiteQueens = new BitSet(64);
    private BitSet whiteKing = new BitSet(64);

    private BitSet blackPawns = new BitSet(64);
    private BitSet blackKnights = new BitSet(64);
    private BitSet blackBishops = new BitSet(64);
    private BitSet blackRooks = new BitSet(64);
    private BitSet blackQueens = new BitSet(64);
    private BitSet blackKing = new BitSet(64);

    private BitSet whiteAttackVectors = new BitSet(64);
    private BitSet blackAttackVectors = new BitSet(64);

    private BitSet whitePins = new BitSet(64);
    private BitSet blackPins = new BitSet(64);

    public Bitboards() {
        setupInitialPositions();
    }

    public void setupInitialPositions() {
        // Set initial positions for white pieces
        for (int i = 48; i <= 55; i++) whitePawns.set(i);
        whiteRooks.set(56);
        whiteRooks.set(63);
        whiteKnights.set(57);
        whiteKnights.set(62);
        whiteBishops.set(58);
        whiteBishops.set(61);
        whiteQueens.set(59);
        whiteKing.set(60);

        // Set initial positions for black pieces (on top)
        for (int i = 8; i <= 15; i++) blackPawns.set(i);
        blackRooks.set(0);
        blackRooks.set(7);
        blackKnights.set(1);
        blackKnights.set(6);
        blackBishops.set(2);
        blackBishops.set(5);
        blackQueens.set(3);
        blackKing.set(4);

        updateAllAttackVectors();
    }

    public void printNumberPosition() {
        System.out.println("""
        /**
         * 
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
         *
         * */""");
    }

    public void printNumberPositionFlipped() {
        System.out.println("""
        /**
         *       H    G    F    E    D    C    B    A
         *    +----+----+----+----+----+----+----+----+
         *  8 |  7 |  6 |  5 |  4 |  3 |  2 |  1 |  0 |  8st rank
         *    +----+----+----+----+----+----+----+----+
         *  7 | 15 | 14 | 13 | 12 | 11 | 10 |  9 |  8 |  7nd rank
         *    +----+----+----+----+----+----+----+----+
         *  6 | 23 | 22 | 21 | 20 | 19 | 18 | 17 | 16 |  6rd rank
         *    +----+----+----+----+----+----+----+----+
         *  5 | 31 | 30 | 29 | 28 | 27 | 26 | 25 | 24 |  5th rank
         *    +----+----+----+----+----+----+----+----+
         *  4 | 39 | 38 | 37 | 36 | 35 | 34 | 33 | 32 |  4th rank
         *    +----+----+----+----+----+----+----+----+
         *  3 | 47 | 46 | 45 | 44 | 43 | 42 | 41 | 40 |  3th rank
         *    +----+----+----+----+----+----+----+----+
         *  2 | 55 | 54 | 53 | 52 | 51 | 50 | 49 | 48 |  2th rank
         *    +----+----+----+----+----+----+----+----+
         *  1 | 63 | 62 | 61 | 60 | 59 | 58 | 57 | 56 |  1th rank
         *    +----+----+----+----+----+----+----+----+
         *       H    G    F    E    D    C    B    A - file(s)
         *
         * */
        """);
    }

    public void loadFEN(String fen) {
        clearBoards(); // Clear current piece positions

        String[] parts = fen.split(" ");
        String[] ranks = parts[0].split("/");

        for (int i = 0; i < ranks.length; i++) {
            int rank = 8 - i; // Ranks are from 8 to 1
            int file = 1; // Files are from 1 to 8 (a to h)
            for (char c : ranks[i].toCharArray()) {
                if (Character.isDigit(c)) {
                    file += Character.getNumericValue(c);
                } else {
                    int index = (rank - 1) * 8 + (file - 1);
                    switch (c) {
                        case 'p': blackPawns.set(index); break;
                        case 'n': blackKnights.set(index); break;
                        case 'b': blackBishops.set(index); break;
                        case 'r': blackRooks.set(index); break;
                        case 'q': blackQueens.set(index); break;
                        case 'k': blackKing.set(index); break;
                        case 'P': whitePawns.set(index); break;
                        case 'N': whiteKnights.set(index); break;
                        case 'B': whiteBishops.set(index); break;
                        case 'R': whiteRooks.set(index); break;
                        case 'Q': whiteQueens.set(index); break;
                        case 'K': whiteKing.set(index); break;
                    }
                    file++;
                }
            }
        }
    }

    public void loadFENFlipped(String fen) {
        clearBoards(); // Clear current piece positions

        String[] parts = fen.split(" ");
        String[] ranks = parts[0].split("/");

        for (int i = 0; i < ranks.length; i++) {
            int rank = i + 1; // Flip the rank to start from 1 to 8 instead of 8 to 1
            int file = 1; // Files are from 1 to 8 (a to h)
            for (char c : ranks[i].toCharArray()) {
                if (Character.isDigit(c)) {
                    file += Character.getNumericValue(c); // Skip the number of empty squares
                } else {
                    int index = (rank - 1) * 8 + (8 - file); // Mirrors the file (flips a-h to h-a)
                    switch (c) {
                        case 'p': blackPawns.set(index); break;
                        case 'n': blackKnights.set(index); break;
                        case 'b': blackBishops.set(index); break;
                        case 'r': blackRooks.set(index); break;
                        case 'q': blackQueens.set(index); break;
                        case 'k': blackKing.set(index); break;
                        case 'P': whitePawns.set(index); break;
                        case 'N': whiteKnights.set(index); break;
                        case 'B': whiteBishops.set(index); break;
                        case 'R': whiteRooks.set(index); break;
                        case 'Q': whiteQueens.set(index); break;
                        case 'K': whiteKing.set(index); break;
                    }
                    file++;
                }
            }
        }
    }


    public void clearBoards() {
        whitePawns.clear();
        whiteKnights.clear();
        whiteBishops.clear();
        whiteRooks.clear();
        whiteQueens.clear();
        whiteKing.clear();

        blackPawns.clear();
        blackKnights.clear();
        blackBishops.clear();
        blackRooks.clear();
        blackQueens.clear();
        blackKing.clear();
    }

    public void printBoard() {
        System.out.println("       A    B    C    D    E    F    G    H");
        System.out.println("    +----+----+----+----+----+----+----+----+");
        for (int row = 8; row >= 1; row--) {
            int start = (row - 1) * 8;
            System.out.print("  " + row + " |");
            for (int col = 0; col < 8; col++) {
                int index = start + col;
                char piece = getPieceChar(index);
                System.out.print("  " + piece + " |");
            }
            System.out.println("  " + row + "th rank");
            System.out.println("    +----+----+----+----+----+----+----+----+");
        }
        System.out.println("       A    B    C    D    E    F    G    H");
    }

    public void printBoardFlipped() {
        System.out.println("       H    G    F    E    D    C    B    A");
        System.out.println("    +----+----+----+----+----+----+----+----+");
        for (int row = 8; row >= 1; row--) {
            int start = (8 - row) * 8; // Adjusting start index for flipped board
            System.out.print("  " + row + " |");
            for (int col = 7; col >= 0; col--) {
                int index = start + col;
                char piece = getPieceChar(index);
                System.out.print("  " + piece + " |");
            }
            System.out.println("  " + row + "th rank");
            System.out.println("    +----+----+----+----+----+----+----+----+");
        }
        System.out.println("       H    G    F    E    D    C    B    A");
    }


    private char getPieceChar(int index) {
        if (whitePawns.get(index))
            return 'P';
        if (whiteKnights.get(index))
            return 'N';
        if (whiteBishops.get(index))
            return 'B';
        if (whiteRooks.get(index))
            return 'R';
        if (whiteQueens.get(index))
            return 'Q';
        if (whiteKing.get(index))
            return 'K';
        if (blackPawns.get(index))
            return 'p';
        if (blackKnights.get(index))
            return 'n';
        if (blackBishops.get(index))
            return 'b';
        if (blackRooks.get(index))
            return 'r';
        if (blackQueens.get(index))
            return 'q';
        if (blackKing.get(index))
            return 'k';
        return ' '; // empty space
    }

    // Get all pieces
    public BitSet getAllPieces() {
        BitSet allPieces = new BitSet(64);
        allPieces.or(getWhitePieces());
        allPieces.or(getBlackPieces());
        return allPieces;
    }

    // Get pieces by CurrentPlayer
    public BitSet getAllPiecesByColor(CurrentPlayer currentPlayer) {
        return currentPlayer.equals(CurrentPlayer.WHITE)
                ? getWhitePieces()
                : getBlackPieces();
    }

    // Get black pieces
    private BitSet getBlackPieces() {
        BitSet blackPieces = new BitSet(64);
        blackPieces.or(blackPawns);
        blackPieces.or(blackKnights);
        blackPieces.or(blackBishops);
        blackPieces.or(blackRooks);
        blackPieces.or(blackQueens);
        blackPieces.or(blackKing);
        return blackPieces;
    }

    // Get white pieces
    private BitSet getWhitePieces() {
        BitSet whitePieces = new BitSet(64);
        whitePieces.or(whitePawns);
        whitePieces.or(whiteKnights);
        whitePieces.or(whiteBishops);
        whitePieces.or(whiteRooks);
        whitePieces.or(whiteQueens);
        whitePieces.or(whiteKing);
        return whitePieces;
    }

    // Get pieces by PieceType
    public BitSet getPieceBitboard(PieceType pieceType, PieceColor pieceColor) {
        return PieceColor.WHITE == pieceColor
                ? getWhitePieceBitboard(pieceType)
                : getBlackPieceBitboard(pieceType);
    }

    private BitSet getWhitePieceBitboard(PieceType pieceType) {
        return switch (pieceType) {
            case PAWN -> whitePawns;
            case KNIGHT -> whiteKnights;
            case BISHOP -> whiteBishops;
            case ROOK -> whiteRooks;
            case QUEEN -> whiteQueens;
            case KING -> whiteKing;
        };
    }

    private BitSet getBlackPieceBitboard(PieceType pieceType) {
        return switch (pieceType) {
            case PAWN -> blackPawns;
            case KNIGHT -> blackKnights;
            case BISHOP -> blackBishops;
            case ROOK -> blackRooks;
            case QUEEN -> blackQueens;
            case KING -> blackKing;
        };
    }

    public BitSet getOccupiedSquares() {
        BitSet occupiedSquares = new BitSet(64);
        occupiedSquares.or(whitePawns);
        occupiedSquares.or(whiteKnights);
        occupiedSquares.or(whiteBishops);
        occupiedSquares.or(whiteRooks);
        occupiedSquares.or(whiteQueens);
        occupiedSquares.or(whiteKing);
        occupiedSquares.or(blackPawns);
        occupiedSquares.or(blackKnights);
        occupiedSquares.or(blackBishops);
        occupiedSquares.or(blackRooks);
        occupiedSquares.or(blackQueens);
        occupiedSquares.or(blackKing);
        return occupiedSquares;
    }


    public Bitboards deepCopy() {
        Bitboards copy = new Bitboards();
        copy.whitePawns = (BitSet) this.whitePawns.clone();
        copy.whiteKnights = (BitSet) this.whiteKnights.clone();
        copy.whiteBishops = (BitSet) this.whiteBishops.clone();
        copy.whiteRooks = (BitSet) this.whiteRooks.clone();
        copy.whiteQueens = (BitSet) this.whiteQueens.clone();
        copy.whiteKing = (BitSet) this.whiteKing.clone();
        copy.blackPawns = (BitSet) this.blackPawns.clone();
        copy.blackKnights = (BitSet) this.blackKnights.clone();
        copy.blackBishops = (BitSet) this.blackBishops.clone();
        copy.blackRooks = (BitSet) this.blackRooks.clone();
        copy.blackQueens = (BitSet) this.blackQueens.clone();
        copy.blackKing = (BitSet) this.blackKing.clone();
        copy.whiteAttackVectors = (BitSet) this.whiteAttackVectors.clone();
        copy.blackAttackVectors = (BitSet) this.blackAttackVectors.clone();
        copy.whitePins = (BitSet) this.whitePins.clone();
        copy.blackPins = (BitSet) this.blackPins.clone();
        return copy;
    }



    // Get Piece class by square
    public Piece getPieceBySquare(int square) {
        if (whitePawns.get(square))
            return new Piece(PieceType.PAWN, PieceColor.WHITE, square);
        if (whiteKnights.get(square))
            return new Piece(PieceType.KNIGHT, PieceColor.WHITE, square);
        if (whiteBishops.get(square))
            return new Piece(PieceType.BISHOP, PieceColor.WHITE, square);
        if (whiteRooks.get(square))
            return new Piece(PieceType.ROOK, PieceColor.WHITE, square);
        if (whiteQueens.get(square))
            return new Piece(PieceType.QUEEN, PieceColor.WHITE, square);
        if (whiteKing.get(square))
            return new Piece(PieceType.KING, PieceColor.WHITE, square);

        if (blackPawns.get(square))
            return new Piece(PieceType.PAWN, PieceColor.BLACK, square);
        if (blackKnights.get(square))
            return new Piece(PieceType.KNIGHT, PieceColor.BLACK, square);
        if (blackBishops.get(square))
            return new Piece(PieceType.BISHOP, PieceColor.BLACK, square);
        if (blackRooks.get(square))
            return new Piece(PieceType.ROOK, PieceColor.BLACK, square);
        if (blackQueens.get(square))
            return new Piece(PieceType.QUEEN, PieceColor.BLACK, square);
        if (blackKing.get(square))
            return new Piece(PieceType.KING, PieceColor.BLACK, square);

        return null;
    }

    public synchronized void placePiece(Piece piece, int position) {
        BitSet bitset = getPieceBitboard(piece.getPieceType(), piece.getPieceColor());
        bitset.set(position);
        updateAttackVectorsForColor(piece.getPieceColor());
    }

    public synchronized void clearPiece(PieceType type, PieceColor color, int position) {
        BitSet bitset = getPieceBitboard(type, color);
        bitset.clear(position);
        updateAttackVectorsForColor(color);
    }

    // Get King for player
    public BitSet getKingBitboard(CurrentPlayer currentPlayer) {
        return currentPlayer.equals(CurrentPlayer.WHITE) ? whiteKing : blackKing;
    }

    public boolean isKingExposed(CurrentPlayer currentPlayer) {
        BitSet kingPosition = getKingBitboard(currentPlayer);
        BitSet attackVectors = currentPlayer.equals(CurrentPlayer.WHITE)
                ? getBlackAttackVectors()
                : getWhiteAttackVectors();

        return kingPosition.intersects(attackVectors);
    }

    public synchronized BitSet getAttackVectorsByColor(PieceColor color) {
        return color == PieceColor.WHITE ? whiteAttackVectors : blackAttackVectors;
    }

    public synchronized boolean isSquareAttacked(int square, CurrentPlayer currentPlayer) {
        if (square < 0 || square >= 64) {
            throw new IndexOutOfBoundsException("Invalid square index: " + square);
        }

        PieceColor attackingColor = currentPlayer.getOppositePlayer().equals(CurrentPlayer.WHITE) ? PieceColor.WHITE
                : PieceColor.BLACK;
        BitSet attackVectors = getAttackVectorsByColor(attackingColor);
        return attackVectors.get(square);
    }

    public void updateAttackVectorsAfterMove(int from, int to, Piece piece) {
        BitSet allPieces = getAllPieces();
        // Clear attack vectors from the old position
        BitSet attacksFromOldPos = AttackVectorsHandler.calculateAttackVectorsForPiece(piece.getPieceType(), from,
                piece.getPieceColor(), allPieces);
        getAttackVectorsByColor(piece.getPieceColor()).andNot(attacksFromOldPos);

        // Set attack vectors from the new position
        BitSet attacksFromNewPos = AttackVectorsHandler.calculateAttackVectorsForPiece(piece.getPieceType(), to,
                piece.getPieceColor(), allPieces);
        getAttackVectorsByColor(piece.getPieceColor()).or(attacksFromNewPos);

        detectPins(piece.getPieceColor() == PieceColor.WHITE ? CurrentPlayer.WHITE : CurrentPlayer.BLACK);
    }

    public void updateAllAttackVectors() {
        whiteAttackVectors.clear();
        blackAttackVectors.clear();
        updateAttackVectorsForColor(PieceColor.WHITE);
        updateAttackVectorsForColor(PieceColor.BLACK);
    }

    private void updateAttackVectorsForColor(PieceColor color) {
        BitSet attackVectors = color == PieceColor.WHITE ? whiteAttackVectors : blackAttackVectors;
        for (PieceType pieceType : PieceType.values()) {
            BitSet pieces = getPieceBitboard(pieceType, color);
            for (int i = pieces.nextSetBit(0); i >= 0; i = pieces.nextSetBit(i + 1)) {
                BitSet allPieces = getAllPieces();
                BitSet attacks = AttackVectorsHandler.calculateAttackVectorsForPiece(pieceType, i, color, allPieces);
                attackVectors.or(attacks);
            }
        }
    }

    public void detectPins(CurrentPlayer player) {
        PieceColor oppositePieceColor = player.getOppositePlayer().equals(CurrentPlayer.WHITE) ? PieceColor.WHITE
                : PieceColor.BLACK;
        BitSet kings = getKingBitboard(player);
        int kingPosition = kings.nextSetBit(0);

        BitSet[] pinCandidates = {
                getPieceBitboard(PieceType.ROOK, oppositePieceColor),
                getPieceBitboard(PieceType.BISHOP, oppositePieceColor),
                getPieceBitboard(PieceType.QUEEN, oppositePieceColor)
        };

        BitSet potentialPins = new BitSet(64); // Reset potential pins

        for (BitSet candidates : pinCandidates) {
            for (int candidatePos = candidates.nextSetBit(0); candidatePos >= 0; candidatePos = candidates
                    .nextSetBit(candidatePos + 1)) {
                if (isInLineOfSight(candidatePos, kingPosition)) {
                    BitSet between = getBetweenSquares(candidatePos, kingPosition);
                    if (between.cardinality() == 1 && between.intersects(getAllPiecesByColor(player))) {
                        potentialPins.or(between);
                    }
                }
            }
        }

        if (player == CurrentPlayer.WHITE) {
            whitePins = potentialPins;
        } else {
            blackPins = potentialPins;
        }
    }

    private boolean isInLineOfSight(int pos1, int pos2) {
        int row1 = pos1 / 8, col1 = pos1 % 8;
        int row2 = pos2 / 8, col2 = pos2 % 8;

        boolean sameRow = row1 == row2;
        boolean sameColumn = col1 == col2;
        boolean sameDiagonal = Math.abs(row1 - row2) == Math.abs(col1 - col2);

        return sameRow || sameColumn || sameDiagonal;
    }

    private BitSet getBetweenSquares(int from, int to) {
        BitSet between = new BitSet(64);
        int rowFrom = from / 8, colFrom = from % 8;
        int rowTo = to / 8, colTo = to % 8;

        int rowStep = Integer.compare(rowTo, rowFrom); // Determine step direction for rows (-1, 0, 1)
        int colStep = Integer.compare(colTo, colFrom); // Determine step direction for columns (-1, 0, 1)

        int currentRow = rowFrom + rowStep;
        int currentCol = colFrom + colStep;

        while (currentRow != rowTo || currentCol != colTo) {
            // Check if the current index is within valid bounds
            if (currentRow >= 0 && currentRow < 8 && currentCol >= 0 && currentCol < 8) {
                int currentIndex = currentRow * 8 + currentCol;
                between.set(currentIndex);
            } else {
                // Break out of the loop if the calculated index is out of bounds
                break;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        return between;
    }


    public boolean isPathClear(int from, int to) {
        BitSet allPieces = getAllPieces(); // Retrieve a bit set of all pieces on the board
        BitSet betweenSquares = getBetweenSquares(from, to); // Calculate the squares between the two positions

        // Perform a logical AND between all pieces and the squares between 'from' and
        // 'to'.
        // If there is no overlap (i.e., no pieces are on the squares between 'from' and
        // 'to'), then the path is clear.
        betweenSquares.and(allPieces);
        return betweenSquares.isEmpty();
    }


    public Piece findLowestValueAttacker(int square, CurrentPlayer currentPlayer, boolean isMidgamePhase) {
        PieceColor attackColor = currentPlayer.getOppositePlayer().equals(CurrentPlayer.WHITE) ? PieceColor.WHITE : PieceColor.BLACK;
        int lowestValue = Integer.MAX_VALUE;
        Piece lowestValueAttacker = null;

        // Check all piece types from the attacking side
        for (PieceType pieceType : PieceType.values()) {
            BitSet attackers = (BitSet) getPieceBitboard(pieceType, attackColor).clone();  // Clone the bitset to avoid modifying the original
            BitSet attackVectors = AttackVectorsHandler.calculateAttackVectorsForPiece(pieceType, square, attackColor, getAllPieces());

            // Check if any attackers of this type can attack the square
            attackers.and(attackVectors);  // This now operates on the cloned bitset
            if (!attackers.isEmpty()) {
                int attackerSquare = attackers.nextSetBit(0);
                int pieceValue = isMidgamePhase ? pieceType.getMidGameValue() : pieceType.getEndGameValue();
                if (pieceValue < lowestValue) {
                    lowestValue = pieceValue;
                    lowestValueAttacker = new Piece(pieceType, attackColor, attackerSquare);
                }
            }
        }

        return lowestValueAttacker;
    }








    public int[] calculateProtectionLevels() {
        int[] protectionLevels = new int[64]; // Array to store net protection level for each square
        BitSet allPieces = getAllPieces();

        // Calculate net protection/attack counts for each piece
        for (int i = 0; i < 64; i++) {
            if (allPieces.get(i)) {
                Piece piece = getPieceBySquare(i);
                BitSet attacks = AttackVectorsHandler.calculateAttackVectorsForPiece(piece.getPieceType(), i,
                        piece.getPieceColor(), allPieces);
                BitSet defenses = getAttackVectorsByColor(piece.getPieceColor());

                int attacksCount = attacks.cardinality();
                int defensesCount = defenses.cardinality();

                protectionLevels[i] = defensesCount - attacksCount; // Net protection
            }
        }
        return protectionLevels;
    }

    public int calculateMobility(PieceColor color) {
        int mobility = 0;
        BitSet pieces = (color == PieceColor.WHITE) ? getWhitePieces() : getBlackPieces();
        BitSet opponentPieces = (color == PieceColor.WHITE) ? getBlackPieces() : getWhitePieces();

        for (int i = pieces.nextSetBit(0); i >= 0; i = pieces.nextSetBit(i + 1)) {
            Piece piece = getPieceBySquare(i);
            BitSet moves = AttackVectorsHandler.calculateMovesForPiece(piece.getPieceType(), i, color, getAllPieces(),
                    opponentPieces);
            mobility += moves.cardinality(); // Count of valid moves
        }

        return mobility;
    }




    public int[] calculateKingSafetyLevels() {
        int[] kingSafetyLevels = new int[2]; // 0 for white, 1 for black

        // Calculate safety levels for each king
        kingSafetyLevels[PieceColor.WHITE.ordinal()] = calculateSafetyForKing(PieceColor.WHITE);
        kingSafetyLevels[PieceColor.BLACK.ordinal()] = calculateSafetyForKing(PieceColor.BLACK);

        return kingSafetyLevels;
    }

    // Helper method to calculate safety for a king of a given color
    private int calculateSafetyForKing(PieceColor color) {
        int kingIndex = (color == PieceColor.WHITE) ? whiteKing.nextSetBit(0) : blackKing.nextSetBit(0);
        BitSet kingSurroundingSquares = getSurroundingSquares(kingIndex);
        BitSet opponentAttackVectors = (color == PieceColor.WHITE) ? blackAttackVectors : whiteAttackVectors;

        int attackersCount = 0;
        for (int i = kingSurroundingSquares.nextSetBit(0); i >= 0; i = kingSurroundingSquares.nextSetBit(i + 1)) {
            if (opponentAttackVectors.get(i)) {
                attackersCount++;
            }
        }

        // Calculate the safety score based on the number of attackers
        return evaluateKingSafety(attackersCount);
    }

    // Helper method to get the squares surrounding the king
    private BitSet getSurroundingSquares(int kingIndex) {
        BitSet surroundingSquares = new BitSet(64);

        // Directions around the king
        int[] directions = {-9, -8, -7, -1, 1, 7, 8, 9};

        for (int dir : directions) {
            int neighborIndex = kingIndex + dir;
            if (neighborIndex >= 0 && neighborIndex < 64) {
                surroundingSquares.set(neighborIndex);
            }
        }

        return surroundingSquares;
    }

    // Helper method to evaluate king safety based on the number of attackers
    private int evaluateKingSafety(int attackersCount) {
        // Safety evaluation table (example values, adjust based on testing and needs)
        int[] safetyTable = {0, 1, 3, 5, 10, 15, 22, 30, 39, 50};

        // Return a capped value if attackersCount exceeds the table size
        return (attackersCount < safetyTable.length) ? safetyTable[attackersCount] : safetyTable[safetyTable.length - 1];
    }


}
