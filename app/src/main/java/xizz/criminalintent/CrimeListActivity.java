package xizz.criminalintent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;

public class CrimeListActivity extends SingleFragmentActivity
		implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
	@Override
	protected Fragment createFragment() { return new CrimeListFragment(); }

	@Override
	protected int getLayoutResId() { return R.layout.activity_masterdetail; }

	@Override
	public void onCrimeSelected(Crime crime) {
		if (findViewById(R.id.detailFragmentContainer) == null) {
			// start an instance of CrimePagerActivity
			Intent i = new Intent(this, CrimePagerActivity.class);
			i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.id);
			startActivityForResult(i, 0);
		} else {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();

			Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
			Fragment newDetail = CrimeFragment.newInstance(crime.id);

			if (oldDetail != null)
				ft.remove(oldDetail);

			ft.add(R.id.detailFragmentContainer, newDetail);
			ft.commit();
		}
	}

	@Override
	public void onCrimeUpdated(Crime crime) {
		FragmentManager fragmentManager = getFragmentManager();
		CrimeListFragment listFragment =
				(CrimeListFragment) fragmentManager.findFragmentById(R.id.fragmentContainer);
		listFragment.updateUI();
	}
}
