package src.event;

import java.util.Random;

import src.*;
import src.object.Obj;
import src.system.Translate;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues.*;

/*
	出産時の励ましイベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 2
*/
public class BreedEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();

	public BreedEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	public boolean checkEventResponse(Body b) {
		// このイベントは固体どうしのイベントだが親子関係の探索が面倒なので
		// ワールドイベントとして登録、受け取り側が自分のつがいか親かを確認する
		boolean ret = false;

		priority = EventPriority.MIDDLE;

		if(b.isUnBirth()) return false;
		if(getFrom() == b) return false;

		// 自分が馬鹿で親におかざりがなかったら参加しない
		if(!getFrom().hasOkazari() && b.getIntelligence() == Intelligence.FOOL) return false;

		if(getFrom().isParent(b) || getFrom().isPartner(b) || b.isParent(getFrom()) || b.isPartner(getFrom())) return true;

		return ret;
	}

	// イベント開始動作
	public void start(Body b) {
		b.moveToEvent(this, getFrom().getX(), getFrom().getY());
	}
	
	// 毎フレーム処理
	// trueを返すとイベント終了
	public UpdateState update(Body b) {
		// 相手の一定距離まで近づいたら移動終了
		if(Translate.distance(b.getX(), b.getY(), getFrom().getX(), getFrom().getY()) < 20000) {
			b.moveToEvent(this, b.getX(), b.getY());
			return UpdateState.FORCE_EXEC;
		} else {
			b.moveToEvent(this, getFrom().getX(), getFrom().getY());
		}
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		// 相手が出産前なら応援
		if(getFrom().isBirth()) {
			b.setHappiness(Happiness.AVERAGE);
			b.lookTo(getFrom().getX(), getFrom().getY());
			b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RootForPartner), 40, false, false);
			return false;
		} else {
			// 誕生
			if(!getFrom().hasBabyOrStalk()) {
				b.lookTo(getFrom().getX(), getFrom().getY());
				if(getFrom().hasPants()) {
					b.setHappiness(Happiness.VERY_SAD);
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Surprise), 40, true, true);
					b.addStress(1000);
				} else {
					b.setHappiness(Happiness.VERY_HAPPY);
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FirstGreeting), 40, true, false);
					b.setStress(0);
				}
			} else {
				b.setHappiness(Happiness.AVERAGE);
				b.lookTo(getFrom().getX(), getFrom().getY());
				return false;
			}
			return true;
		}
	}
}