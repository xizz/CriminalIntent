package xizz.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {
	public static final String EXTRA_DATE = "criminalintent.DATE";

	public static DatePickerFragment newInstance(Date date) {
		// try to set mDate directly does not work because date passed in is on a different thread
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);

		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) getArguments().getSerializable(EXTRA_DATE));

		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH);
		final int hour = calendar.get(Calendar.HOUR);
		final int minute = calendar.get(Calendar.MINUTE);

		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_time, null);

		final DatePicker datePicker = (DatePicker) v.findViewById(R.id.datePicker);
		final TimePicker timePicker = (TimePicker) v.findViewById(R.id.timePicker);

		datePicker.init(year, month, day, null);
		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);

		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.setTitle(R.string.date_time_picker_title)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						calendar.set(Calendar.YEAR, datePicker.getYear());
						calendar.set(Calendar.MONTH, datePicker.getMonth());
						calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
						calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
						calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
						sendResult(Activity.RESULT_OK, calendar.getTime());
					}
				})
				.create();
	}

	private void sendResult(int resultCode, Date date) {
		if (getTargetFragment() == null)
			return;
		Intent i = new Intent();
		i.putExtra(EXTRA_DATE, date);

		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
}
