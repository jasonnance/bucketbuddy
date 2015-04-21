package com.cs495.bucketbuddy;

import android.graphics.Color;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;


public class ViewEntityStatsActivity extends ActionBarActivity {

    private String selectedStat = "points";
    private StatEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entity_stats);
        long entityId = getIntent().getExtras().getLong("entityId");
        entity = new DatabaseHelper(this, null, null, 1).getStatEntity(entityId);

        /*
        // Placeholder test code
        entity = new Team();
        TeamGame testGame = new TeamGame();
        TeamGame testGame2 = new TeamGame();
        TeamSeason testSeason = new TeamSeason();
        testGame.setStat("points",50);
        testGame.setStat("rebounds", 40);
        testGame.setStat("assists", 20);
        testGame2.setStat("points",60);
        testGame2.setStat("rebounds", 35);
        testGame2.setStat("assists", 25);
        testSeason.addGame(testGame);
        testSeason.addGame(testGame2);
        entity.addSeason(testSeason);
        */

        generateSpinner();
        displaySelectedStat();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_entity_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void generateSpinner() {
        String[] choices = StatEntity.REQUIRED_STATS;

        /*
        // Generate different choices based on the entity type
        if (entity instanceof Player) {
            choices = new String[]{"points", "rebounds", "assists"};
        } else {
            choices = new String[]{"points", "rebounds", "assists"};
        }
        */

        Spinner spinner = (Spinner) findViewById(R.id.entityGraphSelectStat);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
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
        GraphicalView chartView = ChartFactory.getLineChartView(this, dataset, mRenderer);
        LinearLayout chartLayout = (LinearLayout) findViewById(R.id.entityGraphLayout);

        // Remove the currently displayed graph, if there is one
        chartLayout.removeAllViewsInLayout();
        chartLayout.addView(chartView);

    }
}
