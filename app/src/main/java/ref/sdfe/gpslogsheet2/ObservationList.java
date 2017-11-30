package ref.sdfe.gpslogsheet2;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Class that that makes a photo and text adapter for the SetupsFragment
 * Created by B028406 on 10/30/2017.
 */

public class ObservationList extends ArrayAdapter {
    private final Activity context;
    private final List<Integer> id;
    private final ArrayList<ProjectEntry.Setup.Observation> observations;

    public ObservationList(Activity context, List<Integer> id,  HashMap<Integer, ProjectEntry.Setup.Observation> observations) {
        super(context, R.layout.list_observation, id);
        this.context = context;
        this.observations = new ArrayList<>(observations.values());
        this.id = id;

    }
    public int getCount(){
        try{
            Log.i("ObservationList", "getCount: " + String.valueOf(id.size()) + " returned");
            return id.size();
        }catch(NullPointerException e){
            Log.i("ObservationList","getCount: 0 returned");
            return 0;
        }
    }
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (rowView == null) rowView = inflater.inflate(R.layout.list_observation, null, true);

        TextView textId = (TextView) rowView.findViewById(R.id.id_observation);

        TextView measurementText = (TextView) rowView.findViewById(R.id.text_measurement);

        TextView noteText = (TextView) rowView.findViewById(R.id.text_note);

        textId.setText(id.get(position));
        Log.i("ObservationList","getView");
        measurementText.setText(String.valueOf(observations.get(id.get(position)).getMeasurement()));

        noteText.setText(observations.get(id.get(position)).getRemark());

        return rowView;
    }
}
