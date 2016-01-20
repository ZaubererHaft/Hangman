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
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
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
        this.openPieChart();
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

    private void openPieChart()
    {
        // Pie Chart Section Names
        String[] code = new String[] {
                "Eclair & Older", "Froyo", "Gingerbread", "Honeycomb",
                "IceCream Sandwich", "Jelly Bean"
        };

        // Pie Chart Section Value
        double[] distribution = { 3.9, 12.9, 55.8, 1.9, 23.7, 1.8 } ;

        // Color of each Pie Chart Sections
        int[] colors = { Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED,
                Color.YELLOW };

        // Instantiating CategorySeries to plot Pie Chart
        CategorySeries distributionSeries =
                new CategorySeries(" Android version distribution as on October 1, 2012");

        for(int i=0 ;i < distribution.length;i++)
        {
            // Adding a slice with its values and name to the Pie Chart
            distributionSeries.add(code[i], distribution[i]);
        }

        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer  = new DefaultRenderer();

        for(int i = 0 ;i<distribution.length;i++)
        {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(colors[i]);

            //seriesRenderer.setDisplayChartValues(true);
            // Adding a renderer for a slice
            defaultRenderer.addSeriesRenderer(seriesRenderer);
        }

        defaultRenderer.setChartTitle("Android version distribution as on October 1, 2012 ");
        defaultRenderer.setChartTitleTextSize(60);

        defaultRenderer.setZoomButtonsVisible(false);
        defaultRenderer.setPanEnabled(false);
        defaultRenderer.setZoomEnabled(false);

        defaultRenderer.setYAxisColor(Color.WHITE);
        defaultRenderer.setXAxisColor(Color.WHITE);
        defaultRenderer.setLabelsColor(Color.WHITE);

        defaultRenderer.setLabelsTextSize(50);
        defaultRenderer.setLegendTextSize(50);

        // Creating an intent to plot bar chart using dataset and multipleRenderer
        GraphicalView chartView = ChartFactory.getPieChartView
                (getBaseContext(), distributionSeries, defaultRenderer);

        // Start Activity
        RelativeLayout rl = (RelativeLayout)this.findViewById(R.id.achievements_subLayout);
        rl.addView(chartView, 0);
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
