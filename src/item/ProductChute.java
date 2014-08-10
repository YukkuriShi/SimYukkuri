package src.item;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.*;
import src.object.Obj;
import src.object.ObjEX;
import src.system.ModLoader;
import src.yukkuriBody.Body;


public class ProductChute extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = 159;
	private static final int images_num = 1; //縺薙�繧ｯ繝ｩ繧ｹ縺ｮ邱丈ｽｿ逕ｨ逕ｻ蜒乗焚
	private static Image[] images = new Image[images_num];
	private static Rectangle boundary = new Rectangle();
	protected Random rnd = new Random();

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "productchute.png");		
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	public Image getImage(){
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
		return hitCheckObjType;
	}

	@Override
	public int objHitProcess( Obj o ) {
		if (o instanceof Body) {
			Cash.sellYukkuri((Body)o);
		} else {
			Cash.addCash(o.getValue() >> 1);
		}
		Cash.addCash( -getCost() );
		o.remove();
		return 0;
	}
	
	@Override
	public void removeListData(){
		objEXList.remove(this);
	}

	public ProductChute(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.PRODUCTCHUTE;
		
		interval = 10;
		value = 1000;
		cost = 0;
	}
}
