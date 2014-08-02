package src.item;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.*;


public class Toilet extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = ObjEX.SHIT;
	private static Image[] images = new Image[2];
	private static Rectangle boundary = new Rectangle();
	
	private boolean autoClean;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		images[0] = ModLoader.loadItemImage(loader, "toilet.png");
		images[1] = ModLoader.loadItemImage(loader, "toilet2.png");

		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}
	
	@Override
	public Image getImage() {
		if(autoClean) return images[1]; 
		return images[0];
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
		if(autoClean) return hitCheckObjType;
		return 0;
	}

	@Override
	public void removeListData(){
		objEXList.remove(this);
	}

	public boolean getAutoClean() {
		return autoClean;
	}

	public Toilet(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.TOILET;
		if(initOption == 0) {
			autoClean = false;
			value = 1000;
			cost = 0;
		} else {
			autoClean = true;
			value = 5000;
			cost = 25;
		}
		
		interval = 30;
	}

	public int objHitProcess( Obj o ) {
		o.remove();
		Cash.addCash(-getCost());
		return 1;
	}
}
