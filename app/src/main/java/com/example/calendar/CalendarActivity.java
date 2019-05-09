package com.example.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class CalendarActivity extends Activity implements View.OnClickListener {

	private GestureDetector gestureDetector = null;
	private CalendarAdapter calV = null;
	private ViewFlipper flipper = null;
	private GridView gridView = null;
	private static int jumpMonth = 0;
	private static int jumpYear = 0;
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";

	private int gvFlag = 0;

	private TextView currentMonth;

	private ImageView prevMonth;

	private ImageView nextMonth;

	public CalendarActivity() {

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date);
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		currentMonth = (TextView) findViewById(R.id.currentMonth);
		prevMonth = (ImageView) findViewById(R.id.prevMonth);
		nextMonth = (ImageView) findViewById(R.id.nextMonth);
		setListener();

		gestureDetector = new GestureDetector(this, new MyGestureListener());
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();
		calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
		addGridView();
		gridView.setAdapter(calV);
		flipper.addView(gridView, 0);
		addTextToTopTextView(currentMonth);
	}

	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			int gvFlag = 0;
			if (e1.getX() - e2.getX() > 120) {
				enterNextMonth(gvFlag);
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				enterPrevMonth(gvFlag);
				return true;
			}
			return false;
		}
	}

	// slip to next month
	private void enterNextMonth(int gvFlag) {
		addGridView();
		jumpMonth++;

		calV = new CalendarAdapter(this, this.getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
		gridView.setAdapter(calV);
		addTextToTopTextView(currentMonth);
		gvFlag++;
		flipper.addView(gridView, gvFlag);
		flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
		flipper.showNext();
		flipper.removeViewAt(0);
	}

	// slip to last month
	private void enterPrevMonth(int gvFlag) {
		addGridView();
		jumpMonth--;

		calV = new CalendarAdapter(this, this.getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
		gridView.setAdapter(calV);
		gvFlag++;
		addTextToTopTextView(currentMonth);
		flipper.addView(gridView, gvFlag);

		flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		flipper.showPrevious();
		flipper.removeViewAt(0);
	}


	//add info of the year
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		textDate.append(calV.getShowYear()).append("年").append(calV.getShowMonth()).append("月").append("\t");
		view.setText(textDate);
	}

	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();

		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(40);
		if (Width == 720 && Height == 1280) {
			gridView.setColumnWidth(40);
		}
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return CalendarActivity.this.gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				int startPosition = calV.getStartPositon();
				int endPosition = calV.getEndPosition();
				if (startPosition <= position + 7 && position <= endPosition - 7) {
					String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];
					String scheduleYear = calV.getShowYear();
					String scheduleMonth = calV.getShowMonth();
					Toast.makeText(CalendarActivity.this, scheduleYear + "-" + scheduleMonth + "-" + scheduleDay, Toast.LENGTH_SHORT).show();
				}
			}
		});
		gridView.setLayoutParams(params);
	}

	private void setListener() {
		prevMonth.setOnClickListener(this);
		nextMonth.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nextMonth:
			enterNextMonth(gvFlag);
			break;
		case R.id.prevMonth:
			enterPrevMonth(gvFlag);
			break;
		default:
			break;
		}
	}

}