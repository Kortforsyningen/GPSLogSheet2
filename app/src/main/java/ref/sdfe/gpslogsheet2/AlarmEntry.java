package ref.sdfe.gpslogsheet2;

/**
 * Created by B028406 on 23-03-2017.
 */

public class AlarmEntry{
    public int alarm_id;
    public String alarm_name;

    // constructor
    public AlarmEntry(int alarm_id, String alarm_name){
        this.alarm_id = alarm_id;
        this.alarm_name = alarm_name;
    }
    // get and set ID
    public int getID(){
        return this.alarm_id;
    }
    public void setID(int id) {
        this.alarm_id = id;
    }
    // get and set name
    public String getName(){
        return this.alarm_name;
    }
    public void setName(String name) {
        this.alarm_name = name;
    }
    @Override
    public String toString() {
        return getName();
    }
}