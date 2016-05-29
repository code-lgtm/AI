package csp;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kumar_Garg on 5/28/2016.
 */
public class Queen {
    private int board;
    private int dVar[];
    private HashMap<Integer, ArrayList<Integer>> domain = new HashMap<>();

    public Queen(int board) {
        this.board = board;
        dVar = new int[this.board];
        for (int i = 0; i < board; i++) {
            ArrayList<Integer> values = new ArrayList<>();
            for (int j = 0; j < board; j++)
                values.add(j);
            dVar[i] = -1;
            domain.put(i,values);
        }
    }

    private boolean areAllConstraintsMet(int row, int value) {
        return isColumnConstraintMet(row, value) &&
                isLowerDiagonalConstraintMet(row, value) &&
                isUpperDiagonalConstraintMet(row, value);
     }

    private boolean isColumnConstraintMet(int row, int value) {
        for(int i = 0; i < this.board; i++) {
            if(i == row) continue;
            if(dVar[i] == -1) continue; // Decision variable has not been assigned yet
            if(value == dVar[i]) return false;
        }
        return true;
    }

    private boolean isLowerDiagonalConstraintMet(int row, int value) {
        if (value < 0) return false;
        if(value >= this.board) return false;
        for(int i = 0; i < this.board; i++) {
            if(i == row) continue;
            if(dVar[i] == -1) continue; // Decision variable has not been assigned yet

            int nonAllowedVal = dVar[i] +  row - i;
            if (nonAllowedVal < 0 || nonAllowedVal >= this.board) continue;
            if(value == nonAllowedVal) return false;
        }
        return true;
    }

    private boolean isUpperDiagonalConstraintMet(int row, int value) {
        if (value < 0) return false;
        if(value >= this.board) return false;

        for(int i = 0; i < this.board; i++) {
            if(i == row) continue;
            if(dVar[i] == -1) continue; // Decision variable has not been assigned yet

            int nonAllowedVal = dVar[i] +  i - row;
            if (nonAllowedVal < 0 || nonAllowedVal >= this.board) continue;
            if(value == nonAllowedVal) return false;
        }
        return true;
    }

    public boolean areAllAssigned() {
        for (int i = 0; i < board; i++)
            if (this.dVar[i] == -1)
                return false;
        return true;
    }

    public boolean backtrackingSearch() {
        // Check if assignment is complete
        if(areAllAssigned()) return true;

        // Select unassigned variable
        int unassigned = -1;
        for (int i = 0; i < board; i++)
            if (dVar[i] == -1)
                unassigned = i;

        // Check for each value if
        for(int value : domain.get(unassigned)) {
            if (areAllConstraintsMet(unassigned, value)) {
                dVar[unassigned] = value;
                boolean result = backtrackingSearch();
                if(result) return true;
                dVar[unassigned] = -1;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        Queen queen = new Queen(4);
        System.out.println(queen.backtrackingSearch());
        for (int i = 0; i < queen.board; i++) {
            System.out.println(queen.dVar[i]);
        }
    }
}
