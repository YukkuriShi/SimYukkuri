package src.yukkuriBody;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.Cash;
import src.Dna;
import src.EventPacket;
import src.MessagePool;
import src.SimYukkuri;
import src.Terrarium;
import src.TrashUtil;
import src.YukkuriUtil;
import src.EventPacket.EventPriority;
import src.MessagePool.Action;
import src.Terrarium.DayState;
import src.TrashUtil.OkazariType;
import src.YukkuriUtil.YukkuriType;
import src.attachment.CardboardBox;
import src.attachment.Fire;
import src.attachment.FakeBadge;
import src.attachment.BronzeBadge;
import src.attachment.SilverBadge;
import src.attachment.GoldBadge;
import src.event.BreedEvent;
import src.event.HateNoOkazariEvent;
import src.event.StealOkazariEvent;
import src.event.RaperReactionEvent;
import src.event.RaperWakeupEvent;
import src.event.RevengeAttackEvent;
import src.item.*;
import src.object.Attachment;
import src.object.Obj;
import src.object.ObjEX;
import src.object.Stalk;
import src.object.Vomit;
import src.object.Obj.Event;
import src.object.Obj.Type;
import src.system.Numbering;
import src.system.Translate;
import src.yukkuri.*;
import src.yukkuriBody.ConstantValues.*;
import src.yukkuriLogic.EventLogic;
import src.yukkuriLogic.ToiletLogic;








//import java.util.Timer;
import javax.swing.Timer;


public abstract class Body extends Obj implements java.io.Serializable {
	static final long serialVersionUID = 7L;

	public abstract int getType();
	public abstract String getNameJ ();
	public abstract String getNameE ();
	public abstract String getNameJ2 ();
	public abstract String getNameE2 ();
	public abstract Image getImage (int type, int direction);
	public abstract void tuneParameters();

	///////
	
	protected static Image[] shadowImages = new Image[3];
	protected static int[] shadowImgW = new int[3];
	protected static int[] shadowImgH = new int[3];
	protected static int[] shadowPivX = new int[3];
	protected static int[] shadowPivY = new int[3];
	protected int[] imgW = new int[3];
	protected int[] imgH = new int[3];
	protected int[] pivX = new int[3];
	protected int[] pivY = new int[3];
	protected int[] braidImgW = new int[3];
	protected int[] braidImgH = new int[3];

	// tunable parameters for each Yukkuri
	protected int EATAMOUNT[] = {100*6, 100*12, 100*24};		// 荳�屓縺ｮ鬟滉ｺ矩㍼
	protected int WEIGHT[] = {100, 300, 600};					// 菴馴㍾
	public  int HUNGRYLIMIT[] = {100*12, 100*18, 100*24}; // 遨ｺ閻ｹ髯千阜
	public int SHITLIMIT[] = {100*12, 100*24, 100*24};		// 縺�ｓ縺�ｓ髯千阜
	public  int DAMAGELIMIT[] = {100*24, 100*24*2, 100*24*4}; // 繝�Γ繝ｼ繧ｸ髯千阜
	protected int STRESSLIMIT[] = {1, 1, 1}; //
	//protected int STRESSLIMIT[] = {100*24, 100*24*3, 100*24*7}; // 繧ｹ繝医Ξ繧ｹ髯千阜   for reference
	protected int TANGLEVEL[] = {300, 600, 1000};				// 蜻ｳ隕壹Ξ繝吶Ν
	public  int BABYLIMIT = 100*24*7;
	public  int CHILDLIMIT = 100*24*21;
	protected int LIFELIMIT = 100*24*365;
	protected int STEP[] = {1, 2, 4};
	protected int RELAXPERIOD = 100*1;
	protected int EXCITEPERIOD = 100*3;
	protected int PREGPERIOD = 100*24;
	protected int SLEEPPERIOD = 100*3;
	protected int ACTIVEPERIOD = 100*6;
	protected int ANGRYPERIOD = 100*1;
	protected int SCAREPERIOD = 100*1;
	protected int sameDest = 30;
	protected int DECLINEPERIOD = 100*6*10; // 10 min.
	protected int DISCIPLINELIMIT = 10;
	protected int BLOCKEDLIMIT = 60;
	protected int DIRTYPERIOD = 250;
	protected int ROBUSTNESS = 1;
	public int EYESIGHT = Terrarium.MAX_X * Terrarium.MAX_Y;
	protected int STRENGTH[] = {100, 100*10, 100*30};
	protected int INCUBATIONPERIOD = 100*12;
	protected int TRASH_OKAZARI_OFS_X[] = {0,0,0};
	protected int TRASH_OKAZARI_OFS_Y[] = {0,0,0};
	////// Weather and Environment Stats
	
	protected int idealTemp = 60;
	protected int comfortTempRange = 35;  // 25-95
	protected int idealTempMax = idealTemp + comfortTempRange;
	protected int idealTempMin = idealTemp - comfortTempRange;
	
	private int VALUEPURCHASE[] = {2, 2, 2};   // These are set in src.yukkuri files for each yukkuri, this is just a placeholder.
	private int VALUESALE[] = {1, 1, 1};
	
	// individual state variables for each Yukkuri.
	protected int destX = -1, destY = -1, destZ = -1; // 遘ｻ蜍募�逶ｮ讓�destination
	protected int countX = 0, countY = 0, countZ = 0; // 遘ｻ蜍暮㍼ how many steps to same direction
	protected int dirX = 0, dirY = 0, dirZ = 0;		// 遘ｻ蜍墓婿蜷�direction to move on
	protected Direction direction = Direction.RIGHT;// 鬘斐�蜷代″ direction of face
	protected AgeState ageState;
	private AgeState bodyAgeState;				// 霄ｫ菴灘ｹｴ鮨｢ BABY, CHILD, ADULT
	private AgeState mindAgeState;				// 邊ｾ逾槫ｹｴ鮨｢ BABY, CHILD, ADULT
	protected int damage = 0;						// 闢�ｩ阪ム繝｡繝ｼ繧ｸ counter indicating damage
	protected int stress = 0;						// 闢�ｩ阪せ繝医Ξ繧ｹ
	private int tang = 500;						// 闊後�閧･縺�
	protected Damage damageState = Damage.NONE;		// 繝�Γ繝ｼ繧ｸ螟冶ｦｳ
	protected int hungry = 0;						// 遨ｺ閻ｹ蠎ｦ counter indicating how hungry
	protected Hunger hungryState = Hunger.NONE;		// 遨ｺ閻ｹ螟冶ｦｳ
	private Attitude attitude = Attitude.AVERAGE;	// 諤ｧ譬ｼ counter indicating shithead/nicehead etc.
	private Intelligence intelligence = Intelligence.AVERAGE;	// 遏･諤ｧ
	protected Happiness happiness = Happiness.AVERAGE; // 蟷ｸ遖丞ｺｦ
	private int shit = 0;
	public OkazariType okazariType = OkazariType.DEFAULT;	// 縺翫°縺悶ｊ遞ｮ蛻･
	protected boolean hasBraid = true;				// 縺翫＆縺偵�鄒ｽ縲∝ｰｻ蟆ｾ譛臥┌ 遞ｮ譌上→縺励※菴輔ｂ謖√▲縺ｦ縺�↑縺�ｂ縺ｮ縺ｯtrue
	private boolean hasPants = false;				// 縺翫￥繧九∩譛臥┌ true if having pants
	protected boolean hasBaby = false;				// 閭守函螯雁ｨ�怏辟｡ having baby or not
	protected boolean hasStalk = false;				// 闌主ｦ雁ｨ�怏辟｡ having baby or not
	protected boolean analClose = false;		// 閭守函蜴ｻ蜍｢譛臥┌
	protected boolean bodyCastration = false;		// 閭守函蜴ｻ蜍｢譛臥┌
	protected boolean stalkCastration = false;		// 闌主悉蜍｢譛臥┌
	protected ArrayList<Dna> babyTypes = new ArrayList<Dna>();
	protected ArrayList<Dna> stalkBabyTypes = new ArrayList<Dna>();
	protected ArrayList<Stalk> stalks = new ArrayList<Stalk>();
	protected Stalk bindStalk = null;				// 閾ｪ蛻�′縺ｶ繧峨＆縺後▲縺ｦ縺�ｋ闌�
	protected boolean dead = false;					// 豁ｻ莠｡繝輔Λ繧ｰdead or alive
	protected boolean crashed = false;				// 豁ｻ菴捺錐螢翫ヵ繝ｩ繧ｰ crushed
	protected boolean burned = false;				// 豁ｻ菴鍋┥謳阪ヵ繝ｩ繧ｰ 
	protected boolean exciting = false;				// 逋ｺ諠�ヵ繝ｩ繧ｰ want to sukkiri or not 
	protected boolean relax = false;				// 繧�▲縺上ｊ縺励※繧九ヵ繝ｩ繧ｰ
	protected boolean sleeping = false;				// 逹｡逵�ｸｭ繝輔Λ繧ｰ
	protected long wakeUpTime;
	protected boolean dirty = false;				// 豎壹ｌ譛臥┌
	protected boolean sick = false;					// 繧�き繝捺怏辟｡ mold
	protected boolean motherhood = false;			// 豈肴�譛臥┌ 
	private boolean rapist = false;				// 繝ｬ繧､繝代�蛹匁怏辟｡
	private boolean superRapist = false;
	protected boolean wet = false;					// 豼｡繧檎憾諷�
	protected PanicType panicType = null;			// 繝代ル繝�け遞ｮ蛻･
	private CriticalDamegeType criticalDamege = null;		// 閾ｴ蜻ｽ蛯ｷ遞ｮ蛻･
	private Body partner = null;
	protected Body parents[] = {null, null};
	protected boolean fatherRaper = false;			// 閾ｪ蛻�′繝ｬ繧､繝励〒縺ｧ縺阪◆蟄舌°
	protected int shittingDiscipline = 0;
	protected int excitingDiscipline = 0;
	protected int furifuriDiscipline = 0;
	protected int messageDiscipline = 0;
	private ArrayList<Attachment> attach = new ArrayList<Attachment>();
	protected Obj linkParent = null;
	// 遞ｮ譌丞挨縺ｮ迚ｹ谿願�蜉�
	private boolean likeBitterFood = false;	// 闍ｦ縺�∴縺輔′螂ｽ縺阪°
	private boolean likeHotFood = false;		// 霎帙＞縺医＆縺悟･ｽ縺阪°
	protected boolean likeWater = false;		// 豌ｴ縺悟ｹｳ豌励°
	protected boolean flyingType = false;		// 遨ｺ繧帝｣帙�縺�
	protected boolean braidType = false;		// 縺贋ｸ九￡縲∫ｾｽ縲∝ｰｻ蟆ｾ繧呈戟縺､縺�
	protected PredatorType predatorType = null;	// 謐暮｣溽ｨｮ縺�

	protected boolean lockmove = false;				// 蜍輔￠縺ｪ縺�
	protected boolean pullAndPush = false;			// 縺ｲ縺｣縺ｱ繧翫�謚ｼ縺励▽縺ｶ縺怜庄閭ｽ縺�
	protected int lockmovePeriod = 0;
	protected int extForce = 0;					// 螟門悸

	//counter controlling for period
	protected int noDamagePeriod = 0;
	protected int noHungryPeriod = 0;
	protected int pregnantPeriod = 0;
	protected int excitingPeriod = 0;
	protected int sleepingPeriod = 0;
	private int dirtyPeriod = 0;
	protected int sickPeriod = 0;
	protected int angryPeriod = 0;
	protected int scarePeriod = 0;
	protected int sadPeriod = 0;
	protected int wetPeriod = 0;
	protected int panicPeriod = 0;
	protected int footBakePeriod = 0;
	
	protected int pregnantPeriodBoost = 0;
	protected int excitingPeriodBoost = 0;
	protected int shitBoost = 0;

	// 遘ｻ蜍�
	protected Obj moveTarget = null;
	public int targetPosOfsX = 0;
	public int targetPosOfsY = 0;
	protected boolean targetBind = false;
	private boolean toFood = false;
	protected boolean toSukkiri = false;
	private boolean toShit = false;
	private boolean toBed = false;
	protected boolean toBody = false;
	// 繧｢繧ｯ繧ｷ繝ｧ繝ｳ
	protected boolean staying = false;
	protected boolean shitting = false;
	protected boolean birth = false;
	protected boolean angry = false;
	protected boolean furifuri = false;
	private boolean strike = false;
	private boolean eating = false;
	protected boolean peropero = false;
	protected boolean sukkiri = false;
	protected boolean scare = false;
	private boolean eatingShit = false;
	protected boolean silent = false;
	protected boolean nobinobi = false;
	protected boolean pikopiko = false;
	
		//////////// Wetstate Handling
	protected int wetTimer = 0;
	protected int dryTimer = 0;
	protected int wetCount = 0;
	public static int WETLIMIT[] = {100, 175, 400};
	protected int meltTimer = 0;
	protected int meltState = 0;
	protected int COOKLIMIT = 75;			//total length of time to use burn01.png before switching to burn.png, 10 ticks per second, so 7.5 seconds
	protected int cookTimer = 0;
	
	///////////////////////////////////////////   TODO organize by tool
	
	protected boolean pinned = false;
	protected boolean pinnedDontMove = false;
	
	protected boolean burning = false; //
	protected boolean burnedDontMove = false;  // burned feet effect
	protected boolean burnedFeet = false; //burned feet
	protected boolean onFire = false;            //currently on fire
	protected boolean cooked = false;

	protected int burnTimer = 0;
	protected int BURNLIMIT[] = {80, 120, 150};
	
	protected boolean steamy = false;
	protected int steamTimer = 0;
	protected int STEAMLIMIT = 25;
	

	
	// for water pools and any other water sources added in the future
	protected boolean wetCurrently = false;		// is the yukkuri in water right now
	protected boolean soaked = false;				// the yukkuri is already soaked
	protected boolean melting = false;			// the yukkuri has begun to melt
	protected boolean dissolving = false;		// the yukkuri has died and will dissolve to death.
	protected boolean hasMelted = false;
	
	

	///// Complacency system
	public enum Complacency  { REBELLIOUS, IRRITATED, UNSURE, COMPLACENT, CONTENT}; //TODO add modifiers to activities
	private int complacencyVal = 1200;
	protected Complacency complacency;

	protected int complacencyMax = 2000;
	protected boolean complacencyInitial = false;
	
	protected boolean reduceComplacency = false;
	public int reduceComplacencyTimer = 0;
	
	protected boolean increaseComplacency = false;
	protected int increaseComplacencyTimer = 0;
	
	protected boolean isSuppressed = false;  //complacency cannot be reduced
	protected int suppressionTimer = 0; //timer for how long the yukkuri complacency cannot be reduced
	
	protected boolean isPraiseLimited = false; //anti-spam function
	protected int praiseLimitTimer = 0; //anti-spam function
	protected int praiseUsed = 0; //praise has diminishing returns
	protected int lowerPraiseUsedTimer = 0; //reduced praisedused by 1 every 550 ticks
	
	protected int moveTowardsAttitudeTimer = 0;  //slowly shifts complacency towards attitude, so shithead yukkuri are never satisfied, and niceheads are more patient.
	
	protected int shockTimer = 0; //TODO
	
	//Poo-poo expansion
	//added 02.08.14 - kirisame
	public static final int INFINITE = 999999;
	protected int previousShitDistance = INFINITE;
	protected int shitStress = 0;
	protected boolean shitPanicEscape = false;
	private boolean cleanFood = true;
	
	///// Stress System
	protected boolean increaseStress = false;
	protected int increaseStressTimer = 0;
	
	protected boolean reduceStress = false;
	protected int reduceStressTimer = 0;
	
	protected boolean stressLocked = false;
	
	///////
	public boolean isSold = false;

	///// Badge System
	
/*Yukkuri can have one of five different badge states, but should only ever have a single badge at a time (TODO)
 * To keep things stupid simple, the process by which a yukkuri gains a badge is split into two methods, the first one firing off when clicked and triggering the second one,
 * which then occurs once per tick.  The yukkuri performs a series of actions, with a chance of failure based on their attitude, complacency, intelligence and a bit of randomness.
 * Each successful action contributes to a running score.  If this score is higher than the score required by the badge, then the badge is awarded.
 * 
 * The boolean isBusy acts to prevent any actions from interrupting the test, maybe remove the checkDamage method from it, maybe not.  Will wait till the system is finished to do that.
 * 
 */
	public enum BadgeState { NONE, FAKE, BRONZE, SILVER, GOLD};
	protected BadgeState badgeState = BadgeState.NONE;
	
	protected int testBadge = 0;
	
	protected int localCounter = 0;
	protected int localStep = 0;
	
	protected boolean isBusy = false;

	// variables related to actions.
	protected String messageBuf;	
	protected int messageCount = 0;
	protected int staycount = 0;
	protected int stayTime = ConstantValues.STAYLIMIT;
	protected int falldownDamage = 0;
	protected int bodyAmount = 0;
	protected int blockedCount = 0;

	protected Random rnd = new Random();

	protected int value[] = {10,100,300};
	protected int cantDiePeriod = 0;
	protected boolean unBirth = false;
	protected boolean canTalk = true;
	protected Color messageLineColor;
	protected Color messageBoxColor;
	protected Color messageTextColor;
	protected float messageWindowLine;
	protected int messageTextSize;
	
	protected int uniqueID = 0;
	private YukkuriType msgType = null;
	protected YukkuriType shitType = null;
	private boolean pin = false;
	protected int speed = 100;
	
	//MISC
	protected boolean isBlind = false;

	// 繧､繝吶Φ繝�
	private ArrayList<EventPacket> eventList = new ArrayList<EventPacket>(); // 縺薙�蛟倶ｽ薙↓蟇ｾ縺励※逋ｺ陦後＆繧後◆繧､繝吶Φ繝医�繝ｪ繧ｹ繝�
	protected EventPacket currentEvent = null;	// 迴ｾ蝨ｨ螳溯｡御ｸｭ縺ｮ繧､繝吶Φ繝�
	protected int forceFace = -1;				// 陦ｨ諠��蠑ｷ蛻ｶ險ｭ螳�
	protected boolean dropShadow = true;		// 蠖ｱ縺ｮ陦ｨ遉ｺ譛臥┌
	
	// 縺頑ｰ励↓蜈･繧�
	protected HashMap<FavItemType, Obj> favItem = new HashMap<ConstantValues.FavItemType, Obj>();

	// private methods
		
	private void checkPinned()
	{
		if (pinned)
		{
			//dontMove = true;
			pinnedDontMove = true;
			return;
			
		}
		pinnedDontMove = false;
		return;
	}
	
	private boolean isPinned()
	{
		if (pinned){
			return true;
		}
		return false;
	}
	
	private AgeState checkBodyAgeState()
	{
		if (getAge() < BABYLIMIT) {
			return AgeState.BABY;
		}
		else if (getAge() < CHILDLIMIT) {
			return AgeState.CHILD;
		}
		return AgeState.ADULT;
	}

	private AgeState checkMindAgeState()
	{
		if (getAge() < BABYLIMIT) {
			return AgeState.BABY;
		}
		else if (getAge() < CHILDLIMIT) {
			return AgeState.CHILD;
		}
		return AgeState.ADULT;
	}

	private Hunger checkHungryState()
	{
		if (hungry < HUNGRYLIMIT[getBodyAgeState().ordinal()]/4) {
			return Hunger.NONE;
		}
		return Hunger.VERY;
	}

