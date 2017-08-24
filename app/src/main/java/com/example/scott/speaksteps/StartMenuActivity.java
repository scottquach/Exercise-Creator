package com.example.scott.speaksteps;

import android.content.Intent;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.scott.speaksteps.database.KeyConstants;

import java.util.ArrayList;

import butterknife.BindView;
import timber.log.Timber;

/*
Research resources used
https://android.jlelse.eu/make-your-app-shine-2-how-to-make-a-button-morph-into-a-form-81d2f0e6bf4a
http://www.uwanttolearn.com/android/constraint-layout-animations-dynamic-constraints-ui-java-hell/
 */

public class StartMenuActivity extends BaseDataActivity {

    private ConstraintSet originalConstraint = new ConstraintSet();
    private ConstraintSet applyConstraintSet = new ConstraintSet();

    private boolean loadIsCard, createIsCard = false;

    @BindView(R.id.layout_start_menu) ConstraintLayout menuLayout;
    @BindView(R.id.container_load_routine) LinearLayout loadContainer;
    @BindView(R.id.inner_container_load_routine) LinearLayout loadInnerContainer;
    @BindView(R.id.container_create_routine) LinearLayout createContainer;
    @BindView(R.id.inner_container_create_routine) LinearLayout createInnerContainer;


    @BindView(R.id.text_set_name) TextView nameEditT;
    @BindView(R.id.button_create) Button createButton;
    @BindView(R.id.button_load) Button loadButton;

    @BindView(R.id.list_view_routines) ListView exercisesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        //initialize

        applyConstraintSet.clone(menuLayout);
        originalConstraint.clone(menuLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_action_settings:

                break;

            case R.id.menu_action_about:

                break;
        }
        return true;
    }

    /*Returns an ArrayLists of the names of
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

    /*Loads the listview of saved exercises located in
    the load CardView
     */
    private void loadListView() {
        final ArrayList<String> names = loadExerciseNames();
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.listview_simple_row, names);
        exercisesListView.setAdapter(adapter);

        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String exerciseName = names.get(position);
                Intent openEditMode = new Intent(StartMenuActivity.this, RoutineActivity.class);
                openEditMode.putExtra("name", exerciseName);
                startActivity(openEditMode);
            }
        });
    }

    /*Start morph animation for loading
    an exercise
     */
    private void loadCardAnim() {
        TransitionManager.beginDelayedTransition(menuLayout);

        applyConstraintSet.centerVertically(R.id.card_load_routine, R.id.layout_start_menu);
        applyConstraintSet.setMargin(R.id.card_load_routine, ConstraintSet.TOP, 0);

        for (int i = 0; i < loadContainer.getChildCount(); i++) {
            loadContainer.getChildAt(i).setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < loadInnerContainer.getChildCount(); i++) {
            loadInnerContainer.getChildAt(i).setVisibility(View.VISIBLE);
        }
        loadButton.setVisibility(View.GONE);

        applyConstraintSet.applyTo(menuLayout);
        loadListView();
    }

    /*Resets the position of the
    load card from it's animated position
     */
    private void resetLoadCard() {
        TransitionManager.beginDelayedTransition(menuLayout);

        for (int i = 0; i < loadContainer.getChildCount(); i++) {
            loadContainer.getChildAt(i).setVisibility(View.GONE);
        }

        for (int i = 0; i < loadInnerContainer.getChildCount(); i++) {
            loadInnerContainer.getChildAt(i).setVisibility(View.GONE);
        }
        loadInnerContainer.setVisibility(View.VISIBLE);
        loadButton.setVisibility(View.VISIBLE);

        originalConstraint.applyTo(menuLayout);
        applyConstraintSet.clone(originalConstraint);
    }

    /*Start morph animation for creating
    a new exercise
     */
    private void createCardAnim() {
        TransitionManager.beginDelayedTransition(menuLayout);

        applyConstraintSet.centerVertically(R.id.card_create_route, R.id.layout_start_menu);
        applyConstraintSet.setMargin(R.id.card_create_route, ConstraintSet.BOTTOM, 0);

        for (int i = 0; i < createContainer.getChildCount(); i++) {
            createContainer.getChildAt(i).setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < createInnerContainer.getChildCount(); i++) {
            createInnerContainer.getChildAt(i).setVisibility(View.VISIBLE);
        }

        applyConstraintSet.applyTo(menuLayout);
    }

    /*Resets the position of the
    create card from it's animated position
     */
    private void resetCreateCard() {
        TransitionManager.beginDelayedTransition(menuLayout);

        for (int i = 0; i < createContainer.getChildCount(); i++) {
            createContainer.getChildAt(i).setVisibility(View.GONE);
        }
        for (int i = 0; i < createInnerContainer.getChildCount(); i++) {
            createInnerContainer.getChildAt(i).setVisibility(View.GONE);
        }
        createInnerContainer.setVisibility(View.VISIBLE);
        createButton.setVisibility(View.VISIBLE);

        originalConstraint.applyTo(menuLayout);
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

    /*Handle the creation of a new sql table for the
    exercise, passes name to editMode Activity. Checks
    if the new name currently exists or not
     */
    public void createButtonClicked(View view) {
        if (createIsCard) {
            String newName = nameEditT.getText().toString().trim();
            if (newName.equals("")) {
                nameEditT.setError(getString(R.string.blank_field));
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
                    dbHelper.saveNewRoutine(newName);

                    Intent createExercise = new Intent(StartMenuActivity.this, EditModeActivity.class);
                    createExercise.putExtra(KeyConstants.NAME, newName);
                    startActivity(createExercise);
                } else {
                    nameEditT.setError(getString(R.string.name_already_exists));
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

    public void loadCancelButtonClicked(View view) {
        loadIsCard = false;
        resetLoadCard();
    }

    public void createCancelButtonClicked(View view) {
        createIsCard = false;
        resetCreateCard();
    }


}
