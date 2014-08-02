package src.attachment;


import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.Attachment;
import src.Body;
import src.MessagePool;
import src.ModLoader;


/****************************************
 *  ライター炎
 * 
 */
public class Fire extends Attachment {

	private static final long serialVersionUID = 1L;

	private static Image[][] images;		// [年齢][左右反転][アニメパターン]
	private static int[] imgW;
	private static int[] imgH;
	private static int[] pivX;
	private static int[] pivY;
	private static final int[] property = {
		4,		// 赤ゆ用画像サイズ 原画をこの値で割る
		2,		// 子ゆ用画像サイズ
		1,		// 成ゆ用画像サイズ
		100,	// アタッチメント取り付け座標
		60,
		0,		// 親オブジェクトの位置基準 0:顔、お飾り向けの元サイズ 1:妊娠などの膨らみも含むサイズ
		1,		// アニメ速度
		0,		// アニメループ回数
		4		// アニメ画像枚数
	};
	
	private int burnPeriod;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		int baby = Body.AgeState.BABY.ordinal();
		int child = Body.AgeState.CHILD.ordinal();
		int adult = Body.AgeState.ADULT.ordinal();
		images = new Image[3][4];
		
		// 炎
		for(int i = 0; i < 4; i++) {
			images[adult][i] = ModLoader.loadItemImage(loader, "effect/fire_" + i + ".png");
		}

		int w = images[adult][0].getWidth(io);
		int h = images[adult][0].getHeight(io);
		for(int i = 0; i < images[adult].length; i++) {
			images[child][i] = images[adult][i].getScaledInstance(w / property[AttachProperty.CHILD_SIZE.ordinal()], h / property[AttachProperty.CHILD_SIZE.ordinal()], Image.SCALE_AREA_AVERAGING);
			images[baby][i] = images[adult][i].getScaledInstance(w / property[AttachProperty.BABY_SIZE.ordinal()], h / property[AttachProperty.BABY_SIZE.ordinal()], Image.SCALE_AREA_AVERAGING);
		}

		imgW = new int[3];
		imgH = new int[3];
		pivX = new int[3];
		pivY = new int[3];
		for (int i = 0; i < 3; i++) {
			if(images[i][0] == null) continue;
			imgW[i] = images[i][0].getWidth(io);
			imgH[i] = images[i][0].getHeight(io);
			pivX[i] = imgW[i] >> 1;
			pivY[i] = imgH[i] - 1;
		}
	}
	
	public Image getImage(Body b) {
		return images[parent.getAgeState().ordinal()][animeFrame];
	}

	protected Event update() {
		// 生きてたらセリフとダメージ加算
		if(!parent.isDead()) {
			if(!parent.isTalking()) {
				parent.setMessage(MessagePool.getMessage(parent, MessagePool.Action.Burning), 20, true, true);
			}
			parent.addDamage(TICK * 90);
			parent.addStress(50);
		}
		// 燃焼時間
		burnPeriod += TICK * 90;
		
		// お飾り消失
		if(burnPeriod > (parent.getDamageLimit() / 3) && parent.hasOkazari()) {
			parent.takeOkazari();
		} else if(burnPeriod > parent.getDamageLimit()) {
			parent.setBurned(true);
		}
		if(parent.isDead() && parent.isBurned()) {
			return Event.REMOVED;
		}

		return Event.DONOTHING;
	}

	public Fire(Body body) {
		super(body);
		setAttachProperty(property);
		setBoundary(pivX[parent.getAgeState().ordinal()],
					pivY[parent.getAgeState().ordinal()],
					imgW[parent.getAgeState().ordinal()],
					imgH[parent.getAgeState().ordinal()]);
		burnPeriod = 0;
		value = 0;
		cost = 0;
	}

}