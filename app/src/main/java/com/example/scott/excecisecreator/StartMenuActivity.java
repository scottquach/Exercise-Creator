package com.example.scott.excecisecreator;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.TransitionManager;
import android.view.View;

/*
Research resources used
https://android.jlelse.eu/make-your-app-shine-2-how-to-make-a-button-morph-into-a-form-81d2f0e6bf4a
http://www.uwanttolearn.com/android/constraint-layout-animations-dynamic-constraints-ui-java-hell/
 */

public class StartMenuActivity extends AppCompatActivity {

    private ConstraintLayout menuLayout;
    private ConstraintSet originalConstraint = new ConstraintSet();
    private ConstraintSet applyConstraintSet = new ConstraintSet();

    private CardView loadCard;
    private CardView createCard;

    private boolean loadIsCard = false;
    private boolean createIsCard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        //initialize
        menuLayout = (ConstraintLayout) findViewById(R.id.StartMenuLayout);
        applyConstraintSet.clone(menuLayout);
        originalConstraint.clone(menuLayout);
        loadCard = (CardView) findViewById(R.id.loadExerciseCard);
        createCard = (CardView) findViewById(R.id.createExerciseCard);
    }

    /*Start morph animation for loading
    an exercise
     */
    private void loadCardAnim(){
        TransitionManager.beginDelayedTransition(menuLayout);

        applyConstraintSet.centerVertically(R.id.loadExerciseCard, R.id.StartMenuLayout);
        applyConstraintSet.setMargin(R.id.loadExerciseCard,ConstraintSet.TOP,0);

        for (int i = 1; i < loadCard.getChildCount(); i++) {
            loadCard.getChildAt(i).setVisibility(View.VISIBLE);
        }

        applyConstraintSet.applyTo(menuLayout);
    }

    /*Resets the position of the
    load card from it's animated position
     */
    private void resetLoadCard(){
        TransitionManager.beginDelayedTransition(menuLayout);

        for (int i = 0; i < loadCard.getChildCount(); i++){
            loadCard.getChildAt(i).setVisibility(View.GONE);
        }

        originalConstraint.applyTo(menuLayout);
    }

    /*Start morph animation for creating
    a new exercise
     */
    private void createCardAnim(){
        TransitionManager.beginDelayedTransition(menuLayout);

        applyConstraintSet.centerVertically(R.id.createExerciseCard, R.id.StartMenuLayout);
        applyConstraintSet.setMargin(R.id.loadExerciseCard, ConstraintSet.BOTTOM,0);

        for (int i = 0; i < createCard.getChildCount(); i++){
            loadCard.getChildAt(i).setVisibility(View.VISIBLE);
        }

        applyConstraintSet.applyTo(menuLayout);
    }

    /*Rsets the position of the
    create card from it's animated position
     */
    private void resetCreateCard(){
        TransitionManager.beginDelayedTransition(menuLayout);

        for (int i = 0; i < createCard.getChildCount(); i++){
            createCard.getChildAt(i).setVisibility(View.GONE);
        }

        originalConstraint.applyTo(menuLayout);
    }

    public void loadButtonClicked(View view) {
        if(loadIsCard){
            Intent loadExercise = new Intent(StartMenuActivity.this, ExceriseActivity.class);
            startActivity(loadExercise);
        }else{
            loadCardAnim();
            loadIsCard = true;
        }
    }

    public void createButtonClicked(View view) {
        if (createIsCard){
            Intent createExercise = new Intent(StartMenuActivity.this, EditModeACtivity.class);
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
