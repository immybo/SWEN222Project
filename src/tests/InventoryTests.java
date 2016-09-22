package tests;

import org.junit.Test;

import junit.framework.TestCase;
import model.Inventory;
import model.Item;
import util.PointD;

public class InventoryTests extends TestCase {

	@Test
	public void testSmallerStorageCapacity1() {
		Inventory inventory = new Inventory(5);
		Item[] items = new Item[5];
		for(int i = 0; i < items.length; i++){
			items[i] = new TestItem(inventory, 1);
			inventory.addItem(items[i], i);
		}
		
		assertFalse(inventory.changeStorageCapacity(4));
		assertFalse(inventory.changeStorageCapacity(0));
		
		for(int i = 0; i < items.length; i++){
			assertTrue(inventory.containsItem(items[i]));
		}
		assertEquals(5, inventory.getStorageCapacity());
	}
	
	@Test
	public void testSmallerStorageCapacity2(){
		Inventory inventory = new Inventory(5);
		Item[] items = new Item[3];
		for(int i = 0; i < items.length; i++){
			items[i] = new TestItem(inventory, 1);
		}
		inventory.addItem(items[0]);
		inventory.addItem(items[1], 2);
		inventory.addItem(items[2], 4);
		
		assertTrue(inventory.changeStorageCapacity(3));
		assertFalse(inventory.changeStorageCapacity(2));
		
		assertTrue(inventory.containsItem(items[0]));
		assertTrue(inventory.containsItem(items[1]));
		assertTrue(inventory.containsItem(items[2]));
		
		assertEquals(3, inventory.getStorageCapacity());
	}
	
	@Test
	public void testLargerStorageCapaacity(){
		Inventory inventory = new Inventory(0);
		assertTrue(inventory.changeStorageCapacity(1));
		inventory.addItem(new TestItem(inventory, 1));
		assertTrue(inventory.changeStorageCapacity(2));
		assertTrue(inventory.changeStorageCapacity(1));
		assertTrue(inventory.changeStorageCapacity(5));
		
		assertEquals(5, inventory.getStorageCapacity());
	}
	
	@Test
	public void testSameSizeStorageCapacity(){
		Inventory inventory = new Inventory(0);
		assertTrue(inventory.changeStorageCapacity(0));
		assertTrue(inventory.changeStorageCapacity(1));
		inventory.addItem(new TestItem(inventory, 1));
		assertTrue(inventory.changeStorageCapacity(1));
		
		assertEquals(1, inventory.getStorageCapacity());
	}
}
