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
package inspiracio.view;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/** An object that can receive the touch events from Android's API and transform them 
 * common gesture calls, for client programmer convenience.
 * 
 * <h3>usage</h3>
 * Make your touch listener and register it with a touch dispatcher:
 * <pre>
 * 	TouchListener listener=new TouchAdapter(){
 * 		//Override the methods you need
 * 	};
 * 	TouchDispatcher dispatcher=newTouchDispatcher();
 * 	dispatcher.addTouchListener(listener);
 * </pre>
 * <p>
 * The listeners you registers must execute fast, without executions,
 * in particular motion callbacks.
 * The listeners themselves must not add or remove listeners.
 * </p>
 * 
 * Then let your view's onTouchEvent() call the dispatcher:
 * <pre>
 * 	public void onTouchEvent(MotionEvent e){
 * 		dispatcher.onTouchEvent(this, e);
 * 	}
 * </pre>
 * 
 * Or set the dispatcher as the OnTouchListener of the view:
 * <pre>
 * 	view.setOnTouchListener(dispatcher);
 * </pre>
 * 
 * <h3>implemented</h3>
 * These callbacks are implemented:
 * <ul>
 * 	<li><strike>onClick</strike>
 * 	<li><strike>onDoubleClick</strike>
 * 	<li><strike>onDrag</strike>
 * 	<li><strike>onLongClick</strike>
 * 	<li><strike>onPinch</strike>
 * 	<li><strike>onPressAndClick</strike>
 * 	<li><strike>onPressAndDrag</strike>
 * 	<li><strike>onRotate</strike>
 * 	<li><strike>onSpread</strike>
 * </ul>
 * 
 * @author alex
 */
public final class TouchDispatcher implements OnTouchListener{

	//State ---------------------------------------------------

	/** The registered touch listeners. 
	 * Synchronise on this list before changing it. */
	private List<TouchListener>listeners;
	
	/** A touch listener that distributes the calls to
	 * all registered listeners. */
	private TouchListener multiplexer;
	
	/** The last motion event that we have received. */
	private MotionEvent me;
	
	//Constructor ---------------------------------------------
	
	public TouchDispatcher(){
		this.listeners=new ArrayList<TouchListener>();
		
		//Makes the multiplexer.
		//The client must promise not to register and unregister listeners in the callbacks.
		this.multiplexer=new TouchListener(){
			
			@Override public void onClick(MouseEvent e){
				for(TouchListener t : listeners)
					t.onClick(e);//I hope they are fast and none of them throws exception.
			}
			
			@Override public void onLongClick(MouseEvent e){throw new RuntimeException("not implemented");}
			@Override public void onDoubleClick(MouseEvent e){throw new RuntimeException("not implemented");}
			
			@Override public void onDrag(DragEvent e){
				//Log.d("TouchDispatcher", "onDrag " + e);
				for(TouchListener t : listeners)
					t.onDrag(e);//I hope they are fast and none of them throws exception.
			}
			
			@Override public void onPinch(PinchEvent e){
				for(TouchListener t : listeners)
					t.onPinch(e);//I hope they are fast and none of them throws exception.
			}
			
			@Override public void onSpread(PinchEvent e){
				for(TouchListener t : listeners)
					t.onSpread(e);//I hope they are fast and none of them throws exception.
			}
			
			@Override public void onPressAndClick(EventObject e){throw new RuntimeException("not implemented");}
			@Override public void onPressAndDrag(EventObject e){throw new RuntimeException("not implemented");}
			@Override public void onRotate(EventObject e){throw new RuntimeException("not implemented");}
		};
	}
	
	//interface OnTouchListener -------------------------------
	
	static enum Mode{M_0, M_1, M_1_MOVING, M_2, M_2_MOVING}
	Mode mode=Mode.M_0;
	
