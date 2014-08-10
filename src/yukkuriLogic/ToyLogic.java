package src.yukkuriLogic;

import java.util.Random;

import src.EventPacket;
import src.MessagePool;
import src.Terrarium;
import src.EventPacket.EventPriority;
import src.MessagePool.Action;
import src.event.SuiRideEvent;
import src.event.SuiSpeake;
import src.item.Sui;
import src.item.Toy;
import src.object.Obj;
import src.object.ObjEX;
import src.system.Translate;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues.Direction;
import src.yukkuriBody.ConstantValues.FavItemType;
import src.yukkuriBody.ConstantValues.Happiness;



/***************************************************
	おもちゃ関係の処理
 */
public class ToyLogic {

	private static final Random rnd = new Random();

	// ボール
	public static final boolean checkToy(Body b) {
		if (b.isDead() || b.isSleeping() || b.isExciting() || b.isScare() || (!b.isRude() && (b.isAdult() || b.wantToShit()))) {
			return false;
		}
		if(b.getCurrentEvent() != null && b.getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}

		boolean ret = false;
		Toy found = null;
		int minDistance = b.getEyesight();
		for (ObjEX t: Toy.objEXList) {
			int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY());
			if (minDistance > distance) {
				if (!b.isRude()) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY(), Terrarium.MAP_BODY[b.getAgeState().ordinal()])) {
						continue;
					}
				}
				found = (Toy)t;
				minDistance = distance;
			}
		}
		if (found != null) {
			if (minDistance <= Translate.distance(0, 0, b.getStep(), b.getStep())) {
				if (found.getZ() != 0) {
					if (b.getFavItem(FavItemType.BALL) == found && !found.isOwned(b)) {
						if (b.isRude()) {
							b.setHappiness(Happiness.VERY_SAD);
							b.addStress(200);
						}
						else {
							b.setHappiness(Happiness.SAD);
							b.addStress(100);
						}
						if (!b.isTalking()) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.LostTreasure), true);
						}
					}
					return false;
				}
				int strength[] = {-1, -4, -6};
				b.setHappiness(Happiness.HAPPY);
				b.addStress(-200);
				if (!found.isOwned(b)) {
					found.setOwner(b);
					b.setFavItem(FavItemType.BALL, found);
					if (!b.isTalking()) {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GetTreasure), true);
					}
				}
				found.kick(b.getDirX() * b.getStep(), b.getDirY() * b.getStep(), strength[b.getAgeState().ordinal()]);
			}
			else {
				if (b.getFavItem(FavItemType.BALL) == found && !found.isOwned(b)) {
					if (b.isRude()) {
						b.setHappiness(Happiness.VERY_SAD);
						b.addStress(200);
					}
					else {
						b.setHappiness(Happiness.SAD);
						b.addStress(100);
					}
					if (!b.isTalking()) {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.LostTreasure), true);
					}
				}
				b.moveTo(found.getX(), found.getY());
			}
		}
		return ret;
	}

	// すぃー
	public static final boolean checkSui(Body b) {
		if (b.isDead() || b.isSleeping() || b.isExciting() || b.isScare()
				|| rnd.nextInt(200) != 0 || (b.getLinkParent() instanceof Sui)) {
			return false;
		}
		if(b.getCurrentEvent() != null) {
			return false;
		}
		int sui_num = Sui.objEXList.size();
		if(rnd.nextInt(100) == 0 && sui_num > 0) {
			if(!b.isTalking()) {
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.YukkuringSui), true);
			}
			return false;
		}
		boolean ret = false;
		Obj found = b.getFavItem(FavItemType.SUI);

		if(found == null) {
			int minDistance = b.getEyesight();
			for (ObjEX s: Sui.objEXList) {
				int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
				if (minDistance > distance) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(), Terrarium.MAP_BODY[b.getAgeState().ordinal()])) {
						continue;
					}

					if(((Sui)s).NoCanBind()) {
						Body bindBody = (Body)((Sui)s).getbindobj();
						if(!(b.isParent(bindBody) || bindBody.isParent(b) || b.isPartner(bindBody) || bindBody.isSister(b))){
							continue;
						}
					}
					found = s;
					minDistance = distance;
				}			
			}
		}
		else if(rnd.nextBoolean() && !b.isTalking() && Translate.distance(b.getX(), b.getY(), found.getX(), found.getY()) < 200000){
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.hasSui), true);
			EventLogic.addWorldEvent(new SuiSpeake(b, null, found, 10), null, null);
			return false;
		}
		if (found != null) {
			/*if (minDistance <= Translate.distance(0, 0, b.getStep(), b.getStep())) {
				b.setHappiness(Body.Happiness.HAPPY);
				b.addStress(-200);
				if (!b.isTalking()) {
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GetTreasure), true);
				}
				((Sui)found).rideOn(b);
			}
			else {
				b.moveTo(found.getX(), found.getY());
			}*/
			Body bindBody =(Body)((Sui)found).getbindobj() ;
			if(bindBody == null){
				if (!b.isTalking()) {
					EventLogic.addWorldEvent(new SuiRideEvent(b, null, found, 100), b, MessagePool.getMessage(b, MessagePool.Action.FindSui));
				}
			}
			else if(bindBody == b){
				if (!b.isTalking()) {
					EventLogic.addWorldEvent(new SuiRideEvent(b, null, found, 100), b, MessagePool.getMessage(b, MessagePool.Action.FindGetSui));
				}
			}
			else{
				if (!b.isTalking()) {
		//			EventLogic.addWorldEvent(new SuiRideEvent(bindBody, null, found, 100), b, MessagePool.getMessage(b, MessagePool.Action.HateYukkuri));
				}
			}
		}
		else if(Sui.objEXList.size() > 0){
			EventLogic.addBodyEvent(b,new SuiSpeake(null, null, null, 10), null, null);
		}
		return ret;
	}
}


