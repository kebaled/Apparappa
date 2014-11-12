package alex.apparappa;

/**
 * Created by alex on 01/11/14.
 */
public class prodItem {
    String pid = null;
    String name = null;
    String selected = "false";

    public prodItem(String pid, String name, String selected) {
        super();
        this.pid = pid;
        this.name = name;
        this.selected = selected;
    }

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        if(selected.equals("true")) return true;
                else return false;
    }
    public void setSelected(boolean selectedValue) {
        if(selectedValue) this.selected = "true";
        else this.selected = "false";
    }
}
