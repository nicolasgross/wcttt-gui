package de.nicolasgross.wcttt.gui.controller;

public class TreeViewItemWrapper<T> {

	private T item;

	public TreeViewItemWrapper(T item) {
		this.item = item;
	}

	public T getItem() {
		return item;
	}

	public void setItem(T item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return item.toString();
	}
}
