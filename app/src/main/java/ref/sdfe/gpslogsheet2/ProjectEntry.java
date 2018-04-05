package ref.sdfe.gpslogsheet2;


import android.util.Log;
import com.google.gson.Gson;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

/**
 * Created by B028406 on 01-06-2017.
 */

class ProjectEntry implements Cloneable{
    private int id; //Unique ID
    private String name; //Project Name
    private String operator; //Operator
    private long startDate; //Start date, set when project is first created
    private long endDate; //TODO: Set by user when project is finished
    private long modDate; //Modification date, set whenever a project parameter is changed
    private HashMap<Integer,Setup> setups; //HashMap to store setups

    public HashMap<Integer, Setup> getSetups() {
        return setups;
    }
    public int getSetupsCount() {
        try{
            return setups.size();
        }catch (NullPointerException E){
            return 0;
        }

    }
    public List<Integer> getSetupIDs() {
        Log.i("ProjectEntry","getSetupIDs called");
        List<Integer> IDs = null;
        try{
            //setups.forEach((i,setup) -> IDs.add(setup.getId());

            for(Map.Entry<Integer,Setup> entry : setups.entrySet()){
                Integer key = entry.getKey();
                IDs.add(key);
                Log.i("ProjectEntry","setupID added");
            }
            return IDs;
        }catch (NullPointerException E){
            return IDs;
        }
    }
    public ProjectEntry(int id) {
        /*
        * This constructor automatically gets current date
        * */
        this.id = id;
        this.startDate = System.currentTimeMillis();
        this.modDate = System.currentTimeMillis();
        setups = new HashMap<>();
    }
    public String getJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public long getModDate() {
        return modDate;
    }
    public void setModDate() {
        // TODO: This just gives the number five.
        //this.modDate = GregorianCalendar.DATE;
        this.modDate = System.currentTimeMillis();
        Log.i("ProjectEntry","Project Modified.");
    }
    public int getId() {
        return id;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
        setModDate();
    }
    public long getStartDate() {
        return startDate;
    }
    public long getEndDate() {
        return endDate;
    }
    public void setEndDate(long endDate) {
        this.endDate = endDate;
        setModDate();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
        setModDate();
    }
    public void addSetup(int id){
        Setup setup = new Setup(id);
        this.setups.put(setup.getId(), setup);
        setModDate();
    }
    public void removeSetup(int id){
        this.setups.remove(id);
        setModDate();
    }
    // OLDJO: Added this so that Project activity can store an unaltered copy.
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    public Map<String, String> generateValuesMap(Setup setup){

        long endDateLocal;
        //If no endDate assigned, assume one week after start date:
        try{endDateLocal = endDate;}
        catch(NullPointerException e){
            endDateLocal = startDate + (7 * 4 * 3600 * 1000); //add one week in milliseconds
        }
        Calendar startCal = GregorianCalendar.getInstance();
        startCal.setTimeInMillis(startDate);
        Calendar endCal = GregorianCalendar.getInstance();
        endCal.setTimeInMillis(endDateLocal);

        Map<String, String> valuesMap = new HashMap<>();

        DecimalFormat decimalFormat = new DecimalFormat("#.000");

        valuesMap.put("project_name",name);

        valuesMap.put("operator",operator);
        valuesMap.put("gps_name",setup.fixedPoint);
        valuesMap.put("hs_name",setup.hsName);
        valuesMap.put("instrument",setup.instrument);
        valuesMap.put("antenna_height",decimalFormat.format(setup.antennaHeight));
        valuesMap.put("antenna_name",setup.antenna);

        int ANTENNA_TYPE_LENGTH=20; // TODO: Consider making this an updatable setting. Hardcoding is bad. :/
        String paddingString = "";
        while ( paddingString.length() < (ANTENNA_TYPE_LENGTH - (setup.antenna.length() + 4)) ){
            paddingString = paddingString + " ";
        }
        paddingString = paddingString + "NONE";
        Log.i("Padding:",paddingString);

        valuesMap.put("antenna_name_padding",paddingString);
        valuesMap.put("antenna_serial",setup.antenna_code);

        Integer year = startCal.get(Calendar.YEAR);
        Integer yearShort = year % 100; //remainder after dividing by 100
        Integer dayOfYear = startCal.get(Calendar.DAY_OF_YEAR);
        Integer month = startCal.get(Calendar.MONTH);
        Integer dayOfMonth = startCal.get(Calendar.DAY_OF_MONTH);

        valuesMap.put("YYYY", year.toString());
        valuesMap.put("YY", yearShort.toString());
        valuesMap.put("day_of_year", dayOfYear.toString());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        valuesMap.put("MM", monthFormat.format(startDate));
        valuesMap.put("dd", dayOfMonth.toString());
        SimpleDateFormat fullFormat = new SimpleDateFormat("yyyyMMdd");
        //valuesMap.put("YYYYmmdd", year.toString() + month.toString() + dayOfMonth.toString());
        valuesMap.put("YYYYmmdd",fullFormat.format(startDate));

        return valuesMap;
    }

