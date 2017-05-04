package ref.sdfe.gpslogsheet2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        new SyncDataFTP(this).execute();
    }
    private static class SyncDataFTP extends AsyncTask<Void, Void, Void> {
        Context mContext;
        public SyncDataFTP(Context context) {
            mContext = context;
        }
//            private String username = "";
//            private String password = "";
//            private String host = "";
//            private String path = "";
//            private Boolean projects = false;
//            private Boolean alarms = false;
//            private Boolean antennae = false;
//            private Boolean fixedpoints = false;
//            private Boolean instruments = false;
//            private Boolean rods = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //get sharedPreferences here
            SharedPreferences credentials = mContext.getSharedPreferences("pref_credentials", MODE_PRIVATE);

            String username = credentials.getString("credentials_ftp_user", "");
            String password = credentials.getString("credentials_ftp_password", "");

            String host = credentials.getString("credentials_ftp_host", "");
            String path = credentials.getString("credentials_ftp_path", "");

            SharedPreferences data_sync = mContext.getSharedPreferences("pref_data_sync", MODE_PRIVATE);

            Boolean antennas = Boolean.valueOf(data_sync.getString("switch_preference_antennas", ""));

            // Display popup if it happens
            Context context = mContext;
            CharSequence text = "onPreExecute executes!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            return null;
        }
//            protected void onProgressUpdate(Integer... progress) {
//                setProgressPercent(progress[0]);
//            }

//            protected void onPostExecute(Integer result) {
//                showDialog("Downloaded " + result + " bytes");
//            }
    }
}
