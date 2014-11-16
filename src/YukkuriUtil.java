package src;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Random;

import src.yukkuri.*;
import src.yukkuri.Common.Marisa;
import src.yukkuri.Common.Reimu;
import src.yukkuri.Rare.Ayaya;
import src.yukkuri.Rare.Suwako;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.Attitude;
import src.yukkuriBody.ConstantValues.Intelligence;
import src.yukkuriBody.ConstantValues.TangType;


/***************************************************
  繧�▲縺上ｊ蜃ｦ逅�け繝ｩ繧ｹ 

 */
public class YukkuriUtil {
	
	private static Random rnd = new Random();

	// 繧�▲縺上ｊ縺ｨ蜷�ョ繝ｼ繧ｿ縺ｮ繝ｪ繧ｹ繝�
	public enum YukkuriType {
		ALICE("Alice", "alice", "alice"),
		AYAYA("Ayaya", "ayaya", "ayaya"),
		CHEN("Chen", "chen", "chen"),
		CHIRUNO("Chiruno", "chiruno", "chiruno"),
		DEIBU("Deibu", "deibu", "deibu"),
		DOSMARISA("DosMarisa", "dosmarisa", "dosmarisa"),
		EIKI("Eiki", "eiki", "eiki"),
		FRAN("Fran", "fran", "fran"),
		HYBRIDYUKKURI("HybridYukkuri", "hybridyukkuri", ""),
		KIMEEMARU("Kimeemaru", "kimeemaru", "kimeemaru"),
		MARISA("Marisa", "marisa", "marisa"),
		MARISAKOTATSUMURI("MarisaKotatsumuri", "marisakotatsumuri", "marisa_kotatumuri"),
		MARISAREIMU("MarisaReimu", "marisareimu", "marisa_reimu"),
		MARISATSUMURI("MarisaTsumuri", "marisatsumuri", "marisa_tumuri"),
		MEIRIN("Meirin", "meirin", "merin"),
		MYON("Myon", "myon", "myon"),
		NITORI("Nitori", "nitori", "nitori"),
		PATCH("Patch", "patch", "patch"),
		RAN("Ran", "ran", "ran"),
		REIMU("Reimu", "reimu", "reimu"),
		REIMUMARISA("ReimuMarisa", "reimumarisa", "reimu_marisa"),
		REMIRYA("Remirya", "remirya", "remirya"),
		SAKUYA("Sakuya", "sakuya", "sakuya"),
		SUWAKO("Suwako", "suwako", "suwako"),
		TARINAI("Tarinai", "tarinai", "tarinai"),
		TENKO("Tenko", "tenko", "tenko"),
		UDONGE("Udonge", "udonge", "udonge"),
		WASAREIMU("WasaReimu", "reimu", "wasa"),
		YURUSANAE("Yurusanae", "yurusanae", "yurusanae"),
		YUUKA("Yuuka", "yuuka", "yuuka"),
		YUYUKO("Yuyuko", "yuyuko", "yuyuko"),
		;
		public String className;
		public String messageFileName;
		public String imageDirName;
		YukkuriType(String clsName, String msgFile, String imgDir) {
			this.className = clsName;
			this.messageFileName = msgFile;
			this.imageDirName = imgDir;
		}
	}
	
	// 繧ｯ繝ｩ繧ｹ蜷阪°繧峨ち繧､繝怜叙蠕�
	public static final YukkuriType getYukkuriType(String className) {
		YukkuriType ret = null;
		for(YukkuriType y :YukkuriType.values()) {
			if(y.className.equals(className)) {
				ret = y;
				break;
			}
		}
		return ret;
	}

