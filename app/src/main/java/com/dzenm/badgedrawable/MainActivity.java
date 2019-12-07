package com.dzenm.badgedrawable;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

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
        if (v.getId() == R.id.build) {
            EditText editText = findViewById(R.id.et_number);
            String number = editText.getText().toString();
            if (TextUtils.isEmpty(number)) {
                Toast.makeText(this, "请输入显示的数量", Toast.LENGTH_SHORT).show();
                return;
            }
            drawBadge(Integer.valueOf(number));
        }else if (v.getId() == R.id.reset) {
            drawBadge(-1);
        }
    }

    private void drawBadge(int number) {
        RadioGroup drawableRadioGroup = findViewById(R.id.pictureRadioGroup);
        int drawableResId = 0;
        if (drawableRadioGroup.getCheckedRadioButtonId() == R.id.selecto) {
            drawableResId = R.drawable.ic_notifications;
        } else {
            drawableResId = R.mipmap.ic_launcher_image;
        }

        RadioGroup circleRadioGroup = findViewById(R.id.circleRadioGroup);
        boolean  isCircle;
        if (circleRadioGroup.getCheckedRadioButtonId() == R.id.circle) {
            isCircle = true;
        } else {
            isCircle = false;
        }

        RadioGroup innerRadioGroup = findViewById(R.id.innerRadioGroup);
        boolean  isInner;
        if (innerRadioGroup.getCheckedRadioButtonId() == R.id.inner) {
            isInner = true;
        } else {
            isInner = false;
        }

        RadioGroup positionRadioGroup = findViewById(R.id.positionRadioGroup);
        int positionBadge = BadgeHelper.BadgePosition.TOP_LEFT;
        if (positionRadioGroup.getCheckedRadioButtonId() == R.id.tl) {
            positionBadge = BadgeHelper.BadgePosition.TOP_LEFT;
        } else if (positionRadioGroup.getCheckedRadioButtonId() == R.id.tr){
            positionBadge = BadgeHelper.BadgePosition.TOP_RIGHT;
        }else if (positionRadioGroup.getCheckedRadioButtonId() == R.id.bl){
            positionBadge = BadgeHelper.BadgePosition.BOTTOM_LEFT;
        }else if (positionRadioGroup.getCheckedRadioButtonId() == R.id.br){
            positionBadge = BadgeHelper.BadgePosition.BOTTOM_RIGHT;
        }

        ImageView imageViewBadge = findViewById(R.id.imageViewBadge);

        Bitmap bitmap = new BadgeHelper.Builder(getApplicationContext())
                .setDrawable(drawableResId)
                .setCircle(isCircle)
                .setInner(isInner)
                .setNumber(number)
                .setBadgePosition(positionBadge)
                .build();
        imageViewBadge.setImageBitmap(bitmap);
    }
}
