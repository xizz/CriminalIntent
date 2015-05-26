package xizz.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";

	private static CrimeLab sCrimeLab;
	private List<Crime> mCrimes;
	private Context mAppContext;
	private CriminalIntentJSONSerializer mSerializer;

	private CrimeLab(Context appContext) {
		mAppContext = appContext;
		mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);

		try {
			mCrimes = mSerializer.loadCrimes();
		} catch (Exception e) {
			mCrimes = new ArrayList<>();
			Log.e(TAG, "Error loading crimes: ", e);
		}

		if (mCrimes.size() == 0) {
			for (int i = 0; i < 25; i++) {
				Crime c = new Crime();
				c.title = "Crime #" + i;
				c.solved = i % 2 == 0;
				mCrimes.add(c);
			}
		}
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

	public boolean saveCrimes() {
		try {
			mSerializer.saveCrimes(mCrimes);
			Log.d(TAG, "crimes saved to file");
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Error saving crimes: " + e);
			return false;
		}
	}
}
