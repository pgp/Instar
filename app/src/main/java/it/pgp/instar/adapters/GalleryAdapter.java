package it.pgp.instar.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import it.pgp.instar.R;

public class GalleryAdapter extends ArrayAdapter<String> {

    public static class GalleryItemViewHolder {
        ImageView imageView;
        GalleryItemViewHolder(ImageView imageView) {
            this.imageView = imageView;
        }
    }

    public final Activity activity;
    public final List<String> objects;
    protected LayoutInflater inflater;
    protected final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    private GalleryAdapter(@NonNull Activity activity, @NonNull List<String> objects) {
        super(activity, android.R.layout.simple_list_item_1, objects);
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

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        String item = getItem(position);
        ImageView imageView;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.single_image, null);
            imageView = convertView.findViewById(R.id.img1);
            convertView.setTag(new GalleryItemViewHolder(imageView));
        }
        else {
            GalleryItemViewHolder viewHolder = (GalleryItemViewHolder) convertView.getTag();
            imageView = viewHolder.imageView;
        }

        Glide
                .with(activity)
                .load(new File(item).getAbsolutePath())
                .centerCrop()
                .apply(new RequestOptions().override(250, 250))
                //.placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .into((ImageView)convertView);

        return convertView;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

}
