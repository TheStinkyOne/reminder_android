package com.aragaer.reminder;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;

public class ReminderListActivity extends Activity implements OnItemClickListener {
	private int size, drag_size;
	private DraggableGridView list;
	final private Map<Long, Drawable> cached_drawables = new HashMap<Long, Drawable>();
	final private CursorAdapter ca = new CursorAdapter(this, null, false) {
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			ImageView view = new ImageView(parent.getContext());
			view.setAdjustViewBounds(true);
			return view;
		}

		public void bindView(View view, Context context, Cursor cursor) {
			final ReminderItem item = ReminderProvider.getItem(cursor);
			Drawable image = cached_drawables.get(item._id);
			if (image == null) {
				image = Bitmaps.memo_drawable(getResources(), item, false);
				image.setBounds(0, 0, size, size);
				cached_drawables.put(item._id, image);
			}
			((ImageView) view).setImageDrawable(image);
		}
	};

	final private DragDropAdapter adapter = new DragDropAdapter(ca) {
		void handle_drag_drop(int from, int to) {
			ReminderProvider.reorder(ReminderListActivity.this, from, to);
		}
	};

	public void onItemClick(AdapterView<?> adapter, View arg1,
			int position, long id) {
		startActivity(new Intent(this,
				ReminderViewActivity.class).putExtra("reminder_id", id));
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Resources r = getResources();
		size = r.getDimensionPixelSize(R.dimen.tile_size);
		drag_size = r.getDimensionPixelSize(R.dimen.view_glyph_size);

		startService(new Intent(this, ReminderService.class));
		list = new DraggableGridView(this) {
			public Drawable getDragDrawable(View view, int position, long id) {
				final Drawable res = cached_drawables.get(id).getConstantState().newDrawable();
				res.setBounds(0, 0, drag_size, drag_size);
				return res;
			}
		};
		list.setNumColumns(-1);
		list.setColumnWidth(size);

		list.setOnItemClickListener(this);
		list.setAdapter(adapter);

		setContentView(list);
		getContentResolver().registerContentObserver(ReminderProvider.content_uri, true, observer);

		observer.onChange(true);
	}

	private final ContentObserver observer = new ContentObserver(new Handler()) {
		@SuppressLint("NewApi")
		public void onChange(boolean selfChange) {
			this.onChange(selfChange, null);
		}
		@SuppressLint("Override")
		public void onChange(boolean selfChange, Uri uri) {
			new AsyncTask<Void, Void, Cursor>() {
				protected Cursor doInBackground(Void... params) {
					return getContentResolver().query(
							ReminderProvider.content_uri, null, null, null, null);
				}
				protected void onPostExecute(Cursor c) {
					ca.changeCursor(c);
				}
			}.execute();
		}
	};

	public void onDestroy() {
		super.onDestroy();
		getContentResolver().unregisterContentObserver(observer);
		final Cursor c = ca.getCursor();
		if (c != null)
			c.close();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, R.string.add_new, Menu.NONE, R.string.add_new)
				.setIcon(R.drawable.content_new)
				.setIntent(new Intent(this, ReminderCreateActivity.class))
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
}

