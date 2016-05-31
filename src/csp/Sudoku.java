package csp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Kumar_Garg on 5/31/2016.
 */
public class Sudoku {
    private static String digits = "123456789";
    private static String rows = "ABCDEFGHI";
    private static String columns = digits;
    private ArrayList<String> squares;
    private ArrayList<ArrayList<String>> unitlist;
    private HashMap<String, HashSet<String>> peers;
    private HashMap<String, ArrayList<ArrayList<String>>> units;

    public Sudoku() {
        this.squares = crossProduct(rows, columns);
        this.populateUnitList();
        this.populateUnits();
        this.populatePeers();
    }

    private ArrayList<String> crossProduct(String A, String B)
    {
        ArrayList<String> product = new ArrayList<>();

        for (int i = 0; i < A.length(); i++) {
            char a = A.charAt(i);
            for (int j = 0; j < B.length(); j++)  {
                char b = B.charAt(j);
                char[] c = {a, b};
                product.add(new String(c));
            }
        }
        return product;
    }

    private void populateUnitList() {
        this.unitlist = new ArrayList<>();
        // Add Boxes
        for (int i = 0; i < rows.length(); i+= 3) {
            String a = rows.substring(i, i+3);
            for (int j = 0; j < columns.length(); j+= 3) {
                String b = columns.substring(j, j+3);
                this.unitlist.add(crossProduct(a, b));
            }
        }
        // Add Rows
        for (int i = 0; i < rows.length(); i++)
            this.unitlist.add(crossProduct(rows.substring(i, i+1), columns));
        // Add Columns
        for (int j = 0; j < columns.length(); j++)
            this.unitlist.add(crossProduct(rows, columns.substring(j, j+1)));
    }

    private void populateUnits() {
        this.units = new HashMap<>();
        for (String square : squares) {
            ArrayList<ArrayList<String>> sqUnits = new ArrayList<>();
            for (ArrayList<String> unit : this.unitlist)
                if (unit.contains(square))
                    sqUnits.add(unit);
            this.units.put(square, sqUnits);
        }
    }

    private void populatePeers() {
        this.peers = new HashMap<>();
        for (String square : squares) {
            HashSet<String> set = new HashSet<>();
            for (ArrayList<String> sqUnit: this.units.get(square))
                if(sqUnit.contains(square))
                    for(String peer : sqUnit)
                        set.add(peer);
            set.remove(square);
            this.peers.put(square, set);
        }
    }

    private HashMap<String, String> parse_grid(String grid) {
        HashMap<String, String> values = new HashMap<>();
        for (String square : this.squares)
            values.put(square, digits);
        for(Map.Entry<String, String> cell : grid_values(grid).entrySet())
            if (digits.contains(cell.getValue()) &&
                    assign(values, cell.getKey(), cell.getValue()) == null)
                return null;
        return values;
    }

    private HashMap<String, String> assign(HashMap<String, String> values, String s, String d) {
        String other_values = values.get(s).replace(d, "");
        for (int i = 0; i < other_values.length(); i++)
            if (propagate(values, s, other_values.substring(i,i+1)) == null)
                return null;
        return values;
    }

    private HashMap<String, String> propagate(HashMap<String, String> values, String s, String d) {
        String oldValue = values.get(s);
        if (!oldValue.contains(d)) return values;
        String newValue = oldValue.replace(d, "");
        values.put(s, newValue);
        if (newValue.length() == 0) return null;
        else if(newValue.length() == 1) {
            for(String s1 : this.peers.get(s))
                if (propagate(values, s1, newValue) == null)
                    return null;
        }

        for (ArrayList<String> unit :this.units.get(s)) {
            ArrayList<String> dPlaces = new ArrayList<>();

            for (String cell : unit) {
                if(values.get(cell).contains(d)) dPlaces.add(cell);
            }
            if (dPlaces.size() == 0) return null;
            if (dPlaces.size() == 1) {
                if(assign(values, dPlaces.get(0), d) == null)
                    return null;
            }
        }
        return values;
    }

    private HashMap<String, String> grid_values(String grid) {
        HashMap<String, String> values = new HashMap<>();
        for (int i = 0, j = 0; i < grid.length(); i++)
        {
            CharSequence digit = grid.subSequence(i, i+1);
            if(this.digits.contains(digit) ||
                    digit.charAt(0) == '0' ||
                    digit.charAt(0) == '.')
                values.put(squares.get(j++), new String(digit.toString()));
        }
        return values;
    }

    public void display(HashMap<String, String> values) {
        for (int i = 0; i < rows.length(); i++) {
            for (int j = 0; j < columns.length(); j++) {
                char c[] = {rows.charAt(i), columns.charAt(j)};
                String square = new String(c);

                System.out.print(values.get(square) + " ");
                if (j == 2 || j == 5)
                    System.out.print("|" + " ");
            }
            System.out.println();
            if(i == 2 || i == 5) {
                for (int j = 0; j < columns.length(); j++)
                    System.out.print("-" + " ");
                System.out.print("-" + " " + "-");
                System.out.println();
            }
        }
    }

    public HashMap<String, String> solve(String grid) {
        return search(parse_grid(grid));
    }

    private HashMap<String, String> search(HashMap<String, String> values) {
        if (values == null) return null;

        boolean solved = true;
        for(String square : squares) {
            if(values.get(square).length() != 1) solved = false;
        }
        if(solved) return values;

        // Minimum Remaining Values
        int len = Integer.MAX_VALUE;
        String s = null;
        String domain = null;
        for (String square : squares) {
            String value = values.get(square);
            if (value.length() > 1 && value.length() < len) {
                s = square;
                domain = value;
                len = value.length();
            }
        }

        ArrayList<HashMap<String, String>> valueList = new ArrayList<>();
        for (int i = 0; i < domain.length(); i++) {
            HashMap<String, String> modifiedValue =
                    search(assign((HashMap<String, String>)values.clone(), s, domain.substring(i, i+1)));
            if (modifiedValue != null) {
                valueList.add(modifiedValue);
            }
        }

        for (HashMap<String, String> newVal : valueList)
            return newVal;

        return null;
    }


    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();
        //String grid = "003020600900305001001806400008102900700000008006708200002609500800203009005010300"; //Simple
        //String grid = "4.....8.5.3..........7......2.....6.....8.4......1.......6.3.7.5..2.....1.4......"; //Hard
        String grid = "3...8.......7....51..............36...2..4....7...........6.13..452...........8.."; //Hard
        //String grid = ".....6....59.....82....8....45........3........6..3.54...325..6.................."; //Hardest
        sudoku.display(sudoku.solve(grid));
    }
}