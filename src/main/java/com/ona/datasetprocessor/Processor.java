package com.ona.datasetprocessor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Processor {
	final static Logger logger = Logger.getLogger(Processor.class);
	final static String JSON_FEED_COMMUNITY = "communities_villages";
	final static String JSON_FEED_WATER_FUNCTIONING = "water_functioning";

	public String calculate(String URL) throws Exception {
		Map<String, List<String>> communitiesWaterPoints = processJSONFeed(readJSONFeed(URL));
		Result result = new Result();
		int functional = 0;
		Map<String, Double> communityBrokenWaterPoints = new HashMap<String, Double>();
		for (Map.Entry<String, List<String>> entry : communitiesWaterPoints.entrySet()) {
			String community = entry.getKey();
			List<String> waterPointsStatus = entry.getValue();

			Community com = new Community();
			com.setName(community);
			com.setWaterPoints(waterPointsStatus.size());
			result.getNumberWaterPoints().add(com);

			Double nonfunctional = 0d;
			// get functional water points number
			for (String status : waterPointsStatus) {
				if (status.equalsIgnoreCase("yes")) {
					functional++;
				} else {
					nonfunctional++;
				}
			}
			if (nonfunctional == 0) {
				communityBrokenWaterPoints.put(community, 0d);
			} else {
				Double percentNonFunctional = 0d;
				percentNonFunctional = round(nonfunctional / (waterPointsStatus.size()), 2) * 100;
				communityBrokenWaterPoints.put(community, percentNonFunctional);
			}

		}
		// prepare function final result
		result.setNumberFunctional(functional);
		//populate the community ranking object
		Map<String, Double> brokenWaterPoints = sortByValue(communityBrokenWaterPoints);
		communityRankings(result, brokenWaterPoints);
		// convert object to json
		ObjectMapper mapper = new ObjectMapper();
		String jsonResult = mapper.writeValueAsString(result);
		logger.info(jsonResult);
		return jsonResult;
	}

	private void communityRankings(Result result, Map<String, Double> brokenWaterPoints) {
		if (brokenWaterPoints.isEmpty() || result == null) {
			return;
		}
		for (Map.Entry<String, Double> entry : brokenWaterPoints.entrySet()) {
			CommunityBrokenPoints cbp = new CommunityBrokenPoints();
			cbp.setBrokenWaterPoints(entry.getValue());
			cbp.setName(entry.getKey());
			result.getCommunity_ranking().add(cbp);
		}
	}

	private JSONArray readJSONFeed(String URL) throws Exception {
		JSONArray array = null;
		try {

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(URL);
			request.addHeader("accept", "application/json");
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				String json = IOUtils.toString(response.getEntity().getContent());
				array = new JSONArray(json);

			} else {
				logger.debug("Failed to download data");
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return array;
	}

	private Map<String, List<String>> processJSONFeed(JSONArray jsonFeed) {
		Map<String, List<String>> communities = new HashMap<String, List<String>>();
		// communities_villages
		if (jsonFeed == null || jsonFeed.length() == 0) {
			return communities;
		}

		for (int i = 0; i < jsonFeed.length(); i++) {
			JSONObject object = jsonFeed.getJSONObject(i);

			String community = object.getString(JSON_FEED_COMMUNITY) != null ? object.getString(JSON_FEED_COMMUNITY) : "";
			String status = object.getString(JSON_FEED_WATER_FUNCTIONING) != null ? object.getString(JSON_FEED_WATER_FUNCTIONING) : "";

			if (!community.isEmpty() && !status.isEmpty()) {
				List<String> statuses = new ArrayList<String>();
				if (communities.isEmpty()) {
					statuses.add(status);
					communities.put(community, statuses);
				} else {

					if (communities.containsKey(community)) {// community
																// already added
						statuses = communities.get(community);
						statuses.add(status);
					} else {
						// new community
						statuses.add(status);
						communities.put(community, statuses);
					}

				}

				logger.info("communities_villages ---> " + community);
				logger.info("water_functioning ---> " + status);
			}
		}
		return communities;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private Map<String, Double> sortByValue(Map<String, Double> map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			@SuppressWarnings("rawtypes")
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
