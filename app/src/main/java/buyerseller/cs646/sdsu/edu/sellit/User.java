package buyerseller.cs646.sdsu.edu.sellit;

import java.io.Serializable;

/**
 * Created by AnuragG on 28-Apr-17.
 */

public class User implements Serializable {
    public String uid;
    public String email;
    public String address;
    public String password;
    public String name;
    public Double latitude;
    public Double longitude;
    public long phone;

    public User(){
    }

    public User(String uid, String email, String address, String password, String name, Double latitude, Double longitude, long phone) {
        this.uid = uid;
        this.email = email;
        this.address = address;
        this.password = password;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
    }
}
