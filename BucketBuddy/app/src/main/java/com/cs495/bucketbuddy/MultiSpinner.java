package com.cs495.bucketbuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Inspired by: http://stackoverflow.com/a/6022474/1521064
 */
public class MultiSpinner extends Spinner {

    private ArrayList<String> entries;
    private boolean[] selected;
    private MultiSpinnerListener listener;

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setItems(ArrayList<String> items) {
        entries = items;
        selected = new boolean[items.size()];
        int i = 0;
        while (i < 5 && i < selected.length) {
            selected[i] = true;
            i++;
        }
    }

    private OnMultiChoiceClickListener mOnMultiChoiceClickListener = new OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            selected[which] = isChecked;
        }
    };

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // build new spinner text & delimiter management
            StringBuffer spinnerBuffer = new StringBuffer();
            for (int i = 0; i < entries.size(); i++) {
                if (selected[i]) {
                    spinnerBuffer.append(entries.get(i));
                    spinnerBuffer.append(", ");
                }
            }

            // Remove trailing comma
            if (spinnerBuffer.length() > 2) {
                spinnerBuffer.setLength(spinnerBuffer.length() - 2);
            }

            // display new text
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item,
                    new String[]{spinnerBuffer.toString()});
            setAdapter(adapter);

            if (listener != null) {
                listener.onItemsSelected(selected);
            }

            // hide dialog
            dialog.dismiss();
        }
    };

    @Override
    public boolean performClick() {
        String[] entriesArray = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            entriesArray[i] = entries.get(i);
        }
        new AlertDialog.Builder(getContext())
                .setMultiChoiceItems(entriesArray, selected, mOnMultiChoiceClickListener)
                .setPositiveButton(android.R.string.ok, mOnClickListener)
                .show();
        return true;
    }

    public void setMultiSpinnerListener(MultiSpinnerListener listener) {
        this.listener = listener;
    }

    public interface MultiSpinnerListener {
        public void onItemsSelected(boolean[] selected);
    }
}