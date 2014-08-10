package src.yukkuri;


import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.*;
import src.YukkuriUtil.YukkuriType;
import src.system.ModLoader;
import src.yukkuri.Common.Marisa;
import src.yukkuri.Common.Reimu;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.*;


public class Deibu extends Body implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	public static final int type = 2005;
	public static final String nameJ = "Deibu";
	public static final String nameE = "Deibu";
	
	public static final int valueSale = 50;
	public static final int valuePurchase = Reimu.valuePurchase;

	private static Image[][][] images = new Image[ConstantValues.NUM_OF_CONDITIONS][2][3];
	private static Rectangle[] boundary = new Rectangle[3];
	private static Dimension[] braidBoundary = new Dimension[3];
	private static boolean imageLoaded = false;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		if(imageLoaded) return;

		ModLoader.loadBodyImagePack(loader, images, "deibu", io);
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
	public int getHybridType(int partnerType) {
		switch (partnerType) {
		case Marisa.type:
			return MarisaReimu.type;
		default:
			return Deibu.type;
		}
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

	// public methods
	public Deibu(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.DEIBU);
		shitType = YukkuriType.DEIBU;
	}
	
	public void tuneParameters() {
		if (rnd.nextBoolean()) {
			motherhood = true;
		}
		EATAMOUNT[AgeState.ADULT.ordinal()] *= 1.5f;
		EATAMOUNT[AgeState.CHILD.ordinal()] *= 1.5f;
		EATAMOUNT[AgeState.BABY.ordinal()] *= 1.5f;
		WEIGHT[AgeState.ADULT.ordinal()] *= 1.5f;
		WEIGHT[AgeState.CHILD.ordinal()] *= 1.5f;
		WEIGHT[AgeState.BABY.ordinal()] *= 1.5f;
		double factor = Math.random()*2+1;
		HUNGRYLIMIT[AgeState.ADULT.ordinal()] *= factor;
		HUNGRYLIMIT[AgeState.CHILD.ordinal()] *= factor;
		HUNGRYLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()*2+1;
		SHITLIMIT[AgeState.ADULT.ordinal()] *= factor;
		SHITLIMIT[AgeState.CHILD.ordinal()] *= factor;
		SHITLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+0.5;
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
		sameDest = rnd.nextInt(20)+20;
		DECLINEPERIOD *= (Math.random()+0.5);
		DISCIPLINELIMIT *= (Math.random()+1);
		ROBUSTNESS = rnd.nextInt(5)+2;
		//EYESIGHT /= 4;
		factor = Math.random()+0.5;
		STRENGTH[AgeState.ADULT.ordinal()] = 150*30;
		STRENGTH[AgeState.CHILD.ordinal()] = 150*11;
		STRENGTH[AgeState.BABY.ordinal()]  = 115;
		
		setAttitude(Attitude.SUPER_SHITHEAD);
		
		int stressFactor = rnd.nextInt(50) - 50;
		int StressMedian = 575;
		STRESSLIMIT[AgeState.ADULT.ordinal()] =StressMedian + stressFactor;
		STRESSLIMIT[AgeState.CHILD.ordinal()] =StressMedian + stressFactor - 150;
		STRESSLIMIT[AgeState.BABY.ordinal()] = StressMedian + stressFactor - 250;
		
		getVALUEPURCHASE()[AgeState.ADULT.ordinal()] = 4*valuePurchase;
		getVALUEPURCHASE()[AgeState.CHILD.ordinal()] = 2*valuePurchase;
		getVALUEPURCHASE()[AgeState.BABY.ordinal()] = valuePurchase;
		
		getVALUESALE()[AgeState.ADULT.ordinal()] = 4*valueSale;
		getVALUESALE()[AgeState.CHILD.ordinal()] = 2*valueSale;
		getVALUESALE()[AgeState.BABY.ordinal()] = valueSale ;
		
		speed = 105;
		braidType = true;
		TRASH_OKAZARI_OFS_X[0] = 0;
		TRASH_OKAZARI_OFS_X[1] = 0;
		TRASH_OKAZARI_OFS_X[2] = 0;

		TRASH_OKAZARI_OFS_Y[0] = -8;
		TRASH_OKAZARI_OFS_Y[1] = -14;
		TRASH_OKAZARI_OFS_Y[2] = -32;
	}
}