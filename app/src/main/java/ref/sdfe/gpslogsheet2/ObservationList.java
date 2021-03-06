package ref.sdfe.gpslogsheet2;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static ref.sdfe.gpslogsheet2.ProjectActivity.project;

/**
 * Created by B028406 on 10/30/2017.
 */

public class ObservationList extends ArrayAdapter {
    private final Activity context;
    private final List<Integer> id;
    private final List<ProjectEntry.Setup.Observation> observation;
    //private final ArrayList<ProjectEntry.Setup.Observation> observations;

    public ObservationList(Activity context,
                           List<Integer> id,
                           List<ProjectEntry.Setup.Observation> observation) {
        super(context, R.layout.list_observation, id);
        this.context = context;
        this.observation = observation;
        //this.observation = new ArrayList<>(observationsHashMap.values());
        this.id = id;

    }

    public int getCount() {
        try {
            //Log.i("ObservationList", "getCount: " + String.valueOf(id.size()) + " returned");
            return id.size();
        } catch (NullPointerException e) {
            //Log.i("ObservationList", "getCount: 0 returned, NullPointerException");
            return 0;
        }
    }

    public long getItemId(int position) {
        //return position;
        return id.get(position);
    }
//    public Object getItem(int position){
//        return observations.get(position);
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (rowView == null) rowView = inflater.inflate(R.layout.list_observation, null, true);

        TextView textId = (TextView) rowView.findViewById(R.id.id_observation);
        TextView measurementText = (TextView) rowView.findViewById(R.id.text_measurement);
        TextView noteText = (TextView) rowView.findViewById(R.id.text_note);

        Button editButton = (Button) rowView.findViewById(R.id.edit_button);
        Button delButton = (Button) rowView.findViewById(R.id.delete_button);

        Log.i("ObservationList", "id.get: " + String.valueOf(id.get(position)));

        //textId.setText(id.get(position));

        Log.i("ObservationList", "getView");
        try {
            //ProjectEntry.Setup.Observation obs = observations.get(id.get(position));
            //ProjectEntry.Setup.Observation obs = observations.get(position);
            ProjectEntry.Setup.Observation obs = this.observation.get(position);

            textId.setText(String.valueOf(obs.getId()));
            //measurementText.setText(String.valueOf(obs.getMeasurement()));
            //measurementText.setText("Testing");
            Double measurement = obs.getMeasurement();
            measurementText.setText(measurement.toString() + " unit");
            //measurementText.setText(String.format(Locale.getDefault()));

            //noteText.setText(obs.getRemark());
            noteText.setText("Still Testing");

            Log.i("ObservationList", "Index within bounds");
        } catch (IndexOutOfBoundsException E) {
            Log.i("ObservationList", "Index out of bounds");
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Edit Measurement dialog
                Log.i("ObservationList", "Edit Button pressed!");
            }
        });
        delButton.setTag(position);
        delButton.setOnClickListener(myDeleteClickListener);

        return rowView;
    }

    private View.OnClickListener myDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Toast.makeText(ObservationList.this.context, "" + position, Toast.LENGTH_SHORT).show();

            // After remove row from list call this
            //Log.i("ObservationList","Deleting Obs. " + String.valueOf(observations.get(position).getId()) );

            //Delete observation in current setup
            //ObservationsFragment.setup.deleteObservation(id.get(position));
//            ObservationsFragment.observationsAdapter.remove(id.get(position));
//            ObservationsFragment.observationsAdapter.notifyDataSetChanged();
//            ObservationsFragment.deleteObservation(id.get(position));
            //ObservationsFragment.removeObservation(id.get(position));


            //Notify Project modified
            //ProjectActivity.project.setModDate();

            //Remove from list:
            //observations.remove(position);
            //id.remove(position);


        }
    };
}
