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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.ortiz.touchview.TouchImageView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import it.pgp.instar.adapters.ExtendedViewPager;
import it.pgp.instar.adapters.GalleryAdapter;
import it.pgp.instar.adapters.GalleryItem;

public class ImageDisplayActivity extends Activity {

    String filepath;
    TextView filepath1;
    public RecyclerView miniGalleryRecyclerView;
    FastScroller fastScroller;

    final AtomicBoolean fullScreen = new AtomicBoolean(false);

    private class TouchImageAdapter extends PagerAdapter {

        private final List<GalleryItem> objects;

        public TouchImageAdapter(List<GalleryItem> objects) {
            this.objects = objects;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TouchImageView v = new TouchImageView(ImageDisplayActivity.this);
            String filepath = objects.get(position).filepath;
            v.setImageBitmap(BitmapFactory.decodeFile(filepath));
            v.setOnClickListener(w->{
                for(View k : new View[]{filepath1,miniGalleryRecyclerView,fastScroller}) {
                    k.setVisibility(k.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
                }

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
            return objects.size();
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

    public ExtendedViewPager evp1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        filepath1 = findViewById(R.id.filepath1);
        evp1 = findViewById(R.id.evp1);
        evp1.setAdapter(new TouchImageAdapter(GalleryAdapter.instance.objects));
        int pos = getIntent().getIntExtra("IMG_POS",-1);
        evp1.setCurrentItem(pos);
        filepath1.setText(filepath);
        window = getWindow();
        defaultUIVisibility = window.getDecorView().getSystemUiVisibility();

        miniGalleryRecyclerView = findViewById(R.id.miniGalleryRecyclerView);
        miniGalleryRecyclerView.setLayoutManager(new GridLayoutManager(this,1,GridLayoutManager.HORIZONTAL,false));
        miniGalleryRecyclerView.setHasFixedSize(true);

        // TODO have to further modify adapter, short/long click listeners must not be shared, no multi select mode should be allowed here)
        // TODO add scroll on current position after click, on both viewpager <-> mini gallery
        // TODO onclick listener in mini gallery should not start a new activity, just re-use existing one
        miniGalleryRecyclerView.setAdapter(GalleryAdapter.createAdapter(this,GalleryAdapter.instance.basePath));
        fastScroller = findViewById(R.id.fastScroll);
        fastScroller.setRecyclerView(miniGalleryRecyclerView);
        miniGalleryRecyclerView.bringToFront();
        fastScroller.bringToFront();
        miniGalleryRecyclerView.getLayoutManager().scrollToPosition(pos);
    }
}
