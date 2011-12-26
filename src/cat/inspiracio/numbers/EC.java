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
package cat.inspiracio.numbers;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;

import cat.inspiracio.parsing.SyntaxTree;

import android.os.Parcel;
import android.os.Parcelable;

// Referenced classes of package bunkenba.numbers:
//            Borrow, PartialException

/** The Extended Complex numbers: that is, complex numbers and one infinity. 
 * Instances of EC are immutable. 
 * */
public final class EC implements Parcelable{

	//Constants ------------------------------------------------------------
	
    private static final String piString ="\u03C0";
    private static final String infinityString ="\u221E";
    public static final EC E = mkReal(Math.E);
    public static final EC HALF = mkReal(0.5D);
    @SuppressWarnings("unused")
	private static final EC HALFPI = mkReal(Math.PI/2);//1.5707963267948966
    public static final EC I = mkCartesian(0.0D, 1.0D);
    public static final EC INFINITY = mkInf();
    private static final EC NEGHALFI = mkCartesian(0.0D, -0.5D);
    public static final EC ONE = mkReal(1.0D);
    @SuppressWarnings("unused")
	private static final EC ONEANDHALFPI = mkReal(Math.PI*1.5);//4.71238898038469
    public static final EC PI = mkReal(Math.PI);
    @SuppressWarnings("unused")
	private static final EC TWOPI = mkReal(Math.PI*2);//6.283185307179586
    public static final EC ZERO = mkReal(0.0D);
    
    //Static state: affects continuous mapping z -> f(z) and some settings. ------------

    /** In mapping z -> arg(z), give the result closest to the last result,
     * rather than the principal result. */
    private static boolean argContinuous;
    
    private static int k;
    
    /** The last quadrant that a complex number was in. */
    private static int lastQuad;
    
    /** How many digits precision should toString give? */
    private static int PRECISION=4;
    
    /** Real numbers closer than this are considered equal. */
    private static double EPSILON=Math.pow(10D, -PRECISION);
    
    //State ----------------------------------------------------------------

    /** Is the number finite? */
    private final boolean finite;
    
    /** Real part of the number. Irrelevant if number is infinite. */
    private final double real;
    
    /** Imaginary part of the number. Irrelevant if number is infinite. */
    private final double imag;

    //Parcelable -------------------------------------------------------------
    //Serialisation in Android.
    
    @Override public final int describeContents(){return 0;}
    @Override public final void writeToParcel(Parcel p, int flags){
    	p.writeBooleanArray(new boolean[]{finite});
    	p.writeDouble(real);
    	p.writeDouble(imag);
    }
    public static Parcelable.Creator<EC>CREATOR=new Parcelable.Creator<EC>(){
    	@Override public final EC[]newArray(int size){return new EC[size];}
    	@Override public final EC createFromParcel(Parcel p){
    		boolean[] bs=new boolean[1];
    		p.readBooleanArray(bs);
    		boolean f=bs[0];
    		double r=p.readDouble();
    		double i=p.readDouble();
    		if(!f)
    			return EC.INFINITY;
    		return EC.mkCartesian(r, i);
    	}
	};
    
    //Settings -------------------------------------------------------------
    
    public static void setPrecision(int np){
    	int op=PRECISION;
        PRECISION = np;
        EPSILON = Math.pow(10D, -PRECISION);
        pcs.firePropertyChange("precision", op, np);
    }
    
    public static int getPrecision(){return PRECISION;}
    
    /** The bean is fake. */
    private static PropertyChangeSupport pcs=new PropertyChangeSupport(new Object());
    
    public static void addPropertyChangeListener(String p, PropertyChangeListener l){
    	pcs.addPropertyChangeListener(p, l);
    }
    
    public static void removePropertyChangeListener(String p, PropertyChangeListener l){
    	pcs.removePropertyChangeListener(p, l);
    }
    
    //Constructors -------------------------------------------------

