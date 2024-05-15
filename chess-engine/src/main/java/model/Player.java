package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import model.enums.CurrentPlayer;

@Getter
@Setter
@AllArgsConstructor
public class Player {
    
    CurrentPlayer currentPlayer;
    boolean hasKingMoved;
    boolean hasKingSideRookMoved;
    boolean hasQueenSideRookMoved;
    boolean hasCastled;
}
