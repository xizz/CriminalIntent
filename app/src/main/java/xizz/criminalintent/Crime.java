package xizz.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
	public UUID id;
	public String title;
	public Date date;
	public boolean solved;

	public Crime() {
		id = UUID.randomUUID();
		date = new Date();
	}
}
