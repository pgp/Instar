package it.pgp.instar.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.pgp.instar.ImageDisplayActivity;
import it.pgp.instar.MainActivity;
import it.pgp.instar.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryItemViewHolder> implements SectionTitleProvider {

    public static GalleryAdapter instance;

    public static int spans = 5;
    public static final int MIN_SPANS = 3;
    public static final int MAX_SPANS = 7;
    public final String basePath;

    public int overridePx;

    public final Drawable[] currentPlaceholders = {null,null};

    public boolean multiselect = false;
    public final AtomicInteger selectedItems = new AtomicInteger(0);

    @NonNull
    @Override
    public GalleryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_image, parent, false);
        return new GalleryItemViewHolder(view);
    }

    final DrawableCrossFadeFactory crossFadeFactory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    final DrawableTransitionOptions drawableTransitionOptions = new DrawableTransitionOptions().withCrossFade(crossFadeFactory);

    public void setPlaceholders() {
        currentPlaceholders[0] = new BitmapDrawable(
                activity.getResources(), Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(activity.getResources(), R.mipmap.placeholder1),
                overridePx, overridePx, false));
        currentPlaceholders[1] = new BitmapDrawable(
                activity.getResources(), Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(activity.getResources(), R.mipmap.placeholder2),
                overridePx, overridePx, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryItemViewHolder holder, int position) {
        GalleryItem item = objects.get(position);

        Glide
                .with(activity)
                .load(new File(item.filepath).getAbsolutePath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(currentPlaceholders[position%2])
                .override(overridePx,overridePx)
                .transition(drawableTransitionOptions)
                .into(holder.imageView);

        if(multiselect)
            holder.imageView.setAlpha(item.selected ? 0.5f : 1.0f);

        holder.bind(position,GalleryAdapter.this);
    }

    public GalleryItem getItem(int position) {
        return objects.get(position);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void clearSelection() {
        for(GalleryItem item : objects)
            item.selected = false;
        selectedItems.set(0);
        notifyDataSetChanged();
    }

    public void selectAll() {
        for(GalleryItem item : objects)
            item.selected = true;
        selectedItems.set(objects.size());
        notifyDataSetChanged();
    }

    public void invertSelection() {
        for(GalleryItem item : objects)
            item.toggleSelection();
        selectedItems.set(objects.size()-selectedItems.get());
        notifyDataSetChanged();
    }

    public void onGalleryItemClicked(int position) {
        if(!(activity instanceof MainActivity)) {
            ((ImageDisplayActivity)activity).evp1.setCurrentItem(position);
            ((ImageDisplayActivity)activity).miniGalleryRecyclerView.getLayoutManager().scrollToPosition(position);
        }
        else if(multiselect) {
            onGalleryItemLongClicked(position);
        }
        else {
            Intent intent = new Intent(activity, ImageDisplayActivity.class);
            intent.putExtra("IMG_POS", position);
            activity.startActivity(intent);
        }
    }

    public boolean onGalleryItemLongClicked(int position) {
        if(!(activity instanceof MainActivity)) return true;
        if(!multiselect) {
            multiselect = true;
            ((MainActivity)activity).toggleActionBar(false);
        }

        boolean targetSelectionStatus = objects.get(position).toggleSelection();
        if(targetSelectionStatus) selectedItems.incrementAndGet();
        else selectedItems.decrementAndGet();

        notifyDataSetChanged();
        return true;
    }

    @Override
    public String getSectionTitle(int position) {
        String filepath = objects.get(position).filepath;
        int slashIdx = filepath.lastIndexOf('/');
        if(slashIdx > 0 && filepath.length()>slashIdx)
            return filepath.substring(slashIdx+1,slashIdx+2);
        else return "";
    }

    public static class GalleryItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public GalleryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img1);
        }

        public void bind(int position, GalleryAdapter galleryAdapter) {
            itemView.setOnClickListener(v -> galleryAdapter.onGalleryItemClicked(position));
            itemView.setOnLongClickListener(v -> galleryAdapter.onGalleryItemLongClicked(position));
        }
    }

    public final AppCompatActivity activity;
    public final List<GalleryItem> objects;
    protected LayoutInflater inflater;

    private GalleryAdapter(@NonNull AppCompatActivity activity, @NonNull List<GalleryItem> objects, String basePath) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.objects = objects;
        this.basePath = basePath;
        DisplayMetrics dM = MainActivity.getDisplaySizes(activity);
        if(activity instanceof MainActivity) {
            MainActivity a = (MainActivity) activity;
            overridePx = a.current == MainActivity.GalleryOrientation.VERTICAL ?
                    dM.widthPixels / spans :
                    dM.heightPixels / spans
            ;
        }
        else {
            overridePx = dM.heightPixels / MAX_SPANS;
        }
        setPlaceholders();

        if(activity instanceof MainActivity)
            instance = this;
    }

    public static GalleryAdapter createAdapter(AppCompatActivity activity, String basePath) {
        File[] ff = new File(basePath).listFiles();
        if(ff == null) return null;
        List<GalleryItem> l = new ArrayList<>();
        for(File f: ff) l.add(new GalleryItem(f.getAbsolutePath()));
        return new GalleryAdapter(activity, l, basePath);
    }

    public static GalleryAdapter from(GalleryAdapter old) {
        return new GalleryAdapter(old.activity, old.objects, old.basePath);
    }
}
