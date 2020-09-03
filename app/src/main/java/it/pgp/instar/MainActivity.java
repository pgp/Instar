package it.pgp.instar;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.futuremind.recyclerviewfastscroll.FastScroller;

import it.pgp.instar.adapters.GalleryAdapter;

public class MainActivity extends Activity {

    RecyclerView mainGridView;
    FastScroller fastScroller;
    final GalleryAdapter[] ga = {null};

    enum GalleryOrientation {
        HORIZONTAL(GridLayoutManager.HORIZONTAL),
        VERTICAL(GridLayoutManager.VERTICAL);

        public final int orientation;

        GalleryOrientation(int orientation) {
            this.orientation = orientation;
        }

        public RelativeLayout.LayoutParams getLayoutParams() {
            if(this==HORIZONTAL) {
                RelativeLayout.LayoutParams lh = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lh.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                return lh;
            }
            else {
                RelativeLayout.LayoutParams lv = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                lv.addRule(RelativeLayout.ALIGN_PARENT_END);
                return lv;
            }
        }

        public GalleryOrientation next() {
            return (this==HORIZONTAL)?VERTICAL:HORIZONTAL;
        }
    }

    GalleryOrientation current = GalleryOrientation.HORIZONTAL;

//    public final AdapterView.OnItemClickListener oicl = (parent, view, position, id) -> {
//        GalleryAdapter a = (GalleryAdapter) mainGridView.getAdapter();
//        String filepath = new File(a.getItem(position)).getAbsolutePath();
//        Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
//        intent.putExtra("IMG_PATH", filepath);
//        startActivity(intent);
//    };

    public static final int STORAGE_PERM_ID = 123;
    private static final int numberOfColumns = 3;

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
//        mainGridView = findViewById(R.id.mainGridView);
        if(mainGridView != null) rl.removeView(mainGridView);
        mainGridView = new RecyclerView(this);
        mainGridView.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        if(fastScroller != null) rl.removeView(fastScroller);
        fastScroller = new FastScroller(this);

        mainGridView.setLayoutManager(new GridLayoutManager(this, 5, current.orientation, false));

        ga[0] = GalleryAdapter.createAdapter(this,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
        if(ga[0] == null) {
            Toast.makeText(this, "Unable to access DCIM/Camera, exiting...", Toast.LENGTH_SHORT).show();
            finishAffinity();
            return;
        }

        fastScroller.setOrientation(current==GalleryOrientation.VERTICAL?
                LinearLayout.VERTICAL:LinearLayout.HORIZONTAL);
        fastScroller.setLayoutParams(current.getLayoutParams());
        mainGridView.setAdapter(ga[0]);
        fastScroller.setRecyclerView(mainGridView);

        rl.addView(mainGridView);
        rl.addView(fastScroller);
        findViewById(R.id.reloadImgCacheButton).bringToFront();
        findViewById(R.id.switchButton).bringToFront();
    }

    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        rl = findViewById(R.id.rootLayout);
        checkStoragePermissions();
    }

    public void reloadImgCache(View unused) {
        new Thread(()-> {
            Glide.get(MainActivity.this).clearDiskCache();
            runOnUiThread(()->{
                refreshAdapter();
                Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}