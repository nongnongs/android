package com.maapuu.mereca.util;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.maapuu.mereca.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2018/5/24.
 */

public class BarChartManager {
    private Context mContext;
    private BarChart mBarChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;

    public BarChartManager(Context mContext,BarChart barChart) {
        this.mContext = mContext;
        this.mBarChart = barChart;
        leftAxis = mBarChart.getAxisLeft();
        rightAxis = mBarChart.getAxisRight();
        xAxis = mBarChart.getXAxis();
    }

    /**
     * 初始化LineChart
     */
    private void initLineChart(List<String> xAxisValues) {
        //背景颜色
//        mBarChart.setBackgroundColor(Color.WHITE);
        mBarChart.setTouchEnabled(false);

        // 可拖曳
        mBarChart.setDragEnabled(false);
        mBarChart.setPinchZoom(false);
        mBarChart.setDoubleTapToZoomEnabled(false);
        //网格
        mBarChart.setDrawGridBackground(false);
        //背景阴影
        mBarChart.setDrawBarShadow(false);
        mBarChart.setHighlightFullBarEnabled(false);

        //显示边界
        mBarChart.setDrawBorders(false);
        mBarChart.setDescription(null);

        //折线图例 标签 设置
        Legend legend = mBarChart.getLegend();
        legend.setEnabled(false);

        //XY轴的设置
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
//        xAxis.setGridColor(Color.parseColor("#EFEFEF"));
        xAxis.setValueFormatter(new StringAxisValueFormatter(xAxisValues));
        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setEnabled(true);
        leftAxis.setGridColor(Color.parseColor("#E0E0E0"));
        leftAxis.setDrawAxisLine(false);//禁止绘制y轴
        leftAxis.setTextColor(Color.parseColor("#E0E0E0"));
        rightAxis.setAxisMinimum(0f);
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(false);
    }

    public class StringAxisValueFormatter implements IAxisValueFormatter {
        private List<String> xValues;
        public StringAxisValueFormatter(List<String> xValues) {
            this.xValues = xValues;
        }

        @Override
        public String getFormattedValue(float v, AxisBase axisBase) {
            if (v < 0 || v > (xValues.size() - 1)){//使得两侧柱子完全显示
                return "";
            }
            return xValues.get((int)v);
        }
    }

    /**
     * 展示柱状图(一条)
     *
     * @param xAxisValues
     * @param yAxisValues
     */
    public void showBarChart(List<String> xAxisValues, List<Float> yAxisValues) {
        initLineChart(xAxisValues);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < xAxisValues.size(); i++) {
            entries.add(new BarEntry(i, yAxisValues.get(i)));
        }
        // 每一个BarDataSet代表一类柱状图
        BarDataSet barDataSet = new BarDataSet(entries, "");

        barDataSet.setColor(mContext.getResources().getColor(R.color.main_color));
        barDataSet.setValueTextSize(9f);
        barDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return StringUtils.formatDouble(value)+"%";
            }
        });
        barDataSet.setValueTextColor(mContext.getResources().getColor(R.color.main_color));
        barDataSet.setFormLineWidth(1f);
        barDataSet.setFormSize(15.f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        BarData data = new BarData(dataSets);
        //设置X轴的刻度数
        xAxis.setLabelCount(xAxisValues.size() - 1, false);
        mBarChart.setData(data);
    }

    /**
     * 展示柱状图(多条)
     *
     * @param xAxisValues
     * @param yAxisValues
     * @param labels
     * @param colours
     */
    public void showBarChart(List<String> xAxisValues, List<List<Float>> yAxisValues, List<String> labels, List<Integer> colours) {
        initLineChart(xAxisValues);
        BarData data = new BarData();
        for (int i = 0; i < yAxisValues.size(); i++) {
            ArrayList<BarEntry> entries = new ArrayList<>();
            for (int j = 0; j < yAxisValues.get(i).size(); j++) {
                entries.add(new BarEntry(j, yAxisValues.get(i).get(j)));
            }
            BarDataSet barDataSet = new BarDataSet(entries, labels.get(i));

            barDataSet.setColor(colours.get(i));
            barDataSet.setValueTextColor(colours.get(i));
            barDataSet.setValueTextSize(10f);
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            data.addDataSet(barDataSet);
        }
        int amount = yAxisValues.size();

        float groupSpace = 0.12f; //柱状图组之间的间距
        float barSpace = (float) ((1 - 0.12) / amount / 10); // x4 DataSet
        float barWidth = (float) ((1 - 0.12) / amount / 10 * 9); // x4 DataSet

        // (0.2 + 0.02) * 4 + 0.08 = 1.00 -> interval per "group"
        xAxis.setLabelCount(xAxisValues.size() - 1, false);
        data.setBarWidth(barWidth);


        data.groupBars(0, groupSpace, barSpace);
        mBarChart.setData(data);
    }


    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);

        rightAxis.setAxisMaximum(max);
        rightAxis.setAxisMinimum(min);
        rightAxis.setLabelCount(labelCount, false);
        mBarChart.invalidate();
    }

    /**
     * 设置X轴的值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setXAxis(float max, float min, int labelCount) {
        xAxis.setAxisMaximum(max);
        xAxis.setAxisMinimum(min);
        xAxis.setLabelCount(labelCount, false);

        mBarChart.invalidate();
    }

    /**
     * 设置高限制线
     *
     * @param high
     * @param name
     */
    public void setHightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "高限制线";
        }
        LimitLine hightLimit = new LimitLine(high, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        leftAxis.addLimitLine(hightLimit);
        mBarChart.invalidate();
    }

    /**
     * 设置低限制线
     *
     * @param low
     * @param name
     */
    public void setLowLimitLine(int low, String name) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        leftAxis.addLimitLine(hightLimit);
        mBarChart.invalidate();
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        mBarChart.setDescription(description);
        mBarChart.invalidate();
    }
}
