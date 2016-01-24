package com.ona.datasetprocessor;

import java.util.ArrayList;
import java.util.List;

public class Result {
	private int numberFunctional;
	private List<Community> numberWaterPoints = new ArrayList<Community>();
	private List<CommunityBrokenPoints> community_ranking = new ArrayList<CommunityBrokenPoints>();

	public List<CommunityBrokenPoints> getCommunity_ranking() {
		return community_ranking;
	}

	public void setCommunity_ranking(List<CommunityBrokenPoints> community_ranking) {
		this.community_ranking = community_ranking;
	}

	public List<Community> getNumberWaterPoints() {
		return numberWaterPoints;
	}

	public void setNumberWaterPoints(List<Community> numberWaterPoints) {
		this.numberWaterPoints = numberWaterPoints;
	}

	public int getNumberFunctional() {
		return numberFunctional;
	}

	public void setNumberFunctional(int numberFunctional) {
		this.numberFunctional = numberFunctional;
	}
}
