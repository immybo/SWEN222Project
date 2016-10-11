import model.World;
import view.GameFrame;


public class LocalClient {
    public static void main(String[] args){
        GameFrame g = GameFrame.instance();
        g.setVisible(true);
        g.connect("localhost");
    }
}
