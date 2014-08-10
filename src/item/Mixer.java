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
import src.yukkuriBody.ConstantValues.AgeState;
import src.yukkuriBody.ConstantValues.CriticalDamegeType;
import src.yukkuriBody.ConstantValues.Happiness;

public class Mixer extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static Image[] images = new Image[4];
	private static Rectangle boundary = new Rectangle();

	private Body bindBody = null;
	private Effect mix = null;
	private int counter = 0;		// ゆっくりが乗ってからの経過時間
	private int amount = 0;			// 保有原料
	private int sweet = 0;			// 糖度
	private boolean sick = false;	// カビ混入
	private Random rnd = new Random();
	
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for(int i = 0; i < 3; i++) {
			images[i] = ModLoader.loadItemImage(loader, "mixer/mixer_" + i + ".png");
		}
		images[3] = ModLoader.loadItemImage(loader, "mixer/mixer_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	@Override
	public Image getImage() {
		if(enabled) {
			if(bindBody != null) {
				if(counter > 60) {
					if(bindBody.getAmount() < (bindBody.getDamageLimit() >> 1)) return images[2];
					return images[1];
				}
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

		bindBody.clearActions();
		bindBody.setX(x);
		bindBody.setY(y);
		bindBody.setCantMove(true);

		counter = 0;

		return 1;
	}

	@Override
	public void upDate() {
		if ( getAge() % 2400 == 0 ) {
			Cash.addCash(-getCost());
		}

		if(bindBody != null) {
			bindBody.setDropShadow(false);
			if(grabbed) {
				bindBody.setX(x);
				bindBody.setY(y);
				if(mix != null) {
					mix.setX(x);
					mix.setY(y);
				}
			} else if(bindBody.getX() != x || bindBody.getY() != y || bindBody.getZ() != z || bindBody.isRemoved()) {
				if(counter > 60) bindBody.setCriticalDamegeType(CriticalDamegeType.CUT);
				bindBody.setForceFace(-1);
				bindBody.setCantMove(false);
				bindBody.setDropShadow(true);
				bindBody = null;
				return;
			}
			counter++;
			// ミキサー駆動開始
			if(counter > 60) {
				if(mix == null) {
					mix = SimYukkuri.mypane.terrarium.addEffect(Effect.EffectType.MIX, bindBody.getX(), bindBody.getY() + 1,
																	-2, 0, 0, 0, false, -1, -1, false, false, false);
				}
				if(!bindBody.isDead()) {
					if(bindBody.isSleeping()) bindBody.wakeup();
					bindBody.addDamage(100);
					bindBody.addStress(20);
					bindBody.setHappiness(Happiness.VERY_SAD);
					bindBody.setForceFace(ConstantValues.PAIN);
					if(rnd.nextInt(10) == 0) {
						bindBody.setMessage(MessagePool.getMessage(bindBody, MessagePool.Action.Scream), true);
					}
				}
				// 材料採取
				amount += 100;
				sweet += bindBody.getStress();
				if(bindBody.isSick()) sick = true;
				if(bindBody.addAmount(-100)) {
					bindBody.remove();
					bindBody = null;
					if(mix != null) {
						mix.remove();
						mix = null;
					}
				}
				// 一定量で餌生成
				if(amount > 12000) {
					ObjEX oex = null;
					if(sick)
						oex = SimYukkuri.mypane.terrarium.addObjEX(ObjEX.ObjEXType.FOOD, getX(), getY(), Food.type.WASTE.ordinal());
					else {
						if(sweet > 200000) 
							oex = SimYukkuri.mypane.terrarium.addObjEX(ObjEX.ObjEXType.FOOD, getX(), getY(), Food.type.SWEETS1.ordinal());
						else
							oex = SimYukkuri.mypane.terrarium.addObjEX(ObjEX.ObjEXType.FOOD, getX(), getY(), Food.type.YUKKURIFOOD.ordinal());
					}
					oex.kick(0, 6, -4);
					amount -= 8400;
					sweet = 0;
					sick = false;
				}
			}
		} else {
			if(mix != null) {
				mix.remove();
				mix = null;
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
		if(mix != null) {
			mix.remove();
			mix = null;
		}
		objEXList.remove(this);
	}

	public Mixer(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.MIXER;
		
		interval = 5;
		value = 3000;
		cost = 50;
	}
}
