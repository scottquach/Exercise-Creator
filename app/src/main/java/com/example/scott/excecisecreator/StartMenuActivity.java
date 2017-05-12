package com.example.scott.excecisecreator;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;

public class StartMenuActivity extends AppCompatActivity {

    private ConstraintLayout menuLayout;
    private ConstraintSet originalConstraint = new ConstraintSet();

    private CardView loadCard;
    private CardView createCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        //initialize
        menuLayout = (ConstraintLayout) findViewById(R.id.StartMenuLayout);
        originalConstraint.clone(menuLayout);
        loadCard = (CardView) findViewById(R.id.loadExerciseCard);
        createCard = (CardView) findViewById(R.id.createExerciseCard);
    }

    /*Start morph animation for loading
    an exercise
     */
    private void loadCardMorphAnim(){


    }

    /*Start morph animation for creating
    a new exercise
     */
    private void createCardMorphAnim(){

    }
}
