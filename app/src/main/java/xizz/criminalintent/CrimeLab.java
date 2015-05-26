package xizz.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
	private static CrimeLab sCrimeLab;
	private List<Crime> mCrimes;
	private Context mAppContext;

	private CrimeLab(Context appContext) {
		mAppContext = appContext;
		mCrimes = new ArrayList<>();
//		for (int i = 0; i < 100; i++) {
//			Crime c = new Crime();
//			c.title = "Crime #" + i;
//			c.solved = i % 2 == 0;
//			mCrimes.add(c);
//		}
	}

	public static CrimeLab get(Context c) {
		if (sCrimeLab == null)
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		return sCrimeLab;
	}

	public Crime getCrime(UUID crimeId) {
		for (Crime c : mCrimes) {
			if (c.id.equals(crimeId))
				return c;
		}
		return null;
	}

	public List<Crime> getCrimes() { return mCrimes; }

	public void addCrime(Crime c) { mCrimes.add(c); }

	public void deleteCrime(Crime c) {
		mCrimes.remove(c);
	}
}
