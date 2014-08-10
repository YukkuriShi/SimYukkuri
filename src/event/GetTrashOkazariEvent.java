package src.event;

import src.*;
import src.object.Obj;
import src.yukkuriBody.Body;
import src.yukkuriBody.Moving;


/*
	ゴミからおかざり入手イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 未使用
	protected Obj target;			// ガラクタ
	protected int count;			// 1
*/
public class GetTrashOkazariEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public GetTrashOkazariEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	public boolean checkEventResponse(Body b) {
		
		priority = EventPriority.MIDDLE;
		return true;
	}

	// イベント開始動作
	public void start(Body b) {
		Moving.moveToEvent(b, this, target.getX(), target.getY());
	}

	public UpdateState update(Body b) {
		if(target.isRemoved()) return UpdateState.ABORT;
		if(b.hasOkazari()) return UpdateState.ABORT;
		Moving.moveToEvent(b, this, target.getX(), target.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		if(target.isRemoved()) return true;
		// おかざりランダム入手
		b.giveOkazari(TrashUtil.getRandomOkazari(b.getAgeState()));
		return true;
	}
}