    class Setup implements Cloneable{

        private HashMap<Integer,Observation> observations; //HashMap to store observations
        private int id;
        private int fixedPointId;
        private String fixedPoint;
        private String hsName;
        private int instrumentId;
        private String instrument;
        private int alarmId;
        private String alarm;
        private int antennaId;
        private String antenna;

        public String getAntenna_code() {
            return antenna_code;
        }

        public void setAntenna_code(String antenna_code) {
            this.antenna_code = antenna_code;
        }

        private String antenna_code;
        private String images;  //semicolon delimited string of paths
        private String imageTitles; //semicolon delimited string of titles
        private String imageDescriptions; //semicolon delimited string of descriptions/notes
        private Boolean verticalToARP; //
        private Boolean usingRod;
        private double antennaHeight;
        private double maxDeviation;

        // clone for backup support (not sure if needed)
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public Setup(int id) {
            observations = new HashMap<Integer, Observation>();
            this.id = id;
            setModDate();
        }

        // Method that generates batch string
        public String generateBatchString(String recipe, Map<String, String> valuesMap) {
            StrSubstitutor substitutor = new StrSubstitutor(valuesMap);
            return substitutor.replace(recipe);
        }


        public int getId() {
            return id;
        }
        public Integer getFixedPointId() {
            return fixedPointId;
        }
        public void setFixedPointId(int id) {
            this.fixedPointId = id;
        }

        public String getFixedPoint() {
            return fixedPoint;
        }

        public void setFixedPoint(String fixedPoint) {
            this.fixedPoint = fixedPoint;
            //setModDate();
        }

        public String getHsName() {
            return hsName;
        }

        public void setHsName(String hsName) {
            this.hsName = hsName;
            //setModDate();
        }

        public int getInstrumentId() {
            return instrumentId;
        }
        public void setInstrumentId(int Id){
            this.instrumentId = Id;
        }

        public String getInstrument() {
            return instrument;
        }

        public void setInstrument(String instrument) {
            this.instrument = instrument;
            //setModDate();
        }

        public int getAlarmId() {
            return alarmId;
        }

        public void setAlarmId(int alarmId) {
            this.alarmId = alarmId;
            //setModDate();
        }

        public String getAlarm() {
            return alarm;
        }

        public void setAlarm(String alarm) {
            this.alarm = alarm;
            //setModDate();
        }

        public int getAntennaId() {
            return antennaId;
        }

        public void setAntennaId(int antennaId) {
            this.antennaId = antennaId;
            //setModDate();
        }

        public String getAntenna() {
            return antenna;
        }

        public void setAntenna(String antenna) {
            this.antenna = antenna;
            //setModDate();
        }

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }
        public void setImageTitles(String imageTitles) {
            this.imageTitles = imageTitles;
        }
        public String getImageTitles(){
            return imageTitles;
        }
        public void setImageDescriptions(String imageDescriptions) {
            this.imageDescriptions = imageDescriptions;
        }
        public String getImageDescriptions(){
            return imageDescriptions;
        }


