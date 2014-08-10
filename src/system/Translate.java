package src.system;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import src.Terrarium;


/***************************************************
 蠎ｧ讓吝､画鋤繧ｯ繝ｩ繧ｹ 

 */
public class Translate {

	private static final float wallX = 0.75f;
//	private static final float wallY = 0.8f;
	private static final float wallY = 0.7f;
	
	public static final float flyLimit = 0.175f;

	public static int mapW;
	public static int mapH;
	public static int mapZ;
	public static int fieldW;
	public static int fieldH;
	public static int bufferW;
	public static int bufferH;
	private static Rectangle displayArea = new Rectangle();
	private static float[] zoomTable;
	private static int zoomRate = 0;
	
	public static int canvasW;
	public static int canvasH;

	
	public static float[] rateX;
	public static int[] ofsX;
	//
	public static int[] mapToFieldY;
	public static int fieldMinY;
	//
	public static float rateZ;
	
	//
	public static int[] fieldToMapY;
	
//
	public static Polygon fieldPoly;

	public static final void setMapSize(int mW, int mH, int mZ) {
		mapW = mW + 1;
		mapH = mH + 1;
		mapZ = mZ + 1;
	}

	public static final void setCanvasSize(int dW, int dH, int fieldSize, int bufSize, float[] rate) {
		canvasW = dW;
		canvasH = dH;
		
		fieldW = canvasW * fieldSize / 100;
		fieldH = canvasH * fieldSize / 100;
		
		bufferW = canvasW * bufSize / 100;
		bufferH = canvasH * bufSize / 100;
		
		zoomTable = rate;
		zoomRate = 0;
		
		setBufferZoom();
		displayArea.x = 0;
		displayArea.y = 0;
	}

	public static final int getFieldW() {
		return fieldW;
	}
	public static final int getFieldH() {
		return fieldH;
	}

	public static final int getBufferW() {
		return bufferW;
	}
	public static final int getBufferH() {
		return bufferH;
	}

	public static final boolean addZoomRate(int val) {
		boolean ret = true;

		zoomRate += val;
		if(zoomRate < 0) {
			zoomRate = 0;
			ret = false;
		} else if(zoomRate >= zoomTable.length) {
			zoomRate = zoomTable.length - 1;
			ret = false;
		}
		return ret;
	}

	public static final void setZoomRate(int val) {
		zoomRate = val;
		if(zoomRate < 0) zoomRate = 0;
		else if(zoomRate >= zoomTable.length) zoomRate = zoomTable.length - 1;
	}

	public static final float getCurrentZoomRate() {
		return zoomTable[zoomRate];
	}

	public static final void setBufferZoom() {
		displayArea.width = (int)((float)fieldW * zoomTable[zoomRate]);
		displayArea.height = (int)((float)fieldH * zoomTable[zoomRate]);
	}

	public static final void setBufferPos(int sx, int sy) {
		displayArea.x = sx;
		displayArea.y = sy;
		checkDisplayLimit();
	}

	public static final void setBufferCenterPos(int sx, int sy) {
		displayArea.x = sx - (displayArea.width >> 1);
		displayArea.y = sy - (displayArea.height >> 1);
		checkDisplayLimit();
	}

	public static final void addBufferPos(int sx, int sy) {
		displayArea.x += sx;
		displayArea.y += sy;
		checkDisplayLimit();
	}

	private static final void checkDisplayLimit() {
		if(displayArea.x < 0) displayArea.x = 0;
		else if((displayArea.x + displayArea.width) >= fieldW) displayArea.x = fieldW - displayArea.width;

		if(displayArea.y < 0) displayArea.y = 0;
		else if((displayArea.y + displayArea.height) >= fieldH) displayArea.y = fieldH - displayArea.height;
	}

	public static final Rectangle getDisplayArea() {
		return displayArea;
	}

	// 繧ｭ繝｣繝ｳ繝舌せ -> 繝輔ぅ繝ｼ繝ｫ繝牙､画鋤
	public static final void transCanvasToField(int x, int y, int[] out) {
		out[0] = displayArea.x + (int)(x * fieldW / canvasW * zoomTable[zoomRate]);
		out[1] = displayArea.y + (int)(y * fieldH / canvasH * zoomTable[zoomRate]);
	}

