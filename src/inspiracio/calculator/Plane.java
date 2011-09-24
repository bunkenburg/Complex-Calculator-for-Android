/*	Copyright 2011 Alexander Bunkenburg alex@inspiracio.com
 * 
 * This file is part of Complex Calculator for Android.
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
 * along with Complex Calculator for Android. If not, see <http://www.gnu.org/licenses/>.
 * */
package inspiracio.calculator;

import static inspiracio.calculator.Polygon.Direction.EAST;
import static inspiracio.calculator.Polygon.Direction.NORTH;
import static inspiracio.calculator.Polygon.Direction.SOUTH;
import static inspiracio.calculator.Polygon.Direction.WEST;
import inspiracio.numbers.Circle;
import inspiracio.numbers.EC;
import inspiracio.numbers.ECList;
import inspiracio.numbers.Line;
import inspiracio.numbers.Piclet;
import inspiracio.numbers.Rectangle;
import inspiracio.view.MouseEvent;
import inspiracio.view.TouchAdapter;
import inspiracio.view.TouchDispatcher;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;

/** The complex plane */
final class Plane extends WorldRepresentation{

	/** Pixel distance between the axis tips and the border of the view. */
    private static int AXISSPACE=30;//30
    
    /** Approximate pixel distance between the marks on the axes. */
    private static int AXISMARKING=60;//40
    
    /** Approximate height of the font, to draw axis labels slightly below the x-axis. */
    private static int FONTHEIGHT=18;//10
    
    /** Size of the triangles at the end of the axes. */
    private static int TRIANGLESIZE=10;//5
    
    /** Length of the we marks on the axes. */
    private static int MARKLENGTH=4;//2

    //State -------------------------------------------------
    
    /** The number 1 is how many pixels? */
    private double ScaleFactor = 80;//40D;
    
    private double CenterReal;
    private double CenterImaginary;
    private double TopImaginary;
    private double LeftReal;
    private double BottomImaginary;
    private double RightReal;

    //The limits of what is shown, in numbers. (Variables copied from World.)
    private double MaxImaginary;
    private double MinImaginary;
    private double MaxReal;
    private double MinReal;

    /** The numbers that are currently displayed. */
    private Set<EC>numbers=new HashSet<EC>();
    
    //Constructors ------------------------------------------
	
	/** Constructor for inflation */
	public Plane(Context ctx, AttributeSet ats){
		super(ctx, ats);
		this.setBackgroundColor(Color.WHITE);//this also here, so that it applies also in XML editing in Eclipse
		
		//Touch events
		TouchDispatcher dispatcher=new TouchDispatcher();
		dispatcher.addTouchListener(new TouchAdapter(){
			/** Users selects a number by clicking on it. */
			@Override public void onClick(MouseEvent e){
				float x=e.getX();
				float y=e.getY();
				Point point=new Point();
				point.x=(int)x;//rounds float to int
				point.y=(int)y;
				EC c=point2Complex(point);
				add(c);
				//Also need to send it to the display
				calculator.add(c);
			}
		});
		this.setOnTouchListener(dispatcher);
	}
	
	//View methods ------------------------------------------------
	
	/** Draw the world. */
	@Override protected void onDraw(Canvas canvas){
		//Get ready
		this.setBackgroundColor(Color.WHITE);
		int height=this.getHeight();
		int width=this.getWidth();
		
		//Create paintbrush
		Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLUE);
		//Text size: the default 12 is quite small. The display has 27 by default. Polishing needed.
		//float textSize=paint.getTextSize();//12
		paint.setTextSize(FONTHEIGHT);
        Drawing drawing = new Drawing(canvas, paint);
		
        //Some points please
		Point point = new Point();
        Point point1 = new Point();
        Point point2 = new Point();
        
        //Find the limits of what's visible
        TopImaginary = CenterImaginary + pix2Math(height / 2);
        BottomImaginary = CenterImaginary - pix2Math(height / 2);
        LeftReal = CenterReal - pix2Math(width / 2);
        RightReal = CenterReal + pix2Math(width / 2);
                
