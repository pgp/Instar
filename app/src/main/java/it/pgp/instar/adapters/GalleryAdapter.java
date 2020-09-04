package it.pgp.instar.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.pgp.instar.ImageDisplayActivity;
import it.pgp.instar.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryItemViewHolder> {

    @NonNull
    @Override
    public GalleryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_image, parent, false);
        return new GalleryItemViewHolder(view);
    }

    final DrawableCrossFadeFactory crossFadeFactory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    final DrawableTransitionOptions drawableTransitionOptions = new DrawableTransitionOptions().withCrossFade(crossFadeFactory);

    @Override
    public void onBindViewHolder(@NonNull GalleryItemViewHolder holder, int position) {
        Glide
                .with(activity)
                .load(new File(objects.get(position)).getAbsolutePath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.square)
                .override(250,250)
                .transition(drawableTransitionOptions)
                .into(holder.imageView);

        holder.bind(getItem(position),GalleryAdapter.this);
    }

    public String getItem(int position) {
        return objects.get(position);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void onGalleryItemClicked(String filepath) {
        Intent intent = new Intent(activity, ImageDisplayActivity.class);
        intent.putExtra("IMG_PATH", filepath);
        activity.startActivity(intent);
    }

    public static class GalleryItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public GalleryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img1);
        }

        public void bind(String item, GalleryAdapter galleryAdapter) {
            itemView.setOnClickListener(v -> galleryAdapter.onGalleryItemClicked(item));
        }
    }

    public final Activity activity;
    public final List<String> objects;
    protected LayoutInflater inflater;

    private GalleryAdapter(@NonNull Activity activity, @NonNull List<String> objects) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.objects = objects;
    }

    public static GalleryAdapter createAdapter(Activity activity, String basePath) {
        File[] ff = new File(basePath).listFiles();
        if(ff == null) return null;
        List<String> l = new ArrayList<>();
        for(File f: ff) l.add(f.getAbsolutePath());
        return new GalleryAdapter(activity, l);
    }
}
