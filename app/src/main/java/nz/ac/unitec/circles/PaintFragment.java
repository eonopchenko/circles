package nz.ac.unitec.circles;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by eugene on 24/08/2017.
 */

public class PaintFragment extends Fragment implements ColorChangedEventListener {

    View mView;
    ImageButton btnToolFillColor;

    @Override
    public void onFillColorChanged(int color) {
        btnToolFillColor.setBackgroundColor(color);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_paint, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SharedDatastructurePaint sharedPaint = (SharedDatastructurePaint)mView.findViewById(R.id.customView);
        final ImageButton btnToolUndo = (ImageButton) mView.findViewById(R.id.btnToolUndo);
        final ImageButton btnToolRedo = (ImageButton) mView.findViewById(R.id.btnToolRedo);
        final ImageButton btnToolClear = (ImageButton) mView.findViewById(R.id.btnToolClear);
        final TextView tvRadius = (TextView) mView.findViewById(R.id.tvRadius);
        final SeekBar sbWidthRadius = (SeekBar) mView.findViewById(R.id.sbRadius);
        btnToolFillColor = (ImageButton) mView.findViewById(R.id.btnToolFillColor);

        btnToolUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPaint.Undo();
            }
        });

        btnToolRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPaint.Redo();
            }
        });

        btnToolClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPaint.Clear();
            }
        });

        sbWidthRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRadius.setText(progress + "");
                sharedPaint.SetRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sharedPaint.setColorChangedEventListener(this);
    }
}