	private Damage checkDamageState()
	{
		if (damage > DAMAGELIMIT[getBodyAgeState().ordinal()]) {
			toDead();
			return Damage.VERY;
		}
		if (damage >= DAMAGELIMIT[getBodyAgeState().ordinal()]/2) {
			return Damage.VERY;
		}
		return Damage.NONE;
	}
	
	private Pain checkPainState()
	{
		if ( checkBurstState() == Burst.NEAR || checkBurstState() == Burst.BURST ){
			return Pain.VERY;
		}
		if ( checkBurstState() == Burst.HALF  || isPinned()  || burnedFeet){
			return Pain.SOME;
		}
		return Pain.NONE;
	}

	private Burst checkBurstState()
	{
		if ( getSize() * 4 / getOriginSize() >= 8 ){
			return Burst.BURST;
		}
		if ( getSize() * 4 / getOriginSize() >= 7 ){
			return Burst.NEAR;
		}
		if ( getSize() * 4 / getOriginSize() >= 6 ){
			return Burst.HALF;
		}
		if ( getSize() * 4 / getOriginSize() >= 5 ){
			return Burst.SAFE;
		}
		return Burst.NONE;
	}

	private void checkHungry() {
	
		if (unBirth == true && ((bindStalk != null) ? bindStalk.getPlantYukkuri() == null:true) && !isReimu()) {
			hungry += TICK * 100;
		}else if (unBirth == true && ((bindStalk != null) ? bindStalk.getPlantYukkuri() == null:true) && !isReimu()) {
				hungry += TICK * 50;
		} else if (exciting && !isRaper()) {
			hungry += TICK * (babyTypes.size() + (exciting ? 1 : 0));
		} else {
			hungry += TICK;
		}
		
		if ( hasStalk() ){
			hungry += TICK * stalks.size() * 5;
		}
		
		if (hungry > HUNGRYLIMIT[getBodyAgeState().ordinal()]) {
			damage += (hungry - HUNGRYLIMIT[getBodyAgeState().ordinal()]);
			damage +=2*TICK;
			hungry = HUNGRYLIMIT[getBodyAgeState().ordinal()];
		}
		if (hungryState == Hunger.NONE && checkHungryState() == Hunger.NONE) {
			noHungryPeriod += TICK;
		} else {
			noHungryPeriod = 0;
		}
		hungryState = checkHungryState();
	}

