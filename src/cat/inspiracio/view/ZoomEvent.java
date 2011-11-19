package cat.inspiracio.view;

import java.util.EventObject;

import android.graphics.Point;

/** Pinch event or spread event.
 * I call it "ZoomEvent" instead of "PinchOrSpreadEvent" and because it is often used for zoom, but it could be used differently. */
public final class ZoomEvent extends EventObject{

	//State -----------------------------------
	
	private float a0x, a0y, a1x, a1y;
	private float b0x, b0y, b1x, b1y;
	private Point centre;
	
	//Constructor -----------------------------
	
	ZoomEvent(Object source){super(source);}
	
	//Accessors -------------------------------
	
	void setInitialA(float x, float y){a0x=x;a0y=y;}
	void setFinalA(float x, float y){a1x=x;a1y=y;}
	void setInitialB(float x, float y){b0x=x;b0y=y;}
	void setFinalB(float x, float y){b1x=x;b1y=y;}
	
	public Point getCentre(){
		if(centre==null){
			centre=new Point();
			centre.x=(int)((a0x+b0x)/2);
			centre.y=(int)((a0y+b0y)/2);
		}
		return centre;
	}
	
	double getInitialDistance(){
		return Math.sqrt(sqr(a0x-b0x)+sqr(a0y-b0y));
	}
	
	double getFinalDistance(){
		return Math.sqrt(sqr(a1x-b1x)+sqr(a1y-b1y));
	}
	
	/** What is the zoom factor? By how much should we zoom in or out? 
	 * 1 means no zoom.
	 * Smaller than one means zoom out.
	 * Greater means zoom in. */
	public double getFactor(){
		return this.getFinalDistance()/this.getInitialDistance();
	}
	
	//Helpers -----------------------------------
	
	private float sqr(float f){return f*f;}
}