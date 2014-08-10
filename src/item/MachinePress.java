package src.item;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.*;
import src.yukkuriBody.Body;



public class MachinePress extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = ObjEX.YUKKURI | ObjEX.SHIT | ObjEX.VOMIT;
	private static final int images_num = 8; //縺薙�繧ｯ繝ｩ繧ｹ縺ｮ邱丈ｽｿ逕ｨ逕ｻ蜒乗焚
	private static int AnimeImagesNum[] = {8};//繧｢繝九Γ縺斐→縺ｫ菴墓椢菴ｿ縺�°
	private static Image[] images = new Image[images_num + 1];
	private static Rectangle boundary = new Rectangle();

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for( int i = 0; i < images_num ; i++ ) {
			images[i] = ModLoader.loadItemImage(loader, "machinepress/machinepress" + String.format("%03d",i+1) + ".png");		
		}
		images[images_num] = ModLoader.loadItemImage(loader, "machinepress/machinepress_off.png");		
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}

	@Override
	public Image getImage() {
		if(enabled) return images[(int)age / 2 % AnimeImagesNum[0]];
		 return images[AnimeImagesNum[0]];
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
	public int objHitProcess( Obj o ) {
		if ( o.getObjType() == Obj.Type.YUKKURI && (int)age/2%AnimeImagesNum[0] == 0) {
			Body p = (Body)o;
			p.strikeByPress();
		}
		return 0;
	}

	@Override
	public void upDate() {
		if ( age % 2400 == 0 ){
			Cash.addCash(-getCost());
		}
	}

	@Override
	public void removeListData(){
		objEXList.remove(this);
	}
	
	public MachinePress(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), 8);
		objEXList.add(this);
		objType = Type.FIX_OBJECT;
		objEXType = ObjEXType.MACHINEPRESS;

		value = 20000;
		cost = 50;
	}
}
