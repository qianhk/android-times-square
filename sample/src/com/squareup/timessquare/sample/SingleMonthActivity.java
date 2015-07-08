package com.squareup.timessquare.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.squareup.timessquare.CalendarView;
import com.squareup.timessquare.Logr;
import com.squareup.timessquare.MonthCellDescriptor;

import java.util.Calendar;
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
        final CalendarView monthView = (CalendarView) findViewById(R.id.month_view);
        Logr.d("lookMonth findViewById month_view");
        monthView.setDate(new Date(2015 - 1900, 6, 20));
        final TextView tvNowDate = (TextView) findViewById(R.id.tv_now_date);
        findViewById(R.id.btn_now_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Date selectedDate = monthView.getSelectedDate();
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedDate);
                String dateStr = String.format("  %04d-%02d-%02d"
                        , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                tvNowDate.setText(dateStr);
            }
        });
    }


}