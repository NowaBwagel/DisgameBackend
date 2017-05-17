package com.nowabwagel.backend.dataclasses;

public enum SkillID {
	WOODCUTTING(0);

	private int id;
	private static SkillID[] skillList;
	static {
		skillList = SkillID.values();
	}

	SkillID(int id) {
		this.id = id;
	}

	public static SkillID getSkillFromID(int id) throws IllegalArgumentException {
		for (SkillID skill : skillList) {
			if (id == skill.getId())
				return skill;
		}
		throw new IllegalArgumentException("Id not found");

	}

	public int getId() {
		return id;
	}
}
