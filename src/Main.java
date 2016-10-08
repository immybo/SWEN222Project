import model.World;
import network.client.Client;
import view.GameFrame;

public class Main {
    public static void main(String[] args){
        GameFrame g = GameFrame.instance();
        g.getRenderPanel().setZone(World.testWorld().getZones()[0]);
        g.setVisible(true);
    }
}
