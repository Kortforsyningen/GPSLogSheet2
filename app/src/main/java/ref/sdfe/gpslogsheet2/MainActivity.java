package ref.sdfe.gpslogsheet2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
        DataBaseHandler db = DataBaseHandler.getInstance(mContext);

        // TEST Projects Test
        ProjectEntry proj1 = new ProjectEntry(1);
        ProjectEntry proj2 = new ProjectEntry(2);
        ProjectEntry proj3 = new ProjectEntry(3);
        ProjectEntry proj4 = new ProjectEntry(4);
        ProjectEntry proj5 = new ProjectEntry(5);
        ProjectEntry proj6 = new ProjectEntry(6);
        ProjectEntry proj7 = new ProjectEntry(7);
        ProjectEntry proj8 = new ProjectEntry(8);
        ProjectEntry proj9 = new ProjectEntry(9);
        ProjectEntry proj10 = new ProjectEntry(10);
        ProjectEntry proj11 = new ProjectEntry(11);
        ProjectEntry proj12 = new ProjectEntry(12);
        ProjectEntry proj13 = new ProjectEntry(13);
        ProjectEntry proj14 = new ProjectEntry(14);
        ProjectEntry proj15 = new ProjectEntry(15);
        ProjectEntry proj16 = new ProjectEntry(16);
        ProjectEntry proj17 = new ProjectEntry(17);
        ProjectEntry proj18 = new ProjectEntry(18);
        ProjectEntry proj19 = new ProjectEntry(19);
        proj1.setName("Project1");
        proj2.setName("Project2");
        proj3.setName("Project3");
        proj4.setName("Project4");
        proj5.setName("Project5");
        proj6.setName("Project6");
        proj7.setName("Project7");
        proj8.setName("Project8");
        proj9.setName("Project9");
        proj10.setName("Project10");
        proj11.setName("Project11");
        proj12.setName("Project12");
        proj13.setName("Project13");
        proj14.setName("Project14");
        proj15.setName("Project15");
        proj16.setName("Project16");
        proj17.setName("Project17");
        proj18.setName("Project18");
        proj19.setName("Project19");


        // put them into the database.
        if (db.getProjectsCount() == 0 ){
            db.addProjectEntry(proj1);
            db.addProjectEntry(proj2);
            db.addProjectEntry(proj3);
            db.addProjectEntry(proj4);
            db.addProjectEntry(proj5);
            db.addProjectEntry(proj6);
            db.addProjectEntry(proj7);
            db.addProjectEntry(proj8);
            db.addProjectEntry(proj9);
            db.addProjectEntry(proj10);
            db.addProjectEntry(proj11);
            db.addProjectEntry(proj12);
            db.addProjectEntry(proj13);
            db.addProjectEntry(proj14);
            db.addProjectEntry(proj15);
            db.addProjectEntry(proj16);
            db.addProjectEntry(proj17);
            db.addProjectEntry(proj18);
            db.addProjectEntry(proj19);
        }

        //Create list of projects
        //TODO: Medium/low priority: Find a way to show the list in reverse chronological order, alphabetical etc.
        List<ProjectEntry> projectsList = db.getAllProjectEntries();
        projectsListIDs = new ArrayList<>();
        projectsListStrings = new ArrayList<>();
        // get project ID's and name
        for (int i = 0; i < db.getProjectsCount(); i++) {
            projectsListIDs.add(projectsList.get(i).getId());
            projectsListStrings.add(projectsList.get(i).getName());
        }
        final Intent intent = new Intent(this, ProjectActivity.class);

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

        // If some project is already opened, e.g. was not closed on last run
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
