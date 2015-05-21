package xizz.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ViewPager viewPager = new ViewPager(this);
		viewPager.setId(R.id.viewPager);
		setContentView(viewPager);

		final List<Crime> crimes = CrimeLab.get(this).getCrimes();

		viewPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				Crime crime = crimes.get(position);
				return CrimeFragment.newInstance(crime.id);
			}

			@Override
			public int getCount() { return crimes.size(); }
		});
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset,
			                           int positionOffsetPixels) { }

			@Override
			public void onPageSelected(int position) {
				Crime crime = crimes.get(position);
				if (crime.title != null)
					setTitle(crime.title);
			}

			@Override
			public void onPageScrollStateChanged(int state) { }
		});
		UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		for (int i = 0; i < crimes.size(); ++i) {
			if (crimes.get(i).id.equals(crimeId)) {
				viewPager.setCurrentItem(i);
				break;
			}
		}
	}
}
