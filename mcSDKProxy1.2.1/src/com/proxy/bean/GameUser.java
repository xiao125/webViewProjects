package com.proxy.bean;

public class GameUser {
	
	private String username;
	private String factionName;
	
	private String uid;
	private String roleId;
	private int serverId;
	private String vipLevel;
	private String serverName;
	private boolean isNewRole = false;
	private String roleCTime;
	private int userLevel=1;
	
	private String extraInfo;
	
	private String scene_id ;
	private String balance ;
	
	public void setScene_id( String  scene_id ){
		
		this.scene_id = scene_id ; 
		
	}
	
	public void setBalance( String  balance ){
		
		this.balance = balance ; 
		
	}
	
	public String getScene_id(){
		
		return scene_id;
		
	}
	
	public String getBalance(){
		
		return balance;
		
	}
	
	public String getRoleCTime() {
		return roleCTime;
	}
	
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public String getVipLevel() {
		return vipLevel;
	}
	public void setVipLevel(String vipLevel) {
		this.vipLevel = vipLevel;
	}
	public String getFactionName() {
		return factionName;
	}
	public void setFactionName(String factionName) {
		this.factionName = factionName;
	}
	public int getUserLevel() {
		return userLevel;
	}
	public void setRoleCTime(String rCTime) {
		this.roleCTime = rCTime;
	}
	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public boolean isNewRole() {
		return isNewRole;
	}
	public void setNewRole(boolean isNewRole) {
		this.isNewRole = isNewRole;
	}
	
}
