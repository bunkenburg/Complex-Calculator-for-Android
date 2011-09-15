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
//            Piclet, EC, ECList, PartialException

public class Circle extends Piclet
{

    public Circle(EC ec, double d)
    {
        if(ec.finite())
            center = ec;
        else
            center = EC.ZERO;
        radius = d;
    }

    public Circle(EC ec, EC ec1)
    {
        if(ec.finite())
            center = ec;
        else
            center = EC.ZERO;
        if(ec1.finite())
        {
            radius = center.distance(ec1);
            return;
        } else
        {
            radius = (1.0D / 0.0D);
            return;
        }
    }

    public double top()
    {
        return center.im() + radius;
    }

    public double bottom()
    {
        return center.im() - radius;
    }

    public double left()
    {
        return center.re() - radius;
    }

    public double right()
    {
        return center.re() + radius;
    }

    protected void sample()
    {
        double d = 0.20943951023931953D;
        double d1 = 0.0D;
        for(int i = 0; i <= 30; i++)
        {
            try
            {
                super.samples = new ECList(center.add(EC.mkPolar(radius, d1)), super.samples);
            }
            catch(PartialException _ex) { }
            d1 += d;
        }

    }

    public EC center;
    public double radius;
}
