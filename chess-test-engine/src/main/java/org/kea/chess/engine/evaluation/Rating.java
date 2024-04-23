package org.kea.chess.engine.evaluation;

public class Rating {

    public static int rating() {
        int counter = 0;
        counter += rateAttackingPosition();
        counter += rateMaterialPosition();
        counter += rateMaterialPosition();
        counter += rateMoveAbilityPosition();
        counter += rateGeneralBoardPosition();
        return 0;
    }

    public static int rateAttackingPosition() {
        return 0;
    }

    public static int rateMaterialPosition() {
        return 0;
    }

    public static int rateMoveAbilityPosition() {
        return 0;
    }

    public static int rateGeneralBoardPosition() {
        return 0;
    }
}
