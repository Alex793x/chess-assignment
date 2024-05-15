package util.precompuation;

import java.util.BitSet;

@FunctionalInterface
public interface SlidingMoveGenerator {
    BitSet generate(int square, BitSet occupied);
}