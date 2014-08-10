package src.item;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.*;
import src.object.Obj;
import src.object.ObjEX;
import src.system.ModLoader;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues.CriticalDamegeType;

public class StickyPlate extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static Image[] images = new Image[2];
	private static Rectangle boundary = new Rectangle();

	private Body bindBody = null;
	
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
			images[0] = ModLoader.loadItemImage(loader, "stickyplate/stickyplate.png");
			images[1] = ModLoader.loadItemImage(loader, "stickyplate/stickyplate_off.png");
			boundary.width = images[0].getWidth(io);
			boundary.height = images[0].getHeight(io);
			boundary.x = boundary.width >> 1;
			boundary.y = boundary.height >> 1;
	}

	@Override
	public Image getImage() {
		if(enabled) return images[0];
		return images[1];
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
		if(((Body)o).isDead()) return 0;
		if(((Body)o).getCriticalDamegeType() == CriticalDamegeType.CUT) return 0;

		bindBody = (Body)o;
		bindBody.clearActions();
		bindBody.setX(x);
		bindBody.setY(y);
		bindBody.setCantMove(true);
		bindBody.setPullAndPush(true);
		return 0;
	}

	@Override
	public void upDate() {
		if(bindBody != null) {
			if(grabbed) {
				bindBody.setX(x);
				bindBody.setY(y);
			}
			else if(bindBody.getX() != x || bindBody.getY() != y || bindBody.isRemoved() || bindBody.isDead()) {
				bindBody.setCantMove(false);
				bindBody.setPullAndPush(false);
				bindBody = null;
			}
		}
	}
	
	@Override
	public void removeListData(){
		if(bindBody != null) {
			bindBody.setCantMove(false);
			bindBody.setPullAndPush(false);
			bindBody = null;
		}
		objEXList.remove(this);
	}

	public StickyPlate(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.STICKYPLATE;
		
		interval = 5;
		value = 2000;
		cost = 50;
	}
}
