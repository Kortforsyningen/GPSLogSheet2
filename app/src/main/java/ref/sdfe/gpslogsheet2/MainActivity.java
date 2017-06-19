package ref.sdfe.gpslogsheet2;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.layout.simple_list_item_1;

public class MainActivity extends AppCompatActivity {
    public Integer lastOpenedProject = -1;
    public List<ProjectEntry> projectsList;
    public Context mContext;
    public List<Integer> projectsListIDs;
    public List<String> projectsListStrings;
    public Dialog dialog;
    public Integer MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    public DataBaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context mContext;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        // Get shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        lastOpenedProject = prefs.getInt("lastOpenedProject", 0);
        final SharedPreferences.Editor editor = prefs.edit();


        // Get readable database in order to get a list of projects
        db = DataBaseHandler.getInstance(mContext);

        // PERMISSIONS!
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


                // MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }



        //Create list of projects
        //TODO: Medium/low priority: Find a way to show the list in reverse chronological order, alphabetical etc.
        projectsList = db.getAllProjectEntries();
        projectsListIDs = new ArrayList<>();
        projectsListStrings = new ArrayList<>();
        // get project ID's and name
        for (int i = 0; i < db.getProjectsCount(); i++) {
            projectsListIDs.add(projectsList.get(i).getId());
            projectsListStrings.add(projectsList.get(i).getName());
        }
        final Intent intent = new Intent(this, ProjectActivity.class);


        //
        dialog = new Dialog(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.load_dialog, new LinearLayoutCompat(mContext), false);
        dialog.setContentView(convertView);
        dialog.setCancelable(true);
        dialog.setTitle("Choose Project:");

        final ListView listview = (ListView) convertView.findViewById(R.id.LoadList);
        final StableArrayAdapter adapter = new StableArrayAdapter(this, simple_list_item_1, projectsListStrings);


        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                editor.putInt("lastOpenedProject",projectsListIDs.get(position));
                editor.apply();
                //TODO: save in prefs
                startActivity(intent);
                dialog.dismiss();
            }
        });

        // If some project is already opened, e.g. was not closed on last run, go straight to it.
        if (lastOpenedProject > 0){
            startActivity(intent);
        }
    }
    /** Called when the user clicks the settings button */
    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    /** Called when the user clicks the Load Project button */
    public void openProject(View view) {
        // Get readable database in order to get a list of projects
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
    }
    /** Called when the user clicks the New Project button */
    public void newProject(View view) {
        // TODO: clear lastOpenedProject? Or do this elsewhere?
        Intent intent = new Intent(this, ProjectActivity.class);
        startActivity(intent);
    }
    @Override
    public void onStart(){
        super.onStart();

        projectsList = db.getAllProjectEntries();
        projectsListIDs = new ArrayList<>();
        projectsListStrings = new ArrayList<>();
        // get project ID's and name
        for (int i = 0; i < db.getProjectsCount(); i++) {
            projectsListIDs.add(projectsList.get(i).getId());
            projectsListStrings.add(projectsList.get(i).getName());
        }
        final Intent intent = new Intent(this, ProjectActivity.class);

    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
