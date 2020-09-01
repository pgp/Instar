package it.pgp.instar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;

import it.pgp.instar.adapters.GalleryAdapter;

public class MainActivity extends Activity {

    GridView mainGridView;

    public final AdapterView.OnItemClickListener oicl = (parent, view, position, id) -> {
        GalleryAdapter a = (GalleryAdapter) mainGridView.getAdapter();
        String filepath = new File(a.getItem(position)).getAbsolutePath();
        Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
        intent.putExtra("IMG_PATH", filepath);
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mainGridView = findViewById(R.id.mainGridView);
        GalleryAdapter ga = GalleryAdapter.createAdapter(this,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
        if(ga == null) {
            Toast.makeText(this, "Unable to access DCIM/Camera, exiting...", Toast.LENGTH_SHORT).show();
            finishAffinity();
            return;
        }
        mainGridView.setAdapter(ga);
        mainGridView.setOnItemClickListener(oicl);
    }
}