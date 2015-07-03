package com.squareup.timessquare;

import android.content.Context;
import android.util.AttributeSet;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Calendar.*;

public class CalendarView extends MonthView {

    private Calendar mTodayCalendar;

    final List<MonthCellDescriptor> mSelectedCellList = new ArrayList<MonthCellDescriptor>();

    private Listener mListener = new Listener() {
        @Override
        public void handleClick(MonthCellDescriptor cell) {
            if (!mSelectedCellList.contains(cell)) {
                clearOldSelections();
                mSelectedCellList.add(cell);
                cell.setSelected(true);
                flushView();
            }
        }
    };

    public Date getSelectedDate() {
        return (mSelectedCellList.size() > 0 ? mSelectedCellList.get(0).getDate() : null);
    }

    private void clearOldSelections() {
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
    }

    public void setDate(Date date) {
        Calendar monthCounter = Calendar.getInstance();
        monthCounter.setTime(date);
        Locale locale = Locale.getDefault();
        SimpleDateFormat monthNameFormat = new SimpleDateFormat(getContext().getString(R.string.month_name_format), locale);
        MonthDescriptor month = new MonthDescriptor(monthCounter.get(MONTH), monthCounter.get(YEAR), date, monthNameFormat.format(date));
        final List<List<MonthCellDescriptor>> monthCells = getMonthCells(month, monthCounter);
        initData(month, monthCells, false, null, null);
    }

    List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month, Calendar startCal) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(startCal.getTime());
        List<List<MonthCellDescriptor>> cells = new ArrayList<List<MonthCellDescriptor>>();
        cal.set(DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(DAY_OF_WEEK);
        int offset = cal.getFirstDayOfWeek() - firstDayOfWeek;
        if (offset > 0) {
            offset -= 7;
        }
        cal.add(Calendar.DATE, offset);


        while ((cal.get(MONTH) < month.getMonth() + 1 || cal.get(YEAR) < month.getYear()) //
                && cal.get(YEAR) <= month.getYear()) {
            List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
            cells.add(weekCells);
            for (int c = 0; c < 7; c++) {
                Date date = cal.getTime();
                boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
                boolean isSelected = false;
                boolean isSelectable = true;
                boolean isToday = CalendarPickerView.sameDate(cal, mTodayCalendar);
                boolean isHighlighted = false;
                int value = cal.get(DAY_OF_MONTH);

                MonthCellDescriptor.RangeState rangeState = MonthCellDescriptor.RangeState.NONE;

                weekCells.add(new MonthCellDescriptor(date, isCurrentMonth, isSelectable, isSelected, isToday, isHighlighted, value, rangeState));
                cal.add(DATE, 1);
            }
        }
        return cells;
    }

}
