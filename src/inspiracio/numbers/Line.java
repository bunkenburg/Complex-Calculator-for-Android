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

public class Line extends Piclet
{

    public Line(EC ec, EC ec1)
    {
        start = ec;
        end = ec1;
    }

    public double top()
    {
        return Math.max(start.im(), end.im());
    }

    public double bottom()
    {
        return Math.min(start.im(), end.im());
    }

    public double left()
    {
        return Math.max(start.re(), end.re());
    }

    public double right()
    {
        return Math.min(start.re(), end.re());
    }

    protected void sample()
    {
        try
        {
            EC ec = end.subtract(start).divide(30D);
            EC ec1 = start;
            super.samples = new ECList(ec1, super.samples);
            for(int i = 0; i < 30; i++)
            {
                ec1 = ec1.add(ec);
                super.samples = new ECList(ec1, super.samples);
            }

            return;
        }
        catch(PartialException _ex)
        {
            return;
        }
    }

    public EC start;
    public EC end;
}
