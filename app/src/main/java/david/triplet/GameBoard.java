package david.triplet;

/**
 * Created by David on 7/15/2016.
 */
public class GameBoard {

    //declare variables
    private String[][] theBoard = new String[3][3];

    //initialize the Game board filled with empty strings
    GameBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                theBoard[i][j] = "";
            }
        }
    }

    //clears the game board by looping through each row and column and puts the empty string
    public void clear() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                theBoard[i][j] = "";
            }
        }
    }

    //getter that returns the virtual board
    public String[][] getBoard() {
        return theBoard;
    }

    //checks to make sure a mark doesn't already exist before placing the mark.
    public void placeMark(int xloc, int yloc, String mark) {
        if (theBoard[xloc][yloc].equals("")) theBoard[xloc][yloc] = mark;
    }

    //determines if there is a winner or not checks each diagonal then loops through each row/column
    public boolean isWinner() {
        //check diagonals
        if (theBoard[0][0].equals(theBoard[1][1]) && theBoard[0][0].equals(theBoard[2][2]) && !theBoard[0][0].equals(""))
            return true;
        if (theBoard[2][0].equals(theBoard[1][1]) && theBoard[2][0].equals(theBoard[0][2]) && !theBoard[2][0].equals(""))
            return true;
        for (int i = 0; i < 3; i++) {
            //check rows
            if (theBoard[i][0].equals(theBoard[i][1]) && theBoard[i][1].equals(theBoard[i][2]) && !theBoard[i][0].equals(""))
                return true;
            //check columns
            if (theBoard[0][i].equals(theBoard[1][i]) && theBoard[1][i].equals(theBoard[2][i]) && !theBoard[0][i].equals(""))
                return true;
        }
        return false;
    }
}
