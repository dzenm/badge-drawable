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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.core.content.ContextCompat;

/**
 * <pre>
 * Bitmap bitmap = new BadgeDrawable.Builder(getApplicationContext())
 *        .setDrawable(drawableResId)
 *        .setCircle(isCircle)
 *        .setInner(isInner)
 *        .setNumber(number)
 *        .setBadgePosition(positionBadge)
 *        .build();
 * imageViewBadge.setImageBitmap(bitmap);
 * </pre>
 */
public class BadgeDrawable {

    private static final int DEFAULT_COUNT = 0;
    private static final int MIDDLE_COUNT = 99;
    private static final int MAXIMUM_COUNT = 999;

    private static final float DEFAULT_BADGE_SIZE = dp2px(20);
    private static final float DEFAULT_BADGE_CIRCLE_SIZE = dp2px(16);
    private static final float DEFAULT_BODER_SIZE = dp2px(2);

    private static Context mContext;

    /**
     * Badge文本颜色, Badge背景颜色, Badge边框颜色
     */
    @ColorInt
    private int mTextColor, mBadgeColor, mBadgeBorderColor;

    /**
     * Badge所在的位置
     */
    @BadgePosition
    private int mBadgePosition = BadgePosition.TOP_RIGHT;

    /**
     * Badge大小, Badge边框大小
     */
    private float mBadgeSize, mBadgeBorderSize;

    /**
     * Badge显示的数量, 默认为{@link #DEFAULT_COUNT}, 只显示红点, 当大于0时, 显示具体的数字,
     * 默认当大于999时, 显示999+, 通过 {@link #mMaximumNumber} 可以设置最大显示的数量,
     * 可以设置 {@link #MIDDLE_COUNT}, {@link #MAXIMUM_COUNT}
     */
    private int mNumber = DEFAULT_COUNT, mMaximumNumber = MAXIMUM_COUNT;

    /**
     * {@link #isCircle} 为false时, 显示为椭圆形, 为true时, 显示为圆形
     * {@link #isInner} 为false时, Badge显示在外面, 为true时, Badge显示在里面
     */
    private boolean isCircle, isInner;

