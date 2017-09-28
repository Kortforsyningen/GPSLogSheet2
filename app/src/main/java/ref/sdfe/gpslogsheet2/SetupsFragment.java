package ref.sdfe.gpslogsheet2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;

/**
 * Created by B028406 on 8/31/2017.
 */

public class SetupsFragment extends Fragment {
    Integer id;
    ProjectEntry project;
    ProjectEntry.Setup setup;

    public SetupsFragment() {


    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("SetupsFragment","onResume()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle bundle) {
        // OLDJO: Inflate the layout
        View view = inflater.inflate(R.layout.setup_tab_layout, container, false);
        id = getArguments().getInt("Id");
        project = ProjectActivity.project;
        setup = project.getSetups().get(id);

        // TextViews

        // Spinners


        // Buttons
        Button location_button = (Button) view.findViewById(R.id.locationButton);
        Button rename_button = (Button) view.findViewById(R.id.renameButton);
        Button delete_button = (Button) view.findViewById(R.id.deleteButton);
        Button clear_button = (Button) view.findViewById(R.id.clearButton);

        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LOCADE CODE HERE
                Log.i("SetupsFragment", "Location Button Pressed!");
            }
        });
        rename_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RENAME CODE HERE
                Log.i("SetupsFragment", "Rename Button Pressed!");
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RENAME CODE HERE
                Log.i("SetupsFragment", "Delete Button Pressed!");

                project.removeSetup(id);

                getFragmentManager().beginTransaction().remove(SetupsFragment.this).commitAllowingStateLoss();
                //This only removes the content of the tab, not the
                Log.i("SetupsFragment","huh");



            }
        });
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RENAME CODE HERE
                Log.i("SetupsFragment", "Clear Button Pressed!");
            }
        });

        return view;
    }
}
