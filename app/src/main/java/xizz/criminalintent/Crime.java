package xizz.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class Crime {
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_DATE = "date";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_PHOTO = "photo";
	private static final String JSON_SUSPECT = "suspect";

	public UUID id;
	public String title;
	public Date date;
	public boolean solved;
	public String photo;
	public String suspect;

	public Crime() {
		id = UUID.randomUUID();
		date = new Date();
	}

	public Crime(JSONObject json) throws JSONException {
		id = UUID.fromString(json.getString(JSON_ID));
		title = json.getString(JSON_TITLE);
		solved = json.getBoolean(JSON_SOLVED);
		date = new Date(json.getLong(JSON_DATE));
		if (json.has(JSON_PHOTO))
			photo = json.getString(JSON_PHOTO);
		if (json.has(JSON_SUSPECT))
			suspect = json.getString(JSON_SUSPECT);
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, id.toString());
		json.put(JSON_TITLE, title);
		json.put(JSON_DATE, date.getTime());
		json.put(JSON_SOLVED, solved);
		json.put(JSON_PHOTO, photo);
		json.put(JSON_SUSPECT, suspect);
		return json;
	}
}
