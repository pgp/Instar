package it.pgp.instar.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.ortiz.touchview.TouchImageView;

/**
 * Web source:
 * https://github.com/MikeOrtiz/TouchImageView/
 */

public class ExtendedViewPager extends ViewPager {
    public ExtendedViewPager(@NonNull Context context) {
        super(context);
    }

    public ExtendedViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof TouchImageView)
            return v.canScrollHorizontally(-dx);
        else return super.canScroll(v, checkV, dx, x, y);
    }
}
