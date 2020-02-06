package com.dzenm.badgedrawable;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dzenm.library.BadgeDrawable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etNumber, etBadgeBorderSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNumber = findViewById(R.id.et_number);
        etBadgeBorderSize = findViewById(R.id.et_border_size);

        // 获取imei
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imei = manager.getDeviceId();
//        etNumber.setText(imei);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.build) {
            String number = etNumber.getText().toString();
            if (TextUtils.isEmpty(number)) {
                Toast.makeText(this, "请输入显示的数量", Toast.LENGTH_SHORT).show();
                return;
            }
            drawBadge(Integer.valueOf(number));
        } else if (v.getId() == R.id.reset) {
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
        boolean isCircle;
        if (circleRadioGroup.getCheckedRadioButtonId() == R.id.circle) {
            isCircle = true;
        } else {
            isCircle = false;
        }

        RadioGroup positionRadioGroup = findViewById(R.id.positionRadioGroup);
        int positionBadge = BadgeDrawable.BadgePosition.TOP_LEFT;
        if (positionRadioGroup.getCheckedRadioButtonId() == R.id.tl) {
            positionBadge = BadgeDrawable.BadgePosition.TOP_LEFT;
        } else if (positionRadioGroup.getCheckedRadioButtonId() == R.id.tr) {
            positionBadge = BadgeDrawable.BadgePosition.TOP_RIGHT;
        } else if (positionRadioGroup.getCheckedRadioButtonId() == R.id.bl) {
            positionBadge = BadgeDrawable.BadgePosition.BOTTOM_LEFT;
        } else if (positionRadioGroup.getCheckedRadioButtonId() == R.id.br) {
            positionBadge = BadgeDrawable.BadgePosition.BOTTOM_RIGHT;
        }

        ImageView imageViewBadge = findViewById(R.id.imageViewBadge);

        int badgeBorderSize = Integer.valueOf(etBadgeBorderSize.getText().toString());
        new BadgeDrawable.Builder(getApplicationContext())
                .setDrawable(drawableResId)
                .setCircle(isCircle)
                .setNumber(number)
                .setBadgeBorderSize(badgeBorderSize)
                .setBadgePosition(positionBadge)
                .build(imageViewBadge);
    }
}
