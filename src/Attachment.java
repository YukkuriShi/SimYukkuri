package src;

import java.awt.Image;

import src.yukkuriBody.Body;

/****************************************
 *  ゆっくりの体に付くアタッチメントのベースクラス
 * 
 * 
 */
public abstract class Attachment extends Obj {

	private static final long serialVersionUID = 1L;

	public enum AttachProperty {
		BABY_SIZE,			// 赤ゆ用画像サイズ 原画をこの値で割る
		CHILD_SIZE,			// 子ゆ用画像サイズ
		ADULT_SIZE,			// 成ゆ用画像サイズ
		OFS_X,				// アタッチメント取り付け座標
		OFS_Y,
		OFS_ORIGIN,			// 親オブジェクトの位置基準 0:顔、お飾り向けの元サイズ 1:妊娠などの膨らみも含むサイズ
		ANIME_INTERVAL,		// アニメ速度
		ANIME_LOOP,			// アニメループ回数
		ANIME_FRAMES,		// アニメ画像枚数
	}

	protected Body parent;
	protected boolean animate;		// アニメーション中
	protected int animeFrame;			// アニメーション表示フレーム
	protected int animeInterval;		// アニメーション間隔
	protected int animeLoop;			// アニメループ回数 0でアニメなし
	
	protected int[] attachProperty;	// AttachPropertyの値
	
	public abstract Image getImage(Body b);
	
	public int getOfsX() {
		return attachProperty[AttachProperty.OFS_X.ordinal()] / attachProperty[parent.getAgeState().ordinal()];
	}

	public int getOfsY() {
		return attachProperty[AttachProperty.OFS_Y.ordinal()] / attachProperty[parent.getAgeState().ordinal()];
	}

	public int getParentOrigin() {
		return attachProperty[AttachProperty.OFS_ORIGIN.ordinal()];
	}

	protected void setAttachProperty(int[] p) {
		attachProperty = p;
		x = attachProperty[AttachProperty.OFS_X.ordinal()];
		y = attachProperty[AttachProperty.OFS_Y.ordinal()];
		animeInterval = 0;
		animeLoop = attachProperty[AttachProperty.ANIME_LOOP.ordinal()];
		if(attachProperty[AttachProperty.ANIME_INTERVAL.ordinal()] == 0) animate = false;
		else animate = true;
	}

	public Attachment(Body body) {
		objType = Type.ATTACHMENT;
		parent = body;
	}

	protected Event update() {
		return Event.DONOTHING;
	}

	public Event clockTick()
	{
		Event ret = Event.DONOTHING;
		age += TICK;
		ret = update();
		
		if(animate) {
			animeInterval += TICK;
			if(animeInterval > attachProperty[AttachProperty.ANIME_INTERVAL.ordinal()]) {
				animeInterval -= attachProperty[AttachProperty.ANIME_INTERVAL.ordinal()];
				animeFrame++;
				if(animeFrame == attachProperty[AttachProperty.ANIME_FRAMES.ordinal()]) {
					animeFrame = 0;
					if(animeLoop > 0) {
						animeLoop--;
						if(animeLoop == 0) animate = false;
					}
				}
			}
		}
		return ret;
	}
}