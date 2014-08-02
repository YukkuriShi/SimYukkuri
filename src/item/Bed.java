package src.item;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.*;


public class Bed extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	private static Image images;
	private static Rectangle boundary = new Rectangle();

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		images = ModLoader.loadItemImage(loader, "bed.png");
		boundary.width = images.getWidth(io);
		boundary.height = images.getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}
	
	@Override
	public Image getImage() {
		return images;
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
	public void removeListData(){
		objEXList.remove(this);
	}

	@Override
	public int getValue() {
		return value;
	}
	
	public Bed(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.BED;
		
		interval = 5000;
		value = 1000;
		cost = 0;
	}
}
