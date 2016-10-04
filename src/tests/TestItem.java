package tests;

import java.awt.Point;

import model.*;
import util.*;

public class TestItem extends Item {
	public TestItem(Point worldPosition){
		super(worldPosition, false);
	}
	
	public TestItem(){
		super(false);
	}
}
