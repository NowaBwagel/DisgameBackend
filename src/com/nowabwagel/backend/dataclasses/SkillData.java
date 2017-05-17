package com.nowabwagel.backend.dataclasses;

import java.io.Serializable;

public class SkillData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8031686351609645155L;

	private final SkillID skillId;
	private int level;
	private float outstandingExp;

	public SkillData(SkillID skillId, int level, float outstandingExp) {
		super();
		this.skillId = skillId;
		this.level = level;
		this.outstandingExp = outstandingExp;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getOutstandingExp() {
		return outstandingExp;
	}

	public void setOutstandingExp(float outstandingExp) {
		this.outstandingExp = outstandingExp;
	}

	public SkillID getSkillId() {
		return skillId;
	}

}
