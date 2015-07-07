package com.squareup.timessquare;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Calendar.*;

public class CalendarView extends MonthView {

    private TextView mTvLeftMonth;
    private TextView mTvRightMonth;

    private Calendar mTodayCalendar;
    private Calendar mDisplayMonth;

    final List<MonthCellDescriptor> mSelectedCellList = new ArrayList<MonthCellDescriptor>();
    final List<Calendar> mSelectedCalendarList = new ArrayList<Calendar>();

    private Listener mListener = new Listener() {
        @Override
        public void handleClick(MonthCellDescriptor cell) {
            if (!cell.isSelected()) {
                clearAllOldSelections();
                mSelectedCellList.add(cell);
                cell.setSelected(true);
                Calendar newlySelectedCal = Calendar.getInstance(Locale.getDefault());
                newlySelectedCal.setTime(cell.getDate());
                mSelectedCalendarList.add(newlySelectedCal);
                flushView();
            }
        }
    };

    public Date getSelectedDate() {
        return (mSelectedCalendarList.size() > 0 ? mSelectedCalendarList.get(0).getTime() : null);
    }

    private void clearAllOldSelections() {
        clearOldCellSelections();
        mSelectedCalendarList.clear();
    }

    private void clearOldCellSelections() {
        for (MonthCellDescriptor cellDescriptor : mSelectedCellList) {
            cellDescriptor.setSelected(false);
        }
        mSelectedCellList.clear();
    }

    public CalendarView(Context context) {
        super(context);
        initConstruct(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConstruct(context);
    }

    private void initConstruct(Context context) {
        mTodayCalendar = Calendar.getInstance(Locale.getDefault());
        setListener(mListener);
        mTvLeftMonth = (TextView) mTitleView.findViewById(R.id.tv_left_month);
        mTvRightMonth = (TextView) mTitleView.findViewById(R.id.tv_right_month);
        mTvLeftMonth.setOnClickListener(mOnClickListener);
        mTvRightMonth.setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == mTvLeftMonth || v == mTvRightMonth) {
                if (v == mTvLeftMonth) {
                    mDisplayMonth.add(MONTH, -1);
                } else {
                    mDisplayMonth.add(MONTH, 1);
                }
                setDate(mDisplayMonth.getTime(), false);
            }
        }
    };

    @Override
    int titleLayoutId() {
        return R.layout.calendar_title;
    }

    public void setDate(Date date) {
        setDate(date, true);
    }

    private void setDate(Date date, boolean selected) {
        if (selected) {
            clearAllOldSelections();
        } else {
            clearOldCellSelections();
        }
        mDisplayMonth = Calendar.getInstance();
        mDisplayMonth.setTime(date);
        if (selected) {
            mSelectedCalendarList.add(mDisplayMonth);
        }

        Locale locale = Locale.getDefault();
        SimpleDateFormat monthNameFormat = new SimpleDateFormat(getContext().getString(R.string.month_name_format), locale);
        MonthDescriptor month = new MonthDescriptor(mDisplayMonth.get(MONTH), mDisplayMonth.get(YEAR), date, monthNameFormat.format(date));
        final List<List<MonthCellDescriptor>> monthCells = getMonthCells(month, mDisplayMonth.getTime());
        initData(month, monthCells, false, null, null);
    }

    List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month, Date startDate) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(startDate);
        List<List<MonthCellDescriptor>> cells = new ArrayList<List<MonthCellDescriptor>>();
        cal.set(DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(DAY_OF_WEEK);
        int offset = cal.getFirstDayOfWeek() - firstDayOfWeek;
        if (offset > 0) {
            offset -= 7;
        }
        cal.add(Calendar.DATE, offset);


        while ((cal.get(MONTH) < month.getMonth() + 1 || cal.get(YEAR) < month.getYear()) && cal.get(YEAR) <= month.getYear()) {
            List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
            cells.add(weekCells);
            for (int c = 0; c < 7; c++) {
                Date date = cal.getTime();
                boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
                boolean isSelected = CalendarPickerView.containsDate(mSelectedCalendarList, cal);
                boolean isSelectable = true;
                boolean isToday = CalendarPickerView.sameDate(cal, mTodayCalendar);
                boolean isHighlighted = false;
                int value = cal.get(DAY_OF_MONTH);
                if (isToday) {
                }
                MonthCellDescriptor.RangeState rangeState = MonthCellDescriptor.RangeState.NONE;

                final MonthCellDescriptor cellDescriptor = new MonthCellDescriptor(date, isCurrentMonth, isSelectable, isSelected, isToday, isHighlighted, value, rangeState);
                weekCells.add(cellDescriptor);
                if (isSelected) {
                    mSelectedCellList.add(cellDescriptor);
                }
                cal.add(DATE, 1);
            }
        }
        return cells;
    }

}
