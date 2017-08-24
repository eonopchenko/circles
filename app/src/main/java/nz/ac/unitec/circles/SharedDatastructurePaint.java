package nz.ac.unitec.circles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
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

    private enum ColorThreadState {
        IDLE,
        STARTED,
        ACTIVE,
        COMPLETED
    }

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


        public float getCenterX() {
            return centerX;
        }

        public float getCenterY() {
            return centerY;
        }

        public int getFillColor() {
            return fillColor;
        }

        public void setFillColor(int fillColor) {
            this.fillColor = fillColor;
        }
    }

    private final List<ColorChangedEventListener> colorChangedListeners = new ArrayList<>();
    private final ArrayList<Sketch> sketchList = new ArrayList<>();
    private final ArrayList<Sketch> undoneSketchList = new ArrayList<>();
    private final Paint paint = new Paint();
    private final Random random = new Random();
    ColorThreadState colorThreadState = ColorThreadState.IDLE;

    public SharedDatastructurePaint(Context context) {
        super(context);
        init();
    }

    public SharedDatastructurePaint(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SharedDatastructurePaint(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(this);

        final Handler h = new Handler();

        Thread t = new Thread(new Runnable() {
            public void run() {
                long time = 0;
                while(true) {
                    switch (colorThreadState) {
                        case IDLE: {
                            break;
                        }

                        case STARTED: {
                            time = SystemClock.currentThreadTimeMillis();
                            colorThreadState = ColorThreadState.ACTIVE;
                            break;
                        }

                        case ACTIVE: {
                            if (SystemClock.currentThreadTimeMillis() - time >= 1000) {
                                colorThreadState = ColorThreadState.COMPLETED;
                            }
                            break;
                        }

                        case COMPLETED: {
                            h.post(updateColor);
                            colorThreadState = ColorThreadState.STARTED;
                            break;
                        }
                    }
                }
            }
        });
        t.start();
    }

    Runnable updateColor = new Runnable() {
        public void run() {
            Sketch s = sketchList.get(sketchList.size() - 1);
            if (s instanceof CircleSketch) {
                int color = random.nextInt();
                fillColorChanged(color);
                ((CircleSketch) s).setFillColor(color);
                invalidate();
            }
        }
    };

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

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                colorThreadState = ColorThreadState.STARTED;
                int color = random.nextInt();
                fillColorChanged(color);
                sketchList.add(new CircleSketch(touchX, touchY, color));
                break;

            case MotionEvent.ACTION_UP:
                colorThreadState = ColorThreadState.IDLE;
                break;
        }

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