	private void checkDamage() {
		if (isSick()) {
			damage += TICK;
		}
		else if (checkHungryState() == Hunger.NONE) {
			damage -= TICK;
		}
		else if (hungry >= HUNGRYLIMIT[getBodyAgeState().ordinal()]) {
			damage += TICK;
		}
		if(getCriticalDamege() == CriticalDamegeType.CUT) {
			damage += TICK*100;
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying2), false);
		}
		if ( Terrarium.orangeSteam ){
			damage -= TICK*50;
		}
		if ( Terrarium.poisonSteam ){
			damage += TICK*100;
			clearActions();
			exciting = false;
			shitting = false;
			furifuri = false;
			wakeup();
			setHappiness(Happiness.VERY_SAD);
			if ( damageState == Damage.VERY ) {
				setNegiMessage(MessagePool.getMessage(this, MessagePool.Action.PoisonDamage), true);
			} else {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.PoisonDamage), ConstantValues.HOLDMESSAGE, false, true);
			}
		}
		if (damage < 0) {
			damage = 0;
		}
		Damage newDamageState = checkDamageState();
		if (damageState == Damage.NONE && newDamageState == Damage.NONE) {
			noDamagePeriod += TICK;
		}
		else {
			noDamagePeriod = 0;
		}
		damageState = newDamageState;
		
		if(damageState == Damage.VERY && currentEvent != null && currentEvent.getPriority() != EventPacket.EventPriority.HIGH) {
			clearEvent();
		}
	}

	private boolean checkShit() {
		if ( unBirth ){
			return true;
		}
		// 螳悟�雜ｳ辟ｼ縺阪＠縺溘ｉ縺�ｓ縺�ｓ縺励↑縺�
		if(getFootBakeLevel() == FootBake.CRITICAL) return false;
		// 繝ｬ繧､繝代�逋ｺ諠�ｸｭ縺ｯ縺�ｓ縺�ｓ辟｡蜉ｹ
		if(isRaper() && isExciting()) return false;
		if(isEating() || peropero || sukkiri) return false;

		// 螳滄ｨ�繧､繝吶Φ繝井ｸｭ縺ｯ遨ｺ閻ｹ縲∫擅逵��萓ｿ諢上′蠅励∴縺ｪ縺�ｈ縺�↓
		if(currentEvent != null) return false;

		boolean cantMove = false;
		// 縺�ｓ縺�ｓ闢�ｩ�
		if (checkHungryState() == Hunger.NONE) {
			setShit(getShit() + TICK*2 + ( shitBoost*20 ));
		}
		else {
			setShit(getShit() + TICK + ( shitBoost*20 ));
		}

		if(currentEvent != null && currentEvent.getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}
		
		// 縺｡縺弱ｌ迥ｶ諷九�蝣ｴ蜷医�鬢｡蟄舌ｒ貍上ｉ縺�
		if(getCriticalDamege() == CriticalDamegeType.CUT) {
			if (getShit() > SHITLIMIT[getBodyAgeState().ordinal()] - TICK*ConstantValues.SHITSTAY*2) {
				SimYukkuri.mypane.terrarium.addCrushedVomit(getX() + 3 - rnd.nextInt(6), getY() - 2, 0, getAgeState(), shitType);
				setShit(1);
				return true;
			}
		}

		// 繧ゅ＠繝医う繝ｬ縺ｫ蛻ｰ逹�＠縺ｦ縺�◆繧牙叉謗呈ｳ�∈
		if(isToShit() && moveTarget instanceof Toilet) {
			if ((90 > Translate.distance(x, y, moveTarget.getX(), moveTarget.getY()))) {
//				if(shit < SHITLIMIT[bodyAgeState.ordinal()] - TICK*SHITSTAY) {
//					shit = SHITLIMIT[bodyAgeState.ordinal()] - TICK*SHITSTAY + 1;
//				}
			}
			else if((getAttitude() == Attitude.NICE || getAttitude() == Attitude.VERY_NICE)
					|| (getAttitude() == Attitude.AVERAGE && getIntelligence() == Intelligence.WISE)) {
				// 諤ｧ譬ｼ縺悟埋濶ｯ縺区勸騾壹〒繧ら衍閭ｽ縺碁ｫ倥￠繧後�繝医う繝ｬ縺ｫ逹�￥縺ｾ縺ｧ150%縺ｾ縺ｧ謌第�縺ｧ縺阪ｋ
				if(getShit() < (SHITLIMIT[getBodyAgeState().ordinal()] * 1.5f)) {
					shitting = false;
					cantMove = false;
					return cantMove;
				}
			}
		}

		// 髯千阜縺瑚ｿ代▼縺�◆繧画賜豕�メ繧ｧ繝�け
		if (getShit() > SHITLIMIT[getBodyAgeState().ordinal()] - TICK*ConstantValues.SHITSTAY && !isBusy) {
			// 邊倡捩蠎翫↓縺､縺�※縺溘ｉ菴灘兇繧偵°縺医ｉ繧後★縺ｫ貍上ｉ縺�
			if (lockmove) {
				setDirty(true);
				setHappiness(Happiness.VERY_SAD);
				addStress(200);
				setShit(1);
				return true;
			}
			
			// 謗呈ｳ�ｺ門ｙ
			if (isHasPants()) {
				setHappiness(Happiness.SAD);
			}
			if(!shitting && (!isBusy)) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Shit), TICK*ConstantValues.SHITSTAY);
				wakeup();
				shitting = true;
				cantMove = true;
				return cantMove;
			}
			cantMove = true;
		}
		else {
			// While shitting is true, the yukkuri might grow up. So, these flags should be clear.
			shitting = false;
			cantMove = false;
		}
		// 髯千阜繧定ｶ�∴縺溷�蜷医�繝√ぉ繝�け
		if ( getShit() > SHITLIMIT[getBodyAgeState().ordinal()] ) {
			// 閧幃摩縺悟｡槭′繧後※縺ｪ縺代ｌ縺ｰ謗呈ｳ�
			if (!analClose) {
				shitting = false;
				clearActions();
				setShit(0);
				if (getBodyAgeState() == AgeState.BABY) {
					setDirty(true);
					setHappiness(Happiness.SAD);
					reduceComplacencyTimer += 40;
					addStress(300);
				}
				if (isHasPants()) {
					setDirty(true);
					setHappiness(Happiness.VERY_SAD);
					reduceComplacencyTimer += 50;
					addStress(500);
				}

				setMessage(MessagePool.getMessage(this, MessagePool.Action.Shit2));
				if (!isHasPants()) {
					if (canFurifuri()) {
						furifuri = true;
					}
					stay();
					addStress(-200);
				}

				if (shitBoost > 0 ){
					shitBoost--;
				}
			} else {
				// 蝪槭′縺｣縺ｦ縺溘ｉ閹ｨ繧峨ｓ縺ｧ遐ｴ陬�
				if ( checkBurstState() == Burst.NEAR ){
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Inflation));
				}else{
					setMessage(MessagePool.getMessage(this, MessagePool.Action.CantShit));
				}
				if(rnd.nextInt(4) == 0) shitBoost++;
				addStress(10);
			}
		}
		return cantMove;
	}

	private boolean checkChildbirth() {
		boolean cantMove = false;
		if ( hasBabyOrStalk() || !hasBabyOrStalk() && birth) {
			pregnantPeriod += TICK + (pregnantPeriodBoost / 2);
			if (pregnantPeriod > PREGPERIOD - TICK*100) {
				if (!birth) {
					if ( hasBabyOrStalk() ){
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Breed));
					}
					wakeup();
				}
				cantMove = true;
				birth = true;
				pregnantPeriodBoost = 0;
			}
			if (pregnantPeriod > PREGPERIOD) {
				// Keep babyType for generating baby.
				hasBaby = false;
				hasStalk = false;
				if ( babyTypes.size() <= 0 ){
					birth = false;
					pregnantPeriod = 0;
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Breed2));
					if (!isHasPants()) {
						if (canFurifuri()) {
							furifuri = true;
						}
						stay();
					}
				}else{
					cantMove = true;
				}
				if ((isHasPants() || (lockmove && !shitting)) && babyTypes.size() > 0) {// 蜍輔￠縺ｪ縺�ｿｽ蜉�
					// 縺願�縺ｮ襍､繧�□縺代け繝ｪ繧｢
					babyTypes.clear();
					setDirty(true);
					if(lockmove && !isHasPants()){
						setHasPants(true);
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Breed2));
						setHasPants(false);
					}else{
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Breed2));
					}
					setHappiness(Happiness.VERY_SAD);
				}
			}
		}
		return cantMove;
	}

	private boolean checkSleep() {
		if (!isBusy() && sleeping || ( wakeUpTime + ACTIVEPERIOD*12/10 < getAge() && !exciting && relax && !scare && !isVerySad()) || unBirth) {
			clearActions();
			sleeping = true;
			angry = false;
			scare = false;
			stressLocked = true;
			damage -= TICK;
			if ( !unBirth ){
				setHappiness(Happiness.AVERAGE);
			}else{
				return sleeping;
			}
			if ( Terrarium.getDayState() == Terrarium.DayState.NIGHT ){
				if ( ( getAge() % ( Terrarium.nightTime / SLEEPPERIOD + 1 ) ) == 0 ) {
					sleepingPeriod += TICK;
				}
			}else{
				sleepingPeriod += TICK;
			}
			if ((sleepingPeriod > SLEEPPERIOD) || (!isRude() && wantToShit())) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Wakeup));
				stay();
				wakeup();
				stressLocked = false;
			}
		} else {
			// 螳滄ｨ�繧､繝吶Φ繝井ｸｭ縺ｯ遨ｺ閻ｹ縲∫擅逵��萓ｿ諢上′蠅励∴縺ｪ縺�ｈ縺�↓
			if(currentEvent != null) return false;
			stressLocked = false;
			sleepingPeriod = 0;
			sleeping = false;
			if ( Terrarium.getDayState() == Terrarium.DayState.NIGHT ) {
				wakeUpTime -= TICK * 3;
			}
		}
		
		// 鬟幄｡檎ｨｮ縺ｧ逵�￥縺ｪ縺｣縺溘ｉ蝨ｰ髱｢縺ｫ髯阪ｊ繧�
		if(canflyCheck() && isSleepy()) {
			moveToZ(0);
		}
		return sleeping;
	}

	private void checkCantDie() {
		if ( cantDiePeriod > 0 ){
			cantDiePeriod -= TICK;
		}
	}
	
	private void checkDiscipline() {
		int period = (isRude() ? 1 : 2) * DECLINEPERIOD;
		if (getAge() % period == 0) {
			if (--shittingDiscipline < 0) {
				shittingDiscipline = 0;
			}
			if (--excitingDiscipline < 0) {
				excitingDiscipline = 0;
			}
			if (--furifuriDiscipline < 0) {
				furifuriDiscipline = 0;
			}
			if (--messageDiscipline < 0) {
				messageDiscipline = 0;
			}
		}
	}
	
	private boolean canFurifuri() {
		 if (isRude() && (rnd.nextInt(furifuriDiscipline+1) == 0)) {
			 return true;
		 }
		 return false;
	}
	
	
	
	public int getFurifuriDiscipline() {
		return furifuriDiscipline;
	}

	public void wakeup() {
		if ( unBirth ) {
			return;
		}
		sleepingPeriod = 0;
		sleeping = false;
		wakeUpTime = getAge();
	}

	private void checkEmotion() {
		// 諤偵ｊ迥ｶ諷九�邨碁℃
		if (angry) {
			angryPeriod += TICK;
			if (angryPeriod > ANGRYPERIOD) {
				angryPeriod = 0;
				angry = false;
			}
		}
		// 諱先�迥ｶ諷九�邨碁℃
		if (scare) {
			scarePeriod += TICK;
			if (scarePeriod > SCAREPERIOD) {
				scarePeriod = 0;
				scare = false;
			}
		}
		// 關ｽ縺｡霎ｼ縺ｿ迥ｶ諷九�邨碁℃
		if (happiness == Happiness.VERY_SAD) {
			sadPeriod--;
			if(sadPeriod < 0) {
				sadPeriod = 0;
				happiness = Happiness.SAD;
			}
		}
		// 邊倡捩邉ｻ繧ｪ繝悶ず繧ｧ繧ｯ繝医�雋ｼ繧贋ｻ倥￠迥ｶ諷�
		if(lockmove && !sukkiri){// 霑ｽ蜉�虚縺代↑縺�
			lockmovePeriod++;
			if(!sleeping && !grabbed && !isTalking()){
				if(lockmovePeriod <300){
					if(rnd.nextInt(15)==0){
						clearActions();
						setAngry();
						setMessage(MessagePool.getMessage(this, MessagePool.Action.CantMove), false);
					}
				}
				else{
					if(rnd.nextInt(15)==0){
						clearActions();
						setAngry();
						setHappiness(Happiness.VERY_SAD);
						setMessage(MessagePool.getMessage(this, MessagePool.Action.CantMove2), false);
					}
				}
			}
			else{
				lockmovePeriod = 0;
			}
			return;
		}
		
		if(currentEvent != null) return;
		
		if(getCriticalDamege() == CriticalDamegeType.CUT) return;
		
		// 莉｣譖ｿ縺翫°縺悶ｊ縺ｮ謐懃ｴ｢
		if(TrashUtil.checkTrashOkazari(this)) return;
		
		if (noHungryPeriod > RELAXPERIOD && noDamagePeriod > RELAXPERIOD
				&& !sleeping && !shitting && !isEating()
				&& !isSad() && !isVerySad() && !isSick() && !isFeelPain()
				&& moveTarget == null) {
			if (!exciting && !relax) {
				int r = 1;
				int adjust = excitingDiscipline*(isRude() ? 1 : 2);
				if (isSuperRapist()) {
					r = rnd.nextInt(1 + adjust);
				}else if (isRapist() && isRude()) {
					r = rnd.nextInt(6 + adjust);
				}
				else if (isRapist() || isRude()) {
					r = rnd.nextInt(12 + adjust);
				}
				else if (!isHungry() && !wantToShit()) {
					r = rnd.nextInt(24 + adjust);
				}
				if (((getBodyAgeState() == AgeState.ADULT && !hasBabyOrStalk() ) || isRapist()) && r == 0) {
					exciting = true;
					excitingPeriod = 0;
					if(isRaper()) {
						EventLogic.addWorldEvent(new RaperWakeupEvent(this, null, null, 1), this, MessagePool.getMessage(this, MessagePool.Action.Excite));
					} else {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Excite));
					}
				}
				else {
					relax = true;
					excitingPeriod = 0;
					if (rnd.nextInt(3) == 0){
						// if yukkuri is not rude, she goes into her shell by discipline.
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Nobinobi), 40);
						nobinobi = true;
						addStress(-50);
						stay(40);
					}else if (isRude() && canFurifuri()) {
						//if yukkuri is rude, she will not do furifuri by discipline.
						setMessage(MessagePool.getMessage(this, MessagePool.Action.FuriFuri), 30);
						furifuri = true;
						addStress(-50);
						stay(30);
					}
					else if (isRude() || (rnd.nextInt(messageDiscipline+1) == 0)){
						// if yukkuri is not rude, she goes into her shell by discipline.
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Relax), 30);
						addStress(-50);
						stay(30);
					}
				}
				angry = false;
				scare = false;
			} else {
				if(!isRaper()) {
					excitingPeriod += TICK + excitingPeriodBoost;
					if (excitingPeriod > EXCITEPERIOD) {
						excitingPeriod = 0;
						exciting = false;
						relax = false;
					}
				}
			}
		}
	}

	private void checkSick() {
		if ( ( ( dirty && damage > ( DAMAGELIMIT[getBodyAgeState().ordinal()])/2 ) || ( Terrarium.humid && damage > 0 )) && !Terrarium.antifungalSteam ) {
			if (Terrarium.humid){
				setDirtyPeriod(getDirtyPeriod() + TICK*4);
			}else{
				setDirtyPeriod(getDirtyPeriod() + TICK);
			}
			if(wet) {
				setDirtyPeriod(getDirtyPeriod() + TICK);
			}
			if (getDirtyPeriod() > DIRTYPERIOD) {
				setSick();
				setDirtyPeriod(0);
			}
		}
		else {
			setDirtyPeriod(0);
		}
		if (sick) {
			if (Terrarium.humid){
				sickPeriod += 4;
			}else{
				sickPeriod++;
			}
			addStress(TICK);
			if(currentEvent != null && currentEvent.getPriority() != EventPacket.EventPriority.HIGH) {
				clearEvent();
			}
		}
	}

	// 縺溘□縺ｮ繝代ル繝�け豎守畑
	private Event checkFear() {
		if(!dead) {
			messageCount--;
			if(messageCount == 0) {
				switch(panicType) {
					case FEAR:
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Fear));
						break;
					case REMIRYA:
						setMessage(MessagePool.getMessage(this, MessagePool.Action.EscapeFromRemirya));
						break;
					default:
						break;
				}
			}
		}
		panicPeriod += TICK;
		if(panicPeriod > 50) {
			setPanic(false, null);
		}

		return Event.DONOTHING;
	}

	private void checkWet() {
		
		if(!wet) return;

		wetPeriod += TICK;
		if(wetPeriod > 200) {
			wet = false;
			wetPeriod = 0;
		}
		if(!likeWater) {
			if(getAgeState() == AgeState.BABY) {
				damage += TICK * 10;
				addStress(20);
				checkDamageState();
				checkDamage();
			}
		}
	}
	
	public void checkStress() {
		if (increaseStressTimer > 0 && !stressLocked)
		{
			increaseStressTimer -= 1;
			stress += 1;
		}
		if (reduceStressTimer > 0 && !stressLocked)
		{
			reduceStressTimer -=1;
			stress -= 1;
		}
		
		if(stress < 0) stress = 0;
	}
	
	public void shiftComplacencyTo(int targetVal)
	{
		if (getComplacencyVal() > targetVal){
			reduceComplacencyTimer +=10;
		}
		else if (getComplacencyVal() < targetVal){
			increaseComplacencyTimer+=10;
		}
		return;
	}
	
	public void checkComplacency(){
		isSuppressed = false;      //reset suppression
		moveTowardsAttitudeTimer += 1;
		lowerPraiseUsedTimer += 1;
		if (lowerPraiseUsedTimer > 550 )
		{
			lowerPraiseUsedTimer = 0;
			if (praiseUsed > 0)
			{
			praiseUsed -= 1;
			}
		}
		
		if (moveTowardsAttitudeTimer == 150)
		{
			moveTowardsAttitudeTimer = 0;
			int targetVal =0;
		    if (getComplacencyVal() < 400) {
		    	targetVal = 200+(rnd.nextInt(100)-50);
		    	shiftComplacencyTo(targetVal);
		    }
		    else if (getComplacencyVal() > 399 && getComplacencyVal() < 750) {
		    	targetVal = 575+(rnd.nextInt(100)-50);
		    	shiftComplacencyTo(targetVal);
		    }
		    else if (getComplacencyVal() > 749 && getComplacencyVal() < 1300) {
		    	targetVal = 1000+(rnd.nextInt(400)-200);
		    	shiftComplacencyTo(targetVal);
		    }
		    else if (getComplacencyVal() > 1299 && getComplacencyVal() < 1500) {
		    	targetVal = 1400+(rnd.nextInt(100)-50);
		    	shiftComplacencyTo(targetVal);
		    }
		    else if (getComplacencyVal() > 1499) {
		    	targetVal = 17500+(rnd.nextInt(100)-50);
		    	shiftComplacencyTo(targetVal);
		    }

		}
		
		if (suppressionTimer > 0)  //set as suppressed or not
		{
			suppressionTimer -= 1;
			isSuppressed = true;
		}
		if (!complacencyInitial)  //Setup complacency for new yukkuri based on attitude
		{
			int localDivisor = getAttitude().ordinal()+1;  // provides divisor for initial complacency setup. Necessary to prevent rare /0 error. VN is divide by 1.
			setComplacencyVal((1600+(rnd.nextInt(400)-300))/ localDivisor) ;
			if (getComplacencyVal() > 1700 && !(isGoldBadge() || isSilverBadge()))
			{
				setComplacencyVal(getComplacencyVal() + (rnd.nextInt(550) - 560));  //prevent excessively high initial complacencyvalues, except for high grade badged yukkuri purchased from a store
		    }
			complacencyInitial = true;    //This section will only trigger once
		}
		//////////////////////////////////////////Main section below
	    if (!dead)
		{

	    	if( isFeelPain() || isFeelHardPain())
	    	{
	    		reduceComplacencyTimer += 3;     //pained yukkuri are uncomfortable
	    	}
	    
	    	if(reduceComplacencyTimer>0 && !isSuppressed){       // reduce complacency value, except when suppressed
	    		reduceComplacencyTimer -= 1;
	    		if (getComplacencyVal() > 0)
	    		{
	    	setComplacencyVal(getComplacencyVal() - 1);
	    		}
	    	}
	    	
	    	if(increaseComplacencyTimer>0 && (!isSuppressed && (reduceComplacencyTimer > 0))){      // increase complacency value. && statement prevents increases due to minor shifting occuring during inappropriate times (eg being hit)
	    		increaseComplacencyTimer -= 1;
	    		if (getComplacencyVal() < (complacencyMax + 1))
	    		{
	    	setComplacencyVal(getComplacencyVal() + 1);
	    		}
	    								}
			                                      
		}
	    if (praiseLimitTimer > 0){
	    	isPraiseLimited = true;
	    praiseLimitTimer -= 1;
	    }
	    if (praiseLimitTimer < 1)
	    {
	    	isPraiseLimited = false;
	    }
	    
		return;
		
	}
	
	public String getComplacencyDirection()
	{
		 String  complacencyDirectionError=("Error");
		 String  complacencyDirectionPos =("+");
		 String  complacencyDirectionNeg =("-");
		 String  complacencyDirectionBot =("-+");
		 String  complacencyDirectionNei =("=");
		increaseComplacency = false;
		reduceComplacency = false;
    	if (reduceComplacencyTimer > 0)
    	{
    		reduceComplacency = true;
    	}
    	if (increaseComplacencyTimer > 0)
    	{
    		increaseComplacency = true;
    	}
    	int localVal = 0;
    	if (reduceComplacency && increaseComplacency)
    	{
    		localVal = 4;
    	}
    	if (reduceComplacency && !increaseComplacency)
    	{
    		localVal = 2;
    	}
    	if (!reduceComplacency && increaseComplacency){
    		localVal = 3;
    	}
    	if (!reduceComplacency && !increaseComplacency){
    		localVal = 1;
    	}
    	switch (localVal)
    	{
    	case 1:  //both are false
    		 return complacencyDirectionNei;  
    	case 2: //reduce is true, increase is false
    		 return complacencyDirectionNeg;
    	case 3: //increase is true, reduce is false
    		return complacencyDirectionPos;
    	case 4: //both are true
    		 return complacencyDirectionBot;
    	default: return complacencyDirectionError;
    	}
	}
	
	public String getStressDirection()
	{
		 String  stressDirectionError=("Error");
		 String  stressDirectionPos =("+");
		 String  stressDirectionNeg =("-");
		 String  stressDirectionBot =("-+");
		 String  stressDirectionNei =("=");
		increaseStress = false;
		reduceStress = false;
    	if (reduceStressTimer > 0)
    	{
    		reduceStress = true;
    	}
    	if (increaseStressTimer > 0)
    	{
    		increaseStress = true;
    	}
    	int localVal = 0;
    	if (reduceStress && increaseStress)
    	{
    		localVal = 4;
    	}
    	if (reduceStress && !increaseStress)
    	{
    		localVal = 2;
    	}
    	if (!reduceStress && increaseStress){
    		localVal = 3;
    	}
    	if (!reduceStress && !increaseStress){
    		localVal = 1;
    	}
    	switch (localVal)
    	{
    	case 1:  //both are false
    		 return stressDirectionNei;  
    	case 2: //reduce is true, increase is false
    		 return stressDirectionNeg;
    	case 3: //increase is true, reduce is false
    		return stressDirectionPos;
    	case 4: //both are true
    		 return stressDirectionBot;
    	default: return stressDirectionError;
    	}
	}
	
	public Complacency getComplacency() {
	//	int complacencyPercentage = (complacencyVal / complacencyMax * 100);	
	    if (getComplacencyVal() < 400) {
	    	complacency = Complacency.REBELLIOUS;
	    }
	    else if (getComplacencyVal() > 399 && getComplacencyVal() < 750) {
	    	complacency = Complacency.IRRITATED;
	    }
	    else if (getComplacencyVal() > 749 && getComplacencyVal() < 1300) {
	    	complacency = Complacency.UNSURE;
	    }
	    else if (getComplacencyVal() > 1299 && getComplacencyVal() < 1500) {
	    	complacency = Complacency.COMPLACENT;
	    }
	    else if (getComplacencyVal() > 1499) {
	    	complacency = Complacency.CONTENT;
	    }
	    return complacency;
	}
	
	
		


	public void checkTang() {
		if(getTang() < 0) setTang(0);
		if(getTang() > TANGLEVEL[2]) setTang(TANGLEVEL[2]); 
	}
	
	private void checkBurntState() {	//used to make talking heads burn
		if (isOnFire() == false && !burned)
		{
			return;
		}
		if (burnedFeet == true)
		{
			burnedDontMove = true;
			return;
		}
		if (isOnFire() == true)
		{
			if (!cooked)
			{
			cookTimer = cookTimer + 1;        //this is all used to trigger the burn01 and then burn sprite
				if (cookTimer > COOKLIMIT)
				{
				cooked = true;
				}
			}
			setHappiness(Happiness.SAD);
		//	panicType = panicType.BURN;
	//		if (isCurrentlyWet())
		//	{
			//	onFire = false;
		//		whiteSteamEffect();
			//	return;
	//		}
			
			burnTimer = burnTimer + 1;
			if (burnTimer == BURNLIMIT[getBodyAgeState().ordinal()] || burnTimer > BURNLIMIT[getBodyAgeState().ordinal()])
			{
				setMessage(MessagePool.getMessage(this, MessagePool.Action.CantMove));
				burnedFeet = true;
				burnedDontMove = true;
				onFire = false;
			}
			return;
		}
		else
		return;
	}
	
	private void whiteSteamEffect() {
		steamTimer = steamTimer + 1;
		steamy = true;
		if (steamTimer == STEAMLIMIT)
		{
		onFire = false;
		steamy = false;
		steamTimer = 0;
		}
	}
	
	public boolean isOnFire() {
		return onFire;
	}
	
	
	private void checkWetState() {					//controls effects from water immersion
		if (isCurrentlyWet())
		{
			
			wetTimer = wetTimer + 1;
				if (wetTimer > WETLIMIT[ageState.ordinal()])
				{
					wetCount = wetCount + 1;
					wetTimer = 0;
				}
			if (wetCount == 0)
			{
				soaked = false;
				melting = false;
				dissolving = false;
				return;
			}
			else if (wetCount == 1)
			{
				soaked = true;
				melting = false;
				dissolving = false;
				//stressFromDrowning = stressFromDrowning + 10;
				return;
			}
			else if (wetCount > 1 )
			{
				soaked = false;
				melting = true;
				dissolving = false;
			//	stressFromDrowning = stressFromDrowning + 20;
				return;
			}
		/*	if (wetCount == 3)
			{
				soaked = false;
				melting = false;
				dissolving = true;
				stressFromDrowning = stressFromDrowning + 25;
				return;
			}
			if (wetCount > 3)
			{
				soaked = false;
				melting = false;
				dissolving = true;
				return;
			} */
			return;
		}
		else
		/*	if (stressFromDrowning > 0)
			{
				stressFromDrowning = stressFromDrowning - 5;
			}
		*/
		if (wetCount > 0 && wetTimer == 0 && !hasMelted)
		{
			wetCount = wetCount - 1;
			wetTimer = WETLIMIT[ageState.ordinal()] - 1;
		}
			return;
	}  
	
	
	private void checkMelting() {					//Pins melting sprites in place prior to death.
		if (melting)
		{
			pinned = true;
			if (meltState == 0)
					{
				meltState = 1;
					}
			meltTimer = meltTimer + 1;
			if (meltTimer > 80)
			{
				meltState = 2;
				hasMelted = true;
				strike(DAMAGELIMIT[ageState.ordinal()]);
			//	melting = false;
			}
			
		}
	}
	
	
	private void checkMessage() {
		--messageCount;
		if (messageCount <= 5) {
			// stop to show the message 0.5 sec. before.
			messageBuf = null;
		}
		if (messageCount <= 0) {
			messageCount = 0;
			furifuri = false;
			setStrike(false);
			setEating(false);
			setEatingShit(false);
			peropero = false;
			sukkiri = false;
			nobinobi = false;
			pikopiko = false;
		}
		if (dead) {
			if (!silent) {
				String messages = MessagePool.getMessage(this, MessagePool.Action.Dead);
				setMessage(messages);
				if (messageBuf == messages)  {
					// if the message is set successfully, be silent.
					silent = true;
				}
			}
			return;
		} else if (messageBuf == null) {
			if (sleeping) {
				if (!isTalking() && rnd.nextInt(10) == 0) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Sleep));
					addStress(-10);
				}
			} else if (!flyingType && getZ() > 15 && panicType == null && !lockmove && getCriticalDamege() != CriticalDamegeType.CUT) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Flying), ConstantValues.HOLDMESSAGE, false, true);
				addStress(-10);
			} else if ( checkBurstState() == Burst.NEAR ) {
				if (!isTalking() && rnd.nextInt(8) == 0) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Inflation));
				}
			} else if ( nearToBirth() && !birth ) {
				if (!isTalking() && rnd.nextInt(8) == 0) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.NearToBirth));
					EventLogic.addWorldEvent(new BreedEvent(this, null, null, 2), null, null);
				}
			}
		}
	}

	public void stay() {
		staying = true;
		stayTime = ConstantValues.STAYLIMIT;
	}
	

	
	public boolean  isCurrentlyWet() {
		return wetCurrently;
	}
	
	
	private void stay(int time) {
		staying = true;
		stayTime = time;
	}
	
	public void setHappiness(Happiness happy) {
		if (dead || isIdiot()) {
			happiness = Happiness.AVERAGE;
			return;
		}
		if (happy == Happiness.SAD) {
			if (happiness != Happiness.VERY_SAD) {
				sadPeriod = 0;
				happiness = happy;
			}
		}
		else if (happy == Happiness.HAPPY) {
			if (happiness != Happiness.VERY_HAPPY) {
				sadPeriod = 0;
				happiness = happy;
			}
		}
		else {
			if (happy == Happiness.VERY_SAD) {
				sadPeriod = 1200 + rnd.nextInt(400) - 200;
			} else {
				sadPeriod = 0;
			}
			happiness = happy;
		}
		if (happiness == Happiness.HAPPY || happiness == Happiness.VERY_HAPPY) {
			scare = false;
			angry = false;
		} else if (happiness == Happiness.SAD || happiness == Happiness.VERY_SAD) {
			angry = false;
		}
	}
	
	public void upDate(){
		// Move Stalk
		if ( getStalks().size() > 0 ) {
			int direction = getDirection().ordinal();
			int centerH = (imgH[getBodyAgeState().ordinal()] + getExpandSize() + getExternalForceW());
			int sX;
			int ofsX;
			int k = 0;
			for ( Stalk stalk: getStalks()) {
				if ( stalk != null ) {
					sX = stalk.getPivotX() + ConstantValues.stalkOfsX[k] - (int)((3 - getBodyAgeState().ordinal()) * 8.75f);
					if (direction == ConstantValues.RIGHT) {
						stalk.setDirection( 0 );
					}else{
						stalk.setDirection( 1 );
						sX = -sX;
					}
					ofsX = Translate.invertX(sX, getY());
					ofsX = Translate.transSize(ofsX);
					stalk.setX( getX() + ofsX );
					stalk.setY( getY() );
					stalk.setZ( getZ() + (int)(centerH * 0.09f)  + ConstantValues.stalkOfsY[k] );
					stalk.upDate();
				}
				k = (k + 1) & 7;
			}
		}
	}
	
	
	
	protected boolean isRudeMessage() {
		if (isRude()) {
			if (rnd.nextInt(messageDiscipline+1) == 0) {
				return true;
			}
			return false;
		}
		return false;
	}

	// 讓呎ｺ悶�繝｡繝�そ繝ｼ繧ｸ陦ｨ遉ｺ
	public void setMessage(String message) {
		setMessage(message, WindowType.NORMAL, ConstantValues.HOLDMESSAGE, true, false);
	}
	// 譎る俣謖�ｮ壹Γ繝�そ繝ｼ繧ｸ陦ｨ遉ｺ
	public void setMessage(String message, int count) {
		setMessage(message, WindowType.NORMAL, count, true, false);
	}
	// 蜑ｲ繧願ｾｼ縺ｿ謖�ｮ壹Γ繝�そ繝ｼ繧ｸ陦ｨ遉ｺ
	public void setMessage(String message, boolean interrupt) {
		setMessage(message, WindowType.NORMAL, ConstantValues.HOLDMESSAGE, interrupt, false);
	}
	// 蜈ｨ謖�ｮ壹Γ繝�そ繝ｼ繧ｸ陦ｨ遉ｺ
	public void setMessage(String message, int count, boolean interrupt, boolean piko) {
		setMessage(message, WindowType.NORMAL, count, interrupt, piko);
	}
	
	// 繝ｯ繝ｼ繝ｫ繝峨う繝吶Φ繝育匱逕溘Γ繝�そ繝ｼ繧ｸ
	public void setWorldEventSendMessage(String message, int count) {
		setMessage(message, WindowType.WORLD_SEND, count, true, false);
	}
	// 繝ｯ繝ｼ繝ｫ繝峨う繝吶Φ繝亥ｿ懃ｭ斐Γ繝�そ繝ｼ繧ｸ
	public void setWorldEventResMessage(String message, int count, boolean interrupt, boolean piko) {
		setMessage(message, WindowType.WORLD_RES, count, interrupt, piko);
	}

	// 蝗ｺ菴薙う繝吶Φ繝育匱逕溘Γ繝�そ繝ｼ繧ｸ
	public void setBodyEventSendMessage(String message, int count) {
		setMessage(message, WindowType.BODY_SEND, count, true, false);
	}
	// 蝗ｺ菴薙う繝吶Φ繝亥ｿ懃ｭ斐Γ繝�そ繝ｼ繧ｸ
	public void setBodyEventResMessage(String message, int count, boolean interrupt, boolean piko) {
		setMessage(message, WindowType.BODY_RES, count, interrupt, piko);
	}
	
	public void setBodyEventResMessage2(String message, int count, boolean interrupt, boolean piko) {
		setMessage(message, WindowType.BODY_RES, count, interrupt, piko );
	}

	protected void setMessage(String message, WindowType type, int count, boolean interrupt, boolean piko) {  //setmessage method
		if ( !canTalk ) {
			messageCount = 0;
			messageBuf = null;
			return;
		}

		// interrupt縺荊rue縺ｪ繧臥樟蝨ｨ繝｡繝�そ繝ｼ繧ｸ陦ｨ遉ｺ荳ｭ縺ｧ繧ょ牡繧願ｾｼ繧�
		if((interrupt || messageCount == 0) ) {
			messageCount = count;
			messageBuf = message;
			// reset actions.
			furifuri = false;
//			strike = false;
//			eating = false;
//			eatingShit = false;
//			peropero = false;
			sukkiri = false;
			nobinobi = false;
			pikopiko = piko;
			messageLineColor = ConstantValues.windowColor[type.ordinal()][0];
			messageBoxColor = ConstantValues.windowColor[type.ordinal()][1];
			messageTextColor = ConstantValues.windowColor[type.ordinal()][2];
			messageWindowLine = ConstantValues.windowLine[type.ordinal()];
			messageTextSize = 12;
		}
	}

	protected void setNegiMessage(String message, boolean piko) {
		setNegiMessage(message, ConstantValues.HOLDMESSAGE, piko);
	}

	protected void setNegiMessage(String message, int count, boolean piko) {
		if ( !canTalk ) {
			messageCount = 0;
			messageBuf = null;
			return;
		}
		messageCount = count;
		messageBuf = message;
		pikopiko = piko;
		// reset actions.
		furifuri = false;
		setStrike(false);
		setEating(false);
		setEatingShit(false);
		peropero = false;
		sukkiri = false;
		nobinobi = false;
		messageLineColor = ConstantValues.negiWindowColor[0];
		messageBoxColor = ConstantValues.negiWindowColor[1];
		messageTextColor = ConstantValues.negiWindowColor[2];
		messageWindowLine = ConstantValues.windowLine[0];
		messageTextSize = 120;
	}

	// public methods
	public int getHybridType(int partnerType) {
		return getType();
	}


	public boolean isBusy(){
		return isBusy;
	}
	public int getDamagePercent() {
		int damagePercent = 100 * getDamage() / getDamageLimit();
		return damagePercent;
	}
	
	public ArrayList<Dna> getBabyTypes() {
		return babyTypes;
	}

	public ArrayList<Dna> getStalkBabyTypes() {
		return stalkBabyTypes;
	}

	public ArrayList<Stalk> getStalks() {
		return stalks;
	}

	public Stalk getBindStalk() {
		return bindStalk;
	}

	public void setBindStalk( Stalk stalk ) {
		bindStalk = stalk;
	}
	
	public void touchStalk() {
		setMessage(MessagePool.getMessage(this, MessagePool.Action.AbuseBaby));
		setHappiness(Happiness.SAD);
	}

	public void setUnBirth( boolean flag ) {
		unBirth = flag;
		enableWall = !flag;
		canTalk = !flag;
		if ( flag ){
			setMessage(null);
		}
	}

	public boolean isUnBirth() {
		return unBirth;
	}
	
	public Dna getBabyTypesDequeue() {
		Dna babyType = null;
		if ( babyTypes.size() > 0 ){
			babyType = babyTypes.get(0);
			babyTypes.remove(0);
		}
		return babyType;
	}

	public Stalk getStalksDequeue() {
		Stalk stalk = null;
		if ( stalks.size() > 0 ){
			stalk = stalks.get(0);
			stalks.remove(0);
		}
		return stalk;
	}
	
	public Dna getDna() {
		Dna ret = new Dna(getType(), getAttitude(), getIntelligence(), false);
		
		return ret;
	}

	public AgeState getAgeState() {
		return getBodyAgeState();
	}
	
	public int getDisplayableWetState()
	{
		int displayableWetState =  (100 * wetTimer / WETLIMIT[ageState.ordinal()] + wetCount * 100) / 2;
		if (displayableWetState > 150)
		{
			displayableWetState = 150;
		}
		return displayableWetState;
	}
	
	public String getDisplayableWetStateString()
	{
		
		String displayableWetStateString = getDisplayableWetState() + "%";
		return displayableWetStateString;
	}

	public long getAge() {
		return age;
	}
	
	public long getAgeLimit() {
		return LIFELIMIT;
	}

	public Attitude getAttitude() {
		return attitude;
	}
	

	


	public Intelligence getIntelligence() {
		return intelligence;
	}

	public static int getHeadageLimit() {
		return ConstantValues.HEADAGELIMIT;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getDamageLimit() {
		return DAMAGELIMIT[getBodyAgeState().ordinal()];
	}
	
	public int getHungry() {
		return hungry;
	}
	
	public int getHungryLimit() {
		return HUNGRYLIMIT[getBodyAgeState().ordinal()];
	}
	
	public int getShit() {
		return shit;
	}
	
	public int getShitLimit() {
		return SHITLIMIT[getBodyAgeState().ordinal()];
	}

	public YukkuriType getShitType() {
		return shitType;
	}

	public int getStress() {
		return stress;
	}

	public void setStress(int s) {
		stress = TICK * s;
	}

	public void addStress(int s) {
		if(dead) return;
		if (s < 0){
		s *= -1;
		reduceStressTimer +=s;
		}
		else
		{
			increaseStressTimer +=s;
		}
	}

	public int getStressLimit() {
		return STRESSLIMIT[getBodyAgeState().ordinal()];
	}

	public int getFootBakePeriod() {
		return footBakePeriod;
	}

	public void setFootBakePeriod(int s) {
		footBakePeriod = s;
	}

	public void addFootBakePeriod(int s) {
		footBakePeriod += s;
	}

	public FootBake getFootBakeLevel() {
		FootBake ret = FootBake.NONE;
		
		if(footBakePeriod < 0) footBakePeriod = 0;

		if(footBakePeriod > DAMAGELIMIT[getBodyAgeState().ordinal()]) {
			ret = FootBake.CRITICAL;
		} else if(footBakePeriod > (DAMAGELIMIT[getBodyAgeState().ordinal()] >> 1)) {
			ret = FootBake.MIDIUM;
		}
		return ret;
	}

	public int getTang() {
		return tang;
	}

	public TangType getTangType() {
		TangType ret;
		if(getTang() < TANGLEVEL[0]) {
			ret = TangType.POOR;
		} else if(getTang() < TANGLEVEL[1]) {
			ret = TangType.NORMAL;
		} else {
			ret = TangType.GOURMET;
		}
		return ret;
	}

	public int getEatAmount() {
		return EATAMOUNT[getBodyAgeState().ordinal()];
	}

	public int getStep() {
		return STEP[getBodyAgeState().ordinal()];
	}

	public String getMessage() {
		return messageBuf;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection( Direction dir ) {
		direction = dir;
	}
	
	public int getAmount() {
		return bodyAmount;
	}

	public boolean addAmount(int val) {
		bodyAmount += val;
		if(bodyAmount <= 0) {
			bodyAmount = 0;
			return true;
		}
		return false;
	}
	
	public void initAmount(AgeState val) {
		bodyAmount = DAMAGELIMIT[val.ordinal()];
	}

	public void getExpandShape(Rectangle r) {
		int forceW = getExternalForceW();
		int forceH = getExternalForceH();
		r.width = imgW[getBodyAgeState().ordinal()] + getExpandSize() + forceW;
		r.height = imgH[getBodyAgeState().ordinal()] + getExpandSize() + forceH;
		r.x = r.width >> 1;
		r.y = r.height - 1;
	}
	
	public int getCollisionX() {
		return (imgW[getBodyAgeState().ordinal()] + getExpandSize()) >> 1;
	}

	// 螯雁ｨ��縺�ｓ縺�ｓ縺ｫ繧医ｋ菴灘梛縺ｮ縺ｵ縺上ｉ縺ｿ蜿門ｾ�
	private int getExpandSize() {
		return (20 - 20/(getBabyTypes().size() + 1 )) + getBabyTypes().size() * 2 + ( ( getShit() * 4 / 5 ) /SHITLIMIT[getBodyAgeState().ordinal()] ) * 5;
	}
	
	// 縺ｲ縺｣縺ｱ繧翫�謚ｼ縺励▽縺ｶ縺励↓繧医ｋ菴灘梛縺ｮ螟牙ｽ｢蜿門ｾ�
	private int getExternalForceW() {
		int ret = 0;
		// +縺ｲ縺｣縺ｱ繧�-謚ｼ縺励▽縺ｶ縺�
		if(extForce < 0) ret = -extForce;
		return ret;
	}

	private int getExternalForceH() {
		int ret = 0;
		// +縺ｲ縺｣縺ｱ繧�-謚ｼ縺励▽縺ｶ縺�
		if(extForce > 0) {
			ret = extForce * 6;
		} else if(extForce < 0) {
			ret = extForce * 2;
		}
		return ret;
	}

	public int getExternalForce() {
		return extForce;
	}

	public int getSize() {
		return imgW[getBodyAgeState().ordinal()] + getExpandSize();
	}
	
	public int getOriginSize() {
		return imgW[getBodyAgeState().ordinal()];
	}
	
	public int getStrength() {
		return STRENGTH[getBodyAgeState().ordinal()];
	}
	
	public int getEyesight() {
		return EYESIGHT;
	}
	
	public int getDirX() {
		return dirX;
	}
	
	public int getDirY() {
		return dirY;
	}

	public int getDestX() {
		return destX;
	}
	
	public int getDestY() {
		return destY;
	}

	public Image getShadowImage() {
		return shadowImages[getBodyAgeState().ordinal()];		
	}

	public int getShadowH() {
		return shadowImgH[getBodyAgeState().ordinal()];
	}

	protected void setBoundary(Rectangle[] r, Dimension[] braid) {
		for(int i = 0; i < r.length; i++) {
			pivX[i] = r[i].x;
			pivY[i] = r[i].y;
			imgW[i] = r[i].width;
			imgH[i] = r[i].height;
		}
		for(int i = 0; i < braid.length; i++) {
			if(braid[i] != null) {
				braidImgW[i] = braid[i].width;
				braidImgH[i] = braid[i].height;
			}
		}
	}

	public int getW() {
		return imgW[getBodyAgeState().ordinal()];
	}

	public int getH() {
		return imgH[getBodyAgeState().ordinal()];
	}

	public int getPivotX() {
		return pivX[getBodyAgeState().ordinal()];
	}

	public int getPivotY() {
		return pivY[getBodyAgeState().ordinal()];
	}
	
	public void getBoundaryShape(Rectangle r) {
		r.x = pivX[getBodyAgeState().ordinal()];
		r.y = pivY[getBodyAgeState().ordinal()];
		r.width = imgW[getBodyAgeState().ordinal()];
		r.height = imgH[getBodyAgeState().ordinal()];
	}

	public int getBraidW() {
		return braidImgW[getBodyAgeState().ordinal()];
	}

	public int getBraidH() {
		return braidImgH[getBodyAgeState().ordinal()];
	}

	public int getMaxHaveBaby() {
		return getDamageLimit()/300;
	}
	
	public int getWeight() {
		return (WEIGHT[getBodyAgeState().ordinal()] + (babyTypes.size() + stalkBabyTypes.size()) * 50);
	}

	public CriticalDamegeType getCriticalDamegeType() {
		return getCriticalDamege();
	}

	public void setCriticalDamegeType(CriticalDamegeType type) {
		setCriticalDamege(type);
	}

	public Obj getMoveTarget() {
		return moveTarget;
	}
	
	public int getForceFace() {
		return forceFace;
	}

	public void setForceFace(int f) {
		forceFace = f;
	}

	public Obj getFavItem(FavItemType key) {
		return favItem.get(key);
	}

	public void setFavItem(FavItemType key, Obj val) {
		favItem.put(key, val);
	}

	public static void loadShadowImages (ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/";
		int sx, sy;

		shadowImages[ConstantValues.adultIndex] = ImageIO.read(loader.getResourceAsStream(path+"shadow.png"));

		sx = (int)((float)shadowImages[ConstantValues.adultIndex].getWidth(io) * ConstantValues.bodySize[1]);
		sy = (int)((float)shadowImages[ConstantValues.adultIndex].getHeight(io) * ConstantValues.bodySize[1]);
		shadowImages[ConstantValues.childIndex] = shadowImages[ConstantValues.adultIndex].getScaledInstance(sx, sy, Image.SCALE_AREA_AVERAGING);
		sx = (int)((float)shadowImages[ConstantValues.adultIndex].getWidth(io) * ConstantValues.bodySize[0]);
		sy = (int)((float)shadowImages[ConstantValues.adultIndex].getHeight(io) * ConstantValues.bodySize[0]);
		shadowImages[ConstantValues.babyIndex] = shadowImages[ConstantValues.adultIndex].getScaledInstance(sx, sy, Image.SCALE_AREA_AVERAGING);
		
		for(int i = 0; i < 3; i++) {
			shadowImgW[i] = shadowImages[i].getWidth(io);
			shadowImgH[i] = shadowImages[i].getHeight(io);
			shadowPivX[i] = shadowImgW[i] >> 1;
			shadowPivY[i] = shadowImgH[i] >> 1;
		}
	}
	
	public void setAngry() {
		if (dead || sleeping) {
			return;
		}
		if (damageState == Damage.NONE && !isVerySad()) {
			angry = true;
			scare = false;
		}
		furifuri = false;
		if(!isRaper()) exciting = false;
		relax = false;
		nobinobi = false;
		excitingPeriod = 0;
		noDamagePeriod = 0;
		noHungryPeriod = 0;
	}

	public void setDirty(boolean flag) {
		dirty = flag;
		if (dirty) {
			setHappiness(Happiness.SAD);
			addStress(50);
		}
		else {
			setHappiness(Happiness.HAPPY);
			addStress(-50);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Sukkiri), 60);
			stay(60);
		}
	}
	
	public void setDirtyFlag(boolean b) {
		dirty = b;
	}
	
	public void setCleaning() {
		// 豎壹ｌ縲∵ｿ｡繧瑚ｧ｣髯､
		setDirty(false);
		wet = false;
		wetPeriod = 0;
	}
	
	public void setSick() {
		if (rnd.nextInt(ROBUSTNESS+1) == 0) {
			sick = true;
		}
	}
	
	public void setLinkParent(Obj o) {
		linkParent = o;
	}

	public Obj getLinkParent() {
		return linkParent;
	}

	public boolean isHybrid() {
		return false;
	}
	
	public boolean isBecomingDiscontent(){
		return (reduceComplacencyTimer > 200);
	}
	
	public boolean isShocked(){
		return (shockTimer > 0);
	}

	public boolean isHungry() {
		return (!dead && (hungry >= HUNGRYLIMIT[getBodyAgeState().ordinal()] / 2) && (!isBusy()));
	}

	public boolean isSoHungry() {
		return (!dead && (hungry >= HUNGRYLIMIT[getBodyAgeState().ordinal()] * 0.8f));
	}

	public boolean isFull() {
		return (!dead && (hungry <= HUNGRYLIMIT[getBodyAgeState().ordinal()] / 20));
	}

	public boolean isTooHungry() {
		return (!dead && hungry >= HUNGRYLIMIT[getBodyAgeState().ordinal()] && checkDamageState() == Damage.VERY);
	}

	public boolean isDead() {
		return dead;
	}

	public boolean isAdult() {
		return (getBodyAgeState() == AgeState.ADULT);
	}
	
	public boolean isChild() {
		return (getBodyAgeState() == AgeState.CHILD);
	}
	
	public boolean isBaby() {
		return (getBodyAgeState() == AgeState.BABY);
	}

	public boolean isSleeping() {
		return (!dead && sleeping);
	}
	
	public boolean isSleepy() {
		if (wakeUpTime + ACTIVEPERIOD < getAge()) {
			return true;
		}
		return false;
	}

	public boolean isShitting() {
		return (!dead && shitting);
	}

	public boolean isExciting() {
		return (!dead && exciting);
	}

	public boolean isAngry() {
		return (!dead && angry);
	}

	public boolean isScare() {
		return (!dead && scare);
	}

	public boolean isSad() {
		return (!dead && happiness == Happiness.SAD);
	}
	
	public boolean isVerySad() {
		return (!dead && happiness == Happiness.VERY_SAD);
	}
	
	public boolean isHappy() {
		return (happiness == Happiness.HAPPY || happiness == Happiness.VERY_HAPPY);
	}

	public boolean isFurifuri() {
		return (!dead && furifuri);
	}
	
	public boolean isNobinobi() {
		return (!dead && nobinobi);
	}

	public boolean isStrike() {
		return (!dead && strike);
	}

	public boolean isDamaged() {
		return (checkDamageState() == Damage.VERY);
	}

	public boolean isFeelPain() {
		return (checkPainState() == Pain.VERY || checkPainState() == Pain.SOME);
	}

	public boolean isFeelHardPain() {
		return (checkPainState() == Pain.VERY || checkPainState() == Pain.VERY);
	}

	public boolean isBirth() {
		return (!dead && birth);
	}

	public boolean isEating() {
		return (!dead && eating);
	}

	public boolean isEatingShit() {
		return (!dead && eatingShit);
	}

	public boolean isPeroPero() {
		return (!dead && peropero);
	}

	public boolean isSukkiri() {
		return (!dead && sukkiri);
	}

	public boolean isDirty() {
		return (dead || dirty);
	}

	public boolean isCrashed() {
		return crashed;
	}

	public boolean isRude() {
		return (getAttitude() == Attitude.SHITHEAD || getAttitude() == Attitude.SUPER_SHITHEAD);
	}

	public boolean isNormal() {
		return (getAttitude() == Attitude.AVERAGE);
	}

	public boolean isSmart() {
		return (getAttitude() == Attitude.VERY_NICE || getAttitude() == Attitude.NICE);
	}
	
	public boolean isSuppressed() {
		return isSuppressed;
	}

	// 螳ｶ譌城未菫ゅ�繝√ぉ繝�け鄒､  霎ｿ繧後ｋ縺ｮ縺ｯ1荳紋ｻ｣遞句ｺｦ
	
	// other縺ｨ菴輔ｉ縺九�螳ｶ譌城未菫ゅ↓縺ゅｋ縺�
	public boolean isFamily(Body other) {

		if(isParent(other)) return true;
		if(other.isParent(this)) return true;
		if(isPartner(other)) return true;
		if(isSister(other)) return true;
		return false;
	}
	// other縺瑚�蛻��隕ｪ縺�
	public boolean isParent(Body other) {
		return (other.parents[Parent.PAPA.ordinal()] == this || other.parents[Parent.MAMA.ordinal()] == this);
	}
	public boolean isPapa(Body other) {
		return (other.parents[Parent.PAPA.ordinal()] == this);
	}
	
	public boolean isMama(Body other) {
		return (other.parents[Parent.MAMA.ordinal()] == this);
	}
	// 辷ｶ隕ｪ繧貞叙蠕�
	public Body getFather() {
		return parents[Parent.PAPA.ordinal()];
	}
	// 豈崎ｦｪ繧貞叙蠕�
	public Body getMother() {
		return parents[Parent.MAMA.ordinal()];
	}
	// other縺瑚�蛻��蟄舌°
	public boolean isChild(Body other) {
		return other.isParent(this);
	}
	// other縺瑚�蛻��縺､縺後＞縺�
	public boolean isPartner(Body other) {
		return (getPartner() != null && getPartner() == other);
	}
	// other縺瑚�蛻��蟋牙ｦｹ縺�
	public boolean isSister(Body other) {
		if (parents[Parent.MAMA.ordinal()] != null) {
			return (parents[Parent.MAMA.ordinal()] == other.parents[Parent.MAMA.ordinal()]);
		}
		return false;
	}
	
	
	// badge methods
	public boolean isNoBadge()
	{
		return (badgeState == BadgeState.NONE);
	}
	
	public boolean isFakeBadge()
	{
		return (badgeState == BadgeState.FAKE);
	}
	
	public boolean isBronzeBadge()
	{
		return (badgeState == BadgeState.BRONZE);
	}
	
	public boolean isSilverBadge()
	{
		return (badgeState == BadgeState.SILVER);
	}
	
	public boolean isGoldBadge()
	{
		return (badgeState == BadgeState.GOLD);
	}
	
	// family methods
	public boolean isElderSister(Body other) {
		return (isSister(other) && (getAge() >= other.getAge()));
	}

	public boolean isRaper() {
		return isRapist();
	}

	public boolean isRaperChild() {
		return fatherRaper;
	}

	public boolean isMotherhood(Body partner) {
		if (isRude() && (getType() != partner.getType()))
			return false;
		else
			return motherhood;
	}
	
	public boolean isOld() {
		return getAge() > (LIFELIMIT*9/10);
	}
	
	public boolean isTalking() {
		return (messageCount > 0);
	}
	
	public boolean isSick() {
		if (sickPeriod > INCUBATIONPERIOD) {
			return true;
		}
		return false;
	}
	
	public boolean isIdiot() {
		return (getType() == Tarinai.type);
	}
	
	public boolean isCantDie() {
		return (cantDiePeriod > 0);
	}
	
	public boolean isBurst() {
		return ( checkBurstState() == Burst.BURST );
	}

	public boolean isBodyCastration() {
		return ( bodyCastration );
	}

	public boolean isInfration() {
		return ( checkBurstState() != Burst.NONE );
	}

	public boolean isBurned() {
		return ( burned );
	}
	
	public boolean isBurnedfeet() {
		return ( burnedFeet);
	}

	public void setBurned(boolean b) {
		burned = b;
		setForcePanicClear();
	}

	public boolean isWet() {
		return ( wet );
	}

	public boolean isbindStalk() {
		return (bindStalk != null);
	}

	// 鬟幄｡檎ｨｮ縺九←縺�°
	// 遞ｮ譌上→縺励※縺ｮ繝輔Λ繧ｰ繧定ｿ斐☆縺ｮ縺ｧ迴ｾ蝨ｨ鬟帙∋繧九°縺ｯcanflyCheck縺ｧ繝√ぉ繝�け
	public boolean isFlyingType() {
		return flyingType;
	}
	
	// 迴ｾ蝨ｨ鬟幄｡悟庄閭ｽ縺�
	public boolean canflyCheck() {
		return (flyingType && hasBraid && !dead && !sleeping);
	}

	// 縺翫＆縺偵�鄒ｽ縲∝ｰｻ蟆ｾ繧呈戟縺､縺�
	// 遞ｮ譌上→縺励※縺ｮ繝輔Λ繧ｰ繧定ｿ斐☆縺ｮ縺ｧ迴ｾ蝨ｨBraid縺後■縺弱ｉ繧後※縺ｪ縺�°縺ｯhasBraidCheck縺ｧ繝√ぉ繝�け
	public boolean isBraidType() {
		return braidType;
	}

	// 迴ｾ蝨ｨ縺翫＆縺偵�鄒ｽ縲∝ｰｻ蟆ｾ縺後≠繧九°
	public boolean hasBraidCheck() {
		return hasBraid;
	}
	
	// 縺翫＆縺堤�螢�
	public void takeBraid() {
		if(dead) return;
		if(!braidType) return;
		if(!hasBraid) return;
		
		hasBraid = false;
		setHappiness(Happiness.VERY_SAD);
		setMessage(MessagePool.getMessage(this, MessagePool.Action.BraidCut));
	}

	public boolean isCantMove() {
		return lockmove;
	}
	
	public void setCantMove(boolean b) {
		lockmove = b;
	}

	public boolean isPredatorType() {
		return (predatorType != null);
	}
	
	///////////// isYukkuri bools
	
	//COMMON
	public boolean isMarisa() {
		return (getType() == 0);
	}
	
	
	public boolean isReimu() {
		return (getType() == 1);
	}
	
	public boolean isAlice() {
		return (getType() == 2);
	}
	
	public boolean isPatchy() {
		return (getType() == 3);
	}
	
	public boolean isChen() {
		return (getType() == 4);
	}
	
	public boolean isMyon() {
		return (getType() == 5);
	}
	
	
	////////////////
	
	
	
	

	public boolean isTargetBind() {
		return targetBind;
	}
	
	public void setTargetBind(boolean val) {
		targetBind = val;
	}

	public boolean hasBabyOrStalk() {
		return ( hasBaby || hasStalk );
	}
	
	public boolean hasBaby() {
		return ( hasBaby );
	}
	
	public void setHasBaby(boolean b) {
		hasBaby = b;
	}
	
	public boolean hasStalk() {
		return ( hasStalk );
	}

	public boolean hasOkazari() {
		return (okazariType != null);
	}

	public OkazariType getOkazariType() {
		return okazariType;
	}

	public boolean hasPants() {
		return isHasPants();
	}
	
	public boolean wantToShit() {
		int step = (checkHungryState() == Hunger.NONE ? TICK*2 : TICK);
		int adjust = 100*(isRude() ? 1 : 2)*shittingDiscipline;
		if ((SHITLIMIT[getBodyAgeState().ordinal()] - getShit()) < (ConstantValues.DIAGONAL * step + adjust)) {
			return true;
		}
		return false;
	}
	
	public boolean nearToBirth() {
		int step = (checkHungryState() == Hunger.NONE ? TICK*2 : TICK);
		int adjust = 100*(isRude() ? 1 : 2);
		
		int limit = PREGPERIOD - pregnantPeriod - ( pregnantPeriodBoost / 2 );
		int diagonal = ConstantValues.DIAGONAL * step + adjust; 
		if (limit < diagonal && hasBabyOrStalk()) {
			return true;
		}
		return false;
	}
	
	// 闌弱ｒ蠑輔▲縺捺栢縺�
	public void removeStalk(Stalk s) {
		if (!isDead() && !isSleeping()) {
			setHappiness(Happiness.VERY_SAD);
			addStress(200);
			reduceComplacencyTimer += 350;
			
			setMessage(MessagePool.getMessage(this, MessagePool.Action.AbuseBaby));
		}
		getStalks().remove(s);
		if(getStalks().size() == 0) hasStalk = false;
	}

	public void doSukkiri(Body p) {
		if (dead) {
			return;
		}
		// change own state
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Sukkiri), 60, true, false);
		setStress(0);
		stay(60);
		p.setStress(0);
		p.stay(60);
		increaseComplacencyTimer += 75;
		p.increaseComplacencyTimer += 75;
		clearActions();
		sukkiri = true;
		exciting = false;
		setHappiness(Happiness.HAPPY);
		hungry += HUNGRYLIMIT[AgeState.BABY.ordinal()];
		hungryState = checkHungryState();
		// if it has pants, cannot get pregnant
		if (hasPants() || p.hasPants()) {
			if (hasPants()) {
				setDirty(true);
			}
			else {
				p.setDirty(true);
			}
			return;
		}
		if ((isSick() || p.isSick()) && rnd.nextBoolean()) {
			p.setSick();
			setSick();
		}
		if (p.isDead()) {
			return;
		}
		// 逶ｸ謇九�螯雁ｨ�愛螳�
		p.setMessage(MessagePool.getMessage(p, MessagePool.Action.Sukkiri), 60, true, false);
		p.clearActions();
		p.sukkiri = true;
		p.exciting = false;
		p.setHappiness(Happiness.HAPPY);
		p.hungry += (HUNGRYLIMIT[AgeState.BABY.ordinal()]*2);
		p.hungryState = p.checkHungryState();
		
		// 螯雁ｨ�ち繧､繝励�繝ｩ繝ｳ繝�Β縺ｧ豎ｺ螳�
		boolean stalkMode = rnd.nextBoolean();
		
		// 隧ｲ蠖薙ち繧､繝励′驕ｿ螯翫＆繧後※縺溘ｉ螯雁ｨ�､ｱ謨�
		if((stalkMode && p.getStalkCastration())
			|| (!stalkMode && p.getBodyCastration())
			|| (!stalkMode && p.getFootBakeLevel() == FootBake.CRITICAL)) {
			p.setHappiness(Happiness.VERY_SAD);
			p.setMessage(MessagePool.getMessage(p, MessagePool.Action.NoPregnancy));
			p.addStress(1000);
			return;
		}
		
		// 蟄蝉ｾ帙�逕滓�
		if(stalkMode) {
			p.hasStalk = true;
		} else {
			p.hasBaby = true;
		}
		if (isAdult() && p.isAdult())
		{
			setPartner(p);
			p.setPartner(this);
		}

		Dna baby;

		for(int i = 0; i < 5; i++) {
			
			baby = YukkuriUtil.createBabyDna(this, p, p.getType(), p.getAttitude(), p.getIntelligence(), false, p.isSick(), p.isDamaged(), i == 4);

			if(stalkMode) {
				p.stalkBabyTypes.add(baby);
			} else {
				if(baby != null) p.babyTypes.add(baby);
			}
		}
	}

	public void doRape(Body p) {
		if (isDead() || isSukkiri()) {
			return;
		}
		// change own state
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Sukkiri), 60, true, false);
		setStress(0);
		stay(65);
		p.addStress(500);
		p.stay(65);
		increaseComplacencyTimer += 125;
		p.reduceComplacencyTimer += 500;
		suppressionTimer += 200;
		shockTimer += 600;

