package ref.sdfe.gpslogsheet2;


import com.google.gson.Gson;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by B028406 on 01-06-2017.
 */

class ProjectEntry {
    private int id;
    private String name;
    private String operator;
    private int startDate;
    private int endDate;
    private int modDate;

    public ProjectEntry(int id) {
        /*
        * This constructor automatically gets current date
        * */
        this.id = id;
        this.startDate = GregorianCalendar.DATE;
    }

    public String getJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public int getModDate() {
        return modDate;
    }
    public void setModDate(int modDate) {
        this.modDate = modDate;
    }
    public int getId() {
        return id;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public int getStartDate() {
        return startDate;
    }
    public int getEndDate() {
        return endDate;
    }
    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



    class Setup{
        private int id;

        public String getFixedPoint() {
            return fixedPoint;
        }

        public void setFixedPoint(String fixedPoint) {
            this.fixedPoint = fixedPoint;
        }

        private String fixedPoint;

        public String getHsName() {
            return hsName;
        }

        public void setHsName(String hsName) {
            this.hsName = hsName;
        }

        private String hsName;

        public int getInstrumentId() {
            return instrumentId;
        }

        private int instrumentId;

        public String getInstrument() {
            return instrument;
        }

        public void setInstrument(String instrument) {
            this.instrument = instrument;
        }

        private String instrument;

        public int getAlarmId() {
            return alarmId;
        }

        public void setAlarmId(int alarmId) {
            this.alarmId = alarmId;
        }

        private int alarmId;

        public String getAlarm() {
            return alarm;
        }

        public void setAlarm(String alarm) {
            this.alarm = alarm;
        }

        private String alarm;

        public int getAntennaId() {
            return antennaId;
        }

        public void setAntennaId(int antennaId) {
            this.antennaId = antennaId;
        }

        private int antennaId;

        public String getAntenna() {
            return antenna;
        }

        public void setAntenna(String antenna) {
            this.antenna = antenna;
        }

        private String antenna;

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }

        private String images;

        public Boolean getVerticalToARP() {
            return verticalToARP;
        }

        public void setVerticalToARP(Boolean verticalToARP) {
            this.verticalToARP = verticalToARP;
        }

        private Boolean verticalToARP;

        public Boolean getUsingRod() {
            return usingRod;
        }

        public void setUsingRod(Boolean usingRod) {
            this.usingRod = usingRod;
        }

        private Boolean usingRod;

        public double getAntennaHeight() {
            return antennaHeight;
        }

        public void setAntennaHeight(double antennaHeight) {
            this.antennaHeight = antennaHeight;
        }

        private double antennaHeight;

        public double getMaxDeviation() {
            return maxDeviation;
        }

        public void setMaxDeviation(double maxDeviation) {
            this.maxDeviation = maxDeviation;
        }

        private double maxDeviation;

        public Setup(int id) {
            this.id = id;
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
                //TODO: set the dayNumber too.
            }
            public String getRemark() {
                return remark;
            }
            public void setRemark(String remark) {
                this.remark = remark;
            }
            public int getId() {
                return id;
            }
            public double getMeasurement() {
                return measurement;
            }
            public void setMeasurement(double measurement) {
                this.measurement = measurement;
            }

            public Observation(int id) {
                this.id = id;
                this.date = GregorianCalendar.DATE;
                this.dayNumber = GregorianCalendar.DAY_OF_YEAR;
            }
        }
    }
}
