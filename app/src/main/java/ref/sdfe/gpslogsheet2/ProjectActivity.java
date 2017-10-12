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
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;

import java.util.Collections;
import java.util.Comparator;
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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.StreamHandler;
import java.util.zip.Inflater;

import static android.R.layout.simple_list_item_1;

public class ProjectActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    public static Integer lastOpenedProject;
    //public List<ProjectEntry> projectsList;
    public Context mContext;
    public static List<Integer> projectsListIDs;
    public static List<String> projectsListStrings;

    private DataBaseHandler db;
    //private EditText projectNameField;
    //private EditText operatorNameField;

    static public ProjectEntry project;
    static public ProjectEntry project_backup; // so that changes can be discarded.
    static public Boolean save = false; // should the project be saved
    static public Boolean projectNameError = true; // Is project name invalid

    // Local variables for the text fields
    private static String current_projectName;
    private static String current_projectOperator;
    private static String current_projectDate;
    private static String current_projectModDate;
    private static String current_projectEndDate;

    private static String projectTextTemplate;

    public static Boolean locationPermitted;
    public static LocationManager locationManager;


    private static InputFilter nameFilter;
    private static InputFilter numberFilter;

    // Setups
    public static String current_setup;
    public ArrayAdapter setupsAdapter;
    public List<Integer> setupsListIDs;
    public List<String> setupsListStrings;
    public HashMap<Integer, ProjectEntry.Setup> setupsList;

    // Lists
    static public List<Double> eastings = new ArrayList<>();
    static public List<Double> northings = new ArrayList<>();
    static public List<String> gpsNames = new ArrayList<>();
    static public List<String> hsNames = new ArrayList<>();
    static public List<String> antennaNames = new ArrayList<>();
    static public List<String> alarmNames = new ArrayList<>();
    static public List<String> instrumentNames = new ArrayList<>();

    static public List<FixedpointEntry> fixedpointEntries;
    static public List<AlarmEntry> alarmEntries;
    static public List<AntennaEntry> antennaEntries;
    static public List<InstrumentEntry> instrumentEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Get application context
        mContext = getApplicationContext();

        projectTextTemplate = getResources().getString(R.string.projectTextTemplate);

        //Get shared preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        //Create editor
        final SharedPreferences.Editor editor = prefs.edit();

        // Get readable database in order to get a list of projects
        db = DataBaseHandler.getInstance(getApplicationContext());
        getData();

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
        if (lastOpenedProject > 0) {
            project = db.getProjectEntry(lastOpenedProject);
            Log.i("ProjectActivity", "Project loaded, id: " + lastOpenedProject.toString());
            projectNameError = false;

            try {
                project_backup = (ProjectEntry) project.clone();
            } catch (CloneNotSupportedException e) {
                Log.i("ProjectActivity", "Cloning not supported :/");
            }

        } else {
            //generate new id number for project.
            Integer id = 0;
            try {
                id = Collections.max(projectsListIDs) + 1;
            } catch (NoSuchElementException e) {
                id = 1;
            }


            project = new ProjectEntry(id);
            Log.i("ProjectActivity", "New Project, id: " + id.toString());
            try {
                project_backup = (ProjectEntry) project.clone();
            } catch (CloneNotSupportedException e) {
                Log.i("ProjectActivity", "Cloning not supported :/");
            }
            project.setName("");
            projectNameError = true;
        }

        // Load project data into local variables
        current_projectName = project.getName();
        Log.i("ProjectActivity", current_projectName);
        current_projectOperator = project.getOperator();

        // Get project dates and convert them to strings
        long start_date_long = project.getStartDate();
        Date start_date = new Date(start_date_long);
        current_projectDate = start_date.toString();
        long mod_date_long = project.getModDate();
        Date mod_date = new Date(mod_date_long);
        current_projectModDate = mod_date.toString();

        //Setups
//        HashMap<Integer,ProjectEntry.Setup> setups = project.getSetups();
//        try {
//            if (setups.isEmpty()) {
//                Log.i("Setups", "There are no setups.");
//            } else {
//                Log.i("Setups", "There are setups.");
//            }
//        }catch(NullPointerException e){
//            Log.i("Setups", "There are no setups 2.");
//            project.addSetup(1);
//
//        }

        setupsListIDs = new ArrayList<>();
        setupsListStrings = new ArrayList<>();

        populateSetupsList();

        //If there are no setups, add an empty one
        if (setupsListIDs.isEmpty()) {
            project.addSetup(0);
            populateSetupsList();
        }


        setupsAdapter = new ArrayAdapter<>(this, simple_list_item_1, setupsListStrings);


        //LOCATION

        // Check for permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermitted = false;
            Log.i("ProjectActivity", "Location not permitted.");
        } else {
            locationPermitted = true;
            Log.i("ProjectActivity", "Location permitted.");
        }

        //Start location service "LocationHandler"
        mContext.startService(new Intent(mContext, LocationHandler.class));

        // Define a listener that responds to location updates
