package src.item;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.*;


public class Beltconveyor extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = ObjEX.YUKKURI | ObjEX.SHIT | ObjEX.FOOD | ObjEX.VOMIT | ObjEX.TOY;
	private static final int images_num = 10; //縺薙�繧ｯ繝ｩ繧ｹ縺ｮ邱丈ｽｿ逕ｨ逕ｻ蜒乗焚
	private static int AnimeImagesNum[] = {5,5};//繧｢繝九Γ縺斐→縺ｫ菴墓椢菴ｿ縺�°
	private static Image[] images = new Image[images_num];
	private static Rectangle boundary = new Rectangle();

	private int beltSpeed;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for( int i = 0; i < images_num ; i++ ){
			images[i] = ModLoader.loadItemImage(loader, "beltconveyor/beltconveyor" + String.format("%03d",i+1) + ".png");		
		}
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}
	
	@Override
	public Image getImage() {
		int frame = 0;

		if(enabled) frame = (int)age;

		switch ( option ) {	//讌ｽ縺ｫ繧｢繝九Γ謖�ｮ壹〒縺阪ｋ繧医≧縺ｫ縺励◆縺�′蠕後〒
			case 0:
			default:
				return images[frame/4%AnimeImagesNum[0]];	//4繝輔Ξ繝ｼ繝�↓1蝗樒判蜒乗峩譁ｰ
			case 1:
				return images[4-(frame/4%AnimeImagesNum[0])];
			case 2:
				return images[9-frame/4%AnimeImagesNum[1]];
			case 3:
				return images[5+(frame/4%AnimeImagesNum[1])];
		}
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
	public int checkHitObj(Rectangle colRect, Obj o) {

		if(o instanceof Body) {
			if(((Body)o).isCantMove()) return 0;
		}
		if (o.getZ() == 0) {	//遨ｺ荳ｭ縺ｮ迚ｩ縲√�繧翫▽縺�※繧狗黄縺ｯ遘ｻ蜍輔＆縺帙↑縺�
			// 蟇ｾ雎｡縺ｮ蠎ｧ讓吶ｒ繝輔ぅ繝ｼ繝ｫ繝牙ｺｧ讓吶↓螟画鋤
			Translate.translate(o.getX(), o.getY(), tmpPos);
			// 轤ｹ縺梧緒逕ｻ遏ｩ蠖｢縺ｫ蜈･縺｣縺溘°縺ｮ蛻､螳�
			if(colRect.contains(tmpPos)) {
				objHitProcess( o );
				return 1;
			}
		}
		return 0;
	}

	@Override
	public int objHitProcess( Obj o ) {
		int objX = o.getX();
		int objY = o.getY();
		int objW = o.getW();
		int objH = o.getH();
		int attr = Terrarium.MAP_ADULT;
		
		if (o instanceof Body) {
			attr = Terrarium.MAP_BODY[((Body)o).getAgeState().ordinal()];
		}
		
		if (  !Terrarium.onBarrier( objX, objY, objW >> 1, objH >> 2, attr)) {		//螢√↓縺ｲ縺｣縺九°縺｣縺ｦ縺�ｌ縺ｰ遘ｻ蜍輔＆縺帙↑縺�
			switch ( option ) {	//遘ｻ蜍輔＆縺帙ｋ譁ｹ蜷�
				case 0:
				default:
					o.setY(objY-beltSpeed);
					break;
				case 1:
					o.setY(objY+beltSpeed);
					break;
				case 2:
					o.setX(objX+beltSpeed);
					break;
				case 3:
					o.setX(objX-beltSpeed);
					break;
			}
		}
		return 0;
	}

	@Override
	public void upDate() {
		if ( age % 2400 == 0 ) {
			Cash.addCash(-getCost());
		}
	}

	public int getBeltSpeed() {
		return beltSpeed;
	}

	@Override
	public void removeListData(){
		objEXList.remove(this);
	}

	@Override
	public boolean checkInterval(int cnt) {
		return true;
	}

	public Beltconveyor(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.BELTCONVEYOR;
		beltSpeed = 1;
		value = 2000;
		cost = 25;
	}
}
