package com.dzenm.library;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.TypedValue;
import android.widget.ImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * <pre>
 * BadgeHelper.Builder(applicationContext)
 *         .setImageView(imageViewBadge)
 *         .setNumber(number)
 *         .setBadgePosition(position)
 *         .build()
 * </pre>
 */
public class BadgeHelper {

    private static final int DEFAULT_COUNT = 0;
    private static final int MIDDLE_COUNT = 99;
    private static final int MAXIMUM_COUNT = 999;

    private static Context mContext;

    private @ColorInt
    int mTextColor, mBadgeColor, mBadgeBorderColor;

    private float mBadgeSize = dp2px(20), mBadgeBorderSize = dp2px(2);

    private @BadgePosition
    int mBadgePosition = BadgePosition.TOP_RIGHT;

    private int mNumber = DEFAULT_COUNT, mMaximumNumber = MAXIMUM_COUNT;

    private boolean isCircle = false;

    @IntDef()
    @Retention(RetentionPolicy.SOURCE)
    public @interface BadgePosition {
        int TOP_RIGHT = 1;
        int BOTTOM_RIGHT = 2;
    }

    private BadgeHelper(Context context) {
        mContext = context;
        mTextColor = getColor(android.R.color.white);
        mBadgeColor = getColor(android.R.color.holo_red_light);
        mBadgeBorderColor = getColor(android.R.color.white);
    }

    private void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    private void setBadgeColor(int badgeColor) {
        mBadgeColor = badgeColor;
    }

    private void setBadgeSize(float badgeSize) {
        mBadgeSize = badgeSize;
    }

    private void setBadgeBorderColor(int badgeBorderColor) {
        mBadgeBorderColor = badgeBorderColor;
    }

    private void setBadgeBorderSize(float badgeBorderSize) {
        mBadgeBorderSize = badgeBorderSize;
    }

    private void setBadgePosition(@BadgePosition int badgePosition) {
        mBadgePosition = badgePosition;
    }

    private void setCircle(boolean circle) {
        isCircle = circle;
    }

    private int getNumber() {
        return mNumber;
    }

    private void setNumber(int number) {
        mNumber = number;
    }

    private void setMaximumNumber(int maximumNumber) {
        mMaximumNumber = maximumNumber;
    }

    private Bitmap buildBitmap(Bitmap bitmap) {
        // 计算显示的文本内容
        String numberText = mNumber > mMaximumNumber ? mMaximumNumber + "+" : String.valueOf(mNumber);

        // 计算显示文本大小和区域
        Rect textBounds = new Rect();
        float numberTextSize = numberTextPercent() * numberTextBaseValue();
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(mTextColor);
        textPaint.setTextSize(numberTextSize);
        textPaint.getTextBounds(numberText, 0, numberText.length(), textBounds);

        // 测量Badge的宽高
        float badgeWidth, badgeHeight;
        if (isDefaultCount()) {
            badgeWidth = badgeHeight = mBadgeSize / 2f;
        } else if (isCircle) {
            badgeWidth = badgeHeight = mBadgeSize;
        } else if (isDigits()) {
            badgeWidth = badgeHeight = textBounds.height() * 2f;
        } else {
            badgeHeight = textBounds.height() * 2f;
            badgeWidth = textBounds.width() + badgeHeight / 2f;
        }


        // 测量Badge的所在的位置
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        RectF badgeRect = null;
        if (mBadgePosition == BadgePosition.TOP_RIGHT) {
            badgeRect = new RectF(width * 1.3f,
                    mBadgeBorderSize,
                    width * 1.3f + badgeWidth,
                    badgeHeight + mBadgeBorderSize);
        } else if (mBadgePosition == BadgePosition.BOTTOM_RIGHT) {
            badgeRect = new RectF(
                    width * 1.3f,
                    height - badgeHeight,
                    width * 1.3f + badgeWidth,
                    height * 1f);
        }

        // 创建带有Badge的Bitmap
        Bitmap outputBitmap = Bitmap.createBitmap(width * 2, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);

        // 绘制原图片
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(mBadgeColor);

        Rect rect = new Rect(0, 0, width, height);
        canvas.translate(width / 2f, 0f);
        canvas.drawBitmap(bitmap, rect, rect, paint);
        canvas.translate(-width / 2f, 0f);

        // 绘制Badge的边框
        if (mBadgeBorderSize > 0) {
            Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setFilterBitmap(true);
            borderPaint.setDither(true);
            borderPaint.setTextAlign(Paint.Align.CENTER);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setColor(mBadgeBorderColor);
            borderPaint.setStrokeWidth(mBadgeBorderSize);
            drawBackground(canvas, badgeRect, borderPaint);
        }

        // 绘制Badge的背景
        drawBackground(canvas, badgeRect, paint);

        // 绘制Badge的文本
        if (!isDefaultCount()) {
            float x = badgeRect.centerX() - (textPaint.measureText(numberText) * 0.5f);
            float y = badgeRect.centerY() - (textPaint.ascent() + textPaint.descent()) * 0.5f;
            canvas.drawText(numberText, x, y, textPaint);
        }
        return outputBitmap;
    }

