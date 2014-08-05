package src;

import src.item.Toilet;



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
		if(b.currentEvent != null && b.currentEvent.getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}

		boolean ret = false;
		for (Shit s: Terrarium.shitList) {
			if(s.getZ() != b.getZ()) continue;

			if ((b.getStep() * b.getStep()) > Translate.distance(b.getX(), b.getY(), s.getX(), s.getY())) {
				if (!b.isTalking() && !b.toShit) {
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
		if(b.currentEvent != null && b.currentEvent.getPriority() != EventPacket.EventPriority.LOW) {
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
		if(b.currentEvent != null && b.currentEvent.getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}

		// 対象が決まっていたら到達したかチェック
		if(b.toFood && b.moveTarget != null) {
			// 途中で消されてたら他の候補を探す
			if(b.moveTarget.removed) {
				b.clearActions();
				return false;
			}
			if ((b.getStep() * b.getStep()) >= Translate.distance(b.getX(), b.getY(), b.moveTarget.getX(), b.moveTarget.getY())) {
				// 到着したら待機状態へ
				if (b.moveTarget.getZ() != 0) {
					// 他の候補を探す
					b.clearActions();
					return false;
				}
			}
			else
			{
				b.moveTo(b.moveTarget.getX(), b.moveTarget.getY(), 0);
			}
			return true;
		}

		boolean ret = false;
		Toilet found = null;
		int minDistance = b.getEyesight();
		int wallMode = b.getAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if(b.canflyCheck()) {
			wallMode = Body.AgeState.ADULT.ordinal();
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
			b.moveToToilet(found, found.getX(), found.getY(), 0);
			ret = true;
		}
		return ret;
	}
}


