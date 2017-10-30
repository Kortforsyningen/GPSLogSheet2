package ref.sdfe.gpslogsheet2;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.id.list;
import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static java.lang.Math.sqrt;

/**
 * Fragment that holds the current setup. Used by tabhost in ProjectActivity.
 * Created by B028406 on 8/31/2017.
 */

public class SetupsFragment extends Fragment {

    Integer id;
    ProjectEntry project;
    ProjectEntry.Setup setup;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final public Integer MY_PERMISSIONS_REQUEST_CAMERA = 200;


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

    // TextViews
    String hsNameString;
    TextView hsNameTextView;

    // Photos
    PhotoList photoAdapter;
    ListView photoList;
    ArrayList<Bitmap> photos = new ArrayList<>();
    ArrayList<String> photoTexts = new ArrayList<>();
    String mCurrentPhotoPath;

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

        // Spinners and textview
        fixedPointSpinner = (Spinner) view.findViewById(R.id.fixedPointSpinner);
        hsNameTextView = (TextView) view.findViewById(R.id.hsNameTextView);
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

        // Set the Spinners and TextViews to saved values:
        fixedPointSpinner.setSelection(getIndex(fixedPointSpinner, setup.getFixedPoint()));
        hsNameString = "hsName: " + setup.getHsName();
        hsNameTextView.setText(hsNameString);
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
                    hsNameString = "hsName: " + setup.getHsName();
                    hsNameTextView.setText(hsNameString);
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
        Button camera_button = (Button) view.findViewById(R.id.cameraButton);

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
                // TODO: Implement a way to rename setups
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // REMOVE TAB CODE HERE
                Log.i("SetupsFragment", "Delete Button Pressed!");

                project.removeSetup(id);
                //TODO: Find a way to remove it correctly.

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
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                useCameraButton();
                Log.i("SetupsFragment", "Camera button Pressed!");
            }
        });

        // Photos
        // Placeholder empty arrays
        //ArrayList<Bitmap> photos = new ArrayList<>();
        //ArrayList<String> photoTexts = new ArrayList<>();

        // TODO: Populate photos

        photoAdapter = new PhotoList(this.getActivity(),photoTexts,photos);
        photoList = (ListView) view.findViewById(R.id.photo_list);
        photoList.setAdapter(photoAdapter);
        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO: Open a larger view of photo (perhaps with options to delete photo, edit text etc. etc.)
                Log.i("SetupsFragment","Photo Clicked");
                //View photo

                final Dialog photo_dialog = new Dialog(SetupsFragment.this.getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View convertView = inflater.inflate(R.layout.photo_dialog, new LinearLayoutCompat(getContext()), false);
                photo_dialog.setContentView(convertView);
                photo_dialog.setCancelable(true);
                photo_dialog.setTitle("Photo Dialog"); //TODO: change this to something relevant.

                final ImageView photoImageView = (ImageView) photo_dialog.findViewById(R.id.photo_image_view);
                final TextView photoTextView = (TextView) photo_dialog.findViewById(R.id.photo_text_view);

                //TODO: This is just a thumbnail, get the actual photo.
                photoImageView.setImageBitmap(photos.get(position));
                photoTextView.setText(photoTexts.get(position));

                //TODO: Add text changed listener to enable user to edit the text.
                //photoTextView.


                //Button cancel_button = (Button) photo_dialog.findViewById(R.id.cancel_button);
                final Button photo_delete_button = (Button) photo_dialog.findViewById(R.id.photo_delete_button);

                //Switch that toggles delete button
                Switch editSwitch = (Switch) photo_dialog.findViewById(R.id.photo_delete_switch);
                editSwitch.setChecked(false);
                editSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            photo_delete_button.setEnabled(true);
                            Log.i("ProjectActivity", "Delete switch on :O !");
                        } else {
                            photo_delete_button.setEnabled(false);
                            Log.i("ProjectActivity", "Delete switch off!");
                        }
                    }
                });

//                cancel_button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        delete_dialog.dismiss();
//                    }
//                });
                photo_delete_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // DELETE CODE HERE
                        //db.delete Photo?
                        photo_dialog.dismiss();
                        //final SharedPreferences.Editor editor = prefs.edit();
                        //editor.putInt("lastOpenedProject", -1);
                        //editor.commit();
                        //save = false;
                        //finish();
                    }
                });
                photo_dialog.show();
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
        if (index == 0) {
            Log.i("SetupsFragment","No matching item found in list");
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

            //TODO: Also display distance to said point. (So far only Log.i)
            //TODO: Perhaps do this all in a dialog so the user can choose to abort?

        }else{
            //TODO: Message that location has not been found yet... beg for patience! :D
            int duration = Toast.LENGTH_SHORT;
            String toast_message = "Location not yet available.";
            Toast toast = Toast.makeText(getContext(),toast_message, duration);
            toast.show();
        }
    }

    private void useCameraButton(){
        // Check permissions
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            // Check system feature
            if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Camera code:
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e("SetupsFragment","Error occurred while creating the File");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getContext(),
                                //"com.example.android.fileprovider",
                                "ref.sdfe.gpslogsheet2.fileprovider",
                                photoFile);
                        //takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                    //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("SetupsFragment","Resultcode :" + resultCode);
        Log.i("SetupsFragment","Supposed to be :" + RESULT_OK);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //mImageView.setImageBitmap(imageBitmap);
            photos.add(imageBitmap);
            photoTexts.add("New Image");
            Log.i("SetupsFragment","Added photo.");
            ((ArrayAdapter) photoList.getAdapter()).notifyDataSetChanged();
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        String imageFileName = "gpslogsheet2_" + timeStamp + "_";
        Log.i("SetupsFragment, file",imageFileName);

        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(getContext().getExternalFilesDir(DIRECTORY_PICTURES),"");
        //File storageDir = new File(getContext().getFilesDir(), "images");
        //File storageDir = new File(getContext().getDir(images,1));

        Log.i("SetupsFragment, dir",storageDir.toString());
        File image = new File(storageDir,imageFileName);


//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );

        // Save a file: path for use with ACTION_VIEW intents
        Log.i("SetupsFragment","HUH!");
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("SetupsFragment",mCurrentPhotoPath);
        return image;
    }
}