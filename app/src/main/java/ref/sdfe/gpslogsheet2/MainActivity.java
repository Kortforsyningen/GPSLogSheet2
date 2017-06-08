package ref.sdfe.gpslogsheet2;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Test zone:
        ProjectEntry projectEntry = new ProjectEntry(1);

        projectEntry.setName("Test Project");
        projectEntry.setOperator("oldjo");
        //projectEntry.

        String pstring = projectEntry.getJsonString();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(this, pstring, duration);
        toast.show();

        // End of Test zone.

    }
    /** Called when the user clicks the settings button */
    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    /** Called when the user clicks the Project button */
    public void openProject(View view) {
        Intent intent = new Intent(this, ProjectActivity.class);
        startActivity(intent);
    }

}
