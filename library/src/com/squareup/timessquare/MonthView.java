// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MonthView extends LinearLayout {
    private TextView mTvTitle;
    private CalendarGridView mGridView;
    private Listener mListener;
    private List<CalendarCellDecorator> mDecoratorList;
    private boolean mRtl;

    public void initMothViewProperty(DateFormat weekdayNameFormat, Listener listener, Calendar today, int dividerColor
            , int dayBackgroundResId, int dayTextColorResId, int titleTextColor, boolean displayHeader
            , int headerTextColor, List<CalendarCellDecorator> decorators, Locale locale) {
        MonthView view = this;
        Logr.d("lookMonth initMothViewProperty childCount=%d", getChildCount());
        view.setDividerColor(dividerColor);
        view.setDayTextColor(dayTextColorResId);
        view.setTitleTextColor(titleTextColor);
        view.setDisplayHeader(displayHeader);
        view.setHeaderTextColor(headerTextColor);

        if (dayBackgroundResId != 0) {
            view.setDayBackground(dayBackgroundResId);
        }

        final int originalDayOfWeek = today.get(Calendar.DAY_OF_WEEK);

        view.mRtl = isRtl(locale);
        int firstDayOfWeek = today.getFirstDayOfWeek();
        final CalendarRowView headerRow = (CalendarRowView) view.mGridView.getChildAt(0);
        for (int offset = 0; offset < 7; offset++) {
            today.set(Calendar.DAY_OF_WEEK, getDayOfWeek(firstDayOfWeek, offset, view.mRtl));
            final TextView textView = (TextView) headerRow.getChildAt(offset);
            textView.setText(weekdayNameFormat.format(today.getTime()));
        }
        today.set(Calendar.DAY_OF_WEEK, originalDayOfWeek);
        view.mListener = listener;
        view.mDecoratorList = decorators;
    }

    private static int getDayOfWeek(int firstDayOfWeek, int offset, boolean isRtl) {
        int dayOfWeek = firstDayOfWeek + offset;
        if (isRtl) {
            return 8 - dayOfWeek;
        }
        return dayOfWeek;
    }

    private static boolean isRtl(Locale locale) {
        // TODO convert the build to gradle and use getLayoutDirection instead of this (on 17+)?
        final int directionality = Character.getDirectionality(locale.getDisplayName(locale).charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT
                || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public MonthView(Context context) {
        super(context);
        Logr.d("lookMonth construct MonthView, one param");
        initConstruct(context);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Logr.d("lookMonth construct MonthView, two param");
        initConstruct(context);

        Resources res = context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MonthView);
        setBackgroundColor(a.getColor(R.styleable.MonthView_android_background,
                res.getColor(R.color.calendar_bg)));
        setDividerColor(a.getColor(R.styleable.MonthView_dividerColor,
                res.getColor(R.color.calendar_divider)));
        setDayTextColor(a.getResourceId(R.styleable.MonthView_dayTextColor,
                R.color.calendar_text_selector));
        setTitleTextColor(a.getColor(R.styleable.MonthView_titleTextColor,
                res.getColor(R.color.calendar_text_active)));
        setDisplayHeader(a.getBoolean(R.styleable.MonthView_displayHeader, true));
        setHeaderTextColor(a.getColor(R.styleable.MonthView_headerTextColor,
                res.getColor(R.color.calendar_text_active)));
        setDayBackground(a.getResourceId(R.styleable.MonthView_dayBackground,
                R.drawable.calendar_bg_selector));
        a.recycle();
    }

    private void initConstruct(Context context) {
        setOrientation(VERTICAL);
        final LayoutInflater inflater = LayoutInflater.from(context);
        View inflateView = inflater.inflate(R.layout.month_title, this);
        mTvTitle = (TextView) inflateView.findViewById(R.id.tv_title);
        inflateView = inflater.inflate(R.layout.month_grid, this);
        mGridView = (CalendarGridView) inflateView.findViewById(R.id.calendar_grid);
    }

    public void setDecoratorList(List<CalendarCellDecorator> decoratorList) {
        this.mDecoratorList = decoratorList;
    }

    public List<CalendarCellDecorator> getDecoratorList() {
        return mDecoratorList;
    }

//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        Logr.d("lookMonth onFinishInflate childCount=%d", getChildCount());
////        mTvTitle = (TextView) findViewById(R.id.mTvTitle);
////        mGridView = (CalendarGridView) findViewById(R.id.calendar_grid);
//    }

    public void initData(MonthDescriptor month, List<List<MonthCellDescriptor>> cells,
                         boolean displayOnly, Typeface titleTypeface, Typeface dateTypeface) {
        Logr.d("lookMonth Initializing MonthView (%d) for %s", System.identityHashCode(this), month);
        long start = System.currentTimeMillis();
        mTvTitle.setText(month.getLabel());

        final int numRows = cells.size();
        mGridView.setNumRows(numRows);
        for (int i = 0; i < 6; i++) {
            CalendarRowView weekRow = (CalendarRowView) mGridView.getChildAt(i + 1);
            weekRow.setListener(mListener);
            if (i < numRows) {
                weekRow.setVisibility(VISIBLE);
                List<MonthCellDescriptor> week = cells.get(i);
                for (int c = 0; c < week.size(); c++) {
                    MonthCellDescriptor cell = week.get(mRtl ? 6 - c : c);
                    CalendarCellView cellView = (CalendarCellView) weekRow.getChildAt(c);

                    String cellDate = Integer.toString(cell.getValue());
                    if (!cellView.getText().equals(cellDate)) {
                        cellView.setText(cellDate);
                    }
                    cellView.setEnabled(cell.isCurrentMonth());
                    cellView.setClickable(!displayOnly);

                    cellView.setSelectable(cell.isSelectable());
                    cellView.setSelected(cell.isSelected());
                    cellView.setCurrentMonth(cell.isCurrentMonth());
                    cellView.setToday(cell.isToday());
                    cellView.setRangeState(cell.getRangeState());
                    cellView.setHighlighted(cell.isHighlighted());
                    cellView.setTag(cell);

                    if (null != mDecoratorList) {
                        for (CalendarCellDecorator decorator : mDecoratorList) {
                            decorator.decorate(cellView, cell.getDate());
                        }
                    }
                }
            } else {
                weekRow.setVisibility(GONE);
            }
        }

        if (titleTypeface != null) {
            mTvTitle.setTypeface(titleTypeface);
        }
        if (dateTypeface != null) {
            mGridView.setTypeface(dateTypeface);
        }

        Logr.d("MonthView.initData took %d ms", System.currentTimeMillis() - start);
    }

    public void setDividerColor(int color) {
        mGridView.setDividerColor(color);
    }

    public void setDayBackground(int resId) {
        mGridView.setDayBackground(resId);
    }

    public void setDayTextColor(int resId) {
        mGridView.setDayTextColor(resId);
    }

    public void setTitleTextColor(int color) {
        mTvTitle.setTextColor(color);
    }

    public void setDisplayHeader(boolean displayHeader) {
        mGridView.setDisplayHeader(displayHeader);
    }

    public void setHeaderTextColor(int color) {
        mGridView.setHeaderTextColor(color);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Logr.d("lookMonth onLayout childCount=%d", getChildCount());
    }

    public interface Listener {
        void handleClick(MonthCellDescriptor cell);
    }
}
