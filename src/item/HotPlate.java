package src.item;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.*;
import src.object.Effect;
import src.object.Obj;
import src.object.ObjEX;
import src.system.ModLoader;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.*;


public class HotPlate extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static Image[] images = new Image[4];
	private static Rectangle boundary = new Rectangle();

	private Body bindBody = null;
	private Effect smoke = null;
	private Random rnd = new Random();
	
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for(int i = 0; i < 3; i++) {
			images[i] = ModLoader.loadItemImage(loader, "hotplate/hotplate" + i + ".png");
		}
		images[3] = ModLoader.loadItemImage(loader, "hotplate/hotplate_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	public Image getImage() {
		if(enabled) {
			if(bindBody != null) {
				FootBake f = null;
				f = bindBody.getFootBakeLevel();
				if(f == FootBake.CRITICAL) return images[2];
				else if(f == FootBake.MIDIUM) return images[1];
			}
			return images[0];
		}
		return images[3];
	}

	@Override
	public Image getImage(int idx) {
		return null;
	}

	@Override
	public int getImageLayerCount() {
		return 0;
	}

	@Override
	public Image getShadowImage() {
		return null;
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override	
	public boolean enableHitCheck() {
		if(bindBody != null) return false;
		return true;
	}

	@Override	
	public int objHitProcess( Obj o ) {
		
		bindBody = (Body)o;
		if(bindBody.getCriticalDamegeType() == CriticalDamegeType.CUT) return 0;

		bindBody.clearActions();
		bindBody.setX(x);
		bindBody.setY(y);
		bindBody.setCantMove(true);
		
		if(smoke == null) {
			smoke = SimYukkuri.mypane.terrarium.addEffect(Effect.EffectType.BAKE, bindBody.getX(), bindBody.getY() + 1,
															-2, 0, 0, 0, false, -1, -1, false, false, false);
		}
		
		return 1;
	}

	@Override
	public void upDate() {
		if ( getAge() % 2400 == 0 ){
			Cash.addCash(-getCost());
		}

		if(bindBody != null) {
			bindBody.setDropShadow(false);
			if(grabbed) {
				bindBody.setX(x);
				bindBody.setY(y);
				if(smoke != null) {
					smoke.setX(x);
					smoke.setY(y);
				}
			}
			else if(bindBody.getX() != x || bindBody.getY() != y || bindBody.getZ() != z || bindBody.isRemoved()) {
				bindBody.setForceFace(-1);
				bindBody.setCantMove(false);
				bindBody.setDropShadow(true);
				bindBody = null;
			} else {
				if(!bindBody.isDead()) {
					if(bindBody.isSleeping()) bindBody.wakeup();
					bindBody.addFootBakePeriod(50);
					bindBody.addDamage(20);
					bindBody.addStress(10);
					bindBody.setHappiness(Happiness.VERY_SAD);
					bindBody.setForceFace(ConstantValues.PAIN);
					if(rnd.nextInt(10) == 0) {
						bindBody.setMessage(MessagePool.getMessage(bindBody, MessagePool.Action.Burning), 40, true, true);
					}
				}
			}
		} else {
			if(smoke != null) {
				smoke.remove();
				smoke = null;
			}
		}
	}
	
	@Override
	public void removeListData(){
		if(bindBody != null) {
			bindBody.setForceFace(-1);
			bindBody.setCantMove(false);
			bindBody = null;
		}
		if(smoke != null) {
			smoke.remove();
			smoke = null;
		}
		objEXList.remove(this);
	}

	public HotPlate(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.HOTPLATE;
		
		interval = 5;
		value = 5000;
		cost = 100;
	}
}
