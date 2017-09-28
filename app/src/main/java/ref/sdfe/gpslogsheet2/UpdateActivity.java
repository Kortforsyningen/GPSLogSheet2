package ref.sdfe.gpslogsheet2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.sql.Date;
import java.util.GregorianCalendar;

public class UpdateActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        new SyncDataFTP(this).execute();

        if (pd == null) {
            finish(); //This caused a headache
        }
    }

    private ProgressDialog pd;

    private void showProgressDialog() {
        if (pd == null) {
            pd = new ProgressDialog(UpdateActivity.this);
            pd.setTitle("Synchronizing");
            pd.setMessage("Please wait...");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
        }
        pd.show();
    }

    private void dismissProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
        finish();
    }

    private class SyncDataFTP extends AsyncTask<Context, Integer, CharSequence> {

        private Context mContext;
        private ProgressDialog pd;
        private Activity activity;

        //private SyncDataFTP(Context context) {
        private SyncDataFTP(Activity activity) {
            this.activity = activity;
            mContext = this.activity.getApplicationContext();
        }

        private String username = "";
        private String password = "";
        private String host = "";
        private String path = "";
        private String filename = "";

        private Boolean projects = false;
        private Boolean images = false;
        private Boolean alarms = false;
        private Boolean antennas = false;
        private Boolean fixedpoints = false;
        private Boolean instruments = false;
        private Boolean rods = false;

        private int reply;
        int duration = Toast.LENGTH_SHORT;

        DataBaseHandler db;

        @Override
        protected void onPreExecute() {
            showProgressDialog();

            super.onPreExecute();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());

            username = prefs.getString("credentials_ftp_user", "");
            password = prefs.getString("credentials_ftp_password", "");

            host = prefs.getString("credentials_ftp_host", "");
            path = prefs.getString("credentials_ftp_path", "");

            alarms = prefs.getBoolean("switch_preference_alarms",false);
            antennas = prefs.getBoolean("switch_preference_antennas",false);
            fixedpoints = prefs.getBoolean("switch_preference_points",false);
            instruments = prefs.getBoolean("switch_preference_instruments",false);
            rods = prefs.getBoolean("switch_preference_rods",false);
            projects = prefs.getBoolean("switch_preference_projects",false);
            images = prefs.getBoolean("switch_preference_images",false);
            projects = prefs.getBoolean("switch_preference_projects",false);


        }

        @Override
        protected CharSequence doInBackground(Context... params) {
            // Check if anything is supposed to be updated at all:
            if (projects || alarms || antennas || fixedpoints || instruments || rods) {


                db = DataBaseHandler.getInstance(mContext);
                try {
                    // New instance of FTPClient
                    FTPClient ftpClient = new FTPClient();
                    ftpClient.setConnectTimeout(5000);

                    //Connect to server
                    try {
                        ftpClient.connect(InetAddress.getByName(host));
                    } catch (IOException e) {
                        return "FTP timed out, please check host address and internet connection.";
                    }

                    reply = ftpClient.getReplyCode();
                    if (!FTPReply.isPositiveCompletion(reply)) {
                        ftpClient.disconnect();
                        return "FTP server refused connection.";
                    }
                    Log.i("FTP", ftpClient.getReplyString());

                    //Login
                    ftpClient.login(username, password);
                    reply = ftpClient.getReplyCode();
                    if (!FTPReply.isPositiveCompletion(reply)) {
                        ftpClient.disconnect();
                        return "Login Failed. Check username/password.";
                    }

                    //Change path
                    ftpClient.changeWorkingDirectory(path);
                    Log.i("FTP", ftpClient.getReplyString());
                    reply = ftpClient.getReplyCode();
                    if (FTPReply.isNegativePermanent(reply)) {
                        ftpClient.disconnect();
                        return "Check Path setting.";
                    }
                    //Change path to settings directory
                    ftpClient.changeWorkingDirectory("settings");
                    Log.i("FTP", ftpClient.getReplyString());
                    reply = ftpClient.getReplyCode();
                    if (FTPReply.isNegativePermanent(reply)) {
                        ftpClient.disconnect();
                        return "Path is missing a settings directory.";
                    }

                    // Enter Passive Mode
                    ftpClient.enterLocalPassiveMode();
                    // !############!
                    // !# SETTINGS #!
                    // !############!

                    // alarms!
                    if (alarms) {
                        filename = "alarms.csv";
                        // Retrieve file as inputstream
                        InputStream inputStream = ftpClient.retrieveFileStream(filename);
                        Log.i("FTP", ftpClient.getReplyString());

                        reply = ftpClient.getReplyCode();
                        if (!FTPReply.isPositivePreliminary(reply)) {
                            ftpClient.disconnect();
                            return filename + " not found in settings directory.";
                        }

                        if (inputStream != null) {
                            // If there is a file, delete old entries in database.
                            db.deleteAlarmTable();

                            // Turn inputstream into a bufferedReader
                            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                            String line = "";
                            // While there are lines to read
                            while ((line = br.readLine()) != null) {
                                String[] str = line.split(",");
                                Log.i("Alarms", str[1]); // testing

                                //Transform TODO: Quality check of data.
                                int alarm_id = Integer.parseInt(str[0]);
                                String alarm_name = str[1];

                                AlarmEntry tempAlarm = new AlarmEntry(alarm_id, alarm_name);

                                //Load entry into database.
                                db.addAlarmEntry(tempAlarm);

                            }
                            br.close();
                            inputStream.close();
                        }

                        ftpClient.completePendingCommand();
                    }
                    // Antennas!
                    if (antennas) {
                        filename = "antennas.csv";
                        // Retrieve file as inputstream
                        InputStream inputStream = ftpClient.retrieveFileStream(filename);
                        Log.i("FTP", ftpClient.getReplyString());

                        reply = ftpClient.getReplyCode();
                        if (!FTPReply.isPositivePreliminary(reply)) {
                            ftpClient.disconnect();
                            return filename + " not found in settings directory.";
                        }

                        if (inputStream != null) {
                            // If there is a file, delete old entries in database.
                            db.deleteAntennaTable();

                            // Turn inputstream into a bufferedReader
                            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                            String line = "";
                            // While there are lines to read
                            while ((line = br.readLine()) != null) {
                                String[] str = line.split(",");
                                Log.i("Antenna", str[1]); // testing

                                //Transform TODO: Quality check of data.
                                int antenna_id = Integer.parseInt(str[0]);
                                String antenna_name = str[1];
                                String antenna_code = str[2];

                                AntennaEntry tempAntenna = new AntennaEntry(antenna_id, antenna_name,
                                        antenna_code);

                                //Load entry into database.
                                db.addAntennaEntry(tempAntenna);

                            }
                            br.close();
                            inputStream.close();
                        }

                        ftpClient.completePendingCommand();
                    }

                    // Update fixedpoints
                    if (fixedpoints) {
                        filename = "fixedpoints.csv";
                        // Retrieve file as inputstream
                        InputStream inputStream = ftpClient.retrieveFileStream(filename);
                        Log.i("FTP", ftpClient.getReplyString());

                        reply = ftpClient.getReplyCode();
                        if (!FTPReply.isPositivePreliminary(reply)) {
                            ftpClient.disconnect();
                            return filename + " not found in settings directory.";
                        }

                        if (inputStream != null) {
                            // If there is a file, delete old entries in database.
                            db.deleteFixedpointTable();

                            // Turn inputstream into a bufferedReader
                            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                            int id = 0;
                            String line = "";
                            // While there are lines to read
                            while ((line = br.readLine()) != null) {
                                String[] str = line.split(",");
                                Log.i("Fixedpoints", str[0]); // testing

                                //Transform TODO: Quality check of data.

                                String gps_name = str[0];
                                String hs_name = str[1];
                                Double x = Double.parseDouble(str[2]);
                                Double y = Double.parseDouble(str[3]);

                                FixedpointEntry tempPoint = new FixedpointEntry(++id, gps_name, hs_name, x, y);

                                //Load entry into database.
                                db.addFixedpointEntry(tempPoint);

                            }
                            br.close();
                            inputStream.close();
                        }

                        ftpClient.completePendingCommand();
                    }


                    // Rods
                    Log.i("Rods", Boolean.toString(rods));
                    if (rods) {
                        filename = "rods.csv";
                        InputStream inputStream = null;
                        // Retrieve file as inputstream
                        inputStream = ftpClient.retrieveFileStream(filename);
                        Log.i("FTP", ftpClient.getReplyString());

                        reply = ftpClient.getReplyCode();
                        if (!FTPReply.isPositivePreliminary(reply)) {
                            ftpClient.disconnect();
                            return filename + " not found in settings directory.";
                        }

                        if (inputStream != null) {
                            // If there is a file, delete old entries in database.
                            db.deleteRodTable();

                            // Turn inputstream into a bufferedReader
                            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                            String line = "";
                            // While there are lines to read
                            while ((line = br.readLine()) != null) {
                                String[] str = line.split(",");
                                Log.i("Rods", str[1]); // testing

                                //Transform TODO: Quality check of data.
                                int rod_id = Integer.parseInt(str[0]);
                                String rod_name = str[1];
                                Double rod_length = Double.parseDouble(str[2]);

                                RodEntry tempRod = new RodEntry(rod_id, rod_name, rod_length);

                                //Load entry into database.
                                db.addRodEntry(tempRod);

                            }
                            br.close();
                            inputStream.close();
                        }
                        ftpClient.completePendingCommand();
                    }

                    //Instruments
                    Log.i("Instruments", Boolean.toString(instruments));
                    if (instruments) {
                        filename = "instruments.csv";
                        // Retrieve file as inputstream
                        InputStream inputStream = ftpClient.retrieveFileStream(filename);
                        Log.i("FTP", ftpClient.getReplyString());

                        reply = ftpClient.getReplyCode();
                        if (!FTPReply.isPositivePreliminary(reply)) {
                            ftpClient.disconnect();
                            return filename + " not found in settings directory.";
                        }

                        if (inputStream != null) {
                            // If there is a file, delete old entries in database.
                            db.deleteInstrumentTable();

                            // Turn inputstream into a bufferedReader
                            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                            String line = "";
                            // While there are lines to read
                            while ((line = br.readLine()) != null) {
                                String[] str = line.split(",");
                                Log.i("Instruments", str[1]); // testing

                                //Transform TODO: Quality check of data.
                                int instrument_id = Integer.parseInt(str[0]);
                                String instrument_name = str[1];

                                InstrumentEntry tempInstrument = new InstrumentEntry(instrument_id, instrument_name);

                                //Load entry into database.
                                db.addInstrumentEntry(tempInstrument);

                            }
                            br.close();
                            inputStream.close();
                        }

                        ftpClient.completePendingCommand();
                    }
                    // TODO: Projects!
                    // TODO: change path
                    if (projects) {
                        // Projects will be saved as individual json files

                        //Change path
                        ftpClient.changeWorkingDirectory("/" + path + "/projects");
                        Log.i("FTP", ftpClient.getReplyString());
                        reply = ftpClient.getReplyCode();
                        if (FTPReply.isNegativePermanent(reply)) {
                            ftpClient.disconnect();
                            return "Could not change into projects path.";
                        }
                        else{
                            //TODO: Projects
                            // Projects have unique names.
                            // If a local project has the name as a project on the server, check
                            // the date date created and date modified.
                            // if date created EQUAL and date modified newer on server, then download
                            // if date created NOT EQUAL, send a warning of conflict.
                        }
                    }

                    // Images

                    if (images) {
                        //Change path
                        ftpClient.changeWorkingDirectory("/" + path + "/images");
                        Log.i("FTP", ftpClient.getReplyString());
                        reply = ftpClient.getReplyCode();
                        if (FTPReply.isNegativePermanent(reply)) {
                            ftpClient.disconnect();
                            return "Could not change into images path.";
                        }
                        else{
                            //TODO: images
                            //
                            // Images will be saved as individual jpeg files
                            // Naming scheme will be closest <fixedpoint name>-yyyy-mm-dd-hh-mm-ss.jpg
                            // TODO: ask if this is a good naming scheme.
                            // TODO: Check: does database save all this info? add this filename to database?
                        }

                    }

                    ftpClient.logout();
                    ftpClient.disconnect();
                    return "Synchronization successful.";
                } catch (IOException e) {
                    return "Something bad happened";
                    //throw new RuntimeException(e);
                }
            }
            else {
                return "Choose what to update.";
            }

        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO: Low priority. Some clever way to show the progress.
            //setProgressPercent(progress[0]);
        }
        @Override
        protected void onPostExecute(CharSequence result) {
            db.close();
            //CharSequence text = "onPostExecute executes!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(mContext, result, duration);
            toast.show();
            dismissProgressDialog();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            final SharedPreferences.Editor editor = prefs.edit();

            //This *might* get used to implement an auto update feature.
            editor.putInt("lastUpdateDate", GregorianCalendar.DATE);
            finish();

        }
    }
}