    @IntDef({
            BadgePosition.TOP_LEFT,
            BadgePosition.TOP_RIGHT,
            BadgePosition.BOTTOM_LEFT,
            BadgePosition.BOTTOM_RIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BadgePosition {
        int TOP_LEFT = 1;
        int TOP_RIGHT = 2;
        int BOTTOM_LEFT = 3;
        int BOTTOM_RIGHT = 4;
    }

    private BadgeDrawable(Context context) {
        mContext = context;
        mTextColor = getColor(android.R.color.white);
        mBadgeColor = getColor(android.R.color.holo_red_light);
        mBadgeBorderColor = getColor(android.R.color.white);
        isCircle = false;
        isInner = true;
        if (isCircle) {
            mBadgeSize = DEFAULT_BADGE_CIRCLE_SIZE;
        } else {
            mBadgeSize = DEFAULT_BADGE_SIZE;
        }
        mBadgeBorderSize = DEFAULT_BODER_SIZE;
    }

    private int getNumber() {
        return mNumber;
    }

    private Bitmap buildBitmap(Bitmap bitmap) {
        // 计算显示的文本内容
        mMaximumNumber = isInner || isCircle? MIDDLE_COUNT : MAXIMUM_COUNT;
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
            badgeWidth = badgeHeight = dp2px(10);
        } else if (isCircle) {
            badgeWidth = badgeHeight = mBadgeSize;
        } else if (isDigits()) {
            badgeWidth = badgeHeight = textBounds.height() + dp2px(8);
        } else {
            badgeHeight = textBounds.height() + dp2px(6);
            badgeWidth = textBounds.width() + dp2px(6);
        }

        // 测量Badge的所在的位置
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        RectF badgeRect = null;
        if (isInner) {
            if (mBadgePosition == BadgePosition.TOP_RIGHT) {
                badgeRect = new RectF(width - badgeWidth,
                        0,
                        width,
                        badgeHeight);
            } else if (mBadgePosition == BadgePosition.BOTTOM_RIGHT) {
                badgeRect = new RectF(width - badgeWidth,
                        height - badgeHeight,
                        width,
                        height);
            } else if (mBadgePosition == BadgePosition.TOP_LEFT) {
                badgeRect = new RectF(0,
                        0,
                        badgeWidth,
                        badgeHeight);
            } else if (mBadgePosition == BadgePosition.BOTTOM_LEFT) {
                badgeRect = new RectF(0,
                        height - badgeHeight ,
                         badgeWidth,
                        height);
            }
        } else {
            if (mBadgePosition == BadgePosition.TOP_RIGHT) {
                badgeRect = new RectF(width * 3 / 4 + badgeWidth,
                        0,
                        width * 3 / 4 + badgeWidth * 2,
                        badgeHeight);
            } else if (mBadgePosition == BadgePosition.BOTTOM_RIGHT) {
                badgeRect = new RectF(
                        width * 3 / 4 + badgeWidth,
                        height - badgeHeight,
                        width * 3 / 4 + badgeWidth * 2,
                        height);
            } else if (mBadgePosition == BadgePosition.TOP_LEFT) {
                badgeRect = new RectF(
                        width * 1 / 4,
                        0,
                        width * 1 / 4 + badgeWidth,
                        badgeHeight);
            } else if (mBadgePosition == BadgePosition.BOTTOM_LEFT) {
                badgeRect = new RectF(
                        width * 1 / 4,
                        height - badgeHeight,
                        width * 1 / 4 + badgeWidth,
                        height);
            }
        }

        // 创建带有Badge的Bitmap
        Bitmap outputBitmap;
        if (isInner) {
            outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } else {
            outputBitmap = Bitmap.createBitmap(
                    (int) (width + 2 * badgeWidth),
                    height,
                    Bitmap.Config.ARGB_8888
            );
        }
        Canvas canvas = new Canvas(outputBitmap);

        // 绘制原图片
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(mBadgeColor);

        Rect rect = new Rect(0, 0, width, height);
        if (!isInner) { canvas.translate(badgeWidth, 0f); }
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (!isInner) { canvas.translate(-badgeWidth, 0f); }

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
            if (isInner) {
                return 0.6f;
            } else {
                if (mNumber > MIDDLE_COUNT) {
                    return 0.5f;
                } else {
                    return 0.6f;
                }
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
     * 绘制Badge背景
     */
    private void drawBackground(Canvas canvas, RectF badgeRect, Paint paint) {
        if (isCircle || isDigits()) {   // 是否绘制椭圆的Badge
            canvas.drawOval(badgeRect, paint);
        } else {
            // 第二个参数是x半径, 第三个参数是y半径
            canvas.drawRoundRect(badgeRect, mBadgeSize * 0.4f, mBadgeSize * 0.4f, paint);
        }
    }

    private boolean isDigits() {
        // 是否是个位的数量
        return mNumber <= MIDDLE_COUNT % 10 && mNumber > DEFAULT_COUNT;
    }

    private boolean isDefaultCount() {
        // 是否是默认数量
        return mNumber == DEFAULT_COUNT;
    }

    public static class Builder {

        private BadgeDrawable mBadgeDrawable;
        private Bitmap mBitmap;

        public Builder(Context context) {
            mBadgeDrawable = new BadgeDrawable(context);
        }

        /**
         * @param textColor Badge文本的颜色, 默认为白色
         * @return this
         */
        public Builder setTextColor(@ColorRes int textColor) {
            mBadgeDrawable.mTextColor = getColor(textColor);
            return this;
        }

        /**
         * @param badgeColor Badge背景颜色, 默认为红色
         * @return this
         */
        public Builder setBadgeColor(@ColorRes int badgeColor) {
            mBadgeDrawable.mBadgeColor = getColor(badgeColor);
            return this;
        }

        /**
         * @param badgeSize Badge的大小, 圆形时默认大小为 {@link #DEFAULT_BADGE_CIRCLE_SIZE},
         *                  椭圆时默认大小为 {@link #DEFAULT_BADGE_SIZE},
         * @return this
         */
        public Builder setBadgeSize(float badgeSize) {
            mBadgeDrawable.mBadgeSize = dp2px(badgeSize);
            return this;
        }

        /**
         * @param badgeBorderColor Badge边框颜色, 默认为白色
         * @return this
         */
        public Builder setBadgeBorderColor(@ColorRes int badgeBorderColor) {
            mBadgeDrawable.mBadgeBorderColor = getColor(badgeBorderColor);
            return this;
        }

        /**
         * @param badgeBorderSize Badge边框大, 默认为 {@link #DEFAULT_BODER_SIZE}
         * @return this
         */
        public Builder setBadgeBorderSize(float badgeBorderSize) {
            mBadgeDrawable.mBadgeBorderSize = dp2px(badgeBorderSize);
            return this;
        }

        /**
         * @param badgePosition Badge显示的位置, 可选值见 {@link BadgePosition}, 默认在右上角
         * @return this
         */
        public Builder setBadgePosition(@BadgePosition int badgePosition) {
            mBadgeDrawable.mBadgePosition = badgePosition;
            return this;
        }

        /**
         * @param circle Badge显示的形状是否是圆形, 默认为否
         * @return this
         */
        public Builder setCircle(boolean circle) {
            mBadgeDrawable.isCircle = circle;
            return this;
        }

        /**
         * @param inner Badge显示是否在Drawable的内部, 默认为是
         * @return this
         */
        public Builder setInner(boolean inner) {
            mBadgeDrawable.isInner = inner;
            return this;
        }

        /**
         * @param number {@link #mNumber} 大于 {@link #mMaximumNumber}, 显示{@link #mMaximumNumber}
         *               {@link #mNumber} 等于 {@link #DEFAULT_COUNT}, 显示点
         *               {@link #mNumber} 小于 {@link #DEFAULT_COUNT}, 不显示点
         * @return this
         */
        public Builder setNumber(int number) {
            mBadgeDrawable.mNumber = number;
            return this;
        }

        /**
         * @param resId 需要添加Badge的Image Resource ID
         * @return this
         */
        public Builder setDrawable(@DrawableRes int resId) {
            setDrawable(mContext.getResources().getDrawable(resId,null));
            return this;
        }

        /**
         * @param drawable 需要添加Badge的Drawable
         * @return this
         */
        public Builder setDrawable(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                mBitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                mBitmap = createBitmapFromDrawable(drawable);
            }
            return this;
        }

        /**
         * @param bitmap 需要添加Badge的Bitmap
         * @return this
         */
        public Builder setDrawable(Bitmap bitmap) {
            mBitmap = bitmap;
            return this;
        }

        /**
         * 在创建一个带有Badge的Bitmap之前, 需要使用一个可以附着Badge的Drawable, 通过
         * {@link #setDrawable(int)} 方法添加一个Drawable, 创建一个最简单的Badge
         * @return 创建一个附带Badge的Biamtp
         */
        public Bitmap build() {
            if (mBadgeDrawable.getNumber() < 0) {
                return mBitmap;
            } else {
                return mBadgeDrawable.buildBitmap(mBitmap);
            }
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