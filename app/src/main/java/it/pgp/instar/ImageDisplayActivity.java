package it.pgp.instar;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

import it.pgp.instar.views.ZoomableImageView;

public class ImageDisplayActivity extends Activity {

    String filepath;
    TextView filepath1;

    final AtomicBoolean fullScreen = new AtomicBoolean(false);

    public static final int fullScreenVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    int defaultUIVisibility;
    Window window;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        filepath1 = findViewById(R.id.filepath1);
        filepath = getIntent().getStringExtra("IMG_PATH");
        filepath1.setText(filepath);
        window = getWindow();
        defaultUIVisibility = window.getDecorView().getSystemUiVisibility();

        ZoomableImageView ziv1 = findViewById(R.id.ziv1);
        ziv1.setImageBitmap(BitmapFactory.decodeFile(filepath));
        ziv1.setOnClickListener(v->{
            filepath1.setVisibility(filepath1.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
            if(!fullScreen.get()) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                window.getDecorView().setSystemUiVisibility(fullScreenVisibility);
            }
            else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                window.getDecorView().setSystemUiVisibility(defaultUIVisibility);
            }
            fullScreen.set(!fullScreen.get());
        });

    }
}
