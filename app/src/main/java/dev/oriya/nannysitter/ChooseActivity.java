package dev.oriya.nannysitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

public class ChooseActivity extends AppCompatActivity {
    private LinearLayout choose_LY_btns;
    private MaterialButton choose_BTN_baby;
    private MaterialButton choose_BTN_parent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        findViews();
        allBTNClicked();
    }

    private void allBTNClicked() {
        choose_BTN_baby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseActivity.this, BabyActivity.class));
                finish();
            }
        });

        choose_BTN_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseActivity.this, ProfileActivity.class));
                finish();

            }
        });
    }

    private void findViews() {
        choose_BTN_baby = findViewById(R.id.choose_BTN_baby);
        choose_BTN_parent = findViewById(R.id.choose_BTN_parent);
        choose_LY_btns = findViewById(R.id.choose_LY_btns);
    }
}