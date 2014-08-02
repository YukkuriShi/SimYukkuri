package src;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;


/***************************************************
 座標変換クラス 

 */
public class Translate {

	// フィールド画像に占める床部分の比率 現在のback.jpgからおよその値を持ってきている
	private static final float wallX = 0.75f;
//	private static final float wallY = 0.8f;
	private static final float wallY = 0.7f;
	
	// 飛行種の最大高度、適当に画面外に出ない値で
	public static final float flyLimit = 0.175f;

	// マップサイズ 内部計算で使用するオブジェクトの位置座標
	public static int mapW;
	public static int mapH;
	public static int mapZ;
	// フィールドサイズ 実際に描画されるフィールドのピクセル値
	public static int fieldW;
	public static int fieldH;
	// バックバッファサイズ
	public static int bufferW;
	public static int bufferH;
	// バックバッファ描画位置、サイズ
	private static Rectangle displayArea = new Rectangle();
	private static float[] zoomTable;
	private static int zoomRate = 0;
	
	// キャンバスサイズ 画面に描画されるウィンドウ枠の大きさ
	public static int canvasW;
	public static int canvasH;

	// 四角のマップを台形に歪めて配置するため
	// フィールド各Y座標でのXのスケールレートをテーブル化
	public static float[] rateX;
	public static int[] ofsX;
	// Y座標は直線なので単純なテーブル引きで済む
	public static int[] mapToFieldY;
	public static int fieldMinY;
	// Z座標はテーブルを使わずレート計算
	public static float rateZ;
	
	// マウスクリックなどフィールドのクリック位置からマップ座標へ変換するテーブル
	// Xはマップ->フィールドから逆引きできるのでYのみ作成
	public static int[] fieldToMapY;
	
	// オブジェクトの内包を簡易判定するためのシェイプ
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

	// キャンバス -> フィールド変換
	public static final void transCanvasToField(int x, int y, int[] out) {
		out[0] = displayArea.x + (int)(x * fieldW / canvasW * zoomTable[zoomRate]);
		out[1] = displayArea.y + (int)(y * fieldH / canvasH * zoomTable[zoomRate]);
	}

	// フィールド -> キャンバス変換
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

	// セットされたマップとフィールドサイズから変換テーブルを作成
	public static final void createTransTable() {
		// マップ->フィールド変換テーブル
		// 壁の部分を引いたYライン数を計算
		float groundY = (float)fieldH * wallY;
		float ofsY = (float)fieldH - groundY;
		// マップの各Y値に対応するフィールドのYを計算
		float deltaY = groundY / (float)mapH; 
		mapToFieldY = new int[mapH];
		for(int y = 0; y < mapH; y++) {
			mapToFieldY[y] = (int)(ofsY + (y * deltaY));
		}
		
		// XはYが小さいほど幅が狭くなるので比率と壁のオフセットを計算
		rateX = new float[mapH];
		ofsX = new int[mapH];
		
		float deltaX = (1.0f - wallX) / (float)mapH;
		for(int y = 0; y < mapH; y++) {
			rateX[y] = (float)fieldW * (wallX + deltaX * (float)y);
			ofsX[y] = (int)(fieldW - rateX[y]) >> 1;
			rateX[y] /= mapW;
		}

		// フィールド->マップ変換テーブル
		fieldToMapY = new int[fieldH];
		// 壁部分は-1で埋める
		for(int y = 0; y < fieldH; y++) {
			fieldToMapY[y] = -1;
		}
		// 壁の計算
		fieldMinY = (int)((float)fieldH * (1.0f - wallY));
		// 床の計算
		groundY = (float)(fieldH - fieldMinY);
		deltaY = mapH / groundY;
		for(int y = 0; y < (fieldH - fieldMinY); y++) {
			fieldToMapY[y + fieldMinY] = (int)(y * deltaY);
		}
		
		// 高さ
		rateZ = (float)fieldH / (float)mapZ;
		
		// 内包判定用ポリゴン
		int[] polx = new int[4];
		int[] poly = new int[4];
		polx[0] = ofsX[0];				poly[0] = fieldMinY;
		polx[1] = fieldW - ofsX[0];	poly[1] = fieldMinY;
		polx[2] = fieldW - 1;			poly[2] = fieldH - 1;
		polx[3] = 0;					poly[3] = fieldH - 1;
		fieldPoly = new Polygon(polx, poly, 4);
	}

	// マップ->フィールド変換
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

	// フィールド->マップ変換 範囲外の座標はnullを返す
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

	// フィールド->マップ変換 範囲外の座標は限界位置として扱う
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

	// オブジェクト用 フィールド->マップ変換 立っているとみなしてYは座標制限に猶予がある
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

	// 床配置物用 フィールド->マップ変換 設置物が完全にフィールド内に納まるように座標を制限する
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

	// 空中物用 フィールド->マップ変換 ここで返す値はx,z
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

	// 移動量フィールド->マップ変換
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

	// マップY座標から画像サイズの距離を計算
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

	// 2点間の距離計算 ルートを省いているので実際の距離にはならないので注意
	public static final int distance(int x1, int y1, int x2, int y2) {
		return ((x2 - x1)*(x2 - x1)+(y2 - y1)*(y2 - y1));
	}
	
	// 飛行種の最大高度マップZを返す
	public static final int getFlyHeightLimit() {
		return (int)(getMapZ() * Translate.flyLimit);
	}

}