    private EC(boolean flag, double re, double im){
        finite=flag;
        real=re;
        imag=im;
    }

    public static EC mkCartesian(double re, double im){
        if(Double.isInfinite(re) || Double.isInfinite(im))
            return INFINITY;
        else
            return new EC(true, re, im);
    }

    private static EC mkInf(){
    	double inf=1.0D / 0.0D;
        return new EC(false, inf, inf);
    }

    public static EC mkPolar(double d, double d1){
        if(Double.isInfinite(d))
            return INFINITY;
        double re=d*Math.cos(d1);
        double im=d*Math.sin(d1);
        return new EC(true, re, im);
    }

    public static EC mkReal(double d){
        return mkCartesian(d, 0.0D);
    }

    //Methods --------------------------------------------------
    
    /** @deprecated No clients */
    public final EC acos()throws PartialException{
        throw new PartialException("EC.acos not implemented.");
    }

    /** Which quadrant does this number lie in? */
    public final int quadrant(){
        double re = re();
        double im = im();
        if(re >= 0.0D && re >= 0.0D)
            return 1;
        if(re < 0.0D && im >= 0.0D)
            return 2;
        if(re < 0.0D && im < 0.0D)
            return 3;
        return re < 0.0D || im >= 0.0D ? 0 : 4;
    }

    /** Argument (=angle) of this number, in radians.
     * If argContinuous, will return the value closest to the result of the 
     * previous call. (Assuming there's only one thread.) */
    private double arg(){
        if(isFinite() && !isZero()){
            double d = Math.atan2(im(), re());
            if(argContinuous){
                int i = quadrant();
                if(lastQuad == 2 && i == 3)
                    k++;
                else if(lastQuad == 3 && i == 2)
                    k--;
                lastQuad = i;
                return d + (double)(2 * k) * Math.PI;
            }else{
                return d;
            }
        }else{
            return 0.0D;
        }
    }

    /** The argument of this number. */
    public final EC argument(){return mkReal(arg());}

    /** Addition. Infinity+infinity is undefined. */
    public final EC add(EC ec)throws PartialException{
        if(finite)
            if(ec.isFinite())
                return mkCartesian(re() + ec.re(), im() + ec.im());
            else
                return INFINITY;
        if(ec.isFinite())
            return INFINITY;
        else
            throw new PartialException(infinityString + " + " + infinityString);
    }

    /** @deprecated No clients */
    public final EC asin()throws PartialException{throw new PartialException("EC.asin not implemented");}

    /** @deprecated No clients */
    public final EC atan()throws PartialException{throw new PartialException("EC.atan not implemented");}

    /** Complex conjugate: just negate the imaginary part. */
    public final EC conj(){
        if(finite)
            return mkCartesian(re(), -im());
        return INFINITY;
    }

    /** cosine. Not defined for infinity. */
    public final EC cos()throws PartialException{
        EC ec = null;
        if(isFinite())
            try{
                EC ec1=this.multiply(I);
                ec=HALF.multiply(ec1.exp().add(ec1.negate().exp()));
            }
            catch(PartialException _ex) { }
        else
            throw new PartialException("cos " + infinityString);
        return ec;
    }

    /** Hyperbolic cosine. Not defined for infinity. */
    public final EC cosh()throws PartialException{
        EC ec = ZERO;
        if(isFinite())
            try{
                ec = HALF.multiply(exp().add(negate().exp()));
            }
            catch(PartialException _ex) { }
        else
            throw new PartialException("cosh " + infinityString);
        return ec;
    }
    
    /** Division by a real number. 
     * Not defined:
     * 	0/0
     * 	infinity/0
     * */
    public final EC divide(double d)throws PartialException{
        EC ec = null;
        if(this.isZero()){
            if(d == 0.0D)
                throw new PartialException("0/0");
            ec = ZERO;
        } else if(finite){
            if(d == 0.0D)
                ec = INFINITY;
            else
                ec = mkCartesian(re() / d, im() / d);
        } else{
            if(d == 0.0D)
                throw new PartialException(infinityString + "/0");
            ec = INFINITY;
        }
        return ec;
    }

