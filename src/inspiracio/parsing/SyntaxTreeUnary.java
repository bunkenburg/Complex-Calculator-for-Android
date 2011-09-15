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

public class SyntaxTreeUnary extends SyntaxTree
{

    public SyntaxTreeUnary(int i, SyntaxTree syntaxtree)
    {
        token = i;
        argument = syntaxtree;
    }

    public String unparse()
        //throws BugException
    {
        if(token == 20)
            return "(" + argument.unparse() + ")!";
        else
            return SyntaxTree.token2String(token) + "(" + argument.unparse() + ")";
    }

    public void partialEvaluate()
        throws BugException
    {
        throw new BugException("SyntaxTreeUnary.partialEvaluate not implemented.");
    }

    public EC evaluate(EC ec)
        throws BugException, PartialException
    {
        EC ec1 = argument.evaluate(ec);
        EC ec2 = null;
        switch(token)
        {
        case -1: 
            throw new BugException("SyntaxTreeUnary.evaluate(NOTOKEN)");

        case 21: // '\025'
        case 22: // '\026'
        case 23: // '\027'
            throw new BugException("SyntaxTreeUnary.evaluate(acos,asin,atan)");

        case 20: // '\024'
            ec2 = ec1.fac();
            break;

        case 0: // '\0'
            ec2 = ec1;
            break;

        case 1: // '\001'
            ec2 = ec1.negate();
            break;

        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
            throw new BugException("SyntaxTreeUnary.evaluate with binary token " + SyntaxTree.token2String(token));

        case 5: // '\005'
            ec2 = ec1.conj();
            break;

        case 19: // '\023'
            ec2 = ec1.sinh();
            break;

        case 6: // '\006'
            ec2 = ec1.cosh();
            break;

        case 7: // '\007'
            ec2 = ec1.tanh();
            break;

        case 8: // '\b'
            ec2 = ec1.argument();
            break;

        case 9: // '\t'
            ec2 = ec1.cos();
            break;

        case 10: // '\n'
            ec2 = ec1.exp();
            break;

        case 11: // '\013'
            ec2 = ec1.modulus();
            break;

        case 12: // '\f'
            ec2 = ec1.opp();
            break;

        case 13: // '\r'
            ec2 = ec1.sin();
            break;

        case 14: // '\016'
            ec2 = ec1.tan();
            break;

        case 15: // '\017'
            ec2 = ec1.imPart();
            break;

        case 16: // '\020'
            ec2 = ec1.ln();
            break;

        case 17: // '\021'
            ec2 = ec1.rePart();
            break;

        case 18: // '\022'
            throw new BugException("SyntaxTreeUnary.evaluate with unexpected token " + SyntaxTree.token2String(token));
        }
        return ec2;
    }

    private SyntaxTree argument;
    private int token;
}