	// 繝輔ぅ繝ｼ繝ｫ繝�-> 繧ｭ繝｣繝ｳ繝舌せ螟画鋤
	public static final void transFieldToCanvas(int x, int y, int[] out) {
		out[0] = (int)((x - displayArea.x) * canvasW / fieldW / zoomTable[zoomRate]);
		out[1] = (int)((y - displayArea.y) * canvasH / fieldH / zoomTable[zoomRate]);
	}

	public static final int getMapX() {
		return mapW;
	}
	public static final int getMapY() {
		return mapH;
	}
	public static final int getMapZ() {
		return mapZ;
	}
	public static final int getCanvasW() {
		return canvasW;
	}
	public static final int getCanvasH() {
		return canvasH;
	}

//
	public static final void createTransTable() {
		//
		float groundY = (float)fieldH * wallY;
		float ofsY = (float)fieldH - groundY;
//
		float deltaY = groundY / (float)mapH; 
		mapToFieldY = new int[mapH];
		for(int y = 0; y < mapH; y++) {
			mapToFieldY[y] = (int)(ofsY + (y * deltaY));
		}
		
		//
		rateX = new float[mapH];
		ofsX = new int[mapH];
		
		float deltaX = (1.0f - wallX) / (float)mapH;
		for(int y = 0; y < mapH; y++) {
			rateX[y] = (float)fieldW * (wallX + deltaX * (float)y);
			ofsX[y] = (int)(fieldW - rateX[y]) >> 1;
			rateX[y] /= mapW;
		}

		//
		fieldToMapY = new int[fieldH];
		// 
		for(int y = 0; y < fieldH; y++) {
			fieldToMapY[y] = -1;
		}
		// 
		fieldMinY = (int)((float)fieldH * (1.0f - wallY));
		// 
		groundY = (float)(fieldH - fieldMinY);
		deltaY = mapH / groundY;
		for(int y = 0; y < (fieldH - fieldMinY); y++) {
			fieldToMapY[y + fieldMinY] = (int)(y * deltaY);
		}
		
		// 
		rateZ = (float)fieldH / (float)mapZ;
		
		// 
		int[] polx = new int[4];
		int[] poly = new int[4];
		polx[0] = ofsX[0];				poly[0] = fieldMinY;
		polx[1] = fieldW - ofsX[0];	poly[1] = fieldMinY;
		polx[2] = fieldW - 1;			poly[2] = fieldH - 1;
		polx[3] = 0;					poly[3] = fieldH - 1;
		fieldPoly = new Polygon(polx, poly, 4);
	}

	// 
	public static final void translate(int x, int y, Point pos) {
//		Point ret = new Point();
		if(y < 0) y = 0;
		if(y >= mapH) y = mapH - 1;
		pos.x = ofsX[y] + (int)(rateX[y] * x);
		pos.y = mapToFieldY[y];
//System.out.println("---------------------");
//System.out.println(x+","+y+" -> "+ret);
		return;
	}
	
	public static final int translateZ(int z) {
		return (int)((float)z * rateZ);
	}

	// 
	public static final Point invert(int x, int y) {
		if(y < 0) return null;
		if(y >= fieldH) return null;
		
		int py = fieldToMapY[y];
		if(py < 0) return null;
		int px = (int)((x - ofsX[py]) / rateX[py]);
		if(px < 0) return null;
		if(px >= mapW) return null;
		Point ret = new Point();
		ret.x = px;
		ret.y = py;
		return ret;
	}

	// 
	public static final Point invertLimit(int x, int y) {
		if(y < 0) y = 0;
		if(y >= fieldH) y = fieldH - 1;
		
		int py = fieldToMapY[y];
		if(py < 0) py = 0;
		int px = (int)((x - ofsX[py]) / rateX[py]);
		if(px < 0) px = 0;
		if(px >= mapW) px = mapW - 1;
		Point ret = new Point();
		ret.x = px;
		ret.y = py;
		return ret;
	}

