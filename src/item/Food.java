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


public class Food extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 2L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static enum type {SHIT, YUKKURIFOOD, BITTER, LEMONPOP, HOT, VIYUGRA, BODY, STALK, SWEETS1, SWEETS2, WASTE, VOMIT};
	// for food
	public static final int FOOD_EMPTY = 0;
	public static final int FOOD_NOT_EMPTY_NORMAL = 1;
	public static final int FOOD_NOT_EMPTY_BITTER = 2;
	public static final int FOOD_NOT_EMPTY_LEMON = 3;
	public static final int FOOD_NOT_EMPTY_HOT = 4;
	public static final int FOOD_NOT_EMPTY_VIYUGRA = 5;
	public static final int SWEETS_EMPTY = 6;
	public static final int SWEETS_NORMAL = 7;
	public static final int SWEETS_HIGH = 8;
	public static final int WASTE_EMPTY = 9;
	public static final int WASTE = 10;
	public static final int STALK_EMPTY = 11;
	public static final int STALK = 12;
	public static final int FOOD_SHADOW = 13;
	public static final int NUM_OF_FOOD_STATE = 14;

	private type foodType;
	private int amount;
	private int imageType;

	private static final int[] value = {0, 200, 250, 400, 400, 650, 0, 0, 1000, 1000, 0};
	private static final int[] looks = {-200, 100, 80, 100, 80, 100, -300, 200, 300, 300, -100}; 
	
	private static Image[] images = new Image[NUM_OF_FOOD_STATE];
	private static Rectangle[] boundary = new Rectangle[NUM_OF_FOOD_STATE];

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images[FOOD_EMPTY] = ModLoader.loadItemImage(loader, "food/gohan1.png");
		images[FOOD_NOT_EMPTY_NORMAL] = ModLoader.loadItemImage(loader, "food/gohan2.png");
		images[FOOD_NOT_EMPTY_BITTER] = ModLoader.loadItemImage(loader, "food/gohan3.png");
		images[FOOD_NOT_EMPTY_LEMON] = ModLoader.loadItemImage(loader, "food/gohan4.png");
		images[FOOD_NOT_EMPTY_HOT] = ModLoader.loadItemImage(loader, "food/gohan5.png");
		images[FOOD_NOT_EMPTY_VIYUGRA] = ModLoader.loadItemImage(loader, "food/gohan6.png");

		images[SWEETS_EMPTY] = ModLoader.loadItemImage(loader, "food/sweets1.png");
		images[SWEETS_NORMAL] = ModLoader.loadItemImage(loader, "food/sweets2.png");
		images[SWEETS_HIGH] = ModLoader.loadItemImage(loader, "food/sweets3.png");

		images[WASTE_EMPTY] = ModLoader.loadItemImage(loader, "food/waste1.png");
		images[WASTE] = ModLoader.loadItemImage(loader, "food/waste2.png");

		images[STALK_EMPTY] = ModLoader.loadItemImage(loader, "food/stalk_food1.png");
		images[STALK] = ModLoader.loadItemImage(loader, "food/stalk_food2.png");

		images[FOOD_SHADOW] = ModLoader.loadItemImage(loader, "food/gohan-shadow.png");

		for (int i=0; i < NUM_OF_FOOD_STATE; i++) {
			boundary[i] = new Rectangle();
			boundary[i].width = images[i].getWidth(io);
			boundary[i].height = images[i].getHeight(io);
			boundary[i].x = boundary[i].width >> 1;
			boundary[i].y = boundary[i].height - 1;
		}
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public int getImageLayerCount() {
		return 1;
	}

	@Override
	public Image getImage(int idx) {
		if (isEmpty()) {
			switch (imageType) {
				default:
					return images[FOOD_EMPTY];
				case SWEETS_NORMAL:
				case SWEETS_HIGH:
					return images[SWEETS_EMPTY];
				case WASTE:
					return images[WASTE_EMPTY];
				case STALK:
					return images[STALK_EMPTY];
			}
		}
		else {
			return images[imageType];	
		}
	}

	public Image getShadowImage() {
		return images[FOOD_SHADOW];
	}

	public static Rectangle getBounding(int idx) {
		return boundary[idx];
	}

	@Override
	public void removeListData(){
		objEXList.remove(this);
	}

	@Override
	public int getValue() {
		return value[foodType.ordinal()];
	}

	@Override
	public int getLooks() {
		return looks[foodType.ordinal()];
	}

	public Food(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		foodType = type.values()[initOption];
		switch (foodType) {
			case YUKKURIFOOD:
				amount = 100*24*24;
				imageType = FOOD_NOT_EMPTY_NORMAL;
				break;
			case BITTER:
				amount = 100*24*24;
				imageType = FOOD_NOT_EMPTY_BITTER;
				break;
			case LEMONPOP:
				amount = 100*24*24;
				imageType = FOOD_NOT_EMPTY_LEMON;
				break;
			case HOT:
				amount = 100*24*24;
				imageType = FOOD_NOT_EMPTY_HOT;
				break;
			case VIYUGRA:
				amount = 100*24*24;
				imageType = FOOD_NOT_EMPTY_VIYUGRA;
				break;
			case STALK:
				amount = 100*24*2;
				imageType = STALK;
				break;
			case SWEETS1:
				amount = 100*24*4;
				imageType = SWEETS_NORMAL;
				break;
			case SWEETS2:
				amount = 100*24*4;
				imageType = SWEETS_HIGH;
				break;
			case WASTE:
				amount = 100*24*32;
				imageType = WASTE;
				break;
			default:
				amount = 100*24*24;
				imageType = 0;
				break;
		}
		setBoundary(boundary[imageType]);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.FOOD;
		removed = false;
	}

	public type getFoodType() {
		return foodType;
	}

	public boolean isEmpty() {
		return (amount == 0);
	}
	
	public int getAmount() {
		return amount;
	}

	public void eatFood(int eatAmount)
	{
		if (isEmpty()) {
			return;
		}
		amount -= eatAmount;
		if (amount < 0) {
			amount = 0;
		}
	}
	
	@Override
	public void kick() {
		kick(0,  -8,  -4);
	}
}
