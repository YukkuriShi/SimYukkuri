package src.yukkuri.Rare;


import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.*;
import src.YukkuriUtil.YukkuriType;
import src.system.ModLoader;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.*;

public class Suwako extends Body implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	public static final int type = 1006;
	public static final String nameJ = "すわこ";
	public static final String nameE = "Suwako";
	
	public static final int valueSale = 750;
	public static final int valuePurchase = 1500;

	private static Image[][][] images = new Image[ConstantValues.NUM_OF_CONDITIONS][2][3];
	private static Rectangle[] boundary = new Rectangle[3];
	private static Dimension[] braidBoundary = new Dimension[3];
	private static boolean imageLoaded = false;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		if(imageLoaded) return;

		ModLoader.loadBodyImagePack(loader, images, "suwako", io);
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

	@Override
	public String getNameJ2() {
		return "";
	}

	@Override
	public String getNameE2() {
		return "";
	}

	// Constructor of this class.
	public Suwako(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.SUWAKO);
		shitType = YukkuriType.SUWAKO;
	}
	
	public void tuneParameters() {
		// Tune individual parameters.
		double factor = Math.random()+1;
		HUNGRYLIMIT[AgeState.ADULT.ordinal()] *= factor; 
		HUNGRYLIMIT[AgeState.CHILD.ordinal()] *= factor;
		HUNGRYLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+1;
		SHITLIMIT[AgeState.ADULT.ordinal()] *= factor;
		SHITLIMIT[AgeState.CHILD.ordinal()] *= factor;
		SHITLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()*2+1;
		DAMAGELIMIT[AgeState.ADULT.ordinal()] *= factor;
		DAMAGELIMIT[AgeState.CHILD.ordinal()] *= factor;
		DAMAGELIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+0.5;
		BABYLIMIT *= factor;
		CHILDLIMIT *= factor;
		LIFELIMIT *= factor;
		factor = Math.random()+1;
		RELAXPERIOD *= factor;
		EXCITEPERIOD *= factor;
		PREGPERIOD *= factor;
		SLEEPPERIOD *= factor;
		ACTIVEPERIOD *= factor;
		sameDest = rnd.nextInt(10)+10;
		DECLINEPERIOD *= (Math.random()+0.5);
		DISCIPLINELIMIT *= (Math.random()+1);
		ROBUSTNESS = rnd.nextInt(10)+2;
		//EYESIGHT /= 1;
		factor = Math.random()+1;
		STRENGTH[AgeState.ADULT.ordinal()] *= factor;
		STRENGTH[AgeState.CHILD.ordinal()] *= factor;
		STRENGTH[AgeState.BABY.ordinal()] *= factor;
		
		getVALUEPURCHASE()[AgeState.ADULT.ordinal()] = 4*valuePurchase;
		getVALUEPURCHASE()[AgeState.CHILD.ordinal()] = 2*valuePurchase;
		getVALUEPURCHASE()[AgeState.BABY.ordinal()] = valuePurchase;
		
		getVALUESALE()[AgeState.ADULT.ordinal()] = 4*valueSale;
		getVALUESALE()[AgeState.CHILD.ordinal()] = 2*valueSale;
		getVALUESALE()[AgeState.BABY.ordinal()] = valueSale ;
		
		int stressFactor = rnd.nextInt(100) - 50;
		int StressMedian = 650;
		STRESSLIMIT[AgeState.ADULT.ordinal()] =StressMedian + stressFactor;
		STRESSLIMIT[AgeState.CHILD.ordinal()] =StressMedian + stressFactor - 150;
		STRESSLIMIT[AgeState.BABY.ordinal()] = StressMedian + stressFactor - 250;

		braidType = true;
	}
}