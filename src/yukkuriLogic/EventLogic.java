package src.yukkuriLogic;
import java.util.Iterator;

import src.EventPacket;
import src.Terrarium;
import src.EventPacket.UpdateState;
import src.system.Translate;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.*;
/***************************************************
	イベントの処理
 */
public class EventLogic {
	
	// ワールドイベントへの登録
	public static final void addWorldEvent(EventPacket event, Body msgBody, String message) {
		addWorldEvent(event, msgBody, message, ConstantValues.HOLDMESSAGE);
	}

	public static final void addWorldEvent(EventPacket event, Body msgBody, String message, int count) {
		Terrarium.eventList.add(event);
		if(msgBody != null) {
			msgBody.setWorldEventSendMessage(message, count);
		}
	}

	// 個体イベントへの登録
	public static final void addBodyEvent(Body to, EventPacket event, Body msgBody, String message) {
		addBodyEvent(to, event, msgBody, message, ConstantValues.HOLDMESSAGE);
	}

	public static final void addBodyEvent(Body to, EventPacket event, Body msgBody, String message, int count) {
		to.getEventList().add(event);
		if(msgBody != null) {
			msgBody.setBodyEventSendMessage(message, count);
		}
	}

	// ワールドイベントの有効期間チェック
	public static final void clockWorldEvent() {
		EventPacket e;

		for (Iterator<EventPacket> i = Terrarium.eventList.iterator(); i.hasNext();) {
			e = i.next();
			if(e.countDown()) {
				i.remove();
			}
		}
	}
	
	// ワールドイベントのチェック
	public static final EventPacket checkWorldEvent(Body b) {
		EventPacket ret = null;
		EventPacket e;
		
		for (Iterator<EventPacket> i = Terrarium.eventList.iterator(); i.hasNext();) {
			e = i.next();
		
//			if(e.from == b) continue;
			if(e.simpleEventAction(b)) {
				continue;
			}
			if(e.checkEventResponse(b)) {
				ret = e;
				break;
			}
		}
		return ret;
	}

	// 固体イベントのチェック
	public static final EventPacket checkBodyEvent(Body b) {
		EventPacket ret = null;
		EventPacket e;
		
		for (Iterator<EventPacket> i = b.getEventList().iterator(); i.hasNext();) {
			e = i.next();
			
			if(e.simpleEventAction(b)) {
				i.remove();
				continue;
			}
			if(ret == null) {
				if(e.checkEventResponse(b)) {
					ret = e;
					i.remove();
					continue;
				}
			}
			if(e.countDown()) {
				i.remove();
			}
		}
		return ret;
	}

	// ワールドイベントのチェック simpleEventAction用
	public static final void checkSimpleWorldEvent(Body b) {
		EventPacket e;
		
		for (Iterator<EventPacket> i = Terrarium.eventList.iterator(); i.hasNext();) {
			e = i.next();
		
			if(e.getFrom() == b) continue;
			if(e.simpleEventAction(b)) {
				continue;
			}
		}
	}

	// 固体イベントのチェック simpleEventAction用
	public static final void checkSimpleBodyEvent(Body b) {
		EventPacket e;
		
		for (Iterator<EventPacket> i = b.getEventList().iterator(); i.hasNext();) {
			e = i.next();
			
			if(e.simpleEventAction(b)) {
				i.remove();
				continue;
			}
		}
	}

	// イベントの毎フレーム処理
	public static final void eventUpdate(Body b) {
		EventPacket.UpdateState state = null;
		// フレーム更新
		state = b.getCurrentEvent().update(b);
		// ABORTが返されたらイベント中断
		if(EventPacket.UpdateState.ABORT == state) {
			b.getCurrentEvent().end(b);
			b.clearActions();
			return;
		}

		// 移動先に到達またはupdateがFORCE_EXECを返したらexecute呼び出し
		// 相手の消滅、死亡などのチェックはexecuteで行う
		if (EventPacket.UpdateState.FORCE_EXEC == state ||
				b.getZ() == 0 && (b.getStep() * b.getStep() + 2) >= Translate.distance(b.getX(), b.getY(), b.getCurrentEvent().toX, b.getCurrentEvent().toY)) {
			if(b.getCurrentEvent().execute(b)) {
				b.getCurrentEvent().end(b);
				b.clearActions();
			}
		}
	}
}