    /**
     * 计算数量文本大小所占百分比
     */
    private float numberTextPercent() {
        if (isCircle) {
            if (mNumber > MAXIMUM_COUNT) {
                return 0.4f;
            } else if (mNumber > MIDDLE_COUNT) {
                return 0.5f;
            } else {
                return 0.6f;
            }
        } else {
            return 0.6f;
        }
    }

    /**
     * 计算数量文本的基值
     */
    private float numberTextBaseValue() {
        return mBadgeSize - mBadgeBorderSize;
    }

    /**
     * 绘制背景
     */
    private void drawBackground(Canvas canvas, RectF badgeRect, Paint paint) {
        if (isCircle || isDigits()) {   // 是否绘制椭圆的Badge
            canvas.drawOval(badgeRect, paint);
        } else {
            // 第二个参数是x半径, 第三个参数是y半径
            canvas.drawRoundRect(badgeRect, mBadgeSize * 0.4f, mBadgeSize * 0.4f, paint);
        }
    }

    /**
     * 是否是个位的数量
     */
    private boolean isDigits() {
        return mNumber <= MIDDLE_COUNT % 10 && mNumber > DEFAULT_COUNT;
    }

    /**
     * 是否是默认数量
     */
    private boolean isDefaultCount() {
        return mNumber == DEFAULT_COUNT;
    }

    public static class Builder {

        private BadgeHelper mBadgeHelper;
        private ImageView mImageView;

        public Builder(Context context) {
            mBadgeHelper = new BadgeHelper(context);
        }

        public Builder setTextColor(int textColor) {
            mBadgeHelper.setTextColor(getColor(textColor));
            return this;
        }

        public Builder setBadgeColor(int badgeColor) {
            mBadgeHelper.setBadgeColor(getColor(badgeColor));
            return this;
        }

        public Builder setBadgeSize(float badgeSize) {
            mBadgeHelper.setBadgeSize(dp2px(badgeSize));
            return this;
        }

        public Builder setBadgeBorderColor(int badgeBorderColor) {
            mBadgeHelper.setBadgeBorderColor(getColor(badgeBorderColor));
            return this;
        }

        public Builder setBadgeBorderSize(float badgeBorderSize) {
            mBadgeHelper.setBadgeBorderSize(dp2px(badgeBorderSize));
            return this;
        }

        public Builder setBadgePosition(@BadgePosition int badgePosition) {
            mBadgeHelper.setBadgePosition(badgePosition);
            return this;
        }

        public Builder setCircle(boolean circle) {
            mBadgeHelper.setCircle(circle);
            return this;
        }

        /**
         * @param number {@link #mNumber} 大于 {@link #mMaximumNumber}, 显示{@link #mMaximumNumber}
         *               {@link #mNumber} 等于 {@link #DEFAULT_COUNT}, 显示点
         *               {@link #mNumber} 小于 {@link #DEFAULT_COUNT}, 不显示点
         * @return this
         */
        public Builder setNumber(int number) {
            mBadgeHelper.setNumber(number);
            return this;
        }

        public Builder setMaximumNumber(int maximumNumber) {
            mBadgeHelper.setMaximumNumber(maximumNumber);
            return this;
        }

        public Builder setImageView(ImageView imageView) {
            mImageView = imageView;
            return this;
        }

        public void build() {
            Drawable drawable = mImageView.getDrawable();
            if (drawable == null) {
                throw new NullPointerException("please set src for imageView");
            }
            Bitmap bitmap;
            drawable = DrawableCompat.wrap(drawable);
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                bitmap = createBitmapFromDrawable(drawable);
            }

            if (mBadgeHelper.getNumber() >= 0) {
                bitmap = mBadgeHelper.buildBitmap(bitmap);
            }
            mImageView.setImageDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
        }

        /**
         * 通过Drawable创建Bitmap
         */
        private Bitmap createBitmapFromDrawable(Drawable drawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    private static Float dp2px(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

    private static int getColor(int resId) {
        return ContextCompat.getColor(mContext, resId);
    }
}