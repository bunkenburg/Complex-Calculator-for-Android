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
package inspiracio.parsing;

import inspiracio.numbers.*;

import java.text.ParseException;
import java.text.ParsePosition;

// Referenced classes of package bunkenba.parsing:
//            SyntaxTreeBinary, SyntaxTreeConstant, SyntaxTreeUnary, SyntaxTreeVariable

public abstract class SyntaxTree
{

    private static int first(char c, String s, int i, int j)
    {
        int k = i;
        for(int l = 0; k < j && (l != 0 || s.charAt(k) != c); k++)
            if(s.charAt(k) == '(')
                l++;
            else
            if(s.charAt(k) == ')')
                l--;

        if(k < j)
            return k;
        else
            return -1;
    }

    private static SyntaxTree getFactor(String s, ParsePosition parseposition, ParsePosition parseposition1)
        throws ParseException
    {
        Object obj = null;
        int i;
        if(s.charAt(parseposition.getIndex()) == '(')
        {
            int j = first(')', s, parseposition.getIndex() + 1, parseposition1.getIndex());
            if(j == -1)
                throw new ParseException("unmatched bracket", parseposition.getIndex());
            obj = parse(s, parseposition.getIndex() + 1, j);
            parseposition.setIndex(j + 1);
        } else
        if((i = recogniseFunction(s, parseposition, parseposition1)) != -1)
        {
            obj = new SyntaxTreeUnary(i, parse(s, parseposition.getIndex(), parseposition1.getIndex()));
            parseposition.setIndex(parseposition1.getIndex());
        } else
        if(in(s.charAt(parseposition.getIndex()), "0123456789"))
            obj = readDigits(s, parseposition, parseposition1);
        else
        if(in(s.charAt(parseposition.getIndex()), "iep\u03C0\u221E"))
            obj = readConstant(s, parseposition, parseposition1);
        else
        if(in(s.charAt(parseposition.getIndex()), "zx"))
            obj = readVariable(s, parseposition, parseposition1);
        if(obj == null)
            throw new ParseException(s, parseposition.getIndex());
        for(; parseposition.getIndex() < parseposition1.getIndex() && s.charAt(parseposition.getIndex()) == '!'; parseposition.setIndex(parseposition.getIndex() + 1))
            obj = new SyntaxTreeUnary(20, ((SyntaxTree) (obj)));

        if(parseposition.getIndex() < parseposition1.getIndex() && s.charAt(parseposition.getIndex()) == '^')
        {
            obj = new SyntaxTreeBinary(4, ((SyntaxTree) (obj)), parse(s, parseposition.getIndex() + 1, parseposition1.getIndex()));
            parseposition.setIndex(parseposition1.getIndex());
        }
        return ((SyntaxTree) (obj));
    }

    /** Does the String contains this character? */
    public static boolean in(char c, String s){
        return s.indexOf(c) != -1;
    }

    private static int last(char c, String s, int i, int j){
        int k = j - 1;
        for(int l = 0; i <= k && (l != 0 || s.charAt(k) != c); k--)
            if(s.charAt(k) == '(')
                l++;
            else if(s.charAt(k) == ')')
                l--;
        if(i <= k)
            return k;
        else
            return -1;
    }

    public static void main(String args[])throws Exception{
    	parse("modz*e^iargz");
    }

    public static SyntaxTree parse(String s)throws ParseException{
        s = stripBlanks(s);
        return parse(s, 0, s.length());
    }

    private static SyntaxTree parse(String s, int i, int j)throws ParseException{
        if(i >= j)
            throw new ParseException("SyntaxTree.parse(empty)", i);
        int k;
        Object obj;
        if((k = last('+', s, i, j)) > -1)
            obj = bracketPlusMinus(s, i, k, j, 0);
        else if((k = last('-', s, i, j)) > -1)
            obj = bracketPlusMinus(s, i, k, j, 1);
        else if((k = last('*', s, i, j)) > -1)
            obj = bracketBin(s, i, k, j, 2);
        else if((k = last('/', s, i, j)) > -1){
            obj = bracketBin(s, i, k, j, 3);
        } else{
            ParsePosition parseposition = new ParsePosition(i);
            ParsePosition parseposition1 = new ParsePosition(j);
            for(obj = getFactor(s, parseposition, parseposition1); parseposition.getIndex() != parseposition1.getIndex(); obj = new SyntaxTreeBinary(2, ((SyntaxTree) (obj)), getFactor(s, parseposition, parseposition1)));
        }
        return ((SyntaxTree) (obj));
    }

