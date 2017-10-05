package ref.sdfe.gpslogsheet2;

/**
 * Created by B028406 on 23-03-2017.
 */

public class FixedpointEntry{
    public int fixedpoint_id;
    public String fixedpoint_gps_name;
    public String fixedpoint_hs_name;
    public double fixedpoint_easting;
    public double fixedpoint_northing;

    // constructor
    public FixedpointEntry(int fixedpoint_id, String gps_name, String hs_name,
                           double easting, double northing){
        this.fixedpoint_id = fixedpoint_id;
        this.fixedpoint_gps_name = gps_name;
        this.fixedpoint_hs_name = hs_name;
        this.fixedpoint_easting = easting;
        this.fixedpoint_northing = northing;
    }
    // get and set ID
    public int getID(){
        return this.fixedpoint_id;
    }
    public void setID(int id) {
        this.fixedpoint_id = id;
    }
    // get and set gps name
    public String getGPSName(){
        return this.fixedpoint_gps_name;
    }
    public void setGPSName(String name) {
        this.fixedpoint_gps_name = name;
    }
    // get and set hs name
    public String getHSName(){
        return this.fixedpoint_hs_name;
    }
    public void setHSName(String name) {
        this.fixedpoint_hs_name = name;
    }
    // get and set easting
    public double getEasting(){
        return this.fixedpoint_easting;
    }
    public void setEasting(double easting) {
        this.fixedpoint_easting = easting;
    }
    // get and set northing
    public double getNorthing(){
        return this.fixedpoint_northing;
    }
    public void setNorthing(double northing) {
        this.fixedpoint_northing = northing;
    }
    @Override
    public String toString() {
        return getGPSName();
    }
}