package src.yukkuri;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.*;
import src.Body.AgeState;
import src.YukkuriUtil.YukkuriType;


public class Marisa extends Body implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	public static final int type = 0;
	public static final String nameJ = "まりさ";
	public static final String nameE = "Marisa";
	public static final int valueSale = 125;
	public static final int valuePurchase = 250;

	private static Image[][][] images = new Image[NUM_OF_CONDITIONS][2][3];
	private static Rectangle[] boundary = new Rectangle[3];
	private static Dimension[] braidBoundary = new Dimension[3];
	private static boolean imageLoaded = false;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		if(imageLoaded) return;

		ModLoader.loadBodyImagePack(loader, images, "marisa", io);
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
	public int getHybridType(int partnerType) {
		switch (partnerType) {
		case Reimu.type:
		case WasaReimu.type:
			return ReimuMarisa.type;
		default:
			return Marisa.type;
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
	public Marisa(int initX, int initY, int initZ, Body.AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		msgType = YukkuriType.MARISA;
		shitType = YukkuriType.MARISA;
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
		
		VALUEPURCHASE[AgeState.ADULT.ordinal()] = 4*valuePurchase;
		VALUEPURCHASE[AgeState.CHILD.ordinal()] = 2*valuePurchase;
		VALUEPURCHASE[AgeState.BABY.ordinal()] = valuePurchase;
		
		VALUESALE[AgeState.ADULT.ordinal()] = 4*valueSale;
		VALUESALE[AgeState.CHILD.ordinal()] = 2*valueSale;
		VALUESALE[AgeState.BABY.ordinal()] = valueSale ;
		
		int stressFactor = rnd.nextInt(200) - 100;
		int StressMedian = 750;
		STRESSLIMIT[AgeState.ADULT.ordinal()] =StressMedian + stressFactor;
		STRESSLIMIT[AgeState.CHILD.ordinal()] =StressMedian + stressFactor - 150;
		STRESSLIMIT[AgeState.BABY.ordinal()] = StressMedian + stressFactor - 250;
		
		speed = 120;
		braidType = true;
	}
}