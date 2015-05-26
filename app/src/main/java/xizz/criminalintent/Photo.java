package xizz.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo {
	private static final String JSON_FILENAME = "filename";

	public String filename;

	public Photo(String f) {
		filename = f;
	}

	public Photo(JSONObject json) throws JSONException {
		filename = json.getString(JSON_FILENAME);
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_FILENAME, filename);
		return json;
	}
}
