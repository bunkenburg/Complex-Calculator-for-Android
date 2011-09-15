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

// Referenced classes of package bunkenba.parsing:
//            SyntaxTree

public class SyntaxTreeBinary extends SyntaxTree
{

    public SyntaxTreeBinary(int i, SyntaxTree syntaxtree, SyntaxTree syntaxtree1)
    {
        token = i;
        left = syntaxtree;
        right = syntaxtree1;
    }

    public String unparse()
        //throws BugException
    {
        return "(" + left.unparse() + ")" + SyntaxTree.token2String(token) + "(" + right.unparse() + ")";
    }

    public void partialEvaluate()
        throws BugException
    {
        throw new BugException("SyntaxTreeBinary.partialEvaluate() not implemented yet.");
    }

    public EC evaluate(EC ec)
        throws BugException, PartialException
    {
        EC ec1 = left.evaluate(ec);
        EC ec2 = right.evaluate(ec);
        EC ec3 = EC.ZERO;
        switch(token)
        {
        case -1: 
            throw new BugException("SyntaxTreeBinary.evaluate(NOTOKEN)");

        case 0: // '\0'
            ec3 = ec1.add(ec2);
            break;

        case 1: // '\001'
            ec3 = ec1.subtract(ec2);
            break;

        case 2: // '\002'
            ec3 = ec1.multiply(ec2);
            break;

        case 3: // '\003'
            ec3 = ec1.divide(ec2);
            break;

        case 4: // '\004'
            ec3 = ec1.power(ec2);
            break;

        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
        case 9: // '\t'
        case 10: // '\n'
        case 11: // '\013'
        case 12: // '\f'
        case 13: // '\r'
        case 14: // '\016'
        case 15: // '\017'
        case 16: // '\020'
        case 17: // '\021'
        case 18: // '\022'
            throw new BugException("SyntaxTreeBinary.evaluate with unary token " + SyntaxTree.token2String(token));
        }
        return ec3;
    }

    private SyntaxTree left;
    private SyntaxTree right;
    private int token;
}
