package com.proxy.bean;



public class FuncButton {
	
	private int id;
	private String name;
	private boolean closePage = true;
	private OnClickListener onClickListener;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public FuncButton(int id, String name, OnClickListener onClickListener) {
		super();
		this.id = id;
		this.name = name;
		this.onClickListener = onClickListener;
	}
	public FuncButton(String name, OnClickListener onClickListener ) {
		super();
		this.name = name;
		this.onClickListener = onClickListener;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public OnClickListener getOnClickListener() {
		return onClickListener;
	}
	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public boolean isClosePage() {
		return closePage;
	}
	public FuncButton setClosePage(boolean closePage) {
		this.closePage = closePage;
		return this;
	}

	public interface OnClickListener{
		void onClick();
	};
	
}
