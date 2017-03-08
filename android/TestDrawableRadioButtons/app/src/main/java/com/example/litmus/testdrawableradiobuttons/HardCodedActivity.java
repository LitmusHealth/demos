package com.example.litmus.testdrawableradiobuttons;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HardCodedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Hard Coded Layout");

        setContentView(R.layout.activity_hard_coded);

        findViewById(R.id.hard_coded_switch_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HardCodedActivity.this, ProgrammaticActivity.class));
            }
        });
    }
}