	/** Distinguishes the events we implement and calls the listeners. 
	 * 
	 * On my phone (Nexus S), a tap produces DOWN; UP, with no MOVE in between.
	 * */
	@Override public final boolean onTouch(View view, MotionEvent e){
		this.dumpEvent(e);
		
		int action=e.getAction()&MotionEvent.ACTION_MASK;
		switch(action){
		
		//Cancel: forget everything
		case MotionEvent.ACTION_CANCEL:
			mode=Mode.M_0;
			break;
			
		//Setting down the first finger
		case MotionEvent.ACTION_DOWN:	//0
			mode=Mode.M_1;
			break;
			
		//Move: A drag or a zoom.
		case MotionEvent.ACTION_MOVE:	//2
			//A drag.
			if(mode==Mode.M_1 || mode==Mode.M_1_MOVING){
				//This only reports the last point.
				//For fast drags, we also need the historical points.
				
				//The points, in chronological order.
				int size=me.getHistorySize();
				float[] pointsX=new float[size+2];
				float[] pointsY=new float[size+2];
				pointsX[0]=this.me.getX();
				pointsY[0]=this.me.getY();
				for(int i=0; i<size; i++){
					pointsX[1+i]=me.getHistoricalX(i);
					pointsY[1+i]=me.getHistoricalY(i);
				}
				pointsX[size+1]=e.getX();
				pointsY[size+1]=e.getY();
				
				for(int i=0; i<size+1; i++){
					DragEvent me=new DragEvent(view);
					me.setStart(pointsX[i], pointsY[i]);
					me.setEnd(pointsX[i+1], pointsY[i+1]);
					this.multiplexer.onDrag(me);
				}
				mode=Mode.M_1_MOVING;
			}
			//A zoom
			else if(mode==Mode.M_2 || mode==Mode.M_2_MOVING){
				//XXX For extra smoothness, loop over the historical points too.
				//XXX I'm guessing the pointer indexes.
				PinchEvent pe=new PinchEvent(view);
				pe.setInitialA(this.me.getX(0), this.me.getY(0));
				pe.setInitialB(this.me.getX(1), this.me.getY(1));
				pe.setFinalA(e.getX(0), e.getY(0));
				pe.setFinalB(e.getX(1), e.getY(1));
				double factor=pe.getFactor();
				if(factor<=1)
					this.multiplexer.onPinch(pe);
				else
					this.multiplexer.onSpread(pe);
				mode=Mode.M_2_MOVING;
			}
			break;
			
		//Don't know what to do.
		case MotionEvent.ACTION_OUTSIDE:
			mode=Mode.M_0;
			break;
			
		//Another finger joins. Ignore third finger ...
		case MotionEvent.ACTION_POINTER_DOWN:
			mode=Mode.M_2;
			break;
			
		//A finger leaves. Ignore third finger ...
		case MotionEvent.ACTION_POINTER_UP:
			mode=Mode.M_1;
			break;
			
		//All fingers are up.
		case MotionEvent.ACTION_UP:	//1
			//A click!
			if(mode==Mode.M_1){
				MouseEvent me=new MouseEvent(view);
				me.setX(e.getX());
				me.setY(e.getY());
				this.multiplexer.onClick(me);
			}
			//End of a drag
			else if(mode==Mode.M_1_MOVING){
				//The position has not changed: no need for callback XXX Or maybe yes, for the historical points?
				DragEvent me=new DragEvent(view);
				me.setStartX(this.me.getX());
				me.setStartY(this.me.getY());
				me.setEndX(e.getX());
				me.setEndY(e.getY());
				//this.multiplexer.onDrag(me);
			}
			mode=Mode.M_0;
			break;
		}
				
		this.me=MotionEvent.obtain(e);//In any case, remember the last motion event. This deep-copies the whole event.Maybe we only need some fields.
		return true;
	}

	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event) {
	   String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
	      "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
	   StringBuilder sb = new StringBuilder();
	   int action = event.getAction();
	   int actionCode = action & MotionEvent.ACTION_MASK;
	   sb.append("event ACTION_" ).append(names[actionCode]);
	   if (actionCode == MotionEvent.ACTION_POINTER_DOWN
	         || actionCode == MotionEvent.ACTION_POINTER_UP) {
	      sb.append("(pid " ).append(
	      action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
	      sb.append(")" );
	   }
	   sb.append("[" );
	   for (int i = 0; i < event.getPointerCount(); i++) {
	      sb.append("#" ).append(i);
	      sb.append("(pid " ).append(event.getPointerId(i));
	      sb.append(")=" ).append((int) event.getX(i));
	      sb.append("," ).append((int) event.getY(i));
	      if (i + 1 < event.getPointerCount())
	         sb.append(";" );
	   }
	   sb.append("] ");
	   sb.append(System.currentTimeMillis());
	   Log.d("TouchDispatcher", sb.toString());
	}
	//Manage registered listeners ----------------------------
	
	/** Registers a touch listener. If you register it twice, it will receive the 
	 * callbacks twice. No guarantees about order of callbacks among several listeners. 
	 * @param listener
	 * */
	public final void addTouchListener(TouchListener listener){
		synchronized(this.listeners){
			this.listeners.add(listener);
		}
	}
	
	/** Unregisters a touch listener. If if was not registered, nothing. 
	 * @param listener 
	 * */
	public final void removeTouchListener(TouchListener listener){
		synchronized(this.listeners){
			this.listeners.remove(listener);
		}
	}
	
}