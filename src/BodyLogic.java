package src;

import java.util.Random;

import src.Body.PanicType;
import src.EventPacket.EventPriority;
import src.TrashUtil.OkazariType;
import src.event.*;
import src.yukkuri.Fran;
import src.yukkuri.Remirya;
import src.yukkuri.Sakuya;




/***************************************************
	繧�▲縺上ｊ蜷悟｣ｫ縺ｮ蜃ｦ逅�
 */
public class BodyLogic {
	
	private static final Random rnd = new Random();

	public static final boolean checkPartner(Body b) {
		if (b.isDead()) {
			return false;
		}
		if(b.isSleeping() || (!b.isExciting() && !b.isRude() && b.wantToShit()) || b.nearToBirth()){
			return false;
		}

		boolean ret = false;

		// 蟇ｾ雎｡縺梧ｱｺ縺ｾ縺｣縺ｦ縺�◆繧牙芦驕斐＠縺溘°繝√ぉ繝�け
		if((b.toBody || b.toSukkiri) && b.moveTarget instanceof Body) {
			Body p = (Body)b.moveTarget;
			
			// 騾比ｸｭ縺ｧ豸医＆繧後※縺溘ｉ莉悶�蛟呵｣懊ｒ謗｢縺�
			if(p.removed) {
				b.clearActions();
				return false;
			}
			// 逶ｸ謇九′螳吶↓豬ｮ縺�※縺溘ｉ辟｡隕�
			if (!b.canflyCheck() && p.getZ() != 0) {
				b.clearActions();
				return false;
			}
			int rangeX = Translate.invertX((int)((b.getCollisionX() + p.getCollisionX()) * 0.6f), p.getY());
			rangeX = Translate.transSize(rangeX);
			int distX = Math.abs(b.getX() - p.getX());
			int distY = Math.abs(b.getY() - p.getY());
			int range = Math.abs(rangeX - distX);
			// 隕九▽縺九▲縺溽嶌謇九↓蟇ｾ縺吶ｋ繧ｳ繝ｪ繧ｸ繝ｧ繝ｳ繝√ぉ繝�け
			// 菴薙′髫｣謗･縺吶ｋ繧医≧縺ｫ讓ｪ髟ｷ縺ｮ繝懊ャ繧ｯ繧ｹ縺ｧ蛻､螳壹ｒ蜿悶ｋ
//System.out.println(range + "," + distY);
			if (range < 5 && distY < 3) {
	
				// 逶ｸ謇九→縺ｮ霍晞屬縺碁團謗･迥ｶ諷九→蛻､譁ｭ縺輔ｌ縺溷�蜷�
				if (!p.isDead()) {
					// 閾ｪ蛻�′逋ｺ諠�＠縺ｦ縺ｦ逶ｸ謇九′逕溘″縺ｦ縺溘ｉ縺吶▲縺阪ｊ螳溯｡�
					if (b.isExciting()) {
						if(b.isRaper()) {
							if(b.getX() < p.getX()) {
								b.setDirection(Body.Direction.RIGHT);
							} else {
								b.setDirection(Body.Direction.LEFT);
							}
							b.constraintDirection(p, true);
							b.doRape(p);
						} else {
							b.constraintDirection(p, false);
							b.doSukkiri(p);
						}
					}
					else if (b.isAdult() && !p.isAdult() && p.isDirty() && (p.isChild(b) || b.isMotherhood(p))) {
						// 閾ｪ蛻�′豈崎ｦｪ縺ｧ逶ｸ謇九′豎壹ｌ縺溷ｭ蝉ｾ帙↑繧峨⊆繧阪⊆繧�
						b.constraintDirection(p, false);
						b.doPeropero(p);
					}
					else if (b.isParent(p) && !p.isAdult() && rnd.nextInt(1) == 0) {
						// 螳ｶ譌上↑繧峨⊆繧阪⊆繧�
						b.constraintDirection(p, false);
						b.doSurisuri(p);
					}
					else if (p.isPartner(b) && rnd.nextInt(1) == 0) {
						// 螳ｶ譌上↑繧峨☆繧翫☆繧�
						b.constraintDirection(p, false);
						b.doSurisuri(p);
					}
					else if (!b.isAdult() && b.isSister(p) && rnd.nextInt(1) == 0) {
						// 蟋牙ｦｹ縺ｮ蝣ｴ蜷医�縺ｺ繧阪⊆繧�
						if (b.isElderSister(p)) {
							b.constraintDirection(p, false);
							b.doPeropero(p);
						} else {
							b.constraintDirection(p, false);
							b.doSurisuri(p);
						}
					}
				} else {
					// 逶ｸ謇九′豁ｻ菴薙�蝣ｴ蜷�
				
					// 逋ｺ諠�＠縺ｦ縺溘ｉ縺吶▲縺阪ｊ
					if (b.isExciting()) {
						if(b.isRaper()) {
							b.doRape(p);
							b.clearActions();
						} else {
							b.doSukkiri(p);
						}
					}
					else if (b.isAdult()) {
						// 閾ｪ蛻�′謌蝉ｽ薙〒逶ｸ謇九′螳ｶ譌上↑繧牙�縺�
						if (!b.isTalking()) {
							if (b.isParent(p)) {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForChild));
								b.setHappiness(Body.Happiness.VERY_SAD);
							}
							else if (b.isPartner(p)) {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForPartner));
								b.setHappiness(Body.Happiness.VERY_SAD);
							}
							b.addStress(250);
						}
					}
					else if (b.isSister(p)){
						// 逶ｸ謇九′蟋牙ｦｹ縺ｪ繧牙�縺�
						if (!b.isTalking()) {
							if (b.age < p.age) {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForEldersister));
							}
							else {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForSister));
							}
							b.setHappiness(Body.Happiness.VERY_SAD);
							b.addStress(200);
						}
					}
				}
				b.clearActions();
				
			} else {
				int dir = 1;
				if(b.getX() < p.getX()) dir = -1;
				
				rangeX *= dir;
				if(b.canflyCheck()) {
					b.moveTo(p.getX() + rangeX, p.getY(), p.getZ());
				} else {
					b.moveTo(p.getX() + rangeX, p.getY());
				}
				// 逶ｸ謇九↓霑ｽ縺�▽縺代↑縺�こ繝ｼ繧ｹ縺後≠繧九◆繧√�荳�ｮ夊ｷ晞屬縺ｾ縺ｧ霑代▼縺�◆繧臥嶌謇九ｒ蜻ｼ縺ｳ豁｢繧√ｋ
				if(Translate.distance(b.getX(), b.getY(), p.getX(), p.getY()) < 2500) {
					if(b.isTargetBind()) p.stay();
				}
			}
			return true;
		}

		if(b.currentEvent != null && b.currentEvent.getPriority() != EventPriority.LOW) return false;

		Body found = null;
		int minDistance = b.getEyesight();
		// 閾ｪ蛻�′逋ｺ諠�＠縺ｦ縺�※縺､縺後＞縺梧里縺ｫ縺�ｋ縺ｪ繧牙━蜈医＠縺ｦ蜷代°縺�
		if(b.isExciting() && b.partner != null && !b.partner.isDead() && !b.isRaper()) {
			found = b.partner;
			minDistance = Translate.distance(b.getX(), b.getY(), found.getX(), found.getY());
		}
		else if(b.isExciting() && b.isRaper() && b.toSukkiri && b.getMoveTarget() != null) {
			// 繝ｬ繧､繝代�縺ｧ縺吶▲縺阪ｊ荳ｭ縺ｪ繧臥ｶ壹￠縺ｦ蜷後ち繝ｼ繧ｲ繝�ヨ縺ｫ
			found = (Body)b.getMoveTarget();
			minDistance = Translate.distance(b.getX(), b.getY(), found.getX(), found.getY());
		} else {
			// 蜈ｨ繧�▲縺上ｊ縺ｫ蟇ｾ縺励※繝√ぉ繝�け
			for (Body p: Terrarium.bodyList) {
				// 閾ｪ蛻�酔螢ｫ縺ｮ繝√ぉ繝�け縺ｯ辟｡諢丞袖縺ｪ縺ｮ縺ｧ繧ｹ繧ｭ繝��
				if (p == b) continue;
				// 逶ｸ謇九′豬ｮ縺�※縺溘ｉ繧ｹ繧ｭ繝��
				if(!b.canflyCheck() && p.getZ() != 0) continue;
				// 繧後∩繧翫ｃ縺碁�蟶ｸ遞ｮ縺ｫ霑代▼縺�◆蝣ｴ蜷�
				/// 證ｫ螳壼�逅�縺輔￥繧��萓句､�
				if(p.getType() != Sakuya.type) {
					if((b.getType() == Remirya.type || b.getType() == Fran.type) && p.getPanicType() == null) {
						if(!p.isDead() && !p.isPredatorType() && !b.isFamily(p)) {
							// 譛�ｫ倬ｫ伜ｺｦ縺ｮ蜊雁�莉･荳九↑繧臥嶌謇九′隱崎ｭ�
							if(b.getZ() < Translate.getFlyHeightLimit()) {
								if (!Terrarium.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(), Terrarium.MAP_BODY[Body.AgeState.ADULT.ordinal()])) {
									p.setPanic(true, Body.PanicType.REMIRYA);
									p.setMessage(MessagePool.getMessage(p, MessagePool.Action.EscapeFromRemirya));
								}
							}
						}
					}
				}
				// 逶ｸ謇九′豁ｻ菴薙〒辯�∴縺ｦ縺�◆繧牙ｼ慕↓繝√ぉ繝�け
				if(p.isDead() && p.getPanicType() == PanicType.BURN) {
					int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
					if (dist <= Translate.distance(0, 0, b.getStep() * 2, b.getStep() * 2)) {
						b.giveFire();
						return true;
					}
				} else if (b.isExciting()) {
					// 閾ｪ蛻�′騾壼ｸｸ縺ｮ逋ｺ諠�↑繧画�菴謎ｻ･螟悶�繧ｹ繧ｭ繝��
					if (!b.isRaper()) {
						if (p.isDead() || !p.isAdult() || p.isChild(b) || p.isParent(b)) {
							continue;
						}
					} else {
						// 繝ｬ繧､繝代�縺ｮ蝣ｴ蜷医�繝ｬ繧､繝代�莉･螟悶ｒ迢吶≧
						if (p.isDead() || p.isUnBirth() || (p.isRaper() && p.isExciting())) {
							continue;
						}
					}
				} else if (p.isDead() && (!p.hasOkazari() || b.isIdiot())) {
					// 閾ｪ蛻�′鬥ｬ鮖ｿ縺ｧ逶ｸ謇九′縺翫°縺悶ｊ縺ｪ縺励�豁ｻ菴薙↑繧蛾｣滓侭謇ｱ縺�↑縺ｮ縺ｧ繧ｹ繧ｭ繝��
					continue;
				}
				int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
				if (minDistance > dist) {
					// 閾ｪ蛻�′繧ｲ繧ｹ縺ｧ縺ｪ縺上�逶ｸ謇九→縺ｮ髢薙↓螢√′縺ゅｌ縺ｰ繧ｹ繧ｭ繝��
					if (!b.isRude()) {
						if (Terrarium.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(), Terrarium.MAP_BODY[b.getAgeState().ordinal()])) {
							continue;
						}
					}
					// 逶ｸ謇狗匱隕狗｢ｺ螳�
					found = p;
					minDistance = dist;
				}
			}
		}
		
		// 逶ｮ讓吶′螳壹∪縺｣縺溘ｉ遘ｻ蜍輔そ繝�ヨ
		if (found != null) {
			int mz = 0;
			// 鬟幄｡檎ｨｮ縺ｯZ繧らｧｻ蜍募庄閭ｽ
			if(b.canflyCheck()) {
				mz = found.getZ(); 
			}
			
			// 繧�▲縺上ｊ蜷悟｣ｫ縺碁㍾縺ｪ繧峨↑縺�ｈ縺�↓逶ｮ讓吝慍轤ｹ縺ｯ菴薙�繧ｵ繧､繧ｺ繧定�諷ｮ
			int colX = calcCollisionX(b, found);
			
			// 逶ｸ謇九′豁ｻ菴薙〒縺ｪ縺代ｌ縺ｰ
			if (!found.isDead()) {
				// 閾ｪ蛻�′逋ｺ諠�＠縺ｦ縺�ｌ縺ｰ縺吶▲縺阪ｊ縺ｫ蜷代°縺�
				if (b.isExciting()) {
					b.moveToSukkiri(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(true);
					ret = true;
				}
				else if (!found.hasOkazari() && b.getOkazariType() == OkazariType.DEFAULT && b.isRude()
						&& !b.isDamaged() && !found.isUnBirth() && b.currentEvent == null) {
					// 縺翫°縺悶ｊ縺ｮ縺ｪ縺�ｆ縺｣縺上ｊ縺ｪ繧牙宛陬√ｒ蜻ｼ縺ｳ縺九￠繧�
					if (rnd.nextInt(20) == 0) {
						if (!b.isTalking()) {
							EventLogic.addWorldEvent(new HateNoOkazariEvent(b, found, null, 10), b, MessagePool.getMessage(b, MessagePool.Action.HateYukkuri));
						}
						ret = true;
					}
				}
				else if (b.isAdult() && !found.isAdult() && found.isDirty() && (found.isChild(b) || b.isMotherhood(found))) {
					// 逶ｸ謇九′豎壹ｌ縺溷ｭ蝉ｾ帙↑繧峨⊆繧阪⊆繧阪＠縺ｫ蜷代°縺�
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(false);
					ret = true;
				}
				else if (b.isChild(found) && !b.isAdult() && b.isDirty()) {
					// 閾ｪ蛻�′豎壹ｌ縺溷ｭ蝉ｾ帙↑繧牙ｮｶ譌上�縺ｨ縺薙ｍ縺ｸ蜷代°縺�
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(true);
					ret = true;
				}
				else if (found.isPartner(b)) {
					// 繝ｩ繝ｳ繝�Β縺ｧ縺､縺後＞縺ｮ縺ｨ縺薙ｍ縺ｸ蜷代°縺�
					if (rnd.nextInt(200) == 0) {
						b.moveToBody(found, found.getX() + colX, found.getY(), mz);
						b.setTargetBind(false);
						ret = true;
					}
				}
				else if (!b.isAdult() && b.isSister(found)) {
					// 繝ｩ繝ｳ繝�Β縺ｧ蟋牙ｦｹ縺ｮ縺ｨ縺薙ｍ縺ｸ蜷代°縺�
					if (rnd.nextInt(200) == 0) {
						b.moveToBody(found, found.getX() + colX, found.getY(), mz);
						b.setTargetBind(false);
						ret = true;
					}
				}
			} else {
				// 豁ｻ菴鍋嶌謇九�陦悟虚
				if (b.isExciting()) {
					// 縺吶▲縺阪ｊ
					b.moveToSukkiri(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(false);
					ret = true;						
				}
				else if (rnd.nextInt(10) == 0) {
					// 螳ｶ譌上�豁ｻ菴薙↓蝌�￥
					if (b.isAdult()) {
						if (b.isParent(found) || b.isPartner(found)) {
							b.moveToBody(found, found.getX() + colX, found.getY(), mz);
							b.setTargetBind(false);
							ret = true;
						} else {
							b.lookTo(found.getX() + colX, found.getY());
						}
					} else {
						if (b.isSister(found)) {
							b.moveToBody(found, found.getX() + colX, found.getY(), mz);
							b.setTargetBind(false);
							ret = true;
						} else {
							b.runAway(found.getX() + colX, found.getY());
						}
					}
					// 繝輔ぅ繝ｼ繝ｫ繝峨�豁ｻ菴薙↓諤ｯ縺医ｋ
					if (!b.isTalking()) {
						if(b.isPredatorType() && found.isPredatorType() || !b.isPredatorType()) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Scare));
							b.setHappiness(Body.Happiness.SAD);
						}
					}
				}
			}
		}
		return ret;
	}
	
	// 菴灘酔螢ｫ縺瑚ｧｦ繧後ｋ菴咲ｽｮ縺ｮX蠎ｧ讓吶ｒ豎ゅａ繧�
	public static final int calcCollisionX(Body from, Body to) {
		int colX = Translate.invertX((int)((from.getCollisionX() + to.getCollisionX()) * 0.6f), to.getY());
		colX = Translate.transSize(colX);
		
		// 縺贋ｺ偵＞縺ｮ菴咲ｽｮ縺九ｉ蜿ｳ縺ｨ蟾ｦ譛�洒霍晞屬繧帝∈謚�
		int dir = 1;
		if(from.getX() < to.getX()) dir = -1;
		colX *= dir;

		return colX;
	}
}


