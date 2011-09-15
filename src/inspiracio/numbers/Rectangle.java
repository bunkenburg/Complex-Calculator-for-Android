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

public class Rectangle extends Piclet
{

    protected Rectangle()
    {
    }

    public Rectangle(EC ec, EC ec1)
    {
        double d = Math.abs(ec.re() - ec1.re());
        double d1 = Math.abs(ec.im() - ec1.im());
        try
        {
            botLeft = ec.add(EC.mkCartesian(-d, -d1));
            topLeft = ec.add(EC.mkCartesian(-d, d1));
            botRight = ec.add(EC.mkCartesian(d, -d1));
            topRight = ec.add(EC.mkCartesian(d, d1));
            return;
        }
        catch(PartialException _ex)
        {
            return;
        }
    }

    public EC getCenter()
    {
        return EC.mkCartesian((botLeft.re() + topRight.re()) / 2D, (botLeft.im() + topRight.im()) / 2D);
    }

    public double top()
    {
        return topLeft.im();
    }

    public double bottom()
    {
        return botLeft.im();
    }

    public double left()
    {
        return topLeft.re();
    }

    public double right()
    {
        return topRight.re();
    }

    public double getHeight()
    {
        return Math.abs(botLeft.im() - topLeft.im());
    }

    public double getWidth()
    {
        return Math.abs(botLeft.re() - botRight.re());
    }

    protected void sample()
    {
        try
        {
            EC ec = botRight.subtract(botLeft).divide(30D);
            EC ec1 = botLeft;
            super.samples = new ECList(ec1, super.samples);
            for(int i = 0; i < 30; i++)
            {
                ec1 = ec1.add(ec);
                super.samples = new ECList(ec1, super.samples);
            }

            ec = topRight.subtract(botRight).divide(30D);
            ec1 = botRight;
            super.samples = new ECList(ec1, super.samples);
            for(int j = 0; j < 30; j++)
            {
                ec1 = ec1.add(ec);
                super.samples = new ECList(ec1, super.samples);
            }

            ec = topLeft.subtract(topRight).divide(30D);
            ec1 = topRight;
            super.samples = new ECList(ec1, super.samples);
            for(int k = 0; k < 30; k++)
            {
                ec1 = ec1.add(ec);
                super.samples = new ECList(ec1, super.samples);
            }

            ec = botLeft.subtract(topLeft).divide(30D);
            ec1 = topLeft;
            super.samples = new ECList(ec1, super.samples);
            for(int l = 0; l < 30; l++)
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

    public EC botLeft;
    public EC topLeft;
    public EC botRight;
    public EC topRight;
}
