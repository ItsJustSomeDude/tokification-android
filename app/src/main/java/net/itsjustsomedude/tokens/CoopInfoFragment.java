package net.itsjustsomedude.tokens;

import static net.itsjustsomedude.tokens.SimpleDialogs.datePicker;
import static net.itsjustsomedude.tokens.SimpleDialogs.registerActivityCallback;
import static net.itsjustsomedude.tokens.SimpleDialogs.textPicker;
import static net.itsjustsomedude.tokens.SimpleDialogs.timePicker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.itsjustsomedude.tokens.databinding.FragmentCoopInfoBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CoopInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CoopInfoFragment extends Fragment {
	public static final String TAG = "CoopInfoFragment";

	private FragmentCoopInfoBinding binding;
	private static final String ARG_COOP_ID = "CoopId";
	private long coopId;
	private Coop coop;
	private Database database;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

	private ActivityResultLauncher<Intent> activityCallback;

	public CoopInfoFragment() {
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param coopId Coop ID.
	 * @return A new instance of fragment CoopInfoFragment.
	 */
	public static CoopInfoFragment newInstance(long coopId) {
		CoopInfoFragment fragment = new CoopInfoFragment();
		Bundle args = new Bundle();
		args.putLong(ARG_COOP_ID, coopId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			coopId = getArguments().getLong(ARG_COOP_ID);

			database = new Database(getContext());
			coop = database.fetchCoop(coopId);
		}

		activityCallback = registerActivityCallback(requireActivity(), result -> {
			refresh();
			render();
		});

	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment using view binding
		binding = FragmentCoopInfoBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		render();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	public void refresh() {
		// This refreshes the coop.
		coop = database.fetchCoop(coopId);
	}

	public void render() {
		if (coop == null) {
			binding.noCoopSelected.setVisibility(View.VISIBLE);
			binding.innerLayout.setVisibility(View.GONE);
			return;
		}

		//Log.i(TAG, "Test: " + coop.id);
		//TODO: This is not ideal, but works for now.
		//Eventually, there should be a service to handle notifications,
		//and that will make this less ugly.
		new NotificationHelper(requireContext()).sendActions(coop);

		binding.coopName.setText(coop.name);
		binding.nameEdit.setOnClickListener(view ->
				textPicker(
						requireContext(),
						"Rename Coop",
						coop.name,
						str -> {
							coop.name = str;
							database.saveCoop(coop);
							// We need to re-fetch the coop because when the name is changed
							// all the events need to be re-fetched.
							coop = database.fetchCoop(coopId);
							render();
						}
				)
		);

		binding.contractName.setText(coop.contract);
		binding.contractEdit.setOnClickListener(view ->
				textPicker(
						requireContext(),
						"Change Contract",
						coop.contract,
						str -> {
							coop.contract = str;
							database.saveCoop(coop);
							// We need to re-fetch the coop because when the name is changed
							// all the events need to be re-fetched.
							coop = database.fetchCoop(coopId);
							render();
						}
				)
		);

		String eventsButtonText = getString(R.string.edit_events, coop.events.size());
		binding.eventsEdit.setText(eventsButtonText);
		binding.eventsEdit.setOnClickListener(view ->
				activityCallback.launch(
						new Intent(requireContext(), EventListActivity.class)
								.putExtra(EventListActivity.PARAM_COOP_ID, coop.id)
				)
		);
		binding.eventsAdd.setOnClickListener(view ->
				activityCallback.launch(
						EditEventActivity.makeCreateIntent(requireContext(), coopId)
				)
		);

		if (coop.startTime == null) {
			binding.startDateButton.setText(R.string.unset_button);
			binding.startTimeButton.setText(R.string.unset_button);
		} else {
			binding.startDateButton.setText(
					dateFormat.format(coop.startTime.getTime())
			);
			binding.startTimeButton.setText(
					timeFormat.format(coop.startTime.getTime())
			);
		}
		binding.startDateButton.setOnClickListener(view ->
				datePicker(requireContext(), coop.startTime, cal -> {
					coop.startTime = cal;
					database.saveCoop(coop);
					render();
				})
		);

		binding.startTimeButton.setOnClickListener(view ->
				timePicker(requireContext(), coop.startTime, cal -> {
					coop.startTime = cal;
					database.saveCoop(coop);
					render();
				})
		);

		if (coop.endTime == null) {
			binding.endDateButton.setText(R.string.unset_button);
			binding.endTimeButton.setText(R.string.unset_button);
		} else {
			binding.endDateButton.setText(
					dateFormat.format(coop.endTime.getTime())
			);
			binding.endTimeButton.setText(
					timeFormat.format(coop.endTime.getTime())
			);
		}
		binding.endDateButton.setOnClickListener(view ->
				datePicker(requireContext(), coop.endTime, cal -> {
					coop.endTime = cal;
					database.saveCoop(coop);
					render();
				})
		);

		binding.endTimeButton.setOnClickListener(view ->
				timePicker(requireContext(), coop.endTime, cal -> {
					coop.endTime = cal;
					database.saveCoop(coop);
					render();
				})
		);

		binding.modeSwitch.setChecked(coop.sinkMode);
		binding.modeSwitch.setOnCheckedChangeListener((view, newState) -> {
			coop.sinkMode = newState;
			database.saveCoop(coop);
			render();
		});

		if (coop.sinkMode) {
			binding.report.setVisibility(View.GONE);
			binding.sinkReportSection.setVisibility(View.VISIBLE);
			//TODO: Don't hard-code reports, set the ddl list here.
		} else {
			String report = new ReportBuilder(coop).normalReport();
			binding.report.setText(report);
			binding.report.setVisibility(View.VISIBLE);
			binding.sinkReportSection.setVisibility(View.GONE);
		}

		binding.reportGenerate.setOnClickListener(view -> {
			if (binding.reportSelect.getSelectedItemPosition() == 0) {
				String report = ReportBuilder.makeBuilder(requireActivity(), coop).sinkReport();
				ReportBuilder.copyText(requireContext(), report);
			} else
				Toast.makeText(requireContext(), "Not implemented yet...", Toast.LENGTH_SHORT).show();
		});
	}
}