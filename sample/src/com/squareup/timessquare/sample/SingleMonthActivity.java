package com.squareup.timessquare.sample;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.squareup.timessquare.Logr;
import com.squareup.timessquare.MonthCellDescriptor;
import com.squareup.timessquare.MonthDescriptor;
import com.squareup.timessquare.MonthView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Calendar.*;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-6-25
 */
public class SingleMonthActivity extends Activity {

    final MonthView.Listener listener = new MonthView.Listener() {
        @Override public void handleClick(MonthCellDescriptor cell) {
//            Date clickedDate = cell.getDate();
//
//            if (cellClickInterceptor != null && cellClickInterceptor.onCellClicked(clickedDate)) {
//                return;
//            }
//            if (!betweenDates(clickedDate, minCal, maxCal) || !isDateSelectable(clickedDate)) {
//                if (invalidDateListener != null) {
//                    invalidDateListener.onInvalidDateSelected(clickedDate);
//                }
//            } else {
//                boolean wasSelected = doSelectDate(clickedDate, cell);
//
//                if (dateListener != null) {
//                    if (wasSelected) {
//                        dateListener.onDateSelected(clickedDate);
//                    } else {
//                        dateListener.onDateUnselected(clickedDate);
//                    }
//                }
//            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logr.d("lookMonth onCreate");
        setContentView(R.layout.single_month_page);
        MonthView monthView = (MonthView) findViewById(R.id.month_view);
        Logr.d("lookMonth findViewById month_view");

        Locale locale = Locale.getDefault();
        Calendar today = Calendar.getInstance(locale);
        Calendar minCal = Calendar.getInstance(locale);
        Calendar maxCal = Calendar.getInstance(locale);
        SimpleDateFormat monthNameFormat = new SimpleDateFormat(getString(com.squareup.timessquare.R.string.month_name_format), locale);
        SimpleDateFormat weekdayNameFormat = new SimpleDateFormat(getString(com.squareup.timessquare.R.string.day_name_format), locale);
        DateFormat fullDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        Resources res = getResources();
        final int bg = res.getColor(com.squareup.timessquare.R.color.calendar_bg);
        int dividerColor = res.getColor(com.squareup.timessquare.R.color.calendar_divider);
        int dayBackgroundResId = com.squareup.timessquare.R.drawable.calendar_bg_selector;
        int dayTextColorResId = com.squareup.timessquare.R.color.calendar_text_selector;
        int titleTextColor = res.getColor(com.squareup.timessquare.R.color.calendar_text_active);
        boolean displayHeader = true;
        int headerTextColor = res.getColor(com.squareup.timessquare.R.color.calendar_text_active);

//        final ViewGroup rootView = (ViewGroup) findViewById(R.id.layout_root);
//        final MonthView monthView = MonthView.create(rootView, LayoutInflater.from(this)
//                , weekdayNameFormat, listener, today, dividerColor,
//                dayBackgroundResId, dayTextColorResId, titleTextColor, displayHeader,
//                headerTextColor, null, locale);
//        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        rootView.addView(monthView, layoutParams);

        final List<MonthDescriptor> months = new ArrayList<MonthDescriptor>();
        final List<List<List<MonthCellDescriptor>>> cells =
                new ArrayList<List<List<MonthCellDescriptor>>>();

        Calendar monthCounter = Calendar.getInstance(locale);
        monthCounter.setTime(minCal.getTime());
        final int maxMonth = maxCal.get(MONTH);
        final int maxYear = maxCal.get(YEAR);
        while ((monthCounter.get(MONTH) <= maxMonth // Up to, including the month.
                || monthCounter.get(YEAR) < maxYear) // Up to the year.
                && monthCounter.get(YEAR) < maxYear + 1) { // But not > next yr.
            Date date = monthCounter.getTime();
            MonthDescriptor month =
                    new MonthDescriptor(monthCounter.get(MONTH), monthCounter.get(YEAR), date,
                            monthNameFormat.format(date));
            cells.add(getMonthCells(month, monthCounter));
            months.add(month);
            monthCounter.add(MONTH, 1);
        }

        Logr.d("lookMonth will init monthView");
        final int position = 0;
        monthView.init(months.get(position), cells.get(position), true, null,
                null);
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
                boolean isSelectable =
                        isCurrentMonth;
                boolean isToday = false;
                boolean isHighlighted = false;
                int value = cal.get(DAY_OF_MONTH);

                MonthCellDescriptor.RangeState rangeState = MonthCellDescriptor.RangeState.NONE;

                weekCells.add(
                        new MonthCellDescriptor(date, isCurrentMonth, isSelectable, isSelected, isToday,
                                isHighlighted, value, rangeState));
                cal.add(DATE, 1);
            }
        }
        return cells;
    }
}