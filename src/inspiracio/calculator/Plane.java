package inspiracio.calculator;

import inspiracio.numbers.Circle;
import inspiracio.numbers.EC;
import inspiracio.numbers.ECList;
import inspiracio.numbers.Line;
import inspiracio.numbers.Piclet;
import inspiracio.numbers.Rectangle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;

/** The complex plane */
final class Plane extends WorldRepresentation{

    private static int AXISSPACE = 30;
    private static int AXISMARKING = 40;
    private static int FONTHEIGHT = 10;
    private static int TRIANGLESIZE = 5;
    private static int MARKLENGTH = 2;

    //State -------------------------------------------------
    
    private double ScaleFactor = 40D;
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

    //Constructors ------------------------------------------
	
	/** Constructor for inflation */
	public Plane(Context ctx, AttributeSet ats){
		super(ctx, ats);
	}
	
	//View methods ------------------------------------------------
	
	/** Draw the world. */
	@Override protected void onDraw(Canvas canvas){
		this.setBackgroundColor(Color.WHITE);
		int height=this.getHeight();
		int width=this.getWidth();
		
		//Create paintbrush
		Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLUE);
        Drawing drawing = new Drawing(canvas, paint);
		
		Point point = new Point();
        Point point1 = new Point();
        Point point2 = new Point();
        
        //Find the limits of what's visible
        TopImaginary = CenterImaginary + Pix2Math(height / 2);
        BottomImaginary = CenterImaginary - Pix2Math(height / 2);
        LeftReal = CenterReal - Pix2Math(width / 2);
        RightReal = CenterReal + Pix2Math(width / 2);
                
        //Draws the x axis.
        double d = raiseSmooth(Pix2Math(AXISMARKING));
        int l = Math2Pix(d);
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = LeftReal + Pix2Math(AXISSPACE);
        double d4 = RightReal - Pix2Math(AXISSPACE);
        double d5 = BottomImaginary + Pix2Math(AXISSPACE);
        double d6 = TopImaginary - Pix2Math(AXISSPACE);
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
        Polygon polygon = Polygon.mkTriangle(point1, Polygon.Direction.EAST, TRIANGLESIZE);
        drawing.draw(polygon);
        if(RightReal <= MaxReal)
            drawing.fill(polygon, Color.BLACK);
        polygon = Polygon.mkTriangle(point, Polygon.Direction.WEST, TRIANGLESIZE);
        drawing.draw(polygon);
        if(MinReal <= LeftReal)
            drawing.fill(polygon, Color.BLACK);
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
        polygon = Polygon.mkTriangle(point1, Polygon.Direction.NORTH, TRIANGLESIZE);
        drawing.draw(polygon);
        if(TopImaginary <= MaxImaginary)
            drawing.fill(polygon, Color.BLACK);
        polygon = Polygon.mkTriangle(point, Polygon.Direction.SOUTH, TRIANGLESIZE);
        drawing.draw(polygon);
        if(MinImaginary <= BottomImaginary)
            drawing.fill(polygon, Color.BLACK);
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
	}

	/** Called when the size of the view has changed. */
	@Override public void onSizeChanged(int a, int b, int c, int d){
		System.out.println("onSizeChanged " + a + " " + b + " " + c + " " + d);
	}
	
	/** Called when a touch event occurs. */
	@Override public boolean onTouchEvent(MotionEvent e){
		return false;
	}
	
	//Business methods ----------------------------------------------

	/** Resets to centre on zero. */
    void reset(){
        CenterReal = 0.0D;
        CenterImaginary = 0.0D;
    }

    /** Shift the image by some pixel distance. */
    void shift(int i, int j){
        CenterImaginary -= Pix2Math(j);
        CenterReal += Pix2Math(i);
    }

    void zoomIn(){ScaleFactor *= 2D;}
    void zoomOut(){ScaleFactor /= 2D;}

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
    @SuppressWarnings("unused")
	private EC Point2Complex(Point point){
        return EC.mkCartesian(LeftReal + (double)point.x / ScaleFactor, TopImaginary - (double)point.y / ScaleFactor);
    }

    /** Converts pixel distance in mathematical distance between numbers. */
    private double Pix2Math(int i){
        return (double)i / ScaleFactor;
    }

    /** Converts mathematical distance between numbers in pixel distance. */
    private int Math2Pix(double d){
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
    @SuppressWarnings("unused")
	private void drawComplex(Drawing drawing, EC ec){
        if(ec.finite()){
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
            drawing.drawCircle((int)((circle.center.re() - LeftReal) * ScaleFactor), -(int)((circle.center.im() - TopImaginary) * ScaleFactor), Math2Pix(circle.radius));
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

    /** Draws a line to a number. */
    private void lineTo(Drawing drawing, EC ec){
        drawing.lineTo((int)((ec.re() - LeftReal) * ScaleFactor), -(int)((ec.im() - TopImaginary) * ScaleFactor));
    }

    /** Moves to a number. */
    private void moveTo(Drawing drawing, EC ec){
        drawing.moveTo((int)((ec.re() - LeftReal) * ScaleFactor), -(int)((ec.im() - TopImaginary) * ScaleFactor));
    }

}