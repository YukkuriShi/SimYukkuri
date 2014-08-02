package src.yukkuri;


import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.*;
import src.Body.AgeState;
import src.YukkuriUtil.YukkuriType;


public class WasaReimu extends Reimu implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	public static final int type = 2001;
	public static final String nameJ = "Reimu";
	public static final String nameE = "Reimu";
	public static final int valueSale = 225;
	public static final int valuePurchase = Reimu.valuePurchase;

	private static Image[][][] images = new Image[NUM_OF_CONDITIONS][2][3];
	private static Rectangle[] boundary = new Rectangle[3];
	private static Dimension[] braidBoundary = new Dimension[3];
	private static boolean imageLoaded = false;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		if(imageLoaded) return;

		ModLoader.loadBodyImagePack(loader, images, "wasa", io);
		ModLoader.setBoundary(images, boundary, braidBoundary, io);
		imageLoaded = true;
	}
	
	public Image getImage(int type, int direction) {
		return images[type][direction][bodyAgeState.ordinal()];
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public String getNameJ() {
		return nameJ;
	}

	@Override
	public String getNameE() {
		return nameE;
	}

	// public methods
	public WasaReimu(int initX, int initY, int initZ, Body.AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		msgType = YukkuriType.REIMU;
		shitType = YukkuriType.REIMU;
		
		VALUEPURCHASE[AgeState.ADULT.ordinal()] = 4*valuePurchase;
		VALUEPURCHASE[AgeState.CHILD.ordinal()] = 2*valuePurchase;
		VALUEPURCHASE[AgeState.BABY.ordinal()] = valuePurchase;
		
		VALUESALE[AgeState.ADULT.ordinal()] = 4*valueSale;
		VALUESALE[AgeState.CHILD.ordinal()] = 2*valueSale;
		VALUESALE[AgeState.BABY.ordinal()] = valueSale ;
		
		int stressFactor = rnd.nextInt(150) - 75;
		int StressMedian = 600;
		STRESSLIMIT[AgeState.ADULT.ordinal()] =StressMedian + stressFactor;
		STRESSLIMIT[AgeState.CHILD.ordinal()] =StressMedian + stressFactor - 150;
		STRESSLIMIT[AgeState.BABY.ordinal()] = StressMedian + stressFactor - 250;
	}
}
