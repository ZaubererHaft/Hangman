package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Achievements menu.<br />
 * Created by Ludwig 19.01.2016.
 * @since 1.0
 */
public class Achievements extends Activity implements IApplyableSettings, View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        this.findViewById(R.id.achievements_close).setOnClickListener(this);

        this.changeBackground();
        this.openChart();
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_Achievements);
        Settings.setColor(rl);

    }

    @Override
    public void onClick(View v)
    {
        Intent i = null;

        switch (v.getId())
        {
            case R.id.achievements_close:
                this.finish();
                break;

            case R.id.account_statistic:
                i = new Intent(this, Statistic.class);
                this.startActivity(i);
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }

    private void openChart()
    {
        int[] val = {1,2,3,4,5,6,7};
        XYSeries series = new XYSeries("London Temperature hourly");
        int hour = 0;


        for (int i = 0; i < val.length; i++)
        {
            series.add(i, val[i]);
        }

        // Now we create the renderer
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.WHITE);
        // Include low and max value
        renderer.setDisplayBoundingPoints(true);
        // we add point markers
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(3);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        // Disable Pan on two axis
        mRenderer.setPanEnabled(false, false);
        mRenderer.setYAxisMax(35);
        mRenderer.setYAxisMin(0);
        mRenderer.setShowGrid(true); // we show the grid

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        GraphicalView chartView = ChartFactory.getLineChartView(this, dataset, mRenderer);

        RelativeLayout rl = (RelativeLayout)this.findViewById(R.id.achievements_subLayout);
        rl.addView(chartView,0);
    }


}