    /** Division by complex number. 
     * Not defined:
     * 	0/0
     * 	0/infinity
     * 	infinity/0
     * 	infinity/infinity
     * */
    public final EC divide(EC ec)throws PartialException{
        EC ec1 = null;
        if(isZero()){
            if(ec.isZero())
                throw new PartialException("0/0");
            if(ec.isFinite())
                ec1 = ZERO;
            else
                throw new PartialException("0/"+infinityString);
        } else if(isFinite()){
            if(ec.isZero())
                ec1 = INFINITY;
            else
            if(ec.isFinite()){
                double ad[] = Borrow.div(re(), im(), ec.re(), ec.im());
                ec1 = mkCartesian(ad[0], ad[1]);
            } else{
                ec1 = ZERO;
            }
        } else{
            if(ec.isZero())
                throw new PartialException(infinityString + "/0");
            if(ec.isFinite())
                ec1 = INFINITY;
            else
                throw new PartialException(infinityString+"/"+infinityString);
        }
        return ec1;
    }

    /** Distance between two numbers. */
    public final double distance(EC ec){
        if(finite && ec.finite)
            return Math.sqrt(sqr(re() - ec.re()) + sqr(im() - ec.im()));
        double inf=1.0/0.0;
        return finite!=ec.finite ? inf : 0.0D;
    }

    /** Are two numbers exactly equal? 
     * (Not just to within EPSILON.) */
    public final boolean equals(EC ec){
        if(!isFinite() && !ec.isFinite())
            return true;
        if(isFinite() || ec.isFinite())
            return false;
        return Double.doubleToLongBits(re()) == Double.doubleToLongBits(ec.re()) && Double.doubleToLongBits(im()) == Double.doubleToLongBits(ec.im());
    }

    /** Exponential function. */
    public final EC exp(){
        if(finite)
            return mkPolar(Math.exp(re()), im());
        return INFINITY;
    }

    /** Factorial function. 
     * Defined only for naturals up to 25. */
    public final EC fac()throws PartialException{
        long n=this.longValue();
        if(n<0)
        	throw new PartialException("(" + n + ")!");
        if(25<n)
        	throw new PartialException(n + "!");
        //Normal calculation
        long factorial = 1L;
        for(; n > 0L; n--)
        	factorial *= n;
        return mkReal(factorial);
    }

    /** Is the number finite? */
    public final boolean isFinite(){return finite;}

    /** Imaginary part. */
    public final double im(){return imag;}

    /** Imaginary part as EC. */
    public final EC imPart(){
        if(finite)
            return mkReal(im());
        return INFINITY;
    }

    /** Is this number exactly zero? */
    private final boolean isZero(){return finite && re() == 0.0D && im() == 0.0D;}

    /** Natural logarithm.
     * Undefined for 0. */
    public final EC ln()throws PartialException{
        if(isFinite()){
            if(isZero())
                throw new PartialException("ln 0");
            return mkCartesian(Math.log(mod()), arg());
        } else
            return INFINITY;
    }

    /** Try to cast to a long. 
     * The imaginary part must be smaller than EPSILON
     * and the real part must be within EPSILON of a long number. */
    private long longValue()throws PartialException{
        if(Math.abs(im()) < EPSILON){
            long l = Math.round(re());
            if(Math.abs(re() - (double)l) < EPSILON)
                return l;
            throw new PartialException(this + " not integer");
        } else
            throw new PartialException(this + " not real");
    }

    /** Modulus: the distance to zero. */
    public final double mod(){
        double d;
        if(isFinite())
            d = Math.sqrt(sqr(re()) + sqr(im()));
        else
            d = 1.0D / 0.0D;//infinity
        return d;
    }

