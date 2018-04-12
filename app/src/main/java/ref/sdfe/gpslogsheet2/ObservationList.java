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
import java.util.concurrent.ExecutionException;

/**
 * Created by B028406 on 10/30/2017.
 */

public class ObservationList extends ArrayAdapter {
    private final Activity context;
    private final List<Integer> id;
    private final ArrayList<ProjectEntry.Setup.Observation> observations;

    public ObservationList(Activity context, List<Integer> id, HashMap<Integer, ProjectEntry.Setup.Observation> observations) {
        super(context, R.layout.list_observation, id);
        this.context = context;
        this.observations = new ArrayList<>(observations.values());
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
        return position;
    }

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
            ProjectEntry.Setup.Observation obs = observations.get(id.get(position));

            textId.setText(String.valueOf(obs.getId()));
            //measurementText.setText(String.valueOf(obs.getMeasurement()));
            measurementText.setText("Testing");
            //noteText.setText(obs.getRemark());
            noteText.setText("Still Testing");
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
            Log.i("ObservationList","Deleting Obs. " + String.valueOf(observations.get(position).getId()) );
            ObservationsFragment.setup.deleteObservation(position);
            ProjectActivity.project.setModDate();
            observations.remove(position);
            id.remove(position);
            //remove(ObservationList.this.getItem(position));
            notifyDataSetChanged();
        }
    };
}
