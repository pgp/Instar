package it.pgp.instar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import it.pgp.instar.adapters.GalleryAdapter;

public class MainActivity extends Activity {

    GridView mainGridView;
    final GalleryAdapter[] ga = {null};

    public final AdapterView.OnItemClickListener oicl = (parent, view, position, id) -> {
        GalleryAdapter a = (GalleryAdapter) mainGridView.getAdapter();
        String filepath = new File(a.getItem(position)).getAbsolutePath();
        Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
        intent.putExtra("IMG_PATH", filepath);
        startActivity(intent);
    };

    public void refreshAdapter() {
        ga[0] = GalleryAdapter.createAdapter(this,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
        if(ga[0] == null) {
            Toast.makeText(this, "Unable to access DCIM/Camera, exiting...", Toast.LENGTH_SHORT).show();
            finishAffinity();
            return;
        }

        mainGridView.setAdapter(ga[0]);
        mainGridView.setOnItemClickListener(oicl);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
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