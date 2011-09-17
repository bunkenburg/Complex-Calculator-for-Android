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

import android.graphics.Point;

/** Substitute for java.awt.Polygon. */
final class Polygon{

	static enum Direction{NORTH,SOUTH,EAST,WEST}
	
	//State -----------------------------------------------
	
	//protected Rectangle bounds;
	int npoints;
	int[] xpoints;
	int[] ypoints;
	
	//Constructors ----------------------------------------

	Polygon(int[] xpoints, int[] ypoints, int npoints){
		this.npoints=npoints;
		this.xpoints=xpoints;
		this.ypoints=ypoints;
	}
	
    /** Makes a little triangle
     * @param point Where is the traingle's tip?
     * @param direction Where is the triangle pointing to?
     * @param size */
    static Polygon mkTriangle(Point point, Direction direction, int size){
        switch(direction){
        case NORTH: // '\0'
            return new Polygon(new int[] {
                point.x - size, point.x, point.x + size
            }, new int[] {
                point.y + size, point.y, point.y + size
            }, 3);
        case EAST: // '\001'
            return new Polygon(new int[] {
                point.x - size, point.x, point.x - size
            }, new int[] {
                point.y - size, point.y, point.y + size
            }, 3);
        case SOUTH: // '\002'
            return new Polygon(new int[] {
                point.x - size, point.x, point.x + size
            }, new int[] {
                point.y - size, point.y, point.y - size
            }, 3);
        case WEST: // '\003'
            return new Polygon(new int[] {
                point.x + size, point.x, point.x + size
            }, new int[] {
                point.y - size, point.y, point.y + size
            }, 3);
        }
        return null;
    }

	//Methods ---------------------------------------------
	
}