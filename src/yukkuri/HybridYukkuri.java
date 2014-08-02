package src.yukkuri;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.*;
import src.Body.AgeState;
import src.YukkuriUtil.YukkuriType;


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
					doreiTmp = SimYukkuri.mypane.terrarium.makeBody(0, 0, 0, parents[Parent.MAMA.ordinal()].getType(), null, Body.AgeState.BABY, null, null);
				}
			}else{
				if ( parents[Parent.PAPA.ordinal()].getType() == 20000 ){
					parentTmp = (HybridYukkuri)parents[Parent.PAPA.ordinal()];
					doreiTmp = parentTmp.dorei;
				}else{
					doreiTmp = SimYukkuri.mypane.terrarium.makeBody(0, 0, 0, parents[Parent.PAPA.ordinal()].getType(), null, Body.AgeState.BABY, null, null);
				}
			}

			if ( parents[Parent.PAPA.ordinal()] != null ){
				if ( parents[Parent.PAPA.ordinal()].getType() == 20000 ){
					parentTmp2 = (HybridYukkuri)parents[Parent.PAPA.ordinal()];
					doreiTmp2 = parentTmp2.dorei;
				}else{
					doreiTmp2 = SimYukkuri.mypane.terrarium.makeBody(0, 0, 0, parents[Parent.PAPA.ordinal()].getType(), null, Body.AgeState.BABY, null, null);
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

		// left face
		images[BODY] =		dorei;
		images[LICK] =		dorei2;
		images[BRAID] =		dorei3;
		images[BRAID_CUT] =	dorei3;
		images[BRAID_MV0] =	dorei3;
		images[BRAID_MV1] =	dorei3;
		images[BRAID_MV2] =	dorei3;
		images[ACCESSORY] =	dorei4;
		images[DAMAGED0] =	dorei;
		images[DAMAGED1] =	dorei;
		images[DAMAGED2] =	dorei;
		images[PANTS] =		dorei4;
		images[STAIN] =		dorei;
		images[SICK0] =		dorei;
		images[SICK1] =		dorei;
		images[SICK2] =		dorei;
		images[NORMAL] =	dorei2;
		images[SLEEPING] =	dorei2;
		images[CHEER] =		dorei2;
		images[SMILE] =		dorei2;
		images[TIRED] =		dorei2;
		images[PUFF] =		dorei2;
		images[CRYING] =	dorei2;
		images[EXCITING] =	dorei2;
		images[REFRESHED] =	dorei2;
		images[DEAD] =		dorei2;
		images[RUDE] =		dorei2;
		images[WET] =		dorei;
		images[PAIN] =		dorei2;
		images[SURPRISE] = 	dorei2;
		images[FRONT_SHIT_SEALED] = dorei;
		images[ROLL_SHIT_SEALED] = dorei;
		images[BODY_CUT] =	dorei;
		images[FOOT_BAKE0] = dorei;
		images[FOOT_BAKE1] = dorei;
		// center
		images[CRUSHED] =		dorei;			
		images[CRUSHED2] =		dorei;			
		images[FRONT_SHIT] =			dorei;
		images[ROLL_ACCESSORY] =	dorei4;
		images[ROLL_SHIT] =		dorei;
		images[FRONT_PANTS] =		dorei4;
		images[ROLL_PANTS] =	dorei4;
		images[BURNED] =		dorei;
		images[BURNED2] =		dorei;

		for(int i = 0; i < 3; i++) {
			images[BODY].setAgeState(Body.AgeState.values()[i]);
			boundary[i] = new Rectangle();
			boundary[i].width = images[BODY].getW();
			boundary[i].height = images[BODY].getH();
			boundary[i].x = boundary[i].width >> 1;
			boundary[i].y = boundary[i].height - 1;
			
			images[BRAID].setAgeState(Body.AgeState.values()[i]);
			braidBoundary[i] = new Dimension();
			braidBoundary[i].width = images[BRAID].getBraidW();
			braidBoundary[i].height = images[BRAID].getBraidH();
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
			if(flyingType) predatorType = Body.PredatorType.SUCTION;
			else predatorType = Body.PredatorType.BITE;
		} else {
			predatorType = null;
		}
	}

	public Image getImage(int type, int direction) {
		images[type].setAgeState(bodyAgeState);
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
	public HybridYukkuri(int initX, int initY, int initZ, Body.AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		msgType = YukkuriType.HYBRIDYUKKURI;
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
		
		VALUEPURCHASE[AgeState.ADULT.ordinal()] = 4*valuePurchase;
		VALUEPURCHASE[AgeState.CHILD.ordinal()] = 2*valuePurchase;
		VALUEPURCHASE[AgeState.BABY.ordinal()] = valuePurchase;
		VALUESALE[AgeState.ADULT.ordinal()] = 5*valueSale;
		VALUESALE[AgeState.CHILD.ordinal()] = 3*valueSale;
		VALUESALE[AgeState.BABY.ordinal()] = valueSale ;
		
		int stressFactor = rnd.nextInt(100) - 50;
		int StressMedian = 650;
		STRESSLIMIT[AgeState.ADULT.ordinal()] =StressMedian + stressFactor;
		STRESSLIMIT[AgeState.CHILD.ordinal()] =StressMedian + stressFactor - 150;
		STRESSLIMIT[AgeState.BABY.ordinal()] = StressMedian + stressFactor - 250;
		
		images = new Body[NUM_OF_CONDITIONS];
		try {
			loadImages_Hyblid();
		} catch (IOException e1) {
			System.out.println("File I/O error");
		}
	}
}