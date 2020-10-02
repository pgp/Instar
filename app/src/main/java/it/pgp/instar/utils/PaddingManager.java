package it.pgp.instar.utils;

import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaddingManager {

    public static boolean hidden = true;
    // FIXME insetsPadding values work only for views that are aligned on main layout's top or bottom (TODO generalize for left, right and refactor code)
    public static Map<Integer, Map<String, Integer>> insetsPadding = new HashMap<>();
    public static Map<Integer, Map<String, Integer>> insetsPaddingBottomViews = new HashMap<>();
    public static Map<Integer, Map<String, Integer>> hiddenInsetsPadding = new HashMap<>();
    public static Map<Integer, Map<String, Integer>> hiddenInsetsPaddingBottomViews = new HashMap<>();
    public static final Handler h = new Handler();

    public final AppCompatActivity activity;
    public DisplayManager dm;
    public DisplayManager.DisplayListener dl;

    public PaddingManager(AppCompatActivity activity) {
        this.activity = activity;
    }
    
    public static final float BOTH_ACTION_BAR_AND_STATUS_PADDING = 81f;
    public static final float STATUS_PADDING = 24f;
    public static final float NAVBAR_PADDING = 48f;

    public void createMaps() {
        if(insetsPadding.isEmpty()) {
            if(hasSoftKeys()) {
                insetsPadding = Utils.mapOf(
                        Surface.ROTATION_0,
                        Utils.mapOf(
                                "top",pxFromDp(BOTH_ACTION_BAR_AND_STATUS_PADDING),
                                "left",0,
                                "bottom",pxFromDp(NAVBAR_PADDING),
                                "right",0
                        ),
                        Surface.ROTATION_90,
                        Utils.mapOf(
                                "top",pxFromDp(BOTH_ACTION_BAR_AND_STATUS_PADDING),
                                "left",0,
                                "bottom",0,
                                "right",pxFromDp(NAVBAR_PADDING)
                        ),
                        Surface.ROTATION_180,// not always called, have,enable setting first
                        Utils.mapOf(
                                "top",pxFromDp(BOTH_ACTION_BAR_AND_STATUS_PADDING),
                                "left",0,
                                "bottom",pxFromDp(NAVBAR_PADDING),
                                "right",0
                        ),
                        Surface.ROTATION_270,
                        Utils.mapOf(
                                "top",pxFromDp(BOTH_ACTION_BAR_AND_STATUS_PADDING),
                                "left",pxFromDp(NAVBAR_PADDING),
                                "bottom",0,
                                "right",0
                        )
                );

                hiddenInsetsPadding = Utils.mapOf(
                        Surface.ROTATION_0,
                        Utils.mapOf(
                                "top",pxFromDp(STATUS_PADDING),
                                "left",0,
                                "bottom",pxFromDp(NAVBAR_PADDING),
                                "right",0
                        ),
                        Surface.ROTATION_90,
                        Utils.mapOf(
                                "top",pxFromDp(STATUS_PADDING),
                                "left",0,
                                "bottom",0,
                                "right",pxFromDp(NAVBAR_PADDING)
                        ),
                        Surface.ROTATION_180, // not always called, have,enable setting first
                        Utils.mapOf(
                                "top",pxFromDp(STATUS_PADDING),
                                "left",0,
                                "bottom",pxFromDp(NAVBAR_PADDING),
                                "right",0
                        ),
                        Surface.ROTATION_270,
                        Utils.mapOf(
                                "top",pxFromDp(STATUS_PADDING),
                                "left",pxFromDp(NAVBAR_PADDING),
                                "bottom",0,
                                "right",0
                        )
                );

                insetsPaddingBottomViews = Utils.mapOf(
                        Surface.ROTATION_0,
                        Utils.mapOf(
                                "top",0,
                                "left",0,
                                "bottom",pxFromDp(NAVBAR_PADDING),
                                "right",0
                        ),
                        Surface.ROTATION_90,
                        Utils.mapOf(
                                "top",0,
                                "left",0,
                                "bottom",0,
                                "right",pxFromDp(NAVBAR_PADDING)
                        ),
                        Surface.ROTATION_180,// not always called, have,enable setting first
                        Utils.mapOf(
                                "top",0,
                                "left",0,
                                "bottom",pxFromDp(NAVBAR_PADDING),
                                "right",0
                        ),
                        Surface.ROTATION_270,
                        Utils.mapOf(
                                "top",0,
                                "left",pxFromDp(NAVBAR_PADDING),
                                "bottom",0,
                                "right",0
                        )
                );

                hiddenInsetsPaddingBottomViews = Utils.mapOf(
                        Surface.ROTATION_0,
                        Utils.mapOf(
                                "top",0,
                                "left",0,
                                "bottom",pxFromDp(NAVBAR_PADDING),
                                "right",0
                        ),
                        Surface.ROTATION_90,
                        Utils.mapOf(
                                "top",0,
                                "left",0,
                                "bottom",0,
                                "right",pxFromDp(NAVBAR_PADDING)
                        ),
                        Surface.ROTATION_180, // not always called, have,enable setting first
                        Utils.mapOf(
                                "top",0,
                                "left",0,
                                "bottom",pxFromDp(NAVBAR_PADDING),
                                "right",0
                        ),
                        Surface.ROTATION_270,
                        Utils.mapOf(
                                "top",0,
                                "left",pxFromDp(NAVBAR_PADDING),
                                "bottom",0,
                                "right",0
                        )
                );
            }
            else {
                insetsPadding = Utils.mapOf(
                        Surface.ROTATION_0,
                        Utils.mapOf(
                                "top",pxFromDp(BOTH_ACTION_BAR_AND_STATUS_PADDING),
                                "left",0,
                                "bottom",0,
                                "right",0
                        ),
                        Surface.ROTATION_90,
                        Utils.mapOf(
                                "top",pxFromDp(BOTH_ACTION_BAR_AND_STATUS_PADDING),
                                "left",0,
                                "bottom",0,
                                "right",0
                        ),
                        Surface.ROTATION_180,// not always called, have,enable setting first
                        Utils.mapOf(
                                "top",pxFromDp(BOTH_ACTION_BAR_AND_STATUS_PADDING),
                                "left",0,
                                "bottom",0,
                                "right",0
                        ),
                        Surface.ROTATION_270,
                        Utils.mapOf(
                                "top",pxFromDp(BOTH_ACTION_BAR_AND_STATUS_PADDING),
                                "left",0,
                                "bottom",0,
                                "right",0
                        )

                );

                hiddenInsetsPadding = Utils.mapOf(
                        Surface.ROTATION_0,
                        Utils.mapOf(
                                "top",pxFromDp(STATUS_PADDING),
                                "left",0,
                                "bottom",0,
                                "right",0
                        ),
                        Surface.ROTATION_90,
                        Utils.mapOf(
                                "top",pxFromDp(STATUS_PADDING),
                                "left",0,
                                "bottom",0,
                                "right",0
                        ),
                        Surface.ROTATION_180,// not always called, have,enable setting first
                        Utils.mapOf(
                                "top",pxFromDp(STATUS_PADDING),
                                "left",0,
                                "bottom",0,
                                "right",0
                        ),
                        Surface.ROTATION_270,
                        Utils.mapOf(
                                "top",pxFromDp(STATUS_PADDING),
                                "left",0,
                                "bottom",0,
                                "right",0
                        )
                );

                insetsPaddingBottomViews = insetsPadding;
                hiddenInsetsPaddingBottomViews = hiddenInsetsPadding;
            }
        }
    }


    public void adjustPaddings(List<View> viewsTop, List<View> viewsBottom) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createMaps();

            int degrees = activity.getWindowManager().getDefaultDisplay().getRotation();

            for(View v : viewsTop) {
                if (hidden)
                    v.setPadding(
                            hiddenInsetsPadding.get(degrees).get("left"),
                            hiddenInsetsPadding.get(degrees).get("top"),
                            hiddenInsetsPadding.get(degrees).get("right"),
                            hiddenInsetsPadding.get(degrees).get("bottom")
                    );
                else
                    v.setPadding(
                            insetsPadding.get(degrees).get("left"),
                            insetsPadding.get(degrees).get("top"),
                            insetsPadding.get(degrees).get("right"),
                            insetsPadding.get(degrees).get("bottom")
                    );
            }

            for(View v : viewsBottom) {
                if (hidden)
                    v.setPadding(
                            hiddenInsetsPaddingBottomViews.get(degrees).get("left"),
                            hiddenInsetsPaddingBottomViews.get(degrees).get("top"),
                            hiddenInsetsPaddingBottomViews.get(degrees).get("right"),
                            hiddenInsetsPaddingBottomViews.get(degrees).get("bottom")
                    );
                else
                    v.setPadding(
                            insetsPaddingBottomViews.get(degrees).get("left"),
                            insetsPaddingBottomViews.get(degrees).get("top"),
                            insetsPaddingBottomViews.get(degrees).get("right"),
                            insetsPaddingBottomViews.get(degrees).get("bottom")
                    );
            }
        }
    }


    public void registerDisplayListener(List<View> viewsTop, List<View> viewsBottom) {
        adjustPaddings(viewsTop, viewsBottom);

        dm = (DisplayManager) activity.getSystemService(AppCompatActivity.DISPLAY_SERVICE);

        if(dl != null)
            dm.unregisterDisplayListener(dl);

        dl = new DisplayManager.DisplayListener() {
            @Override
            public void onDisplayAdded(int displayId) {}

            @Override
            public void onDisplayRemoved(int displayId) {}

            @Override
            public void onDisplayChanged(int displayId) {
                Toast.makeText(activity, "Current orientation: "+activity.getWindowManager().getDefaultDisplay().getRotation(), Toast.LENGTH_SHORT).show();
                adjustPaddings(viewsTop,viewsBottom);
            }
        };

        dm.registerDisplayListener(dl, h);
    }

    public boolean hasSoftKeys() {
        boolean hasSoftwareKeys;

        Display d = activity.getWindowManager().getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        hasSoftwareKeys =  (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        return hasSoftwareKeys;
    }

    private int pxFromDp(float dp) {
        return (int)(dp * activity.getResources().getDisplayMetrics().density);
    }
}
