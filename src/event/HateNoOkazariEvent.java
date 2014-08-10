package src.event;

import java.util.Random;

import src.*;
import src.yukkuriBody.Body;
import src.yukkuriBody.BodyLogic;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.Attitude;
import src.yukkuriBody.ConstantValues.Intelligence;
/*
	縺翫°縺悶ｊ縺ｮ縺ｪ縺�ｆ縺｣縺上ｊ縺ｸ縺ｮ謾ｻ謦�う繝吶Φ繝�
	protected Body from;			// 繧､繝吶Φ繝医ｒ逋ｺ縺励◆蛟倶ｽ�
	protected Body to;				// 謾ｻ謦�ｯｾ雎｡
	protected Obj target;			// 譛ｪ菴ｿ逕ｨ
	protected int count;			// 10
*/
public class HateNoOkazariEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();

	public HateNoOkazariEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	// 蜿ょ刈繝√ぉ繝�け
	// 縺薙％縺ｧ蜷�ｨｮ繝√ぉ繝�け繧定｡後＞縲√う繝吶Φ繝医∈蜿ょ刈縺吶ｋ縺九ｒ霑斐☆
	// 縺ｾ縺溘�繧､繝吶Φ繝亥━蜈亥ｺｦ繧ょｿ�ｦ√↓蠢懊§縺ｦ險ｭ螳壹〒縺阪ｋ
	public boolean checkEventResponse(Body b) {
		boolean ret = false;

		// 蝟�憶縺ｯ蜿ょ刈縺励↑縺�
		if(b.isSmart()) return false;
		// 閾ｪ蛻�′雉｢縺��蜷医�縺翫°縺悶ｊ縺後↑縺上※繧ょｮｶ譌上ｒ隱崎ｭ倥＠縺ｦ蜿ょ刈縺励↑縺�
		if(b.getIntelligence() == Intelligence.WISE) {
			if(to.isParent(b) || to.isPartner(b) || b.isParent(to) || b.isPartner(to)) return false;
		}

		// 閾ｪ蛻�′縺企｣ｾ繧翫≠繧翫�蛛･蠎ｷ縺ｧ襍ｷ縺阪※縺溘ｉ蜿ょ刈繝√ぉ繝�け
		if(b.hasOkazari() && !b.isDamaged() && !b.isSleeping()) {
			// 繝峨ご繧ｹ縺ｯ蜿ょ刈
			if(b.getAttitude() == Attitude.SUPER_SHITHEAD) ret = true;
			else {
				// 繧ｲ繧ｹ縲∵勸騾壹�逶ｸ謇九′轢墓ｭｻ縺倥ｃ縺ｪ縺代ｌ縺ｰ蜿ょ刈
				if(!b.isDamaged()) {
					if(b.isRude() || rnd.nextBoolean()) ret = true;
				}
			}
		}
		
		if(ret) {
			if(getFrom() != b) {
				b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.HateYukkuri), ConstantValues.HOLDMESSAGE, true, false);

			}
		}
		return ret;
	}

	// 繧､繝吶Φ繝磯幕蟋句虚菴�
	public void start(Body b) {
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
	}
	
	// 豈弱ヵ繝ｬ繝ｼ繝��逅�
	// true繧定ｿ斐☆縺ｨ繧､繝吶Φ繝育ｵゆｺ�
	public UpdateState update(Body b) {
		// 逶ｸ謇九′豸医∴縺ｦ縺励∪縺｣縺溘ｉ繧､繝吶Φ繝井ｸｭ譁ｭ
		if(to.isRemoved()) return UpdateState.ABORT;
		// 逶ｸ謇九↓霑ｽ縺�▽縺代↑縺�こ繝ｼ繧ｹ縺後≠繧九◆繧√�荳�ｮ夊ｷ晞屬縺ｾ縺ｧ霑代▼縺�◆繧臥嶌謇九ｒ蜻ｼ縺ｳ豁｢繧√ｋ
		if(Translate.distance(b.getX(), b.getY(), to.getX(), to.getY()) < 2500) {
			to.stay();
		}
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
		return null;
	}

	// 繧､繝吶Φ繝育岼讓吶↓蛻ｰ逹�＠縺滄圀縺ｫ蜻ｼ縺ｰ繧後ｋ
	// true繧定ｿ斐☆縺ｨ繧､繝吶Φ繝育ｵゆｺ�
	public boolean execute(Body b) {
		// 逶ｸ謇九′谿九▲縺ｦ縺�◆繧画判謦�
		if(!to.isDead() && !to.isRemoved() && to.getZ() < 5) {
			b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.HateYukkuri), ConstantValues.HOLDMESSAGE, true, false);
			if(b.getDirection() == ConstantValues.Direction.LEFT) {
				SimYukkuri.mypane.terrarium.addEffect(Effect.EffectType.HIT, b.getX()-10, b.getY(), 0,
														0, 0, 0, false, 500, 1, true, false, true);
			} else {
				SimYukkuri.mypane.terrarium.addEffect(Effect.EffectType.HIT, b.getX()+10, b.getY(), 0,
														0, 0, 0, true, 500, 1, true, false, true);
			}
			if (to.isPartner(b))
			{
				String partnerAttack = b.getNameE() + " take it easy, it's " + to.getNameE() + ", not some uneasy yukkuri!";
				to.setMessage(partnerAttack);
				String bPartnerAttack = "This shitty yukkuri is nothing like " + b.getNameE() + "'s beautiful " + to.getNameE() +". So go die easy!";
				to.setMessage(bPartnerAttack);
			}
			b.setForceFace(ConstantValues.PUFF);
			to.strikeByYukkuri(b, this);
			b.addStress(-500);
			to.reduceComplacencyTimer += 100;
		}
		return true;
	}
}