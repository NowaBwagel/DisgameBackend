package com.nowabwagel.backend.dataclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Character implements Serializable{

	private static final long serialVersionUID = -2425414887984498553L;

	private String name;
	private ArrayList<String> clanIds;
	private HashMap<String, SkillData> skills;
	
	public Character(String name){
		this.name = name;
		this.clanIds = new ArrayList<String>();
		this.skills = new HashMap<String, SkillData>();
	}

}
