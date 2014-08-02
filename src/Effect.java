package src;


import java.awt.Image;

/****************************************
 *  セーブデータに保存する必要の無い軽量エフェクト
 * 
 * 
 */
public abstract class Effect extends Obj {

	private static final long serialVersionUID = 1L;
	
	public enum EffectType {
		BAKE,
		HIT,
		MIX,
		STEAM,
	}

	protected int direction;		// 向き
	protected int interval;			// アニメーション間隔
	protected int frames;			// 総フレーム

	protected int lifeTime;			// 生存期間
	protected boolean animate;		// アニメーション中
	protected int animeFrame;			// アニメーション表示フレーム
	protected int animeInterval;		// アニメーション間隔カウント
	protected int animeLoop;			// アニメループ回数 0でアニメなし
	protected boolean animeEnd;		// アニメ終了と同時に消滅
	protected boolean enableGravity;	// 重力の影響を受ける

	public abstract Image getImage();

	public Effect(int sX, int sY, int sZ, int vX, int vY, int vZ, boolean invert,
						int life, int loop, boolean end, boolean grav, boolean front) {
		if(front) {
			Terrarium.frontLightEffectList.add(this);
		} else {
			Terrarium.sortLightEffectList.add(this);
		}
		objType = Type.LIGHT_EFFECT;
		x = sX;
		y = sY;
		z = sZ;
		vx = vX;
		vy = vY;
		vz = vZ;
		if(invert) direction = 1;
		else direction = 0;
		lifeTime = life;
		animeInterval = 0;
		animeLoop = loop;
		if(loop == 0) animate = false;
		else animate = true;
		animeEnd = end;
		enableGravity = grav;
	}
	
	public void setAnimeFrame(int f) {
		animeFrame = f;		
	}
	
	public Event clockTick()
	{
		age += TICK;
		if ( removed || (lifeTime != -1 && age > lifeTime)) {
			return Event.REMOVED;
		}
		x += vx;
		y += vy;
		if ( enableGravity ) {
			vz += 1;
		}
		z -= vz;
		
		if(animate) {
			animeInterval += TICK;
			if(animeInterval > interval) {
				animeInterval -= interval;
				animeFrame++;
				if(animeFrame == frames) {
					if(animeEnd) return Event.REMOVED;
					animeFrame = 0;
					if(animeLoop != -1) {
						animeLoop--;
						if(animeLoop <= 0) animate = false;
					}
				}
			}
		}
		
		return Event.DONOTHING;
	}
}