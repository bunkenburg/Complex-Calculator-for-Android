package cat.inspiracio.widget;

import android.text.Editable;
import android.text.InputFilter;

/** Editable for direct input connection */
final class DirectEditable implements Editable{

	//State --------------------------------------
	
	@SuppressWarnings("unused")
	private DirectInputConnection dic;
	private IMEEditText editText;
	
	//Constructors -------------------------------
	
	DirectEditable(){}
	
	//My methods --------------------------------
	
	final void setInputConnection(DirectInputConnection dic){
		this.dic=dic;
		this.editText=dic.getEditText();
	}
	
	final CharSequence getText(){
		CharSequence cs=this.editText.getText();
		return cs;
	}
	
	//Methods -----------------------------------
	
	@Override public final char charAt(int index){
		CharSequence cs=this.getText();
		char c=cs.charAt(index);
		return c;
	}

	@Override public final int length(){
		CharSequence cs=this.getText();
		int l=cs.length();
		return l;
	}

	@Override public final CharSequence subSequence(int start, int end){
		CharSequence cs=this.getText();
		CharSequence ss=cs.subSequence(start, end);
		return ss;
	}

	/** Exactly like String.getChars(): copy chars start through end-1 
	 * from this CharSequence into dest beginning at offset destoff. */
	@Override public final void getChars(int start, int end, char[] dest, int destoff){
		CharSequence cs=this.getText();
		for(int i=0; i<end-start; i++){
			char c=cs.charAt(i+start);
			dest[destoff+i]=c;
		}
	}

	@Override public final void removeSpan(Object what){
		throw new RuntimeException("not implemented");
	}

	@Override public final void setSpan(Object what, int start, int end, int flags){
		throw new RuntimeException("not implemented");
	}

	@Override public final int getSpanEnd(Object tag){
		//throw new RuntimeException("not implemented");
		return -1;
	}

	@Override public final int getSpanFlags(Object tag){
		throw new RuntimeException("not implemented");
	}

	/** XXX */
	@Override public final int getSpanStart(Object tag){	//tag==android.view.inputmethod.ComposingText
		//throw new RuntimeException("not implemented");
		return -1;
	}

	@Override public final <T> T[] getSpans(int arg0, int arg1, Class<T> arg2){
		throw new RuntimeException("not implemented");
	}

	@Override public final int nextSpanTransition(int start, int limit, @SuppressWarnings("rawtypes") Class type){
		throw new RuntimeException("not implemented");
	}

	/** Appends the character sequence text. */
	@Override public final Editable append(CharSequence text){
		this.editText.append(text);
		return this;
	}

	@Override public final Editable append(char text){
		String s=""+text;
		this.editText.append(s);
		return this;
	}

	@Override public final Editable append(CharSequence text, int start, int end){
		this.editText.append(text, start, end);
		return this;
	}

	@Override public final void clear(){
		this.replace(0, length(), "", 0, 0);
	}

	/** Removes all spans from the Editable, as if by calling removeSpan(Object) on each of them. */
	@Override public final void clearSpans(){
		throw new RuntimeException("not implemented");
	}

	@Override public final Editable delete(int st, int en){
		return replace(st, en, "", 0, 0);
	}

	@Override public final InputFilter[] getFilters(){
		InputFilter[]filters=this.editText.getFilters();
		return filters;
	}

	@Override public final Editable insert(int where, CharSequence text){
		return replace(where, where, text, 0, text.length());
	}

	@Override public final Editable insert(int where, CharSequence text, int start, int end){
		return replace(where, where, text, start, end);
	}

	@Override public final Editable replace(int st, int en, CharSequence text){
		return replace(st, en, text, 0, text.length());
	}

	/** Replaces the specified range (st…en) of text in this Editable 
	 * with a copy of the slice start…end from source. */
	@Override public final Editable replace(int st, int en, CharSequence source, int start, int end){
		//Maybe this can be done in a better way.
		CharSequence cs=source.subSequence(start, end);
		CharSequence text=this.getText();
		CharSequence pre=text.subSequence(0, st);
		CharSequence post=text.subSequence(en, text.length());
		text=pre.toString()+cs+post;
		this.editText.setText(text);
		return this;
	}

	@Override public final void setFilters(InputFilter[] filters){
		this.editText.setFilters(filters);
	}

}