    /** Modulus: the distance to zero. */
    public final EC modulus(){
        if(finite)
            return mkReal(mod());
        return INFINITY;
    }

    /** Multiplication. 
     * Undefined:
     * 	0*infinity 
     * 	infinity*0. */
    public final EC multiply(EC ec)throws PartialException{
        EC ec1;
        if(isZero()){
            if(ec.isFinite())
                ec1 = ZERO;
            else
                throw new PartialException("0*" + infinityString);
        } else if(isFinite()){
            if(ec.isZero())
                ec1 = ZERO;
            else if(ec.isFinite())
                ec1 = mkCartesian(re() * ec.re() - im() * ec.im(), re() * ec.im() + ec.re() * im());
            else
                ec1 = INFINITY;
        } else{
            if(ec.isZero())
                throw new PartialException(infinityString + "*0");
            ec1 = INFINITY;
        }
        return ec1;
    }

    public final EC negate(){
        if(finite)
            return mkCartesian(-re(), -im());
        return INFINITY;
    }

    /** Returns the number that is opposite to this one on the Riemann sphere. */
    public final EC opp(){
        if(!isFinite())
            return ZERO;
        if(isZero())
            return INFINITY;
        return mkPolar(1.0D / mod(), arg() + Math.PI);//the usual case
    }

    /** Raises this number to the power of another, x^y.
     * This is x.
     * Undefined:
     * 	0^0
     * 	infinity^0
     * */
    public final EC power(EC y)throws PartialException{
    	EC x=this;
        if(x.isZero())
        	// 0^0 = undefined
            if(y.isZero())
                throw new PartialException("0^0");
        	// 0^y = 0
            else
                return ZERO;
        if(x.isFinite()) {
        	// x^0 = 1
            if(y.isZero())
                return ONE;
            if(y.isFinite()){									// x^y
                double mx=Math.log(x.mod());				// ln(mod(x))
                double ax=Math.atan2(x.im(),x.re());		// angle(x). Angle(-1) should be pi, not -pi. I want -pi < ax <= pi.
                if(ax==-Math.PI)
                	ax=Math.PI;								//Prefer pi over -pi
                double mr=Math.exp(mx*y.re() - y.im()*ax);	// mod(x^y) = e^(mod(x)*re(y) - angle(x)*im(y)) 
                double ar= y.im() * mx + y.re() * ax;		// angle(x^y) = im(y)*mod(x) + re(y)*angle(x)
                return mkPolar(mr, ar);
            }
            // x^inf = inf
            else
                return INFINITY;
        }
        // inf^0 = undefined
        if(y.isZero())
            throw new PartialException(infinityString + "^0");
        // inf^y = inf
        else
            return INFINITY;
    }

    /** The real part of this number, as double. */
    public final double re(){
        if(!finite)
            return 1.0D / 0.0D;//infinity
        return real;
    }

    /** The real part of this number, as EC. */
    public final EC rePart(){
        if(finite)
            return mkReal(re());
        return INFINITY;
    }

    /** The reciprocal: 1/z. */
    public final EC reciprocal(){
        if(isZero())
            return INFINITY;
        if(!isFinite())
            return ZERO;
        else
            return mkPolar(1.0D / mod(), arg() + Math.PI);//usual case
    }

    /** Forget the last quadrant result. */
    public static void resetArg(){
        lastQuad=0;
        k=0;
    }

    /** The argument function should be continuous. */
    public static void setArgContinuous(){argContinuous=true;}
    
    /** The argument function should give principal values. */
    public static void setArgPrincipal(){argContinuous=false;}

    /** sin.
     * Undefined sin(infinity). */
    public final EC sin()throws PartialException{
        EC ec = ZERO;
        if(isFinite())
            try{
                EC ec1 = multiply(I);
                ec = NEGHALFI.multiply(ec1.exp().subtract(ec1.negate().exp()));
            }
            catch(PartialException _ex) { }
        else
            throw new PartialException("sin " + infinityString);
        return ec;
    }

