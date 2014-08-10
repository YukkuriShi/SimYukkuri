package src.item;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.*;
import src.object.ObjEX;
import src.system.ModLoader;


public class Trash extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	private static Image[] images = new Image[2];
	private static Rectangle boundary = new Rectangle();

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		images[0] = ModLoader.loadItemImage(loader, "trash/trash.png");
		images[1] = ModLoader.loadItemImage(loader, "trash/trash_shadow.png");
		
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}

	@Override
	public Image getImage() {
		return images[0];
	}

	@Override
	public int getImageLayerCount() {
		return 1;
	}

	@Override
	public Image getImage(int idx) {
		return images[0];
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public Image getShadowImage() {
		return images[1];
	}
	
	@Override
	public void removeListData(){
		objEXList.remove(this);
	}
	
	@Override
	public void kick() {
		kick(0, -8, -4);
	}

	public Trash(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.TRASH;
		
		value = 0;
		cost = 0;
	}
}
