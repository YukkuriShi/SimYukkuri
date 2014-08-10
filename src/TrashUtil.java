package src;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Random;

import src.event.GetTrashOkazariEvent;
import src.item.Trash;
import src.object.Obj;
import src.object.ObjEX;
import src.system.ModLoader;
import src.system.Translate;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.AgeState;
import src.yukkuriLogic.EventLogic;

/***************************************************
  ガラクタ処理クラス 

 */
public class TrashUtil {
	
	private static Random rnd = new Random();

	// おかざりの種類とスクリーン座標オフセット
	public enum OkazariType {
		DEFAULT("", 0, 0, 0, 0, 0, 0),
		BABY1("okazari_baby_01", 8, -14, 14, -36, 28, -80),
		BABY2("okazari_baby_02", 4, -14, 8, -40, 20, -80),
		CHILD1("okazari_child_01", 0, 0, 8, -34, 24, -80),
		CHILD2("okazari_child_02", 0, 0, 6, -32, 22, -80),
		ADULT1("okazari_adult_01", 0, 0, 0, 0, 18, -56),
		ADULT2("okazari_adult_02", 0, 0, 0, 0, 8, -46),
		ADULT3("okazari_adult_03", 0, 0, 0, 0, 8, -56),
		;
		public String fileName;
		public int[] ofsX = new int[3];
		public int[] ofsY = new int[3];
		OkazariType(String name, int bx, int by, int cx, int cy, int ax, int ay) {
			this.fileName = name;
			this.ofsX[0] = bx;
			this.ofsY[0] = by;
			this.ofsX[1] = cx;
			this.ofsY[1] = cy;
			this.ofsX[2] = ax;
			this.ofsY[2] = ay;
		}
	}

	// 各世代のお飾りの開始位置と数
	private static final int[] OKAZARI_START = {OkazariType.BABY1.ordinal(),
												OkazariType.CHILD1.ordinal(),
												OkazariType.ADULT1.ordinal()};
	private static final int[] OKAZARI_NUM = {2, 2, 3};

	private static Image[][] images = new Image[OkazariType.values().length][2];
	private static Rectangle[] boundary = new Rectangle[OkazariType.values().length];

	// ゴミおかざりの画像読み込み
	public static final void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		OkazariType[] o = OkazariType.values();
		for(int i = 1; i < o.length; i++) {
			images[i][0] = ModLoader.loadItemImage(loader, "trash/" + o[i].fileName + ".png");
			images[i][1] = ModLoader.flipImage(images[i][0]);
			
			boundary[i] = new Rectangle();
			boundary[i].width = images[i][0].getWidth(io);
			boundary[i].height = images[i][0].getHeight(io);
			boundary[i].x = boundary[i].width >> 1;
			boundary[i].y = boundary[i].height - 1;
		}
	}

	// ゴミあさりチェック
	public static final boolean checkTrashOkazari(Body b) {
		
		if(b.hasOkazari()) return false;
		if(b.isVerySad()) return false;

		Obj found = null;
		int minDistance = b.getEyesight();
		int wallMode = b.getAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if(b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		for (ObjEX t: Trash.objEXList) {
			int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY() - t.getH()/6);
			if (minDistance > distance) {
				if (Terrarium.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY() - t.getH()/6, Terrarium.MAP_BODY[wallMode])) {
					continue;
				}
				found = t;
				minDistance = distance;
			}
		}
		if (found != null) {
			EventLogic.addBodyEvent(b, new GetTrashOkazariEvent(b, null, found, 1), null, null);
			return true;
		}
		return false;
	}
	
	public static final OkazariType getRandomOkazari(AgeState ageState) {
		int num = OKAZARI_START[ageState.ordinal()] + rnd.nextInt(OKAZARI_NUM[ageState.ordinal()]);
		return OkazariType.values()[num];
	}
	
	public static final Image getOkazariImage(OkazariType type, int direction) {
		return images[type.ordinal()][direction];
	}
	
	public static final void getOkazariPos(Rectangle base, Body body, int direction, int[] rect) {
		int order = body.getOkazariType().ordinal();
		// W, H
		rect[2] = Translate.transSize(boundary[order].width);
		rect[3] = Translate.transSize(boundary[order].height);
		// X, Y
		rect[0] = Translate.transSize(body.getTrashOkazariOfsX());
		rect[1] = Translate.transSize(body.getTrashOkazariOfsY());

		if(direction == 0) {
			rect[0] = rect[0] + base.x + (base.width >> 1) - (rect[2] >> 1);
		} else {
			rect[0] = base.x + (base.width >> 1) - rect[0] - (rect[2] >> 1);
		}
		rect[1] = rect[1] + base.y + base.height - rect[3];
		
	}
}