    private static SyntaxTree readConstant(String s, ParsePosition parseposition, ParsePosition parseposition1) throws ParseException{
        EC ec = null;
        int i = parseposition.getIndex();
        if(s.startsWith("inf", i)){
            ec = EC.INFINITY;
            i += 3;
        } else if(s.startsWith("i", i))
        {
            ec = EC.I;
            i++;
        } else
        if(s.startsWith("e", i))
        {
            ec = EC.E;
            i++;
        } else
        if(s.startsWith("\u03C0", i))
        {
            ec = EC.PI;
            i++;
        } else
        if(s.startsWith("\u221E", i))
        {
            ec = EC.INFINITY;
            i++;
        } else
        if(s.startsWith("pi", i))
        {
            ec = EC.PI;
            i += 2;
        } else
        {
            throw new ParseException("readConstant " + s, i);
        }
        parseposition.setIndex(i);
        return new SyntaxTreeConstant(ec);
    }

    private static SyntaxTree readDigits(String s, ParsePosition parseposition, ParsePosition parseposition1)
        throws ParseException
    {
        int i;
        for(i = parseposition.getIndex(); i < parseposition1.getIndex() && in(s.charAt(i), "0123456789"); i++);
        EC ec;
        if(parseposition1.getIndex() == i)
            ec = EC.mkReal((new Double(s.substring(parseposition.getIndex(), i))).doubleValue());
        else
        if(s.charAt(i) == '.')
        {
            int j;
            for(j = i + 1; j < parseposition1.getIndex() && in(s.charAt(j), "0123456789"); j++);
            if(i + 1 <= j - 1)
            {
                i = j;
                ec = EC.mkReal((new Double(s.substring(parseposition.getIndex(), i))).doubleValue());
            } else
            {
                throw new ParseException("SyntaxTree.readDigits: decimal point followed by non-digit", i);
            }
        } else
        {
            ec = EC.mkReal((new Double(s.substring(parseposition.getIndex(), i))).doubleValue());
        }
        parseposition.setIndex(i);
        return new SyntaxTreeConstant(ec);
    }

    private static SyntaxTree readVariable(String s, ParsePosition parseposition, ParsePosition parseposition1)
    {
        parseposition.setIndex(parseposition.getIndex() + 1);
        return new SyntaxTreeVariable();
    }

    private static int recogniseFunction(String s, ParsePosition parseposition, ParsePosition parseposition1)
    {
        int i = parseposition.getIndex();
        if(s.startsWith("acos", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 4);
            return 22;
        }
        if(s.startsWith("arg", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 3);
            return 8;
        }
        if(s.startsWith("asin", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 4);
            return 21;
        }
        if(s.startsWith("atan", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 4);
            return 23;
        }
        if(s.startsWith("conj", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 4);
            return 5;
        }
        if(s.startsWith("cosh", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 4);
            return 6;
        }
        if(s.startsWith("cos", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 3);
            return 9;
        }
        if(s.startsWith("D", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 1);
            return 18;
        }
        if(s.startsWith("exp", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 3);
            return 10;
        }
        if(s.startsWith("Im", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 2);
            return 15;
        }
        if(s.startsWith("ln", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 2);
            return 16;
        }
        if(s.startsWith("mod", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 3);
            return 11;
        }
        if(s.startsWith("opp", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 3);
            return 12;
        }
        if(s.startsWith("Re", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 2);
            return 17;
        }
        if(s.startsWith("sinh", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 4);
            return 19;
        }
        if(s.startsWith("sin", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 3);
            return 13;
        }
        if(s.startsWith("tanh", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 4);
            return 7;
        }
        if(s.startsWith("tan", parseposition.getIndex()))
        {
            parseposition.setIndex(i + 3);
            return 14;
        } else
        {
            return -1;
        }
    }

