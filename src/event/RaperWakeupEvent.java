package src.event;

import src.*;
import src.yukkuriBody.Body;
import src.yukkuriLogic.EventLogic;

/*
	レイパー発情通知イベント
	protected Body from;			// レイパー
	protected Body to;				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class RaperWakeupEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public RaperWakeupEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	public boolean simpleEventAction(Body b) {
		// 自分自身はスキップ
		if(b == getFrom()) return false;
		// 死体、睡眠はスキップ
		if(b.isDead() || b.isSleeping()) return true;
		
		// 自分もレイパーなら連鎖して発情
		if(b.isRaper()) {
			b.forceToRaperExcite();
			return true;
		}
		
		// 一般人の反応
		// 固体ごとに異なる行動をするため新しいイベントのインスタンスを作成して固体イベントに登録
		EventLogic.addBodyEvent(b, new RaperReactionEvent(getFrom(), null, null, 1), null, null);
		return true;
	}

	// 参加チェック
	public boolean checkEventResponse(Body b) {
		return false;
	}

	// イベント開始動作
	public void start(Body b) {
	}
	
	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		return true;
	}
}