	// 荳｡隕ｪ縺九ｉ襍､繧�ｸ�源蛻��DNA繧剃ｽ懈�縲�
	// forceCreate繧稚rue縺ｧ遒ｺ螳溘↓襍､繧�′縺ｧ縺阪ｋ縲ゅ∪繧後↓闌弱↓�代▽繧りｵ､繧�′縺ｧ縺阪↑縺��繧貞屓驕ｿ縺ｧ縺阪ｋ
	public static final Dna createBabyDna(Body father, Body mother, int motherType, Attitude motherAtt, Intelligence motherInt, boolean isRape, boolean motherSick, boolean motherDamage, boolean forceCreate) {
		Dna ret = null;

		// 遞ｮ蛻･縺ｮ豎ｺ螳�
		int babyType;
		boolean hybrid = false;
		boolean hybrid2 = false;
		if (rnd.nextInt(2) == 0 && !forceCreate) {
			// 菴懈�螟ｱ謨�
			return null;
		}
		if (!father.isHybrid() && motherType != HybridYukkuri.type) {
			if(rnd.nextInt(70) == 0) {
				hybrid = true;
				hybrid2 = true;
			}
		} else if (father.isHybrid() && motherType == HybridYukkuri.type) {
			if(rnd.nextInt(25) == 0) hybrid = true;
		}else{
			if(rnd.nextInt(65) == 0) hybrid = true;
		}
		
		if(father.getType() == DosMarisa.type || motherType == DosMarisa.type) {
			hybrid = false;
		}

		if(hybrid) {
			if(hybrid2 && mother != null && rnd.nextBoolean()) {
				babyType = mother.getHybridType(father.getType());
			} else {
				babyType = HybridYukkuri.type;
			}
		}else{
			if (rnd.nextBoolean() ) {
				babyType = father.getType();
			} else {
				babyType = motherType;
			}
			
			if(babyType == DosMarisa.type) {
				babyType = Marisa.type;
			}
			if(babyType == Yurusanae.type && !(father.getType() == Suwako.type || mother.getType() == Suwako.type)) {
				babyType = Suwako.type;
			}
		}

		if ((babyType == Reimu.type) && rnd.nextInt(10) == 0) {
			babyType = WasaReimu.type;
		}else if ((babyType == WasaReimu.type) && rnd.nextBoolean()) {
			babyType = Reimu.type;
		}else if ((babyType == Marisa.type || babyType == MarisaKotatsumuri.type ) && rnd.nextInt(120) == 0){
			babyType = MarisaTsumuri.type;
		}else if ((babyType == Marisa.type || babyType == MarisaTsumuri.type  ) && rnd.nextInt(120) == 0){
			babyType = MarisaKotatsumuri.type;
		}else if ((babyType == MarisaTsumuri.type || babyType == MarisaKotatsumuri.type ) && rnd.nextInt(10) != 2){
			babyType = Marisa.type;
		}else if ((babyType == Kimeemaru.type ) && rnd.nextInt(5) == 3){
			babyType = Ayaya.type;
		}else if ((babyType == Ayaya.type ) && rnd.nextBoolean()){
			babyType = Kimeemaru.type;
			}
		/* else if ((babyType == Alice.type && !father.isRaper()  ) && rnd.nextInt(100) == 7){
			babyType = Shanghai.type;
		} */                                          // For when Yukkuri Shanghai is implemented

		if (father.isSick() || motherSick || father.isDamaged() || motherDamage) {
			babyType = Tarinai.type;
		}

		ret = new Dna();
		ret.type = babyType;
		ret.raperChild = isRape;

		// 諤ｧ譬ｼ縺ｮ險ｭ螳�
		// 0(螟ｧ蝟�憶+螟ｧ蝟�憶)��(繝峨ご繧ｹ+繝峨ご繧ｹ)    Set attitude based on parents
		int attBase = father.getAttitude().ordinal() + motherAtt.ordinal();
		
		switch(attBase) {  // attitude values go Very_Nice to Super_Shithead 0vn, 1n, 2a, 3s, 4ss
			case 0:  /// Parents were both Very_Nice
				if(rnd.nextInt(15) == 0) {
					ret.attitude = Attitude.values()[2 + rnd.nextInt(3)];  // 1/15 chance of average, shithead, or super shithead
				} else {
					ret.attitude = Attitude.values()[rnd.nextInt(3)];      // usually verynice - average
				}
				break;
			case 1:  // 1 parent nice, other vn
			case 2:  //both parents nice
			case 3: //one parent average and nice/ one parent shithead and verynice
				if(rnd.nextInt(15) == 0) {
					ret.attitude = Attitude.values()[rnd.nextInt(2)];  // 1/15 chance of vn or nice
				} else {
					ret.attitude = Attitude.values()[1 + rnd.nextInt(4)]; //usually  nice- super shithead
				}
				break;
			case 4:
				if(rnd.nextInt(10) == 0) {
					ret.attitude = Attitude.values()[rnd.nextInt(3)];
				} else {
					ret.attitude = Attitude.values()[1 + rnd.nextInt(4)];
				}
				break;
			case 5:
			case 6:
			case 7:
				if(rnd.nextInt(15) == 0) {
					ret.attitude = Attitude.values()[1 + rnd.nextInt(3)];
				} else {
					ret.attitude = Attitude.values()[2 + rnd.nextInt(3)];
				}
				break;
			case 8:
				if(rnd.nextInt(20) == 0) {
					ret.attitude = Attitude.values()[rnd.nextInt(3)];
				} else {
					ret.attitude = Attitude.values()[3 + rnd.nextInt(2)];
				}
				break;
		}

// INTELLIGENCE
		int intBase = father.getIntelligence().ordinal() + motherInt.ordinal();
		
		switch(intBase) {
			case 0:
				if(rnd.nextInt(15) == 0) {
					ret.intelligence = Intelligence.values()[1 + rnd.nextInt(2)];
				} else {
					ret.intelligence = Intelligence.values()[rnd.nextInt(2)];
				}
				break;
			case 1:
			case 2:
			case 3:
				if(rnd.nextInt(10) == 0) {
					ret.intelligence = Intelligence.values()[rnd.nextInt(3)];
				} else {
					ret.intelligence = Intelligence.values()[1];
				}
				break;
			case 4:
				if(rnd.nextInt(15) == 0) {
					ret.intelligence = Intelligence.values()[rnd.nextInt(2)];
				} else {
					ret.intelligence = Intelligence.values()[1 + rnd.nextInt(2)];
				}
				break;
		}
		return ret;
	}

