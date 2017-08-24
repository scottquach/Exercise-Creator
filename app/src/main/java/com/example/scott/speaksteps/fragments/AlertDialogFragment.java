package com.example.scott.speaksteps.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Scott Quach on 8/6/2017.
 */

public class AlertDialogFragment extends DialogFragment{

    public AlertDialogFragment(){}

    public static AlertDialogFragment newInstance(String title, String message, String positiveButton,
                                                  String negativeButton){
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("positive", positiveButton);
        args.putString("negative", negativeButton);
        fragment.setArguments(args);
        return fragment;
    }

    public interface AlertDialogInterface{
        void onPositive();
        void onNegative(DialogInterface dialogInterface);
    }

    private AlertDialogInterface listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof AlertDialogInterface){
            listener = (AlertDialogInterface) activity;
        } else {
            new RuntimeException("Must initiate listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(args != null ? args.getString("title") : "title")
                .setMessage(args != null ? args.getString("message") : "message")
                .setPositiveButton(args != null ? args.getString("positive") : "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) listener.onPositive();
                    }
                })
                .setNegativeButton(args != null ? args.getString("negative") : "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) listener.onNegative(dialogInterface);
                    }
                });

        return builder.create();
    }
}
