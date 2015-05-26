package xizz.criminalintent;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CriminalIntentJSONSerializer {
	private static final String TAG = "JSONSerializer";

	private Context mContext;
	private String mFilename;

	public CriminalIntentJSONSerializer(Context c, String f) {
		mContext = c;
		mFilename = f;
	}

	public List<Crime> loadCrimes() throws IOException, JSONException {
		ArrayList<Crime> crimes = new ArrayList<>();
		BufferedReader reader = null;
		try {
			// open and read the file into a StringBuilder
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				// line breaks are omitted and irrelevant
				jsonString.append(line);
			}
			// parse the JSON using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			// build the array of crimes from JSONObjects
			for (int i = 0; i < array.length(); i++) {
				crimes.add(new Crime(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString());
		} finally {
			if (reader != null)
				reader.close();
		}
		return crimes;
	}

	public void saveCrimes(List<Crime> crimes) throws JSONException, IOException {
		// build an array in JSON
		JSONArray array = new JSONArray();
		for (Crime c : crimes)
			array.put(c.toJSON());

		// write the file to disk
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
