package it.pgp.instar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.futuremind.recyclerviewfastscroll.FastScroller;

import it.pgp.instar.adapters.CustomScrollerViewProvider;
import it.pgp.instar.adapters.GalleryAdapter;

public class MainActivity extends AppCompatActivity {

    LayoutInflater inflater;
    RecyclerView mainGalleryView;
    FastScroller fastScroller;
    final GalleryAdapter[] ga = {null};
    RequestManager GlideR;

    private final RecyclerView.OnScrollListener glideScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    GlideR.pauseRequests();
                    Log.e("GlideR","Paused");
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    GlideR.resumeRequests();
                    Log.e("GlideR","Resumed");
                    break;
            }
        }
    };

    public enum GalleryOrientation {
        HORIZONTAL(GridLayoutManager.HORIZONTAL, R.layout.recyclerview_horizontal),
        VERTICAL(GridLayoutManager.VERTICAL, R.layout.recyclerview_vertical);

        public final int orientation;
        public final int layoutResId;

        GalleryOrientation(int orientation, int layoutResId) {
            this.orientation = orientation;
            this.layoutResId = layoutResId;
        }

        public GalleryOrientation next() {
            return (this==HORIZONTAL)?VERTICAL:HORIZONTAL;
        }
    }

    public GalleryOrientation current = GalleryOrientation.VERTICAL;

    public static final int STORAGE_PERM_ID = 123;

    public void checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERM_ID);
        }
        else refreshAdapter();
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
            refreshAdapter();
        }
    }

    public void switchGalleryOrientation(View unused) {
        current = current.next();
        refreshAdapter();
    }

    public void refreshAdapter() {
        rl.removeAllViews();
        inflater.inflate(current.layoutResId, rl, true);
        mainGalleryView = findViewById(R.id.mainGalleryView);
        fastScroller = findViewById(R.id.fastScroll);

        mainGalleryView.setLayoutManager(new GridLayoutManager(this, GalleryAdapter.spans, current.orientation, false));
        mainGalleryView.setHasFixedSize(true);

        ga[0] = GalleryAdapter.createAdapter(this,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
        if(ga[0] == null) {
            Toast.makeText(this, "Unable to access DCIM/Camera, exiting...", Toast.LENGTH_SHORT).show();
            finishAffinity();
            return;
        }

        ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (detector.getCurrentSpan() > 120 && detector.getTimeDelta() > 200) {
                    if (detector.getCurrentSpan() - detector.getPreviousSpan() < -1) {
                        Toast.makeText(MainActivity.this, "Less than 1", Toast.LENGTH_SHORT).show();
                        GalleryAdapter.spans++;
                        if(GalleryAdapter.spans > GalleryAdapter.MAX_SPANS)
                            GalleryAdapter.spans = GalleryAdapter.MAX_SPANS;
                        refreshAdapter();
                        return true;
                    } else if (detector.getCurrentSpan() - detector.getPreviousSpan() > 1) {
                        Toast.makeText(MainActivity.this, "Greater than 1", Toast.LENGTH_SHORT).show();
                        GalleryAdapter.spans--;
                        if(GalleryAdapter.spans < GalleryAdapter.MIN_SPANS)
                            GalleryAdapter.spans = GalleryAdapter.MIN_SPANS;
                        refreshAdapter();
                        return true;
                    }
                }
                return false;
            }
        });

        mainGalleryView.setOnTouchListener( (v,me) ->{
            mScaleGestureDetector.onTouchEvent(me);
            return false;
        });

        mainGalleryView.setAdapter(ga[0]);
        mainGalleryView.addOnScrollListener(glideScrollListener);
        fastScroller.setViewProvider(new CustomScrollerViewProvider());
        fastScroller.setRecyclerView(mainGalleryView);
    }

    RelativeLayout rl;
    public int screenH, screenW;

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
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        rl = findViewById(R.id.rootLayout);
        inflater = LayoutInflater.from(this);
        GlideR = Glide.with(MainActivity.this);
        DisplayMetrics displayMetrics = getDisplaySizes(this);
        screenH = displayMetrics.heightPixels;
        screenW = displayMetrics.widthPixels;
        checkStoragePermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GalleryAdapter.instance = null;
    }

    @Override
    public void onBackPressed() {
        if(GalleryAdapter.instance != null && GalleryAdapter.instance.multiselect)
            exitMultiSelectMode();
        else super.onBackPressed();
    }

    public final DialogInterface.OnClickListener alertDialogNoOpsChoice = (dialog, which) -> {};

    public void exitMultiSelectMode() {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle("Exit multi select mode? Current selection will be lost");
        bld.setNegativeButton("No", alertDialogNoOpsChoice);
        bld.setPositiveButton("Yes", (dialog, which) -> {
            GalleryAdapter.instance.multiselect = false;
            GalleryAdapter.instance.clearSelection();
            getSupportActionBar().hide();
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
                        refreshAdapter();
                        Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                    });
                }).start());
        AlertDialog alertDialog = bld.create();
        alertDialog.show();
    }
}