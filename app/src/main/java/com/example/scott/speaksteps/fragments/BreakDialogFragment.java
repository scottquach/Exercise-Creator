package com.example.scott.speaksteps.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.example.scott.speaksteps.R;

/**
 * Created by Scott Quach on 7/15/2017.
 */

public class BreakDialogFragment extends DialogFragment{

    public interface BreakDialogListener {
        void createBreak(int minutes, int seconds);
    }

    private BreakDialogListener listener;

    public static BreakDialogFragment newInstance(){
        BreakDialogFragment fragment = new BreakDialogFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BreakDialogListener){
            listener = (BreakDialogListener) activity;
        }else{
            throw new RuntimeException("Listener must not be null");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.create_break);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_break, null);

        builder.setView(view);

        final NumberPicker minuteNP = (NumberPicker) view.findViewById(R.id.picker_minute);
        final NumberPicker secondNP = (NumberPicker) view.findViewById(R.id.picker_second);
        minuteNP.setMinValue(0);
        minuteNP.setMaxValue(60);
        secondNP.setMinValue(0);
        secondNP.setMaxValue(59);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int minutes = minuteNP.getValue();
                int seconds = secondNP.getValue();
                if (listener != null) listener.createBreak(minutes, seconds);

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
