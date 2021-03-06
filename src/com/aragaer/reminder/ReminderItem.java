package com.aragaer.reminder;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

class ReminderItem {
	long _id = -1;
	byte glyph_data[];
	String text;
	Date when;
	int color = Bitmaps.COLOR_WHITE;

	static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ReminderItem(Bitmap b) {
		this(b, null, new Date());
	}

	public ReminderItem(Bitmap b, String s) {
		this(b, s, new Date());
	}

	public static byte[] bitmap_to_bytes(Bitmap b) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, 100, bos);
		byte bb[] = bos.toByteArray();
		try {
			bos.close();
		} catch (Exception e) {
			Log.e("Reminder", e.toString());
		}
		return bb;
	}

	public ReminderItem(Bitmap b, String s, Date w) {
		glyph_data = bitmap_to_bytes(b);
		b.recycle();
		text = s;
		when = w;
	}

	public ReminderItem(long id, byte bb[], String s, Date w, int c) {
		_id = id;
		glyph_data = bb;
		text = s;
		when = w;
		color = c;
	}

	/* for reuse */
	public void setTo(long id, byte bb[], String s, Date w, int c) {
		_id = id;
		glyph_data = bb;
		text = s;
		when = w;
		color = c;
	}

	public Bitmap getGlyph(int size) {
		Bitmap glyph = BitmapFactory.decodeByteArray(glyph_data, 0,
				glyph_data.length);
		Bitmap result = Bitmap.createScaledBitmap(glyph, size, size, true);
		return result;
	}

	public String getText() {
		return text == null ? df.format(when) : text;
	}
}