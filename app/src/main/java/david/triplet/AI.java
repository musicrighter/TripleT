package david.triplet;

/**
 * Created by David on 7/15/2016.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AI {
    private int difficulty;
    private String myMark;
    private String oppMark;
    private String[][] board;
    private Random random = new Random();

    AI(String marker) {
        this.myMark = marker;
        this.oppMark = (myMark.equals("X")) ? "O" : "X";
    }

    public int[] move(GameBoard board, String marker, int difficulty) {
        this.myMark = marker;
        this.oppMark = (myMark.equals("X")) ? "O" : "X";
        this.board = board.getBoard();
        this.difficulty = difficulty;
        int[] result = minmax(2, myMark, Integer.MIN_VALUE, Integer.MAX_VALUE);

        return new int[] { result[1], result[2] };
    }

    private int[] minmax(int depth, String player, int alpha, int beta) {
        List<int[]> nextMoves = generateMoves();
        //mySeed is maximizing; while oppSeed is minimizing
        int score;
        int bestRow = -1;
        int bestCol = -1;

        if (nextMoves.isEmpty() || depth == 0) {
            //game over or depth reached, evaluate score
            score = evaluate();
            return new int[] { score, bestRow, bestCol };
        }
        else {
            if (difficulty == 0) {
                int movesSize = nextMoves.size();
                int index = random.nextInt(movesSize);
                bestRow = nextMoves.get(index)[0];
                bestCol = nextMoves.get(index)[1];
                return new int[] { (player.equals(myMark)) ? alpha : beta, bestRow, bestCol };
            }
            for (int[] move : nextMoves) {
                //try this move for the current "player"
                board[move[0]][move[1]] = player;
                if (player.equals(myMark)) {
                    // player
                    score = minmax(depth - 1, oppMark, alpha, beta)[0];
                    if (score > alpha) {
                        alpha = score;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }
                else {
                    score = minmax(depth - 1, myMark, alpha, beta)[0];
                    if (score < beta) {
                        beta = score;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }
                //undo move
                board[move[0]][move[1]] = "";
                //cut-off
                if (alpha >= beta)
                    break;
            }
        }
        return new int[] { (player.equals(myMark)) ? alpha : beta, bestRow, bestCol };
    }

    private List<int[]> generateMoves() {
        List<int[]> nextMoves = new ArrayList<>();

        //if game over
        if (hasWon(myMark) || hasWon(oppMark)) {
            return nextMoves; // return empty list
        }

        //search for empty cells and add to the List
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                if (board[row][col].equals("")) {
                    nextMoves.add(new int[] { row, col });
                }
            }
        }
        return nextMoves;
    }

    /**
     * The heuristic evaluation function for the current board
     *
     * Return +100, +10, +1 for EACH 3-, 2-, 1-in-a-line for computer. -100,
     *         -10, -1 for EACH 3-, 2-, 1-in-a-line for opponent. 0 otherwise
     */
    private int evaluate() {
        int score = 0;
        // Evaluate score for each of the 8 lines (3 rows, 3 columns, 2
        // diagonals)
        score += evaluateLine(0, 0, 0, 1, 0, 2); // row 0
        score += evaluateLine(1, 0, 1, 1, 1, 2); // row 1
        score += evaluateLine(2, 0, 2, 1, 2, 2); // row 2
        score += evaluateLine(0, 0, 1, 0, 2, 0); // col 0
        score += evaluateLine(0, 1, 1, 1, 2, 1); // col 1
        score += evaluateLine(0, 2, 1, 2, 2, 2); // col 2
        score += evaluateLine(0, 0, 1, 1, 2, 2); // diagonal
        score += evaluateLine(0, 2, 1, 1, 2, 0); // alternate diagonal
        return score;
    }

    /**
     * The heuristic evaluation function for the given line of 3 cells
     *
     * Return +100, +10, +1 for 3-, 2-, 1-in-a-line for computer. -100, -10, -1
     *         for 3-, 2-, 1-in-a-line for opponent. 0 otherwise
     */
    private int evaluateLine(int row1, int col1, int row2, int col2, int row3, int col3) {
        int score = 0;

        //first cell
        if (board[row1][col1].equals(myMark)) {
            score = 1;
        }
        else if (board[row1][col1].equals(oppMark)) {
            score = -1;
        }

        //second cell
        if (board[row2][col2].equals(myMark)) {
            if (score == 1) { //cell1 is mySeed
                score = 10;
            }
            else if (score == -1) { //cell1 is oppSeed
                return 0;
            }
            else { //cell1 is empty
                score = 1;
            }
        } else if (board[row2][col2].equals(oppMark)) {
            if (score == -1) { //cell1 is oppSeed
                score = -10;
            }
            else if (score == 1) { //cell1 is mySeed
                return 0;
            }
            else { //cell1 is empty
                score = -1;
            }
        }

        // Third cell
        if (board[row3][col3].equals(myMark)) {
            if (score > 0) { //cell1 and/or cell2 is mySeed
                score *= 10;
            }
            else if (score < 0) { //cell1 and/or cell2 is oppSeed
                return 0;
            }
            else { //cell1 and cell2 are empty
                score = 1;
            }
        } else if (board[row3][col3].equals(oppMark)) {
            if (score < 0) { //cell1 and/or cell2 is oppSeed
                score *= 10;
            }
            else if (score > 1) { //cell1 and/or cell2 is mySeed
                return 0;
            }
            else { //cell1 and cell2 are empty
                score = -1;
            }
        }
        return score;
    }

    public boolean hasWon(String thePlayer) {
        //check diagonals
        if (board[0][0].equals(thePlayer) && board[1][1].equals(thePlayer) && board[2][2].equals(thePlayer))
            return true;

        if (board[2][0].equals(thePlayer) && board[1][1].equals(thePlayer) && board[0][2].equals(thePlayer))
            return true;

        for (int i = 0; i < 3; i++) {
            //check rows
            if (board[i][0].equals(thePlayer) && board[i][1].equals(thePlayer) && board[i][2].equals(thePlayer))
                return true;

            //check columns
            if (board[0][i].equals(thePlayer) && board[1][i].equals(thePlayer)
                    && board[2][i].equals(thePlayer))
                return true;
        }

        return false;
    }
}
