package engine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import model.Move;

@Getter
@Setter
@AllArgsConstructor
public class MoveEvaluationResult {

    private int evaluation;
    private Move bestMove;

    @Override
    public String toString() {
        return "MinMaxResult{" +
                "evaluation=" + evaluation +
                ", bestMove=" + bestMove +
                '}';
    }
}