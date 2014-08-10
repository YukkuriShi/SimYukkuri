package src.event;

import src.*;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.FavItemType;
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
		if(getFrom() == b) return false;
		// イベントの発信者が家族かチェック
		if(b.isParent(getFrom()) || getFrom().isParent(b) || b.isPartner(getFrom())) {
			if (!Terrarium.acrossBarrier(b.getX(), b.getY(), getFrom().getX(), getFrom().getY(), Terrarium.MAP_BODY[b.getAgeState().ordinal()])) {
				b.setFavItem(FavItemType.BED, getFrom().getFavItem(FavItemType.BED));
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