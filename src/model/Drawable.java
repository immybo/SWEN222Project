package model;

import java.io.Serializable;

public abstract class Drawable implements Serializable {
	private String drawID;

	public String getDrawID() {
		return drawID;
	}

	public void setDrawID(String drawID) {
		this.drawID = drawID;
	}
	
	
}