//		clearActions();
		sukkiri = true;
		setHappiness(Happiness.HAPPY);
		hungry += HUNGRYLIMIT[AgeState.BABY.ordinal()] >> 2;
		hungryState = checkHungryState();
		// if it has pants, cannot get pregnant
		if (hasPants() || p.hasPants()) {
			if (hasPants()) {
				p.setMessage(MessagePool.getMessage(p, MessagePool.Action.ScareRapist));
				p.setHappiness(Happiness.SAD);
				setDirty(true);
			} else {
				p.setMessage(MessagePool.getMessage(p, MessagePool.Action.ScareRapist));
				p.setHappiness(Happiness.SAD);
				p.setDirty(true);
			}
			return;
		}
		if ((isSick() || p.isSick()) && rnd.nextBoolean()) {
			p.setSick();
			setSick();
		}
		if (p.isDead()) {
			return;
		}
		// 逶ｸ謇九�螯雁ｨ�愛螳�
		p.setMessage(MessagePool.getMessage(p, MessagePool.Action.RaperSukkiri), 60, true, false);
//		p.clearActions();
		p.sukkiri = true;
		p.exciting = false;
		p.setHappiness(Happiness.VERY_SAD);
		p.setForceFace(ConstantValues.CRYING);
		p.hungry += HUNGRYLIMIT[AgeState.BABY.ordinal()];
		p.hungryState = p.checkHungryState();
		
		// 驕ｿ螯翫＆繧後※縺溘ｉ螯雁ｨ�､ｱ謨�
		if(p.getStalkCastration()) {
			return;
		}
		
		// 蟄蝉ｾ帙�逕滓�
		p.hasStalk = true;
		if (isAdult() && p.isAdult())
		{
			setPartner(p);
			p.setPartner(this);
		}

		Dna baby;

		for(int i = 0; i < 5; i++) {
			baby = YukkuriUtil.createBabyDna(this, p, p.getType(), p.getAttitude(), p.getIntelligence(), true, p.isSick(), p.isDamaged(), i == 4);
			p.stalkBabyTypes.add(baby);
		}
		EventLogic.addWorldEvent(new RaperWakeupEvent(this, null, null, 1), null, null);
	}

	// 邊ｾ蟄宣､｡豕ｨ蜈･
	public void injectInto(Dna dna) {
		if(dead){
			return;
		}
		strikeByPunish();
		reduceComplacencyTimer += 200;
		Terrarium.setAlarm();
		if(dna == null || bodyCastration) {
			return;
		}
		
		Dna baby = YukkuriUtil.createBabyDna(this, null, dna.type, dna.attitude, dna.intelligence, false, false, false, true);

		babyTypes.add(baby);
		hasBaby = true;
	}

	public void forceToExcite() {
		if(isRaper()) {
			forceToRaperExcite();
			EventLogic.addWorldEvent(new RaperWakeupEvent(this, null, null, 1), this, MessagePool.getMessage(this, MessagePool.Action.Excite));
			return;
		}
		if (dead || exciting || isSick()) {
			return;
		}
		wakeup();
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Excite));
		exciting = true;
		reduceComplacencyTimer += 75;
		setPartner(null);
		stay();
	}

	public void forceToRaperExcite() {
		if (dead || exciting) {
			return;
		}
		wakeup();
		clearActions();
		exciting = true;
		setPartner(null);
		stay();
		increaseComplacencyTimer += 55;
	}

	public void forceToSleep() {
		if (dead) {
			return;
		}
		// 遏帷崟縺檎匱逕溘＠縺昴≧縺ｪ迥ｶ豕√�縺薙％縺ｧ繝√ぉ繝�け
		if(panicType == PanicType.BURN || getCriticalDamege() != null) {
			return;
		}
		
		clearActions();
		exciting = false;
		excitingPeriod = 0;
		panicType = null;
		panicPeriod = 0;
		sleepingPeriod = 0;
		sleeping = true;
	}

	public void doSurisuri(Body p) {
		if (dead || p.dead) {
			return;
		}
		setMessage(MessagePool.getMessage(this, MessagePool.Action.SuriSuri));
		addStress(-100);
		increaseComplacencyTimer += 25;
		p.increaseComplacencyTimer += 35;
		stay(40);
		p.addStress(-100);
		p.stay(40);
		setHappiness(Happiness.VERY_HAPPY);
		p.setHappiness(Happiness.VERY_HAPPY);
		if ((isSick() || p.isSick()) && rnd.nextBoolean()) {
			p.setSick();
			setSick();
		}
	}

	public void doPeropero(Body p) {
		if (dead || p.dead) {
			return;
		}
		setMessage(MessagePool.getMessage(this, MessagePool.Action.PeroPero));
		peropero = true;
		stay();
		p.dirty = false;
		setHappiness(Happiness.VERY_HAPPY);
		addStress(-50);
		increaseComplacencyTimer += 35;
		p.increaseComplacencyTimer += 45;
		p.setHappiness(Happiness.VERY_HAPPY);
		p.addStress(-200);
		if ((isSick() || p.isSick()) && rnd.nextBoolean()) {
			p.setSick();
			setSick();
		}
	}

	// 繧�▲縺上ｊ縺ｮ蜷代″繧貞宛蠕｡縺吶ｋ
	public void constraintDirection(Body b, boolean alignDir)
	{
		if(alignDir) {
			// 閾ｪ蛻�→蜷後§譁ｹ蜷代ｒ蜷代°縺帙ｋ
			b.direction = direction;
		} else {
			// 蜷代″蜷医≧繧医≧縺ｫ螟画峩
			if(getX() < b.getX()) {
				setDirection(Direction.RIGHT);
				b.setDirection(Direction.LEFT);
			} else {
				setDirection(Direction.LEFT);
				b.setDirection(Direction.RIGHT);
			}
		}
	}

	public void moveTo(int toX, int toY) {
		moveTo(toX, toY, 0);
	}

	public void moveTo(int toX, int toY, int toZ) {
		if (dead) {
			return;
		}
		if (blockedCount != 0) {
			return;
		}
		destX = Math.max(0, Math.min(toX, Terrarium.MAX_X));
		destY = Math.max(0, Math.min(toY, Terrarium.MAX_Y));
		destZ = Math.max(0, Math.min(toZ, Terrarium.MAX_Z));
	}
	
	public void moveToZ(int toZ) {
		if (dead) {
			return;
		}
		destZ = Math.max(0, Math.min(toZ, Terrarium.MAX_Z));
	}
	
	public void setTargetMoveOffset(int ox, int oy) {
		targetPosOfsX = ox;
		targetPosOfsY = oy;
	}

	

	public void lookTo(int toX, int toY) {
		if (dead || sleeping) {
			return;
		}
		if (toX > x) {
			direction = Direction.RIGHT;
		}
		else if (toX < x) {
			direction = Direction.LEFT;
		}
		stay();
	}

	public void eatFood(int amount) {

		hungry -= amount;
		if (hungry < 0) {
			hungry = 0;
		}
		angry = false;
		scare = false;
		setEating(true);
		stay();
		hungryState = checkHungryState();
	}

	// 莉悶�繧�▲縺上ｊ縺九ｉ鬟溘∋繧峨ｌ繧�
	public void eatBody(int amount) {
		bodyAmount -= amount;
		if(dead) {
			// 豁ｻ菴馴｣溘∋
			if (bodyAmount <= DAMAGELIMIT[getBodyAgeState().ordinal()]/2) {
				crashed = true;
				if (bodyAmount <= 0) {
					removed = true;
					bodyAmount = 0;
				}
			}
		} else {
			// 逕溘″縺溘∪縺ｾ鬟溘∋繧峨ｌ繧�
			addDamage(amount);
			wakeup();
			reduceComplacencyTimer += 450;
			if (bodyAmount <= DAMAGELIMIT[getBodyAgeState().ordinal()]/2) {
				bodyCut();
				if (bodyAmount <= 0) {
					toDead();
					crashed = true;
					bodyAmount = 1;
				}
			}
		}
	}

	public void eatBody(int amount, Body eater) {
		eatBody(amount);
		reduceComplacencyTimer += 200;
		if(dead) return;
		if(unBirth) return;
		
		Vomit v = SimYukkuri.mypane.terrarium.addVomit(getX(), getY(), getZ(), getAgeState(), getShitType());
		v.crushVomit();
		if(isSmart() || getAgeState().ordinal() < eater.getAgeState().ordinal() || isCantMove() || getFootBakeLevel() == FootBake.CRITICAL) {
			// 蝟�憶縺句虚縺代↑縺�憾諷九°閾ｪ蛻�ｈ繧雁､ｧ縺阪＞逶ｸ謇九�騾�￡繧�
			setMessage(MessagePool.getMessage(this, MessagePool.Action.EatenByBody));
			setHappiness(Happiness.VERY_SAD);
			runAway(getX(), getY());
		} else {
			// 蜿肴茶
			setAngry();
			setMessage(MessagePool.getMessage(this, MessagePool.Action.EatenByBody));
			EventLogic.addBodyEvent(this, new RevengeAttackEvent(this, eater, null, 1), null, null);
		}
	}

	public void addDamage(int amount) {
		if (dead) {
			return;
		}
		damage += amount;
	}

	public void strike(int amount) {
		if (dead) {
			return;
		}
		reduceComplacencyTimer += 5;
		damage += amount;
		addStress(amount >> 1);
		staying = false;
		setStrike(true);
		stay();
		damageState = checkDamageState();
		wakeup();
	}

	public void strikeByPunish() {
		if (dead) {
			return;
		}
		if (isIdiot()) {
			strike(ConstantValues.NEEDLE);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream));
			setAngry();
			return;
		}
		if (exciting && !isRaper()) {
			excitingDiscipline++;
			exciting = false;
		}
		else if (shitting) {
			shittingDiscipline++;
			shitting = false;
			setShit(getShit() - ANGRYPERIOD*2);
		}
		else if (furifuri) {
			furifuriDiscipline++;
			furifuri = false;
		}
		else if (messageBuf != null) {
			messageDiscipline++;
			messageBuf = null;
		}
		strike(ConstantValues.NEEDLE);
		reduceComplacencyTimer += 75;
		if (!isRude())
		{
			suppressionTimer += 75;
		}
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream));
		setAngry();
		if (isRude() && (messageDiscipline > DISCIPLINELIMIT) && (furifuriDiscipline != 0)) {
			if (rnd.nextBoolean()) {
				setAttitude(Attitude.AVERAGE);
				messageDiscipline = 0;
				furifuriDiscipline = 0;
			}
		}
	}
	
	public void strikeByBurn() {
		if (dead)
		{
			return;
		}
		if(sleeping) wakeup();
		staycount = 0;
		staying = false;
		setToFood(false);
		toSukkiri = false;
		setToShit(false);
		shitting = false;
		birth = false;
		reduceComplacencyTimer += 300;
		angry = false;
		furifuri = false;
		setEating(false);
		peropero = false;
		sukkiri = false;
		scare = false;
		setEatingShit(false);
		nobinobi = false;
		setHappiness(Happiness.VERY_SAD);
	//	setMessage(msgBurnedE());
		burned = true;
		onFire = true;
		angry = true;
	//	stressFromBurned+= (70000 + rnd.nextInt(23000));
		cookTimer = 0;
		COOKLIMIT = 100;
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Burning));

		return;
	}

	public void strikeByHammer() {
		if (dead) {
			return;
		}
		strike(ConstantValues.HAMMER);
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream));
		reduceComplacencyTimer += 400;
		shockTimer += 150;
		suppressionTimer += 140;
		if (getPartner() !=null)
		{
		getPartner().reduceComplacencyTimer+=200;
		}
		if (parents[Parent.MAMA.ordinal()] !=null)
		{
			String abuseBaby = "Don't hit " + getNameE() + "'s little one like that!";
			parents[Parent.MAMA.ordinal()].setMessage(abuseBaby);
		}
	
		setAngry();
		if (dead) {
			if (!silent) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying));
				stay();
			}
			if (getBodyAgeState() != AgeState.ADULT) {
				crashed = true;
			}
		}
	}

	public void strikeByPress() {
		if (!dead) {
			strike(ConstantValues.HAMMER*10);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream));
			setAngry();
		}
		if (dead) {
			if (!silent) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying));
				stay();
			}
			crashed = true;
		}
	}

	// 繧�▲縺上ｊ縺九ｉ謾ｻ謦�ｒ蜿励￠縺滓凾縺ｮ蜃ｦ逅�
	public void strikeByYukkuri(Body enemy, EventPacket e) {
		if (dead) {
			return;
		}
		// 逶ｸ謇九�繝吶�繧ｹ謾ｻ謦�鴨險育ｮ�
		int ap = enemy.getStrength();
		// 迥ｶ諷九↓繧医ｋ繝�Γ繝ｼ繧ｸ螟牙喧
		if(enemy.isDamaged()) {
			ap *= 0.75f;
		}
		if(wet) {
			ap *= 1.5f;
		}
		if(isHasPants()) {
			ap *= 0.8f;
		}
		if(exciting) {
			ap *= 0.25f;
		}
		// 蜷ｹ縺｣鬟帙�險ｭ螳�
		// 菴馴㍾蟾ｮ
		int kick = (enemy.getWeight() - getWeight()) / 100;
		if(kick < 0) kick = 0;
		kick += 3;
		if(enemy.getDirection() == Direction.LEFT) {
			kick = -kick;
		}
		
		strike(ap);
		setHappiness(Happiness.SAD);
		kick(kick, 0, -4);
		if (dead) {
			if (!silent) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying));
				stay();
			}
			crashed = true;
		} else {
			// 繝ｪ繧｢繧ｯ繧ｷ繝ｧ繝ｳ險ｭ螳�
			if(getFootBakeLevel() == FootBake.CRITICAL) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream));
			} else {
				if(e instanceof HateNoOkazariEvent) {
					// 閾ｪ蛻�↓縺翫°縺悶ｊ縺檎┌縺上※謾ｻ謦�＆繧後◆縺ｨ縺�
					if(isRude() || (getAttitude() == Attitude.AVERAGE && rnd.nextBoolean())) {
						// 蜿肴茶
						setAngry();

						EventLogic.addBodyEvent(this, new RevengeAttackEvent(this, enemy, null, 1), null, null);
					} else {
						if (!isPartner(enemy))
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream));
						if (isPartner(enemy))
						{
							String partnerAttack = enemy.getNameE() + " take it easy, it's " + getNameE() + ", not some uneasy yukkuri!";
							setMessage(partnerAttack);
							String bPartnerAttack = "This shitty yukkuri is nothing like " + enemy.getNameE() + "'s beautiful " + getNameE() +". So go die easy!";
							enemy.setMessage(bPartnerAttack);
						}
						
					}
				} else if(e instanceof RaperReactionEvent) {
					// 閾ｪ蛻�′繝ｬ繧､繝代�縺ｧ謾ｻ謦�＆繧後◆縺ｨ縺�
					// 逶ｸ謇九ｒ繝ｬ繧､繝怜ｯｾ雎｡縺ｫ
					int colX = BodyLogic.calcCollisionX(this, enemy);
					Moving.moveToSukkiri(this,enemy, enemy.getX() + colX, enemy.getY());
				}
			}
			
		}
	}

	public void strikeByObject(int ap, int weight, int vecX, int vecY) {
		if (dead) {
			return;
		}
		// 迥ｶ諷九↓繧医ｋ繝�Γ繝ｼ繧ｸ螟牙喧
		if(wet) {
			ap *= 1.5;  
		}
		if(isHasPants()) {
			ap *= 0.8;
		}
		// 蜷ｹ縺｣鬟帙�險ｭ螳�
		// 菴馴㍾蟾ｮ
		int kick = (weight - getWeight()) / 100;
		if(kick < 1) kick = 1;
//System.out.println(vecX+","+vecY+":"+kick);
		vecX *= kick;
		vecY *= kick;
		
		strike(ap);
		kick(vecX, vecY, -5);
		if (dead) {
			if (!silent) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying));
				stay();
			}
			crashed = true;
		}
	}

	// 菴薙�辷�匱
	public void bodyBurst() {
		if (crashed == false) {
			strike(ConstantValues.HAMMER*20);
			toDead();
		}
		if (dead) {
			if (!silent) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying));
				stay();
			}
			crashed = true;
		}
	}
	
	// 菴薙�蛻�妙
	public void bodyCut() {
		clearActions();
		setCriticalDamege(CriticalDamegeType.CUT);
		for(int i = 0; i < 5; i++) {
			SimYukkuri.mypane.terrarium.addVomit(getX() + 7 - rnd.nextInt(14), getY() + 7 - rnd.nextInt(14), 0, getAgeState(), shitType);
		}
	}
	
	public void kick() {
		int blowLevel[] = {-4, -3, -2};
		kick(0, blowLevel[getBodyAgeState().ordinal()]*2, blowLevel[getBodyAgeState().ordinal()]);
		strikeByPunish();
	}
	
	public void putStress(int numOfBody) {
		if (dead || (getAge() % 10 != 0) || (ConstantValues.HEADAGELIMIT >= numOfBody)) {
			return;
		}
		damage += rnd.nextInt(numOfBody - ConstantValues.HEADAGELIMIT);
		damageState = checkDamageState();
	}

	public void takeOkazari() {

		okazariType = null;

		if (isIdiot()) return;

		if (!dead) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.NoAccessory));
			setHappiness(Happiness.VERY_SAD);
			addStress(1000);
		}
	}

	public void giveOkazari(OkazariType type) {

		if (isIdiot() && type == OkazariType.DEFAULT) {
			okazariType = TrashUtil.getRandomOkazari(getAgeState());
			return;
		} else {
			okazariType = type;
			if (!dead && !isIdiot()) {
				if(okazariType == OkazariType.DEFAULT) {
					setHappiness(Happiness.VERY_HAPPY);
					addStress(-1000);
				} else {
					setHappiness(Happiness.SAD);
					addStress(-100);
				}
			}
		}
	}
	
	public int getTrashOkazariOfsX() {
		return okazariType.ofsX[getAgeState().ordinal()] + TRASH_OKAZARI_OFS_X[getAgeState().ordinal()];
	}

	public int getTrashOkazariOfsY() {
		return okazariType.ofsY[getAgeState().ordinal()] + TRASH_OKAZARI_OFS_Y[getAgeState().ordinal()];
	}

	public void takePants() {
		setHasPants(false);
	}
	
	public void givePants() {
		setHasPants(true);
		if (!dead) {
			if (!dirty && hasOkazari()) {
				setHappiness(Happiness.HAPPY);
				addStress(-200);
			}
		}
	}

	public void giveJuice(){
		if (dead) {
			return;
		}
		if (!sleeping && !isCantDie() && !isTalking()) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Healing));
			stay();
		}
		damage = 0;
		damageState = checkDamageState();
		hungry = 0;
		hungryState = checkHungryState();
		angry = false;
		scare = false;
		exciting = false;
		if(panicType == PanicType.BURN) {
			removeAttachment(Fire.class, true);
		}
		setHappiness(Happiness.AVERAGE);
		setStress(0);
		setForcePanicClear();
		clearActions();
	}
	
	public void sellYukkuri(){
		Cash.sellYukkuri(this);
		this.remove();
	}
	
	// 縺ゅ↓繧�ｋ髢蛾事
	public void invAnalClose() {
		analClose = !analClose;
		setAnalClose(analClose);
	}
	public boolean getAnalClose() {
		return analClose;
	}
	public void setAnalClose(boolean flag) {
		if(dead) {
			return;
		}
		analClose = flag;
		// 蟇昴※縺�◆繧峨Μ繧｢繧ｯ繧ｷ繝ｧ繝ｳ縺ｪ縺�
		if(sleeping) {
			return;
		}
		if(analClose) {
			// 髢蛾事
			setHappiness(Happiness.HAPPY); //Why does this make it happy...
			setMessage(MessagePool.getMessage(this, MessagePool.Action.AnalSealed));
		} else {
			// 髢区叛
			setHappiness(Happiness.AVERAGE);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Sukkiri));
			stay();
		}
	}

	// 闌主悉蜍｢
	public void invStalkCastration() {
		stalkCastration = !stalkCastration;
		setStalkCastration(stalkCastration);
	}
	public boolean getStalkCastration() {
		return stalkCastration;
	}
	public void setStalkCastration(boolean flag) {
		if(dead) {
			return;
		}
		stalkCastration = flag;
		// 蟇昴※縺�◆繧峨Μ繧｢繧ｯ繧ｷ繝ｧ繝ｳ縺ｪ縺�
		if(sleeping) {
			return;
		}
		if(stalkCastration) {
			// 髢蛾事
			setHappiness(Happiness.VERY_SAD);
			addStress(400);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Alarm));
		} else {
			// 髢区叛
			setHappiness(Happiness.AVERAGE);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Sukkiri));
		}
	}

	// 閭守函蜴ｻ蜍｢
	public void invBodyCastration() {
		bodyCastration = !bodyCastration;
		setBodyCastration(bodyCastration);
	}

	public boolean getBodyCastration() {
		return bodyCastration;
	}
	
	public void setBodyCastration(boolean flag) {
		if(dead) {
			return;
		}
		bodyCastration = flag;
		// 蟇昴※縺�◆繧峨Μ繧｢繧ｯ繧ｷ繝ｧ繝ｳ縺ｪ縺�
		if(sleeping) {
			return;
		}
		if(bodyCastration) {
			// 髢蛾事
			reduceComplacencyTimer += 500;
			setHappiness(Happiness.VERY_SAD);
			addStress(400);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Alarm));
		} else {
			// 髢区叛
			setHappiness(Happiness.AVERAGE);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Sukkiri));
		}
	}
	
	// 轣ｫ繧偵▽縺代ｋ
	public void giveFire() {
		if(burned || PanicType.BURN == panicType || crashed) {
			return;
		}
		if(!dead) {
			// 蟇昴※縺溘ｉ襍ｷ縺阪ｋ
			if(sleeping) wakeup();
			staycount = 0;
			staying = false;
			setToFood(false);
			toSukkiri = false;
			setToShit(false);
			shitting = false;
			birth = false;
			angry = false;
			furifuri = false;
			setEating(false);
			peropero = false;
			sukkiri = false;
			scare = false;
			setEatingShit(false);
			nobinobi = false;
			setHappiness(Happiness.VERY_SAD);
		}
		panicType = PanicType.BURN;
		wet = false;
		wetPeriod = 0;
		getAttach().add(new Fire(this));
	}
	

	
	public void setupBadgeTest(int badgeType) // 0=fake, 1=bronze, 2=silver, 3=gold,  //TODO: use timer to delay tests so they don't overlap. Could also lock to a clocktick and map out the actions
	{
		int testScore = 0;
		int passingScore = 0;
		int isRude = 0;
		String readyFakeTest;
		staycount = 0;
		staying = false;
		setToFood(false);
		toSukkiri = false;
		setToShit(false);
		shitting = false;
		birth = false;
		angry = false;
		furifuri = false;
		setEating(false);
		peropero = false;
		sukkiri = false;
		scare = false;
		setEatingShit(false);
		nobinobi = false;
		if (isSleeping())
		{
			wakeup();
		}

		switch(badgeType)
		{
		case 0: //testing for fake
			// Stage 1, Setup the Test
			clearActions();
			if (getIntelligence() == Intelligence.WISE)
			{
				String tooSmart = getNameE() + " doesn't want a fake mister badge like that!";
				String tooSmartRude = getNameE() + " doesn't want a fake mister badge like that! Go die easy!";
				if (isRude()){
					setMessage(tooSmartRude, 30, true, true);
				}
				else
				{
					setMessage(tooSmart, 30, true, true);
				}
				setAngry();
				stay(30);
				break;
			}
			passingScore = 0; //set requires score to receive badge
			if (isRude())
			{
			 readyFakeTest = getNameE() + " deserves a badge so that shitty slave has to give " + getNameE() + " anything " + getNameE()+ " wants!";
			 isRude=1;
			}
			else
			{
			 readyFakeTest = getNameE() + " is ready to become a badged yukkuri and take it even easier!";
			 isRude=0;
			}
			setMessage(readyFakeTest, 60, false, true); //Deliver the previously defined dialog.  
			stay(210);
			testBadge =1; // determines which badge we're testing for in the next section
			isBusy=true;			
			break;
		}
	}
	
	public void runBadgeTest(int localSwitch){  //TODO Placeholder
		String demoStretchAdult = "Demonstrating Stretch~Stretch~!";
		String demoStretchBaby = "Demonshtwating Stwetch~Stwetch~ eajy!";
		switch (localSwitch){
		
		
	case 1:
		localCounter++;
		stay(50);
		if (localCounter > 60 && localStep ==0)
		{
			//Determine dialog as either adult or baby
			if (isAdult()){
				setMessage(demoStretchAdult, 60, true, false);
		}
			else {
				setMessage(demoStretchBaby, 60, true, true);
		}
			clearActions();
			stay(60);
				nobinobi = true;
				stay(100);
			localCounter = 0;
			localStep ++;
		}
		if (localCounter > 70 && localStep ==1)
		{
			localStep ++;
			clearActions();
			localCounter  =0;
			stay(50);
			String successRude = "Mister badge is all " + getNameE() + "'s , so bring sweet-sweets old geezer!"; //placeholder to show success
			String success = "Mister badge is all " + getNameE() + "'s! Take it easy!"; //placeholder to show success
			if (isRude()){
			setMessage(successRude, 30, true, false);  
			}
			else
			{
				setMessage(success, 30, true, true);  
			}
			addAttachment(new FakeBadge((Body)this));


		}
		if (localCounter > 40 && localStep ==2)
		{
			localStep =0; //Reset all the old variables so this doesn't get checked anymore, and so a second test works properly.
			testBadge = 0;
			isBusy=false;
		}
		break;
		
	default: 
		break;
		}
	}
		

	

	
	
	public void givePin() {
		if(dead || flyingType || crashed) {
			return;
		}
		if (pinned)
		{
			pinned = false;
			setHappiness(Happiness.AVERAGE);
			return;
		}
		if(!pinned) {
			if(sleeping) wakeup();
			staycount = 0;
			staying = false;
			setToFood(false);
			toSukkiri = false;
			setToShit(false);
			shitting = false;
			birth = false;
			angry = false;
			furifuri = false;
			setEating(false);
			peropero = false;
			sukkiri = false;
			scare = false;
			setEatingShit(false);
			nobinobi = false;
			setHappiness(Happiness.VERY_SAD);
			pinned = true;
			return;
		}
		return;
	}

	// 豌ｴ繧偵°縺代ｋ
	public void giveWater() {
		
		if(!dead) {
			// 蟇昴※縺溘ｉ襍ｷ縺阪ｋ
			if(sleeping) wakeup();
			staycount = 0;
			staying = false;
			setToFood(false);
			toSukkiri = false;
			setToShit(false);
			shitting = false;
			birth = false;
			angry = false;
			furifuri = false;
			setEating(false);
			peropero = false;
			sukkiri = false;
			scare = false;
			setEatingShit(false);
			nobinobi = false;
			// 豌ｴ縺悟ｹｳ豌励↑繧牙ｹｸ遖丞ｺｦ繧｢繝��
			if(likeWater) {
				if(panicType != PanicType.BURN) {
					setHappiness(Happiness.HAPPY);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Sukkiri));
				} 
				else {
					setHappiness(Happiness.VERY_SAD);
				}
			} 
			else {
				setHappiness(Happiness.VERY_SAD);
				if(panicType != PanicType.BURN) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Wet));
					Soak();
				}
			}
		}
		wet = true;
		onFire = false;
		wetPeriod = 0;
		if(panicType == PanicType.BURN) {
			removeAttachment(Fire.class, true);
		}
		setForcePanicClear();
	}
	
