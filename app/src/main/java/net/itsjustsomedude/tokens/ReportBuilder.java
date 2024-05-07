package net.itsjustsomedude.tokens;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ReportBuilder {
	private static final String TAG = "ReportBuilder";

	private final Coop coop;
	private final String sinkName;

	private final HashMap<String, String> data = new HashMap<>();

	public ReportBuilder(Coop coop, String sinkName) {
		this.coop = coop;
		this.sinkName = sinkName;
	}

	public void calculateValues() {
		boolean startEstimate;
		boolean endEstimate;
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

		nowEpoch = Calendar.getInstance().getTimeInMillis() / 1000L;

		if (coop.startTime == null) {
			long sinceHourStart = nowEpoch % 3600;
			startEpoch = nowEpoch - sinceHourStart;
			startLine = ":warning: No start time set, assuming start of current hour." + startEpoch;
			startEstimate = true;
		} else {
			startEpoch = coop.startTime.getTimeInMillis() / 1000L;
			startLine = String.format("<:contract:589317482901405697> Start Time: <t:%1$s> (<t:%1$s:R>)", startEpoch);
			startEstimate = false;
		}

		if (coop.endTime == null) {
			endEpoch = startEpoch + 12 * 60 * 60;
			endLine = ":warning: No end time set, assuming 12 hours from start time." + endEpoch;
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

		HashMap<String, Integer> tokensSent = new HashMap<>();
		HashMap<String, Integer> tokensRec = new HashMap<>();
		HashMap<String, Double> tvalSent = new HashMap<>();
		HashMap<String, Double> tvalRec = new HashMap<>();

		for (String person : coop.getPeople(sinkName)) {
			tokensSent.put(person, 0);
			tokensRec.put(person, 0);
			tvalSent.put(person, 0d);
			tvalRec.put(person, 0d);
		}

		Log.i(TAG, "Number of events: " + coop.events.size());

		for (Event ev : coop.events) {
			long time = ev.time.getTimeInMillis() / 1000L;

			double tv = tval(startEpoch, endEpoch, time, ev.count);
			if (ev.direction.equals("sent")) {
				tokensSent.merge(ev.person, ev.count, Integer::sum);
				tvalSent.merge(ev.person, tv, Double::sum);
				tokensRec.merge(sinkName, ev.count, Integer::sum);
				tvalRec.merge(sinkName, tv, Double::sum);
			} else {
				tokensRec.merge(ev.person, ev.count, Integer::sum);
				tvalRec.merge(ev.person, tv, Double::sum);
				tokensSent.merge(sinkName, ev.count, Integer::sum);
				tvalSent.merge(sinkName, tv, Double::sum);
			}
		}

		for (String person : coop.getPeople(sinkName)) {
			double delta = tvalSent.get(person) - tvalRec.get(person);

			Log.i(TAG, delta + " Delta");

			String output = String.format(Locale.US,
					rowFormat,
					person,
					delta,
					tokensSent.get(person),
					tvalSent.get(person),
					tokensRec.get(person),
					tvalRec.get(person) * -1
			);
			table.put(person, output);
		}
		tvalTable = String.join("\n", table.values());

		ArrayList<String> futures = new ArrayList<>();
		if (nowEpoch < endEpoch) {
			futures.add("__Running Token Value__");
			futures.add(String.format("<:icon_token:653018008670961665> Now: `%s`", tvalNow));

			if (tval30Mins > 0.03)
				futures.add(String.format("In 30 minutes: `%s`", tval30Mins));
			if (tval60Mins > 0.03)
				futures.add(String.format("In 60 minutes: `%s`", tval60Mins));
		} else {
			futures.add(":tada: Contract Complete!");
		}
		futureTable = String.join("\n", futures);

		genTimeLine = String.format("Report Generated at <t:%1$s> (<t:%1$s:R>)", nowEpoch);

		data.put("startEstimate", Boolean.toString(startEstimate));
		data.put("endEstimate", Boolean.toString(endEstimate));
		data.put("nowEpoch", Long.toString(nowEpoch));
		data.put("startEpoch", Long.toString(startEpoch));
		data.put("endEpoch", Long.toString(endEpoch));
		data.put("tvalNow", Double.toString(tvalNow));
		data.put("tval30Mins", Double.toString(tval30Mins));
		data.put("tval60Mins", Double.toString(tval60Mins));
		data.put("genTimeLine", genTimeLine);
		data.put("startLine", startLine);
		data.put("endLine", endLine);
		data.put("tvalTable", tvalTable);
		data.put("futureTable", futureTable);
	}

	public String sinkReport() {
		calculateValues();
		//final String url = "https://discord.com/channels/455380663013736479/455512567004004353/1217529083286651082";
		final String[] out = new String[]{
				"# __Tokification__ (Android Alpha :eyes:)",
				"",
				data.get("genTimeLine"),
				"_This message will be manually updated every 15 to 45 minutes, depending on how busy I am._",
				"",
				"__Contract Info__",
				data.get("startLine"),
				data.get("endLine"),
				"_Note that all token values are only accurate once the end time is accurate._",
				"",
				data.get("futureTable"),
				"",
				"__Player's Current TVals__ (as seen by the :people_hugging: sink)",
				"```",
				"Player      |   Δ TVal| +TS|  +TSVal| -TR|   -TRVal",
				"------------+---------+----+--------+----+---------",
				data.get("tvalTable"),
				"```",
				/*String.format(*/"_This is not a wonky command, but an app written by ItsJustSomeDude. Stay tuned for further updates!_"/*, url)*/,
				// See [the FAQ](%s) for more info.
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

	public static double tval(Calendar coopStart, Calendar coopEnd, Calendar token, int count) {
		long tokenTime = token.getTimeInMillis() / 1000L;
		long startTime = coopStart.getTimeInMillis() / 1000L;
		long endTime = coopEnd.getTimeInMillis() / 1000L;

		return tval(startTime, endTime, tokenTime, count);
	}
}
