package src;


import java.awt.Color;


/* 
 *    Copyright 2013 Mimisuke
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

public class Barrier implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private int sx, sy, ex, ey;
	private Color color;
	private int attribute;
	
	public int getSX() {
		return sx;
	}
	
	public int getSY() {
		return sy;
	}
	
	public int getEX() {
		return ex;
	}
	
	public int getEY() {
		return ey;
	}

	public Color getColor() {
		return color;
	}

	public int getAttribute() {
		return attribute;
	}

	public Barrier(int x1, int y1, int x2, int y2, int type) {
		sx = x1;
		sy = y1;
		ex = x2;
		ey = y2;
		attribute = type;
		switch(type) {
			case Terrarium.BARRIER_GAP_MINI:
				color = Color.YELLOW;
				break;
			case Terrarium.BARRIER_GAP_BIG:
				color = Color.ORANGE;
				break;
			case Terrarium.BARRIER_NET_MINI:
				color = Color.PINK;
				break;
			case Terrarium.BARRIER_NET_BIG:
				color = Color.MAGENTA;
				break;
			case Terrarium.BARRIER_WALL:
				color = Color.GRAY;
				break;
			case Terrarium.BARRIER_WATER:
				color = Color.CYAN;
				break;
		}
	}
}