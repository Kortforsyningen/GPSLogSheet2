package ref.sdfe.gpslogsheet2;

/**
 * Created by B028406 on 23-03-2017.
 */

public class AntennaEntry{
    public int antenna_id;
    public String antenna_name;

    // constructor
    public AntennaEntry(int antenna_id, String antenna_name){
        this.antenna_id = antenna_id;
        this.antenna_name = antenna_name;
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
}