package it.pgp.instar;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;

import it.pgp.instar.views.ZoomableImageView;

public class ImageDisplayActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        ZoomableImageView ziv1 = findViewById(R.id.ziv1);
        ziv1.setImageBitmap(BitmapFactory.decodeFile(getIntent().getStringExtra("IMG_PATH")));
    }
}
