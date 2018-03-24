package caseydlvr.recurringtasks.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.models.DurationUnit;


public class TaskActivity extends AppCompatActivity {

    @BindView(R.id.durationUnitSpinner) Spinner mDurationUnitSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        ButterKnife.bind(this);
        populateSpinner();
    }

    private void populateSpinner() {
        ArrayAdapter<DurationUnit> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                buildDurationUnits());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDurationUnitSpinner.setAdapter(adapter);
    }

    private List<DurationUnit> buildDurationUnits() {
        List<DurationUnit> durationUnits = new ArrayList<>();
        durationUnits.add(new DurationUnit("hour", getString(R.string.hours)));
        durationUnits.add(new DurationUnit("day", getString(R.string.days)));
        durationUnits.add(new DurationUnit("week", getString(R.string.weeks)));
        durationUnits.add(new DurationUnit("month", getString(R.string.months)));
        durationUnits.add(new DurationUnit("year", getString(R.string.years)));

        return durationUnits;
    }
}
