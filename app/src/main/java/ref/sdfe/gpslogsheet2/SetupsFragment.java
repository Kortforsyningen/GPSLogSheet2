package ref.sdfe.gpslogsheet2;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by B028406 on 8/31/2017.
 */

public class SetupsFragment extends Fragment {
    public SetupsFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // OLDJO: Inflate the layout
        View view = inflater.inflate(R.layout.setup_tab_layout, container, false);
        return view;
    }
}