    public static void say(String s)
    {
    }

    public static void say(boolean flag)
    {
        say((new Boolean(flag)).toString());
    }

    public abstract String unparse();

    public String toString()
    {
        return unparse();
    }

    public abstract void partialEvaluate()
        throws BugException, PartialException;

    public abstract EC evaluate(EC ec)
        throws BugException, PartialException;

    public static String stripBlanks(String s)
    {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        for(int i = 0; i < s.length(); i++)
            if(!Character.isWhitespace(s.charAt(i)))
                stringbuffer.append(s.charAt(i));

        return stringbuffer.toString();
    }

    protected static String token2String(int i)
    {
        switch(i)
        {
        case -1: 
            return "NOTOKEN";

        case 0: // '\0'
            return "+";

        case 1: // '\001'
            return "-";

        case 2: // '\002'
            return "*";

        case 3: // '\003'
            return "/";

        case 4: // '\004'
            return "^";

        case 22: // '\026'
            return "acos";

        case 21: // '\025'
            return "asin";

        case 23: // '\027'
            return "atan";

        case 5: // '\005'
            return "conj";

        case 6: // '\006'
            return "cosh";

        case 7: // '\007'
            return "tanh";

        case 19: // '\023'
            return "sinh";

        case 8: // '\b'
            return "arg";

        case 9: // '\t'
            return "cos";

        case 10: // '\n'
            return "exp";

        case 11: // '\013'
            return "mod";

        case 12: // '\f'
            return "opp";

        case 13: // '\r'
            return "sin";

        case 14: // '\016'
            return "tan";

        case 15: // '\017'
            return "Im";

        case 16: // '\020'
            return "ln";

        case 17: // '\021'
            return "Re";

        case 18: // '\022'
            return "D";

        case 20: // '\024'
            return "!";
        }
        return "a token";
    }

    private static SyntaxTree bracketBin(String s, int i, int j, int k, int l)
        throws ParseException
    {
        SyntaxTree syntaxtree = parse(s, i, j);
        SyntaxTree syntaxtree1 = parse(s, j + 1, k);
        return new SyntaxTreeBinary(l, syntaxtree, syntaxtree1);
    }

    private static SyntaxTree bracketPlusMinus(String s, int i, int j, int k, int l)
        throws ParseException
    {
        if(j == i)
        {
            SyntaxTree syntaxtree = parse(s, i + 1, k);
            return new SyntaxTreeUnary(l, syntaxtree);
        } else
        {
            return bracketBin(s, i, j, k, l);
        }
    }

    public SyntaxTree()
    {
    }

    //private static final String constants = "iep\u03C0\u221E";
    //private static final String variables = "zx";
    //private static final String digits = "0123456789";
    //private static final String functionInitials = "acDeIlmoRst";
    protected static final int NOTOKEN = -1;
    protected static final int SUMTOKEN = 0;
    protected static final int DIFFERENCETOKEN = 1;
    protected static final int PRODUCTTOKEN = 2;
    protected static final int QUOTIENTTOKEN = 3;
    protected static final int POWERTOKEN = 4;
    protected static final int CONJTOKEN = 5;
    protected static final int COSHTOKEN = 6;
    protected static final int TANHTOKEN = 7;
    protected static final int ARGTOKEN = 8;
    protected static final int COSTOKEN = 9;
    protected static final int EXPTOKEN = 10;
    protected static final int MODTOKEN = 11;
    protected static final int OPPTOKEN = 12;
    protected static final int SINTOKEN = 13;
    protected static final int TANTOKEN = 14;
    protected static final int IMTOKEN = 15;
    protected static final int LNTOKEN = 16;
    protected static final int RETOKEN = 17;
    protected static final int DTOKEN = 18;
    protected static final int SINHTOKEN = 19;
    protected static final int FACTOKEN = 20;
    protected static final int ASINTOKEN = 21;
    protected static final int ACOSTOKEN = 22;
    protected static final int ATANTOKEN = 23;
}
