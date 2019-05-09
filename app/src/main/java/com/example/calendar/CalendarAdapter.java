package com.example.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class CalendarAdapter extends BaseAdapter {
	private boolean isLeapyear = false;
	private int daysOfMonth = 0;
	private int dayOfWeek = 0;
	private int lastDaysOfMonth = 0;
	private Context context;
	private String[] dayNumber = new String[42];

	private SpecialCalendar sc = null;
	private LunarCalendar lc = null;
	private Resources res = null;
	private Drawable drawable = null;

	private String currentYear = "";
	private String currentMonth = "";
	private String currentDay = "";


	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private int currentFlag = -1;

	private String showYear = "";
	private String showMonth = "";
	private String animalsYear = "";
	private String leapMonth = "";
	private String cyclical = "";

	private String sysDate = "";
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";

	public CalendarAdapter() {
		Date date = new Date();
		sysDate = sdf.format(date);
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];

	}

	public CalendarAdapter(Context context, Resources rs, int jumpMonth, int jumpYear, int year_c, int month_c, int day_c) {
		this();
		this.context = context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		this.res = rs;

		int stepYear;
		int stepMonth = month_c + jumpMonth;
		if (stepMonth > 0) {
			// slip to next month
			if (stepMonth % 12 == 0) {
				stepYear = year_c + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = year_c + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// slip to last month
			stepYear = year_c - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
			if (stepMonth % 12 == 0) {

			}
		}

		currentYear = String.valueOf(stepYear);
		currentMonth = String.valueOf(stepMonth);
		currentDay = String.valueOf(day_c);

		getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));

	}

	@Override
	public int getCount() {
		return dayNumber.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.calendar_item, null);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext);
		String d = dayNumber[position].split("\\.")[0];
		String dv = dayNumber[position].split("\\.")[1];

		SpannableString sp = new SpannableString(d + "\n" + dv);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.2f), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (dv != null || dv != "") {
			sp.setSpan(new RelativeSizeSpan(0.75f), d.length() + 1, dayNumber[position].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		textView.setText(sp);
		textView.setTextColor(Color.GRAY);

		if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {

			textView.setTextColor(Color.BLACK);
			drawable = new ColorDrawable(Color.rgb(23, 126, 214));
			if (position % 7 == 0 || position % 7 == 6) {
				textView.setTextColor(Color.rgb(23, 126, 214));
				drawable = new ColorDrawable(Color.rgb(23, 126, 214));
			}
		}

		if (currentFlag == position) {
			drawable = new ColorDrawable(Color.rgb(23, 126, 214));
			textView.setBackgroundDrawable(drawable);
			textView.setTextColor(Color.WHITE);
		}
		return convertView;
	}

	public void getCalendar(int year, int month) {
		isLeapyear = sc.isLeapYear(year); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month);
		dayOfWeek = sc.getWeekdayOfMonth(year, month);
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1);
		Log.d("DAY", isLeapyear + " ======  " + daysOfMonth + "  ============  " + dayOfWeek + "  =========   " + lastDaysOfMonth);
		getweek(year, month);
	}

	private void getweek(int year, int month) {
		int j = 1;
		String lunarDay;

		for (int i = 0; i < dayNumber.length; i++) {
			//go to the last month
			if (i < dayOfWeek) {
				int temp = lastDaysOfMonth - dayOfWeek + 1;
				lunarDay = lc.getLunarDate(year, month - 1, temp + i, false);
				dayNumber[i] = (temp + i) + "." + lunarDay;

			} else if (i < daysOfMonth + dayOfWeek) {
				String day = String.valueOf(i - dayOfWeek + 1);
				lunarDay = lc.getLunarDate(year, month, i - dayOfWeek + 1, false);
				dayNumber[i] = i - dayOfWeek + 1 + "." + lunarDay;
				if (sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)) {
					currentFlag = i;
				}
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lc.animalsYear(year));
				setLeapMonth(lc.leapMonth == 0 ? "" : String.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));
			} else { // go to the next month
				lunarDay = lc.getLunarDate(year, month + 1, j, false);
				dayNumber[i] = j + "." + lunarDay;
				j++;
			}
		}

		String abc = "";
		for (int i = 0; i < dayNumber.length; i++) {
			abc = abc + dayNumber[i] + ":";
		}
		Log.d("DAYNUMBER", abc);

	}


	/**
	 * return the date
	 *
	 */
	public String getDateByClickItem(int position) {
		return dayNumber[position];
	}

	/**
	 * return the 1st day of the month
	 * 
	 *
	 */
	public int getStartPositon() {
		return dayOfWeek + 7;
	}

	/**
	 * return the last day of the month
	 * 
	 *
	 */
	public int getEndPosition() {
		return (dayOfWeek + daysOfMonth + 7) - 1;
	}

	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}

	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}

	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}

	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}
}
