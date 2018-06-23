package ru.android.cyfral.servisnik.ui.ordercard;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TimePicker;

public class MyTimePicker extends TimePicker {

    private static final int TIME_PICKER_INTERVAL = 5;

    public MyTimePicker(Context context) {
        super(context);
    }

    @Override
    public Integer getCurrentMinute() {
        return super.getCurrentMinute()*TIME_PICKER_INTERVAL;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field field = classForid.getField("minute");

            NumberPicker mMinuteSpinner = (NumberPicker) findViewById(field.getInt(null));
            mMinuteSpinner.setMinValue(0);
            mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL)
                displayedValues.add(String.format("%02d", i));
            mMinuteSpinner.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}