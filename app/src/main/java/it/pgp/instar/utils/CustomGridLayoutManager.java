package it.pgp.instar.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.pgp.instar.enums.GalleryOrientation;

public class CustomGridLayoutManager extends GridLayoutManager {

    public final GalleryOrientation orientation;
    public boolean scrollEnabled = true;
    private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 1000;

    public CustomGridLayoutManager(Context context, int spanCount, boolean reverseLayout, GalleryOrientation o) {
        super(context, spanCount, o.orientation, reverseLayout);
        this.orientation = o;
    }

    @Override
    public boolean canScrollHorizontally() {
        return orientation == GalleryOrientation.HORIZONTAL && scrollEnabled && super.canScrollHorizontally();
    }

    @Override
    public boolean canScrollVertically() {
        return orientation == GalleryOrientation.VERTICAL && scrollEnabled && super.canScrollVertically();
    }

    public void lockScroll() {
        scrollEnabled = false;
    }

    public void unlockScroll() {
        scrollEnabled = true;
    }

    @Override
    protected void calculateExtraLayoutSpace(@NonNull RecyclerView.State state, @NonNull int[] extraLayoutSpace) {
        extraLayoutSpace[0] = DEFAULT_EXTRA_LAYOUT_SPACE;
        extraLayoutSpace[1] = DEFAULT_EXTRA_LAYOUT_SPACE;
    }
}
