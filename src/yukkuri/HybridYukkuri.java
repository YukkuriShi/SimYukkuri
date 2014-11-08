package src.yukkuri;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.*;
import src.YukkuriUtil.YukkuriType;
import src.yukkuri.Common.Reimu;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.*;


public class HybridYukkuri extends Body implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	public static final int type = 20000;
	public String nameJ;
	public String nameE;
	public String nameJ2;
	public String nameE2;
	protected Body dorei;
	protected Body dorei2;
	protected Body dorei3;
	protected Body dorei4;
	public static final int valueSale = 1500;
	public static final int valuePurchase = 0;
	
	private Body[] images;
	private static Rectangle[] boundary = new Rectangle[3];
	private static Dimension[] braidBoundary = new Dimension[3];

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
	}
	
	public void loadImages_Hyblid () throws IOException {
		HybridYukkuri	parentTmp =	null;
		HybridYukkuri	parentTmp2 = null;
		Body			doreiTmp =	null;
		Body			doreiTmp2 =	null;
		nameJ = "繧�▲縺上ｊ";
		nameE = "Yukkuri";
		nameJ2 = "繧�▲縺上ｊ";
		nameE2 = "Yukkuri";

		if(  parents[Parent.MAMA.ordinal()] == null && parents[Parent.PAPA.ordinal()] == null  ){
			doreiTmp = new Reimu(100, 100, 0, AgeState.BABY, null, null);
			doreiTmp2 = dorei;
		}else{
			if ( parents[Parent.MAMA.ordinal()] != null ){
				if ( parents[Parent.MAMA.ordinal()].getType() == 20000 ){
					parentTmp = (HybridYukkuri)parents[Parent.MAMA.ordinal()];
					doreiTmp = parentTmp.dorei;
				}else{
					doreiTmp = SimYukkuri.mypane.terrarium.makeBody(0, 0, 0, parents[Parent.MAMA.ordinal()].getType(), null, AgeState.BABY, null, null);
				}
			}else{
				if ( parents[Parent.PAPA.ordinal()].getType() == 20000 ){
					parentTmp = (HybridYukkuri)parents[Parent.PAPA.ordinal()];
					doreiTmp = parentTmp.dorei;
				}else{
					doreiTmp = SimYukkuri.mypane.terrarium.makeBody(0, 0, 0, parents[Parent.PAPA.ordinal()].getType(), null, AgeState.BABY, null, null);
				}
			}

			if ( parents[Parent.PAPA.ordinal()] != null ){
				if ( parents[Parent.PAPA.ordinal()].getType() == 20000 ){
					parentTmp2 = (HybridYukkuri)parents[Parent.PAPA.ordinal()];
					doreiTmp2 = parentTmp2.dorei;
				}else{
					doreiTmp2 = SimYukkuri.mypane.terrarium.makeBody(0, 0, 0, parents[Parent.PAPA.ordinal()].getType(), null, AgeState.BABY, null, null);
				}
			}else{
				doreiTmp2 = doreiTmp;
			}
		}
		
		if ( rnd.nextBoolean() ){
			dorei = doreiTmp;
			dorei2 = doreiTmp2;
		}else{
			dorei = doreiTmp2;
			dorei2 = doreiTmp;
		}
		
		if ( rnd.nextBoolean() ){
			dorei3 = doreiTmp;
		}else{
			dorei3 = doreiTmp2;
		}
		
		if ( rnd.nextBoolean() ){
			dorei4 = doreiTmp;
		}else{
			dorei4 = doreiTmp2;
		}
		nameJ = dorei.getNameJ();
		nameE = dorei.getNameE();
		nameJ2 = dorei2.getNameJ();
		nameE2 = dorei2.getNameE();

		
		//////////////////////////////////needs sprite instructions or crashes!!!
		// left face
		images[ConstantValues.BODY] =		dorei;
		images[ConstantValues.BODYPINNED] =		dorei;
		images[ConstantValues.LICK] =		dorei2;
		images[ConstantValues.BRAID] =		dorei3;
		images[ConstantValues.BRAID_CUT] =	dorei3;
		images[ConstantValues.BRAID_MV0] =	dorei3;
		images[ConstantValues.BRAID_MV1] =	dorei3;
		images[ConstantValues.BRAID_MV2] =	dorei3;
		images[ConstantValues.ACCESSORY] =	dorei4;
		images[ConstantValues.DAMAGED0] =	dorei;
		images[ConstantValues.DAMAGED1] =	dorei;
		images[ConstantValues.DAMAGED2] =	dorei;
		images[ConstantValues.PANTS] =		dorei4;
		images[ConstantValues.STAIN] =		dorei;
		images[ConstantValues.SICK0] =		dorei;
		images[ConstantValues.SICK1] =		dorei;
		images[ConstantValues.SICK2] =		dorei;
		images[ConstantValues.NORMAL] =	dorei2;
		images[ConstantValues.SLEEPING] =	dorei2;
		images[ConstantValues.CHEER] =		dorei2;
		images[ConstantValues.SMILE] =		dorei2;
		images[ConstantValues.TIRED] =		dorei2;
		images[ConstantValues.PUFF] =		dorei2;
		images[ConstantValues.CRYING] =	dorei2;
		images[ConstantValues.EXCITING] =	dorei2;
		images[ConstantValues.REFRESHED] =	dorei2;
		images[ConstantValues.DEAD] =		dorei2;
		images[ConstantValues.RUDE] =		dorei2;
		images[ConstantValues.WET] =		dorei;
		images[ConstantValues.PAIN] =		dorei2;
		images[ConstantValues.SURPRISE] = 	dorei2;
		images[ConstantValues.FRONT_SHIT_SEALED] = dorei;
		images[ConstantValues.ROLL_SHIT_SEALED] = dorei;
		images[ConstantValues.BODY_CUT] =	dorei;
		images[ConstantValues.FOOT_BAKE0] = dorei;
		images[ConstantValues.FOOT_BAKE1] = dorei;
		// center
		images[ConstantValues.CRUSHED] =		dorei;			
		images[ConstantValues.CRUSHED2] =		dorei;			
		images[ConstantValues.FRONT_SHIT] =			dorei;
		images[ConstantValues.ROLL_ACCESSORY] =	dorei4;
		images[ConstantValues.ROLL_SHIT] =		dorei;
		images[ConstantValues.FRONT_PANTS] =		dorei4;
		images[ConstantValues.ROLL_PANTS] =	dorei4;
		images[ConstantValues.BURNED] =		dorei;
		images[ConstantValues.BURNED2] =		dorei;

		for(int i = 0; i < 3; i++) {
			images[ConstantValues.BODY].setAgeState(AgeState.values()[i]);
			boundary[i] = new Rectangle();
			boundary[i].width = images[ConstantValues.BODY].getW();
			boundary[i].height = images[ConstantValues.BODY].getH();
			boundary[i].x = boundary[i].width >> 1;
			boundary[i].y = boundary[i].height - 1;
			
			images[ConstantValues.BRAID].setAgeState(AgeState.values()[i]);
			braidBoundary[i] = new Dimension();
			braidBoundary[i].width = images[ConstantValues.BRAID].getBraidW();
			braidBoundary[i].height = images[ConstantValues.BRAID].getBraidH();
		}
		
		// 縺贋ｸ九￡譛臥┌
		if(dorei3.isBraidType()) {
			braidType = true;
		} else {
			braidType = false;
		}
		
		// 鬟幄｡檎憾諷句ｼ慕ｶ吶℃
		flyingType = dorei3.isFlyingType();
		// 謐暮｣溽ｨｮ蠑慕ｶ吶℃
		if(dorei.isPredatorType()) {
			if(flyingType) predatorType = PredatorType.SUCTION;
			else predatorType = PredatorType.BITE;
		} else {
			predatorType = null;
		}
	}

	public Image getImage(int type, int direction) {
		images[type].setAgeState(getBodyAgeState());
		return images[type].getImage(type, direction);
	}
	
	public Body getBaseBody(int idx) {
		if(idx == 0) return dorei;
		else if(idx == 1) return dorei2;
		else if(idx == 2) return dorei3;
		return dorei4;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public int getHybridType(int partnerType) {
		return HybridYukkuri.type;
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
		return nameJ2;
	}

	@Override
	public String getNameE2() {
		return nameE2;
	}

	@Override
	public boolean isHybrid () {
		return true;
	}
	
	// public methods
	public HybridYukkuri(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.HYBRIDYUKKURI);
		shitType = p1.getShitType();
	}
	
	public void tuneParameters() {
		if (rnd.nextBoolean()) {
			motherhood = true;
		}
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
		STRENGTH[AgeState.ADULT.ordinal()] *= factor;
		STRENGTH[AgeState.CHILD.ordinal()] *= factor;
		STRENGTH[AgeState.BABY.ordinal()] *= factor;
		
		getVALUEPURCHASE()[AgeState.ADULT.ordinal()] = 4*valuePurchase;
		getVALUEPURCHASE()[AgeState.CHILD.ordinal()] = 2*valuePurchase;
		getVALUEPURCHASE()[AgeState.BABY.ordinal()] = valuePurchase;
		getVALUESALE()[AgeState.ADULT.ordinal()] = 5*valueSale;
		getVALUESALE()[AgeState.CHILD.ordinal()] = 3*valueSale;
		getVALUESALE()[AgeState.BABY.ordinal()] = valueSale ;
		
		int stressFactor = rnd.nextInt(100) - 50;
		int StressMedian = 650;
		STRESSLIMIT[AgeState.ADULT.ordinal()] =StressMedian + stressFactor;
		STRESSLIMIT[AgeState.CHILD.ordinal()] =StressMedian + stressFactor - 150;
		STRESSLIMIT[AgeState.BABY.ordinal()] = StressMedian + stressFactor - 250;
		
		images = new Body[ConstantValues.NUM_OF_CONDITIONS];
		try {
			loadImages_Hyblid();
		} catch (IOException e1) {
			System.out.println("File I/O error");
		}
	}
}