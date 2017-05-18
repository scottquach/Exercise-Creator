package com.example.scott.excecisecreator;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/*
Research resources used
https://android.jlelse.eu/make-your-app-shine-2-how-to-make-a-button-morph-into-a-form-81d2f0e6bf4a
http://www.uwanttolearn.com/android/constraint-layout-animations-dynamic-constraints-ui-java-hell/
 */

public class StartMenuActivity extends AppCompatActivity {

    private ConstraintLayout menuLayout;
    private ConstraintSet originalConstraint = new ConstraintSet();
    private ConstraintSet applyConstraintSet = new ConstraintSet();

    private LinearLayout loadContainer;
    private LinearLayout createContainer;

    private boolean loadIsCard = false;
    private boolean createIsCard = false;

    private Button createButton;
    private Button loadButton;

    private EditText nameEditT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        //initialize
        menuLayout = (ConstraintLayout) findViewById(R.id.StartMenuLayout);
        applyConstraintSet.clone(menuLayout);
        originalConstraint.clone(menuLayout);
        loadContainer = (LinearLayout) findViewById(R.id.loadExerciseContainer);
        createContainer = (LinearLayout) findViewById(R.id.createExerciseContainer);
        createButton = (Button) findViewById(R.id.createButton);
        loadButton = (Button) findViewById(R.id.loadButton);
        nameEditT = (EditText) findViewById(R.id.setNameEditText);
    }

    /*Start morph animation for loading
    an exercise
     */
    private void loadCardAnim(){
        TransitionManager.beginDelayedTransition(menuLayout);

        applyConstraintSet.centerVertically(R.id.loadExerciseCard, R.id.StartMenuLayout);
        applyConstraintSet.setMargin(R.id.loadExerciseCard,ConstraintSet.TOP,0);

        for (int i = 0; i < loadContainer.getChildCount(); i++) {
            loadContainer.getChildAt(i).setVisibility(View.VISIBLE);
        }

        applyConstraintSet.applyTo(menuLayout);
    }

    /*Resets the position of the
    load card from it's animated position
     */
    private void resetLoadCard(){
        TransitionManager.beginDelayedTransition(menuLayout);

        for (int i = 0; i < loadContainer.getChildCount(); i++){
            loadContainer.getChildAt(i).setVisibility(View.GONE);
        }
        loadButton.setVisibility(View.VISIBLE);

        originalConstraint.applyTo(menuLayout);
        applyConstraintSet.clone(originalConstraint);
    }

    /*Start morph animation for creating
    a new exercise
     */
    private void createCardAnim(){
        TransitionManager.beginDelayedTransition(menuLayout);

        applyConstraintSet.centerVertically(R.id.createExerciseCard, R.id.StartMenuLayout);
        applyConstraintSet.setMargin(R.id.createExerciseCard, ConstraintSet.BOTTOM,0);

        for (int i = 0; i < createContainer.getChildCount(); i++){
            createContainer.getChildAt(i).setVisibility(View.VISIBLE);
        }

        applyConstraintSet.applyTo(menuLayout);
    }

    /*Rsets the position of the
    create card from it's animated position
     */
    private void resetCreateCard(){
        TransitionManager.beginDelayedTransition(menuLayout);

        for (int i = 0; i < createContainer.getChildCount(); i++){
            createContainer.getChildAt(i).setVisibility(View.GONE);
        }
        createButton.setVisibility(View.VISIBLE);

        originalConstraint.applyTo(menuLayout);
        applyConstraintSet.clone(originalConstraint);
    }

    public void loadButtonClicked(View view) {
        if(loadIsCard){
            String exerciseName = nameEditT.getText().toString();
            if (exerciseName.equals("")){
                nameEditT.setError("Field cannot be blank");
            }else{
                Intent loadExercise = new Intent(StartMenuActivity.this, ExceriseActivity.class);
                startActivity(loadExercise);
            }
        }else{
            loadCardAnim();
            loadIsCard = true;
        }
    }

    public void createButtonClicked(View view) {
        if (createIsCard){
            Intent createExercise = new Intent(StartMenuActivity.this, EditModeActivity.class);
            startActivity(createExercise);
        }else{
            createCardAnim();
            createIsCard = true;
        }
    }

    public void loadCancelButtonClicked(View view) {
        resetLoadCard();
        loadIsCard = false;
    }

    public void createCancelButtonClicked(View view) {
        resetCreateCard();
        createIsCard = false;
    }


}
