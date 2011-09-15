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
//            Piclet

public class PicletList
{

    public PicletList(Piclet piclet, PicletList picletlist)
    {
        head = piclet;
        tail = picletlist;
    }

    public Piclet head()
    {
        return head;
    }

    public PicletList tail()
    {
        return tail;
    }

    private Piclet head;
    private PicletList tail;
}
