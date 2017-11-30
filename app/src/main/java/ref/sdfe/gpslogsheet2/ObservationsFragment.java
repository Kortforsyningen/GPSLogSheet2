package ref.sdfe.gpslogsheet2;

import android.app.Activity;
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
    ArrayAdapter observationsAdapter;

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
        Log.i("ObservationsFragment","Setup selected: " + setup.getId());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get setup in which to add measurements
        setup = ProjectActivity.project.getSetups().get(SetupsFragment.id);
        Log.i("ObservationsFragment","Setup selected: " + setup.getId());


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
        Log.i("ObservationsFragment","Setup selected: " + setup.getId());

        //TODO: observations adapter
        setup.addObservation(0,1.5);
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
                    //Log.i("ObservationFragment",String.valueOf(ids.));
                    // Find maximum ID and add one
                    int id;
                    try{id = Collections.max(ids) + 1;}catch(NullPointerException e){
                        id = 0;
                    }

                    setup.addObservation(id,Double.parseDouble(measurementField.getText().toString()));
                    Log.i("ObservationsFragment","Observation added!");
                    observations = setup.getObservations();
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

        return view;
    }
}
