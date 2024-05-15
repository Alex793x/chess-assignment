package engine.move_generation.model;


import lombok.Getter;
import lombok.Setter;
import model.Move;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Getter
@Setter
public class MoveResult {
    
    private PriorityQueue<Move> validMoves;
    private PriorityQueue<Move> validCaptures;
    private PriorityQueue<Move> checkMoves;
    private PriorityQueue<Move> castlingMoves;
    private PriorityQueue<Move> promotionMoves;

    public MoveResult(Comparator<Move> moveComparator) {
        this.validMoves = new PriorityQueue<>(moveComparator);
        this.validCaptures = new PriorityQueue<>(moveComparator);
        this.checkMoves = new PriorityQueue<>(moveComparator);
        this.castlingMoves = new PriorityQueue<>(moveComparator);
        this.promotionMoves = new PriorityQueue<>(moveComparator);
    }

    public List<PriorityQueue<Move>> getAllQueues() {
        List<PriorityQueue<Move>> allQueues = new ArrayList<>();
        allQueues.add(validMoves);
        allQueues.add(validCaptures);
        allQueues.add(checkMoves);
        allQueues.add(castlingMoves);
        allQueues.add(promotionMoves);
        return allQueues;
    }
}