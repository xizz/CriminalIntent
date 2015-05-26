package xizz.criminalintent;

import android.app.ActionBar;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends ListFragment {
	private static final String TAG = "CrimeListFragment";

	private boolean mSubtitleVisible;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		getActivity().setTitle(R.string.crimes_title);
		final List<Crime> crimes = CrimeLab.get(getActivity()).getCrimes();

		CrimeAdapter adapter = new CrimeAdapter(crimes);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Crime c = (Crime) (getListAdapter()).getItem(position);
		Log.d(TAG, c.title + " was clicked");

		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.id);
		startActivity(i);
	}

	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		if (mSubtitleVisible && getActivity().getActionBar() != null)
			getActivity().getActionBar().setSubtitle(R.string.subtitle);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "onViewCreated()");
		setEmptyText(getString(R.string.empty_text));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_crime_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_crime:
				Crime crime = new Crime();
				CrimeLab.get(getActivity()).addCrime(crime);
				Intent i = new Intent(getActivity(), CrimePagerActivity.class);
				i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.id);
				startActivityForResult(i, 0);
				return true;
			case R.id.menu_item_show_subtitle:
				ActionBar actionBar = getActivity().getActionBar();
				if (actionBar == null)
					return false;
				if (mSubtitleVisible) {
					actionBar.setSubtitle(null);
					item.setTitle(R.string.show_subtitle);
					mSubtitleVisible = false;
				} else {
					actionBar.setSubtitle(R.string.subtitle);
					item.setTitle(R.string.hide_subtitle);
					mSubtitleVisible = true;
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private class CrimeAdapter extends ArrayAdapter<Crime> {
		public CrimeAdapter(List<Crime> crimes) {
			super(getActivity(), android.R.layout.simple_list_item_1, crimes);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.list_item_crime, null);
			}

			Crime c = getItem(position);

			TextView titleTextView =
					(TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
			titleTextView.setText(c.title);
			TextView dateTextView =
					(TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
			dateTextView.setText(c.date.toString());
			CheckBox solvedCheckBox =
					(CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
			solvedCheckBox.setChecked(c.solved);

			return convertView;
		}
	}

}
