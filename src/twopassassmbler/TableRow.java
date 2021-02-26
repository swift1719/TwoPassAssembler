/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twopassassmbler;

/**
 *
 * @author Ayush
 */

public class TableRow {
String symbol;
int address,index;
public String getSymbol() {
	return symbol;
}
public TableRow(String symbol, int address) {
	super();
	this.symbol = symbol;
	this.address = address;
	index=0;
}
public void setSymbol(String symbol) {
	this.symbol = symbol;
}
public TableRow(String symbol, int address, int index) {
	super();
	this.symbol = symbol;
	this.address = address;
	this.index = index;
}
public int getAddress() {
	return address;
}
public void setAddess(int address) {
	this.address = address;
}
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
}