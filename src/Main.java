import model.World;
import network.Client;
import view.GameFrame;

public class Main {
    public static void main(String[] args){
        GameFrame g = new GameFrame();
        g.getRenderPanel().setZone(World.testWorld().getZones()[0]);
        g.setVisible(true);
    }
}
