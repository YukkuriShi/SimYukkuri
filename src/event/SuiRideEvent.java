package src.event;

import java.util.Random;

import src.EventPacket;
import src.MessagePool;
import src.Terrarium;
import src.item.Sui;
import src.object.Obj;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.FavItemType;
import src.yukkuriBody.ConstantValues.Happiness;
import src.yukkuriLogic.EventLogic;

/*
	すぃーの乗車管理イベント
	protected Body from;			// 乗るゆっくり
	protected Body to;				// 未使用
	protected Obj target;			// すぃー
	protected int count;			// 100
*/
public class SuiRideEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();
	int tick = 0;
	boolean memberride=false;

	public SuiRideEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	public boolean checkEventResponse(Body b) {
//		boolean ret = false;
		if(getFrom() == b) {
			return true;
		}
		else if(getFrom().getCurrentEvent()==this) {
			if(b.isParent(getFrom()) || getFrom().isParent(b) || b.isPartner(getFrom()) || getFrom().isSister(b)){
				if (b.isDead() || b.isSleeping() || b.isExciting() || b.isScare()) {
					return false;
				}
				b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FindGetSuiOtner), ConstantValues.HOLDMESSAGE, true, false);
				return true;
			}
		}
	
		return false;
	}

	// イベント開始動作
	public void start(Body b) {
		b.moveToEvent(this, target.getX(), target.getY());
	}
	
	// 毎フレーム処理
	public UpdateState update(Body b) {
//		System.out.println(tick);
		Sui s = (Sui)target;
		if(b.getFavItem(FavItemType.SUI) != null) {
			
			if(getFrom() == b) {
				if(s.getcurrent_bindbody_num() >= 3 || tick > 50) {
					b.setHappiness(Happiness.HAPPY);
					if(s.getcurrent_condition() == 1 ){
						
						if(!memberride || tick%50==0) {
							b.moveTo(rnd.nextInt(Terrarium.MAX_X), rnd.nextInt(Terrarium.MAX_Y-Sui.getBounding().height/2));
						}
						if(tick > 500) {
							if(!b.isTalking()) {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.RideOffSui), true);
							}
							s.rideOff(b);
							return UpdateState.ABORT;
						}
					}
					else {
						if(!b.isTalking()) {
							if(rnd.nextBoolean()) {
								b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RidingSui), ConstantValues.HOLDMESSAGE, true, false);
							}
							else{
								b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.DrivingSui), ConstantValues.HOLDMESSAGE, true, false);
							}
						}
						if(rnd.nextInt(100)==0){
							EventLogic.addWorldEvent(new SuiSpeake(null, null, target, 1), null, null);
						}
					}
				}
				tick++;
			}
			else{
				if(!b.isTalking() && s.getcurrent_condition() != 1 ) {
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RidingSui), ConstantValues.HOLDMESSAGE, true, false);
				}
				if(getFrom().getCurrentEvent()!=this && s.getcurrent_condition() == 1 ) {
					if(!b.isTalking()) {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.RideOffSui), true);
					}
					s.rideOff(b);
					return UpdateState.ABORT;
				}
			}
		} else{
			
			b.moveToEvent(this, target.getX(), target.getY());
			if(getFrom()==b && s.iscanriding() || s.getcurrent_bindbody_num() >= 3 ){
				memberride=false;
				return UpdateState.ABORT;
			}
			if(getFrom() != b && getFrom().getCurrentEvent()==null){
				memberride=false;
				return UpdateState.ABORT;
			}
			if (b.isDead() || b.isSleeping() || b.isExciting() || b.isScare()) {
				memberride=false;
				return UpdateState.ABORT;
			}
			if(getFrom() != b && getFrom().getFavItem(FavItemType.SUI) != null
					&& b.getFavItem(FavItemType.SUI) == null && memberride==false && rnd.nextBoolean()) {
				if(!b.isTalking()){
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantRideSuiOtner), true);
				}
				getFrom().moveTo(b.getX(), b.getY());
				memberride=true;
			}
		}
		return null;
	}	
	
	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		Sui s = (Sui)target;
		if(b.getFavItem(FavItemType.SUI) == null && s.getcurrent_condition() == 1 ){
			if(b == getFrom() || (b != getFrom() && getFrom().getFavItem(FavItemType.SUI) != null)){
				s.rideOn(b);
				memberride=false;
			}
		}
		return false;
	}
	
	public void end(Body b) {
		memberride = false;
	}
}