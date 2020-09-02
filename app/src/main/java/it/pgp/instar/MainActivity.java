package it.pgp.instar;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import it.pgp.instar.adapters.GalleryAdapter;

public class MainActivity extends Activity {

    RecyclerView mainGridView;
    final GalleryAdapter[] ga = {null};

    enum GalleryOrientation {
        HORIZONTAL(GridLayoutManager.HORIZONTAL),
        VERTICAL(GridLayoutManager.VERTICAL);

        public final int orientation;

        GalleryOrientation(int orientation) {
            this.orientation = orientation;
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
        else onCreateAfterPermOK();
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
            onCreateAfterPermOK();
        }
    }

    public void swicthGalleryOrientation(View unused) {
        current = current.next();
        refreshAdapter();
    }

    public void refreshAdapter() {
        mainGridView.setLayoutManager(new GridLayoutManager(this, 5, current.orientation, false));

        ga[0] = GalleryAdapter.createAdapter(this,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
        if(ga[0] == null) {
            Toast.makeText(this, "Unable to access DCIM/Camera, exiting...", Toast.LENGTH_SHORT).show();
            finishAffinity();
            return;
        }

        mainGridView.setAdapter(ga[0]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        checkStoragePermissions();
    }

    public void onCreateAfterPermOK() {
        mainGridView = findViewById(R.id.mainGridView);
        refreshAdapter();
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