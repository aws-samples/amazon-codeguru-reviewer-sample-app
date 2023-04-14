package com.shipmentEvents.handlers;

import java.io.File;
import java.io.FileWriter;

public class TestBean2 {

	public void doBusiness() {
		
		try {
			File file = new File("c:\\temp\\test.txt");
			FileWriter fw = new FileWriter(file);
			fw.write("test");
			//fw.close();
		} catch (Exception ex) {
		}
	}
}