        //Draws the x axis.
        double d = raiseSmooth(pix2Math(AXISMARKING));
        int l = math2Pix(d);
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = LeftReal + pix2Math(AXISSPACE);
        double d4 = RightReal - pix2Math(AXISSPACE);
        double d5 = BottomImaginary + pix2Math(AXISSPACE);
        double d6 = TopImaginary - pix2Math(AXISSPACE);
        if(d3 <= 0.0D && d4 >= 0.0D)
            d1 = 0.0D;
        else if(d3 > 0.0D)
            d1 = d3;
        else if(d4 < 0.0D)
            d1 = d4;
        if(d5 <= 0.0D && d6 >= 0.0D)
            d2 = 0.0D;
        else if(d5 > 0.0D)
            d2 = d5;
        else if(d6 < 0.0D)
            d2 = d6;
        cartesian2Point(d1, d2, point2);
        cartesian2Point(d3, d2, point);
        cartesian2Point(d4, d2, point1);
        drawing.drawLine(point, point1, Color.LTGRAY);
        Polygon polygon = Polygon.mkTriangle(point1, EAST, TRIANGLESIZE);
        drawing.draw(polygon);
        if(RightReal <= MaxReal)
            drawing.fill(polygon, Color.RED);
        polygon = Polygon.mkTriangle(point, WEST, TRIANGLESIZE);
        drawing.draw(polygon);
        if(MinReal <= LeftReal)
            drawing.fill(polygon, Color.RED);
        int j = point2.y;
        double d7 = Math.ceil(d3 / d);
        d3 = d7 * d;
        int i = real2Pix(d3);
        for(; d3 < d4; d3 += d){
            drawing.moveTo(i, j);
            drawing.line(0, MARKLENGTH);
            canvas.drawText(EC.toString(d3), i + MARKLENGTH, j + FONTHEIGHT, paint);
            i += l;
        }
        
        //Draws the y axis
        cartesian2Point(d1, d5, point);
        cartesian2Point(d1, d6, point1);
        drawing.drawLine(point, point1, Color.LTGRAY);
        polygon = Polygon.mkTriangle(point1, NORTH, TRIANGLESIZE);
        drawing.draw(polygon);
        if(TopImaginary <= MaxImaginary)
            drawing.fill(polygon, Color.RED);
        polygon = Polygon.mkTriangle(point, SOUTH, TRIANGLESIZE);
        drawing.draw(polygon);
        if(MinImaginary <= BottomImaginary)
            drawing.fill(polygon, Color.RED);
        i = point2.x;
        d7 = Math.ceil(d5 / d);
        d5 = d7 * d;
        for(int k = imag2Pix(d5); d5 < d6; k -= l){
            if(d5 != 0.0D || d1 != 0.0D){
                String s = EC.toString(d5) + "i";
                drawing.moveTo(i, k);
                drawing.line(-MARKLENGTH, 0);
                canvas.drawText(s, i - MARKLENGTH - paint.measureText(s), k + FONTHEIGHT, paint);
            }
            d5 += d;
        }
        
