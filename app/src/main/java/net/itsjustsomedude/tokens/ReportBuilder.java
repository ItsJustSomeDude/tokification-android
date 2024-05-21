package net.itsjustsomedude.tokens;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ReportBuilder {
	private static final String TAG = "ReportBuilder";

	private final Coop coop;
	private final String sinkName;

	boolean startEstimate;
	boolean endEstimate;
	boolean ended;
	long nowEpoch;
	long startEpoch;
	long endEpoch;
	double tvalNow;
	double tval30Mins;
	double tval60Mins;
	String genTimeLine;
	String startLine;
	String endLine;
	String tvalTable;
	String futureTable;

	HashMap<String, Integer> tokensSent;
	HashMap<String, Integer> tokensRec;
	HashMap<String, Double> tvalSent;
	HashMap<String, Double> tvalRec;

	public static ReportBuilder makeBuilder(Context ctx, Coop coop) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		String stringValue = sharedPreferences.getString("player_name", "You");

		return new ReportBuilder(coop, stringValue);
	}

	public ReportBuilder(Coop coop) {
		this.coop = coop;
		if (coop.sinkMode)
			this.sinkName = "Sink";
		else
			this.sinkName = "You";

		refreshValues();
	}

	public ReportBuilder(Coop coop, String sinkName) {
		this.coop = coop;
		this.sinkName = sinkName;

		refreshValues();
	}

	public void refreshValues() {
		nowEpoch = Calendar.getInstance().getTimeInMillis() / 1000L;

		if (coop.startTime == null) {
			long sinceHourStart = nowEpoch % 3600;
			startEpoch = nowEpoch - sinceHourStart;
			startLine = ":warning: No start time set, assuming start of current hour.";
			startEstimate = true;
		} else {
			startEpoch = coop.startTime.getTimeInMillis() / 1000L;
			startLine = String.format("<:contract:589317482901405697> Start Time: <t:%1$s> (<t:%1$s:R>)", startEpoch);
			startEstimate = false;
		}

		if (coop.endTime == null) {
			endEpoch = startEpoch + 12 * 60 * 60;
			endLine = ":warning: No end time set, assuming 12 hours from start time.";
			endEstimate = true;
		} else {
			endEpoch = coop.endTime.getTimeInMillis() / 1000L;
			endLine = String.format(":alarm_clock: End Time: <t:%1$s> (<t:%1$s:R>)", endEpoch);
			endEstimate = false;
		}

		tvalNow = round(tval(startEpoch, endEpoch, nowEpoch, 1), 4);
		tval30Mins = round(tval(startEpoch, endEpoch, nowEpoch + 30 * 60, 1), 4);
		tval60Mins = round(tval(startEpoch, endEpoch, nowEpoch + 60 * 60, 1), 4);

		String rowFormat = "%-12.12s|%9.3f|%4d|%8.3f|%4d|%9.3f";
		HashMap<String, String> table = new HashMap<>();

		tokensSent = new HashMap<>();
		tokensRec = new HashMap<>();
		tvalSent = new HashMap<>();
		tvalRec = new HashMap<>();

		Log.i(TAG, "Number of events: " + coop.events.size());

		for (Coop.Event ev : coop.events) {
			long time = ev.time.getTimeInMillis() / 1000L;

			double tv = tval(startEpoch, endEpoch, time, ev.count);
			if (ev.direction.equals("sent")) {
				Integer i = tokensSent.get(ev.person);
				tokensSent.put(ev.person, i == null ? ev.count : i + ev.count);

				Double j = tvalSent.get(ev.person);
				tvalSent.put(ev.person, j == null ? tv : j + tv);

				Integer k = tokensRec.get(sinkName);
				tokensRec.put(sinkName, k == null ? ev.count : k + ev.count);

				Double l = tvalRec.get(ev.person);
				tvalRec.put(sinkName, l == null ? tv : l + tv);
			} else {
				Integer k = tokensRec.get(ev.person);
				tokensRec.put(ev.person, k == null ? ev.count : k + ev.count);

				Double l = tvalRec.get(ev.person);
				tvalRec.put(sinkName, l == null ? tv : l + tv);

				Integer i = tokensSent.get(sinkName);
				tokensSent.put(sinkName, i == null ? ev.count : i + ev.count);

				Double j = tvalSent.get(ev.person);
				tvalSent.put(ev.person, j == null ? tv : j + tv);
			}
		}

		for (String person : coop.getPeople(sinkName)) {
			Double sent = tvalSent.get(person);
			Double rec = tvalRec.get(person);
			double delta = (sent == null ? 0 : sent) - (rec == null ? 0 : rec);

			Log.i(TAG, delta + " Delta");

			String output = String.format(Locale.US,
					rowFormat,
					person,
					delta,
					tokensSent.get(person),
					sent,
					tokensRec.get(person),
					rec == null ? 0 : (rec * -1)
			);
			table.put(person, output);
		}
		tvalTable = String.join("\n", table.values());

		ArrayList<String> futures = new ArrayList<>();
		if (nowEpoch < endEpoch) {
			ended = false;
			futures.add("__Running Token Value__");
			futures.add(String.format("<:icon_token:653018008670961665> Now: `%s`", tvalNow));

			if (tval30Mins > 0.03)
				futures.add(String.format("In 30 minutes: `%s`", tval30Mins));
			if (tval60Mins > 0.03)
				futures.add(String.format("In 60 minutes: `%s`", tval60Mins));
		} else {
			ended = true;
			futures.add(":tada: Contract Complete!");
		}
		futureTable = String.join("\n", futures);

		genTimeLine = String.format("Report Generated at <t:%1$s> (<t:%1$s:R>)", nowEpoch);
	}

	public String sinkReport() {
		//final String url = "https://discord.com/channels/455380663013736479/455512567004004353/1217529083286651082";
		final String[] out = new String[]{
				"# __Tokification__ (Android Alpha :eyes:)",
				"",
				genTimeLine,
				"_This message will be manually updated every 15 to 45 minutes, depending on how busy I am._",
				"",
				"__Contract Info__",
				startLine,
				endLine,
				"_Note that all token values are only accurate once the end time is accurate._",
				"",
				futureTable,
				"",
				"__Player's Current TVals__ (as seen by the :people_hugging: sink)",
				"```",
				"Player      |   Δ TVal| +TS|  +TSVal| -TR|   -TRVal",
				"------------+---------+----+--------+----+---------",
				tvalTable,
				"```",
				/*String.format(*/"_This is not a wonky command, but an app written by ItsJustSomeDude. Stay tuned for further updates!_"/*, url)*/,
				// See [the FAQ](%s) for more info.
		};

		return String.join("\n", out);
	}

	public String normalReport() {
		String est = "";
		if (startEstimate)
			est += "(Unknown Start)";
		if (endEstimate)
			est += "(Assuming 12 hour duration)";


		Double tvSent = tvalSent.get(sinkName);
		Double tvRec = tvalRec.get(sinkName);
		Integer tSent = tokensSent.get(sinkName);
		Integer tRec = tokensRec.get(sinkName);

		double sent = (tvSent == null ? 0 : tvSent);
		double rec = (tvRec == null ? 0 : tvRec);

		String[] out = new String[]{
				String.format("Your ΔTVal: %s %s", round(sent - rec, 5), est),
				String.format("TVal Now: %s %s", ended ? "Contract Complete!" : round(tvalNow, 5), est),
				String.format("Sent TVal: %s (%s tokens)", round(sent, 5), tSent),
				String.format("Received TVal: -%s (%s tokens)", round(rec, 4), tRec)
		};

		return String.join("\n", out);
	}

	private static double round(double input, int roundTo) {
		double i = Math.pow(10, roundTo);
		return Math.round(input * i) / i;
	}

	public static double tval(long startTime, long endTime, long tokenTime, int count) {
		double duration = endTime - startTime;
		double elapsed = tokenTime - startTime;

		double i = Math.pow(1 - 0.9 * (elapsed / duration), 4);
		double singleValue = Math.max(i, 0.03);

		Log.i("TVAL", "Start: " + startTime + ", End: " + endTime + ", Token: " + tokenTime + " Duration: " + duration + ", Elapsed: " + elapsed + ", TVal: " + i);

		return singleValue * count;
	}

//	public static double tval(Calendar coopStart, Calendar coopEnd, Calendar token, int count) {
//		long tokenTime = token.getTimeInMillis() / 1000L;
//		long startTime = coopStart.getTimeInMillis() / 1000L;
//		long endTime = coopEnd.getTimeInMillis() / 1000L;
//
//		return tval(startTime, endTime, tokenTime, count);
//	}

	public static void copyText(Context ctx, String toCopy) {
		ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("SinkReport", toCopy);
		clipboard.setPrimaryClip(clip);
	}
}
