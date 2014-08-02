package src.item;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.*;
import src.YukkuriUtil.YukkuriType;


public class FoodMaker extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = ObjEX.YUKKURI | ObjEX.SHIT | ObjEX.FOOD | ObjEX.VOMIT;
	private static final int images_num = 6; //縺薙�繧ｯ繝ｩ繧ｹ縺ｮ邱丈ｽｿ逕ｨ逕ｻ蜒乗焚
	private static int AnimeImagesNum[] = {images_num};//繧｢繝九Γ縺斐→縺ｫ菴墓椢菴ｿ縺�°
	private static Image[] images = new Image[images_num + 1];
	private static Rectangle boundary = new Rectangle();
	protected Random rnd = new Random();

	protected boolean processReady = true;
	protected int stockFood = -1;
	private static final int numOfBodyType = 5;
	private static final int numOfFoodType = 5;

											//Re(Wasa),Ma,Al,Ta,Hy or rare,YF,BT,LP,HOT,VG,shit
	private static final int[][] makeTable = {{ 1,0,5,0,1,1,2,3,4,5,0},//Re(Wasa)
											  { 0,1,5,0,1,1,2,3,4,5,0},//Ma
											  { 5,5,1,0,1,5,2,5,4,5,0},//Al
											  { 0,0,0,2,1,2,0,2,0,0,0},//Ta
											  { 1,1,1,1,3,1,1,1,1,1,1},//Hy or rare
											  { 1,1,5,2,1,1,1,1,4,1,0},//YF
											  { 2,2,2,0,1,1,2,0,0,0,0},//BT
											  { 3,3,5,2,1,1,0,3,4,0,0},//LP
											  { 4,4,4,0,1,4,0,4,4,0,0},//HOT
											  { 5,5,5,0,1,1,0,0,0,5,0},//VG
											  { 0,0,0,0,1,0,0,0,0,0,0}};//shit
	
	private static final Food.type[] foodTable =	{Food.type.SHIT,
													 Food.type.YUKKURIFOOD,
													 Food.type.BITTER,
													 Food.type.LEMONPOP,
													 Food.type.HOT,
													 Food.type.VIYUGRA
													 };

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for( int i = 0; i < images_num; i++ ){
			images[i] = ModLoader.loadItemImage(loader, "foodmaker/foodmaker" + String.format("%03d",i+1) + ".png");
		}
		images[images_num] = ModLoader.loadItemImage(loader, "foodmaker/foodmaker_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
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
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override	
	public int objHitProcess( Obj o ) {
		if ( !processReady ){
			return 0;
		}
		Food.type foodType = null;
		if (stockFood == -1){
			if ( o.getObjType() == Obj.Type.YUKKURI ){
				Body b = (Body)o;
				if ( b.isCrashed() == true || b.isBaby() == true ) {
					if ( b.getType() == 3 ) {
						stockFood = 0;
					}else if ( b.getType() == 4 ) {
						stockFood = 3;
					}else if( b.getType() >= 6 ){
						stockFood = 4;
					}else if( b.getType() >= 10000 ){
						stockFood = 4;
					}else{
						stockFood = b.getType();
					}
					if ( b.isAdult() == true ) {
						objHitProcess( o );
					}else{
						processReady = false;
						b.remove();
					}
				}
			}else if ( o.getObjType() == Obj.Type.PLATFORM && o instanceof Food ) {
				Food f = (Food)o;
				switch(f.getFoodType()){
					case YUKKURIFOOD:
						stockFood = numOfBodyType;
						break;
					case BITTER:
						stockFood = numOfBodyType + 1;
						break;
					case LEMONPOP:
						stockFood = numOfBodyType + 2;
						break;
					case HOT:
						stockFood = numOfBodyType + 3;
						break;
					case VIYUGRA:
						stockFood = numOfBodyType + 4;
						break;
					default:
						stockFood = numOfBodyType;
						break;
				}
				processReady = false;
				f.remove();
			}else if ( o.getObjType() == Obj.Type.SHIT ){
				Shit s = (Shit)o;
				stockFood = numOfBodyType+numOfFoodType;
				processReady = false;
				s.remove();
			}
		}else{
			if ( o.getObjType() == Obj.Type.YUKKURI ){
				Body b = (Body)o;
				if ( b.isCrashed() == true || b.isBaby() == true ) {
					if ( b.getType() == 3 ) {
						foodType = foodTable[makeTable[stockFood][0]];
					}else if ( b.getType() == 4 ) {
						foodType = foodTable[makeTable[stockFood][3]];
					}else if ( b.getType() >= 6 ) {
						foodType = foodTable[makeTable[stockFood][4]];
					}else if( b.getType() >= 10000 ){
						foodType = foodTable[makeTable[stockFood][4]];
					}else{
						foodType = foodTable[makeTable[stockFood][b.getType()]];
					}
					processReady = false;
					Cash.addCash(-getCost());
					b.remove();
				}
			}else if ( o.getObjType() == Obj.Type.PLATFORM && o instanceof Food ){
				Food f = (Food)o;
				switch(f.getFoodType()){
					case YUKKURIFOOD:
						foodType = foodTable[makeTable[stockFood][numOfBodyType]];
						break;
					case BITTER:
						foodType = foodTable[makeTable[stockFood][numOfBodyType + 1]];
						break;
					case LEMONPOP:
						foodType = foodTable[makeTable[stockFood][numOfBodyType + 2]];
						break;
					case HOT:
						foodType = foodTable[makeTable[stockFood][numOfBodyType + 3]];
						break;
					case VIYUGRA:
						foodType = foodTable[makeTable[stockFood][numOfBodyType + 4]];
						break;
					default:
						break;
				}
				processReady = false;
				Cash.addCash(-getCost());
				f.remove();
			}else if ( o.getObjType() == Obj.Type.SHIT ){
				Shit s = (Shit)o;
				foodType = foodTable[makeTable[stockFood][numOfBodyType+numOfFoodType]];
				processReady = false;
				Cash.addCash(-getCost());
				s.remove();
			}
			if (foodType == null) {
				return 0;
			}
			int dir = 1;
			if(x + 52 >= Terrarium.MAX_X) dir = -1;
			if (foodType == Food.type.SHIT){
				SimYukkuri.mypane.terrarium.addVomit(x + (52 * dir), y, 0, Body.AgeState.ADULT, YukkuriType.REIMU);
			}else{
				SimYukkuri.mypane.terrarium.addObjEX(ObjEX.ObjEXType.FOOD, x + (52 * dir), y, foodType.ordinal());
			}
			stockFood = -1;
		}
		return 0;
	}

	@Override
	public void upDate() {
		if ( age % 4 == 0 && processReady == false ){
			processReady = true;
		}
	}
	
	@Override
	public void removeListData(){
		objEXList.remove(this);
	}

	public FoodMaker(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.FOODMAKER;
		
		interval = 20;
		value = 10000;
		cost = 100;
	}
}
