package src.yukkuriLogic;

import java.util.Random;

import src.EventPacket;
import src.Obj;
import src.ObjEX;
import src.Terrarium;
import src.Translate;
import src.EventPacket.EventPriority;
import src.Terrarium.DayState;
import src.event.FavCopyEvent;
import src.item.Bed;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues.*;


/***************************************************
	ベッド関係の処理
 */
public class BedLogic {

	private static Random rnd = new Random();
	
	public static final boolean checkBed(Body b) {
		if(b.isDead() || b.isIdiot() || b.isSleeping() || b.getCriticalDamegeType() != null) return false;

		if(b.nearToBirth()) {
			if(b.getCurrentEvent() != null && b.getCurrentEvent().getPriority() == EventPacket.EventPriority.HIGH) {
				return false;
			}
		} else {
			if(b.getCurrentEvent() != null && b.getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
				return false;
			}
		}

		// 対象が決まっていたら到達したかチェック
		if(b.isToBed() && b.getMoveTarget() != null) {
			// 途中で消されてたら他の候補を探す
			if(b.getMoveTarget().isRemoved()) {
				b.setFavItem(FavItemType.BED, null);
				b.clearActions();
				return false;
			}
			if ((b.getStep() * b.getStep()) >= Translate.distance(b.getX(), b.getY(), b.getMoveTarget().getX() + b.targetPosOfsX, b.getMoveTarget().getY() + b.targetPosOfsY)
					&& b.getZ() == 0) {
				// 到着したら待機状態へ
				if(b.getFavItem(FavItemType.BED) == null) {
					// 見つけたベッドをお気に入りにして家族にも伝達
					b.setFavItem(FavItemType.BED, b.getMoveTarget());
					EventLogic.addWorldEvent(new FavCopyEvent(b, null, null, 1), null, null);
				}
				b.stay();
			}
			else
			{
				b.moveTo(b.getMoveTarget().getX() + b.targetPosOfsX, b.getMoveTarget().getY() + b.targetPosOfsY, 0);
			}
			return true;
		}
		
		// ベッドに向かう条件
		boolean flag = false;
		if (b.isSleepy()	//	眠い
			|| Terrarium.getDayState().ordinal() >= Terrarium.DayState.EVENING.ordinal()	// 夜になった
			|| b.nearToBirth()) {	// 出産間近
			flag = true;
		}
		
		if(!flag) return false;

		boolean ret = false;
		int minDistance = b.getEyesight();
		Obj found = b.getFavItem(FavItemType.BED);
		int wallMode = b.getAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if(b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		if (found != null) {
			// お気に入りが壁で到達できなくなってたらリセット
			if (Terrarium.acrossBarrier(b.getX(), b.getY(), found.getX(), found.getY(), Terrarium.MAP_BODY[wallMode])) {
				found = null;
			}
		}

		if(found == null) {
			for (ObjEX t: Bed.objEXList) {
				int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY());
				if (minDistance > distance) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY(), Terrarium.MAP_BODY[wallMode])) {
						continue;
					}
					found = (Bed)t;
					minDistance = distance;
				}
			}
		}

		if (found != null) {
			int ofsX = 0;
			int ofsY = 0;
			if(b.hasBabyOrStalk()) {
				ofsY = Translate.invertY(found.getH() - 4);
				ofsY = -(ofsY >> 1);
			} else {
				ofsX = Translate.invertX(found.getW(), found.getY() - 4);
				ofsX = -(ofsX >> 1) + rnd.nextInt(ofsX);
				ofsY = Translate.invertY(found.getH() - 4);
				ofsY = -(ofsY >> 1) + rnd.nextInt(ofsY);
			}
			b.moveToBed(found, found.getX() + ofsX, found.getY() + ofsY, 0);
			b.setTargetMoveOffset(ofsX, ofsY);
			ret = true;
		}
		return ret;
	}
}

