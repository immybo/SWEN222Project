import model.World;
import view.GameFrame;


public class LocalClient {
    public static void main(String[] args){
        GameFrame g = GameFrame.instance();
        g.getRenderPanel().setZone(World.firstLevel().getZones()[0]);
        g.setVisible(true);
        g.connect("localhost");
    }
}
