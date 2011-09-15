package inspiracio.calculator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/** Some representation where complex numbers can be shown. */
abstract class WorldRepresentation extends View{
	
	//Constructors ----------------------------------------------------------------------
	
	protected WorldRepresentation(Context ctx){
		super(ctx);
	}
	
	protected WorldRepresentation(Context ctx, AttributeSet ats){
		super(ctx, ats);
	}
	
	protected WorldRepresentation(Context ctx, AttributeSet ats, int defaultStyle){
		super(ctx, ats, defaultStyle);
	}
	
	//Methods ---------------------------------------------------------------------------

//	@Override protected void onMeasure(int wMeasureSpec, int hMeasureSpec){
//		int mh=mh(hMeasureSpec);
//		int mw=mw(wMeasureSpec);
//		this.setMeasuredDimension(mw, mh);
//	}
	
	/** Draw the world. */
	@Override protected void onDraw(Canvas canvas){
		
	}
	
	//Helpers --------------------------------------------------------------------------
	
	private int mh(int ms){
		int specMode=MeasureSpec.getMode(ms);
		int specSize=MeasureSpec.getSize(ms);
		//Calculate the view height
		return specSize;
	}
	
	private int mw(int ms){
		int specMode=MeasureSpec.getMode(ms);
		int specSize=MeasureSpec.getSize(ms);
		//Calculate the view width
		return specSize;
	}
}