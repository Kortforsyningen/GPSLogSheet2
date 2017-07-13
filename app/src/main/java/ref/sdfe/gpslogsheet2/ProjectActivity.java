package ref.sdfe.gpslogsheet2;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;

import java.util.Collections;
import java.util.Date;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class ProjectActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    public static Integer lastOpenedProject;
    public List<ProjectEntry> projectsList;
    public Context mContext;
    public static List<Integer> projectsListIDs;
    public static List<String> projectsListStrings;

    private DataBaseHandler db;
    private EditText projectNameField;
    private EditText operatorNameField;

    static public ProjectEntry project;
    static public ProjectEntry project_backup; // so that changes can be discarded.
    static public Boolean save = false; // should the project be saved
    static public Boolean projectNameError = true; // Is project name invalid

    // Local variables for the text fields
    private static String current_projectName;
    private static String current_projectOperator;
    private static String current_projectDate;
    private static String current_projectModDate;

    private static Boolean locationPermitted;


    private static InputFilter nameFilter;
    private static InputFilter numberFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Get application context
        mContext = getApplicationContext();

        //Get shared preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        //Create editor
        final SharedPreferences.Editor editor = prefs.edit();

        // Get readable database in order to get a list of projects
        db = DataBaseHandler.getInstance(getApplicationContext());

        //Get list of projects
        List<ProjectEntry> projectsList = db.getAllProjectEntries();
        projectsListIDs = new ArrayList<>();
        projectsListStrings = new ArrayList<>();
        // get project ID's and name
        for (int i = 0; i < db.getProjectsCount(); i++) {
            projectsListIDs.add(projectsList.get(i).getId());
            projectsListStrings.add(projectsList.get(i).getName());
        }

        //Check for open/selected project
        lastOpenedProject = prefs.getInt("lastOpenedProject", 0);
        if (lastOpenedProject > 0){
            project = db.getProjectEntry(lastOpenedProject);
            projectNameError = false;
            try{
                project_backup = (ProjectEntry) project.clone();
            }catch(CloneNotSupportedException e){
                Log.i("ProjectActivity","Cloning not supported :/");
            }

        }else{
            //generate new id number for project.
            Integer id = 0;
            try {
                id = Collections.max(projectsListIDs) + 1;
            }catch(NoSuchElementException e){
                id = 1;
            }


            project = new ProjectEntry(id);
            try{
                project_backup = (ProjectEntry) project.clone();
            }catch(CloneNotSupportedException e){
                Log.i("ProjectActivity","Cloning not supported :/");
            }
            project.setName("");
            projectNameError = true;
        }

        // Load project data into local variables
        current_projectName = project.getName();
        Log.i("ProjectActivity",current_projectName);
        current_projectOperator = project.getOperator();
        //Log.i("ProjectActivity",current_projectOperator);

        // Get project dates and convert them to strings
        long start_date_long = project.getStartDate();
        Date start_date = new Date(start_date_long);
        current_projectDate = start_date.toString();
        long mod_date_long = project.getModDate();
        Date mod_date = new Date(mod_date_long);
        current_projectModDate = mod_date.toString();

        //Setups
        HashMap<Integer,ProjectEntry.Setup> setups = project.getSetups();
        try {
            if (setups.isEmpty()) {
                Log.i("Setups", "There are no setups.");
            } else {
                Log.i("Setups", "There are setups.");
            }
        }catch(NullPointerException e){
            Log.i("Setups", "There are no setups.");
        }

        //LOCATION

        // Check for permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermitted = false;
            Log.i("ProjectActivity","Location not permitted.");
        }else{
            locationPermitted = true;
            Log.i("ProjectActivity","Location permitted.");
        }


        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                // TODO: save location into local variable.
                //makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        String locationProvider = LocationManager.GPS_PROVIDER;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //toolbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // EMAIL BUTTON
        FloatingActionButton fab_email = (FloatingActionButton) findViewById(R.id.fab_email);
        fab_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "TODO: Email current project.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // TODO: Email
                // Make this fab button appear only when project has an observation
                // Generate .batch files
                // send email with relevant files.
            }
        });

        // CLOSE BUTTON
        FloatingActionButton fab_close = (FloatingActionButton) findViewById(R.id.fab_close);
        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (projectNameError){
                    // Project has invalid name
                    Log.i("ProjectActivity","back pressed! Name Error!");
                    final Dialog dialog = new Dialog(ProjectActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = inflater.inflate(R.layout.discard_dialog, new LinearLayoutCompat(mContext), false);
                    dialog.setContentView(convertView);
                    dialog.setCancelable(true);
                    dialog.setTitle("Project name error, do you want to Discard changes?");

                    Button cancel_button = (Button)dialog.findViewById(R.id.cancel_button);
                    Button discard_button = (Button)dialog.findViewById(R.id.discard_button);

                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // CANCEL CODE HERE
                            // Close activity
                            dialog.dismiss();
                            //Move focus to projectNameField
                            //projectNameField.requestFocus();
                        }
                    });

                    discard_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // DISCARD CODE HERE

                            // read backup project.
                            if (!project_backup.getName().isEmpty()){
                                project = project_backup;
                                save = true;
                            }else{
                                save = false;
                            }

                            editor.apply();

                            // Close activity
                            dialog.dismiss();
                            finish();
                        }
                    });

                    dialog.show();

                }else {

                    //Snackbar.make(view, "TODO: Save and close project.", Snackbar.LENGTH_LONG)
                    //        .setAction("Action", null).show();

                    final Dialog dialog = new Dialog(ProjectActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = inflater.inflate(R.layout.close_dialog, new LinearLayoutCompat(mContext), false);
                    dialog.setContentView(convertView);
                    dialog.setCancelable(true);
                    dialog.setTitle("Close Project: Save or Discard changes?");

                    Button save_button = (Button) dialog.findViewById(R.id.save_button);
                    Button discard_button = (Button) dialog.findViewById(R.id.discard_button);

                    save_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // SAVE CODE HERE

                            // Clear lastOpenedProject from sharedprefs
                            save = true;
                            editor.putInt("lastOpenedProject", 0);
                            editor.apply();

                            // Close activity
                            dialog.dismiss();
                            finish();
                        }
                    });

                    discard_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // DISCARD CODE HERE

                            // read backup project.
                            save = true;
                            project = project_backup;


                            // Clear lastOpenedProject from sharedprefs
                            editor.putInt("lastOpenedProject", 0);
                            editor.apply();

                            // Close activity
                            dialog.dismiss();
                            finish();
                        }
                    });

                    dialog.show();
                }
            }
        });

        // Input filters for the text fields
        nameFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                if (source instanceof SpannableStringBuilder) {
                    SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder) source;
                    for (int i = end - 1; i >= start; i--) {
                        char currentChar = source.charAt(i);
                        if (!Character.isLetterOrDigit(currentChar) &&
                                !(currentChar == '_') &&
                                !(currentChar == '-')) {
                            sourceAsSpannableBuilder.delete(i, i + 1);
                        }
                    }
                    return source;
                } else {
                    StringBuilder filteredStringBuilder = new StringBuilder();
                    for (int i = start; i < end; i++) {
                        char currentChar = source.charAt(i);
                        if (Character.isLetterOrDigit(currentChar) ||
                                (currentChar == '_') ||
                                (currentChar == '-')) {
                            filteredStringBuilder.append(currentChar);
                        }
                    }
                    return filteredStringBuilder.toString();
                }
            }
        };
        numberFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                if (source instanceof SpannableStringBuilder) {
                    SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder) source;
                    for (int i = end - 1; i >= start; i--) {
                        char currentChar = source.charAt(i);
                        if (!Character.isDigit(currentChar) &&
                                !(currentChar == '.')) {
                            sourceAsSpannableBuilder.delete(i, i + 1);
                        }
                    }
                    return source;
                } else {
                    StringBuilder filteredStringBuilder = new StringBuilder();
                    for (int i = start; i < end; i++) {
                        char currentChar = source.charAt(i);
                        if (Character.isDigit(currentChar) ||
                                (currentChar == '.')) {
                            filteredStringBuilder.append(currentChar);
                        }
                    }
                    return filteredStringBuilder.toString();
                }
            }
        };

    }

    @Override
    public void onBackPressed() {
        Log.i("ProjectActivity", "back pressed!");
        Log.i("ProjectActivity", "projectNameError = " + projectNameError.toString());
        final SharedPreferences.Editor editor = prefs.edit();

        // TODO: This sometimes does not work.
        if (project_backup.getModDate() == project.getModDate()){
            // No changes detected
            Log.i("ProjectActivity", "No changes detected.");
            finish();
        } else if (projectNameError) {
            // Project has invalid name
            Log.i("ProjectActivity", "back pressed! Name Error!");
            final Dialog dialog = new Dialog(ProjectActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.discard_dialog, new LinearLayoutCompat(mContext), false);
            dialog.setContentView(convertView);
            dialog.setCancelable(true);
            dialog.setTitle("Project name error, do you want to Discard changes?");

            Button cancel_button = (Button) dialog.findViewById(R.id.cancel_button);
            Button discard_button = (Button) dialog.findViewById(R.id.discard_button);

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // CANCEL CODE HERE
                    // Close activity
                    dialog.dismiss();
                    //Move focus to projectNameField

                    //TODO: Move focus to project name field, the below does not work.
                    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    //projectNameField.requestFocus();
                }
            });

            discard_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // DISCARD CODE HERE
                    // read backup project.
                    save = false;
                    dialog.dismiss();
                    finish();
                }
            });

            dialog.show();

        }else {
            Log.i("ProjectActivity", "back pressed! Dialog coming up!");
            final Dialog dialog = new Dialog(ProjectActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.close_dialog, new LinearLayoutCompat(mContext), false);
            dialog.setContentView(convertView);
            dialog.setCancelable(true);
            dialog.setTitle("Save or Discard changes?");

            Button save_button = (Button) dialog.findViewById(R.id.save_button);
            Button discard_button = (Button) dialog.findViewById(R.id.discard_button);

            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // SAVE CODE HERE
                    save = true;

                    // Close activity
                    dialog.dismiss();
                    finish();
                }
            });

            discard_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // DISCARD CODE HERE

                    // read backup project.
                    save = true;
                    project = project_backup;

                    // Close activity
                    dialog.dismiss();
                    finish();
                }
            });

            dialog.show();
        }
    }


