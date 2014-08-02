package src.item;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.*;


public class Toy extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	private static final int BALL = 0;
	private static final int SHADOW = 1;
	private static final int NUM_OF_BALL_IMG = 2;
	private static Image[] images = new Image[NUM_OF_BALL_IMG];
	private static Rectangle boundary = new Rectangle();
	private Body owner = null;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		images[BALL] = ModLoader.loadItemImage(loader, "toy/ball.png");
		images[SHADOW] = ModLoader.loadItemImage(loader, "toy/shadow.png");
		
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}
	
	@Override
	public Image getImage() {
		return (images[BALL]);
	}

	@Override
	public Image getImage(int idx) {
		return (images[BALL]);
	}

	@Override
	public int getImageLayerCount() {
		return 1;
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public Image getShadowImage() {
		return images[SHADOW];
	}
	
	@Override
	public void removeListData(){
		objEXList.remove(this);
	}

	@Override
	public void grab() {
		owner = null;
		grabbed = true;
	}
	
	@Override
	public void kick() {
		kick(0, -8, -4);
	}
	
	public void setOwner(Body b) {
		owner = b;
	}
	
	public Body getOwner() {
		return owner;
	}
	
	public boolean isOwned(Body b) {
		return (owner == b);
	}

	public Toy(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.TOY;
		value = 2000;
		cost = 0;
	}
}
