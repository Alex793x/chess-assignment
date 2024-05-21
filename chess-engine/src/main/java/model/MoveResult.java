package model;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Getter
@Setter
public class MoveResult {
    
    private PriorityQueue<Move> validMoves;
    private PriorityQueue<Move> validCaptures;
    private PriorityQueue<Move> promotionMoves;

    public MoveResult(Comparator<Move> moveComparator) {
        this.validMoves = new PriorityQueue<>(moveComparator);
        this.validCaptures = new PriorityQueue<>(moveComparator);
        this.promotionMoves = new PriorityQueue<>(moveComparator);
    }


    @Override
    public String toString() {
        return "MoveResult{" +
                "validMoves=" + validMoves +
                ", validCaptures=" + validCaptures +
                ", promotionMoves=" + promotionMoves +
                '}';
    }
}