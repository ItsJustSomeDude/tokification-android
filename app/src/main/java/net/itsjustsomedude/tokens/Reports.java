package net.itsjustsomedude.tokens;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Reports {
	private Coop coop;
	
	public Reports(Coop coop) {
		
	}
	
	public static String sinkReport(Coop coop) {
		long now = Calendar.getInstance().getTimeInMillis() / 1000l;
		
		String startLine = "";
        String endLine = "";
		
		long start;
		long end;
		
		if (coop.startTime == null) {
			long sinceHourStart = now % 3600;
            start = now - sinceHourStart;
            startLine = ":warning: No start time set, assuming start of current hour.";
		} else {
			start = coop.startTime.getTimeInMillis() / 1000l;
			startLine = String.format("<:contract:589317482901405697> Start Time: <t:%1$s> (<t:%1$s:R>)", start);
		}

		if (coop.endTime == null) {
            end = start + 12 * 60 * 60;
            endLine = ":warning: No end time set, assuming 12 hours from start time.";
		} else {
			end = coop.endTime.getTimeInMillis() / 1000L;
			startLine = String.format(":alarm_clock: End Time: <t:%1$s> (<t:%1$s:R>)", end);
		}
		
		ArrayList<String> rVals = new ArrayList<String>();
        if (now < end) {
			rVals.add("__Running Token Value__");
			Log.i("Report", "TVal at Current Moment");
			double tvalNow = round(tval(start, end, now, 1), 4);
			rVals.add(String.format("<:icon_token:653018008670961665> Now: `%s`", tvalNow));

			//next = round(tval(start, end, now + 30 * 60, 1), 4)
            //next2 = round(tval(start, end, now + 60 * 60, 1), 4)
			
			//if next > 0.03:
            //rVals.append(f"In 30 minutes: `{ next }`",)

            //if next2 > 0.03:
            //rVals.append(f"In 60 minutes: `{ next2 }`",)
		} else {
			rVals.add(":tada: Contract Complete!");
		}
		
		String rowFormat = "%-12.12s|%9.3f|%4d|%8.3f|%4d|%9.3f";

		HashMap<String, String> table = new HashMap<>();

        HashMap<String, Integer> tokensSent = new HashMap<>();
		HashMap<String, Integer> tokensRec = new HashMap<>();
		HashMap<String, Double> tvalSent = new HashMap<>();
		HashMap<String, Double> tvalRec = new HashMap<>();
		
		for (String person : coop.getPeople("sink")) {
			tokensSent.put(person, 0);
			tokensRec.put(person, 0);
			tvalSent.put(person, 0d);
			tvalRec.put(person, 0d);
		}
		
		Log.i("Report", "Number of events: " + coop.events.size());
		
		for (Event ev : coop.events) {
			long time = ev.time.getTimeInMillis() / 1000l;
			
			double tv = tval(start, end, time, ev.count);
			if (ev.direction.equals("sent")) {
				tokensSent.merge(ev.person, ev.count, Integer::sum);
				tvalSent.merge(ev.person, tv, Double::sum);
				// TODO: Add Sink.
			} else {
				tokensRec.merge(ev.person, ev.count, Integer::sum);
				tvalRec.merge(ev.person, tv, Double::sum);
				// TODO: Add Sink
			}
		}
		
		for (String person : coop.getPeople("sink")) {
			double delta = tvalSent.get(person) - tvalRec.get(person);
			
			Log.i("Report", delta + " Delta");
            
            String output = String.format(
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
		
		final String url = "https://discord.com/channels/455380663013736479/455512567004004353/1217529083286651082";
        final String[] out = new String[] {
			"# __Tokification__ (Android Alpha :eyes:)",
            "",
            String.format("Report Generated at <t:%1$s> (<t:%1$s:R>)", now),
            "_This message will be manually updated every 15 to 45 minutes, depending on how busy I am._",
            "",
            "__Contract Info__",
            startLine,
            endLine,
            "_Note that all token values are only accurate once the end time is accurate._",
            "",
			String.join("\n", rVals),
            "",
            "__Player's Current TVals__ (as seen by the :people_hugging: sink)",
            "```",
            "Player      |   Δ TVal| +TS|  +TSVal| -TR|   -TRVal",
            "------------+---------+----+--------+----+---------",
            String.join("\n", table.values()),
            "```",
            String.format("_This is not a wonky command, but an app written by ItsJustSomeDude. Stay tuned for further updates!_", url),
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
		long tokenTime = token.getTimeInMillis() / 1000l;
		long startTime = coopStart.getTimeInMillis() / 1000l;
		long endTime = coopEnd.getTimeInMillis() / 1000l;
		
		return tval(startTime, endTime, tokenTime, count);
	}
}
