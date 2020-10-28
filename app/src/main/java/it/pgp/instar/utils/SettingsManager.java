package it.pgp.instar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import it.pgp.instar.adapters.GalleryAdapter;

public class SettingsManager {

    public final Context context;

    public SettingsManager(Context context) {
        this.context = context;
    }

    public void storeSpanNumber(int spans) {
        SharedPreferences.Editor x = PreferenceManager.getDefaultSharedPreferences(context).edit();
        x.putString("span_num",spans+"");
        x.commit(); // OK to persist immediately here
    }

    public int loadSpanNumber() {
        // TODO find a good way of forcing numeric inputType and validating min/max bounds on EditTextPreference
        int spans = 5;
        String tmp = PreferenceManager.getDefaultSharedPreferences(context).getString("span_num", "5");
        try {
            spans = Integer.parseInt(tmp);
            if(spans > GalleryAdapter.MAX_SPANS)
                spans = GalleryAdapter.MAX_SPANS;
            if(spans < GalleryAdapter.MIN_SPANS)
                spans = GalleryAdapter.MIN_SPANS;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return spans;
    }

}
