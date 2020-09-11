package it.pgp.instar;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.ortiz.touchview.TouchImageView;

import java.util.concurrent.atomic.AtomicBoolean;

import it.pgp.instar.adapters.ExtendedViewPager;
import it.pgp.instar.adapters.GalleryAdapter;

public class ImageDisplayActivity extends Activity {

    String filepath;
    int position;
    TextView filepath1;

    final AtomicBoolean fullScreen = new AtomicBoolean(false);

    private class TouchImageAdapter extends PagerAdapter {

        private final GalleryAdapter galleryAdapter;

        public TouchImageAdapter(GalleryAdapter galleryAdapter) {
            this.galleryAdapter = galleryAdapter;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TouchImageView v = new TouchImageView(ImageDisplayActivity.this);
            String filepath = galleryAdapter.getItem(position);
            v.setImageBitmap(BitmapFactory.decodeFile(filepath));
            v.setOnClickListener(w->{
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
            container.addView(v, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            return v;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return galleryAdapter.getItemCount();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    public static final int fullScreenVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    int defaultUIVisibility;
    Window window;

    protected ExtendedViewPager evp1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        filepath1 = findViewById(R.id.filepath1);
        evp1 = findViewById(R.id.evp1);
        evp1.setAdapter(new TouchImageAdapter(GalleryAdapter.instance));
        position = getIntent().getIntExtra("IMG_POS",-1);
        filepath1.setText(filepath);
        window = getWindow();
        defaultUIVisibility = window.getDecorView().getSystemUiVisibility();
    }
}