public void giveBox(){
	if(getAttachmentSize(CardboardBox.class) != 0) {
		removeAttachment(CardboardBox.class, true);
		EYESIGHT = Terrarium.MAX_X*Terrarium.MAX_Y;
		isBlind = false;
	} else {
		addAttachment(new CardboardBox((this))); //TODO work in progress
		EYESIGHT = 55;
		isBlind = true;
	}
}

	public PanicType getPanicType() {
		return panicType;
	}

	// 迺ｰ蠅�↓繧医ｋ繝代ル繝�け迥ｶ諷九�險ｭ螳�
	public void setPanic(boolean flag, PanicType pType) {
		if(isDead() || isSleeping()) return;
		
		// 雜ｳ繧翫↑縺�ｆ縺ｯ荳榊虚
		if(Tarinai.type == getType()) return;
		
		// 逋ｺ諠�Ξ繧､繝代�縺ｫ縺ｯ辟｡蜉ｹ
		if(isRaper() && isExciting()) return;
		
		if(flag) {
			// 譌｢縺ｫ繝代ル繝�け迥ｶ諷九�蝣ｴ蜷医�繧ｫ繧ｦ繝ｳ繧ｿ縺ｮ繝ｪ繧ｻ繝�ヨ縺ｮ縺ｿ
			if(panicType != null) {
				panicPeriod = 0;
				return;
			}
			panicType = pType;
			panicPeriod = 0;
			staycount = 0;
			exciting = false;
			staying = false;
			setToFood(false);
			toSukkiri = false;
			setToShit(false);
			shitting = false;
			birth = false;
			angry = false;
			furifuri = false;
			setEating(false);
			peropero = false;
			sukkiri = false;
			scare = false;
			setEatingShit(false);
			nobinobi = false;
			setHappiness(Happiness.VERY_SAD);
		} else {
			panicType = null;
			panicPeriod = 0;
			setHappiness(Happiness.SAD);
		}
	}
	
	public void setForcePanicClear() {
		panicType = null;
		panicPeriod = 0;
	}
	
	public void Bathing() //TODO add pools so this can actually happen
	{
		wetCurrently = true;
		dirty = false; 
		setHappiness(Happiness.HAPPY);

		if (wetCount ==0){
			//setMessage(MessagePool.getMessage(this, MessagePool.Action.Bathing); //TODO add dialog
		}
		return;
	}
	
	public void Soak()  //TODO tweak timer so that this doesnt last so long, have it contribute to melting
	{
		//wetCurrently = true;
		dirty = false; 
		setHappiness(Happiness.VERY_SAD);
		return;
	}

	// 螢ｰ謗帙￠
	
	public boolean partnerIsDistressed()
	{
		boolean IsDistressed = true;
		if (getPartner()==null) // catch null pointer exception
		{
			IsDistressed = false;
			return IsDistressed;
		}
		else if (!getPartner().isFeelPain() && !getPartner().isDamaged())
		{
			IsDistressed = false;
			return IsDistressed;
		}
		return IsDistressed; //true
	}
	
	public boolean yukkuriIsDistressed()
	{
		boolean IsDistressed = true;
		if (isSick() && isRude())
		{
			IsDistressed = false;
			return IsDistressed;
		}
		else if (!isFeelPain() && !isDamaged())
		{
			IsDistressed = false;
			return IsDistressed;
		}
		return IsDistressed; //true
	}
	
	/*
	public boolean sisterIsDistressed() //TODO, need a way to actually reference sisters
	{
		boolean IsDistressed = true;
		if ( && isRude())
		{
			IsDistressed = false;
			return IsDistressed;
		}
		else if (!isFeelPain() && !isDamaged())
		{
			IsDistressed = false;
			return IsDistressed;
		}
		return IsDistressed;
	}
	*/
	
	/*
	public boolean childIsDistressed() //TODO, need a way to actually reference sisters
	{
		boolean IsDistressed = true;
		if (this && isRude())
		{
			IsDistressed = false;
			return IsDistressed;
		}
		else if (!isFeelPain() && !isDamaged())
		{
			IsDistressed = false;
			return IsDistressed;
		}
		return IsDistressed;
	}
	*/
	
	public boolean isRebellious(){
		return complacency==Complacency.REBELLIOUS;
	}
	
	public boolean familyIsDistressed()
	{
		return (getAttitude() !=Attitude.SUPER_SHITHEAD && (partnerIsDistressed() || yukkuriIsDistressed())); //TODO, add sister & child
	}
	
	public boolean isPraiseLimited()
	{
		return isPraiseLimited;
	}
	
	
	public void voiceReaction(int type) {
		if(panicType != null || dead) {
			return;
		}
		switch(type) {
		case 0:{
			if (isPraiseLimited()){
				break;
			}
			if (!isRebellious() && !familyIsDistressed()) //TODO move to external method for cleanliness and ease of mainteinence.
			{
			clearActions();
			scare=false;
			angry=false;
			furifuri = false;
			if(!isRaper()) exciting = false;
			nobinobi = false;
			excitingPeriod = 0;
			noDamagePeriod = 0;
			noHungryPeriod = 0;
			relax=true;
			String ysn = "Take it easy!";  
			String ysnBaby = "Chake id eajy!";
			if (isAdult()){
				setMessage(ysn, 30);
			}
			else
			{
			setMessage(ysnBaby, 30);
			}
			addStress(-100);
			praiseLimitTimer = 350 ; 
			increaseComplacencyTimer += 100 - 5*praiseUsed;
			praiseUsed += 1;
			wakeup();
			break;
			}
			
			else
			{
			double dialogSwitch = Math.random();
			if (isRebellious() && !familyIsDistressed()){
			if (dialogSwitch > .49)
			{
				String ysnLiar = "Liar mister can't take it easy...";  
				setMessage(ysnLiar);
			}
			if (dialogSwitch < .5)
			{
				String uneasyMister = "Uneasy mister can't be trusted...";
				setMessage(uneasyMister);
			}
			}
			else if (partnerIsDistressed()){
			if (dialogSwitch > .49)
			{
				String partnerMister = getNameE() + " can't take it easy with " + getPartner().getNameE() + " like this!";
				setMessage(partnerMister);
			}
			if (dialogSwitch < .5)
			{
				String partnerMister = "This is no time to take it easy, " + getPartner().getNameE() + " needs help!";
				setMessage(partnerMister);
			}}
			else if (yukkuriIsDistressed()){
			if ((isFeelPain() || getPartner().isDamaged()) && dialogSwitch > .49)
			{
				String partnerMister = getNameE() + " can't take it easy with " + getPartner().getNameE() + " like this!";
				setMessage(partnerMister);
			}
			if (dialogSwitch < .5 && (getPartner().isFeelPain() || getPartner().isDamaged()))
			{
				String partnerMister = "This is no time to take it easy, " + getPartner().getNameE() + " needs help!";
				setMessage(partnerMister);
			}}
			

			addStress(100);
			praiseLimitTimer = 150; 
			reduceComplacencyTimer += 55;
			praiseUsed += 1;
			break;
			}
		}
		case 1:{
			clearActions();
			setAngry();
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Alarm));
			addStress(100);
			break;
		}
		default:
			break;
		}
	}
	
	public boolean getPullAndPush() {
		return pullAndPush;
	}

	public void setPullAndPush(boolean flag) {
		pullAndPush = flag;
	}

	public void lockSetZ(int force) {
		extForce = force;
		if(extForce == 0){
			return;
		}
		clearActions();
		setAngry();
		if(extForce < 0) {
			// 縺､縺ｶ繧�
			if(extForce < ConstantValues.extForcePushLimit[getBodyAgeState().ordinal()]) {
				// 蝨ｧ豁ｻ
				lockmove = false;
				extForce = 0;
				setZ(0);
				bodyBurst();
			} else if(extForce < (ConstantValues.extForcePushLimit[getBodyAgeState().ordinal()] >> 1)) {
				// 髯千阜
				if(rnd.nextInt(10) == 0){
					setHappiness(Happiness.VERY_SAD);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Press2), ConstantValues.HOLDMESSAGE, true, true);
					addStress(10);
				}
				if(rnd.nextInt(80) == 0){
					int ofsX = Translate.invertX(getCollisionX()>>1, y);
					if(getDirection() == Direction.LEFT) ofsX = -ofsX;
					SimYukkuri.mypane.terrarium.addVomit(getX() + ofsX, getY() + 2, getZ(), getBodyAgeState(), shitType);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Vomit), 30);
				}
			} else {
				if(rnd.nextInt(10) == 0){
					setHappiness(Happiness.AVERAGE);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Press));
				}
			}
		} else if(extForce > 0) {
			// 縺ｲ縺｣縺ｱ繧�
			if(extForce > ConstantValues.extForcePullLimit[getBodyAgeState().ordinal()]) {
				// 縺｡縺弱ｌ
				extForce = 0;
				lockmove = false;
				bodyCut();
			} else if(extForce > ConstantValues.extForcePullLimit[getBodyAgeState().ordinal()] >> 1){
				// 髯千阜
				if(rnd.nextInt(10) == 0) {
					setHappiness(Happiness.VERY_SAD);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Pull2), ConstantValues.HOLDMESSAGE, true, true);
					addStress(10);
				}
			} else {
				if(rnd.nextInt(10) == 0){
					setHappiness(Happiness.AVERAGE);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Pull));
				}
			}
		}
	}

	public void releaseLockNobinobi() {
		if(extForce == 0) return;
		if(extForce < 0) {
			extForce = 0;
		} else if(extForce*2/3 <getSize()) {
			strike(extForce*100*24/getSize());
			extForce = 0;
		} else{
			strikeByHammer();
			setDirty(true);
			extForce = 0;
		}
		return;
	}

	public void runAway(int fromX, int fromY) {
		if (dead || sleeping || exciting || angry) {
			return;
		}
		int toX, toY;
		if (x > fromX) {
			toX = Terrarium.MAX_X;
		}
		else {
			toX = 0;
		}
		if (y > fromY) {
			toY = Terrarium.MAX_Y;
		}
		else {
			toY = 0;
		}
		moveTo(toX, toY);
		clearActions();
		scare = true;
	}

	// 繧｢繧ｿ繝�メ繝｡繝ｳ繝郁ｿｽ蜉�
	public void addAttachment(Attachment at) {
		getAttach().add(at);
	}
	
	// 謖�ｮ壹け繝ｩ繧ｹ縺ｮ繧｢繧ｿ繝�メ繝｡繝ｳ繝域焚蜿門ｾ�
	public int getAttachmentSize(Class<?> type) {
		int ret = 0;
		for(Attachment at :getAttach()) {
			if(at.getClass().equals(type)) {
				ret++;
			}
		}
		return ret;
	}

	// 謖�ｮ壹け繝ｩ繧ｹ縺ｮ繧｢繧ｿ繝�メ繝｡繝ｳ繝磯勁蜴ｻ
	public void removeAttachment(Class<?> type, boolean all) {

		Iterator<Attachment> itr = getAttach().iterator();
		while(itr.hasNext()){
			Attachment at = itr.next();
			if(at.getClass().equals(type)) {
				itr.remove();
				if(!all) break;
			}
		}
	}

	@Override
	public void remove() {
		removed = true;
		parents[Parent.PAPA.ordinal()] = null;
		parents[Parent.MAMA.ordinal()] = null;
		setPartner(null);
		getAttach().clear();
	}
	
	private void clearRelation() {
		if (parents[Parent.PAPA.ordinal()] != null)
			if (parents[Parent.PAPA.ordinal()].removed)
				parents[Parent.PAPA.ordinal()] = null;
		if (parents[Parent.MAMA.ordinal()] != null)
			if (parents[Parent.MAMA.ordinal()].removed)
				parents[Parent.MAMA.ordinal()] = null;
		if (getPartner() != null)
			if (getPartner().removed)
				setPartner(null);
	}
	
	public void clearActions() {
// TODO
//		if(ride){
//			if(ridingObj.getcurrent_condition() >1){
//				return;
//			}
//			ride=false;
//		}
		toSukkiri = false;
		nobinobi = false;
		setToBed(false);
		setToFood(false);
		setToShit(false);
		toBody = false;
		if(currentEvent != null){
			currentEvent.end(this);
		}
		currentEvent = null;
		
		moveTarget = null;
		forceFace = -1;
		dropShadow = true;
		targetPosOfsX = 0;
		targetPosOfsY = 0;
		targetBind = false;
	}
	


	public void clearEvent() {
		if(currentEvent != null){
			currentEvent.end(this);
		}
		currentEvent = null;
		forceFace = -1;
		dropShadow = true;
	}

	public void rapidPregnantPeriod(){
		if ( hasBabyOrStalk() ){
			pregnantPeriodBoost += TICK;
		}
	}
	
	public void rapidExcitingDiscipline(){
		if (excitingDiscipline > 0){
			excitingDiscipline -= TICK;
		}
	}
	
	public void rapidShit(){
		shitBoost += TICK*5;
	}
	
	@Override
	public int getValue() {
		return value[getBodyAgeState().ordinal()];
	}

	public void setCantDie() {
		cantDiePeriod = 3;
	}
	
	public void toDead(){
		if (!isCantDie() && !dead) {
			dead = true;
		}
	}

	public void revival(){
		if ( dead == true ) {
			dead = false;
			crashed = false;
			giveJuice();
		}
	}

	public void disPlantStalks() {
		for ( Stalk s : stalks ){
			if ( s != null ){
			s.setPlantYukkuri( null );
			}
		}
		stalks.clear();
	}
	
	public void setAgeState( AgeState setAgeState ) {
		setBodyAgeState(setAgeState);
		if ( setAgeState == AgeState.BABY ){
			setAge(0);
		}else if ( setAgeState == AgeState.CHILD ){
			setAge(BABYLIMIT);
		}else if ( setAgeState == AgeState.ADULT ){
			setAge(CHILDLIMIT);
		}
	}
	
	public int getMessageTextSize(){
		return messageTextSize;
	}
	
	public Color getMessageTextColor(){
		return messageTextColor;
	}
	
	public Color getMessageBoxColor(){
		return messageBoxColor;
	}

	public Color getMessageLineColor(){
		return messageLineColor;
	}

	public float getMessageLineWidth(){
		return messageWindowLine;
	}
	
	// 閭ｴ菴薙�繝吶�繧ｹ繧ｰ繝ｩ繝輔ぅ繝�け繧定ｿ斐☆
	// mode[0] 豁｣髱｢蜷代″縺区ｨｪ蜷代″縺�
	public int getBodyBaseImage(Image[] layer, int[] mode) {
		int direction = getDirection().ordinal();
		int idx = 0;
		
		mode[0] = 0;
		mode[1] = 0;
		mode[2] = 0;

		if ( isShitting() || isBirth() && babyTypes.size() > 0) {
			// 謗呈ｳ��蜃ｺ逕｣譎�
			if(analClose) {
				layer[idx] = getImage(ConstantValues.FRONT_SHIT_SEALED,ConstantValues.LEFT);
			} else {
				layer[idx] = getImage(ConstantValues.FRONT_SHIT,ConstantValues.LEFT);
			}
			idx++;

			if(hasPants()) {
				layer[idx] = getImage(ConstantValues.FRONT_PANTS,ConstantValues.LEFT);
				idx++;
			}
			if(okazariType == OkazariType.DEFAULT) {
				layer[idx] = getImage(ConstantValues.ROLL_ACCESSORY,ConstantValues.LEFT);
				idx++;
			}
		}
		else if (isFurifuri() && !isCantMove()) {
			// 縺ｵ繧翫�繧�
			int dir = ConstantValues.LEFT;
			if (getAge() % 8 <= 3) {
				dir = ConstantValues.LEFT;
			}
			else if (getAge() % 8 <= 7) {
				dir = ConstantValues.RIGHT;
			}
			
			if(analClose) {
				layer[idx] = getImage(ConstantValues.ROLL_SHIT_SEALED, dir);
			} else {
				layer[idx] = getImage(ConstantValues.ROLL_SHIT, dir);
			}
			idx++;

			if(hasPants()) {
				layer[idx] = getImage(ConstantValues.ROLL_PANTS,dir);
				idx++;
			}
			if(okazariType == OkazariType.DEFAULT) {
				layer[idx] = getImage(ConstantValues.ROLL_ACCESSORY,ConstantValues.LEFT);
				idx++;
			}
		} else if (isCrashed()) {
			// 貎ｰ繧後◆豁ｻ菴�
			if(isBurned()) {
				layer[idx] = getImage(ConstantValues.BURNED2,ConstantValues.LEFT);
				idx++;
			} else {
				if(okazariType == OkazariType.DEFAULT) {
					layer[idx] = getImage(ConstantValues.CRUSHED,ConstantValues.LEFT);
				} else {
					layer[idx] = getImage(ConstantValues.CRUSHED2,ConstantValues.LEFT);
				}
				idx++;
			}
		} else if(isBurned() && isDead()) {
			// 辟ｼ豁ｻ菴�
			layer[idx] = getImage(ConstantValues.BURNED,ConstantValues.LEFT);
			idx++;
		} else {
			// 騾壼ｸｸ譎�
			layer[idx] = getImage(ConstantValues.BODY, direction);
			idx++;
			if(okazariType == OkazariType.DEFAULT) {
				layer[idx] = getImage(ConstantValues.ACCESSORY, direction);
				idx++;
			} else if(okazariType != null) {
				layer[idx] = TrashUtil.getOkazariImage(okazariType, direction);
				idx++;
			}
			mode[0] = 1;
		}
		return idx;
	}

	// 鬘斐げ繝ｩ繝輔ぅ繝�け繧定ｿ斐☆
	public int getFaceImage(Image[] layer, int[] mode) {
		int direction = getDirection().ordinal();
		int idx = 0;
		
		// 霍ｳ縺ｭ縺ｪ縺�
		mode[0] = 0;
		
		// mode縺ｯ遘ｻ蜍暮未菫ゅ�險ｭ螳�
		if(flyingType) {
			if(!isGrabbed() && !isSleeping()) {
				if(isExciting()) {
					mode[0] = 1;	// 螟ｧ繧ｸ繝｣繝ｳ繝�
				} else if(isSukkiri()) {
					mode[0] = 2;	// 縺吶▲縺阪ｊ
				} else if (isNobinobi()) {
					mode[0] = 4;	// 縺ｮ縺ｳ縺ｮ縺ｳ
				} else if (!isCantMove() && getCriticalDamegeType() != CriticalDamegeType.CUT && canflyCheck()) {
					mode[0] = 3;	// 霍ｳ縺ｭ縺ｦ遘ｻ蜍�
				}
			}
		} else {
			if(!isGrabbed() && getZ() == 0 && !isSleeping()) {
				if(isExciting() && getFootBakeLevel() == FootBake.NONE) {
					mode[0] = 1;	// 螟ｧ繧ｸ繝｣繝ｳ繝�
				} else if(isSukkiri()) {
					mode[0] = 2;	// 縺吶▲縺阪ｊ
				} else if (isNobinobi()) {
					mode[0] = 4;	// 縺ｮ縺ｳ縺ｮ縺ｳ
				} else if (!isCantMove() && getFootBakeLevel() == FootBake.NONE && getCriticalDamegeType() != CriticalDamegeType.CUT
						&& !isDamaged() && !isSick() && !isFeelPain() && linkParent == null) {
					mode[0] = 3;	// 霍ｳ縺ｭ縺ｦ遘ｻ蜍�
				}
			}
		}
		
		// 陦ｨ諠�崋螳�
		if(forceFace != -1) {
			layer[idx] = getImage(forceFace, direction);
			idx++;
			return idx;
		}
		
		if(isDead()) {
			layer[idx] = getImage(ConstantValues.DEAD,direction);
			idx++;
		} else if(getCriticalDamege() == CriticalDamegeType.CUT) {
			layer[idx] = getImage(ConstantValues.PAIN,direction);
			idx++;
		} else if(isExciting()) {
			layer[idx] = getImage(ConstantValues.EXCITING,direction);
			idx++;
		} else if(isSleeping()) {
			layer[idx] = getImage(ConstantValues.SLEEPING,direction);
			idx++;
		} else if(isPeroPero() || isEating()) {
			if (isStrike() || isVerySad() || isFeelHardPain()) {
				layer[idx] = getImage(ConstantValues.CRYING,direction);
			} else if(isSad() || isEatingShit() || isFeelPain()) {
				layer[idx] = getImage(ConstantValues.TIRED, direction);
			} else {
				layer[idx] = getImage(ConstantValues.SMILE, direction);
			}
			idx++;
		} else if(isSukkiri()) {
			layer[idx] = getImage(ConstantValues.REFRESHED,direction);
			idx++;
		} else if(isDamaged() || isSick() || isFeelPain()) {
			if (isStrike() || isVerySad() || isFeelHardPain()) {
				layer[idx] = getImage(ConstantValues.CRYING,direction);
			} else {
				layer[idx] = getImage(ConstantValues.TIRED,direction);
			}
			idx++;
		} else {
			if(panicType != null) {
				layer[idx] = getImage(ConstantValues.CRYING,direction);
			} else if(getZ() != 0 && !isCantMove() && !(linkParent instanceof Sui)) {
				layer[idx] = getImage(ConstantValues.CHEER,direction);
			} else if(isStrike() || isVerySad()) {
				layer[idx] = getImage(ConstantValues.CRYING,direction);
			} else if(isAngry()) {
				layer[idx] = getImage(ConstantValues.PUFF,direction);
			} else if(isSad() || isOld()) {
				layer[idx] = getImage(ConstantValues.TIRED,direction);
			} else if(isHappy() || isNobinobi()) {
				layer[idx] = getImage(ConstantValues.SMILE, direction);
			} else if(isTalking() && isRude()) {
				layer[idx] = getImage(ConstantValues.RUDE,direction);
			} else if(isTalking() && !isRude()) {
				layer[idx] = getImage(ConstantValues.CHEER,direction);
			} else {
				layer[idx] = getImage(ConstantValues.NORMAL,direction);
			}
			idx++;
		}
		return idx;
	}

	// 豎壹ｌ縺ｪ縺ｩ縺ｮ繧ｨ繝輔ぉ繧ｯ繝医げ繝ｩ繝輔ぅ繝�け繧定ｿ斐☆
	public int getEffectImage(Image[] layer, int[] mode) {
		int direction = getDirection().ordinal();
		int idx = 0;
		
		mode[0] = 0;
		
		//EFFECTS OVERLAYS GO HERE
		
		//burn effects
		
		if(burned && cooked && burnedDontMove) {
			layer[idx] = getImage(ConstantValues.BURN, direction);
			idx++;
		} else if(burned && !burnedDontMove && isOnFire()) {
			layer[idx] = getImage(ConstantValues.BURN01, direction);
			idx++;
		}


		// 雜ｳ辟ｼ縺�
		FootBake f = getFootBakeLevel();
		if(f == FootBake.MIDIUM ) {
			layer[idx] = getImage(ConstantValues.FOOT_BAKE0, direction);
			idx++;
		} else if(f == FootBake.CRITICAL) {
			layer[idx] = getImage(ConstantValues.FOOT_BAKE1, direction);
			idx++;
		}
		// 繝�Γ繝ｼ繧ｸ
		if(damage > DAMAGELIMIT[getBodyAgeState().ordinal()] * 0.75f) {
			layer[idx] = getImage(ConstantValues.DAMAGED2, direction);
			idx++;
		} else if(damage > DAMAGELIMIT[getBodyAgeState().ordinal()] * 0.5f) {
			layer[idx] = getImage(ConstantValues.DAMAGED1, direction);
			idx++;
		} else if(damage > DAMAGELIMIT[getBodyAgeState().ordinal()] * 0.25f) {
			layer[idx] = getImage(ConstantValues.DAMAGED0, direction);
			idx++;
		} else if(isOld()) {
			layer[idx] = getImage(ConstantValues.DAMAGED2, direction);
			idx++;
		}
		// 縺翫￥繧九∩
		if(hasPants()) {
			layer[idx] = getImage(ConstantValues.PANTS, direction);
			idx++;
		}
		if(isPinned()) {
			layer[idx] = getImage(ConstantValues.BODYPINNED, direction);
			idx++;
		}
		// 雜ｳ豎壹ｌ
		if(isDirty()) {
			layer[idx] = getImage(ConstantValues.STAIN, direction);
			idx++;
		}
		// 縺九�
		if(sickPeriod > (INCUBATIONPERIOD << 6)) {
			layer[idx] = getImage(ConstantValues.SICK2, direction);
			idx++;
		} else if(sickPeriod > (INCUBATIONPERIOD << 3)) {
			layer[idx] = getImage(ConstantValues.SICK1, direction);
			idx++;
		} else if(sickPeriod > INCUBATIONPERIOD) {
			layer[idx] = getImage(ConstantValues.SICK0, direction);
			idx++;
		}
		// 豼｡繧�
		if(isWet()) {
			layer[idx] = getImage(ConstantValues.WET, direction);
			idx++;
		}
		return idx;
	}

	// 縺翫＆縺偵�鄒ｽ縲∝ｰｻ蟆ｾ縺ｮ繧ｰ繝ｩ繝輔ぅ繝�け繧定ｿ斐☆
	public int getBraidImage(Image[] layer, int[] mode) {
		int direction = getDirection().ordinal();
		int idx = 0;
		
		if(hasBraidCheck()) {
			// 騾壼ｸｸ
			if(canflyCheck()) {
				// 鬟幄｡檎憾諷�
				layer[idx] = getImage((int)(ConstantValues.BRAID_MV0 + ((getAge() % 6) >> 1)), direction);
				idx++;
			} else {
				if(pikopiko) {
					layer[idx] = getImage((int)(ConstantValues.BRAID_MV0 + ((getAge() % 6) >> 1)), direction);
					idx++;
				} else {
					layer[idx] = getImage(ConstantValues.BRAID, direction);
					idx++;
				}
			}
		} else {
			// 遐ｴ螢顔憾諷�
			layer[idx] = getImage(ConstantValues.BRAID_CUT, direction);
			idx++;
		}
		return idx;
	}

	// 繧､繝吶Φ繝医↓蜿榊ｿ懊〒縺阪ｋ迥ｶ諷九°繝√ぉ繝�け縺吶ｋ
	// 繧､繝吶Φ繝医�驥崎ｦ∝ｺｦ縺ｧ蟇昴※縺�※繧りｵｷ縺阪◆繧翫〒縺阪ｋ繧医≧縺ｫ縺吶ｋ縺溘ａ
	// 縺薙％縺ｧ縺ｯ蜍輔＞縺溘ｉ隕九◆逶ｮ縺ｫ縺翫°縺励￥縺ｪ繧狗憾豕√�縺ｿ繝√ぉ繝�け
	private boolean canEventResponse() {
		// 豁ｻ菴薙�辟｡逅�
		if(dead) return false;
		if(getCriticalDamege() == CriticalDamegeType.CUT) return false;
		// 縺�ｓ縺�ｓ逶ｴ蜑阪�蜍輔￠縺ｪ縺�憾諷九�辟｡逅�
		if(shitting) return false;
		// 蜃ｺ逕｣逶ｴ蜑阪�蜍輔￠縺ｪ縺�憾諷九�辟｡逅�
		if(birth) return false;
		// 縺吶▲縺阪ｊ繧｢繝九Γ繝ｼ繧ｷ繝ｧ繝ｳ荳ｭ縺ｯ辟｡逅�
		if(sukkiri) return false;
		// 縺ｺ繧阪⊆繧堺ｸｭ繧ら┌逅�
		if(peropero) return false;
		// 莉悶�繧､繝吶Φ繝亥ｮ溯｡御ｸｭ繧ら┌逅�
		if(currentEvent != null) return false;

		return true;
	}
	
	public EventPacket getCurrentEvent() {
		return currentEvent;
	}
	
	public boolean isDropShadow() {
		return dropShadow;
	}

	public void setDropShadow(boolean val) {
		dropShadow = val;
	}

