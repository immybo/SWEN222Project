package editor;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

import view.InformationPanel;
import view.RenderPanel;

public class EditorFrame extends JFrame {
	private RenderPanel canvas;
	private EditPanel editPanel;
	
    public EditorFrame(){
    	this.setPreferredSize(new Dimension(800,800));
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        canvas = new RenderPanel();
        editPanel = new EditPanel(canvas);
        
        this.setPreferredSize(new Dimension(1300,800));
        editPanel.setPreferredSize(new Dimension(500,800));
        canvas.setPreferredSize(new Dimension(800,800));

        this.add(canvas, BorderLayout.CENTER);
        this.add(editPanel, BorderLayout.WEST);

        pack();
    }
    
    public static void main(String[] args){
    	new EditorFrame().show();
    }
}
