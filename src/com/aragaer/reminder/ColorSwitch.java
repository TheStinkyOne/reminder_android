package com.aragaer.reminder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ColorSwitch extends RadioGroup {
	private static final int ADD = 100;
	private int margin;
	public ColorSwitch(Context context) {
		this(context, null);
	}
	public ColorSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		margin = context.getResources().getDimensionPixelSize(R.dimen.notification_glyph_margin);

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
		lp.setMargins(margin, margin, margin, margin);
		for (int i = 0; i < Bitmaps.N_COLORS; i++) {
			RadioButton rb = new RadioButton(context);
			GradientDrawable border = Bitmaps.border();
			rb.setId(ADD + i);
			final int c = Bitmaps.colors[i];
			border.setStroke(margin, c);
			border.setColor(Color.argb(192, Color.red(c), Color.green(c), Color.blue(c)));
			rb.setBackgroundDrawable(border);
			rb.setButtonDrawable(android.R.color.transparent);
			addView(rb, lp);
		}
		check(ADD);
	}
	public void onMeasure(int wms, int hms) {
		int w = MeasureSpec.getSize(wms);
		int h = MeasureSpec.getSize(hms);

		if (w > h)
			w /= getChildCount();
		else
			h /= getChildCount();

		w -= 2 * margin;
		h -= 2 * margin;
		setMeasuredDimension(wms, hms);
		wms = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
		hms = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);

		for (int i = 0; i < getChildCount(); i++)
			getChildAt(i).measure(wms, hms);
	}
	public void onLayout(boolean changed, int l, int t, int r, int b) {
		setOrientation(r - l > b - t ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
		super.onLayout(changed, l, t, r, b);
	}
	public int getValue() {
		return getCheckedRadioButtonId() - ADD;
	}
}
