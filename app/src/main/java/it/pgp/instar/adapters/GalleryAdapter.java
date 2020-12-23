package it.pgp.instar.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.pgp.instar.ImageDisplayActivity;
import it.pgp.instar.MainActivity;
import it.pgp.instar.enums.GalleryOrientation;
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

    public ResizeOptions resizeOptions;

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

        GlideR
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

    public void deleteSelection() {
        AlertDialog.Builder bld = new AlertDialog.Builder(activity);
        int N = selectedItems.get();
        bld.setTitle("Delete "+N+" items?");
        bld.setNegativeButton("No", MainActivity.alertDialogNoOpsChoice);
        bld.setPositiveButton("Yes", (dialog, which) -> {
            int actuallyDeletedItems = 0;
            for(int i=0;i<objects.size();i++) {
                GalleryItem item = objects.get(i);
                if (item.selected) {
                    if (item.getFile().delete()) actuallyDeletedItems++;
                }
            }
            if(actuallyDeletedItems != N)
                Toast.makeText(activity, "Items deleted: "+actuallyDeletedItems+", unable to delete some items", Toast.LENGTH_SHORT).show();
            if(actuallyDeletedItems > 0)
                reloadFromFiles();
        });
        AlertDialog alertDialog = bld.create();
        alertDialog.show();
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
    public final RequestManager GlideR;
    public final List<GalleryItem> objects;
    protected LayoutInflater inflater;

    // this workaround is needed because, in DisplayManager.DisplayListener onDisplayChanged,
    // we have old real metrics, not current ones (that is, width and height are inverted if rotation diff is not 180°)
    private int[] getActualScreenDimsWrtOrientation(@NonNull AppCompatActivity activity) {
        DisplayMetrics dM = MainActivity.getDisplaySizes(activity);
        int currentRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        int[] whr = new int[3]; // width, heigth, rotation
        int[] wh = new int[2]; // width, heigth, rotation
        switch(currentRotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180: // portrait modes, width is min, heigth is max
                wh[0] = Math.min(dM.widthPixels, dM.heightPixels);
                wh[1] = Math.max(dM.widthPixels, dM.heightPixels);
                break;
            case Surface.ROTATION_90: // landscape modes, width is max, heigth is min
            case Surface.ROTATION_270:
                wh[0] = Math.max(dM.widthPixels, dM.heightPixels);
                wh[1] = Math.min(dM.widthPixels, dM.heightPixels);
                break;

        }
//        whr[2] = currentRotation;
        return wh;
    }

    private GalleryAdapter(@NonNull AppCompatActivity activity, @NonNull List<GalleryItem> objects, String basePath) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.GlideR = Glide.with(activity);
        this.objects = objects;
        this.basePath = basePath;
        int[] wh = getActualScreenDimsWrtOrientation(activity);
        if(activity instanceof MainActivity) {
            MainActivity a = (MainActivity) activity;
            overridePx = a.currentOrientation == GalleryOrientation.VERTICAL ?
                    wh[0] / spans :
                    wh[1] / spans
            ;
        }
        else {
            overridePx = wh[1] / MAX_SPANS;
        }
        setPlaceholders();

        if(activity instanceof MainActivity)
            instance = this;
    }

    private void reloadFromFiles() {
        objects.clear();
        selectedItems.set(0);
        File[] ff = new File(basePath).listFiles();
        if(ff == null) {
            Toast.makeText(activity, "Base path no longer readable, exiting", Toast.LENGTH_SHORT).show();
            activity.finishAffinity();
            return;
        }
        for(File f: ff) objects.add(new GalleryItem(f.getAbsolutePath()));
        notifyDataSetChanged();
    }

    public static GalleryAdapter createAdapter(AppCompatActivity activity, String basePath) {
        File[] ff = new File(basePath).listFiles();
        if(ff == null) return null;
        List<GalleryItem> l = new ArrayList<>();
        for(File f: ff) l.add(new GalleryItem(f.getAbsolutePath()));
        return new GalleryAdapter(activity, l, basePath);
    }

    public static GalleryAdapter from(GalleryAdapter old) {
        GalleryAdapter ga = new GalleryAdapter(old.activity, old.objects, old.basePath);
        ga.multiselect = old.multiselect;
        ga.selectedItems.set(old.selectedItems.get());
        return ga;
    }
}
