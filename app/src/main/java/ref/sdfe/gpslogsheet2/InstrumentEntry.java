package ref.sdfe.gpslogsheet2;

/**
 * Created by B028406 on 23-03-2017.
 */

public class InstrumentEntry{
    public int instrument_id;
    public String instrument_name;

    // constructor
    public InstrumentEntry(int instrument_id, String instrument_name){
        this.instrument_id = instrument_id;
        this.instrument_name = instrument_name;
    }
    // get and set ID
    public int getID(){
        return this.instrument_id;
    }
    public void setID(int id) {
        this.instrument_id = id;
    }
    // get and set name
    public String getName(){
        return this.instrument_name;
    }
    public void setName(String name) {
        this.instrument_name = name;
    }
}