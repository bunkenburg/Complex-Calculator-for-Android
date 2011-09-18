/*	Copyright 2011 Alexander Bunkenburg alex@inspiracio.com
 * 
 * This file is part of Complex Calculator.
 * 
 * Complex Calculator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Complex Calculator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Complex Calculator. If not, see <http://www.gnu.org/licenses/>.
 * */
package inspiracio.calculator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;

/** Some convenience methods for drawing. */
final class Drawing{

    //State -------------------------------------------------------
	
    /** The paint brush to use. */
    private Paint paint;
    
    /** The position of an imaginary drawing pen. */
    private Point pen;
    
    /** The canvas object used to draw. */
    private Canvas canvas;

    //Constructor --------------------------------------------------
    
    /** @param canvas The canvas where to paint.
     * @param paint The paint brush to use.
     * */
    Drawing(Canvas canvas, Paint paint){
        this.pen = new Point();
        this.canvas = canvas;
        this.paint=paint;
    }

    //Drawing methods ------------------------------------------------------
    
    /** Draws a little cross at pen position. */
    void cross(){
        canvas.drawLine(pen.x - 2, pen.y - 2, pen.x + 2, pen.y + 2, paint);
        canvas.drawLine(pen.x + 2, pen.y - 2, pen.x - 2, pen.y + 2, paint);
    }

    void cross(int i, int j, int k){
        pen.x = i;
        pen.y = j;
        canvas.drawLine(pen.x - k, pen.y - k, pen.x + k, pen.y + k, paint);
        canvas.drawLine(pen.x + k, pen.y - k, pen.x - k, pen.y + k, paint);
    }

    /** Draws a little cross at a point. */
    void cross(Point point){
        canvas.drawLine(point.x - 2, point.y - 2, point.x + 2, point.y + 2, paint);
        canvas.drawLine(point.x + 2, point.y - 2, point.x - 2, point.y + 2, paint);
        pen.x = point.x;
        pen.y = point.y;
    }

    /** Draws a circle around (x,y). */
    void drawCircle(int x, int y, double radius){
        canvas.drawCircle(x, y, (float) radius, paint);
    }

    /** Draws a circle around a point. */
    void drawCircle(Point point, double radius){
        canvas.drawCircle(point.x, point.y, (float)radius, paint);
    }

    void drawLine(Point point, Point point1){
        canvas.drawLine(point.x, point.y, point1.x, point1.y, paint);
        pen.x = point1.x;
        pen.y = point1.y;
    }

    /** Draws a line in a given colour. */
    void drawLine(Point point, Point point1, int color){
        int color1 = paint.getColor();
        paint.setColor(color);
        canvas.drawLine(point.x, point.y, point1.x, point1.y, paint);
        paint.setColor(color1);
        pen.x = point1.x;
        pen.y = point1.y;
    }

	/** Draws the lines of the polygon on the canvas. */
	void draw(Polygon p){
		//4 floats for every line
		float[]pts=new float[4*p.npoints];
		for(int i=0; i<p.npoints-1; i++){
			//4 floats for line i
			pts[4*i]  =p.xpoints[i];
			pts[4*i+1]=p.ypoints[i];
			pts[4*i+2]=p.xpoints[i+1];
			pts[4*i+3]=p.ypoints[i+1];
		}
		//One line from the last to the first point
		pts[4*p.npoints-4]=p.xpoints[p.npoints-1];
		pts[4*p.npoints-3]=p.ypoints[p.npoints-1];
		pts[4*p.npoints-2]=p.xpoints[0];
		pts[4*p.npoints-1]=p.ypoints[0];
		
		canvas.drawLines(pts, paint);
	}

	/** Draws a String at a point. */
    void draw(String s){
        canvas.drawText(s, pen.x, pen.y, paint);
    }

    /** Fills the polygon with the given colour.
     * @param polygon The polygon to fill.
     * @param colour In this colour.
     * */
    final void fill(Polygon polygon, int colour){
    	//I can't get this to work. It only draws the strokes. For now, I rely on colour.
    	int oldColor=paint.getColor();
    	Style oldStyle=paint.getStyle();
    	
    	paint.setColor(colour);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);
    	
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        for(int i=0; i<polygon.npoints-1; i++){
            path.moveTo(polygon.xpoints[i], polygon.ypoints[i]);
            path.lineTo(polygon.xpoints[i+1], polygon.ypoints[i+1]);
        }
        path.moveTo(polygon.xpoints[polygon.npoints-1], polygon.ypoints[polygon.npoints-1]);
        path.lineTo(polygon.xpoints[0], polygon.ypoints[0]);
        path.close();

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);

        paint.setColor(oldColor);
        paint.setStyle(oldStyle);
    }

    /** Draws a line from current pen position, the x and y distances are given by a point. */
    void line(Point point){
        canvas.drawLine(pen.x, pen.y, pen.x + point.x, pen.y + point.y, paint);
        pen.offset(point.x, point.y);
    }

    /** Draws a line from current pen position. */
    void line(int i, int j){
        canvas.drawLine(pen.x, pen.y, pen.x + i, pen.y + j, paint);
        pen.offset(i, j);
    }

    /** Draws a line to a point. */
    void lineTo(int i, int j){
        canvas.drawLine(pen.x, pen.y, i, j, paint);
        pen.x = i;
        pen.y = j;
    }

    /** Draws a line to a point. */
    void lineTo(Point point){
        canvas.drawLine(pen.x, pen.y, point.x, point.y, paint);
        pen.x = point.x;
        pen.y = point.y;
    }

    /** Moves the pen. */
    void move(int i, int j){
        pen.offset(i, j);
    }

    void moveTo(Point point){
        pen.x = point.x;
        pen.y = point.y;
    }

    void moveTo(int i, int j){
        pen.x = i;
        pen.y = j;
    }

}