        //Draws the numbers we are currently showing
        this.add(EC.mkCartesian(10, 1));
        this.drawStuff(drawing);
	}

	/** Called when the size of the view has changed. */
	@Override public void onSizeChanged(int a, int b, int c, int d){
		System.out.println("onSizeChanged " + a + " " + b + " " + c + " " + d);
	}
		
	//Business methods ----------------------------------------------

	/** Adds a number to be displayed in the world. */
	@Override void add(EC c){
		this.updateExtremes(c);
		this.numbers.add(c);
		this.invalidate();
	}

	/** Clears all displayed numbers and stuff. */
	@Override void clear(){
		this.numbers.clear();
		this.invalidate();
	}
	

	/** Resets to centre on zero. */
    @Override void reset(){
        CenterReal = 0.0D;
        CenterImaginary = 0.0D;
		this.invalidate();
    }

    /** Shift the image by some pixel distance. */
    void shift(int i, int j){
        CenterImaginary -= pix2Math(j);
        CenterReal += pix2Math(i);
		this.invalidate();
    }

    void zoomIn(){
    	ScaleFactor *= 2D;
    	this.invalidate();
    }

    void zoomOut(){
    	ScaleFactor /= 2D;
    	this.invalidate();
    }

	//Converters ----------------------------------------------------
	
	/** Converts a complex number in Cartesian coordinates to a point on the plane. 
	 * @param x real part
	 * @param y imaginary part
	 * @param point Sets the properties of this point. */
    private void cartesian2Point(double x, double y, Point point){
        point.x = (int)((x - LeftReal) * ScaleFactor);
        point.y = -(int)((y - TopImaginary) * ScaleFactor);
    }

    /** Converts a point to a number. */
	private EC point2Complex(Point point){
        return EC.mkCartesian(LeftReal + (double)point.x / ScaleFactor, TopImaginary - (double)point.y / ScaleFactor);
    }

    /** Converts pixel distance in mathematical distance between numbers. */
    private double pix2Math(int i){
        return (double)i / ScaleFactor;
    }

    /** Converts mathematical distance between numbers in pixel distance. */
    private int math2Pix(double d){
        return (int)(d * ScaleFactor);
    }

    /** Real to pixel x-value. */
    private int real2Pix(double d){
        return (int)((d - LeftReal) * ScaleFactor);
    }

    /** Imaginary to pixel y-value. */
    private int imag2Pix(double d){
        return (int)((TopImaginary - d) * ScaleFactor);
    }

    /** Not sure what it does. */
    private static double raiseSmooth(double d){
        int i;
        for(i = 0; d < 1.0D; i--)
            d *= 10D;
        while(d >= 10D){
            d /= 10D;
            i++;
        }
        if(d > 5D)
            d = 10D;
        else if(d > 2.5D)
            d = 5D;
        else if(d > 2D)
            d = 2.5D;
        else if(d > 1.0D)
            d = 2D;
        for(; i < 0; i++)
            d /= 10D;
        for(; i > 0; i--)
            d *= 10D;
        return d;
    }

    //Drawing methods ------------------------------------------------
    
    /** Draws a complex number. */
	private void drawComplex(Drawing drawing, EC ec){
        if(ec.isFinite()){
            drawing.cross((int)((ec.re() - LeftReal) * ScaleFactor), -(int)((ec.im() - TopImaginary) * ScaleFactor), MARKLENGTH);
            drawing.move(2, 2);
            drawing.draw(ec.toString());
        }
    }

    /** Draws a list of numbers. */
    private void drawECList(Drawing drawing, ECList eclist){
        if(eclist != null){
            moveTo(drawing, eclist.head());
            lineTo(drawing, eclist.head());
            for(eclist = eclist.tail(); eclist != null; eclist = eclist.tail())
                lineTo(drawing, eclist.head());
        }
    }

    /** Draws a piclet. */
    @SuppressWarnings("unused")
	private void drawPiclet(Drawing drawing, Piclet piclet){
    	//XXX Use subclassing better
        if(piclet instanceof Line){
            moveTo(drawing, ((Line)piclet).start);
            lineTo(drawing, ((Line)piclet).end);
            return;
        }
        if(piclet instanceof Circle){
            Circle circle = (Circle)piclet;
            drawing.drawCircle((int)((circle.center.re() - LeftReal) * ScaleFactor), -(int)((circle.center.im() - TopImaginary) * ScaleFactor), math2Pix(circle.radius));
            return;
        }
        if(piclet instanceof Rectangle){
            Rectangle rectangle = (Rectangle)piclet;
            moveTo(drawing, rectangle.botLeft);
            lineTo(drawing, rectangle.botRight);
            lineTo(drawing, rectangle.topRight);
            lineTo(drawing, rectangle.topLeft);
            lineTo(drawing, rectangle.botLeft);
            return;
        } else{
            drawECList(drawing, piclet.getSamples());
            return;
        }
    }

    /** Draws the stuff that the plane should show: just the current numbers. */
    @Override void drawStuff(Drawing drawing){
        for(EC c : this.numbers)
            this.drawComplex(drawing, c);
    }

    /** Draws a line to a number. */
    private void lineTo(Drawing drawing, EC ec){
        drawing.lineTo((int)((ec.re() - LeftReal) * ScaleFactor), -(int)((ec.im() - TopImaginary) * ScaleFactor));
    }

    /** Moves to a number. */
    private void moveTo(Drawing drawing, EC ec){
        drawing.moveTo((int)((ec.re() - LeftReal) * ScaleFactor), -(int)((ec.im() - TopImaginary) * ScaleFactor));
    }

    //Helpers that maintain the state ---------------------------------------------------
    
    protected void updateExtremes(EC ec){
        if(ec.isFinite()){
            MaxImaginary = Math.max(MaxImaginary, ec.im());
            MinImaginary = Math.min(MinImaginary, ec.im());
            MaxReal = Math.max(MaxReal, ec.re());
            MinReal = Math.min(MinReal, ec.re());
        }
    }

}