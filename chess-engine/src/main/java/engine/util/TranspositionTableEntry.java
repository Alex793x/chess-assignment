package engine.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TranspositionTableEntry {
    private int evaluation;
    private int depth;
    private TranspositionTableEntryType type;

    @Override
    public String toString() {
        return "TranspositionTableEntry{" +
                "evaluation=" + evaluation +
                ", depth=" + depth +
                ", type=" + type +
                '}';
    }
}

