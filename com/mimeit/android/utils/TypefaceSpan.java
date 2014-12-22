package com.mimeit.android.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import com.mimeit.android.utils.Fonts.Font;

/**
 * custom typeface span used for custom font.
 * "https://android.googlesource.com/platform/frameworks/base.git/+/master/core/java/android/text/style/TypefaceSpan.java"
 *
 */
public class TypefaceSpan extends MetricAffectingSpan {

    private final Context mContext;
    private final Font mFont;

    // new instance
    public TypefaceSpan(Context context, Font font) {
        mContext = context;
        mFont = font;
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        apply(p);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        apply(tp);
    }

    // applies currrent font to given paint
    private void apply(Paint paint) {
        int oldStyle;
        Typeface oldTypeface = paint.getTypeface();
        if (oldTypeface == null)
            oldStyle = 0;
        else
            oldStyle = oldTypeface.getStyle();

        Typeface newTypeface = Typeface.create(mFont.getFont(mContext),
                oldStyle);
        int fake = oldStyle & ~newTypeface.getStyle();
        if ((fake & Typeface.BOLD) != 0)
            paint.setFakeBoldText(true);
        if ((fake & Typeface.ITALIC) != 0)
            paint.setTextSkewX(-0.25f);

        paint.setTypeface(newTypeface);
    }

}
