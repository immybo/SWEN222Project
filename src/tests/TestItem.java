package tests;

import model.*;
import util.*;

public class TestItem extends Item {
	public TestItem(Zone zone, PointD worldPosition, double size){
		super(zone, worldPosition, size);
	}
	
	public TestItem(Inventory inventory, double size){
		super(inventory, size);
	}
}
