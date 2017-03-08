package com.example.litmus.testdrawableradiobuttons;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ProgrammaticActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Programmatic Layout");

        setContentView(R.layout.activity_programmatic);

        RadioGroup choiceSelect = (RadioGroup) findViewById(R.id.image_choice_list);
        Resources resources = getResources();

        for (Integer i = 1; i < 7; i++) {
            RadioButton imageButton = new RadioButton(this);
            float weight = 1f;
            if (i == 6) weight = 0f;
            imageButton.setLayoutParams(
                    new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                weight));

            String faceID = "faces_" + i.toString();
            int imgId = resources.getIdentifier(faceID, "drawable", getPackageName());
            imageButton.setButtonDrawable(imgId);
            imageButton.setBackgroundResource(R.drawable.radio_image_selector);
            choiceSelect.addView(imageButton);
        }

        findViewById(R.id.programmatic_switch_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProgrammaticActivity.this, HardCodedActivity.class));
            }
        });
    }
}
