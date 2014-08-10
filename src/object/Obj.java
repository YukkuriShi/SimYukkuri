package src.object;

import java.awt.Point;
import java.awt.Rectangle;

import src.Terrarium;

/*********************************************************
 *  すべてのゲーム内オブジェクトの元となるクラス
 *  でいぶ、ドスなどの突然変異でエラーが出る可能性があるので
 *  private変数は使わず代わりにprotectedを使用してください
 */
public class Obj implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public enum Type {YUKKURI, SHIT, PLATFORM, FIX_OBJECT, OBJECT, LIGHT_EFFECT, ATTACHMENT, VOMIT}
	public enum Event {DONOTHING, BIRTHBABY, DOSHIT, DEAD, REMOVED, DOSUKKIRI, DOVOMIT}

	public static final int TICK = 1;

	// basic attributes
	protected int value = 0;
	protected int cost = 0;
	protected long age;			//age of this object
	public int x;		//location

	public int y;

	public int z;
	public int vx;	//velocity of the object

	public int vy;

	public int vz;
	protected Point screenPivot = new Point();		// 描画原点
	protected Rectangle screenRect = new Rectangle(); // 描画XYWH
	protected int imgW;
	protected int imgH;
	protected int pivX;
	protected int pivY;
	protected Type objType;		//YUKKURI, SHIT, FOOD, TOILET, TOY
	protected boolean removed = false;
	public boolean grabbed = false;
	protected boolean enableWall = true;
	protected Obj bindObj = null;

	public long getAge() {
		return age;
	}

	public void addAge(long val) {
		setAge(getAge() + val);
		if(getAge() < 0) setAge(0);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getVX() {
		return vx;
	}

	public int getVY() {
		return vy;
	}

	public int getVZ() {
		return vz;
	}

	public void setX (int X)
	{
		if (X < 0 && enableWall ) {
			x = 0;
		}
		else if (X > Terrarium.MAX_X && enableWall ) {
			x = Terrarium.MAX_X;
		}
		else {
			x = X;
		}
	}

	public void setY (int Y) {
		if (Y < 0 && enableWall ) {
			y = 0;
		}
		else if(Y > Terrarium.MAX_Y && enableWall ) {
			y = Terrarium.MAX_Y;
		}
		else {
			y = Y;
		}
	}

	public void setForceX (int X)
	{
		x = X;
	}

	public void setForceY (int Y) {
		y = Y;
	}

	public void setZ(int Z)
	{
		if (Z < 0 && enableWall ) {
			z = 0;
		}
		else if (Z > Terrarium.MAX_Z && enableWall ) {
			z = Terrarium.MAX_Z;
		}
		else {
			z = Z;
		}
	}

	// オブジェクト画像の原点とサイズをセット
	protected void setBoundary(int px, int py, int w, int h) {
		pivX = px;
		pivY = py;
		imgW = w;
		imgH = h;
	}

	protected void setBoundary(Rectangle r) {
		pivX = r.x;
		pivY = r.y;
		imgW = r.width;
		imgH = r.height;
	}

	public int getW() {
		return imgW;
	}

	public int getH() {
		return imgH;
	}

	public int getPivotX() {
		return pivX;
	}

	public int getPivotY() {
		return pivY;
	}
	
	public void getBoundaryShape(Rectangle r) {
		r.x = pivX;
		r.y = pivY;
		r.width = imgW;
		r.height = imgH;
	}

	// 画面上に描画されているオブジェクトの原点をセット
	public void setScreenPivot(int x, int y) {
		screenPivot.x = x;
		screenPivot.y = y;
	}

	public void setScreenPivot(Point rect) {
		screenPivot.x = rect.x;
		screenPivot.y = rect.y;
	}

	public Point getScreenPivot() {
		return screenPivot;
	}

	// 画面上に描画されているオブジェクトの左上座標とサイズをセット
	public void setScreenRect(int x, int y, int w, int h) {
		screenRect.x = x;
		screenRect.y = y;
		screenRect.width = w;
		screenRect.height = h;
	}

	public void setScreenRect(Rectangle rect) {
		screenRect.x = rect.x;
		screenRect.y = rect.y;
		screenRect.width = rect.width;
		screenRect.height = rect.height;
	}

	public Rectangle getScreenRect() {
		return screenRect;
	}

	public void grab() {
		grabbed = true;
	}

	public void release() {
		grabbed = false;
	}

	public boolean isGrabbed() {
		return grabbed;
	}

	public Type getObjType() {
		return objType;
	}

	public int getValue() {
		return value;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setEnableWall( boolean flag ) {
		enableWall = flag;
	}
	
	public void remove() {
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void kick(int vX, int vY, int vZ) {
		vx = vX;
		vy = vY;
		vz = vZ;
	}
	
	public void kick() {
	}

	public Obj getBindObj() {
		return bindObj;
	}
	public void setBindObj( Obj obj ) {
		bindObj = obj;
	}

	public Event clockTick()
	{
		setAge(getAge() + TICK);
		if (removed) {
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
				else if (Terrarium.onBarrier(x, y, 16, 16, Terrarium.MAP_ADULT)) {
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
				else if (Terrarium.onBarrier(x, y, 16, 16, Terrarium.MAP_ADULT)) {
					y -= vy;
					vy = 0;
				}
			}
			if (z != 0 || vz != 0) {
				vz += 1;
				z -= vz;
				if (z <= 0) {
					z = 0;
					vx = 0;
					vy = 0;
					vz = 0;					
				}
			}
		}
		return Event.DONOTHING;
	}

	public void setAge(long age) {
		this.age = age;
	}
}