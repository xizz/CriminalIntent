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

	public UUID id;
	public String title;
	public Date date;
	public boolean solved;

	public Crime() {
		id = UUID.randomUUID();
		date = new Date();
	}

	public Crime(JSONObject json) throws JSONException {
		id = UUID.fromString(json.getString(JSON_ID));
		title = json.getString(JSON_TITLE);
		solved = json.getBoolean(JSON_SOLVED);
		date = new Date(json.getLong(JSON_DATE));
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, id.toString());
		json.put(JSON_TITLE, title);
		json.put(JSON_DATE, date.getTime());
		json.put(JSON_SOLVED, solved);
		return json;
	}
}
