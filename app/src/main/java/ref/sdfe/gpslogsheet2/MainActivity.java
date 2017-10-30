package ref.sdfe.gpslogsheet2;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.layout.simple_list_item_1;

public class MainActivity extends AppCompatActivity {
    public Integer lastOpenedProject;
    public Context mContext;
    public Dialog dialog;
    public Integer MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    public Integer MY_PERMISSIONS_REQUEST_CAMERA = 200;
    public DataBaseHandler db;
    public ArrayAdapter adapter;
    public ListView listview;
    public SharedPreferences.Editor editor;
    public SharedPreferences prefs;
    public View convertView;

    //Projects
    private Integer allowProject;
    private List<Integer> projectsListIDs;
    private List<String> projectsListStrings;

    //Fixedpoints
    private List<FixedpointEntry> fixedpoints;
    private List<String> gpsNames;
    private List<String> hsNames;
    private List<Double> x;
    private List<Double> y;

    //Instruments

    //Rods

    //Antennae

    //Alarms


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context mContext;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        // Get shared preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        lastOpenedProject = prefs.getInt("lastOpenedProject", 0);
        allowProject = prefs.getInt("allowProject", 0);
        editor = prefs.edit();

        //If the there has been an update ever.
        if (prefs.getInt("lastUpdateDate",0) > 0){
            populateLists();
            allowProject = 1;
            editor.putInt("allowProject", 1);
        }



        // PERMISSIONS!
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }




        //Create list of projects
        //TODO: Medium/low priority: Find a way to show the list in reverse chronological order, alphabetical etc.

        final Intent intent = new Intent(this, ProjectActivity.class);

        dialog = new Dialog(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        convertView = inflater.inflate(R.layout.load_dialog, new LinearLayoutCompat(mContext), false);
        dialog.setContentView(convertView);
        dialog.setCancelable(true);
        dialog.setTitle("Choose Project:");

        projectsListIDs = new ArrayList<>();
        projectsListStrings = new ArrayList<>();

        populateProjectList();

        adapter = new ArrayAdapter<>(this, simple_list_item_1, projectsListStrings);
        listview = (ListView) convertView.findViewById(R.id.LoadList);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                editor.putInt("lastOpenedProject",projectsListIDs.get(position));
                editor.apply();
                startActivity(intent);
                dialog.dismiss();
            }
        });

        // If some project is already opened, e.g. was not closed on last run, go straight to it.
        if (lastOpenedProject > 0){
            startActivity(intent);
        }
    }
    public void populateProjectList(){
        // Get readable database in order to get a list of projects
        db = DataBaseHandler.getInstance(getApplicationContext());
        projectsListIDs.clear();
        projectsListStrings.clear();
        List<ProjectEntry> projectsList = db.getAllProjectEntries();
        //Log.i("Salsa sauce",projectsList.get(0).getName());
        // get project ID's and name
        for (int i = 0; i < db.getProjectsCount(); i++) {
            projectsListIDs.add(projectsList.get(i).getId());
            projectsListStrings.add(projectsList.get(i).getName());
        }
        try{
            adapter.notifyDataSetChanged();
        }catch (NullPointerException e){

        }
        db.close();
    }
    public void populateLists(){
        // Get readable database in order to get a list of projects
        db = DataBaseHandler.getInstance(getApplicationContext());

        fixedpoints = db.getAllFixedpointEntries();
        List<InstrumentEntry> instruments = db.getAllInstrumentEntries();
        List<RodEntry> rods = db.getAllRodEntries();
        List<AlarmEntry> alarms = db.getAllAlarmEntries();
        List<AntennaEntry> antennae = db.getAllAntennaEntries();

        db.close();
    }

    /** Called when the user clicks the settings button */
    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    /** Called when the user clicks the Load Project button */
    public void openProject(View view) {
        // Get readable database in order to get a list of projects
        populateProjectList();

        DataBaseHandler db = DataBaseHandler.getInstance(mContext);
        List<ProjectEntry> projectsList = db.getAllProjectEntries();
        //If there are projects
        if (projectsList.size() > 0){
            dialog.show();
        }else{ //No projects
            Dialog nothing_to_load_dialog = new Dialog(MainActivity.this);
            nothing_to_load_dialog.setContentView(R.layout.nothing_to_load);
            nothing_to_load_dialog.setTitle("Nothing to Load");
            nothing_to_load_dialog.show();
            nothing_to_load_dialog.setCancelable(true);
        }
        db.close();
    }
    /** Called when the user clicks the New Project button */
    public void newProject(View view) {
        Log.i("MainActivity","allowProject: " + allowProject.toString());
        Log.i("MainActivity","lastUpdateDate: " + prefs.getInt("lastUpdateDate",0));
        if (allowProject.equals(1)){
            // TODO: clear lastOpenedProject? Or do this elsewhere?
            editor.putInt("lastOpenedProject", -1);
            editor.commit();

            Intent intent = new Intent(this, ProjectActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public void onStart(){
        super.onStart();

    }
    @Override
    public void onResume(){
        super.onResume();
        populateProjectList();
        adapter.notifyDataSetChanged();

        //If the there has been an update ever.
        if (prefs.getInt("lastUpdateDate",0) > 0){
            populateLists();
            allowProject = 1;
            editor.putInt("allowProject", 1);
            editor.commit();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        populateProjectList();
        adapter.notifyDataSetChanged();
        db.close();
    }

}
