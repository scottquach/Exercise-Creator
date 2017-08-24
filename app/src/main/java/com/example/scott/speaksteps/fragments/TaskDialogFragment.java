package com.example.scott.speaksteps.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.example.scott.speaksteps.R;

/**
 * Created by Scott Quach on 7/15/2017.
 */

public class TaskDialogFragment extends DialogFragment {

    public interface TaskDialogListener {
        void createTask(String task);
    }

    private TaskDialogListener listener;

    public static TaskDialogFragment newInstance() {
        TaskDialogFragment fragment = new TaskDialogFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof TaskDialogListener) {
            listener = (TaskDialogListener) activity;
        } else {
            throw new RuntimeException("Listener can not be null");
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
        builder.setTitle(R.string.new_task);

        final EditText input = new EditText(getActivity());
        input.setHint(R.string.task_name);
        builder.setView(input);

        builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String task = input.getText().toString();
                if (task.isEmpty()) {
                    input.setError("must not be empty");
                } else {
                   if (listener != null) listener.createTask(task);
                }
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
