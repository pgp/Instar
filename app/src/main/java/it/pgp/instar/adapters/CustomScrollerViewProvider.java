package it.pgp.instar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.viewprovider.DefaultBubbleBehavior;
import com.futuremind.recyclerviewfastscroll.viewprovider.ScrollerViewProvider;
import com.futuremind.recyclerviewfastscroll.viewprovider.ViewBehavior;
import com.futuremind.recyclerviewfastscroll.viewprovider.VisibilityAnimationManager;

import it.pgp.instar.MainActivity;
import it.pgp.instar.R;

/**
 * Created by Michal on 05/08/16.
 */
public class CustomScrollerViewProvider extends ScrollerViewProvider {

    private TextView bubble;
    private View handle;

    @Override
    public View provideHandleView(ViewGroup container) {
        handle = new ImageView(getContext());
        ((ImageView)handle).setImageResource(
                getScroller().getOrientation()==MainActivity.GalleryOrientation.VERTICAL.orientation ?
                        R.mipmap.arrows_horizontal: R.mipmap.arrows_vertical); // FIXME why horizontal and vertical correctly work if exchanged?
        handle.setLayoutParams(new ViewGroup.LayoutParams(MainActivity.screenW/12, MainActivity.screenH/12)); // TODO decide whether dp or mm is better than relative size
//        Utils.setBackground(handle, drawCircle(dimen, dimen, ContextCompat.getColor(getContext(), R.color.colorPrimary)));
//        handle.setVisibility(View.INVISIBLE); // TODO maybe have to make handle public in order to make it visible in glideScrollListener on start dragging
        return handle;
    }

    @Override
    public View provideBubbleView(ViewGroup container) {
        bubble = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.fastscroll__default_bubble, container, false);
        return bubble;
    }

//    @Override
//    public View provideBubbleView(ViewGroup container) {
//        bubble = new TextView(getContext());
//        int dimen = 100;
//        bubble.setLayoutParams(new ViewGroup.LayoutParams(dimen, dimen));
//        Utils.setBackground(bubble, drawCircle(dimen, dimen, ContextCompat.getColor(getContext(), R.color.colorPrimary)));
////        bubble.setVisibility(View.INVISIBLE);
//        bubble.setGravity(Gravity.CENTER);
//        bubble.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
//        getScroller().addScrollerListener(new RecyclerViewScrollListener.ScrollerListener() {
//            @Override
//            public void onScroll(float relativePos) {
//                //Yeah, yeah, but we were so preoccupied with whether or not we could,
//                //that we didn't stop to think if we should.
//                bubble.setRotation(relativePos*360f);
//            }
//        });
//        return bubble;
//    }

    @Override
    public TextView provideBubbleTextView() {
        return bubble;
    }

    @Override
    public int getBubbleOffset() {
        return (int) (getScroller().isVertical() ? (float)handle.getHeight()/2f-(float)bubble.getHeight()/2f : (float)handle.getWidth()/2f-(float)bubble.getWidth()/2);
    }

    @Override
    protected ViewBehavior provideHandleBehavior() {
//        return new CustomHandleBehavior(
//                new VisibilityAnimationManager.Builder(handle)
//                        .withHideDelay(2000)
//                        .build(),
//                new CustomHandleBehavior.HandleAnimationManager.Builder(handle)
//                        .withGrabAnimator(R.animator.custom_grab)
//                        .withReleaseAnimator(R.animator.custom_release)
//                        .build()
//        );
        return null;
    }

    @Override
    protected ViewBehavior provideBubbleBehavior() {
        return new DefaultBubbleBehavior(new VisibilityAnimationManager.Builder(bubble).withHideDelay(0).build());
    }

//    private static ShapeDrawable drawCircle (int width, int height, int color) {
//        ShapeDrawable oval = new ShapeDrawable (new OvalShape());
//        oval.setIntrinsicHeight(height);
//        oval.setIntrinsicWidth(width);
//        oval.getPaint().setColor(color);
//        return oval;
//    }

}

