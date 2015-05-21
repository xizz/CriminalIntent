package xizz.criminalintent;

import android.app.Fragment;
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

import java.util.UUID;

public class CrimeFragment extends Fragment {
	public static final String EXTRA_CRIME_ID = "criminalintent.CRIME_ID";

	private Crime mCrime;

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
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle
			savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, container, false);

		final EditText titleField = (EditText) v.findViewById(R.id.crime_title);
		final Button dateButton = (Button) v.findViewById(R.id.crime_date);
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

		dateButton.setText(mCrime.date.toString());
		dateButton.setEnabled(false);

		solvedCheckBox.setChecked(mCrime.solved);
		solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.solved = isChecked;
			}
		});

		return v;
	}
}
