package src.event;

import java.util.Random;

import src.*;
import src.object.Effect;
import src.object.Obj;
import src.system.Translate;
import src.yukkuriBody.Body;
import src.yukkuriBody.BodyLogic;
import src.yukkuriBody.ConstantValues;

/*
	ゆっくりが攻撃されたときの反撃イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 攻撃対象
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class RevengeAttackEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();

	public RevengeAttackEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	// 参加チェック
	public boolean checkEventResponse(Body b) {
		// これは特殊な扱いをするイベントで先に条件をチェックしてから
		// 自分自身のリストに登録するので無条件にtrue
		return true;
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
		if(!to.isRemoved() && to.getZ() < 5) {
			b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RevengeAttack), ConstantValues.HOLDMESSAGE, true, false);
			if(b.getDirection() == ConstantValues.Direction.LEFT) {
				SimYukkuri.mypane.terrarium.addEffect(Effect.EffectType.HIT, b.getX()-10, b.getY(), 0,
														0, 0, 0, false, 500, 1, true, false, true);
			} else {
				SimYukkuri.mypane.terrarium.addEffect(Effect.EffectType.HIT, b.getX()+10, b.getY(), 0,
														0, 0, 0, true, 500, 1, true, false, true);
			}
			b.setForceFace(ConstantValues.PUFF);
			to.strikeByYukkuri(b, this);
			b.addStress(-500);
		}
		return true;
	}
}