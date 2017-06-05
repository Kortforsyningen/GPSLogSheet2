package ref.sdfe.gpslogsheet2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class UpdateActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        new SyncDataFTP(this.getApplicationContext()).execute();
        finish();
    }
    static class SyncDataFTP extends AsyncTask<Context, Integer, CharSequence> {

        private Context mContext;
        private SyncDataFTP(Context context) {
            mContext = context;
        }

        private String username = "";
        private String password = "";
        private String host = "";
        private String path = "";
        private String filename = "";

        private Boolean projects = false;
        private Boolean alarms = false;
        private Boolean antennas = false;
        private Boolean fixedpoints = false;
        private Boolean instruments = false;
        private Boolean rods = false;

        private int reply;
        int duration = Toast.LENGTH_SHORT;

        @Override
        protected void onPreExecute() {
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
            rods = prefs.getBoolean("switch_preference_bars",false);
        }

        @Override
        protected CharSequence doInBackground(Context... params) {
            // Check if anything is supposed to be updated at all:
            if (projects || alarms || antennas || fixedpoints || instruments || rods) {

                DataBaseHandler db = DataBaseHandler.getInstance(mContext);
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


                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    return "Something bad happened";
                    //throw new RuntimeException(e);
                }
            }
            else {
                return "Choose what to update.";
            }

            return "Synchronization successful.";
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO: Some clever way to show the progress.
            //setProgressPercent(progress[0]);
        }
        @Override
        protected void onPostExecute(CharSequence result) {
            //CharSequence text = "onPostExecute executes!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(mContext, result, duration);
            toast.show();
        }
    }
}