        public Boolean getVerticalToARP() {
            return verticalToARP;
        }

        public void setVerticalToARP(Boolean verticalToARP) {
            this.verticalToARP = verticalToARP;
            //setModDate();
        }

        public Boolean getUsingRod() {
            return usingRod;
        }

        public void setUsingRod(Boolean usingRod) {
            this.usingRod = usingRod;
            //setModDate();
        }

        public double getAntennaHeight() {
            return antennaHeight;
        }

        public void setAntennaHeight(double antennaHeight) {
            this.antennaHeight = antennaHeight;
            //setModDate();
        }

        public double getMaxDeviation() {
            return maxDeviation;
        }

        public void setMaxDeviation(double maxDeviation) {
            this.maxDeviation = maxDeviation;
            //setModDate();
        }
        public void addObservation(Integer id, double measurement){
            Observation obs = new Observation(id, measurement);
                    this.observations.put(id,obs);
            Log.i("ProjectEntry","Observation " + id.toString() + " added");
        }
        public HashMap<Integer, Observation> getObservations(){
            return observations;
        }
        public void addObservation(Observation observation){
            this.observations.put(observation.getId(),observation);
            //setModDate();
        }
        public void deleteObservation(Integer id){
            observations.remove(id);
            //setModDate();
        }
        public Integer getObservationCount() {
            try {
                return observations.size();
            } catch (NullPointerException E) {
                return 0;
            }
        }
        public List<Integer> getObservationIDs(){
            //TODO: getObservationIDs: This is broken at the moment.
//            Log.i("ProjectEntry","getObservationIDs called");
//            List<Integer> IDs = null;
//            Log.i("ProjectEntry getObsID",observations.keySet().toString());
//            String[] ids = new String[] {};
//            ids = observations.keySet().
//            for(String s : observations.entrySet().) IDs.add(Integer.valueOf(s));
//            try{
//                //TODO: This keeps generating a nullpointer exception, even when keySet is populated
//                for(Map.Entry<Integer,Observation> entry : observations.entrySet()){
//                    Integer key = entry.getKey();
//                    IDs.add(IDs.size()+1,key);
//                    Log.i("ProjectEntry","ObservationID added");
//                }
//                return IDs;
//            }catch (NullPointerException E){
//                Log.i("ProjectEntry","ObservationID NullPointerException");
//                return IDs;
//            }
            List <Integer> IDs = new ArrayList<>();
            Log.i("getObservationIDs","start");
            if (!observations.entrySet().isEmpty()){
                Log.i("getObservationIDs","1");
                for(Map.Entry<Integer,Observation> entry : observations.entrySet()){
                    Log.i("getObservationIDs, ID:", entry.getKey().toString());
                    IDs.add(entry.getKey());
                }
                return IDs;
            }else{
                return IDs;
            }

        }

        class Observation{
            private int id;
            private double measurement;
            private int date;
            private String time;  //TODO: should this be integer or string? also make setter and getter
            private int dayNumber;
            private String remark;

            public int getDayNumber() {
                return dayNumber;
            }
            public int getDate() {
                return date;
            }
            public void setDate(int date) {
                this.date = date;
                //setModDate();
                //TODO: set the dayNumber too.
            }
            public String getRemark() {
                return remark;
            }
            public void setRemark(String remark) {
                this.remark = remark;
                //setModDate();
            }
            public int getId() {
                return id;
            }
            public double getMeasurement() {
                return measurement;
            }
            public void setMeasurement(double measurement) {
                this.measurement = measurement;
                //setModDate();
            }

            public Observation(int id, double measurement) {
                this.id = id;
                this.measurement = measurement;
                this.date = GregorianCalendar.DATE;
                this.dayNumber = GregorianCalendar.DAY_OF_YEAR;
                //setModDate();
            }
            public String GenerateBatch(){
                // Placeholder for batch file generation method.
                return "teqc.exe - etc.. etc...";
            }
        }
    }
}
