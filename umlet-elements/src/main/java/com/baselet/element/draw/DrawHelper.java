package com.baselet.element.draw;

import java.util.Arrays;
import java.util.List;

import com.baselet.control.basics.geom.PointDouble;
import com.baselet.diagram.draw.DrawHandler;

public class DrawHelper {

	public static List<PointDouble> drawPackage(DrawHandler drawer, double upperLeftX, double upperLeftY, double titleHeight, double titleWidth, double fullHeight, double fullWidth) {
		PointDouble start = new PointDouble(upperLeftX, upperLeftY);
		List<PointDouble> points = Arrays.asList(
				start,
				new PointDouble(upperLeftX + titleWidth, upperLeftY),
				new PointDouble(upperLeftX + titleWidth, upperLeftY + titleHeight),
				new PointDouble(upperLeftX + fullWidth, upperLeftY + titleHeight),
				new PointDouble(upperLeftX + fullWidth, upperLeftY + fullHeight),
				new PointDouble(upperLeftX, upperLeftY + fullHeight),
				start);
		drawer.drawLines(points);
		drawer.drawLines(new PointDouble(upperLeftX, upperLeftY + titleHeight), new PointDouble(upperLeftX + titleWidth, upperLeftY + titleHeight));
		return points;
	}

	public static void drawInputBox(DrawHandler drawer, double x, double y, double w, double h) {
		double lw=drawer.getLineWidth();
		drawer.setLineWidth(0.2);
		List<PointDouble> points = Arrays.asList(
				new PointDouble(x, y),
				new PointDouble(x+w, y),
				new PointDouble(x+w, y+h),
				new PointDouble(x,y+h),
				new PointDouble(x, y)
				);
		drawer.drawLines(points);
		drawer.setLineWidth(lw);
	}
	public static void drawTab(DrawHandler drawer, double x, double y, double w, double h, double b) {
		List<PointDouble> points = Arrays.asList(
				new PointDouble(x, y+h),
				new PointDouble(x+b, y),
				new PointDouble(x+w+b, y),
				new PointDouble(x+w+2*b,y+h)
				);
		drawer.drawLines(points);
	}
	public static void drawHScroll(DrawHandler drawer, double x, double y, double w, double h) {
		double b=3;
		List<PointDouble> points = Arrays.asList(
				new PointDouble(x, y),
				new PointDouble(x+w, y),
				new PointDouble(x+w, y+h),
				new PointDouble(x,y+h),
				new PointDouble(x, y)
				);
		List<PointDouble> leftArrow = Arrays.asList(
				new PointDouble(x+b, y+h/2),
				new PointDouble(x+h-b, y+b),
				new PointDouble(x+h-b, y+h-b),
				new PointDouble(x+b, y+h/2)
				);
		List<PointDouble> rightArrow = Arrays.asList(
				new PointDouble(x+w-b, y+h/2),
				new PointDouble(x+w-h+b, y+b),
				new PointDouble(x+w-h+b, y+h-b),
				new PointDouble(x+w-b, y+h/2)
				);
		drawer.drawLines(points);
		drawer.drawLines(new PointDouble(x+h, y), new PointDouble(x+h, y+h));
		drawer.drawLines(new PointDouble(x+w-h, y), new PointDouble(x+w-h, y+h));
		drawer.drawLines(leftArrow);
		drawer.drawLines(rightArrow);
	}
	public static void drawVScroll(DrawHandler drawer, double x, double y, double w, double h) {
		if(x<0 || y<0 || h<=0 || w<=0) return;
		double b=3;
		List<PointDouble> points = Arrays.asList(
				new PointDouble(x, y),
				new PointDouble(x+w, y),
				new PointDouble(x+w, y+h),
				new PointDouble(x,y+h),
				new PointDouble(x, y)
				);
		List<PointDouble> upArrow = Arrays.asList(
				new PointDouble(x+w/2, y+b),
				new PointDouble(x+b, y+w-b),
				new PointDouble(x+w-b, y+w-b),
				new PointDouble(x+w/2, y+b)
				);
		List<PointDouble> downArrow = Arrays.asList(
				new PointDouble(x+w/2, y+h-b),
				new PointDouble(x+b, y+h-w+b),
				new PointDouble(x+w-b, y+h-w+b),
				new PointDouble(x+w/2, y+h-b)
				);
		drawer.drawLines(points);
		drawer.drawLines(new PointDouble(x, y+w), new PointDouble(x+w, y+w));
		drawer.drawLines(new PointDouble(x, y+h-w), new PointDouble(x+w, y+h-w));
		drawer.drawLines(upArrow);
		drawer.drawLines(downArrow);
	}
	public static void drawPanel(DrawHandler drawer, double x, double y, double w, double h, double sl, double sr) {
		if(x<0 || y<0 || h<=0 || w<=0) return;
		List<PointDouble> points = Arrays.asList(
				new PointDouble(x+sl, y),
				new PointDouble(x, y),
				new PointDouble(x, y+h),
				new PointDouble(x+w,y+h),
				new PointDouble(x+w,y),
				new PointDouble(x+sr,y)
				);
		drawer.drawLines(points);
	}
	public static void drawActor(DrawHandler drawer, int hCenter, int yTop, double dimension) {
		drawer.drawCircle(hCenter, yTop + DrawHelper.headRadius(dimension), DrawHelper.headRadius(dimension)); // Head
		drawer.drawLine(hCenter - DrawHelper.armLength(dimension), yTop + DrawHelper.armHeight(dimension), hCenter + DrawHelper.armLength(dimension), yTop + DrawHelper.armHeight(dimension)); // Arms
		drawer.drawLine(hCenter, yTop + DrawHelper.headRadius(dimension) * 2, hCenter, yTop + DrawHelper.headToBodyLength(dimension)); // Body
		drawer.drawLine(hCenter, yTop + DrawHelper.headToBodyLength(dimension), hCenter - DrawHelper.legSpan(dimension), yTop + DrawHelper.headToLegLength(dimension)); // Legs
		drawer.drawLine(hCenter, yTop + DrawHelper.headToBodyLength(dimension), hCenter + DrawHelper.legSpan(dimension), yTop + DrawHelper.headToLegLength(dimension)); // Legs
	}

	public static double headToLegLength(double dimension) {
		return legSpan(dimension) * 2 + headToBodyLength(dimension);
	}

	private static double legSpan(double dimension) {
		return dimension;
	}

	private static double headToBodyLength(double dimension) {
		return dimension * 2 + headRadius(dimension) * 2;
	}

	private static double armHeight(double dimension) {
		return armLength(dimension);
	}

	public static double armLength(double dimension) {
		return dimension * 1.5;
	}

	private static double headRadius(double dimension) {
		return dimension / 2;
	}

}
