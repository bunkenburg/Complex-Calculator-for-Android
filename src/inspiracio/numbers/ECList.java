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
//            EC

public class ECList
{

    public ECList(EC ec, ECList eclist)
    {
        head = ec;
        tail = eclist;
    }

    public ECList cons(EC ec)
    {
        return new ECList(ec, this);
    }

    public EC head()
    {
        return head;
    }

    boolean isEmpty()
    {
        return this == null;
    }

    public ECList tail()
    {
        return tail;
    }

    private EC head;
    private ECList tail;
}
