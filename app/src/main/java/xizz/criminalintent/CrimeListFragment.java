package xizz.criminalintent;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends ListFragment {
	private static final String TAG = "CrimeListFragment";
	private boolean mSubtitleVisible;
	private Callbacks mCallbacks;

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
	public void onListItemClick(ListView l, View v, int position, long id) {
		Crime c = (Crime) (getListAdapter()).getItem(position);
		Log.d(TAG, c.title + " was clicked");
		mCallbacks.onCrimeSelected(c);
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
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean
					checked) { }

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.menu_crime_list_multi, menu);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_delete_crime:
						CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
						CrimeLab crimeLab = CrimeLab.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; --i) {
							if (getListView().isItemChecked(i))
								crimeLab.deleteCrime(adapter.getItem(i));
						}
						crimeLab.saveCrimes();
						mode.finish();
						adapter.notifyDataSetChanged();
						return true;
					default:
						return false;
				}
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) { }
		});
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
				((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
				mCallbacks.onCrimeSelected(crime);
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

	public void updateUI() {
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
	}

	public interface Callbacks {
		void onCrimeSelected(Crime crime);
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
