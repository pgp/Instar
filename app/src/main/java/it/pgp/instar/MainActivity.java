package it.pgp.instar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.List;

import it.pgp.instar.adapters.CustomScrollerViewProvider;
import it.pgp.instar.adapters.GalleryAdapter;
import it.pgp.instar.enums.GalleryOrientation;
import it.pgp.instar.enums.GalleryRefreshMode;
import it.pgp.instar.utils.CustomGridLayoutManager;
import it.pgp.instar.utils.PaddingManager;
import it.pgp.instar.views.ScaleInfoView;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LayoutInflater inflater;
    RecyclerView mainGalleryView;
    CustomGridLayoutManager lm;
    FastScroller fastScroller;
    List<View> viewListTop, viewListBottom;
    GalleryAdapter ga;
    RequestManager GlideR;
    DrawerLayout drawer;

    private final RecyclerView.OnScrollListener glideScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_SETTLING:
                    GlideR.pauseRequests();
                    Log.d("GlideR","Paused");
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    GlideR.resumeRequests();
                    Log.d("GlideR","Resumed");
                    break;
            }
        }
    };

    public GalleryOrientation currentOrientation = GalleryOrientation.VERTICAL;

    public static final int STORAGE_PERM_ID = 123;

    public void checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERM_ID);
        }
        else refreshAdapter(GalleryRefreshMode.REFRESH_ALL);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERM_ID) {
            if (grantResults.length == 0) { // request cancelled
                Toast.makeText(this, R.string.storage_perm_denied, Toast.LENGTH_SHORT).show();
                finishAffinity();
                return;
            }

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.storage_perm_denied, Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    return;
                }
            }

            Toast.makeText(this, R.string.storage_perm_granted, Toast.LENGTH_SHORT).show();
            refreshAdapter(GalleryRefreshMode.REFRESH_ALL);
        }
    }

    public void switchGalleryOrientation(View unused) {
        currentOrientation = currentOrientation.next();
        refreshAdapter(GalleryRefreshMode.KEEP_SELECTION_MODE);
    }

    public void refreshAdapter(GalleryRefreshMode refreshOnlyViews) {
        rl.removeAllViews();
        inflater.inflate(currentOrientation.layoutResId, rl, true);
        mainGalleryView = findViewById(R.id.mainGalleryView);
        fastScroller = findViewById(R.id.fastScroll);
        viewListTop = Arrays.asList(mainGalleryView,fastScroller);
        viewListBottom = Arrays.asList(findViewById(R.id.switchButton));
        lm = new CustomGridLayoutManager(this, GalleryAdapter.spans, false, currentOrientation);
        mainGalleryView.setLayoutManager(lm);
        mainGalleryView.setHasFixedSize(true);
        drawer = findViewById(R.id.drawer_layout);

        switch(refreshOnlyViews) {
            case REFRESH_ALL:
                ga = GalleryAdapter.createAdapter(this,
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
                break;
            case KEEP_SELECTION_MODE:
                ga = GalleryAdapter.from(ga);
                break;
            case KEEP_ALL:
                break;
        }

        if(ga == null) {
            Toast.makeText(this, "Unable to access DCIM/Camera, exiting...", Toast.LENGTH_SHORT).show();
            finishAffinity();
            return;
        }

        ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (detector.getCurrentSpan() - detector.getPreviousSpan() < -1) {
                    sv.currentAngle -= 1;
                }
                else if (detector.getCurrentSpan() - detector.getPreviousSpan() > 1) {
                    sv.currentAngle += 1;
                }
                sv.invalidate();
                return true;
            }

            ScaleInfoView sv;

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                sv = new ScaleInfoView(MainActivity.this);
                lm.lockScroll();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                sv.setLayoutParams(params);
                rl.addView(sv);
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                int currentSpans = sv.getTargetSpans();

                if(GalleryAdapter.spans != currentSpans) {
                    GalleryAdapter.spans = currentSpans;
                    PaddingManager.h.postDelayed(()->refreshAdapter(GalleryRefreshMode.KEEP_SELECTION_MODE),250); // use postDelayed here for avoiding spurious NPEs
                }
                lm.unlockScroll();
                rl.removeView(sv);

            }
        });

        mainGalleryView.setOnTouchListener( (v,me) ->{
            mScaleGestureDetector.onTouchEvent(me);
            return false;
        });

        mainGalleryView.setAdapter(ga);
        mainGalleryView.addOnScrollListener(glideScrollListener);
        fastScroller.setViewProvider(new CustomScrollerViewProvider());
        fastScroller.setRecyclerView(mainGalleryView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rl.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        paddingManager.registerDisplayListener(viewListTop, viewListBottom);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    RelativeLayout rl;
    public PaddingManager paddingManager;

    public static DisplayMetrics getDisplaySizes(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.all:
                GalleryAdapter.instance.selectAll();
                break;
            case R.id.none:
                GalleryAdapter.instance.clearSelection();
                break;
            case R.id.invert:
                GalleryAdapter.instance.invertSelection();
                break;
            case R.id.delete_items:
                if(GalleryAdapter.instance.selectedItems.get()==0)
                    Toast.makeText(this, "No items selected for deletion", Toast.LENGTH_SHORT).show();
                else GalleryAdapter.instance.deleteSelection();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(128, 0, 0, 0)));
        paddingManager = new PaddingManager(this);
        if(PaddingManager.hidden) getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        rl = findViewById(R.id.rootLayout);
        inflater = LayoutInflater.from(this);
        GlideR = Glide.with(MainActivity.this);

        checkStoragePermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GalleryAdapter.instance = null;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else if(GalleryAdapter.instance != null && GalleryAdapter.instance.multiselect)
            exitMultiSelectMode();
        else super.onBackPressed();
    }

    public static final DialogInterface.OnClickListener alertDialogNoOpsChoice = (dialog, which) -> {};

    public void exitMultiSelectMode() {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle("Exit multi select mode? Current selection will be lost");
        bld.setNegativeButton("No", alertDialogNoOpsChoice);
        bld.setPositiveButton("Yes", (dialog, which) -> {
            GalleryAdapter.instance.multiselect = false;
            GalleryAdapter.instance.clearSelection();
            toggleActionBar(true);
        });
        AlertDialog alertDialog = bld.create();
        alertDialog.show();
    }

    public void reloadImgCache(View unused) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle("Recreate thumbnail cache?");
        bld.setNegativeButton("No", alertDialogNoOpsChoice);
        bld.setPositiveButton("Yes", (dialog, which) ->
                new Thread(()-> {
                    Glide.get(MainActivity.this).clearDiskCache();
                    runOnUiThread(()->{
                        refreshAdapter(GalleryRefreshMode.REFRESH_ALL);
                        Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                    });
                }).start());
        AlertDialog alertDialog = bld.create();
        alertDialog.show();
    }

    public void toggleActionBar(boolean hidden) {
        PaddingManager.hidden = hidden;
        if(PaddingManager.hidden) {
            getSupportActionBar().hide();
            refreshAdapter(GalleryRefreshMode.KEEP_SELECTION_MODE);
        }
        else {
            // legacy refreshAdapter() could not be called here, because otherwise selectedItems would become incorrect by 1
            // but it had to be called in order to update insets, so signature was modified from boolean to enum param
            getSupportActionBar().show();
            refreshAdapter(GalleryRefreshMode.KEEP_ALL);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        switch(item.getItemId()) {
            case R.id.switch_gallery_orientation:
                switchGalleryOrientation(null);
                break;
            case R.id.reload_img_cache:
                reloadImgCache(null);
                break;

        }

        return true;
    }
}