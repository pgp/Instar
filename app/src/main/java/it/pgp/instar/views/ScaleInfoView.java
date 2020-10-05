package it.pgp.instar.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import it.pgp.instar.adapters.GalleryAdapter;

public class ScaleInfoView extends View {

    private int height, width = 0;
    private int padding = 0;
    private int fontSize = 0;
    private int numeralSpacing = 0;
    private int radius = 0;
    private Paint paint;
    private boolean isInit;
    private int externalRadius;

    public int currentAngle = 0;

    public ScaleInfoView(Context context) {
        super(context);
    }

    public ScaleInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        height = getHeight();
        width = getWidth();
        padding = numeralSpacing + 50;
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20,
                getResources().getDisplayMetrics());
        int min = Math.min(height, width);
        radius = min / 2 - padding;
        externalRadius = radius + padding - 10;
        paint = new Paint();
        isInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) initView();
        drawCircle(canvas);
        drawHand(canvas, (float)currentAngle);
        drawNumeral(canvas);
    }

    private void drawHand(Canvas canvas, double loc) {
        double angle = Math.PI * loc / 30 - Math.PI / 2;
        canvas.drawCircle(
                (float) (width / 2 + Math.cos(angle) * externalRadius),
                (float) (height / 2 + Math.sin(angle) * externalRadius),
                11,
                paint);
    }

    private void drawCircle(Canvas canvas) {
        paint.reset();
        paint.setColor(getResources().getColor(android.R.color.black));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, height / 2, externalRadius, paint);
    }

    private void drawNumeral(Canvas canvas) {
        paint.setTextSize(fontSize);
        String tmp = getTargetSpans() +"";
        canvas.drawText(tmp, width / 2, height / 2, paint);
    }

    public int getTargetSpans() {
        int deltaSpans = currentAngle / 10;
        int targetSpans = GalleryAdapter.spans - deltaSpans;
        if(targetSpans < GalleryAdapter.MIN_SPANS)
            targetSpans = GalleryAdapter.MIN_SPANS;
        else if(targetSpans > GalleryAdapter.MAX_SPANS)
            targetSpans = GalleryAdapter.MAX_SPANS;
        return targetSpans;
    }

}