    /** Hyperbolic sin function.
     * Undefined sinh(infinity). */
    public final EC sinh()throws PartialException{
        EC ec = ZERO;
        if(isFinite())
            try{
                ec = HALF.multiply(exp().subtract(negate().exp()));
            }
            catch(PartialException _ex) { }
        else
            throw new PartialException("sinh "+infinityString);
        return ec;
    }

    /** Square of real number. */
    public static final double sqr(double d){return d*d;}

    /** Square root. */
    public final EC sqrt(){
        if(!isFinite())
            return INFINITY;
        double modulus=mod();
        double d1;
        double d2;
        if(modulus==0.0D)
            d1 = d2 = modulus;
        else if(re() > 0.0D){
            d1 = Math.sqrt(0.5D * (modulus + re()));
            d2 = im() / d1 / 2D;
        }else{
            d2 = Math.sqrt(0.5D * (modulus - re()));
            if(im() < 0.0D)
                d2 = -d2;
            d1 = im() / d2 / 2D;
        }
        return mkCartesian(d1, d2);
    }

    /** Subtraction. 
     * Undefined: infinity-infinity. */
    public final EC subtract(EC ec)throws PartialException{
        if(finite)
            if(ec.isFinite())
                return mkCartesian(re() - ec.re(), im() - ec.im());
            else
                return INFINITY;
        if(ec.isFinite())
            return INFINITY;
        throw new PartialException(infinityString + "-" + infinityString);
    }

    /** tan */
    public final EC tan()throws PartialException{return sin().divide(cos());}

    /** Hyperbolic tan function. */
    public final EC tanh()throws PartialException{return sinh().divide(cosh());}

    /** Print it nicely. 
     * The user sees output of this function. 
     * Rounds using EPSILON to good short results. */
    @Override public final String toString(){
    	if(!isFinite())
    		return infinityString;
    	double re = re();
    	double im = im();
    	String s = toString(re);

    	//It's just a real number.
    	if(Math.abs(im) < EPSILON)
    		return s;

    	String s1;
    	if(Math.abs(im - 1.0D) < EPSILON)
    		s1 = "i";
    	else if(Math.abs(im + 1.0D) < EPSILON)
    		s1 = "-i";
    	else
    		s1 = toString(im) + "i";

    	if(Math.abs(re) < EPSILON)
    		return s1;
    	else
    		return s + (im <= 0.0D ? "" : "+") + s1;
    }

    /** Print a real number nicely, for the user.
     * Prefers rounded numbers and multiples of pi. */
    public static String toString(double d){
    	if(Double.isNaN(d))
    		return Double.toString(d);//Whatever that gives.
    	if(Double.isInfinite(d))
    		return infinityString;
    	
    	//Detect multiples of pi. Having beautiful displays like 2pi is very educational.
    	long n=Math.round(d/Math.PI);//rounding
    	double reconstruct=n*Math.PI;
    	double distance=Math.abs(d-reconstruct);
    	if(distance<EPSILON){
    		if(n==0)
    			return "0";
    		if(n==1)
    			return piString;
    		if(n==-1)
    			return "-" + piString;
    		return n + piString;
    	}
    	    	
    	//General formatting
        NumberFormat nf=NumberFormat.getInstance();//Instantiate every time, because not threadsafe.
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(PRECISION);
        String s = nf.format(d);
        //Cut off trailing zeros.
        if(SyntaxTree.in('.', s)){
        	//Cuts off trailing zeros.
            StringBuffer b=new StringBuffer(s);
            while(b.charAt(b.length() - 1) == '0')
            	b.setLength(b.length() - 1);
            //Maybe cut off trailing '.' too.
            if(b.charAt(b.length() - 1) == '.')
                b.setLength(b.length() - 1);
            return b.toString();
        }
        return s;
    }

}