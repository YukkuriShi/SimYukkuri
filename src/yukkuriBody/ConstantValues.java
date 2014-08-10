package src.yukkuriBody;

import java.awt.Color;
import java.awt.Image;

import src.Terrarium;

public  class ConstantValues {
	// public variables	
		public enum AgeState { BABY, CHILD, ADULT };
		public enum Direction { LEFT, RIGHT };
		public enum Parent { PAPA, MAMA };
		public enum PanicType { FEAR, BURN, REMIRYA };
		public enum TangType { POOR, NORMAL, GOURMET };
		public enum CriticalDamegeType { CUT };
		public enum PredatorType { BITE, SUCTION };
		public enum WindowType {NORMAL, WORLD_SEND, WORLD_RES, BODY_SEND, BODY_RES};
		public enum FavItemType {BALL, BED, SUI};
		// Facing information
		public static final int LEFT = 0;
		public static final int RIGHT = 1;
		// Expression and condition information
		public static final int BURNED = 0;
		public static final int BURNED2 = 1;
		public static final int CRUSHED = 2;
		public static final int CRUSHED2 = 3;
		public static final int ROLL_ACCESSORY = 4;
		public static final int FRONT_PANTS = 5;
		public static final int FRONT_SHIT = 6;
		public static final int FRONT_SHIT_SEALED = 7;
		public static final int ROLL_PANTS = 8;
		public static final int ROLL_SHIT = 9;
		public static final int ROLL_SHIT_SEALED = 10;
		// 讓ｪ
		public static final int BODY = 11;
		public static final int ACCESSORY = 12;
		public static final int PANTS = 13;
		public static final int BODY_CUT = 14;
		public static final int DAMAGED0 = 15;
		public static final int DAMAGED1 = 16;
		public static final int DAMAGED2 = 17;
		public static final int SICK0 = 18;
		public static final int SICK1 = 19;
		public static final int SICK2 = 20;
		public static final int FOOT_BAKE0 = 21;
		public static final int FOOT_BAKE1 = 22;
		public static final int STAIN = 23;
		public static final int WET = 24;
		public static final int LICK = 25;
		public static final int BRAID = 26;
		public static final int BRAID_CUT = 27;
		public static final int BRAID_MV0 = 28;
		public static final int BRAID_MV1 = 29;
		public static final int BRAID_MV2 = 30;
		// 鬘�
		public static final int CHEER = 31;
		public static final int CRYING = 32;
		public static final int DEAD = 33;
		public static final int EXCITING= 34;
		public static final int NORMAL = 35;
		public static final int PAIN = 36;
		public static final int PUFF = 37;
		public static final int REFRESHED = 38;
		public static final int RUDE = 39;
		public static final int SLEEPING = 40;
		public static final int SMILE = 41;
		public static final int SURPRISE = 42;
		public static final int TIRED = 43;
		///////////////////////////////////////////////
		//ADDED BY ETERNITY
		public static final int BODYPINNED = 44;
		//WETSTATE
		public static final int BODYSOAKED = 45;
		public static final int BODYMELTING = 46;
		public static final int BODYDISSOLVING = 47;
		public static final int MELTED1 = 70;    
		public static final int MELTED2 = 71; 
		public static final int MELTED1a = 72;    
		public static final int MELTED2a = 73; 
		public static final int FAKE = 74;
		public static final int BRONZE = 75;
		public static final int SILVER = 76;
		public static final int GOLD = 77;
		public static final int WHITESTEAM = 78;
				
		public static final int BURN = 98;
		public static final int BURN01 = 99;
						
		public static final int NUM_OF_CONDITIONS = 200; //MAXIMUM NUMBER OF SPRITES
	
		///////////////////////////////////////////////
		public static final Color[][] windowColor = {
			//      霈ｪ驛ｭ邱�                                                     蝪励ｊ                                                              繝�く繧ｹ繝�
			{new Color(0, 0, 0, 255), new Color(255, 255, 255, 200), new Color(0, 0, 0, 255)},
			{new Color(0, 0, 0, 255), new Color(200, 200, 255, 200), new Color(0, 0, 0, 255)},
			{new Color(0, 0, 255, 255), new Color(255, 255, 255, 200), new Color(0, 0, 0, 255)},
			{new Color(0, 0, 0, 255), new Color(255, 160, 160, 200), new Color(0, 0, 0, 255)},
			{new Color(255, 0, 128, 255), new Color(255, 255, 255, 200), new Color(0, 0, 0, 255)},
		};
		public static final Color[] negiWindowColor = {new Color(0, 0, 0, 255), new Color(255, 0, 0, 200), new Color(0, 0, 0, 255) };
		public static final float[] windowLine = {1.0f, 1.0f, 2.0f, 1.0f, 2.0f};

		// private variables
		public enum Hunger { NONE, VERY };
		public enum Damage { NONE, VERY };
		public enum Pain { NONE, SOME, VERY };
		public enum Happiness { VERY_HAPPY, HAPPY, AVERAGE, SAD, VERY_SAD };
		public enum Attitude { VERY_NICE, NICE, AVERAGE, SHITHEAD, SUPER_SHITHEAD };
		public enum Burst { NONE, SAFE, HALF, NEAR, BURST };
		public enum Intelligence { WISE, AVERAGE, FOOL };
		public enum FootBake {NONE, MIDIUM, CRITICAL };

		public static final int NEEDLE = 100;
		public static final int HAMMER = 100*24*2;
		public static final int HOLDMESSAGE = 20;		// 2sec
		public static final int STAYLIMIT = 20;		// 2sec
		public static final int HEADAGELIMIT = 100;
		public static final int SHITSTAY = 100;
		public static final int DIAGONAL = (int)Math.sqrt(Terrarium.MAX_X * Terrarium.MAX_X + Terrarium.MAX_Y * Terrarium.MAX_Y);

		// Used in image loading.
		public static final float[] bodySize = {0.25f, 0.5f, 1.0f};
		protected static final int stalkOfsX[] = {0, 1, -1, 1, 0, -1, 0, 1};
		protected static final int stalkOfsY[] = {0, 1, 0, -1, 1, -1, -1, 0};
		protected static final int extForcePullLimit[] = {20, 40, 80};
		protected static final int extForcePushLimit[] = {-10, -20, -40};
		public static final int babyIndex = AgeState.BABY.ordinal();
		public static final int childIndex = AgeState.CHILD.ordinal();
		public static final int adultIndex = AgeState.ADULT.ordinal();
	

		public static final int MAXSIZE = 128;
		
		
}
