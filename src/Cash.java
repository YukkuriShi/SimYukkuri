package src;

import src.object.Obj;
import src.yukkuriBody.Body;

public class Cash {
	
	private static long cash = 10000;
	
	public static void setCash(long val) {
		cash = val;
	}
	
	public static long getCash() {
		return cash;
	}
	
	public static void addCash( int addcash ) {
		cash += addcash;
	}

	public static void buyItem(Obj item) {
		cash -= item.getValue();
	}
	
	public static void buyYukkuri(Body body) {
	int val = 0;
	val = body.getVALUEPURCHASE()[body.getBodyAgeState().ordinal()];
	
	
	
	cash -= val;
}

	public static void sellYukkuri(Body body) {
		int val = 0;
		val = body.getVALUESALE()[body.getBodyAgeState().ordinal()];  //New method of determining value
		if(!body.hasOkazari()) val *= 0.8;
		if(body.isBraidType() && !body.hasBraidCheck()) val *= 0.8;
		switch(body.getAttitude()) {
			case VERY_NICE:
				val *= 1.25;
				break;
			case NICE:
				val *= 1.2;
				break;
			case AVERAGE:
				val *= 1;
				break;
			case SHITHEAD:
				val *= 0.95;
				break;
			case SUPER_SHITHEAD:
				val *= 0.75;
				break;
		}
		switch(body.getIntelligence()) {
			case WISE:
				val *= 1.2;
				break;
			case AVERAGE:
				val *= 1;
				break;
			case FOOL:
				val *= 0.95;
				break;
		}
		if (body.getDamage() > 0)
		{
		val = val *(1 - (body.getDamage() /body.getDamageLimit()));
		}
		cash += val;
	}
	
	public static int getYukkuriValue(Body body) {
		int val = 0;
		val = body.getVALUESALE()[body.getBodyAgeState().ordinal()];  //New method of determining value
		if(!body.hasOkazari()) val *= 0.8;
		if(body.isBraidType() && !body.hasBraidCheck()) val *= 0.8;
		switch(body.getAttitude()) {
			case VERY_NICE:
				val *= 1.25;
				break;
			case NICE:
				val *= 1.2;
				break;
			case AVERAGE:
				val *= 1;
				break;
			case SHITHEAD:
				val *= 0.95;
				break;
			case SUPER_SHITHEAD:
				val *= 0.75;
				break;
		}
		switch(body.getIntelligence()) {
			case WISE:
				val *= 1.2;
				break;
			case AVERAGE:
				val *= 1;
				break;
			case FOOL:
				val *= 0.95;
				break;
		}
		if (body.getDamage() > 0)   // not working?
		{
		val = val *(1 - (body.getDamage() /body.getDamageLimit()));
		}
		return val;
	}	
}