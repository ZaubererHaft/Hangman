package teamfmg.hangman;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by Ludwig on 1/21/16.
 * Use this class to define Charts drawn on an activity.
 */
public final class Charts
{

    /**
     * This generates a pie chart from the given parameters.
     * @param activity Where to draw the chart.
     * @param sectionNames The Names of the sections.
     * @param sectionValues The values of the sections.
     * @param sectionColors The colors of the sections.
     * @param title Title of this chart.
     * @param legend Shows the legend of the diagram.
     * @param texts Shows the labels on the segments.
     * @return View to use in activity.
     */
    public static View generatePieChart
    (
        Activity activity, String[] sectionNames,
        double[] sectionValues, int[] sectionColors, String title, boolean legend, boolean texts
    )
    {
        // Instantiating CategorySeries to plot Pie Chart
        CategorySeries distributionSeries = new CategorySeries(title);


        for (int i = 0; i < sectionValues.length; i++)
        {
            // Adding a slice with its values and name to the Pie Chart
            distributionSeries.add(sectionNames[i], sectionValues[i]);
        }


        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer  = new DefaultRenderer();

        for(int i = 0; i< sectionValues.length; i++)
        {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(sectionColors[i]);

            //seriesRenderer.setDisplayChartValues(true);
            // Adding a renderer for a slice
            defaultRenderer.addSeriesRenderer(seriesRenderer);
        }

        defaultRenderer.setShowLegend(legend);
        defaultRenderer.setShowLabels(texts);

        defaultRenderer.setChartTitle(title);
        defaultRenderer.setChartTitleTextSize(activity.getResources().getDimension(R.dimen.textSizeBig));

        defaultRenderer.setZoomButtonsVisible(false);
        defaultRenderer.setPanEnabled(false);
        defaultRenderer.setZoomEnabled(false);

        defaultRenderer.setYAxisColor(Color.WHITE);
        defaultRenderer.setXAxisColor(Color.WHITE);
        defaultRenderer.setLabelsColor(Color.WHITE);

        defaultRenderer.setLabelsTextSize(activity.getResources().getDimension(R.dimen.textSizeMedium));
        defaultRenderer.setLegendTextSize(activity.getResources().getDimension(R.dimen.textSizeSmall));

        // Creating an intent to plot bar chart using dataset and multipleRenderer
        return ChartFactory.getPieChartView
                (activity.getBaseContext(), distributionSeries, defaultRenderer);

    }

    public static View generateLineChart(Activity activity)
    {
        int[] val = {1,2,3,4,5,6,7};
        XYSeries series = new XYSeries("London Temperature hourly");

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

        return ChartFactory.getLineChartView(activity, dataset, mRenderer);
    }
}
