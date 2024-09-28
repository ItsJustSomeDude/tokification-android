package net.itsjustsomedude.tokens.reports

class DetailedReport : Report() {
    override fun generate(data: ReportData): String {
        if (data.isStartEstimated || data.isEndEstimated)
            return "Start and End are required for detailed report."

        val header = "Elapse|# |D|Prior|Diff"
        val rowFormat = "`{:6d}|{:2d}|{:1s}|{:5.2f}|{:s}{:4.2f}|`<t:{:s}:f>"

        val rows = mapOf<String, List<String>>()
        val cums = mapOf<String, Double>()

        return "Thoon."
    }
}

//		for (String person : coop.getPeople(sinkName)) {
//			rows.put(person, new ArrayList<>());
//			cums.put(person, 0.0);
//		}
//
//		for (Coop.Event ev : coop.events) {
//			long t = ev.time.getTimeInMillis() / 1000L;
//			long elapsedSeconds = t - startEpoch;
//			double tv = tval(startEpoch, endEpoch, t, ev.count);
//
//			String direction = ev.direction.equals("sent")
//					? "→" : "←";
//
//			String sign = ev.direction.equals("sent")
//					? "+" : "-";
//
//			String row = String.format(Locale.US, rowFormat,
//					elapsedSeconds,
//					ev.count,
//					direction,
//					cums.get(ev.person),
//					sign,
//					tv,
//					t
//			);
//
//			// add to row list
//			// add tv to cums
//		}
//    return ""

//        t = ts(ev['time'])
//        d = ev['direction']
//        c = int(ev['count'])
//        p = ev['player']
//
//        ela = t - start
//        tv = tval(start, end, t, c)
//
//        sign = ""
//        direction = "←"
//        if ev['direction'] == 'sent':
//            direction = "→"
//            sign = "+"
//        else:
//            tv = tv * -1
//
//        # row = f"<t:{t}:f>`\t|{ela}|{c}|{direction}|{round(cums[p], 2)}|{sign}{round(tv, 2)}`"
//        row = rowFormat.format(ela, c, direction, cums[p], sign, tv, f"<t:{t}:f>")
//        rows[p].append(row)
//
//        cums[p] += tv
//
//    table = ["# __Tokification Detailed Report__",
//        "Key:",
//        "`Time (timeElapsed): count ↔ direction: runningDelta ±change`\n",
//    ]
//    for person in people:
//        table.append(f"__{person}__")
//        table.append(f"`{header}`")
//        table.append("\n".join(rows[person]))
//        table.append(f"Final TVal: `{round(cums[person], 2)}`")
//        table.append("")
//
//    print(table)
//    return "\n".join(table)
//}
