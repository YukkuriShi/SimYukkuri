package src.yukkuri;


import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.*;
import src.YukkuriUtil.YukkuriType;
import src.system.ModLoader;
import src.yukkuri.Common.Reimu;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.*;


public class WasaReimu extends Reimu implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	public static final int type = 2001;
	public static final String nameJ = "Reimu";
	public static final String nameE = "Reimu";
	public static final int valueSale = 225;
	public static final int valuePurchase = Reimu.valuePurchase;

	private static Image[][][] images = new Image[ConstantValues.NUM_OF_CONDITIONS][2][3];
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
		return images[type][direction][getBodyAgeState().ordinal()];
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
	public WasaReimu(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.REIMU);
		shitType = YukkuriType.REIMU;
		
		getVALUEPURCHASE()[AgeState.ADULT.ordinal()] = 4*valuePurchase;
		getVALUEPURCHASE()[AgeState.CHILD.ordinal()] = 2*valuePurchase;
		getVALUEPURCHASE()[AgeState.BABY.ordinal()] = valuePurchase;
		
		getVALUESALE()[AgeState.ADULT.ordinal()] = 4*valueSale;
		getVALUESALE()[AgeState.CHILD.ordinal()] = 2*valueSale;
		getVALUESALE()[AgeState.BABY.ordinal()] = valueSale ;
		
		int stressFactor = rnd.nextInt(150) - 75;
		int StressMedian = 600;
		STRESSLIMIT[AgeState.ADULT.ordinal()] =StressMedian + stressFactor;
		STRESSLIMIT[AgeState.CHILD.ordinal()] =StressMedian + stressFactor - 150;
		STRESSLIMIT[AgeState.BABY.ordinal()] = StressMedian + stressFactor - 250;
	}
}
