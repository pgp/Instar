package it.pgp.instar;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.ortiz.touchview.TouchImageView;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import it.pgp.instar.adapters.CustomScrollerViewProvider;
import it.pgp.instar.adapters.ExtendedViewPager;
import it.pgp.instar.adapters.GalleryAdapter;
import it.pgp.instar.adapters.GalleryItem;

public class ImageDisplayActivity extends AppCompatActivity {

    String filepath;
    TextView filepath1;
    public RecyclerView miniGalleryRecyclerView;
    FastScroller fastScroller;

    final AtomicBoolean fullScreen = new AtomicBoolean(false);
    private LinearLayoutManager lm;

    private class TouchImageAdapter extends PagerAdapter {

        private final List<GalleryItem> objects;

        public TouchImageAdapter(List<GalleryItem> objects) {
            this.objects = objects;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TouchImageView v = new TouchImageView(ImageDisplayActivity.this);
            File f = objects.get(position).getFile();
            if(f.isDirectory()) {
                container.addView(v, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                return v;
            }

            String filepath = objects.get(position).filepath;
            v.setImageBitmap(BitmapFactory.decodeFile(filepath));
            v.setOnClickListener(w->{
                for(View k : new View[]{filepath1,miniGalleryRecyclerView}) {
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
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
        lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        miniGalleryRecyclerView.setLayoutManager(lm);
        miniGalleryRecyclerView.setHasFixedSize(true);

        miniGalleryRecyclerView.setAdapter(GalleryAdapter.createAdapter(this,GalleryAdapter.instance.basePath));
        fastScroller = findViewById(R.id.fastScroll);
        fastScroller.setViewProvider(new CustomScrollerViewProvider());
        fastScroller.setRecyclerView(miniGalleryRecyclerView);
//        miniGalleryRecyclerView.bringToFront();
//        fastScroller.bringToFront();
        evp1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                lm.scrollToPosition(position+2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
