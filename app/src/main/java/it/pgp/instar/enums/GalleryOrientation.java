package it.pgp.instar.enums;

import androidx.recyclerview.widget.GridLayoutManager;

import it.pgp.instar.R;

public enum GalleryOrientation {
    HORIZONTAL(GridLayoutManager.HORIZONTAL, R.layout.recyclerview_horizontal),
    VERTICAL(GridLayoutManager.VERTICAL, R.layout.recyclerview_vertical);

    public final int orientation;
    public final int layoutResId;

    GalleryOrientation(int orientation, int layoutResId) {
        this.orientation = orientation;
        this.layoutResId = layoutResId;
    }

    public GalleryOrientation next() {
        return (this==HORIZONTAL)?VERTICAL:HORIZONTAL;
    }
}