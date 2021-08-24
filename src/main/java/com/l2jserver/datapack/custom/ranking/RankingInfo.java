package com.l2jserver.datapack.custom.ranking;

public class RankingInfo {

	public enum RankingType {
		ALLTIMEPVP, WEEKLYPVP, MONTHLYPVP, ALLTIMEPK, WEEKLYPK, MONTHLYPK, ALLTIMEASSIST, WEEKLYASSIST, MONTHLYASSIST, ALLTIMESCORE, WEEKLYSCORE, MONTHLYSCORE
	}

	public enum Type {
		PLAYER, CLAN
	}

	private int id;
	private String name;
	private Type type;
	private RankingType rankingType;
	private int count;

	public RankingInfo(int id, String name, Type type, RankingType rankingType, int count) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.rankingType = rankingType;
		this.count = count;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public RankingType getRankingType() {
		return rankingType;
	}

	public void setRankingType(RankingType rankingType) {
		this.rankingType = rankingType;
	}

}
