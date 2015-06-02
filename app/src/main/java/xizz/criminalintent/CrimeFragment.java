package xizz.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
	public static final String TAG = "CrimeFragment";
	public static final String EXTRA_CRIME_ID = "criminalintent.CRIME_ID";

	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_IMAGE = "image";
	private static final int REQUEST_DATE = 11;
	private static final int REQUEST_PHOTO = 22;
	private static final int REQUEST_CONTACT = 33;

	private Crime mCrime;
	private Button mDateButton;
	private ImageView mPhotoView;
	private Button mSuspectButton;
	private Callbacks mCallbacks;

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
		setHasOptionsMenu(true);

		UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onStart() {
		super.onStart();
		showPhoto();
	}

	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
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
				mCallbacks.onCrimeUpdated(mCrime);
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});

		solvedCheckBox.setChecked(mCrime.solved);
		solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.solved = isChecked;
				mCallbacks.onCrimeUpdated(mCrime);
			}
		});

		mDateButton = (Button) v.findViewById(R.id.crime_date);
		mDateButton.setText(mCrime.date.toString());
		mDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager manager = getActivity().getFragmentManager();
				DateTimePickerFragment pickerFragment = DateTimePickerFragment.newInstance(mCrime
						.date);
				pickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				pickerFragment.show(manager, DIALOG_DATE);
			}
		});

		ImageButton mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
			}
		});
		PackageManager pm = getActivity().getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
				!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			mPhotoButton.setEnabled(false);
		}

		mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
		mPhotoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCrime.photo == null)
					return;

				FragmentManager fm = getActivity().getFragmentManager();
				String path = getActivity().getFileStreamPath(mCrime.photo).getAbsolutePath();
				ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
			}
		});

		mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(i, REQUEST_CONTACT);
			}
		});
		if (mCrime.suspect != null) {
			mSuspectButton.setText(mCrime.suspect);
		}

		Button reportButton = (Button) v.findViewById(R.id.crime_reportButton);
		reportButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
				i = Intent.createChooser(i, getString(R.string.send_report));
				startActivity(i);
			}
		});

		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;

		switch (requestCode) {
			case REQUEST_DATE:
				mCrime.date = (Date) data.getSerializableExtra(DateTimePickerFragment.EXTRA_DATE);
				mDateButton.setText(mCrime.date.toString());
				mCallbacks.onCrimeUpdated(mCrime);
				break;
			case REQUEST_PHOTO:
				String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
				if (filename != null) {
					Log.d(TAG, "filename: " + filename);
					mCrime.photo = filename;
					Log.d(TAG, "Crime: " + mCrime.title + " has a photo");
					mCallbacks.onCrimeUpdated(mCrime);
					showPhoto();
				}
				break;
			case REQUEST_CONTACT:
				Uri contactUri = data.getData();
				String[] queryFields = new String[]{ContactsContract.Contacts
						.DISPLAY_NAME_PRIMARY};
				Cursor c = getActivity().getContentResolver()
						.query(contactUri, queryFields, null, null, null);

				if (c.getCount() == 0) {
					c.close();
					return;
				}

				c.moveToFirst();
				String suspect = c.getString(0);
				mCrime.suspect = suspect;
				mCallbacks.onCrimeUpdated(mCrime);
				mSuspectButton.setText(suspect);
				c.close();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_crime, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_delete_crime:
				CrimeLab.get(getActivity()).deleteCrime(mCrime);
				getActivity().finish();
				return false;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void showPhoto() {
		BitmapDrawable b = null;
		if (mCrime.photo != null) {
			String path = getActivity().getFileStreamPath(mCrime.photo).getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(b);
	}

	private String getCrimeReport() {
		String solvedString;
		if (mCrime.solved)
			solvedString = getString(R.string.crime_report_solved);
		else
			solvedString = getString(R.string.crime_report_unsolved);

		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.date).toString();

		String suspect = mCrime.suspect;
		if (suspect == null) {
			suspect = getString(R.string.crime_report_no_suspect);
		} else {
			suspect = getString(R.string.crime_report_suspect, suspect);
		}

		return getString(R.string.crime_report, mCrime.title, dateString, solvedString, suspect);
	}

	public interface Callbacks {
		void onCrimeUpdated(Crime crime);
	}
}