	// 遯∫┯螟臥焚繝√ぉ繝�け
	public static final Body checkTransform(Body b) {
		Body ret = null;

		switch(b.getType()) {
			case Reimu.type:
				// 縺ｧ縺��蛹�
				if(b.isDead()) return null;
				if(!b.isAdult()) return null;
				if(b.getStress() > 0) return null;
				if(b.getTangType() == TangType.POOR) return null;
				if(b.isDamaged()) return null;
				if(!b.isFull()) return null;
				if(!b.isRude()) return null;
				if(b.isFeelPain()) return null;
				if(rnd.nextInt(100) == 0) {
					ret = b;
				}
				break;
			case Marisa.type:
				// 繝峨せ蛹�
				if(Terrarium.antidosSteam) return null;
				if(b.isDead()) return null;
				if(!b.isAdult()) return null;
				if(b.getStress() > 0) return null;
				if(b.isDamaged()) return null;
				if(b.isHungry()) return null;
				if(!b.isHappy()) return null;
				if(b.isFeelPain()) return null;
				if(rnd.nextInt(1000) == 0) {
					ret = b;
				}
				break;
			default:
				break;
		}
		return ret;
	}
	
	// 遯∫┯螟臥焚螳溯｡�
	public static final void execTransform(Body b) {
		switch(b.getType()) {
			case Reimu.type:
				// 縺ｧ縺��蛹�
				synchronized(SimYukkuri.lock) {
					Terrarium.bodyList.remove(b);
					Body to = new Deibu(b.getX(), b.getY(), b.getZ(), b.getAgeState(), null, null);
					YukkuriUtil.changeBody(to, b);
					Terrarium.bodyList.add(to);
					if(MyPane.selectBody == b) {
						MyPane.selectBody = to;
					}
				}
				break;
			case Marisa.type:
				// 繝峨せ蛹�
				// 繝峨せ縺ｯ繝輔ぅ繝ｼ繝ｫ繝峨↓荳�ｽ薙□縺�
				for(Body tmp :Terrarium.bodyList) {
					if(tmp.getType() == DosMarisa.type) return;
				}
				synchronized(SimYukkuri.lock) {
					Terrarium.bodyList.remove(b);
					Body to = new DosMarisa(b.getX(), b.getY(), b.getZ(), b.getAgeState(), null, null);
					YukkuriUtil.changeBody(to, b);
					Terrarium.bodyList.add(to);
					if(MyPane.selectBody == b) {
						MyPane.selectBody = to;
					}
				}
				break;
			default:
				break;
		}
	}

