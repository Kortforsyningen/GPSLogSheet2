package ref.sdfe.gpslogsheet2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by B028406 on 8/31/2017.
 *
 *
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return ProjectActivity.ProjectSettingsFragment.newInstance(position + 1);
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