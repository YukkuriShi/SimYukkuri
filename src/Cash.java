package src;




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

/////    SHITARABA METHOD OF DETERMINING PURCHASE VALUE
/*	public static void buyYukkuri(Body body) {
		int val = 0;
		
		// 蝓ｺ譛ｬ萓｡蛟､
		if(body.isPredatorType()) {
			// 謐暮｣溽ｨｮ
			val = 3000;
		} else if(body.isHybrid()) {
			// 繝上う繝悶Μ繝�ラ
			val = 5000;
		} else if(body.isIdiot()) {
			// 雜ｳ繧翫↑縺�
			val = 100;
		} else if(body.getType() == DosMarisa.type) {
			// 繝峨せ
			val = 50000;
		} else if(body.getType() < 100 || body.getType() == Deibu.type
				 || body.getType() == MarisaTsumuri.type || body.getType() == MarisaKotatsumuri.type
				 || body.getType() == WasaReimu.type) {
			// 騾壼ｸｸ遞ｮ
			val = 1500;
			if(body.getType() > 100 && body.getType() != Deibu.type) {
				val += 1000;
			}
		} else if((body.getType() >= 1000 && body.getType() < 2000) || body.getType() == Kimeemaru.type) {
			// 蟶悟ｰ醍ｨｮ
			val = 15000;
		}
		
		switch(body.getAgeState()) {
			case BABY:
				val /= 3;
				break;
			case CHILD:
				val /= 2;
				break;
			case ADULT:
				val /= 1;
				break;
		}
		
		cash -= val;
	}
	*/
	
	public static void buyYukkuri(Body body) {
	int val = 0;
	val = body.VALUEPURCHASE[body.bodyAgeState.ordinal()];
	
	
	
	cash -= val;
}


	
	

	public static void sellYukkuri(Body body) {
		int val = 0;
		val = body.VALUESALE[body.bodyAgeState.ordinal()];  //New method of determining value
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

		
		cash += val;
	}
	
	public static int getYukkuriValue(Body body) {
		int val = 0;
		val = body.VALUESALE[body.bodyAgeState.ordinal()];  //New method of determining value
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

		
		return val;
	}
	
}