	private static final HashMap<String, Object> NO_COPY_FIELD = new HashMap<String, Object>() {
		{
			put("imgW", null);
			put("imgH", null);
			put("pivX", null);
			put("pivY", null);
			put("braidImgW", null);
			put("braidImgH", null);

			put("EATAMOUNT", null);
			put("WEIGHT", null);
			put("HUNGRYLIMIT", null);
			put("SHITLIMIT", null);
			put("DAMAGELIMIT", null);
			put("STRESSLIMIT", null);
			put("TANGLEVEL", null);
			put("BABYLIMIT", null);
			put("CHILDLIMIT", null);
			put("LIFELIMIT", null);
			put("STEP", null);
			put("RELAXPERIOD", null);
			put("EXCITEPERIOD", null);
			put("PREGPERIOD", null);
			put("SLEEPPERIOD", null);
			put("ACTIVEPERIOD", null);
			put("ANGRYPERIOD", null);
			put("SCAREPERIOD", null);
			put("sameDest", null);
			put("DECLINEPERIOD", null);
			put("DISCIPLINELIMIT", null);
			put("BLOCKEDLIMIT", null);
			put("DIRTYPERIOD", null);
			put("ROBUSTNESS", null);
			put("STRENGTH", null);
			put("TRASH_OKAZARI_OFS_X", null);
			put("TRASH_OKAZARI_OFS_Y", null);

			put("speed", null);
			put("msgType", null);
			put("shitType", null);
		}
	};

	public static final void changeBody(Body to, Body from) {
		Field[] fromField = null;
		Class<?> toClass = null;
		Field toField = null;

		// Obj繧ｯ繝ｩ繧ｹ縺ｮ繧ｳ繝斐�
		fromField = from.getClass().getSuperclass().getSuperclass().getDeclaredFields();
		toClass = to.getClass().getSuperclass().getSuperclass();
		
		for(int i = 0; i < fromField.length; i++) {
			int mod = fromField[i].getModifiers();
			if(Modifier.isFinal(mod)) continue;
			if(Modifier.isStatic(mod)) continue;
			try {
				toField = toClass.getDeclaredField(fromField[i].getName());
				toField.set(to, fromField[i].get(from));
			} catch (SecurityException e) {
				// TODO 閾ｪ蜍慕函謌舌＆繧後◆ catch 繝悶Ο繝�け
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO 閾ｪ蜍慕函謌舌＆繧後◆ catch 繝悶Ο繝�け
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO 閾ｪ蜍慕函謌舌＆繧後◆ catch 繝悶Ο繝�け
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO 閾ｪ蜍慕函謌舌＆繧後◆ catch 繝悶Ο繝�け
				e.printStackTrace();
			}
		}
		
		// Body繧ｯ繝ｩ繧ｹ縺ｮ繧ｳ繝斐�
		fromField = from.getClass().getSuperclass().getDeclaredFields();
		toClass = to.getClass().getSuperclass();

		for(int i = 0; i < fromField.length; i++) {
			int mod = fromField[i].getModifiers();
			if(Modifier.isFinal(mod)) continue;
			if(Modifier.isStatic(mod)) continue;
			if(NO_COPY_FIELD.containsKey(fromField[i].getName())) continue;
			try {
				toField = toClass.getDeclaredField(fromField[i].getName());
				toField.set(to, fromField[i].get(from));
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		// 蟷ｴ鮨｢縺ｮ陬懈ｭ｣
		switch (to.getAgeState()) {
			case BABY:
				to.setAge(0);
				break;
			case CHILD:
				to.setAge(to.BABYLIMIT + 1);
				break;
			case ADULT:
			default:
				to.setAge(to.CHILDLIMIT + 1);
				break;
		}
	}
}
