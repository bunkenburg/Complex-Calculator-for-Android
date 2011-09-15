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


// Referenced classes of package bunkenba.numbers:
//            Rectangle, Circle, EC, PartialException

public class Square extends Rectangle
{

    public Square(Circle circle)
    {
        try
        {
            super.botLeft = circle.center.add(EC.mkCartesian(-circle.radius, -circle.radius));
            super.topLeft = circle.center.add(EC.mkCartesian(-circle.radius, circle.radius));
            super.botRight = circle.center.add(EC.mkCartesian(circle.radius, -circle.radius));
            super.topRight = circle.center.add(EC.mkCartesian(circle.radius, circle.radius));
            return;
        }
        catch(PartialException _ex)
        {
            return;
        }
    }

    public Square(EC ec, EC ec1)
    {
        double d = Math.abs(ec.re() - ec1.re());
        double d1 = Math.abs(ec.im() - ec1.im());
        double d2 = (d + d1) / 2D;
        try
        {
            super.botLeft = ec.add(EC.mkCartesian(-d2, -d2));
            super.topLeft = ec.add(EC.mkCartesian(-d2, d2));
            super.botRight = ec.add(EC.mkCartesian(d2, -d2));
            super.topRight = ec.add(EC.mkCartesian(d2, d2));
            return;
        }
        catch(PartialException _ex)
        {
            return;
        }
    }

    public double getSide()
    {
        return Math.abs(super.botLeft.re() - super.botRight.re());
    }
}
