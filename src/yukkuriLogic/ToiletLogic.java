package src.yukkuriLogic;

import src.EventPacket;
import src.MessagePool;
import src.Terrarium;
import src.EventPacket.EventPriority;
import src.MessagePool.Action;
import src.item.Toilet;
import src.object.Obj;
import src.object.ObjEX;
import src.object.Shit;
import src.system.Translate;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues.AgeState;
import src.yukkuriBody.ConstantValues.Attitude;
import src.yukkuriBody.Moving;



/***************************************************
	トイレ関係の処理
 */
public class ToiletLogic {
	private static final int INFINITE = 999999;
	public static final boolean checkShit(Body b) {
		// 毎フレームチェックは重いのでインターバル
		if(b.getAge() % 10 != 0) return false;
		
		if (b.isDead() || b.isIdiot() || b.isSleeping() || b.isExciting() || b.nearToBirth() ) {
			return false;
		}
		if(b.getCurrentEvent() != null && b.getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}

		boolean ret = false;
		for (Shit s: Terrarium.shitList) {
			if(s.getZ() != b.getZ()) continue;

			if ((b.getStep() * b.getStep()) > Translate.distance(b.getX(), b.getY(), s.getX(), s.getY())) {
				if (!b.isTalking() && !b.isToShit()) {
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateShit), false);
					b.addStress(50);
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	public static final int getMinimumShitDistance(Body b)
	{
		int min = INFINITE;
		int current = 0;
		
		if (b.isDead() || b.isIdiot() || b.isSleeping() || b.isExciting() || b.nearToBirth() ) {
			return INFINITE;
		}
		if(b.getCurrentEvent() != null && b.getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
			return 0;
		}

		for (Shit s: Terrarium.shitList) {
			if(s.getZ() != b.getZ()) continue;
			current = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
			if(current < min) 	
				min = current;
		}
		return min;
	}

	public static final boolean checkToilet(Body b) {
		if (b.isIdiot() || !b.wantToShit() || b.nearToBirth() ) {
			return false;
		}
		if(b.getCurrentEvent() != null && b.getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}

		// 対象が決まっていたら到達したかチェック
		if(b.isToFood() && b.getMoveTarget() != null) {
			// 途中で消されてたら他の候補を探す
			if(b.getMoveTarget().isRemoved()) {
				b.clearActions();
				return false;
			}
			if ((b.getStep() * b.getStep()) >= Translate.distance(b.getX(), b.getY(), b.getMoveTarget().getX(), b.getMoveTarget().getY())) {
				// 到着したら待機状態へ
				if (b.getMoveTarget().getZ() != 0) {
					// 他の候補を探す
					b.clearActions();
					return false;
				}
			}
			else
			{
				b.moveTo(b.getMoveTarget().getX(), b.getMoveTarget().getY(), 0);
			}
			return true;
		}

		boolean ret = false;
		Toilet found = null;
		int minDistance = b.getEyesight();
		int wallMode = b.getAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if(b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		for (ObjEX t: Toilet.objEXList) {
			int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY() - t.getH()/6);
			if (minDistance > distance) {
				if (!b.isRude()) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY() - t.getH()/6, Terrarium.MAP_BODY[wallMode])) {
						continue;
					}
				}
				found = (Toilet)t;
				minDistance = distance;
			}
		}
		if (found != null) {
			Moving.moveToToilet(b, found, found.getX(), found.getY(), 0);
			ret = true;
		}
		else
		{
			if(b.getAttitude().equals(Attitude.VERY_NICE) || b.getAttitude().equals(Attitude.NICE))
			{
				Obj toilet = new Obj();
				// TO DO :magic numbers will be changed
				Moving.moveToToilet(b, toilet, 5, b.getY());
				ret = true;
			}
		}
		
		return ret;
	}
}


