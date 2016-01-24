package com.ona.test.datasetprocessor;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ona.datasetprocessor.Processor;
import com.ona.datasetprocessor.Result;

public class ProcessorTest {
	Processor processor;

	@BeforeClass
	public void setUp() {
		processor = new Processor();
	}

	@Test
	public void calculateTest() throws Exception{
		String jsonResult=processor.calculate("https://raw.githubusercontent.com/onaio/ona-tech/master/data/water_points.json");
		ObjectMapper mapper = new ObjectMapper();
		Result result=mapper.readValue(jsonResult, Result.class);
		Assert.assertEquals(result.getNumberFunctional(), 623);
		Assert.assertEquals(result.getNumberWaterPoints().size(), 65);//assert no of communities
	}
}
