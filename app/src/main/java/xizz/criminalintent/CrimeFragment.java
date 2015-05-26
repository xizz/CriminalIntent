package xizz.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
	public static final String TAG = "CrimeFragment";
	public static final String EXTRA_CRIME_ID = "criminalintent.CRIME_ID";

	private static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE = 0;

	private Crime mCrime;
	private Button mDateButton;

	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
	}

	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle
			savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, container, false);

		final EditText titleField = (EditText) v.findViewById(R.id.crime_title);
		final CheckBox solvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);

		titleField.setText(mCrime.title);
		titleField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mCrime.title = s.toString();
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});

		solvedCheckBox.setChecked(mCrime.solved);
		solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.solved = isChecked;
			}
		});

		mDateButton = (Button) v.findViewById(R.id.crime_date);
		mDateButton.setText(mCrime.date.toString());
		mDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager manager = getActivity().getFragmentManager();
				DatePickerFragment pickerFragment = DatePickerFragment.newInstance(mCrime.date);
				pickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				pickerFragment.show(manager, DIALOG_DATE);
			}
		});
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_DATE) {
			mCrime.date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mDateButton.setText(mCrime.date.toString());
		}
	}
}
