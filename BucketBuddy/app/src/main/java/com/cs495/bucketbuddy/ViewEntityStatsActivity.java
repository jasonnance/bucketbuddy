package com.cs495.bucketbuddy;

import android.graphics.Color;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;


public class ViewEntityStatsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entity_stats);

        // TODO long entityId = getIntent().getExtras().getLong("entityId");
        // TODO StatEntity entity = new DatabaseHelper(this, null, null, 1).getStatEntity(entityId);
        // Placeholder test code
        StatEntity entity = new Team();
        TeamGame testGame = new TeamGame();
        TeamGame testGame2 = new TeamGame();
        TeamSeason testSeason = new TeamSeason();
        testGame.setStat("points",50);
        testGame2.setStat("points",60);
        testSeason.addGame(testGame);
        testSeason.addGame(testGame2);
        entity.addSeason(testSeason);

        generateRadioButtons(entity);

        String statName = "points";
        XYSeries series = new XYSeries(statName);
        ArrayList<ArrayList<Object>> careerStat = entity.getCareerStat(statName);
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
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setYLabelsVerticalPadding(-10);;
        mRenderer.setAxisTitleTextSize(40);
        mRenderer.setLabelsTextSize(30);
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setShowLegend(false);
        mRenderer.setChartTitle(statName);
        mRenderer.setChartTitleTextSize(50);
        GraphicalView chartView = ChartFactory.getLineChartView(this, dataset, mRenderer);
        LinearLayout chartLayout = (LinearLayout) findViewById(R.id.entityGraphLayout);
        chartLayout.addView(chartView);

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

    private void generateRadioButtons(StatEntity entity) {
        ArrayList<RadioButton> radios = new ArrayList<RadioButton>();

        RadioButton pointsRadio = new RadioButton(this);
        pointsRadio.setText(R.string.points);
        radios.add(pointsRadio);

        RadioButton reboundsRadio = new RadioButton(this);
        reboundsRadio.setText(R.string.rebounds);
        radios.add(reboundsRadio);

        RadioButton assistsRadio = new RadioButton(this);
        assistsRadio.setText(R.string.assists);
        radios.add(assistsRadio);

        if (entity instanceof Player) {
            // add player specific stats here
        }
        else {
            // add team specific stats here
        }
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.entityGraphSelectStat);
        for (RadioButton radio : radios) {
            radio.setTextColor(Color.WHITE);
            radio.setTextSize(12);
            radioGroup.addView(radio);
        }
    }
}
