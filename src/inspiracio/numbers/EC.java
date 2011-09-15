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
package inspiracio.numbers;

import inspiracio.parsing.SyntaxTree;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;

// Referenced classes of package bunkenba.numbers:
//            Borrow, PartialException

public final class EC{

    private static final String piString ="\u03C0";
    private static final String infinityString ="\u221E";// "inf";
    private static boolean argContinuous;
    private static int k;
    private static int lastQuad;
    public static final EC E = mkReal(2.7182818284590451D);
    public static final EC HALF = mkReal(0.5D);
    //private static final EC HALFPI = mkReal(1.5707963267948966D);
    public static final EC I = mkCartesian(0.0D, 1.0D);
    public static final EC INFINITY = mkInf();
    //private static final EC MINUSI = mkCartesian(0.0D, -1D);
    //private static final EC MINUSONE = mkReal(-1D);
    //private static final EC MINUSTWO = mkReal(-2D);
    //private static final EC NEGHALF = mkReal(-0.5D);
    private static final EC NEGHALFI = mkCartesian(0.0D, -0.5D);
    public static final EC ONE = mkReal(1.0D);
    //private static final EC ONEANDHALFPI = mkReal(4.7123889803846897D);
    public static final EC PI = mkReal(Math.PI);
    //private static final EC TWO = mkReal(2D);
    //private static final EC TWOPI = mkReal(6.2831853071795862D);
    public static final EC ZERO = mkReal(0.0D);
    private static int PRECISION;
    private static double EPSILON;
    private static NumberFormat nf;
    private final boolean finite;
    private final double real;
    private final double imag;

    static{
        PRECISION = 4;
        EPSILON = Math.pow(10D, -PRECISION);
        nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(10);
    }

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

    private EC(boolean flag, double d, double d1){
        finite = flag;
        real = d;
        imag = d1;
    }

    public static EC mkCartesian(double d, double d1){
        if(Double.isInfinite(d) || Double.isInfinite(d1))
            return INFINITY;
        else
            return new EC(true, d, d1);
    }

    private static EC mkInf(){
        return new EC(false, (1.0D / 0.0D), (1.0D / 0.0D));
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
    
    public EC acos()throws PartialException{
        throw new PartialException("EC.acos not implemented.");
    }

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
        if(finite() && !isZero()){
            double d = Math.atan2(im(), re());
            if(argContinuous){
                int i = quadrant();
                if(lastQuad == 2 && i == 3)
                    k++;
                else if(lastQuad == 3 && i == 2)
                    k--;
                lastQuad = i;
                return d + (double)(2 * k) * 3.1415926535897931D;
            } else{
                return d;
            }
        } else{
            return 0.0D;
        }
    }

    public EC argument(){
        return mkReal(arg());
    }

    public EC add(EC ec)throws PartialException{
        if(finite)
            if(ec.finite())
                return mkCartesian(re() + ec.re(), im() + ec.im());
            else
                return INFINITY;
        if(ec.finite())
            return INFINITY;
        else
            throw new PartialException("EC.add(EC): inf+inf");
    }

