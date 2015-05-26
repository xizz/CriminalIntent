package xizz.criminalintent;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class CrimeCameraActivity extends SingleFragmentActivity {
	@Override
	protected Fragment createFragment() {return new CrimeCameraFragment(); }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
		super.onCreate(savedInstanceState);
	}
}
