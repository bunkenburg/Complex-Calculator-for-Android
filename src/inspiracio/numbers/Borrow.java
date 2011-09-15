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
class Borrow
{

    public static double hypot(double d, double d1)
    {
        double d2 = d;
        double d3 = d1;
        long l1 = (Double.doubleToLongBits(d) << 1) >>> 1;
        long l2 = (Double.doubleToLongBits(d1) << 1) >>> 1;
        int k = (int)(l1 >>> 32);
        int l = (int)(l2 >>> 32);
        if(l > k)
        {
            int i = k;
            k = l;
            l = i;
            long l3 = l1;
            l1 = l2;
            l2 = l3;
        }
        d2 = Double.longBitsToDouble(l1);
        d3 = Double.longBitsToDouble(l2);
        if(k - l > 0x3c00000)
            return d2 + d3;
        int i1 = 0;
        int j = 0;
        if(k > 0x5f300000)
        {
            if(k >= 0x7ff00000)
            {
                double d10 = d2 + d3;
                if((l1 & 0xfffffffffffffL) == 0L)
                    d10 = d2;
                if((l2 ^ 0x7ff0000000000000L) == 0L)
                    d10 = d3;
                return d10;
            }
            j = 0xda800000;
            i1 += 600;
        }
        if(l < 0x20b00000)
            if(l <= 0xfffff)
            {
                if(l2 == 0L)
                    return d2;
                double d4 = Double.longBitsToDouble(0x7fd0000000000000L);
                d3 *= d4;
                d2 *= d4;
                i1 -= 1022;
            } else
            {
                i1 -= 600;
                j = 0x25800000;
            }
        if(j != 0)
        {
            k += j;
            l += j;
            l1 += j << 32;
            l2 += j << 32;
            d2 = Double.longBitsToDouble(l1);
            d3 = Double.longBitsToDouble(l2);
        }
        double d11 = d2 - d3;
        if(d11 > d3)
        {
            double d5 = Double.longBitsToDouble((long)k << 32);
            double d8 = d2 - d5;
            d11 = d5 * d5 - (d3 * -d3 - d8 * (d2 + d5));
        } else
        {
            d2 += d2;
            double d12 = Double.longBitsToDouble((long)l << 32);
            double d13 = d3 - d12;
            double d6 = Double.longBitsToDouble((long)(k + 0x100000) << 32);
            double d9 = d2 - d6;
            d11 = d6 * d12 - (d11 * -d11 - (d6 * d13 + d9 * d3));
        }
        d11 = Math.sqrt(d11);
        if(i1 != 0)
        {
            double d7 = Double.longBitsToDouble(0x3ff0000000000000L + ((long)i1 << 52));
            d11 *= d7;
        }
        return d11;
    }

    public static double[] div(double d, double d1, double d2, double d3)
    {
        double d4 = Math.abs(d2);
        double d5 = Math.abs(d3);
        double d6;
        double d7;
        double d10;
        if(d4 <= d5)
        {
            double d8 = d2 / d3;
            d10 = d3 * (1.0D + d8 * d8);
            d6 = d * d8 + d1;
            d7 = d1 * d8 - d;
        } else
        {
            double d9 = d3 / d2;
            d10 = d2 * (1.0D + d9 * d9);
            d6 = d + d1 * d9;
            d7 = d1 - d * d9;
        }
        double ad[] = {
            d6 / d10, d7 / d10
        };
        return ad;
    }

    Borrow()
    {
    }
}
