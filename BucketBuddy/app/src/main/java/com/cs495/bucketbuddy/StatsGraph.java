package com.cs495.bucketbuddy;

import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;


public class StatsGraph extends Fragment {
    private String selectedStat = "points";
    private StatEntity entity;
    private View v;

    public StatsGraph() {}

    public static StatsGraph newInstance(int sectionNumber) {
        StatsGraph fragment = new StatsGraph();
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

        v = inflater.inflate(R.layout.activity_view_entity_stats, container, false);
        long entityId = getActivity().getIntent().getExtras().getLong("entityId");
        entity = new DatabaseHelper(getActivity(), null, null, 1).getStatEntity(entityId);
        generateSpinner();
        displaySelectedStat();

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void generateSpinner() {
        String[] choices;

        // Generate different choices based on the entity type
        if (entity instanceof Player) {
            choices = Player.GRAPHABLE_STATS;
        } else {
            choices = Team.GRAPHABLE_STATS;
        }

        Spinner spinner = (Spinner) v.findViewById(R.id.entityGraphSelectStat);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                choices);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStat = (String) parent.getItemAtPosition(position);
                displaySelectedStat();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void displaySelectedStat() {
        XYSeries series = new XYSeries(selectedStat);
        ArrayList<ArrayList<Object>> careerStat = entity.getCareerStat(selectedStat);
        int gameNum = 0;
        for (ArrayList<Object> seasonStat : careerStat) {
            for (Object stat : seasonStat) {
                series.add(++gameNum, (int) stat);
            }
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(5);
        renderer.setColor(Color.WHITE);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(7);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        mRenderer.setXLabels(series.getItemCount() - 1);
        mRenderer.setXTitle("Game Number");
        mRenderer.setYLabelsAlign(Paint.Align.CENTER);
        mRenderer.setYLabelsVerticalPadding(-10);;
        mRenderer.setAxisTitleTextSize(40);
        mRenderer.setLabelsTextSize(30);
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setShowLegend(false);
        mRenderer.setChartTitle(selectedStat);
        mRenderer.setChartTitleTextSize(50);
        GraphicalView chartView = ChartFactory.getLineChartView(getActivity(), dataset, mRenderer);
        LinearLayout chartLayout = (LinearLayout) v.findViewById(R.id.entityGraphLayout);

        // Remove the currently displayed graph, if there is one
        chartLayout.removeAllViewsInLayout();
        chartLayout.addView(chartView);

    }
}