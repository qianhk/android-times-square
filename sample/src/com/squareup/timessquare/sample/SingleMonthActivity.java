package com.squareup.timessquare.sample;

import android.app.Activity;
import android.os.Bundle;
import com.squareup.timessquare.CalendarView;
import com.squareup.timessquare.Logr;
import com.squareup.timessquare.MonthCellDescriptor;

import java.util.Date;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-6-25
 */
public class SingleMonthActivity extends Activity {

    final CalendarView.Listener listener = new CalendarView.Listener() {
        @Override
        public void handleClick(MonthCellDescriptor cell) {
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
        CalendarView monthView = (CalendarView) findViewById(R.id.month_view);
        Logr.d("lookMonth findViewById month_view");
        monthView.setDate(new Date(2015 - 1900, 8, 8));
    }


}