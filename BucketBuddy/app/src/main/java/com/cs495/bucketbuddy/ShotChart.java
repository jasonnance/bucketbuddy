package com.cs495.bucketbuddy;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShotChart extends Fragment {

    private View v;
    private StatEntity entity;

    public ShotChart() {}

    public static ShotChart newInstance(int sectionNumber) {
        ShotChart fragment = new ShotChart();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_shot_chart, container, false);
        long entityId = getActivity().getIntent().getExtras().getLong("entityId");
        entity = new DatabaseHelper(getActivity(), null, null, 1).getStatEntity(entityId);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawShots();
    }

    private void drawShots() {
        ImageView court = (ImageView) v.findViewById(R.id.shotchart_court);
        court.setDrawingCacheEnabled(true);
        court.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        /*
        float pxWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                court.getMeasuredWidth(),
                getResources().getDisplayMetrics());
        float pxHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                court.getMeasuredHeight(),
                getResources().getDisplayMetrics());
                */
        court.layout(0, 0, court.getMeasuredWidth(), court.getMeasuredHeight());

        court.buildDrawingCache(true);
        Bitmap courtBitmap = court.getDrawingCache();

        Bitmap overlayBitmap = Bitmap.createBitmap(courtBitmap.getWidth(), courtBitmap.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas overlayCanvas = new Canvas(overlayBitmap);

        Paint madeShotPaint = new Paint();
        madeShotPaint.setColor(Color.GREEN);
        Paint missedShotPaint = new Paint();
        missedShotPaint.setColor(Color.RED);

        overlayCanvas.drawBitmap(courtBitmap,0,0,null);
        ArrayList<ArrayList<Object>> careerShots = (ArrayList<ArrayList<Object>>) entity.getCareerStat("shotCoords");
        for (ArrayList<Object> seasonShots : careerShots) {
            for (Object shots : seasonShots) {
                ArrayList<Shot> gameShots = (ArrayList<Shot>) shots;
                for (Shot shot : gameShots) {
                    // Mystery scaling factor to account for the difference in game screen
                    // and view stats... no idea why this is needed
                    double scalingFactor = 1.33;
                    int x = (int) Math.round(shot.getX() * scalingFactor);
                    int y = (int) Math.round(shot.getY() * scalingFactor);
                    if (shot.getMade()) {
                        overlayCanvas.drawCircle(x, y, 30, madeShotPaint);
                    }
                    else {
                        overlayCanvas.drawCircle(x,y,30,missedShotPaint);
                    }
                }
            }
        }

        court.setImageDrawable(new BitmapDrawable(getResources(), overlayBitmap));
    }

}
