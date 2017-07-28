package com.example.scott.excecisecreator;

import android.content.Intent;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.scott.excecisecreator.database.DataBaseHelper;

import java.util.ArrayList;

import butterknife.BindView;

/*
Research resources used
https://android.jlelse.eu/make-your-app-shine-2-how-to-make-a-button-morph-into-a-form-81d2f0e6bf4a
http://www.uwanttolearn.com/android/constraint-layout-animations-dynamic-constraints-ui-java-hell/
 */

public class StartMenuDataActivity extends BaseDataActivity {

    private ConstraintSet originalConstraint = new ConstraintSet();
    private ConstraintSet applyConstraintSet = new ConstraintSet();

    private boolean loadIsCard, createIsCard, recyclerLoaded = false;

    @BindView(R.id.StartMenuLayout)
    ConstraintLayout menuLayout;
    @BindView(R.id.loadExerciseContainer)
    LinearLayout loadContainer;
    @BindView(R.id.loadExerciseInnerContainer)
    LinearLayout loadInnerContainer;
    @BindView(R.id.createExerciseContainer)
    LinearLayout createContainer;
    @BindView(R.id.createExerciseInnerContainer)
    LinearLayout createInnerContainer;


    @BindView(R.id.setNameEditText)
    TextView nameEditT;
    @BindView(R.id.createButton)
    Button createButton;
    @BindView(R.id.loadButton)
    Button loadButton;

    @BindView(R.id.exercisesListView)
    ListView exercisesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        //initialize

        applyConstraintSet.clone(menuLayout);
        originalConstraint.clone(menuLayout);
    }

    /*Returns an ArrayLists of the names of
    saved exercises
     */
    private ArrayList<String> loadExerciseNames() {
        ArrayList<String> names = new ArrayList<String>();
        Cursor cursor = dbHelper.getExerciseNames();

        int count = cursor.getCount();
        Log.d("debug", String.valueOf(count));

        if (cursor != null && cursor.moveToFirst()) {
            Log.d("debug", "Cursor wasn't empty");
            do {
                String temp = cursor.getString(0);
                Log.d("debug", temp);
                names.add(temp);
            } while (cursor.moveToNext());
        } else {
            Log.d("debug", "no saved exercises");
        }

        cursor.close();
        return names;
    }

    /*Loads the listview of saved exercises located in
    the load CardView
     */
    private void loadListView() {
        final ArrayList<String> names = loadExerciseNames();
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.listview_simple_row, names);
        exercisesListView.setAdapter(adapter);

        recyclerLoaded = true;

        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recyclerLoaded = false;
                String exerciseName = names.get(position);
                Intent openEditMode = new Intent(StartMenuDataActivity.this, RoutineDataActivity.class);
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

        applyConstraintSet.centerVertically(R.id.loadExerciseCard, R.id.StartMenuLayout);
        applyConstraintSet.setMargin(R.id.loadExerciseCard, ConstraintSet.TOP, 0);

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

        applyConstraintSet.centerVertically(R.id.createExerciseCard, R.id.StartMenuLayout);
        applyConstraintSet.setMargin(R.id.createExerciseCard, ConstraintSet.BOTTOM, 0);

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
                        Log.d("name matching", newName + " : " + current);
                        doesExist = true;
                    }
                }

                if (!doesExist) {
                    dbHelper.saveNewExercise(newName);

                    Intent createExercise = new Intent(StartMenuDataActivity.this, EditModeDataActivity.class);
                    createExercise.putExtra("name", newName);
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
