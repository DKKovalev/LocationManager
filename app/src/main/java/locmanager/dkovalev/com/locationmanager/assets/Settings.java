package locmanager.dkovalev.com.locationmanager.assets;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "Settings")
public class Settings extends Model {
    @Column(name = "soundSetting")
    public String soundSetting;
    @Column(name = "wirelessSetting")
    public String wirelessSetting;

    public Settings() {
        super();
    }

    public Settings(String wirelessSetting, String soundSetting) {
        this.wirelessSetting = wirelessSetting;
        this.soundSetting = soundSetting;
    }

    @Override
    public String toString() {
        return soundSetting
                + " "
                + wirelessSetting;
    }
}