@Override
protected void onDestroy(){
        super.onDestroy();

        //TODO: save project to database
        if ( save ) {
            // Get application context
            mContext = getApplicationContext();

            // Get readable database in order to get a list of projects
            //db = DataBaseHandler.getInstance(mContext);

            //If project ID already in database
            if ( db.getAllProjectIDs().contains(project.getId())){
                db.updateProjectEntry(project);
            }else {
                // else it is a new project
                if (db.getAllProjectNames().contains(project.getName())){
                    Log.i("SQL", "Tried to save new project with non-unique name");
                }else{
                    db.addProjectEntry(project);
                }
            }
        }
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i("Toolbar","Options pressed");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_delete) {
            Log.i("Toolbar","Delete pressed");
            //TODO: create popup with warning to delete

            final Dialog delete_dialog = new Dialog(ProjectActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.delete_dialog, new LinearLayoutCompat(mContext), false);
            delete_dialog.setContentView(convertView);
            delete_dialog.setCancelable(true);
            delete_dialog.setTitle("WARNING!");

            Button cancel_button = (Button)delete_dialog.findViewById(R.id.cancel_button);
            final Button delete_button = (Button)delete_dialog.findViewById(R.id.delete_button);

            //Switch that toggles delete button
            Switch editSwitch = (Switch)delete_dialog.findViewById(R.id.delete_switch);
            editSwitch.setChecked(false);
            editSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        delete_button.setEnabled(true);
                        Log.i("ProjectActivity","Delete switch on :O !");
                    } else {
                        delete_button.setEnabled(false);
                        Log.i("ProjectActivity","Delete switch off!");
                    }
                }
            });

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_dialog.dismiss();
                }
            });
            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // DELETE CODE HERE
                    Log.i("ProjectActivity","TODO: DELETION");
                    db.deleteProject(project);
                    delete_dialog.dismiss();
                    final SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("lastOpenedProject", -1);
                    editor.commit();
                    save = false;
                    finish();
                }
            });

            delete_dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // OLDJO: A Project setting fragment
    public static class ProjectSettingsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "1";

        public ProjectSettingsFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProjectSettingsFragment newInstance(int sectionNumber) {
            ProjectSettingsFragment fragment = new ProjectSettingsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // OLDJO: Inflate the layout
            View rootView = inflater.inflate(R.layout.fragment_project, container, false);

            //Make this listen for changes in floating action button
            FloatingActionButton fcb = (FloatingActionButton) rootView.findViewById(R.id.floatingCameraButton);
            fcb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Snackbar.make(view, "TODO: Camera Activity or something. Also placement.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                }
            });

            // Text fields
            final EditText projectNameField = (EditText) rootView.findViewById(R.id.editProjectName);
            projectNameField.setText(current_projectName);
            if (current_projectName.isEmpty()){
                projectNameField.setError("Please enter a project name");
                projectNameError = true;
            }
            projectNameField.setFilters(new InputFilter[] {nameFilter });
            projectNameField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    project.setModDate();
                }
                @Override
                public void afterTextChanged(Editable s) {
//                    //Make sure the field is not left empty. This worked hilariously wrong.
//                    if (projectNameField.getText().toString().isEmpty()){
//                        projectNameField.setText("New_Project");
//                    }
//                    if(TextUtils.isEmpty(projectNameField.getText().toString())) {
//                        projectNameField.setError("Project name cannot be empty.");
//                        projectNameError = true;
//                    }

                    //TODO: make uniqueness test here!
                    // If the project list contain the name, but not the current project, e.g. non-unique.
                    if (projectsListStrings.contains(projectNameField.getText().toString())) {
                        if (lastOpenedProject > 0) {
                            //int pos = projectsListIDs.
                            if (!projectsListIDs.get(projectsListStrings.indexOf(projectNameField.getText().toString())).equals(lastOpenedProject)){
                            //if (!projectNameField.getText().toString().toLowerCase().equals(current_projectName.toLowerCase())) {
                                projectNameField.setError("Project name must be unique.");
                                projectNameError = true;
                            }
                        }else{
                            projectNameField.setError("Project name must be unique.");
                            projectNameError = true;
                        }
                        projectNameError = false;
                    }else if (projectNameField.getText().toString().isEmpty()) {
                        projectNameField.setError("Project name cannot be empty.");
                        projectNameError = true;
                    }else{
                        projectNameError = false;
                    }

                    project.setName(projectNameField.getText().toString());
                    current_projectName = projectNameField.getText().toString();
                    Log.i("ProjectSettings", "Project Name Set to " + projectNameField.getText().toString());
                    Log.i("ProjectEntry", "Project Name really IS " + project.getName());
                }
            });
            projectNameField.setFocusable(false);

            final EditText operatorNameField = (EditText) rootView.findViewById(R.id.editOperator);
            operatorNameField.setText(current_projectOperator);
            operatorNameField.setFilters(new InputFilter[] {nameFilter });
            operatorNameField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    project.setModDate();
                }
                @Override
                public void afterTextChanged(Editable s) {
                    //TODO: make uniqueness test here!
                    //if (~(projectNameField.getText()
                    project.setOperator(operatorNameField.getText().toString());
                    current_projectOperator = operatorNameField.getText().toString();
                    Log.i("ProjectSettings", "Operator Name Set to " + operatorNameField.getText().toString());
                    Log.i("ProjectEntry", "Operator Name really IS " + project.getOperator());
                }
            });
            operatorNameField.setFocusable(false);

            //Switch that toggles editing
            Switch editSwitch = (Switch) rootView.findViewById(R.id.edit_switch);
            editSwitch.setChecked(false);
            editSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        projectNameField.setFocusableInTouchMode(true);
                        operatorNameField.setFocusableInTouchMode(true);
                        Log.i("ProjectSettings","switch on!");
                    } else {
                        operatorNameField.setFocusable(false);
                        projectNameField.setFocusable(false);
                        Log.i("ProjectSettings","switch off!");
                    }
                }
            });

            //If new project start with edit on, else edit off.
            if (current_projectName.isEmpty()){
                editSwitch.setChecked(true);
                projectNameField.requestFocus();
            }else{
                editSwitch.setChecked(false);
            }

            return rootView;
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ObservationsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "2";

        public ObservationsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ObservationsFragment newInstance(int sectionNumber) {
            ObservationsFragment fragment = new ObservationsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_observations, container, false);
        }
    }
    /**
     * MAPS fragment
     */
    public static class MapsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "3";

        public MapsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MapsFragment newInstance(int sectionNumber) {
            MapsFragment fragment = new MapsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_maps, container, false);
        }
    }    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ExtrasFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "4";

        public ExtrasFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ExtrasFragment newInstance(int sectionNumber) {
            ExtrasFragment fragment = new ExtrasFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_extras, container, false);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return ProjectSettingsFragment.newInstance(position + 1);
                case 1:
                    return ObservationsFragment.newInstance(position + 1);
                case 2:
                    return MapsFragment.newInstance(position + 1);
                case 3:
                    return ExtrasFragment.newInstance(position + 1);
                }
            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        //OLDJO The following controls what the tabs are called.
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Project Settings";
                case 1:
                    return "Observations";
                case 2:
                    return "Map";
                case 3:
                    return "Extras";
            }
            return null;
        }
    }
}

