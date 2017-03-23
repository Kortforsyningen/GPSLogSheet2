package ref.sdfe.gpslogsheet2;

/**
 * Created by B028406 on 23-03-2017.
 */

public class RodEntry{
    public int rod_id;
    public String rod_name;
    public double rod_length; //in meters

    // constructor
    public RodEntry(int rod_id, String rod_name, double rod_length){
        this.rod_id = rod_id;
        this.rod_name = rod_name;
        this.rod_length = rod_length;
    }
    // get and set ID
    public int getID(){
        return this.rod_id;
    }
    public void setID(int id) {
        this.rod_id = id;
    }
    // get and set name
    public String getName(){
        return this.rod_name;
    }
    public void setName(String name) {
        this.rod_name = name;
    }
    // get and set length
    public double getLength(){
        return this.rod_length;
    }
    public void setLength(double length) {
        this.rod_length = length;
    }

}