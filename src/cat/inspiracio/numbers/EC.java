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
    private static final String infinityString ="\u221E";// "inf";
    public static final EC E = mkReal(Math.E);
    public static final EC HALF = mkReal(0.5D);
    //private static final EC HALFPI = mkReal(Math.PI/2);
    public static final EC I = mkCartesian(0.0D, 1.0D);
    public static final EC INFINITY = mkInf();
    //private static final EC MINUSI = mkCartesian(0.0D, -1D);
    //private static final EC MINUSONE = mkReal(-1D);
    //private static final EC MINUSTWO = mkReal(-2D);
    //private static final EC NEGHALF = mkReal(-0.5D);
    private static final EC NEGHALFI = mkCartesian(0.0D, -0.5D);
    public static final EC ONE = mkReal(1.0D);
    //private static final EC ONEANDHALFPI = mkReal(Math.PI*1.5);
    public static final EC PI = mkReal(Math.PI);
    //private static final EC TWO = mkReal(2D);
    //private static final EC TWOPI = mkReal(Math.PI*2);
    public static final EC ZERO = mkReal(0.0D);
    
    //Static state: affects continuous mapping z -> f(z) and some settings. ------------

    private static boolean argContinuous;
    private static int k;
    private static int lastQuad;
    private static int PRECISION;
    private static double EPSILON;
    private static NumberFormat nf;
    
    static{
        PRECISION = 4;
        EPSILON = Math.pow(10D, -PRECISION);
        nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(10);
    }

    //State ----------------------------------------------------------------

    /** Is the number finite? */
    private final boolean finite;
    
    /** Real part of the number */
    private final double real;
    
    /** Imaginary part of the number */
    private final double imag;

    //Parcelable -------------------------------------------------------------
    
    @Override public int describeContents(){return 0;}
    @Override public void writeToParcel(Parcel p, int flags){
    	p.writeBooleanArray(new boolean[]{finite});
    	p.writeDouble(real);
    	p.writeDouble(imag);
    }
    public static Parcelable.Creator<EC>CREATOR=new Parcelable.Creator<EC>(){
    	@Override public EC[]newArray(int size){return new EC[size];}
    	@Override public EC createFromParcel(Parcel p){
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
        nf.setMaximumFractionDigits(np);
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
        else
            return new EC(true, d * Math.cos(d1), d * Math.sin(d1));
    }

    public static EC mkReal(double d){
        return mkCartesian(d, 0.0D);
    }

    //Methods --------------------------------------------------
    
    /** @deprecated No clients */
    public EC acos()throws PartialException{
        throw new PartialException("EC.acos not implemented.");
    }

    /** Which quadrant does this number lie in? */
    public int quadrant(){
        double d = re();
        double d1 = im();
        if(d >= 0.0D && d >= 0.0D)
            return 1;
        if(d < 0.0D && d1 >= 0.0D)
            return 2;
        if(d < 0.0D && d1 < 0.0D)
            return 3;
        return d < 0.0D || d1 >= 0.0D ? 0 : 4;
    }

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
            } else{
                return d;
            }
        } else{
            return 0.0D;
        }
    }

    public EC argument(){return mkReal(arg());}

    public EC add(EC ec)throws PartialException{
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
    public EC asin()throws PartialException{
        throw new PartialException("EC.asin not implemented");
    }

    /** @deprecated No clients */
    public EC atan()throws PartialException{
        throw new PartialException("EC.atan not implemented");
    }

    public EC conj(){
        if(finite)
            return mkCartesian(re(), -im());
        else
            return INFINITY;
    }

    public EC cos()throws PartialException{
        EC ec = null;
        if(isFinite())
            try{
                EC ec1 = multiply(I);
                ec = HALF.multiply(ec1.exp().add(ec1.negate().exp()));
            }
            catch(PartialException _ex) { }
        else
            throw new PartialException("cos " + infinityString);
        return ec;
    }

    public EC cosh()throws PartialException{
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

    public final EC divide(double d)throws PartialException{
        EC ec = null;
        if(isZero()){
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

    public final double distance(EC ec){
        if(finite && ec.finite)
            return Math.sqrt(sqr(re() - ec.re()) + sqr(im() - ec.im()));
        return finite != ec.finite ? (1.0D / 0.0D) : 0.0D;
    }

    public final boolean equals(EC ec){
        if(!isFinite() && !ec.isFinite())
            return true;
        if(isFinite() || ec.isFinite())
            return false;
        return Double.doubleToLongBits(re()) == Double.doubleToLongBits(ec.re()) && Double.doubleToLongBits(im()) == Double.doubleToLongBits(ec.im());
    }

    public final EC exp(){
        if(finite)
            return mkPolar(Math.exp(re()), im());
        else
            return INFINITY;
    }

    public final EC fac()throws PartialException{
        long l = longValue();
        if(l >= 0L){
            if(l <= 25L){
                long l1 = 1L;
                for(; l > 0L; l--)
                    l1 *= l;
                return mkReal(l1);
            } else{
                throw new PartialException(l+"!");
            }
        } else{
            throw new PartialException("(" + l + ")!");
        }
    }

    /** Is the number finite? */
    public final boolean isFinite(){return finite;}

    public double im(){return imag;}

    public final EC imPart(){
        if(finite)
            return mkReal(im());
        else
            return INFINITY;
    }

    private final boolean isZero(){
        return re() == 0.0D && im() == 0.0D;
    }

    public final EC ln()throws PartialException{
        if(isFinite()){
            if(isZero())
                throw new PartialException("ln 0");
            else
                return mkCartesian(Math.log(mod()), arg());
        } else{
            return INFINITY;
        }
    }

    private long longValue()throws PartialException{
        if(Math.abs(im()) < EPSILON){
            long l = Math.round(re());
            if(Math.abs(re() - (double)l) < EPSILON)
                return l;
            else
                throw new PartialException(this + " not integer");
        } else{
            throw new PartialException(this + " not real");
        }
    }

    public final double mod(){
        double d;
        if(isFinite())
            d = Math.sqrt(sqr(re()) + sqr(im()));
        else
            d = (1.0D / 0.0D);
        return d;
    }

    public final EC modulus(){
        if(finite)
            return mkReal(mod());
        else
            return INFINITY;
    }

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
        else
            return INFINITY;
    }

    /** Returns the number that is opposite to this one on the Riemann sphere. */
    public final EC opp(){
        if(!isFinite())
            return ZERO;
        if(isZero())
            return INFINITY;
        else
            return mkPolar(1.0D / mod(), arg() + Math.PI);
    }

    /** Raises this number to the power of another, x^y.
     * This is x.
     * @param y */
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

    public final double re(){
        if(!finite)
            return 1.0D / 0.0D;
        else
            return real;
    }

    public final EC rePart(){
        if(finite)
            return mkReal(re());
        else
            return INFINITY;
    }

    public final EC reciprocal(){
        if(isZero())
            return INFINITY;
        if(!isFinite())
            return ZERO;
        else
            return mkPolar(1.0D / mod(), arg() + Math.PI);
    }

    public static void resetArg(){
        lastQuad = 0;
        k = 0;
    }

    public static void setArgContinuous(){argContinuous = true;}

    public static void setArgPrincipal(){argContinuous = false;}

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

    public static final double sqr(double d){return d * d;}

    public final EC sqrt(){
        if(!isFinite())
            return INFINITY;
        double d = mod();
        double d1;
        double d2;
        if(d == 0.0D)
            d1 = d2 = d;
        else if(re() > 0.0D){
            d1 = Math.sqrt(0.5D * (d + re()));
            d2 = im() / d1 / 2D;
        } else{
            d2 = Math.sqrt(0.5D * (d - re()));
            if(im() < 0.0D)
                d2 = -d2;
            d1 = im() / d2 / 2D;
        }
        return mkCartesian(d1, d2);
    }

    public final EC subtract(EC ec)throws PartialException{
        if(finite)
            if(ec.isFinite())
                return mkCartesian(re() - ec.re(), im() - ec.im());
            else
                return INFINITY;
        if(ec.isFinite())
            return INFINITY;
        else
            throw new PartialException(infinityString + "-" + infinityString);
    }

    public final EC tan()throws PartialException{
        return sin().divide(cos());
    }

    public final EC tanh()throws PartialException{
        return sinh().divide(cosh());
    }

    /** >Print it nicely. */
    @Override public final String toString(){
        if(isFinite()){
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
        }else
            return infinityString;
    }

    /** Print a real number nicely. */
    public static String toString(double d){
    	//Some special real numbers
    	if(Math.abs(d-Math.PI)<EPSILON)
    		return piString;
    	if(Math.abs(d+Math.PI)<EPSILON)
    		return "-" + piString;
    	
    	//General formatting
        String s = nf.format(d);
        if(SyntaxTree.in('.', s)){
        	//Cuts off trailing zeros.
            StringBuffer b=new StringBuffer(s);
            while(b.charAt(b.length() - 1) == '0')
            	b.setLength(b.length() - 1);
            //Maybe cut off trailing '.' too.
            if(b.charAt(b.length() - 1) == '.')
                b.setLength(b.length() - 1);
            return b.toString();
        } else
            return s;
    }

}