package com.ona.datasetprocessor;

import org.apache.log4j.Logger;

public class Main {
	final static String JSON_FEED_SOURCE = "https://raw.githubusercontent.com/onaio/ona-tech/master/data/water_points.json";
	final static Logger logger = Logger.getLogger(Main.class);
	public static void main(String[] args) {
		try {
			Processor processor = new Processor();
			String result=processor.calculate(JSON_FEED_SOURCE);
			System.out.println(" \n\n\n\n RESULT IS : \n\n\n\n"+result);
		} catch (Exception e) {
			logger.error(e);
		}

	}

}
