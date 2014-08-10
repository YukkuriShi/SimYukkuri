package src.event;

import java.util.Random;

import src.*;
import src.item.Food;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.Happiness;
import src.yukkuriLogic.FoodLogic;


/*
	空中捕食イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 捕食対象
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class FlyingEatEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private static final int[] ofsZ = {2, 0, -5};
	int tick = 0;
	Random rnd = new Random();

	public FlyingEatEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	// 参加チェック
	public boolean checkEventResponse(Body b) {
		priority = EventPriority.HIGH;
		return true;
	}

	// イベント開始動作
	public void start(Body b) {
		b.moveToEvent(this, b.getX(), b.getY(),  Translate.getFlyHeightLimit());
		to.setLinkParent(b);
	}
	
	// 毎フレーム処理
	// trueを返すとイベント終了
	public UpdateState update(Body b) {
		// 相手が消えてしまったらイベント中断
		if(to.isRemoved()) {
			to.setLinkParent(null);
			return UpdateState.ABORT;
		}
		// 相手が捕まれたらイベント中断
		if(to.isGrabbed()) {
			to.setLinkParent(null);
			return UpdateState.ABORT;
		}
		// 相手が死んだらイベント中断
		if(to.isDead()) {
			to.setLinkParent(null);
			return UpdateState.ABORT;
		}
		// 相手の座標を縛る
		to.setX(b.getX());
		to.setY(b.getY() + 1);
		to.setZ(b.getZ() + ofsZ[to.getAgeState().ordinal()]);
		
		// 高度に達してたらexecuteへ
		if(Math.abs(b.getZ() - Translate.getFlyHeightLimit()) < 3) return UpdateState.FORCE_EXEC;
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		// 相手が消えてしまったらイベント中断
		if(to.isRemoved()) {
			to.setLinkParent(null);
			return true;
		}
		// 相手が捕まれたらイベント中断
		if(to.isGrabbed()) {
			to.setLinkParent(null);
			return true;
		}
		
		tick++;
		if(tick == 20) {
			tick = 0;

			FoodLogic.eatFood(b, Food.type.BODY, Math.min(b.getEatAmount(), to.getAmount()));
			to.eatBody(Math.min(b.getEatAmount(), to.getAmount()));
			if (to.isSick()) b.setSick();
			if(to.isDead()) {
				to.setMessage(MessagePool.getMessage(to, MessagePool.Action.Dead));
				to.setLinkParent(null);
				return true;
			} else {
				to.setMessage(MessagePool.getMessage(to, MessagePool.Action.EatenByBody2));
				to.setHappiness(Happiness.VERY_SAD);
				to.setForceFace(ConstantValues.PAIN);
			}
		}
		return false;
	}
	
	// イベント終了処理
	public void end(Body b) {
		to.setLinkParent(null);
	}
}