package engine.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import model.Move;

@Setter
@Getter
@AllArgsConstructor
public class TranspositionTableEntry {
    private int evaluation;
    private int depth;
    private TranspositionTableEntryType type;
    private Move bestMove; // Store the best move
    private Move bestMoveFromPreviousIteration;

    @Override
    public String toString() {
        return "TranspositionTableEntry{" +
                "evaluation=" + evaluation +
                ", depth=" + depth +
                ", type=" + type +
                ", bestMove=" + bestMove +
                '}';
    }
}
