package view;

import javax.swing.*;

import model.World;
import model.Zone;
import network.NetworkError;
import network.client.Client;

import java.awt.*;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * The main frame, which contains all of the components for
 * the actual game. This frame is a singleton; GameFrame.instance()
 * should be called to get the instance.
 *
 * @author Robert Campbell
 */
public class GameFrame extends JFrame {
	// The sole instance of GameFrame in the program
	private static GameFrame frame;
	
	// If the screen is big enough, the frame width and height
	// will initially be set to these values
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 768;

    // As a proportion of the frame's total width
    private static final double INFORMATION_PANEL_WIDTH = 0.3;

    // Shows the actual game map, entities, etc.
    private RenderPanel canvas;
    // Shows the player information about the current game -
    // For example, their inventory, health
    private InformationPanel informationPanel;
	private Client client;

    /**
     * Internal constructor for GameFrame.
     */
    private GameFrame(){
        setSizeDefault();
        this.setTitle("The Adventures of Pupo and Yelo");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        canvas = new RenderPanel();
        informationPanel = new InformationPanel(this);

        this.add(canvas, BorderLayout.CENTER);
        this.add(informationPanel, BorderLayout.EAST);
        
        ImageIcon img = new ImageIcon("images/icon.png");
        this.setIconImage(img.getImage());

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
     * This size will never be bigger than the screen size.
     */
    private void setSizeDefault(){
    	Dimension screenSize = getScreenSize();
    	int width = DEFAULT_WIDTH;
    	if(width > screenSize.getWidth() * 0.7)
    		width = (int)(screenSize.getWidth() * 0.7);
    	int height = DEFAULT_HEIGHT;
    	if(height > screenSize.getHeight() * 0.7)
    		height = (int)(screenSize.getHeight() * 0.7);
    	
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
    
    /**
     * Displays a dialog box with boolean options
     * to the user.
     * 
     * @param title The title of the dialog box.
     * @param text The text to be displayed in the dialog box.
     * @return 0 if yes was selected; 1 if no was selected or the window was closed.
     */
    public int displayBooleanQuestion(String title, String text){
    	String[] options = { "Yes" , "No" };
    	return JOptionPane.showOptionDialog(this, text, title, JOptionPane.DEFAULT_OPTION,
    										JOptionPane.QUESTION_MESSAGE, null, options, 1);
    }
    
    /**
     * Displays a message box to the user.
     * 
     * @param text The text to be displayed in the dialog box.
     */
    public void displayMessage(String text){
    	JOptionPane.showMessageDialog(this, text);
    }
	
    /**
     * Attempts to connect this client to a given
     * hostname.
     * @param hostname
     * @throws NetworkError
     */
	public void connect(String hostname) throws NetworkError {
		if (client != null) {
			client.disconnect();
		}
		client = new Client(hostname);
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
	
	/**
	 * Displays a dialog explaining a network error, 
	 * the cause of which is the given throwable.
	 * 
	 * @param e The cause of the network error.
	 */
	public void showNetworkErrorBox(Throwable e) {
			showMessageBox("Network Error: "+e.getMessage());
	}
    
	/**
	 * Show a message box popup with the given message
	 * @param message -- message to show
	 */
	public void showMessageBox(String message) {
		JOptionPane.showMessageDialog(this.getParent(), message,
				"Game", JOptionPane.OK_OPTION);
	}
	
	/**
	 * Gets the current instance of GameFrame,
	 * or creates a new one if no instance is
	 * present (i.e. on the first call to this
	 * method in the program.
	 * 
	 * A GameFrame should only be gotten through
	 * this method.
	 * 
	 * @return The instance of GameFrame.
	 */
    public static GameFrame instance(){
    	if(frame != null){
    		return frame;
    	}
    	return new GameFrame();
    }
}
