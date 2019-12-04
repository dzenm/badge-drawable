package com.dzenm.badgedrawable;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.dzenm.library.BadgeHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonTopLeft) {
            drawBadge(99, BadgeHelper.BadgePosition.TOP_RIGHT);
        }else if (v.getId() == R.id.buttonTopRight) {
            drawBadge(0, BadgeHelper.BadgePosition.TOP_RIGHT);
        }else if (v.getId() == R.id.buttonBottomLeft) {
            drawBadge(599, BadgeHelper.BadgePosition.BOTTOM_RIGHT);
        }else if (v.getId() == R.id.buttonBottomRight) {
            drawBadge(1000, BadgeHelper.BadgePosition.BOTTOM_RIGHT);
        }else if (v.getId() == R.id.buttonReset) {
            drawBadge(-1, BadgeHelper.BadgePosition.TOP_RIGHT);
        }
    }

    private void drawBadge(int number, int position) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int drawableResId;
        if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonSelectorDrawable) {
            drawableResId = R.drawable.selector_badge;
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonVectorDrawable) {
            drawableResId = R.drawable.ic_notifications;
        } else {
            drawableResId = R.drawable.ic_launcher;
        }

        ImageView imageViewBadge = findViewById(R.id.imageViewBadge);
        imageViewBadge.setImageResource(drawableResId);

        new BadgeHelper.Builder(getApplicationContext())
                .setImageView(imageViewBadge)
                .setNumber(number)
                .setBadgePosition(position)
                .build();
    }
}
