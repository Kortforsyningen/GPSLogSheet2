package ref.sdfe.gpslogsheet2;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

/**
 * Created by B028406 on 8/31/2017.
 */

public class SetupsFragment extends Fragment {

    Integer id;
    ProjectEntry project;
    ProjectEntry.Setup setup;


    FixedpointEntry currentFixedPoint;
    int currentFixedPointId;
    InstrumentEntry currentInstrument;
    int currentInstrumentId;
    AlarmEntry currentAlarm;
    int currentAlarmId;
    AntennaEntry currentAntenna;
    int currentAntennaId;

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

    // Spinners
    Spinner fixedPointSpinner;
    Spinner instrumentSpinner;
    Spinner antennaSpinner;
    Spinner alarmSpinner;

    //Location
    public static Boolean locationPermitted;
    double latitude;
    double longitude;
    boolean locationFound;

    BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("ref.sdfe.gpslogsheet2.LOCATION_UPDATED")) {
                locationFound = true;
                latitude = intent.getDoubleExtra("lat",0.0f);
                longitude = intent.getDoubleExtra("lon",0.0f);
                Log.i("SetupsFragment", "Location received: Latitude: " + latitude + ", Longitude: " + longitude);
            }
        }
    };

    public SetupsFragment() {


    }
    @Override
    public void onStart() {
        super.onStart();
        getContext().registerReceiver(locationReceiver, new IntentFilter("ref.sdfe.gpslogsheet2.LOCATION_UPDATED"));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("SetupsFragment", "onResume()");
    }
    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(locationReceiver);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle bundle) {
        // OLDJO: Inflate the layout
        View view = inflater.inflate(R.layout.setup_tab_layout, container, false);
        id = getArguments().getInt("Id");
        project = ProjectActivity.project;
        setup = project.getSetups().get(id);
        Log.i("SetupsFragment", "Getting setup: " + setup.getId()
                + ", from project: " + project.getId() + ", called: " + project.getName());

        //Load current values
        try {
            currentFixedPointId = setup.getFixedPointId();
        } catch (NullPointerException e) {
            currentFixedPointId = -1;
        }
        try {
            currentInstrumentId = setup.getInstrumentId();
        } catch (NullPointerException e) {
            currentInstrumentId = -1;
        }
        try {
            currentAlarmId = setup.getAlarmId();
        } catch (NullPointerException e) {
            currentAlarmId = -1;
        }
        try {
            currentAntennaId = setup.getAntennaId();
        } catch (NullPointerException e) {
            currentAntennaId = -1;
            Log.i("huh", "null");
        }


        eastings = ProjectActivity.eastings;
        northings = ProjectActivity.northings;
        gpsNames = ProjectActivity.gpsNames;
        hsNames = ProjectActivity.hsNames;
        antennaNames = ProjectActivity.antennaNames;
        alarmNames = ProjectActivity.alarmNames;
        instrumentNames = ProjectActivity.instrumentNames;

        //Location
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermitted = false;
            Log.i("SetupsFragment", "Location not permitted.");
        } else {
            locationPermitted = true;
            Log.i("SetupsFragment", "Location permitted.");
        }

        if (locationPermitted) {
            Log.i("SetupsFragment", "location permitted");
            //getLocation();

        } else {
            Log.i("SetupsFragment", "location denied");
        }

        // TextViews

        // Spinners
        fixedPointSpinner = (Spinner) view.findViewById(R.id.fixedPointSpinner);
        instrumentSpinner = (Spinner) view.findViewById(R.id.instrumentSpinner);
        antennaSpinner = (Spinner) view.findViewById(R.id.antennaSpinner);
        alarmSpinner = (Spinner) view.findViewById(R.id.alarmSpinner);

        fixedPointAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, ProjectActivity.fixedpointEntries);
        instrumentAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, ProjectActivity.instrumentEntries);
        antennaAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, ProjectActivity.antennaEntries);
        alarmAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, ProjectActivity.alarmEntries);

        fixedPointSpinner.setAdapter(fixedPointAdapter);
        instrumentSpinner.setAdapter(instrumentAdapter);
        antennaSpinner.setAdapter(antennaAdapter);
        alarmSpinner.setAdapter(alarmAdapter);

        // Set the spinners to saved values:
        fixedPointSpinner.setSelection(getIndex(fixedPointSpinner, setup.getFixedPoint()));
        instrumentSpinner.setSelection(getIndex(instrumentSpinner, setup.getInstrument()));
        antennaSpinner.setSelection(getIndex(antennaSpinner, setup.getAntenna()));
        alarmSpinner.setSelection(getIndex(alarmSpinner, setup.getAlarm()));

        // Set up listeners that set the project to modified if a *new* selection is made.
        fixedPointSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFixedPoint = (FixedpointEntry) parent.getItemAtPosition(position);
                if (currentFixedPointId != currentFixedPoint.getID()) {
                    setup.setFixedPointId(currentFixedPoint.getID());
                    setup.setFixedPoint(currentFixedPoint.getGPSName());
                    setup.setHsName(currentFixedPoint.getHSName());
                    project.setModDate();
                    Log.i("SetupsFragment", "Selected FixedPoint: " + currentFixedPoint.getGPSName());
                } else {
                    Log.i("SetupsFragment", "Selected SAME FixedPoint: " + currentFixedPoint.getGPSName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        instrumentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentInstrument = (InstrumentEntry) parent.getItemAtPosition(position);
                if (currentInstrumentId != currentInstrument.getID()) {
                    Log.i("SetupsFragment", "Selected Instrument: " + currentInstrument.getName());
                    setup.setInstrument(currentInstrument.getName());
                    setup.setInstrumentId(currentInstrument.getID());
                    project.setModDate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        antennaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentAntenna = (AntennaEntry) parent.getItemAtPosition(position);
                if (currentAntennaId != currentAntenna.getID()) {
                    Log.i("SetupsFragment", "Selected Antenna: " + currentAntenna.getName());
                    setup.setAntenna(currentAntenna.getName());
                    setup.setAntennaId(currentAntenna.getID());
                    project.setModDate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        alarmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentAlarm = (AlarmEntry) parent.getItemAtPosition(position);
                if (currentAlarmId != currentAlarm.getID()) {
                    Log.i("SetupsFragment", "Selected Alarm: " + currentAlarm.getName());
                    setup.setAlarm(currentAlarm.getName());
                    setup.setAlarmId(currentAlarm.getID());
                    project.setModDate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Buttons
        Button location_button = (Button) view.findViewById(R.id.locationButton);
        Button rename_button = (Button) view.findViewById(R.id.renameButton);
        Button delete_button = (Button) view.findViewById(R.id.deleteButton);
        Button clear_button = (Button) view.findViewById(R.id.clearButton);

        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LOCATE CODE HERE
                Log.i("SetupsFragment", "Location Button Pressed!");
                useLocationButton();
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
                // CLEAR CODE HERE
                Log.i("SetupsFragment", "Clear Button Pressed!");
                //TODO: Move this to a confirmation dialog.
                fixedPointSpinner.setSelection(getIndex(fixedPointSpinner, gpsNames.get(0)));
            }
        });

        return view;
    }

    // Method to get the Spinners to show the correct selection.
    private int getIndex(Spinner spinner, String myString) {

        int index = 0; //return 0 if not found.

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    private void useLocationButton(){
        if(locationFound){
            double distance;
            double tempDistance = 1e9;
            int tempIndex = 0;
            for(int i = 0; i<eastings.size(); i++){
                //This is not very accurate, e.g. it is the numerical distance in degrees
                //It did correctly identify Matriklen to be KMS3 though.
                distance = sqrt((eastings.get(i)-longitude)*(eastings.get(i)-longitude)
                        + (northings.get(i)-latitude)*(northings.get(i)-latitude));
                if (distance < tempDistance){
                    tempDistance = distance;
                    tempIndex = i;
                }
            }
            Log.i("useLocationButton()","Nearest point ID: " + tempIndex
                    + ", and name: " +  gpsNames.get(tempIndex)
                    + ", and distance: " + tempDistance);

            fixedPointSpinner.setSelection(getIndex(fixedPointSpinner, gpsNames.get(tempIndex)),true);
            //Added boolean 'true' so the new location is saved.

            //TODO: Also display distance to said point. (So far only i Log.i)
            //TODO: Perhaps do this all in a dialog so the user can choose to abort?

        }else{
            //TODO: Message that location has not been found yet... beg for patience! :D
            int duration = Toast.LENGTH_SHORT;
            String toast_message = "Location not yet available.";
            Toast toast = Toast.makeText(getContext(),toast_message, duration);
            toast.show();
        }
    }
}