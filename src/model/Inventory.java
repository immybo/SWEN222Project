package model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.*;

import datastorage.*;

/**
 * Defines something which can contain a certain
 * amount of items. Items can usually be taken out
 * of or put into an inventory.
 *
 * @author Robert Campbell
 */
public class Inventory implements Storable, Serializable {
	private static final long serialVersionUID = 549205869207817829L;
	private int currentNumItems;
	private Item[] items;
	private int storageCapacity;

	/**
	 * Builds a new empty inventory with the given
	 * storage capacity.
	 *
	 * @param storageCapacity The maximum amount of items this inventory can store.
	 */
	public Inventory(int storageCapacity){
		this.storageCapacity = storageCapacity;
		items = new Item[storageCapacity];
		currentNumItems = 0;
	}

	/**
	 * Returns whether or not this inventory has space
	 * for at least one more item.
	 *
	 * @return Whether or not this inventory has space for at least one more item.
	 */
	public boolean hasRoom(){
		return currentNumItems < storageCapacity;
	}

	/**
	 * Attempts to add an item into the next available slot
	 * of this inventory. Does nothing if there is no more
	 * room in this inventory.
	 *
	 * @param item The item to add.
	 * @return Whether or not the item was able to be added.
	 */
	public boolean addItem(Item item){
		if(!hasRoom()) return false;

		return addItem(item, nextAvailableSlot());
	}

	/**
	 * Attempts to add an item into the given slot of this
	 * inventory. Does nothing if that slot is full.
	 *
	 * @param item The item to add to this inventory.
	 * @param index The slot to add the item at.
	 * @return Whether or not adding the given item at the given slot was successful.
	 */
	public boolean addItem(Item item, int index){
		if(index >= storageCapacity || index < 0)
			throw new IllegalArgumentException("Attempting to add an item at an index out of bounds: " + index);

		if(items[index] == null){
			items[index] = item;
			currentNumItems++;
			return true;
		}

		return false;
	}

	/**
	 * Attempts to remove the first instance of a given item
	 * from this inventory.
	 * Returns whether or not the item was found; does nothing
	 * if it wasn't found.
	 *
	 * @param item The item to remove from the inventory.
	 * @return Whether or not the item was found and therefore removed.
	 */
	public boolean removeItem(Item item){
		for(int i = 0; i < items.length; i++){
			if(items[i] == item){
				removeAtIndex(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether or not this inventory contains the given
	 * item.
	 *
	 * @param item The item to check whether or not this inventory contains.
	 * @return Whether or not this inventory contains that item.
	 */
	public boolean containsItem(Item item){
		for(int i = 0; i < items.length; i++){
			if(items[i] == item)
				return true;
		}
		return false;
	}

	/**
	 * Removes all items of the given class from this inventory,
	 * returning how many items were removed.
	 *
	 * @param type The class whose items will be removed.
	 * @return How many items were removed.
	 */
	public int removeAllOfType(Class<? extends Item> type){
		int found = 0;
		for(int i = 0; i< items.length; i++){
			if(items[i].getClass().equals(type)){
				removeAtIndex(i);
				found++;
			}
		}
		return found;
	}

	/**
	 * Returns indices of all items of the given class that are in this inventory.
	 *
	 * @param type The class whose items will be returned.
	 * @return An array of indices of items of the given class.
	 */
	public Integer[] getAllOfType(Class<? extends Item> type){
		List<Integer> elements = new LinkedList<Integer>();
		for(int i = 0; i< items.length; i++){
			if(items[i] != null && items[i].getClass().equals(type)){
				elements.add(i);
			}
		}
		return elements.toArray(new Integer[0]);
	}

	public int getStorageCapacity(){
		return storageCapacity;
	}

	public boolean changeStorageCapacity(int newStorageCapacity){
		// Make sure that we have enough room for the items
		// If we have to, displace some of them
		if(newStorageCapacity < storageCapacity){
			if(currentNumItems > newStorageCapacity) return false;

			for(int i = newStorageCapacity; i < storageCapacity; i++){
				if(items[i] != null){
					Item temp = items[i];
					removeAtIndex(i);
					addItem(temp);
				}
			}

			// No need to resize the array if we don't have memory
			// concerns
		}
		// Copy it over to a new, larger array if it's getting larger
		else if(newStorageCapacity > storageCapacity){
			Item[] newItems = new Item[newStorageCapacity];
			for(int i = 0; i < storageCapacity; i++)
				newItems[i] = items[i];
			items = newItems;
		}
		// No need for a case where we're updating to an equal capacity

		storageCapacity = newStorageCapacity;
		return true;
	}

	/**
	 * @return The position of the given item in this inventory, or -1 if it isn't contained.
	 */
	public int indexOf(Item item){
		for(int i = 0; i < storageCapacity; i++){
			if(items[i] == item) return i;
		}
		return -1;
	}

	public Item getItem(int index){
		if(index < 0 || index > items.length)
			throw new IllegalArgumentException("Attempting to get an item from an inventory at an invalid index.");
		return items[index];
	}

	/**
	 * @return An array of all items in this inventory, with null denoting no item at that index.
	 */
	public Item[] getItems(){
		return items.clone();
	}

	private void removeAtIndex(int index){
		if(index < 0 || index > items.length)
			throw new IllegalArgumentException("Attempting to remove an item from an inventory at an invalid index.");

		items[index] = null;
		currentNumItems--;
	}

	private int nextAvailableSlot(){
		if(!hasRoom()) return -1;

		for(int i = 0; i < items.length; i++)
			if(items[i] == null)
				return i;

		throw new IllegalStateException("No next available slot in an inventory that still has room");
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof Inventory){
			Inventory i = (Inventory) o;
			if(this.currentNumItems == i.currentNumItems && this.storageCapacity == i.storageCapacity
					&& Arrays.equals(this.items, i.items)){
				return true;
			}
		}
		return false;
	}

	@Override
	public Element toXMLElement(Document doc) {
		Element elem = doc.createElement("inventory");
		elem.setAttribute("capacity", this.getStorageCapacity()+"");

		for(int i = 0; i < items.length; i++){
			if(items[i] != null){
				Element child = items[i].toXMLElement(doc);
				child.setAttribute("index", i+"");
				elem.appendChild(child);
			}
		}

		return elem;
	}

	public static class Factory implements StorableFactory<Inventory> {
		@Override
		public Inventory fromXMLElement(Element elem) {
			int capacity = Integer.parseInt(elem.getAttribute("capacity"));
			Item[] items = new Item[capacity];

			NodeList nl = elem.getChildNodes();
			for(int i = 0; i < nl.getLength(); i++){
				Node n = nl.item(i);

				Item item = new Item.Factory().fromNode(n);

				if(item == null)
					throw new XMLParseException("Invalid item class.");

				int index = Integer.parseInt(((Element)n).getAttribute("index"));

				items[index] = item;
			}

			Inventory inventory = new Inventory(capacity);
			for(int i = 0; i < items.length; i++){
				if(items[i] != null){
					inventory.addItem(items[i], i);
				}
			}

			return inventory;
		}
	}
}
