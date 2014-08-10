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


public class DosMarisa extends Body implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	public static final int type = 2006;
	public static final String nameJ = "Dosu";
	public static final String nameE = "Dosu";
	public static final int valueSale = 5000;
	public static final int valuePurchase = Marisa.valuePurchase;

	private static Image[][][] images = new Image[ConstantValues.NUM_OF_CONDITIONS][2][3];
	private static Rectangle[] boundary = new Rectangle[3];
	private static Dimension[] braidBoundary = new Dimension[3];
	private static boolean imageLoaded = false;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		if(imageLoaded) return;

		ModLoader.loadBodyImagePack(loader, images, "dosmarisa", io);
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
		case Reimu.type:
		case WasaReimu.type:
			return ReimuMarisa.type;
		default:
			return DosMarisa.type;
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
	
	// Constructor of this class.
	public DosMarisa(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.DOSMARISA);
		shitType = YukkuriType.DOSMARISA;
	}
	
	public void tuneParameters() {
		// Tune individual parameters.
		double factor = Math.random()+3.0;
		EATAMOUNT[AgeState.ADULT.ordinal()] *= factor;
		EATAMOUNT[AgeState.CHILD.ordinal()] *= factor;
		EATAMOUNT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+10.0;
		WEIGHT[AgeState.ADULT.ordinal()] *= factor;
		WEIGHT[AgeState.CHILD.ordinal()] *= factor;
		WEIGHT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+1.0;
		HUNGRYLIMIT[AgeState.ADULT.ordinal()] *= factor; 
		HUNGRYLIMIT[AgeState.CHILD.ordinal()] *= factor;
		HUNGRYLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+3.0;
		SHITLIMIT[AgeState.ADULT.ordinal()] *= factor;
		SHITLIMIT[AgeState.CHILD.ordinal()] *= factor;
		SHITLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()*2+30.0;
		DAMAGELIMIT[AgeState.ADULT.ordinal()] *= factor;
		DAMAGELIMIT[AgeState.CHILD.ordinal()] *= factor;
		DAMAGELIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+3.5;
		BABYLIMIT *= factor;
		CHILDLIMIT *= factor;
		LIFELIMIT *= factor;
		factor = Math.random()+3.0;
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
		factor = Math.random()+6.0;
		STRENGTH[AgeState.ADULT.ordinal()] *= factor;
		STRENGTH[AgeState.CHILD.ordinal()] *= factor;
		STRENGTH[AgeState.BABY.ordinal()] *= factor;
		
		int stressFactor = rnd.nextInt(350) - 175;
		int StressMedian = 1950;
		STRESSLIMIT[AgeState.ADULT.ordinal()] =StressMedian + stressFactor;
		STRESSLIMIT[AgeState.CHILD.ordinal()] =StressMedian + stressFactor - 150;
		STRESSLIMIT[AgeState.BABY.ordinal()] = StressMedian + stressFactor - 250;
		

		getVALUEPURCHASE()[AgeState.ADULT.ordinal()] = 4*valuePurchase;
		getVALUEPURCHASE()[AgeState.CHILD.ordinal()] = 2*valuePurchase;
		getVALUEPURCHASE()[AgeState.BABY.ordinal()] = valuePurchase;
		
		getVALUESALE()[AgeState.ADULT.ordinal()] = 4*valueSale;
		getVALUESALE()[AgeState.CHILD.ordinal()] = 2*valueSale;
		getVALUESALE()[AgeState.BABY.ordinal()] = valueSale ;
		
		comfortTempRange += 15;
		
		speed = 100;
		braidType = true;
		TRASH_OKAZARI_OFS_X[0] = 12;
		TRASH_OKAZARI_OFS_X[1] = 25;
		TRASH_OKAZARI_OFS_X[2] = 50;

		TRASH_OKAZARI_OFS_Y[0] = -44;
		TRASH_OKAZARI_OFS_Y[1] = -95;
		TRASH_OKAZARI_OFS_Y[2] = -190;
	}
}