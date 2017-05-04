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

        // get singleton instance of the database
        DataBaseHandler db = DataBaseHandler.getInstance(this);

        Context context = getApplicationContext();
        CharSequence text = Integer.toString(db.getRodsCount());
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

//        RodEntry rod = db.getRodEntry(2);

//        CharSequence text15 = rod.getName();
//        Toast toast15 = Toast.makeText(context, text15, duration);
//        toast15.show();

        //Create sample data
        RodEntry sampleRod = new RodEntry(1,"Virker det?!?!",1.05);
        // add sample rod to db

        //db.addRodEntry(sampleRod);

//        List<RodEntry> rodList = db.getAllRodEntries();
//
//        for (RodEntry tempRod : rodList) {
//            db.deleteRod(tempRod);
//        }

        CharSequence text2= Integer.toString(db.getRodsCount());
        Toast toast2 = Toast.makeText(context, text2, duration);
        toast2.show();


//        int count = db.getRodsCount();



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
