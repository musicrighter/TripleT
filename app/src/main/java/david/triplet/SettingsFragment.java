package david.triplet;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by David on 7/14/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}