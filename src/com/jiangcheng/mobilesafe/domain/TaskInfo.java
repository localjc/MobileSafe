package com.jiangcheng.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/*
 * 进程信息的业务Bean
 */
public class TaskInfo {


	private Drawable icon;
	private String name;
	private String packname;
	private long memsize;
	private boolean checked;
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	/*
	 * true:用户进程
	 * false：系统进程
	 */
	private boolean userTask;
	
	
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public long getMemsize() {
		return memsize;
	}
	public void setMemsize(long memsize) {
		this.memsize = memsize;
	}
	public boolean isUserTask() {
		return userTask;
	}
	public void setUserTask(boolean userTask) {
		this.userTask = userTask;
	}
	@Override
	public String toString() {
		return "TaskInfo [icon=" + icon + ", name=" + name + ", packname="
				+ packname + ", memsize=" + memsize + ", userTask=" + userTask
				+ "]";
	}
	
}
