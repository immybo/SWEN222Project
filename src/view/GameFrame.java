package view;

import javax.swing.*;

import model.World;
import model.Zone;
import network.NetworkError;
import network.client.Client;

import java.awt.*;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * The main frame, which contains all of the components for
 * the actual game.
 *
 * @author Robert Campbell
 */
public class GameFrame extends JFrame {
	private static GameFrame frame;
	
    private static final int DEFAULT_WIDTH = 3000;
    private static final int DEFAULT_HEIGHT = 3000;

    private static final double INFORMATION_PANEL_WIDTH = 0.3;

    // Shows the actual game map, entities, etc.
    private RenderPanel canvas;
    // Shows the player information about the current game -
    // For example, their inventory, health
    private InformationPanel informationPanel;

    private GameFrame(){
        setSizeDefault();
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        canvas = new RenderPanel(this);
        informationPanel = new InformationPanel(this);

        this.add(canvas, BorderLayout.WEST);
        this.add(informationPanel, BorderLayout.EAST);

        pack();
    }

    /**
     * Makes sure that all components are at reasonable sizes for
     * the current size of the frame.
     */
    @Override
    public void pack(){
    	Dimension prefSize = this.getPreferredSize();
        informationPanel.setPreferredSize(new Dimension(
                (int)(prefSize.getWidth()*INFORMATION_PANEL_WIDTH), (int)prefSize.getHeight()
        ));
        canvas.setPreferredSize(new Dimension(
                (int)(prefSize.getWidth()*(1-INFORMATION_PANEL_WIDTH)), (int)prefSize.getHeight()
        ));

        // The default pack method just changes the actual sizes to match their
        // preferred sizes; we add onto it so that everything has a reasonable
        // preferred size considering the size of the frame.
        super.pack();
    }
    
    /**
     * Sets the size of this frame to be a reasonable default.
     */
    private void setSizeDefault(){
    	Dimension screenSize = getScreenSize();
    	int width = DEFAULT_WIDTH;
    	if(width > screenSize.getWidth() * 0.9)
    		width = (int)(screenSize.getWidth() * 0.9);
    	int height = DEFAULT_HEIGHT;
    	if(height > screenSize.getHeight() * 0.9)
    		height = (int)(screenSize.getHeight() * 0.9);
    	
    	if(width > 1024){
    		width = 1024;
    		height = (int)(1024 * ((double)screenSize.getHeight() / screenSize.getWidth()));
    	}
    	
    	this.setPreferredSize(new Dimension(width, height));
    }

    private Dimension getScreenSize(){
        GraphicsDevice screen = GraphicsEnvironment
                                .getLocalGraphicsEnvironment()
                                .getDefaultScreenDevice();
        int width = screen.getDisplayMode().getWidth();
        int height = screen.getDisplayMode().getHeight();
        return new Dimension(width, height);
    }
    
    public RenderPanel getRenderPanel() {
    	return canvas;
    }
    
    public int displayBooleanQuestion(String title, String text){
    	String[] options = { "Yes" , "No" };
    	return JOptionPane.showOptionDialog(this, text, title, JOptionPane.DEFAULT_OPTION,
    										JOptionPane.QUESTION_MESSAGE, null, options, 1);
    }
    
    public void displayMessage(String text){
    	JOptionPane.showMessageDialog(this, text);
    }
	
	public void connect(String hostname) throws NetworkError {
		Client client = new Client(this, hostname);
		informationPanel.setClient(client);
		getRenderPanel().attachToClient(client);
		client.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				client.disconnect();
				showNetworkErrorBox(e);
			}
		});
		client.run();
	}
	
	public void showNetworkErrorBox(Throwable e) {
			JOptionPane.showMessageDialog(this.getParent(), "Network Error: "+e.getMessage(),
				"Error", JOptionPane.OK_OPTION);
	}
    
    public static GameFrame instance(){
    	if(frame != null){
    		return frame;
    	}
    	return new GameFrame();
    }
}