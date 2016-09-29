import model.World;
import view.GameFrame;

public class Main {
    public static void main(String[] args){
        GameFrame g = new GameFrame();
        g.setZone(World.testWorld().getZones()[0]);
        g.show();
        
        
    }
    
    
}
