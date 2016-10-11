package view;

import javax.imageio.ImageIO;
import javax.swing.*;

import model.World;
import model.Zone;
import network.NetworkError;
import network.client.Client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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
	
	private JPanel splashScreenPanel;
	private boolean allConnected;

    /**
     * Internal constructor for GameFrame.
     */
    private GameFrame(){
    	allConnected = false;
    	
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
		setSizeDefault();
		
        this.setTitle("The Adventures of Pupo and Yelo");
        
        ImageIcon img = new ImageIcon("images/pupoIcon.png");
        this.setIconImage(img.getImage());

        informationPanel = new InformationPanel(this);
        this.add(informationPanel, BorderLayout.EAST);
        
        canvas = new RenderPanel();
    	
    	try {
			splashScreenPanel = new JPanel(){
				private final BufferedImage image = ImageIO.read(new File("images/splashScreen.png"));
				
				@Override
				public void paint(Graphics g){
					super.paint(g);
					g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
					
					g.setColor(Color.WHITE);
					
					String drawString = "";
					if(client == null){
						drawString = "Please connect to a server.";
					} else {
						drawString = "Waiting for other player.";
					}
					
					g.setFont(new Font("Arial", Font.BOLD, 32));
					
					int width = this.getWidth();
					int height = this.getHeight();

					int stringWidth = g.getFontMetrics().stringWidth(drawString);
					int stringX = (width - stringWidth) / 2;
					int stringY = height - 50;
					
					g.drawString(drawString, stringX, stringY);
				}
			};
			
	    	this.add(splashScreenPanel, BorderLayout.CENTER);
	    	pack();
	    	
		} catch (IOException e) {
			initialiseFrameForGame();
		}
    }
    
    public void setAllConnected(){
    	if(!allConnected){
    		allConnected = true;
	    	initialiseFrameForGame();
    	}
    }
    
    private void initialiseFrameForGame(){
    	splashScreenPanel.setVisible(false);
    	this.remove(splashScreenPanel);
        this.add(canvas, BorderLayout.CENTER);
    	canvas.setVisible(true);
        pack();
        repaint();
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
        
        splashScreenPanel.setPreferredSize(new Dimension(
                (int)(prefSize.getWidth()*(1-INFORMATION_PANEL_WIDTH)), (int)prefSize.getHeight()
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
		client = new Client(this, hostname);
		informationPanel.setClient(client);
		getRenderPanel().attachToClient(client);
		client.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				client.disconnect();
				showNetworkErrorBox(e);
			}
		});
		client.run();
		repaint();
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
