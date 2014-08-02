package src.event;

import src.*;

/*
	お気に入りの情報を家族で共有するシンプルアクション
	protected Body from;			// イベントを発した個体
	protected Body to;				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class FavCopyEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public FavCopyEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	public boolean simpleEventAction(Body b) {
		if(from == b) return false;
		// イベントの発信者が家族かチェック
		if(b.isParent(from) || from.isParent(b) || b.isPartner(from)) {
			if (!Terrarium.acrossBarrier(b.getX(), b.getY(), from.getX(), from.getY(), Terrarium.MAP_BODY[b.getAgeState().ordinal()])) {
				b.setFavItem(Body.FavItemType.BED, from.getFavItem(Body.FavItemType.BED));
			}
		}
		return true;
	}
	
	public boolean checkEventResponse(Body b) {
		return false;
	}

	public void start(Body b) {
	}
	
	public boolean execute(Body b) {
		return true;
	}
}