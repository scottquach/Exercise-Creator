package com.example.scott.speaksteps;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintSet;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.scott.speaksteps.database.KeyConstants;
import com.example.scott.speaksteps.databinding.ActivityStartMenuBinding;

import java.util.ArrayList;

import timber.log.Timber;

public class StartMenuActivity extends BaseDataActivity {

    private ConstraintSet originalConstraint = new ConstraintSet();
    private ConstraintSet applyConstraintSet = new ConstraintSet();

    private boolean loadIsCard, createIsCard = false;

    ActivityStartMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start_menu);

        //initialize

        applyConstraintSet.clone(binding.layoutStartMenu);
        originalConstraint.clone(binding.layoutStartMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menu_action_settings:
//                startActivity(new Intent(this, SettingsActivity.class));
//                break;

            case R.id.menu_action_about:
                startActivity(new Intent(this, AboutActivity.class));

                break;
        }
        return true;
    }

    /**Returns an ArrayLists of the names of
      saved exercises
    */
    private ArrayList<String> loadExerciseNames() {
        ArrayList<String> names = new ArrayList<String>();
        Cursor dataCursor = dbHelper.getExerciseNames();

        int count = dataCursor.getCount();
        Timber.d("Exercise name cursor count: " + count);

        if (dataCursor.moveToFirst()) {
            do {
                String temp = dataCursor.getString(0);
                names.add(temp);
                Timber.d("Iterated name was: " + temp);
            } while (dataCursor.moveToNext());
        } else {
            Timber.d("There are no saved exercises");
        }
        dataCursor.close();
        return names;
    }

    /**Loads the listview of saved exercises located in
    the load CardView
     */
    private void loadListView() {
        final ArrayList<String> names = loadExerciseNames();
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.listview_simple_row, names);
        binding.listViewRoutines.setAdapter(adapter);

        binding.listViewRoutines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String exerciseName = names.get(position);
                Intent openEditMode = new Intent(StartMenuActivity.this, RoutineActivity.class);
                openEditMode.putExtra("name", exerciseName);
                startActivity(openEditMode);
            }
        });
    }

    /**Start morph animation for loading
    an exercise
     */
    private void loadCardAnim() {
        TransitionManager.beginDelayedTransition(binding.layoutStartMenu);

        applyConstraintSet.centerVertically(R.id.card_load_routine, R.id.layout_start_menu);
        applyConstraintSet.centerHorizontally(R.id.card_load_routine, R.id.layout_start_menu);

        for (int i = 0; i < binding.containerLoadRoutine.getChildCount(); i++) {
            binding.containerLoadRoutine.getChildAt(i).setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < binding.innerContainerLoadRoutine.getChildCount(); i++) {
            binding.innerContainerLoadRoutine.getChildAt(i).setVisibility(View.VISIBLE);
        }
        binding.buttonLoad.setVisibility(View.GONE);

        applyConstraintSet.applyTo(binding.layoutStartMenu);
        loadListView();
    }

    /**Resets the position of the
    load card from it's animated position
     */
    private void resetLoadCard() {
        TransitionManager.beginDelayedTransition(binding.layoutStartMenu);

        for (int i = 0; i < binding.containerLoadRoutine.getChildCount(); i++) {
            binding.containerLoadRoutine.getChildAt(i).setVisibility(View.GONE);
        }

        for (int i = 0; i < binding.innerContainerLoadRoutine.getChildCount(); i++) {
            binding.innerContainerLoadRoutine.getChildAt(i).setVisibility(View.GONE);
        }
        binding.innerContainerLoadRoutine.setVisibility(View.VISIBLE);
        binding.buttonLoad.setVisibility(View.VISIBLE);

        originalConstraint.applyTo(binding.layoutStartMenu);
        applyConstraintSet.clone(originalConstraint);
    }

    /**Start morph animation for creating
    a new exercise
     */
    private void createCardAnim() {
        TransitionManager.beginDelayedTransition(binding.layoutStartMenu);

        applyConstraintSet.centerVertically(R.id.card_create_route, R.id.layout_start_menu);
        applyConstraintSet.centerHorizontally(R.id.card_create_route, R.id.layout_start_menu);


        for (int i = 0; i < binding.containerCreateRoutine.getChildCount(); i++) {
            binding.containerCreateRoutine.getChildAt(i).setVisibility(View.VISIBLE);
        }

        applyConstraintSet.applyTo(binding.layoutStartMenu);
    }

    /**Resets the position of the
    create card from it's animated position
     */
    private void resetCreateCard() {
        TransitionManager.beginDelayedTransition(binding.layoutStartMenu);

        for (int i = 0; i < binding.containerCreateRoutine.getChildCount(); i++) {
            binding.containerCreateRoutine.getChildAt(i).setVisibility(View.GONE);
        }
        binding.buttonCreate.setVisibility(View.VISIBLE);

        originalConstraint.applyTo(binding.layoutStartMenu);
        applyConstraintSet.clone(originalConstraint);
    }

    public void loadButtonClicked(View view) {
        if (!loadIsCard) {
            if (createIsCard) {
                createIsCard = false;
                resetCreateCard();
            }
            loadCardAnim();
            loadIsCard = true;
        }
    }

    /**Handle the creation of a new sql table for the
    exercise, passes name to editMode Activity. Checks
    if the new name currently exists or not
     */
    public void createButtonClicked(View view) {
        if (createIsCard) {
            String newName = binding.textSetName.getText().toString().trim();
            if (newName.equals("")) {
                binding.textSetName.setError(getString(R.string.blank_field));
            } else {
                boolean doesExist = false;
                ArrayList<String> currentNames = loadExerciseNames();
                for (String current : currentNames) {
                    if (newName.equals(current)) {
                        Timber.d("name already exists");
                        doesExist = true;
                        break;
                    }
                }

                if (!doesExist) {
                    if (isValidFormat(newName)) {
                        dbHelper.saveNewRoutine(newName);
                        Intent createExercise = new Intent(StartMenuActivity.this, EditModeActivity.class);
                        createExercise.putExtra(KeyConstants.NAME, newName);
                        startActivity(createExercise);
                    } else {
                        binding.textSetName.setError(getString(R.string.name_invalid_format));
                    }
                } else {
                    binding.textSetName.setError(getString(R.string.name_already_exists));
                }
            }

        } else {
            if (loadIsCard) {
                loadIsCard = false;
                resetLoadCard();
            }
            createCardAnim();
            createIsCard = true;
        }
    }

    /**
     * checks if the wanted name of a new table
     * is of a valid format (no periods, etc.)
     */
    private boolean isValidFormat(String name) {
        return name.matches("^[a-zA-Z0-9_ ]*$") && !Character.isDigit(name.charAt(0));
    }

    public void loadCancelButtonClicked(View view) {
        loadIsCard = false;
        resetLoadCard();
    }
}
