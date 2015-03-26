package pkg;

import java.awt.Color;

public class Level {
	/**
	 * 範囲(下限)
	 */
	public int lower;
	/**
	 * 文字色
	 */
	public Color color;
	/**
	 * 文字
	 */
	public String str;
	/**
	 * 速度
	 */
	public int speed;
	
	public Level(int lower, Color color, String str, int speed) {
		super();
		this.lower = lower;
		this.color = color;
		this.str = str;
		this.speed = speed;
	}
	public int getLower() {
		return lower;
	}
	public void setLower(int lower) {
		this.lower = lower;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
}