/*
	public void rideOn(Sui s){// TODO
		ride=true;
		ridingObj=s;
	}
ride = fav
riding = bind
	public void rideObjBind(Sui s){
		rideObj = s;
	}
	
	public Sui getrideObj(){
		if(rideObj != null && (rideObj.isRemoved() || rideObj.getbindobj() != this)){
			rideObj=null;
		}
		return rideObj;
	}
	
	public boolean isride(){
		return ride;
	}
	
	public void rideOff(){
		ride=false;
		ridingObj=null;
	}
*/	
	public Obj getTarget(){
		return moveTarget;
	}

	@Override
	public void grab() {
		grabbed = true;
		if ( bindStalk != null ){
			if ( bindStalk.getPlantYukkuri() != null ) {
				if ( !bindStalk.getPlantYukkuri().isDead() ) {
					bindStalk.getPlantYukkuri().setHappiness(Happiness.VERY_SAD);
					bindStalk.getPlantYukkuri().setMessage(MessagePool.getMessage(bindStalk.getPlantYukkuri(), MessagePool.Action.AbuseBaby));
					addStress(500);
				}
			}
			bindStalk.getBindBaby().set( bindStalk.getBindBaby().indexOf( this ), null );
			setBindStalk( null );
		}
	}

	// calling every tick
	@Override
	public Obj.Event clockTick() {
		
		// if removed, remove body
		if (removed) {
			disPlantStalks();
			return Obj.Event.REMOVED;
		}

		int i = 0;
		Attachment at;
		while(i < getAttach().size()) {
			at = getAttach().get(i);
			if(at.clockTick() == Event.REMOVED) {
				getAttach().remove(i);
			} else {
				i++;
			}
		}

		// if partner and parents are removed, clean relationship.
		clearRelation();
		
		// if dead, do nothing.
		if (dead) {
			clearActions();
			Moving.moveBody(this, true); // for falling the body
			checkMessage();
			return Obj.Event.DEAD;
		}

		Event retval = Event.DONOTHING;

		if(Terrarium.getInterval() == 0) {
			if(Terrarium.ageBoostSteam && getBodyAgeState() != AgeState.ADULT) addAge(10000);
			if(Terrarium.ageStopSteam) addAge(-256);
		}
		// check age
		setAge(getAge() + TICK);
		if (getAge() > LIFELIMIT) {
			toDead();
			Moving.moveBody(this, true); // for falling the body
			checkMessage();
			if (dead) {
				return Obj.Event.DEAD;
			}
		}
		// 蟷ｴ鮨｢繝√ぉ繝�け
		AgeState curAge = getBodyAgeState();
		FootBake foot = getFootBakeLevel();
		setBodyAgeState(checkBodyAgeState());
		if(!curAge.equals(getBodyAgeState())) {
			// 蜉�ｽ｢
			initAmount(getBodyAgeState());
			// DamageLimit繧呈ｵ∫畑縺励※繧九ヱ繝ｩ繝｡繝ｼ繧ｿ縺ｯ迥ｶ諷九ｒ邯ｭ謖√☆繧九◆繧√％縺薙〒蜀崎ｨ育ｮ�
			switch(foot) {
				case MIDIUM:
					footBakePeriod = (DAMAGELIMIT[getBodyAgeState().ordinal()] >> 1) + 1;
					break;
				case CRITICAL:
					footBakePeriod = DAMAGELIMIT[getBodyAgeState().ordinal()] + 1;
					break;
				default:
					break;
			}
		}
		setMindAgeState(checkMindAgeState());

		boolean dontMove = false;

		// 繝代ル繝�け譎ゅ�縺溘□襍ｰ繧�
		if(panicType != null) {
			switch(panicType) {
				case BURN:
					break;
				default:
					retval = checkFear();
					break;
			}
			if (lockmove || getCriticalDamege() != null || (getFootBakeLevel() == FootBake.CRITICAL && !canflyCheck())) {
				dontMove = true;
			}
			setHappiness(Happiness.VERY_SAD);
			checkDamage();
			checkStress();
			checkComplacency();
			checkSick();
			checkCantDie();
			Moving.moveBody(this,dontMove);
			return retval;
		}
		// check status
		if (!isBusy()){
			checkHungry();
			checkDamage();
			checkStress();
			checkComplacency();
			checkSick();
			checkCantDie();
			checkPinned();
			checkWetState();
			checkBurntState();
		}
		if (testBadge > 0){ 
			runBadgeTest(testBadge);
		}
	
		// check events
		int oldShit = getShit();
		// check shit
		if (checkShit() || getCriticalDamege() != null || (getFootBakeLevel() == FootBake.CRITICAL && !canflyCheck())) {
			dontMove = true;
		}
		if ( shitting == false && oldShit != 0 && getShit() == 0) {
			if (!hasPants()) {
				retval = Obj.Event.DOSHIT;
			}
		}
		

		// check pregnant
		boolean oldHasBaby = hasBabyOrStalk();
		if (checkChildbirth()) {
			dontMove = true;
		}

		if (oldHasBaby == true && hasBabyOrStalk() == false || hasBabyOrStalk() == false && birth == true) {
			if ( stalks.size() <= 0 && hasStalk == true){
				hasStalk = false;
			}
			retval = Obj.Event.BIRTHBABY;
		}
		
		if ( isBurst() ){
			toDead();
			Moving.moveBody(this, true); // for falling the body
			checkMessage();
			if (dead){
				bodyBurst();
				return Obj.Event.DEAD;
			}
		}

		// check wet
		checkWet();
		
		// check sleep
		if (checkSleep()) {
			dontMove = true;
		}

		// check relax
		if (lockmove || isFurifuri()) {// 蜍輔￠縺ｪ縺�
			dontMove = true;
		}
		
		// if eating, cannot move.
		if (isEating()) {
			dontMove = true;
		}

		// check relax and excitement
		if (!isBusy()){
		checkEmotion();
		}

		// check discipline level
		checkDiscipline();

		if (staying) {
			staycount += TICK;
			if (staycount > stayTime) {
				staycount = 0;
				staying = false;
			}
			else {
				dontMove = true;
			}
		}
		
		// 繧､繝吶Φ繝医↓蜿榊ｿ懊〒縺阪ｋ迥ｶ諷九°繝√ぉ繝�け
		if(canEventResponse()) {
			// 閾ｪ霄ｫ縺ｫ蜷代￠繧峨ｌ縺溘う繝吶Φ繝医�繝√ぉ繝�け
			currentEvent = EventLogic.checkBodyEvent(this);
			if(currentEvent == null) {
				// 繝ｯ繝ｼ繝ｫ繝峨う繝吶Φ繝医�繝√ぉ繝�け
				currentEvent = EventLogic.checkWorldEvent(this);
			}
			// 繧､繝吶Φ繝磯幕蟋�
			if(currentEvent != null) {
				currentEvent.start(this);
			}
		} else {
			// 繧､繝吶Φ繝亥ｿ懃ｭ斐〒縺阪↑縺��蜷医〒繧ゆｾ句､悶〒simpleAction縺�￠蜻ｼ縺ｰ繧後ｋ
			// 閾ｪ霄ｫ縺ｫ蜷代￠繧峨ｌ縺溘う繝吶Φ繝医�繝√ぉ繝�け
			EventLogic.checkSimpleBodyEvent(this);
			// 繝ｯ繝ｼ繝ｫ繝峨う繝吶Φ繝医�繝√ぉ繝�け
			EventLogic.checkSimpleWorldEvent(this);
		}

		// move to destination
		// if there is no destination, walking randomly.
		Moving.moveBody(this, dontMove);

		checkMessage();
		wetCurrently = false; //counteracts the wet from pool and prevent water timer accumulation while out of the pool
		if (!wetCurrently && wetTimer > 0)
		{
			dryTimer = dryTimer + 1;
			if (dryTimer == 7 && getDisplayableWetState() > 75)
			{
				wetTimer = wetTimer - 2;
				dryTimer = 0;
			}
			if (dryTimer == 7 && getDisplayableWetState() < 76)
			{
				wetTimer = wetTimer -1;
				dryTimer = 0;
			}
		}

		return retval;
	}
	
	//wetCurrently = false; //counteracts constant wetness, basically a countertimer


	public Body(int initX, int initY, int initZ, AgeState initAgeState, Body mama, Body papa ) {    /// Create new yukkuri stats.  Needs rewrite
		objType = Type.YUKKURI;
		x = initX;
		y = initY;
		z = initZ;
		parents[Parent.PAPA.ordinal()] = papa;
		parents[Parent.MAMA.ordinal()] = mama;
		removed = false;
		if (papa != null){   // if the yukkuri has a father, basically, if it wasn't purchased
		if (rnd.nextBoolean()) {
			setAttitude((papa != null ? papa.getAttitude() : Attitude.SHITHEAD));
		}
		else {
			setAttitude((mama != null ? mama.getAttitude() : Attitude.NICE));
			 }
		}
		else{
			switch(rnd.nextInt(8)) {
			case 0:
				setAttitude(Attitude.SUPER_SHITHEAD);
				break;
			case 1:
			case 2:
			case 3:
			case 4:

				setAttitude(Attitude.SHITHEAD);
				break;
			case 5:
			case 6:
				setAttitude(Attitude.AVERAGE);
				break;
			default:
				setAttitude(Attitude.NICE);
				break;
		}
		}

		switch(rnd.nextInt(9)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				setIntelligence(Intelligence.FOOL);
				break;
			case 5:
				setIntelligence(Intelligence.WISE);
				break;
			default:
				setIntelligence(Intelligence.AVERAGE);
				break;
		}

		if(papa != null && papa.getFavItem(ConstantValues.FavItemType.BED) != null) {
			setFavItem(ConstantValues.FavItemType.BED, papa.getFavItem(ConstantValues.FavItemType.BED));
		} else if(mama != null && mama.getFavItem(ConstantValues.FavItemType.BED) != null) {
			setFavItem(ConstantValues.FavItemType.BED, mama.getFavItem(ConstantValues.FavItemType.BED));
		}
		tuneParameters(); // Update individual parameters.
		switch (initAgeState) {
		case BABY:
			setAge(0);
			break;
		case CHILD:
			setAge(BABYLIMIT);
			break;
		case ADULT:
		default:
			setAge(CHILDLIMIT);
			break;
		}
		setAge(getAge() + rnd.nextInt(100));
		setBodyAgeState(checkBodyAgeState());
		setMindAgeState(getBodyAgeState());
		initAmount(initAgeState);
		wakeUpTime = getAge();
		setShit(rnd.nextInt(SHITLIMIT[getBodyAgeState().ordinal()]));
		if (getBodyAgeState() == AgeState.BABY) {
			if (mama != null) {
				if (mama.isDamaged()) {
					damage = rnd.nextInt(mama.getDamage());
					checkBodyAgeState();
					checkDamageState();
				}
				if (mama.isSick()) {
					setSick();	
				}
				if (mama.isDead()) {
					damage+=DAMAGELIMIT[AgeState.BABY.ordinal()]/4*3+rnd.nextInt(DAMAGELIMIT[AgeState.BABY.ordinal()]);	
				}
			}
		}
		dirX = Moving.randomDirection(this.rnd.nextBoolean(), dirX);
		dirY = Moving.randomDirection(this.rnd.nextBoolean(), dirY);

		messageTextSize = 12;
		
		uniqueID = Numbering.INSTANCE.numberingYukkuriID();
	}
	public boolean isToBed() {
		return toBed;
	}
	public void setToBed(boolean toBed) {
		this.toBed = toBed;
	}
	public int[] getVALUEPURCHASE() {
		return VALUEPURCHASE;
	}
	public void setVALUEPURCHASE(int vALUEPURCHASE[]) {
		VALUEPURCHASE = vALUEPURCHASE;
	}
	public AgeState getBodyAgeState() {
		return bodyAgeState;
	}
	public void setBodyAgeState(AgeState bodyAgeState) {
		this.bodyAgeState = bodyAgeState;
	}
	public int[] getVALUESALE() {
		return VALUESALE;
	}
	public void setVALUESALE(int vALUESALE[]) {
		VALUESALE = vALUESALE;
	}
	public ArrayList<EventPacket> getEventList() {
		return eventList;
	}
	public void setEventList(ArrayList<EventPacket> eventList) {
		this.eventList = eventList;
	}
	public boolean isToFood() {
		return toFood;
	}
	public void setToFood(boolean toFood) {
		this.toFood = toFood;
	}
	public boolean isCleanFood() {
		return cleanFood;
	}
	public void setCleanFood(boolean cleanFood) {
		this.cleanFood = cleanFood;
	}
	public void setEating(boolean eating) {
		this.eating = eating;
	}
	public void setTang(int tang) {
		this.tang = tang;
	}
	public boolean isLikeBitterFood() {
		return likeBitterFood;
	}
	public void setLikeBitterFood(boolean likeBitterFood) {
		this.likeBitterFood = likeBitterFood;
	}
	public void setStrike(boolean strike) {
		this.strike = strike;
	}
	public boolean isLikeHotFood() {
		return likeHotFood;
	}
	public void setLikeHotFood(boolean likeHotFood) {
		this.likeHotFood = likeHotFood;
	}
	public boolean isSuperRapist() {
		return superRapist;
	}
	public void setSuperRapist(boolean superRapist) {
		this.superRapist = superRapist;
	}
	public boolean isRapist() {
		return rapist;
	}
	public void setRapist(boolean rapist) {
		this.rapist = rapist;
	}
	public int getDirtyPeriod() {
		return dirtyPeriod;
	}
	public void setDirtyPeriod(int dirtyPeriod) {
		this.dirtyPeriod = dirtyPeriod;
	}
	public void setEatingShit(boolean eatingShit) {
		this.eatingShit = eatingShit;
	}
	public YukkuriType getMsgType() {
		return msgType;
	}
	public void setMsgType(YukkuriType msgType) {
		this.msgType = msgType;
	}
	public Body getPartner() {
		return partner;
	}
	public void setPartner(Body partner) {
		this.partner = partner;
	}
	public AgeState getMindAgeState() {
		return mindAgeState;
	}
	public void setMindAgeState(AgeState mindAgeState) {
		this.mindAgeState = mindAgeState;
	}
	public boolean isPin() {
		return pin;
	}
	public void setPin(boolean pin) {
		this.pin = pin;
	}
	public CriticalDamegeType getCriticalDamege() {
		return criticalDamege;
	}
	public void setCriticalDamege(CriticalDamegeType criticalDamege) {
		this.criticalDamege = criticalDamege;
	}
	public ArrayList<Attachment> getAttach() {
		return attach;
	}
	public void setAttach(ArrayList<Attachment> attach) {
		this.attach = attach;
	}
	public int getComplacencyVal() {
		return complacencyVal;
	}
	public void setComplacencyVal(int complacencyVal) {
		this.complacencyVal = complacencyVal;
	}
	public void setAttitude(Attitude attitude) {
		this.attitude = attitude;
	}
	public void setIntelligence(Intelligence intelligence) {
		this.intelligence = intelligence;
	}

	public void setShit(int shit) {
		this.shit = shit;
	}
	public boolean isHasPants() {
		return hasPants;
	}
	public void setHasPants(boolean hasPants) {
		this.hasPants = hasPants;
	}
	public boolean isToShit() {
		return toShit;
	}
	public void setToShit(boolean toShit) {
		this.toShit = toShit;
	}
}