    public EC asin()throws PartialException{
        throw new PartialException("EC.asin not implemented");
    }

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
        if(finite())
            try{
                EC ec1 = multiply(I);
                ec = HALF.multiply(ec1.exp().add(ec1.negate().exp()));
            }
            catch(PartialException _ex) { }
        else
            throw new PartialException("EC.cos(inf)");
        return ec;
    }

    public EC cosh()throws PartialException{
        EC ec = ZERO;
        if(finite())
            try{
                ec = HALF.multiply(exp().add(negate().exp()));
            }
            catch(PartialException _ex) { }
        else
            throw new PartialException("EC.cosh(inf)");
        return ec;
    }

    public EC divide(double d)throws PartialException{
        EC ec = null;
        if(isZero()){
            if(d == 0.0D)
                throw new PartialException("EC.divide(double): 0/0");
            ec = ZERO;
        } else if(finite){
            if(d == 0.0D)
                ec = INFINITY;
            else
                ec = mkCartesian(re() / d, im() / d);
        } else{
            if(d == 0.0D)
                throw new PartialException("EC.divide(double): inf/0");
            ec = INFINITY;
        }
        return ec;
    }

    public EC divide(EC ec)throws PartialException{
        EC ec1 = null;
        if(isZero()){
            if(ec.isZero())
                throw new PartialException("EC.divide: 0/0");
            if(ec.finite())
                ec1 = ZERO;
            else
                throw new PartialException("EC.divide: 0/inf");
        } else if(finite()){
            if(ec.isZero())
                ec1 = INFINITY;
            else
            if(ec.finite()){
                double ad[] = Borrow.div(re(), im(), ec.re(), ec.im());
                ec1 = mkCartesian(ad[0], ad[1]);
            } else{
                ec1 = ZERO;
            }
        } else{
            if(ec.isZero())
                throw new PartialException("EC.divide: inf/0");
            if(ec.finite())
                ec1 = INFINITY;
            else
                throw new PartialException("EC.divide: inf/inf");
        }
        return ec1;
    }

    public double distance(EC ec){
        if(finite && ec.finite)
            return Math.sqrt(sqr(re() - ec.re()) + sqr(im() - ec.im()));
        return finite != ec.finite ? (1.0D / 0.0D) : 0.0D;
    }

    public boolean equals(EC ec){
        if(!finite() && !ec.finite())
            return true;
        if(finite() || ec.finite())
            return false;
        return Double.doubleToLongBits(re()) == Double.doubleToLongBits(ec.re()) && Double.doubleToLongBits(im()) == Double.doubleToLongBits(ec.im());
    }

    public EC exp(){
        if(finite)
            return mkPolar(Math.exp(re()), im());
        else
            return INFINITY;
    }

    public EC fac()throws PartialException{
        long l = longValue();
        if(l >= 0L){
            if(l <= 25L){
                long l1 = 1L;
                for(; l > 0L; l--)
                    l1 *= l;
                return mkReal(l1);
            } else{
                throw new PartialException("too big");
            }
        } else{
            throw new PartialException("negative");
        }
    }

    public boolean finite(){return finite;}

    public double im(){return imag;}

    public EC imPart(){
        if(finite)
            return mkReal(im());
        else
            return INFINITY;
    }

    private boolean isZero(){
        return re() == 0.0D && im() == 0.0D;
    }

    public EC ln()throws PartialException{
        if(finite()){
            if(isZero())
                throw new PartialException("EC.ln(0)");
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
                throw new PartialException("not integer");
        } else{
            throw new PartialException("not real");
        }
    }

    public double mod(){
        double d;
        if(finite())
            d = Math.sqrt(sqr(re()) + sqr(im()));
        else
            d = (1.0D / 0.0D);
        return d;
    }

    public EC modulus(){
        if(finite)
            return mkReal(mod());
        else
            return INFINITY;
    }

    public EC multiply(EC ec)throws PartialException{
        EC ec1;
        if(isZero()){
            if(ec.finite())
                ec1 = ZERO;
            else
                throw new PartialException("EC.multiply: 0*inf");
        } else if(finite()){
            if(ec.isZero())
                ec1 = ZERO;
            else if(ec.finite())
                ec1 = mkCartesian(re() * ec.re() - im() * ec.im(), re() * ec.im() + ec.re() * im());
            else
                ec1 = INFINITY;
        } else{
            if(ec.isZero())
                throw new PartialException("EC.multiply: fin*0");
            ec1 = INFINITY;
        }
        return ec1;
    }

    public EC negate(){
        if(finite)
            return mkCartesian(-re(), -im());
        else
            return INFINITY;
    }

    public EC opp(){
        if(!finite())
            return ZERO;
        if(isZero())
            return INFINITY;
        else
            return mkPolar(1.0D / mod(), arg() + 3.1415926535897931D);
    }

    public EC power(EC ec)throws PartialException{
        if(isZero())
            if(ec.isZero())
                throw new PartialException("EC.power(0,0)");
            else
                return ZERO;
        if(finite()) {
            if(ec.isZero())
                return ONE;
            if(ec.finite()){
                double d = Math.log(mod());
                double d1 = Math.atan2(im(), re());
                double d2 = Math.exp(d * ec.re() - ec.im() * d1);
                d1 = ec.im() * d + ec.re() * d1;
                return mkPolar(d2, d1);
            } else{
                return INFINITY;
            }
        }
        if(ec.isZero())
            throw new PartialException("EC.power(inf, 0)");
        else
            return INFINITY;
    }

    public double re(){
        if(!finite)
            return (1.0D / 0.0D);
        else
            return real;
    }

    public EC rePart(){
        if(finite)
            return mkReal(re());
        else
            return INFINITY;
    }

    public EC reciprocal(){
        if(isZero())
            return INFINITY;
        if(!finite())
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

    public EC sin()throws PartialException{
        EC ec = ZERO;
        if(finite())
            try{
                EC ec1 = multiply(I);
                ec = NEGHALFI.multiply(ec1.exp().subtract(ec1.negate().exp()));
            }
            catch(PartialException _ex) { }
        else
            throw new PartialException("EC.sin(inf)");
        return ec;
    }

    public EC sinh()throws PartialException{
        EC ec = ZERO;
        if(finite())
            try{
                ec = HALF.multiply(exp().subtract(negate().exp()));
            }
            catch(PartialException _ex) { }
        else
            throw new PartialException("EC.sinh(inf)");
        return ec;
    }

    public static final double sqr(double d){return d * d;}

    public EC sqrt(){
        if(!finite())
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

    public EC subtract(EC ec)throws PartialException{
        if(finite)
            if(ec.finite())
                return mkCartesian(re() - ec.re(), im() - ec.im());
            else
                return INFINITY;
        if(ec.finite())
            return INFINITY;
        else
            throw new PartialException("EC.subtract: inf-inf");
    }

    public EC tan()throws PartialException{
        return sin().divide(cos());
    }

    public EC tanh()throws PartialException{
        return sinh().divide(cosh());
    }

    /** >Print it nicely. */
    @Override public String toString(){
        if(finite()){
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