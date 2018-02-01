package ref.sdfe.gpslogsheet2;


import android.util.Log;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by B028406 on 01-06-2017.
 */

class ProjectEntry implements Cloneable{
    private int id;
    private String name;
    private String operator;
    private long startDate;
    private long endDate;
    private long modDate;
    private HashMap<Integer,Setup> setups; //HasMap to store setups

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

            Log.i("ProjectEntry","getObservationIDs called");
            List<Integer> IDs = null;
            Log.i("ProjectEntry getObsID",observations.keySet().toString());
            try{
                for(Map.Entry<Integer,Observation> entry : observations.entrySet()){
                    Integer key = entry.getKey();
                    IDs.add(key);
                    Log.i("ProjectEntry","ObservationID added");
                }
                return IDs;
            }catch (NullPointerException E){
                Log.i("ProjectEntry","ObservationID NullPointerException");
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
