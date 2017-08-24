package nz.ac.unitec.circles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by eugene on 24/08/2017.
 */

public class SharedDatastructurePaint extends View implements View.OnTouchListener {

    private abstract class Sketch {
    }

    private class CircleSketch extends Sketch {

        private float centerX;
        private float centerY;
        private int fillColor;

        CircleSketch(float centerX, float centerY, int fillColor) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.fillColor = fillColor;
        }

        public int getFillColor() {
            return fillColor;
        }

        public float getCenterX() {
            return centerX;
        }

        public float getCenterY() {
            return centerY;
        }
    }

    private final List<ColorChangedEventListener> colorChangedListeners = new ArrayList<>();
    private final ArrayList<Sketch> sketchList = new ArrayList<>();
    private final ArrayList<Sketch> undoneSketchList = new ArrayList<>();
    private final Paint paint = new Paint();
    private final Random random = new Random();

    public SharedDatastructurePaint(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public SharedDatastructurePaint(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public SharedDatastructurePaint(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Sketch s : sketchList) {
            if (s instanceof CircleSketch) {
                CircleSketch circle = (CircleSketch) s;
                paint.setColor(circle.getFillColor());
                canvas.drawCircle(circle.getCenterX(), circle.getCenterY(), 30, paint);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int color = random.nextInt();
        fillColorChanged(color);
        sketchList.add(new CircleSketch(event.getX(), event.getY(), color));
        invalidate();
        return true;
    }

    public void setColorChangedEventListener(ColorChangedEventListener listener) {
        this.colorChangedListeners.add(listener);
    }

    private void fillColorChanged(int color) {
        for (ColorChangedEventListener listener : colorChangedListeners) {
            listener.onFillColorChanged(color);
        }
    }

    public void Undo() {

        int size = sketchList.size();

        if ((size > 0) && (undoneSketchList.size() < 10)) {
            undoneSketchList.add(sketchList.get(size - 1));
            sketchList.remove(size - 1);
        }

        invalidate();
    }

    public void Redo() {

        int size = undoneSketchList.size();

        if (size > 0) {
            sketchList.add(undoneSketchList.get(size - 1));
            undoneSketchList.remove(size - 1);
        }

        invalidate();
    }

    public void Clear() {

        undoneSketchList.clear();

        int size = sketchList.size();

        for(int index = size >= 10 ? size - 10 : 0; index < size; index++) {
            undoneSketchList.add(sketchList.get(index));
        }

        sketchList.clear();

        invalidate();
    }
}
