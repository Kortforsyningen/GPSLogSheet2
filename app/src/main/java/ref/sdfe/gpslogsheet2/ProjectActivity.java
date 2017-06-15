package ref.sdfe.gpslogsheet2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.net.Uri;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SharedPreferences prefs;
    public Integer lastOpenedProject;
    public List<ProjectEntry> projectsList;
    public Context mContext;
    public List<Integer> projectsListIDs;
    public List<String> projectsListStrings;
    private ProjectEntry project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Get application context
        mContext = getApplicationContext();

        //Get shared preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        // Get readable database in order to get a list of projects
        DataBaseHandler db = DataBaseHandler.getInstance(mContext);

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
        }else{
            //generate new id number for project.
            Integer id = projectsListIDs.get(projectsListIDs.size());
            project = new ProjectEntry(++id);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //toolbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


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
        FloatingActionButton fab_close = (FloatingActionButton) findViewById(R.id.fab_close);
        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "TODO: Save and close project.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // TODO: make dialog, "Do you want to save changes?"
                // if yes commit project and images to database
                // if no discard changes and close project.
            }
        });
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