//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                // Called when a new location is found by the network location provider.
//                // TODO: save location into local variable.
//                //makeUseOfNewLocation(location);
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//            }
//
//            public void onProviderEnabled(String provider) {
//            }
//
//            public void onProviderDisabled(String provider) {
//            }
//        };
//        String locationProvider = LocationManager.GPS_PROVIDER;

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

                if (projectNameError) {
                    // Project has invalid name
                    Log.i("ProjectActivity", "back pressed! Name Error!");
                    final Dialog dialog = new Dialog(ProjectActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = inflater.inflate(R.layout.discard_dialog, new LinearLayoutCompat(mContext), false);
                    dialog.setContentView(convertView);
                    dialog.setCancelable(true);
                    dialog.setTitle(R.string.invalid_name_query);

                    Button cancel_button = (Button) dialog.findViewById(R.id.cancel_button);
                    Button discard_button = (Button) dialog.findViewById(R.id.discard_button);

                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // CANCEL CODE HERE
                            dialog.dismiss();
                        }
                    });

                    discard_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // DISCARD CODE HERE
                            save = false;
                            // read backup project.
//                            if (!project_backup.getName().isEmpty()){
//                                project = project_backup;
//                                save = true;
//                            }else{
//                                save = false;
//                            }

                            editor.apply();

                            // Close activity
                            dialog.dismiss();
                            finish();
                        }
                    });

                    dialog.show();

                } else {

                    final Dialog dialog = new Dialog(ProjectActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = inflater.inflate(R.layout.close_dialog, new LinearLayoutCompat(mContext), false);
                    dialog.setContentView(convertView);
                    dialog.setCancelable(true);
                    dialog.setTitle(R.string.close_project_title);

                    Button save_button = (Button) dialog.findViewById(R.id.save_button);
                    Button discard_button = (Button) dialog.findViewById(R.id.discard_button);

                    save_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // SAVE CODE HERE
                            save = true;

                            // Clear lastOpenedProject from sharedprefs
                            editor.putInt("lastOpenedProject", -1);
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
                            save = false;

                            // Clear lastOpenedProject from sharedprefs
                            editor.putInt("lastOpenedProject", -1);
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

    // TODO: setups list, and adapter to display them in the project activity.
    public void populateSetupsList() {
        setupsListIDs.clear();
        setupsListStrings.clear();
        setupsList = project.getSetups();

        for (Map.Entry<Integer, ProjectEntry.Setup> entry : setupsList.entrySet()) {
            Integer id = entry.getKey();
            setupsListIDs.add(id);
            setupsListStrings.add(entry.getValue().getFixedPoint());
        }


        try {
            setupsAdapter.notifyDataSetChanged();
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onBackPressed() {
        Log.i("ProjectActivity", "back pressed!");
        Log.i("ProjectActivity", "projectNameError = " + projectNameError.toString());
        final SharedPreferences.Editor editor = prefs.edit();

        if (project_backup.getModDate() == project.getModDate()) {
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

        } else {
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
                    save = false;

                    // Close activity
                    dialog.dismiss();
                    finish();
                }
            });

            dialog.show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //TODO: save project to database
        if (save) {
            // Get application context
            mContext = getApplicationContext();

            //Log.i("ProjectActivity", "Project JSON: " + project.getJsonString());

            // Get readable database in order to get a list of projects
            //db = DataBaseHandler.getInstance(mContext);

            //If project ID already in database
            if (db.getAllProjectIDs().contains(project.getId())) {
                db.updateProjectEntry(project);
            } else {
                // else it is a new project
                if (db.getAllProjectNames().contains(project.getName())) {
                    Log.i("SQL", "Tried to save new project with non-unique name");
                } else {
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
            Log.i("Toolbar", "Options pressed");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_delete) {
            Log.i("Toolbar", "Delete pressed");
            //TODO: create popup with warning to delete

            final Dialog delete_dialog = new Dialog(ProjectActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.delete_dialog, new LinearLayoutCompat(mContext), false);
            delete_dialog.setContentView(convertView);
            delete_dialog.setCancelable(true);
            delete_dialog.setTitle("WARNING!");

            Button cancel_button = (Button) delete_dialog.findViewById(R.id.cancel_button);
            final Button delete_button = (Button) delete_dialog.findViewById(R.id.delete_button);

            //Switch that toggles delete button
            Switch editSwitch = (Switch) delete_dialog.findViewById(R.id.delete_switch);
            editSwitch.setChecked(false);
            editSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        delete_button.setEnabled(true);
                        Log.i("ProjectActivity", "Delete switch on :O !");
                    } else {
                        delete_button.setEnabled(false);
                        Log.i("ProjectActivity", "Delete switch off!");
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
                    Log.i("ProjectActivity", "TODO: DELETION");
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
        private FragmentTabHost setupsTabHost;
        private List<Fragment> setupsFragmentsList;
        private View rootView;

        public ProjectSettingsFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProjectSettingsFragment newInstance(int sectionNumber) {
            Log.i("ProjectSettingsFragment", "newInstance");
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
            Log.i("ProjectSettingsFragment", "onCreateView");
            rootView = inflater.inflate(R.layout.fragment_project, container, false);

            //Make this listen for changes in floating action button
            FloatingActionButton fcb = (FloatingActionButton) rootView.findViewById(R.id.floatingCameraButton);
            fcb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "TODO: Camera Activity or something. Also placement.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            // Input Text fields
            final EditText projectNameField = (EditText) rootView.findViewById(R.id.editProjectName);
            projectNameField.setText(current_projectName);
            if (current_projectName.isEmpty()) {
                projectNameField.setError("Please enter a project name");
                projectNameError = true;
            }
            projectNameField.setFilters(new InputFilter[]{nameFilter});
            projectNameField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //setModDate should perhaps be private to ProjectEntry
                    //project.setModDate();

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // If the project list contain the name, but not the current project, e.g. non-unique.
                    if (projectsListStrings.contains(projectNameField.getText().toString())) {
                        if (lastOpenedProject > 0) {
                            //int pos = projectsListIDs.
                            if (!projectsListIDs.get(projectsListStrings.indexOf(projectNameField.getText().toString())).equals(lastOpenedProject)) {
                                //if (!projectNameField.getText().toString().toLowerCase().equals(current_projectName.toLowerCase())) {
                                projectNameField.setError("Project name must be unique.");
                                projectNameError = true;
                            }
                        } else {
                            projectNameField.setError("Project name must be unique.");
                            projectNameError = true;
                        }

                        //projectNameError = false;
                    } else if (projectNameField.getText().toString().isEmpty()) {
                        projectNameField.setError("Project name cannot be empty.");
                        projectNameError = true;
                    } else {
                        projectNameError = false;
                    }
                    if (projectNameField.getText().toString().equals(current_projectName)) {

                    } else {
                        project.setName(projectNameField.getText().toString());
                        current_projectName = projectNameField.getText().toString();
                    }

                }
            });
            projectNameField.setFocusable(false);

            final EditText operatorNameField = (EditText) rootView.findViewById(R.id.editOperator);
            operatorNameField.setText(current_projectOperator);
            operatorNameField.setFilters(new InputFilter[]{nameFilter});
            operatorNameField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //project.setModDate();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (operatorNameField.getText().toString().equals(current_projectOperator)) {

                    } else {
                        project.setOperator(operatorNameField.getText().toString());
                        current_projectOperator = operatorNameField.getText().toString();

                    }

                }
            });
            operatorNameField.setFocusable(false);

            Button addSetupButton = (Button) rootView.findViewById(R.id.add_tab);
            addSetupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // LOCADE CODE HERE
                    Log.i("ProjectSettingsFragment", "Add Setup Button pressed!");
                    addSetupTab();
                }
            });

            //Switch that toggles editing
            Switch editSwitch = (Switch) rootView.findViewById(R.id.edit_switch);
            editSwitch.setChecked(false);
            editSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        projectNameField.setFocusableInTouchMode(true);
                        operatorNameField.setFocusableInTouchMode(true);
                        Log.i("ProjectSettings", "switch on!");
                    } else {
                        operatorNameField.setFocusable(false);
                        projectNameField.setFocusable(false);
                        Log.i("ProjectSettings", "switch off!");
                    }
                }
            });

            //If new project start with edit on, else edit off.
            if (current_projectName.isEmpty()) {
                editSwitch.setChecked(true);
                projectNameField.requestFocus();
            } else {
                editSwitch.setChecked(false);
            }

            // Text fields

            final TextView projectTextView = (TextView) rootView.findViewById(R.id.projectTextView);

            // Get project end date or display not set if the value is null.
            if (Long.valueOf(project.getEndDate()).toString().equals("0")) {
                current_projectEndDate = "Not set.";
            } else {
                current_projectEndDate = DateFormat.format("dd/MM/yyyy", new Date(project.getEndDate())).toString();
            }
            current_projectModDate = DateFormat.format("dd/MM/yyyy", new Date(project.getModDate())).toString();
            current_projectDate = DateFormat.format("dd/MM/yyyy", new Date(project.getStartDate())).toString();

            projectTextView.setText(String.format(projectTextTemplate,
                    current_projectDate, current_projectModDate, current_projectEndDate));

            initSetupTabHost(rootView);
            return rootView;
        }

        // Tabs with setups
        private void initSetupTabHost(View view) {
            // Setups
            setupsTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
            setupsTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
            setupsTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    //Set current setup
                    current_setup = tabId;
                    Log.i("ProjectActivity", "Current setup: " + tabId);
                }

            });

            populateSetupTabHost();
        }

        private void populateSetupTabHost() {
            //Are there any setups
            if (project.getSetupsCount() > 0) {
                HashMap<Integer, ProjectEntry.Setup> setups = project.getSetups();
                List<ProjectEntry.Setup> setupsSorted = new ArrayList<>(setups.values());

                // Sort
                Collections.sort(setupsSorted, new Comparator<ProjectEntry.Setup>() {
                    @Override
                    public int compare(ProjectEntry.Setup o1, ProjectEntry.Setup o2) {
                        return o1.getId() - o2.getId();
                    }
                });

                for (ProjectEntry.Setup s : setupsSorted){
                    Integer id = s.getId();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Id", id);
                    setupsTabHost.addTab(setupsTabHost.newTabSpec(id.toString()).setIndicator(id.toString()), SetupsFragment.class, bundle);
                }
            }
        }

        // Add empty setup
        private void addSetupTab() {
            //Get the setups from the project.
            HashMap<Integer, ProjectEntry.Setup> setups = project.getSetups();
            // Find maximum ID and add one
            Integer id = Collections.max(setups.keySet()) + 1;
            project.addSetup(id);
            Bundle bundle = new Bundle();
            bundle.putInt("Id", id);
            setupsTabHost.addTab(setupsTabHost.newTabSpec(id.toString()).setIndicator(id.toString()), SetupsFragment.class, bundle);
            setupsTabHost.setCurrentTab(id);
        }

        public void refreshSetupTab() {
            setupsTabHost.clearAllTabs();
            populateSetupTabHost();
        }

    }


    private void getData() {

        //Clear old values
        try {
            gpsNames.clear();
            hsNames.clear();
            eastings.clear();
            northings.clear();
            alarmNames.clear();
            antennaNames.clear();
            instrumentNames.clear();
        } catch (NullPointerException e) {
            Log.i("ProjectActivity","getData(): Empty lists");
        }

        //Get readable database in order to get a list of projects
        //db = DataBaseHandler.getInstance(getApplicationContext());
        fixedpointEntries = db.getAllFixedpointEntries();
        alarmEntries = db.getAllAlarmEntries();
        antennaEntries = db.getAllAntennaEntries();
        instrumentEntries = db.getAllInstrumentEntries();

        //Add dummies so spinner can show empty
        fixedpointEntries.add(new FixedpointEntry(-1,"","",0,0));
        alarmEntries.add(new AlarmEntry(-1,""));
        antennaEntries.add(new AntennaEntry(-1,"",""));
        instrumentEntries.add(new InstrumentEntry(-1,""));

        //Sorting
        Collections.sort(fixedpointEntries, new Comparator<FixedpointEntry>() {
            @Override
            public int compare(FixedpointEntry o1, FixedpointEntry o2) {
                return o1.getGPSName().compareTo(o2.getGPSName());
            }
        });
        Collections.sort(alarmEntries, new Comparator<AlarmEntry>() {
            @Override
            public int compare(AlarmEntry o1, AlarmEntry o2) {
                return o1.alarm_id - o2.alarm_id;
            }
        });
        Collections.sort(antennaEntries, new Comparator<AntennaEntry>() {
            @Override
            public int compare(AntennaEntry o1, AntennaEntry o2) {
                return o1.antenna_id - o2.antenna_id;
            }
        });
        Collections.sort(instrumentEntries, new Comparator<InstrumentEntry>() {
            @Override
            public int compare(InstrumentEntry o1, InstrumentEntry o2) {
                return o1.instrument_id - o2.instrument_id;
            }
        });

        //Fixedpoints
        for (int i = 0; i < db.getFixedpointsCount(); i++) {
            gpsNames.add(i, fixedpointEntries.get(i).getGPSName());
            hsNames.add(i, fixedpointEntries.get(i).getHSName());
            eastings.add(i, fixedpointEntries.get(i).getEasting());
            northings.add(i, fixedpointEntries.get(i).getNorthing());
        }

        for (int i = 0; i < db.getAlarmsCount(); i++) {
            alarmNames.add(i, alarmEntries.get(i).getName());
        }
        for (int i = 0; i < db.getAntennaeCount(); i++) {
            antennaNames.add(i, antennaEntries.get(i).getName());
        }
        for (int i = 0; i < db.getInstrumentsCount(); i++) {
            instrumentNames.add(i, instrumentEntries.get(i).getName());
        }
        Log.i("SetupsFragment", "Loaded " + instrumentNames.size() + " instruments, "
                + alarmNames.size() + " alarms, and "
                + gpsNames.size() + " fixedpoints");
    }

}