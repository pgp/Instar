package it.pgp.instar.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Web source:
 * https://medium.com/@veeyikpong/adding-dividers-in-between-recyclerview-items-for-gridlayoutmanager-6ba48d13bf16
 * https://gist.github.com/nuovothoth/ed8f3952e90a5c4ed263ad1480382522
 */


/**
 * MiddleDividerItemDecoration is a [RecyclerView.ItemDecoration] that can be used as a divider
 * between items of a [LinearLayoutManager]. It supports both [.HORIZONTAL] and
 * [.VERTICAL] orientations.
 * It can also supports [.ALL], included both the horizontal and vertical. Mainly used for GridLayout.
 * <pre>
 * For normal usage with LinearLayout,
 * val mItemDecoration = MiddleDividerItemDecoration(context!!,DividerItemDecoration.VERTICAL)
 * For GridLayoutManager with inner decorations,
 * val mItemDecoration = MiddleDividerItemDecoration(context!!,MiddleDividerItemDecoration.ALL)
 * recyclerView.addItemDecoration(mItemDecoration);
 </pre> *
 */
public class MiddleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = MiddleDividerItemDecoration.class.getSimpleName();

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    public static final int ALL = 2;

    private Drawable mDivider;
    private int mOrientation;
    private Rect mBounds;

    /**
     * Creates a divider [RecyclerView.ItemDecoration] that can be used with a
     * [LinearLayoutManager].
     *
     * @param context Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be [.HORIZONTAL] or [.VERTICAL].
     */
    public MiddleDividerItemDecoration(Context context, int orientation) {
        mDivider = null;
        /**
         * Current orientation. Either [.HORIZONTAL] or [.VERTICAL].
         */
        mOrientation = 0;
        mBounds = new Rect();

        TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this " + "DividerItemDecoration. Please set that attribute all call setDrawable()");
        }
        a.recycle();
        setOrientation(orientation);

    }

    public void setDividerColor(int color) {
        if (mDivider != null) mDivider.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
    }

    /**
     * Sets the orientation for this divider. This should be called if
     * [RecyclerView.LayoutManager] changes orientation.
     *
     * @param orientation [.HORIZONTAL] or [.VERTICAL]
     */
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL && orientation != ALL) {
            throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    /**
     * Sets the [Drawable] for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    public void setDrawable(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        mDivider = drawable;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getLayoutManager() == null || mDivider == null) return;

        switch (mOrientation) {
            case ALL:
                drawVertical(c, parent);
                drawHorizontal(c, parent);
                break;
            case VERTICAL:
                drawVertical(c, parent);
                break;
            default:
                drawHorizontal(c, parent);
                break;
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();

        int left;
        int right;

        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight()-parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        int childCount = parent.getChildCount();

        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            int leftItems = childCount % ((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
            if(leftItems == 0) {
                leftItems = ((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
            }
            childCount -= leftItems;
        }

        for (int i=0 ; i<childCount-1 ;i++) {
            View child = parent.getChildAt(i);
            if (child == null) return;

            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            int top = bottom - mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }

        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();

        int top;
        int bottom;

        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth()-parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        int childCount = parent.getChildCount();
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            childCount = ((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
        }

        for (int i=0 ; i<childCount-1 ; i++) {
            View child = parent.getChildAt(i);
            if(child == null) return;
            int right = mBounds.right + Math.round(child.getTranslationX());
            int left = right - mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }

        canvas.restore();
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mDivider == null) {
            outRect.set(0,0,0,0);
            return;
        }

        if (mOrientation == VERTICAL) {
            outRect.set(0,0,0,mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0,0,mDivider.getIntrinsicWidth(), 0);
        }
    }
}
