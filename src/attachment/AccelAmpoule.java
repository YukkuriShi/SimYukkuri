package src.attachment;


import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.Attachment;
import src.ModLoader;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues.*;

/****************************************
 *  成長促進アンプル
 * 
 */
public class AccelAmpoule extends Attachment {

	private static final long serialVersionUID = 1L;

	private static Image[][] images;		// [年齢][左右反転]
	private static int[] imgW;
	private static int[] imgH;
	private static int[] pivX;
	private static int[] pivY;
	private static final int[] property = {
		1,		// 赤ゆ用画像サイズ 原画をこの値で割る
		1,		// 子ゆ用画像サイズ
		1,		// 成ゆ用画像サイズ
		0,	// アタッチメント取り付け座標
		0,
		1,		// 親オブジェクトの位置基準 0:顔、お飾り向けの元サイズ 1:妊娠などの膨らみも含むサイズ
		0,		// アニメ速度
		0,		// アニメループ回数
		1		// アニメ画像枚数
	};
	
	int tick = 0;
	
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		int baby = AgeState.BABY.ordinal();
		int child = AgeState.CHILD.ordinal();
		int adult = AgeState.ADULT.ordinal();
		images = new Image[3][2];
		
		images[adult][0] = ModLoader.loadItemImage(loader, "ampoule/body_accel.png");

		int w = images[adult][0].getWidth(io);
		int h = images[adult][0].getHeight(io);
		images[child][0] = images[adult][0].getScaledInstance(w / property[AttachProperty.CHILD_SIZE.ordinal()], h / property[AttachProperty.CHILD_SIZE.ordinal()], Image.SCALE_AREA_AVERAGING);
		images[baby][0] = images[adult][0].getScaledInstance(w / property[AttachProperty.BABY_SIZE.ordinal()], h / property[AttachProperty.BABY_SIZE.ordinal()], Image.SCALE_AREA_AVERAGING);
			
		images[adult][1] = ModLoader.flipImage(images[adult][0]);
		images[child][1] = ModLoader.flipImage(images[child][0]);
		images[baby][1] = ModLoader.flipImage(images[baby][0]);

		imgW = new int[3];
		imgH = new int[3];
		pivX = new int[3];
		pivY = new int[3];
		for (int i = 0; i < 3; i++) {
			imgW[i] = images[i][0].getWidth(io);
			imgH[i] = images[i][0].getHeight(io);
			pivX[i] = imgW[i] >> 1;
			pivY[i] = imgH[i] - 1;
		}
	}

	public int getOfsX() {
		return (parent.getW() >> 1);
	}

	public int getOfsY() {
		return (int)(parent.getH() * 0.35f);
	}
	
	protected Event update() {
		tick++;
		if(!parent.isAdult() && tick == 100) {
			tick = 0;
			parent.addAge(TICK * 10000);
		}
		return Event.DONOTHING;
	}

	public Image getImage(Body b) {
		if(b.getDirection() == Direction.RIGHT) {
			return images[parent.getAgeState().ordinal()][1];
		}
		return images[parent.getAgeState().ordinal()][0];
	}

	public AccelAmpoule(Body body) {
		super(body);
		setAttachProperty(property);
		setBoundary(pivX[parent.getAgeState().ordinal()],
					pivY[parent.getAgeState().ordinal()],
					imgW[parent.getAgeState().ordinal()],
					imgH[parent.getAgeState().ordinal()]);
		value = 1000;
		cost = 0;
	}
}
