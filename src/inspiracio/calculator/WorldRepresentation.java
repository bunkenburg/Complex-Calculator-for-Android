package inspiracio.calculator;

import inspiracio.numbers.EC;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/** Some representation where complex numbers can be shown. */
abstract class WorldRepresentation extends View{
	
	//State -----------------------------------------------------------------------------
	
	//Constructors ----------------------------------------------------------------------
	
	/** Required for instantiation in code */
	protected WorldRepresentation(Context ctx){
		super(ctx);
	}
	
	/** Inflation from resource file */
	protected WorldRepresentation(Context ctx, AttributeSet ats){
		super(ctx, ats);
	}
	
	/** Inflation from resource file */
	protected WorldRepresentation(Context ctx, AttributeSet ats, int defaultStyle){
		super(ctx, ats, defaultStyle);
	}
	
	//Methods ---------------------------------------------------------------------------

	/** Adds a number to be displayed in the world. */
	abstract void add(EC c);
	
	/** Clears all displayed numbers and stuff. */
	abstract void clear();
	
    /** Draws the stuff that the world should show. */
    abstract void drawStuff(Drawing drawing);

	/** Resets to centre on zero. */
    abstract void reset();
    
    //Helpers --------------------------------------------------------------------------
	
}