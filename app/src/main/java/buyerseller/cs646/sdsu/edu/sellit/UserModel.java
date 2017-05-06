package buyerseller.cs646.sdsu.edu.sellit;

import java.io.Serializable;

/**
 * Created by AnuragG on 28-Apr-17.
 */

public class UserModel implements Serializable {
    public String uid;
    public String email;
    public String address;
    public String password;
    public String name;
    public Double latitude;
    public Double longitude;
    public String phone;

    public UserModel(){
    }
}
