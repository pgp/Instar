package it.pgp.instar.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.pgp.instar.ImageDisplayActivity;
import it.pgp.instar.MainActivity;
import it.pgp.instar.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryItemViewHolder> {

    public static GalleryAdapter instance;

    public static int spans = 5;
    public static final int MIN_SPANS = 3;
    public static final int MAX_SPANS = 7;

    public int overridePx;

    public final Drawable[] currentPlaceholders = {null,null};

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

        if(selectedItems.get() > 0)
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

    public void onGalleryItemClicked(int position) {
        if(selectedItems.get() > 0) { // multi select mode
            onGalleryItemLongClicked(position);
        }
        else {
            Intent intent = new Intent(activity, ImageDisplayActivity.class);
            intent.putExtra("IMG_POS", position);
            activity.startActivity(intent);
        }
    }

    public boolean onGalleryItemLongClicked(int position) {
        boolean targetSelectionStatus = objects.get(position).toggleSelection();
        if(targetSelectionStatus) selectedItems.incrementAndGet();
        else selectedItems.decrementAndGet();
        Toast.makeText(activity, "Long click position "+position, Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
        return true;
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

    public final MainActivity activity;
    public final List<GalleryItem> objects;
    protected LayoutInflater inflater;

    private GalleryAdapter(@NonNull MainActivity activity, @NonNull List<GalleryItem> objects) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.objects = objects;
        overridePx = activity.current == MainActivity.GalleryOrientation.VERTICAL ?
                activity.screenW / spans :
                activity.screenH / spans
        ;
        setPlaceholders();
        instance = this;
    }

    public static GalleryAdapter createAdapter(MainActivity activity, String basePath) {
        File[] ff = new File(basePath).listFiles();
        if(ff == null) return null;
        List<GalleryItem> l = new ArrayList<>();
        for(File f: ff) l.add(new GalleryItem(f.getAbsolutePath()));
        return new GalleryAdapter(activity, l);
    }
}
