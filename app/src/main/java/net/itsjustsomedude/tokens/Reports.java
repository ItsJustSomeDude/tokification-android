package net.itsjustsomedude.tokens;
import java.util.ArrayList;
import java.util.Calendar;

public class Reports {
	public static String sinkReport(Coop coop) {
		long now = Calendar.getInstance().getTimeInMillis();
		
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
			end = coop.endTime.getTimeInMillis() / 1000l;
			startLine = String.format(":alarm_clock: End Time: <t:%1$s> (<t:%1$s:R>)", end);
		}
		
		ArrayList<String> rVals = new ArrayList<String>();
        if (now < end) {
			rVals.add("__Running Token Value__");
			double tvalNow = round(tval(start, end, now, 1), 4);
			rVals.add(String.format("<:icon_token:653018008670961665> Now: `%s`", tvalNow));

			//if next > 0.03:
            //rVals.append(f"In 30 minutes: `{ next }`",)

            //if next2 > 0.03:
            //rVals.append(f"In 60 minutes: `{ next2 }`",)
	
		} else {
			rVals.add(":tada: Contract Complete!");
		}

		return "";
	}

    private static double round(double input, int roundTo) {
        double i = Math.pow(10d, roundTo);
		return Math.round(input * i) / i;
	}

    public static double tval(long startTime, long endTime, long tokenTime, int count) {
	    long duration = endTime - startTime;
		long elapsed = tokenTime - startTime;
		
		double i = Math.pow(1 - 0.9 * (elapsed / duration), 4);
		double singleValue = Math.max(i, 0.03);
		
		return singleValue * count;
    }

    public static double tval(Calendar coopStart, Calendar coopEnd, Calendar token, int count) {
		long tokenTime = token.getTimeInMillis() / 1000l;
		long startTime = coopStart.getTimeInMillis() / 1000l;
		long endTime = coopEnd.getTimeInMillis() / 1000l;
		
		return tval(startTime, endTime, tokenTime, count);
	}
}
