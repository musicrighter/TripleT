package david.triplet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HomeActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener {

    //declare variables
    private EditText p1NameEditText;
    private EditText p2NameEditText;


    // set up preferences
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initialize variables
        p1NameEditText = (EditText) findViewById(R.id.p1NameEditText);
        p2NameEditText = (EditText) findViewById(R.id.p2NameEditText);
        Button playerButton = (Button) findViewById(R.id.playerButton);
        Button computerButton = (Button) findViewById(R.id.computerButton);

        // set the default values for the preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // get the default SharedPreferences object
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String p1Name = prefs.getString("p1Name", "");
        String p2Name = prefs.getString("p2Name", "");

        p1NameEditText.setText(p1Name);
        p2NameEditText.setText(p2Name);

        playerButton.setOnClickListener(this);
        computerButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //check which menu item to run
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        changeFocus();

        String p1Name = p1NameEditText.getText().toString();
        String p2Name = p2NameEditText.getText().toString();

        Intent intent = new Intent(this, GameActivity.class);

        editor = prefs.edit();
        editor.putString("p1Name", p1Name);
        editor.putString("p2Name", p2Name);

        switch (view.getId()) {
            case R.id.playerButton:
                editor.putString("gameType", "twoPlayer");  // send state to 2nd activity
                break;
            case R.id.computerButton:
                editor.putString("gameType", "computer");  // send state to 2nd activity
                break;
        }
        editor.apply();

        startActivity(intent);
    }

    public void changeFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(HomeActivity.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.rootLayout).requestFocus();
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            changeFocus();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        changeFocus();
        return true;
    }
}
