package pkg;

import java.awt.Color;
import java.awt.Font;

public class ScreenString {

	/**
	 * 文字
	 */
	public String str;
	/**
	 * 文字色
	 */
	public Color color;
	/**
	 * 文字フォント
	 */
	public Font font;
	/**
	 * 表示場所(x)
	 */
	public int x;
	/**
	 * 表示場所(y)
	 */
	public int y;
	
	public ScreenString(String str, Color color, Font font, int x, int y) {
		super();
		this.str = str;
		this.color = color;
		this.font = font;
		this.x = x;
		this.y = y;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public Color getColor() {
		return color;
	}
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
}
