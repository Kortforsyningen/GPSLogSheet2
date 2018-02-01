package ref.sdfe.gpslogsheet2;

//import android.app.Activity;
import android.app.Dialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by B028406 on 9/14/2017.
 */

public class ObservationsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "2";

    //public static String setupId;
    public static ProjectEntry.Setup setup;
    HashMap<Integer, ProjectEntry.Setup.Observation> observations;
    List <Integer> observationIDs;
    ListView observationsList;

    // Lists
    //List<Double> measurements = new ArrayList<>();

    // Adapters
    static ArrayAdapter observationsAdapter;

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
    public void onPause() {
        super.onPause();
        setup = ProjectActivity.project.getSetups().get(SetupsFragment.id);
        Log.i("ObservationsFragment","onPause, Setup selected: " + setup.getId());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get setup in which to add measurements
        setup = ProjectActivity.project.getSetups().get(SetupsFragment.id);
        Log.i("ObservationsFragment","onResume, Setup selected: " + setup.getId());


        try {if (!observationIDs.isEmpty()){
            // If there are any observations
            // TODO:
            observationsList.setAdapter(observationsAdapter);
        }else{
            //If there aren't any observations yet

        }}catch(NullPointerException e){

        }
        Log.i("ObservationsFragment", "onResume()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_observations, container, false);

        // Get setup in which to add measurements
        setup = ProjectActivity.project.getSetups().get(SetupsFragment.id);
        Log.i("ObservationsFragment","OnCreateView, Setup selected: " + setup.getId());

        //TODO: observations adapter
        Log.i("getObservationCount", setup.getObservationCount().toString());
        if(setup.getObservationCount() < 1){
            // add dummy observation
            setup.addObservation(0,0);

        }

        observationIDs = setup.getObservationIDs();
        observations = setup.getObservations();

        try{
            for (Integer id : observationIDs) {

            }
        }catch(NullPointerException e){
            setup.addObservation(0,0);
            Log.i("ObservationsFragment","Nullpointer exep");
            observations = setup.getObservations();
        }

        //observationsAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, observationIDs);


        try {if (!observationIDs.isEmpty()){
            // If there are any observations
            // TODO:
            observationsList.setAdapter(observationsAdapter);
        }else{
            //If there aren't any observations yet

        }}catch(NullPointerException e){

        }

        observationsAdapter = new ObservationList(this.getActivity(),observationIDs,observations);
        observationsList = (ListView) view.findViewById(R.id.observations_list);
        observationsList.setAdapter(observationsAdapter);

        // Add observation dialog:
        final Dialog addObservationDialog = new Dialog(this.getContext());
        addObservationDialog.setContentView(inflater.inflate(R.layout.add_observation_dialog, new LinearLayoutCompat(getContext()), false));
        Button obs_cancel_button = (Button) addObservationDialog.findViewById(R.id.button_cancel_obs);
        Button obs_save_button = (Button) addObservationDialog.findViewById(R.id.button_save_obs);
        Button obs_delete_button = (Button) addObservationDialog.findViewById(R.id.button_delete_obs);
        final EditText measurementField = (EditText) addObservationDialog.findViewById(R.id.measurement);

        obs_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CANCEL CODE HERE
                addObservationDialog.dismiss();
            }
        });
        obs_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (measurementField.getText().toString().isEmpty()){
                    //Nothing to save
                    Log.i("ObservationFragment","Nothing to save!");
                }else{
                    // SAVE CODE HERE

                    //Get the setups from the project.
                    List<Integer> ids = setup.getObservationIDs();
//                    for(Integer id : ids){
//                        Log.i("ObsFragment, IDS",id.toString());
//                    }
                    Log.i("ObservationFragment",String.valueOf(ids));
                    // Find maximum ID and add one
                    int id;
                    try{id = Collections.max(ids) + 1;}catch(NullPointerException e){
                        id = 1;
                    }

                    setup.addObservation(id,Double.parseDouble(measurementField.getText().toString()));
                    Log.i("ObservationsFragment","Observation added!");
                    observations = setup.getObservations();
                    observationIDs = setup.getObservationIDs();

                    observationsAdapter.notifyDataSetChanged();
                    ((ArrayAdapter) observationsList.getAdapter()).notifyDataSetChanged();

                    addObservationDialog.dismiss();
                }
            }
        });
        obs_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DELETE CODE HERE

                //TODO: Alert dialog (Y/N)

                addObservationDialog.dismiss();
            }
        });



        // Add observation button
        Button add_button = (Button) view.findViewById(R.id.add_observation_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addObservationDialog.show();
            }
        });

        if (observationsAdapter.isEmpty()) {
            populateObservations();
        }

        return view;
    }

    public static void fragmentChanged(){
        /**
         * This gets called when user selects different setup in ProjectSettingsFragment
         * Makes sure that ObservationsFragment is working on correct setup.
         */
        try {
            observationsAdapter.notifyDataSetChanged();
        }catch(NullPointerException e){
            Log.i("ObservationsFragment", "fragmentChanged(), NPE");
    }

        Log.i("ObservationsFragment", "fragmentChanged()");
//        Toast.makeText(actvity, "Fragment Changed", Toast.LENGTH_SHORT).show();
    }

    private void populateObservations(){

        Log.i("ObservationFragment", "Attempting to populate observations.");
        observations = setup.getObservations();
        observationIDs = setup.getObservationIDs();
        try {
            // If there are any observations
            if (!observationIDs.isEmpty()) {
                Log.i("ObservationFragment", "Populating observations.");
                for (Integer obs : observationIDs) {
                    }
                }
            }catch(NullPointerException e){
            Log.i("ObservationFragment", "Nullpointer Exception");
        }
    }
}