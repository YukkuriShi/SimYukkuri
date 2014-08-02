package src.event;

import java.util.Random;

import src.*;

/*
	おかざりのないゆっくりへの攻撃イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 攻撃対象
	protected Obj target;			// 未使用
	protected int count;			// 10
*/
public class HateNoOkazariEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();

	public HateNoOkazariEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	public boolean checkEventResponse(Body b) {
		boolean ret = false;

		// 善良は参加しない
		if(b.isSmart()) return false;
		// 自分が賢い場合はおかざりがなくても家族を認識して参加しない
		if(b.getIntelligence() == Body.Intelligence.WISE) {
			if(to.isParent(b) || to.isPartner(b) || b.isParent(to) || b.isPartner(to)) return false;
		}

		// 自分がお飾りあり、健康で起きてたら参加チェック
		if(b.hasOkazari() && !b.isDamaged() && !b.isSleeping()) {
			// ドゲスは参加
			if(b.getAttitude() == Body.Attitude.SUPER_SHITHEAD) ret = true;
			else {
				// ゲス、普通は相手が瀕死じゃなければ参加
				if(!b.isDamaged()) {
					if(b.isRude() || rnd.nextBoolean()) ret = true;
				}
			}
		}
		
		if(ret) {
			if(from != b) {
				b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.HateYukkuri), Body.HOLDMESSAGE, true, false);
			}
		}
		return ret;
	}

	// イベント開始動作
	public void start(Body b) {
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
	}
	
	// 毎フレーム処理
	// trueを返すとイベント終了
	public UpdateState update(Body b) {
		// 相手が消えてしまったらイベント中断
		if(to.isRemoved()) return UpdateState.ABORT;
		// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
		if(Translate.distance(b.getX(), b.getY(), to.getX(), to.getY()) < 2500) {
			to.stay();
		}
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		// 相手が残っていたら攻撃
		if(!to.isDead() && !to.isRemoved() && to.getZ() < 5) {
			b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.HateYukkuri), Body.HOLDMESSAGE, true, false);
			if(b.getDirection() == Body.Direction.LEFT) {
				SimYukkuri.mypane.terrarium.addEffect(Effect.EffectType.HIT, b.getX()-10, b.getY(), 0,
														0, 0, 0, false, 500, 1, true, false, true);
			} else {
				SimYukkuri.mypane.terrarium.addEffect(Effect.EffectType.HIT, b.getX()+10, b.getY(), 0,
														0, 0, 0, true, 500, 1, true, false, true);
			}
			b.setForceFace(Body.PUFF);
			to.strikeByYukkuri(b, this);
			b.addStress(-500);
		}
		return true;
	}
}