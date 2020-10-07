package it.pgp.instar.utils;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

import it.pgp.instar.enums.GalleryOrientation;

public class CustomGridLayoutManager extends GridLayoutManager {

    public final GalleryOrientation orientation;
    public boolean scrollEnabled = true;

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
}
