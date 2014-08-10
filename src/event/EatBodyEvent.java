package src.event;

import java.util.Random;

import src.*;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.*;
/*
	死体食事中におかざりを戻すイベント
	protected Body from;			// 死体
	protected Body to;				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 30
*/
public class EatBodyEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();
	int tick = 0;

	public EatBodyEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	public boolean checkEventResponse(Body b) {
		boolean ret = false;

		if(getFrom() == b) return false;

		priority = EventPriority.MIDDLE;
		// 自分が食事中でドゲス以外なら反応
		if(b.isEating() && b.getMoveTarget() == getFrom() && b.getAttitude() != Attitude.SUPER_SHITHEAD) {
			ret = true;
		}
		return ret;
	}

	// イベント開始動作
	public void start(Body b) {
		// ゆっくりが隠れないように死体の手前に出る
		b.moveToEvent(this, getFrom().getX() + 5, getFrom().getY() + 4);
	}
	
	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		// 複数の動作を順次行うのでtickで管理
		if(tick == 0) {
			// 固まる
			b.lookTo(getFrom().getX(), getFrom().getY());
			b.setForceFace(ConstantValues.NORMAL);
			b.stay();
		} else if(tick == 30) {
			// 驚く
			b.lookTo(getFrom().getX(), getFrom().getY());
			b.setForceFace(ConstantValues.SURPRISE);
			b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Surprise), 52, true, false);
			b.stay();
		} else if(tick == 80) {
			// 吐く
			b.lookTo(getFrom().getX(), getFrom().getY());
			b.setForceFace(ConstantValues.CRYING);
			b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Vomit), 62, true, false);
			int ofsX = Translate.invertX(b.getCollisionX()>>1, b.getY());
			if(b.getDirection() == Direction.LEFT) ofsX = -ofsX;
			SimYukkuri.mypane.terrarium.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b.getAgeState(), b.getShitType());
			b.stay();
		} else if(tick == 140) {
			// 善良ほどストレスを受ける
			switch(b.getAttitude()) {
				default:
					break;
				case VERY_NICE:
					b.addStress(4000);
					break;
				case NICE:
					b.addStress(2500);
					break;
				case AVERAGE:
					b.addStress(1500);
					break;
				case SHITHEAD:
					b.addStress(500);
					break;
			}
			b.setHappiness(Happiness.VERY_SAD);
			return true;
		} else {
			b.lookTo(getFrom().getX(), getFrom().getY());
			b.stay();
		}
		tick++;
		
		return false;
	}
}