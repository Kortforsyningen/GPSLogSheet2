package ref.sdfe.gpslogsheet2;

/**
 * Created by B028406 on 23-03-2017.
 */

public class AntennaEntry{
    public int antenna_id;
    public String antenna_name;
    public String antenna_code;

    // constructor
    public AntennaEntry(int antenna_id, String antenna_name, String antenna_code){
        this.antenna_id = antenna_id;
        this.antenna_name = antenna_name;
        this.antenna_code = antenna_code;
    }
    // get and set ID
    public int getID(){
        return this.antenna_id;
    }
    public void setID(int id) {
        this.antenna_id = id;
    }
    // get and set name
    public String getName(){
        return this.antenna_name;
    }
    public void setName(String name) {
        this.antenna_name = name;
    }
    public String getCode(){
        return this.antenna_code;
    }
    public void setCode(String code) {
        this.antenna_code = code;
    }
}