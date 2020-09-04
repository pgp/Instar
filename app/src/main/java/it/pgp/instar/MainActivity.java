package it.pgp.instar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.futuremind.recyclerviewfastscroll.FastScroller;

import it.pgp.instar.adapters.GalleryAdapter;

public class MainActivity extends Activity {

    LayoutInflater inflater;
    RecyclerView mainGalleryView;
    FastScroller fastScroller;
    final GalleryAdapter[] ga = {null};

    enum GalleryOrientation {
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

    GalleryOrientation current = GalleryOrientation.HORIZONTAL;

//    public final AdapterView.OnItemClickListener oicl = (parent, view, position, id) -> {
//        GalleryAdapter a = (GalleryAdapter) mainGalleryView.getAdapter();
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
        rl.removeAllViews();
        inflater.inflate(current.layoutResId, rl, true);
        mainGalleryView = findViewById(R.id.mainGalleryView);
        fastScroller = findViewById(R.id.fastScroll);

        mainGalleryView.setLayoutManager(new GridLayoutManager(this, 5, current.orientation, false));

        ga[0] = GalleryAdapter.createAdapter(this,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
        if(ga[0] == null) {
            Toast.makeText(this, "Unable to access DCIM/Camera, exiting...", Toast.LENGTH_SHORT).show();
            finishAffinity();
            return;
        }

        mainGalleryView.setAdapter(ga[0]);
        fastScroller.setRecyclerView(mainGalleryView);
    }

    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        rl = findViewById(R.id.rootLayout);
        inflater = LayoutInflater.from(this);
        checkStoragePermissions();
    }

    public void reloadImgCache(View unused) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle("Recreate thumbnail cache?");
        bld.setNegativeButton("No", (dialog, which) -> {});
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