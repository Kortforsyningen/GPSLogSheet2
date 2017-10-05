package ref.sdfe.gpslogsheet2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.layout.simple_list_item_1;

/**
 * Created by B028406 on 8/31/2017.
 */

public class SetupsFragment extends Fragment {
    Integer id;
    ProjectEntry project;
    ProjectEntry.Setup setup;

    FixedpointEntry currentFixedPoint;
    InstrumentEntry currentInstrument;
    AlarmEntry currentAlarm;
    AntennaEntry currentAntenna;

    // Lists
    List<Double> eastings = new ArrayList<>();
    List<Double> northings = new ArrayList<>();
    List<String> gpsNames = new ArrayList<>();
    List<String> hsNames = new ArrayList<>();
    List<String> antennaNames = new ArrayList<>();
    List<String> alarmNames = new ArrayList<>();
    List<String> instrumentNames = new ArrayList<>();

    // Adapters
    ArrayAdapter<FixedpointEntry> fixedPointAdapter;
    ArrayAdapter instrumentAdapter;
    ArrayAdapter antennaAdapter;
    ArrayAdapter alarmAdapter;


    public SetupsFragment() {


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("SetupsFragment", "onResume()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle bundle) {
        // OLDJO: Inflate the layout
        View view = inflater.inflate(R.layout.setup_tab_layout, container, false);
        id = getArguments().getInt("Id");
        project = ProjectActivity.project;
        setup = project.getSetups().get(id);
        Log.i("SetupsFragment","Getting setup: " + setup.getId()
                + ", from project: " + project.getId() + ", called: " + project.getName());
        eastings = ProjectActivity.eastings;
        northings = ProjectActivity.northings;
        gpsNames = ProjectActivity.gpsNames;
        hsNames = ProjectActivity.hsNames;
        antennaNames = ProjectActivity.antennaNames;
        alarmNames = ProjectActivity.alarmNames;
        instrumentNames = ProjectActivity.instrumentNames;

        // TextViews

        // Spinners
        final Spinner fixedPointSpinner = (Spinner) view.findViewById(R.id.fixedPointSpinner);
        final Spinner instrumentSpinner = (Spinner) view.findViewById(R.id.instrumentSpinner);
        final Spinner antennaSpinner = (Spinner) view.findViewById(R.id.antennaSpinner);
        final Spinner alarmSpinner = (Spinner) view.findViewById(R.id.alarmSpinner);

        fixedPointAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,ProjectActivity.fixedpointEntries);
        instrumentAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,ProjectActivity.instrumentEntries);
        antennaAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,ProjectActivity.antennaEntries);
        alarmAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,ProjectActivity.alarmEntries);

        fixedPointSpinner.setAdapter(fixedPointAdapter);
        instrumentSpinner.setAdapter(instrumentAdapter);
        antennaSpinner.setAdapter(antennaAdapter);
        alarmSpinner.setAdapter(alarmAdapter);

        // Set the spinners to saved values:
        fixedPointSpinner.setSelection(getIndex(fixedPointSpinner, setup.getFixedPoint()));
        instrumentSpinner.setSelection(getIndex(instrumentSpinner, setup.getInstrument()));
        antennaSpinner.setSelection(getIndex(antennaSpinner, setup.getAntenna()));
        alarmSpinner.setSelection(getIndex(alarmSpinner, setup.getAlarm()));

        fixedPointSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFixedPoint = (FixedpointEntry) parent.getItemAtPosition(position);
                Log.i("SetupsFragment","Selected FixedPoint: " + currentFixedPoint.getGPSName());
                setup.setFixedPointId(currentFixedPoint.getID());
                setup.setFixedPoint(currentFixedPoint.getGPSName());
                setup.setHsName(currentFixedPoint.getHSName());
                project.setModDate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("SetupsFragment","No Setup Selected");
            }
        });
        instrumentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentInstrument = (InstrumentEntry) parent.getItemAtPosition(position);
                Log.i("SetupsFragment","Selected Instrument: " + currentInstrument.getName());
                setup.setInstrument(currentInstrument.getName());
                project.setModDate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
        antennaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentAntenna = (AntennaEntry) parent.getItemAtPosition(position);
                Log.i("SetupsFragment","Selected Antenna: " + currentAntenna.getName());
                setup.setAntenna(currentAntenna.getName());
                setup.setAntennaId(currentAntenna.getID());
                project.setModDate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        alarmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentAlarm = (AlarmEntry) parent.getItemAtPosition(position);
                Log.i("SetupsFragment","Selected Alarm: " + currentAlarm.getName());
                setup.setAlarm(currentAlarm.getName());
                setup.setAlarmId(currentAlarm.getID());
                project.setModDate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Buttons
        Button location_button = (Button) view.findViewById(R.id.locationButton);
        Button rename_button = (Button) view.findViewById(R.id.renameButton);
        Button delete_button = (Button) view.findViewById(R.id.deleteButton);
        Button clear_button = (Button) view.findViewById(R.id.clearButton);

        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LOCADE CODE HERE
                Log.i("SetupsFragment", "Location Button Pressed!");
            }
        });
        rename_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RENAME CODE HERE
                Log.i("SetupsFragment", "Rename Button Pressed!");
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // REMOVE TAB CODE HERE
                Log.i("SetupsFragment", "Delete Button Pressed!");

                project.removeSetup(id);
                //Find a way to remove it correctly.

                getFragmentManager().beginTransaction().remove(SetupsFragment.this).commitAllowingStateLoss();
                //This only removes the content of the tab, not the
                Log.i("SetupsFragment", "huh");


            }
        });
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RENAME CODE HERE
                Log.i("SetupsFragment", "Clear Button Pressed!");
                fixedPointSpinner.setId(-1);
            }
        });

        return view;
    }
    // Method to get the Spinners to show the correct selection.
    private int getIndex(Spinner spinner, String myString){

        int index = 0; //return 0 if not found.

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equals(myString)){
                index = i;
            }
        }
        return index;
    }
}