package david.triplet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import java.util.Random;

/**
 * Created by David on 7/14/2016.
 */
public class GameActivity extends Activity implements View.OnClickListener {

    //declare variables
    private int turn;
    private int width;
    private AI ai = null;
    private String p1Name;
    private String p2Name;
    private String gameType;
    private View rootLayout;
    private TableLayout gridTableLayout;
    private TextView turnTextView;
    private GameBoard board = null;
    private boolean isOver = false;
    private Random random = new Random();
    private String mark = "X", aiMark = "O";
    private int moveCount = 0, xIndex = 0, yIndex = 0;

    private final int[] ID_LIST = { R.id.cell11, R.id.cell12,
            R.id.cell13, R.id.cell21, R.id.cell22, R.id.cell23,
            R.id.cell31, R.id.cell32, R.id.cell33 };

    // set up preferences
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private boolean keepNames = true;
    private boolean noBackground = false;
    private boolean turnPause = false;
    private boolean flipMarkers = false;
    private boolean flipTurns = false;
    private int difficulty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //initialize variables
        turnTextView = (TextView) findViewById(R.id.turnTextView);
        rootLayout = findViewById(R.id.rootLayout);
        gridTableLayout = (TableLayout) findViewById(R.id.gridTableLayout);
        Button homeButton = (Button) findViewById(R.id.homeButton);
        Button playAgainButton = (Button) findViewById(R.id.playAgainButton);

        homeButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);

        // set the default values for the preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // get the default SharedPreferences object
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        flipMarkers = prefs.getBoolean("pref_marker", false);

        if (flipMarkers) {
            mark = "O";
            aiMark = "X";
        }

        //set up a new board and AI and assign the initial variables
        board = new GameBoard();
        ai = new AI(aiMark);
        turn = 1;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        width = findViewById(R.id.Row1).getWidth();

        gridTableLayout.getLayoutParams().width = width;

        ImageView cells;
        for (int item : ID_LIST) {
            cells = (ImageView) findViewById(item);
            cells.getLayoutParams().height = (int) ((width/3) * 1.03);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // get preferences
        keepNames = prefs.getBoolean("pref_keep_names", true);
        noBackground = prefs.getBoolean("pref_no_background", false);
        turnPause = prefs.getBoolean("pref_AI_turn", false);
        flipTurns = prefs.getBoolean("pref_first_turn", false);
        difficulty = Integer.parseInt(prefs.getString("pref_AI_difficulty", "1"));

        if (noBackground) {
            rootLayout.setBackgroundColor(Color.BLACK);
        }
        else {
            rootLayout.setBackgroundColor(Color.parseColor("#d0007ca8"));
        }

        //get the game state sent from the HomeActivity
        p1Name = prefs.getString("p1Name", "Player 1");
        p2Name = prefs.getString("p2Name", "Player 2");
        gameType = prefs.getString("gameType", "computer");

        if (gameType.equals("computer")) {
            p2Name = "Computer";
        }

        if (!keepNames) {
            editor = prefs.edit();
            editor.putString("p1Name", "");
            editor.putString("p2Name", "");
            editor.apply();
        }

        if (flipTurns) {
            turn = 2;

            if (gameType.equals("computer")) {
                getAIMove(board);
            }
        }

        turnTextView.setText(checkName(1));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                break;
            case R.id.playAgainButton:
                clear();
                clear();
                break;
        }
    }

    //action for when a cell is clicked. Determines which cell has been clicked and passed that information on the the virtual game board.
    public void cellClick(View view) {

        //get the id of the clicked object and assign it to a Textview variable
        ImageView cell = (ImageView) findViewById(view.getId());

        //check the content and make sure the cell is empty and that the game isn't over
        Drawable content = (Drawable) cell.getDrawable();

        if (content == null && !isOver) {
            if (gameType.equals("computer")) {
                disableClick();
            }

            //find the X Y location values of the particular cell that was clicked
            switch (cell.getId()) {
                case R.id.cell11:
                    xIndex = 0;
                    yIndex = 0;
                    break;
                case R.id.cell12:
                    xIndex = 0;
                    yIndex = 1;
                    break;
                case R.id.cell13:
                    xIndex = 0;
                    yIndex = 2;
                    break;
                case R.id.cell21:
                    xIndex = 1;
                    yIndex = 0;
                    break;
                case R.id.cell22:
                    xIndex = 1;
                    yIndex = 1;
                    break;
                case R.id.cell23:
                    xIndex = 1;
                    yIndex = 2;
                    break;
                case R.id.cell31:
                    xIndex = 2;
                    yIndex = 0;
                    break;
                case R.id.cell32:
                    xIndex = 2;
                    yIndex = 1;
                    break;
                case R.id.cell33:
                    xIndex = 2;
                    yIndex = 2;
                    break;
            }

            //place the player's mark on the specific X Y location on both the virtual and displayed board
            board.placeMark(xIndex, yIndex, mark);

            String uri = "@drawable/" + mark.toLowerCase();
            int imageResource = getResources().getIdentifier(uri, null, this.getPackageName());
            Drawable res = ContextCompat.getDrawable(this, imageResource);
            cell.setImageDrawable(res);

            //change turn
            turn = ((turn + 2) % 2) + 1;

            //display the game results
            turnTextView.setText(checkName(1));

            moveCount++;
            isOver = checkEnd();

            //if the game isn't over
            if (!isOver) {
                if (gameType.equals("computer")) {
                    //change turn
                    turn = ((turn + 2) % 2) + 1;

                    turnTextView.setText("Computer's turn");

                    if (turnPause) {
                        //get random number to use for die
                        int delay = random.nextInt(600)+200;

                        //pause for a random amount of time between .5 and 1.5 seconds
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                turnTextView.setText(checkName(1));
                                //get the AI's move
                                getAIMove(board);
                                enableClick();
                            }
                        }, delay);
                    }
                    else {
                        turnTextView.setText(checkName(1));
                        //get the AI's move
                        getAIMove(board);
                        enableClick();
                    }
                }
                else {
                    mark = mark.equals("X") ? "O" : "X";
                }
            }
        }
    }

    private boolean checkEnd() {
        //check the board for a winner
        if (board.isWinner()) {
            displayWin(true);
            return true;
        }

        //check if move total has been reached
        else if (moveCount >= 9) {
            displayWin(false);
            return true;
        }

        //otherwise keep playing
        return false;
    }

    private void displayWin(boolean endState) {
        String player;
        if (endState) {
            //change turn
            turn = ((turn + 2) % 2) + 1;

            player = checkName(0) + " wins!";
        }
        else {
            player = "It's a draw!";
        }

        ImageView cell;
        for (int item : ID_LIST) {
            cell = (ImageView) findViewById(item);
            cell.setAlpha(.5F);
        }

        //to get flash effect
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView cell;
                for (int item : ID_LIST) {
                    cell = (ImageView) findViewById(item);
                    cell.setAlpha(1F);
                }
            }
        }, 200);

        //display the game results
        turnTextView.setText(player);
    }

    public String checkName(int withText) {
        String name;

        if (turn == 1) {
            name = p1Name;
        }
        else {
            name = p2Name;
        }

        //no entered name
        if (name.length() == 0) {
            if (withText == 1) {
                return "Player " + turn + "'s turn";
            }
            else {
                return "Player " + turn;
            }
        }
        else {
            if (withText == 1) {
                return name + "'s turn";
            }
            else {
                return name;
            }
        }
    }

    //clears the game Board
    private void clear() {
        //get the id list of all the ImageView cells
        int[] idList = { R.id.cell11, R.id.cell12, R.id.cell13, R.id.cell21, R.id.cell22, R.id.cell23, R.id.cell31, R.id.cell32, R.id.cell33 };
        ImageView cell;
        //for each cell clear the image
        for (int item : idList) {
            cell = (ImageView) findViewById(item);
            cell.setImageDrawable(null);
        }

        //reset the game state and clear the virtual board
        if (!keepNames) {
            p1Name = "Player 1";
            p2Name = "Player 2";
        }

        if (flipMarkers) {
            mark = "O";
            aiMark = "X";
        }
        else {
            mark = "X";
            aiMark = "O";
        }

        if (flipTurns) {
            turn = 2;
            if (gameType.equals("computer")) {
                getAIMove(board);
            }
        }
        else {
            turn = 1;
        }

        enableClick();
        turnTextView.setText(checkName(1));
        isOver = false;
        moveCount = 0;
        board.clear();
    }

    //gets the AI's next move giving the current state of the board
    private void getAIMove(GameBoard board) {
        //send the board to the AI for it to determine and return the move in an array {x,y}
        int[] move = ai.move(board, aiMark, difficulty);
        ImageView cell = null;

        //determine the right cell to use by id first go to the right row then the right column
        switch (move[0]) {
            case 0:
                switch (move[1]) {
                    case 0:
                        cell = (ImageView) findViewById(R.id.cell11); break;
                    case 1:
                        cell = (ImageView) findViewById(R.id.cell12); break;
                    case 2:
                        cell = (ImageView) findViewById(R.id.cell13); break;
                }
                break;
            case 1:
                switch (move[1]) {
                    case 0:
                        cell = (ImageView) findViewById(R.id.cell21); break;
                    case 1:
                        cell = (ImageView) findViewById(R.id.cell22); break;
                    case 2:
                        cell = (ImageView) findViewById(R.id.cell23); break;
                }
                break;
            case 2:
                switch (move[1]) {
                    case 0:
                        cell = (ImageView) findViewById(R.id.cell31); break;
                    case 1:
                        cell = (ImageView) findViewById(R.id.cell32); break;
                    case 2:
                        cell = (ImageView) findViewById(R.id.cell33); break;
                }
                break;
        }

        //Make sure nothing in the cell then place the mark with the ai's Mark, increment move count and check game over
        if (cell != null && cell.getDrawable() == null) {
            board.placeMark(move[0], move[1], aiMark);
            if (flipMarkers) {
                cell.setImageResource(R.drawable.x);
            }
            else {
                cell.setImageResource(R.drawable.o);
            }
            moveCount++;
            isOver = checkEnd();
        }
    }

    public void disableClick() {
        findViewById(R.id.cell11).setClickable(false);
        findViewById(R.id.cell12).setClickable(false);
        findViewById(R.id.cell13).setClickable(false);

        findViewById(R.id.cell21).setClickable(false);
        findViewById(R.id.cell22).setClickable(false);
        findViewById(R.id.cell23).setClickable(false);

        findViewById(R.id.cell31).setClickable(false);
        findViewById(R.id.cell32).setClickable(false);
        findViewById(R.id.cell33).setClickable(false);
    }

    public void enableClick() {
        findViewById(R.id.cell11).setClickable(true);
        findViewById(R.id.cell12).setClickable(true);
        findViewById(R.id.cell13).setClickable(true);

        findViewById(R.id.cell21).setClickable(true);
        findViewById(R.id.cell22).setClickable(true);
        findViewById(R.id.cell23).setClickable(true);

        findViewById(R.id.cell31).setClickable(true);
        findViewById(R.id.cell32).setClickable(true);
        findViewById(R.id.cell33).setClickable(true);
    }
}