	// 
	public static final Point invertObject(int x, int y, int pivX, int margin) {
		int minY = y - margin;
		int maxY = y + margin;
		if(minY < fieldMinY) {
			minY = fieldMinY;
			y = minY + margin;
		} else if(maxY >= fieldH) {
			maxY = fieldH - 1;
			y = maxY - margin;
		}
		int py = fieldToMapY[y];

		int minX = x - pivX;
		int maxX = x + pivX;
		if(minX < ofsX[py]) {
			minX = ofsX[py];
			x = minX + pivX;
		} else if(maxX >= (fieldW - ofsX[py])) {
			maxX = fieldW - ofsX[py] - 1;
			x = maxX - pivX;
		}
		int px = (int)((x - ofsX[py]) / rateX[py]);
		Point ret = new Point();
		ret.x = px;
		ret.y = py;
		return ret;
	}

	//
	public static final void invertGround(int x, int y, int pivX, int pivY, Point pos) {
		int minY = y - pivY;
		int maxY = y + pivY;
		if(minY < fieldMinY) {
			minY = fieldMinY;
			y = minY + pivY;
		} else if(maxY >= fieldH) {
			maxY = fieldH - 1;
			y = maxY - pivY;
		}
		int py = fieldToMapY[y];

		int minX = x - pivX;
		int maxX = x + pivX;
		if(minX < ofsX[py]) {
			minX = ofsX[py];
			x = minX + pivX;
		} else if(maxX >= (fieldW - ofsX[py])) {
			maxX = fieldW - ofsX[py] - 1;
			x = maxX - pivX;
		}
		int px = (int)((x - ofsX[py]) / rateX[py]);
		pos.x = px;
		pos.y = py;
	}

	// 
	public static final void invertFlying(int x, int y, int z, int pivX, Point pos) {
		int py = fieldToMapY[y];
		if(py < 0) py = 0;

		int minX = x - pivX;
		int maxX = x + pivX;
		if(minX < ofsX[py]) {
			minX = ofsX[py];
			x = minX + pivX;
		} else if(maxX >= (fieldW - ofsX[py])) {
			maxX = fieldW - ofsX[py] - 1;
			x = maxX - pivX;
		}
		int px = (int)((x - ofsX[py]) / rateX[py]);
		if(z < 0) z = 0;
		pos.x = px;
		pos.y = z * Terrarium.MAX_Z / fieldH;
	}

	// 
	public static final void invertDelta(int x, int y, Point pos) {
		if(y < 0) y = 0;
		if(y >= fieldH) y = fieldH - 1;

		int py = fieldToMapY[y];
		if(py < 0) py = 0;
		int px = (int)(x / rateX[py]);
		if(px < 0) px = 0;
		if(px >= mapW) px = mapW - 1;
		pos.x = px;
		pos.y = py;
	}

	// 
	public static final int invertX(int x, int mapY) {
		if(mapY < 0) mapY = 0;
		if(mapY >= mapH) mapY = mapH - 1;
		return (int)((float)x / rateX[mapY]);
	}

	public static final int invertY(int y) {
		return (int)((float)y * (float)mapH / (float)fieldH);
	}

	public static final int invertZ(int z) {
		return (int)((float)z * (float)Terrarium.MAX_Z / (float)fieldH);
	}

	public static final int transSize(int size) {
		return size;
//		return (int)(size * 100 / Terrarium.terrariumSizeParcent);
	}


	
	//
	public static final int distance(int x1, int y1, int x2, int y2) {
		return ((x2 - x1)*(x2 - x1)+(y2 - y1)*(y2 - y1));
	}
	
	// 
	public static final int getFlyHeightLimit() {
		return (int)(getMapZ() * Translate.flyLimit);
	}

	//IMPORTED FOR COMPATABILITY
	static final private double m = 1.0 / 8.0;
	static final private double n = 7.0 / 8.0;

	static public int transX(int x, int y, int X, int Y) {
		return (int)(((n-m)*Y*x - m*X*y + m*X*Y)/((n-m-1)*y + Y));
	}

	static public int transY(int x, int y, int X, int Y) {
		return (int)(((n-m)*Y*y)/((n-m-1)*y + Y));
	}

	static public int invX(int x, int y, int X, int Y) {
		return (int)((Y*x + m*X*y - m*X*Y)/((n-m)*Y - (n-m-1)*y));
	}

	static public int invY(int x, int y, int X, int Y) {
		return (int)(Y*y/((n-m)*Y - (n-m-1)*y));
	}
	
	static public int transSizeOld(int size) {
		return (int)(size*100/Terrarium.terrariumSizeParcent);
	}
	
}
