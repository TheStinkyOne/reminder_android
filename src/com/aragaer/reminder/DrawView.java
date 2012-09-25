package com.aragaer.reminder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

class DrawView extends View implements OnTouchListener {
    static final int BITMAP_SIZE = 500;

    static Paint p_grid = new Paint(0x7), p = new Paint(0x7);
    int size;
    Bitmap bmp;
    Canvas c;
    Matrix m = new Matrix();

    static {
        p_grid.setColor(Color.LTGRAY);
        p.setColor(Color.WHITE);
    }

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
    }

    public void reset() {
        bmp = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Config.ARGB_8888);
        c = new Canvas(bmp);
    }

    public Bitmap getBitmap() {
        return bmp;
    }

    protected void onMeasure(int w, int h) {
        size = Math.min(MeasureSpec.getSize(w), MeasureSpec.getSize(h)) - 1;
        size -= size % 4;
        p.setStrokeWidth(BITMAP_SIZE/20);
        m.setScale(1f*size/BITMAP_SIZE, 1f*size/BITMAP_SIZE);

        super.onMeasure(size+1, size+1);
        setMeasuredDimension(size+1, size+1);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed)
            return;
        int px = (right - left - size) / 2, py = (bottom - top - size) / 2;
        super.onLayout(changed, px, py, px + size, py + size);
    }

    public void onDraw(Canvas canvas) {
        final int cell = size / 4;
        for (int i = 0; i <= 4; i++) {
            canvas.drawLine(0, cell * i, size, cell * i, p_grid);
            canvas.drawLine(cell * i, 0, cell * i, size, p_grid);
        }
        if (bmp != null)
            canvas.drawBitmap(bmp, m, p);
    }

    float x, y;
    public boolean onTouch(View view, MotionEvent event) {
        final float scale = 1f*BITMAP_SIZE / size;
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            x = event.getX() * scale;
            y = event.getY() * scale;
            c.drawCircle(x, y, BITMAP_SIZE/40, p);
            break;
        case MotionEvent.ACTION_MOVE:
            float ox = x;
            float oy = y;
            x = event.getX() * scale;
            y = event.getY() * scale;
            c.drawLine(ox, oy, x, y, p);
            c.drawCircle(x, y, BITMAP_SIZE/40, p);
            break;
        default:
            return false;
        }
        invalidate();
        return true;
    }
}