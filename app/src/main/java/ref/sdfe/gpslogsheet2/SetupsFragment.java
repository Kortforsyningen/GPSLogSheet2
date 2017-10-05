package ref.sdfe.gpslogsheet2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
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
import static android.content.Context.LOCATION_SERVICE;

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

    //Location
    public static Boolean locationPermitted;
    LocationManager locationManager;
    Location location;
    double latitude;
    double longitude;
    boolean locationFound;

    public Integer MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    public Integer MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 200;


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
            getLocation();
        } else {
            Log.i("SetupsFragment", "location denied");
        }

        //Log.i("SetupsFragment","Current Location Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());


        // TextViews

        // Spinners
        final Spinner fixedPointSpinner = (Spinner) view.findViewById(R.id.fixedPointSpinner);
        final Spinner instrumentSpinner = (Spinner) view.findViewById(R.id.instrumentSpinner);
        final Spinner antennaSpinner = (Spinner) view.findViewById(R.id.antennaSpinner);
        final Spinner alarmSpinner = (Spinner) view.findViewById(R.id.alarmSpinner);

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
    private int getIndex(Spinner spinner, String myString) {

        int index = 0; //return 0 if not found.

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    private void getLocation() {

        // TODO: This does NOT work at the moment, it is a mess eiter way.

        int MIN_TIME_BW_UPDATES = 0;
        int MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;


        // PERMISSIONS!
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
        }


        Location location = null;
        Log.i("getLocation()", "Called.");


        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
            Log.i("getLocation()", "no network provider is enabled");
        } else {
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                Log.i("getLocation()", "onLocationChanged");
                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                locationFound = true;
                                Log.i("getLocation()", "Latitude: " + latitude + ", Longitude: " + longitude);
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                Log.i("getLocation()", "onStatusChanged");
                            }

                            @Override
                            public void onProviderEnabled(String provider) {
                                Log.i("getLocation()", "onProviderEnabled");
                            }

                            @Override
                            public void onProviderDisabled(String provider) {
                                Log.i("getLocation()", "onProviderDisabled");
                            }
                        });
                Log.d("Network", "Network Enabled");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    Log.i("getLocation()", "onLocationChanged, GPS");
                                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    locationFound = true;
                                    Log.i("getLocation()", "Latitude: " + latitude + ", Longitude: " + longitude);
                                }

                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {
                                    Log.i("getLocation()", "onStatusChanged, GPS");
                                }

                                @Override
                                public void onProviderEnabled(String provider) {
                                    Log.i("getLocation()", "onProviderEnabled, GPS");
                                }

                                @Override
                                public void onProviderDisabled(String provider) {
                                    Log.i("getLocation()", "onProviderDisabled, GPS");
                                }
                            });
                    Log.d("GPS", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        }
    }
    private void useLocationButton(){
        if(locationFound){
            //TODO: calculate distances from location to each fixedpoint and choose the nearest.
            //TODO: Also display distance to said point.
            //TODO: Make spinner select that point
            //TODO: Perhaps do this all in a dialog so the user can choose to abort?

        }else{
            //TODO: Message that location has not been found yet... beg for patience! :D
        }
    }
}