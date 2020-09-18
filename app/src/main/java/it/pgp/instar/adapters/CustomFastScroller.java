package it.pgp.instar.adapters;

import android.content.Context;
import android.util.AttributeSet;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.futuremind.recyclerviewfastscroll.viewprovider.ScrollerViewProvider;

public class CustomFastScroller extends FastScroller {
    public CustomFastScroller(Context context) {
        super(context);
    }

    public CustomFastScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFastScroller(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setViewProvider(ScrollerViewProvider viewProvider) {
        super.setViewProvider(new CustomScrollerViewProvider());
    }
}
