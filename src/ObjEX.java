package src;


import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class ObjEX extends Obj implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	public enum ObjEXType	{FOOD,
							TOILET,
							BED,
							TOY,
							BELTCONVEYOR,
							BREEDINGPOOL,
							GARBAGECHUTE,
							MACHINEPRESS,
							FOODMAKER,
							ORANGEPOOL,
							PRODUCTCHUTE,
							STALK,
							DIFFUSER,
							YUNBA,
							STICKYPLATE,
							HOTPLATE,
							MIXER,
							AUTOFEEDER,
							SUI,
							TRASH,}
	protected ObjEXType objEXType;
	protected int option;

	public static final int YUKKURI = 1;
	public static final int SHIT	= 2;
	public static final int FOOD	= 4;
	public static final int TOILET	= 8;
	public static final int TOY		= 16;
	public static final int	PLATFORM= 32;
	public static final int	FIX_OBJECT= 64;
	public static final int	OBJECT= 128;
	public static final int	VOMIT= 256;
	public static final int hitCheckObjType 		= 0;

	protected Obj linkParent = null;		// 親オブジェクト
	protected int looks = 0;
	protected int interval = 1;				// 負荷分散のためのインターバル値
	protected boolean enabled = true;		// 汎用スイッチ
	protected int colW = 0;		// 原点からの当たり判定範囲
	protected int colH = 0;		// 画像=判定ならpivXYでOK プレス機など設置地面付近なら要調整
	protected Point tmpPos = new Point();

	abstract public Image getImage();

	abstract public Image getImage(int idx);

	abstract public int getImageLayerCount();

	abstract public Image getShadowImage();

	public ObjEXType getObjEXType() {
		return objEXType;
	}
	
	public int getOption() {
		return option;
	}
	
	public void setOption( int setOption ) {
		option = setOption;
	}

	public int getHitCheckObjType() {
		return hitCheckObjType;
	}
	
	public Obj getParent() {
		return linkParent;
	}

	public int getLooks() {
		return looks;
	}
	
	public boolean checkInterval(int cnt) {
		return ((cnt % interval) == 0);
	}
	
	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enb) {
		enabled = enb;
	}

	public void invertEnabled() {
		enabled = !enabled;
	}
	
	protected void setCollisionSize(int halfW, int halfH) {
		colW = halfW;
		colH = halfH;
	}
	
	public void getCollisionRect(Rectangle r) {
		int x = getScreenPivot().x;
		int y = getScreenPivot().y;
		r.x = x - colW;
		r.y = y - colH;
		r.width = colW << 1;
		r.height = colH << 1;
	}

	public boolean enableHitCheck() {
		return true;
	}

	public int checkHitObj(Rectangle colRect, Obj o) {
		
		int objZ = o.getZ();
		if ( objZ == 0 ) {	//空中の物は移動させない
			// 対象の座標をフィールド座標に変換
			Translate.translate(o.getX(), o.getY(), tmpPos);
			// 点が描画矩形に入ったかの判定
			if(colRect.contains(tmpPos)) {
				objHitProcess( o );
				return 1;
			}
		}
		return 0;
	}

	public int objHitProcess( Obj o ) {
		return 0;
	}
	
	public void upDate() {
	}

	abstract public void removeListData();

	@Override
	public Event clockTick()
	{
		age += TICK;
		if (removed) {
			removeListData();
			return Event.REMOVED;
		}
		if (!grabbed) {
			if (vx != 0) {
				x += vx;
				if (x < 0) {
					x = 0;
					vx *= -1;
				}
				else if (x > Terrarium.MAX_X) {
					x = Terrarium.MAX_X;
					vx *= -1;
				}
				else if (Terrarium.onBarrier(x, y, getW() >> 2, getH() >> 2, Terrarium.MAP_ADULT)) {
					x -= vx;
					vx = 0;
				}
			}
			if (vy != 0) {
				y += vy;
				if (y < 0) {
					y = 0;
					vy *= -1;
				}
				else if (y > Terrarium.MAX_Y) {
					y = Terrarium.MAX_Y;
					vy *= -1;
				}
				else if (Terrarium.onBarrier(x, y, getW() >> 2, getH() >> 2, Terrarium.MAP_ADULT)) {
					y -= vy;
					vy = 0;
				}
			}
			if (z != 0 || vz != 0) {
				vz += 1;
				z -= vz;
				if (z <= 0 || objType == Type.PLATFORM ) {
					z = 0;
					vx = 0;
					vy = 0;
					vz = 0;
				}
			}
		}
		upDate();
		return Event.DONOTHING;
	}

	public ObjEX(int initX, int initY, int initOption) {
		objType = Type.PLATFORM;
		x = initX;
		y = initY;
		z = 0;
		option = initOption;
		enabled = true;
	}
}