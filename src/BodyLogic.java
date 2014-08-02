package src;

import java.util.Random;

import src.Body.PanicType;
import src.EventPacket.EventPriority;
import src.TrashUtil.OkazariType;
import src.event.*;
import src.yukkuri.Fran;
import src.yukkuri.Remirya;
import src.yukkuri.Sakuya;




/***************************************************
	ゆっくり同士の処理
 */
public class BodyLogic {
	
	private static final Random rnd = new Random();

	public static final boolean checkPartner(Body b) {
		if (b.isDead()) {
			return false;
		}
		if(b.isSleeping() || (!b.isExciting() && !b.isRude() && b.wantToShit()) || b.nearToBirth()){
			return false;
		}

		boolean ret = false;

		// 対象が決まっていたら到達したかチェック
		if((b.toBody || b.toSukkiri) && b.moveTarget instanceof Body) {
			Body p = (Body)b.moveTarget;
			
			// 途中で消されてたら他の候補を探す
			if(p.removed) {
				b.clearActions();
				return false;
			}
			// 相手が宙に浮いてたら無視
			if (!b.canflyCheck() && p.getZ() != 0) {
				b.clearActions();
				return false;
			}
			int rangeX = Translate.invertX((int)((b.getCollisionX() + p.getCollisionX()) * 0.6f), p.getY());
			rangeX = Translate.transSize(rangeX);
			int distX = Math.abs(b.getX() - p.getX());
			int distY = Math.abs(b.getY() - p.getY());
			int range = Math.abs(rangeX - distX);
			// 見つかった相手に対するコリジョンチェック
			// 体が隣接するように横長のボックスで判定を取る
//System.out.println(range + "," + distY);
			if (range < 5 && distY < 3) {
	
				// 相手との距離が隣接状態と判断された場合
				if (!p.isDead()) {
					// 自分が発情してて相手が生きてたらすっきり実行
					if (b.isExciting()) {
						if(b.isRaper()) {
							if(b.getX() < p.getX()) {
								b.setDirection(Body.Direction.RIGHT);
							} else {
								b.setDirection(Body.Direction.LEFT);
							}
							b.constraintDirection(p, true);
							b.doRape(p);
						} else {
							b.constraintDirection(p, false);
							b.doSukkiri(p);
						}
					}
					else if (b.isAdult() && !p.isAdult() && p.isDirty() && (p.isChild(b) || b.isMotherhood(p))) {
						// 自分が母親で相手が汚れた子供ならぺろぺろ
						b.constraintDirection(p, false);
						b.doPeropero(p);
					}
					else if (b.isParent(p) && !p.isAdult() && rnd.nextInt(1) == 0) {
						// 家族ならぺろぺろ
						b.constraintDirection(p, false);
						b.doSurisuri(p);
					}
					else if (p.isPartner(b) && rnd.nextInt(1) == 0) {
						// 家族ならすりすり
						b.constraintDirection(p, false);
						b.doSurisuri(p);
					}
					else if (!b.isAdult() && b.isSister(p) && rnd.nextInt(1) == 0) {
						// 姉妹の場合のぺろぺろ
						if (b.isElderSister(p)) {
							b.constraintDirection(p, false);
							b.doPeropero(p);
						} else {
							b.constraintDirection(p, false);
							b.doSurisuri(p);
						}
					}
				} else {
					// 相手が死体の場合
				
					// 発情してたらすっきり
					if (b.isExciting()) {
						if(b.isRaper()) {
							b.doRape(p);
							b.clearActions();
						} else {
							b.doSukkiri(p);
						}
					}
					else if (b.isAdult()) {
						// 自分が成体で相手が家族なら嘆く
						if (!b.isTalking()) {
							if (b.isParent(p)) {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForChild));
								b.setHappiness(Body.Happiness.VERY_SAD);
							}
							else if (b.isPartner(p)) {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForPartner));
								b.setHappiness(Body.Happiness.VERY_SAD);
							}
							b.addStress(100);
						}
					}
					else if (b.isSister(p)){
						// 相手が姉妹なら嘆く
						if (!b.isTalking()) {
							if (b.age < p.age) {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForEldersister));
							}
							else {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForSister));
							}
							b.setHappiness(Body.Happiness.VERY_SAD);
							b.addStress(100);
						}
					}
				}
				b.clearActions();
				
			} else {
				int dir = 1;
				if(b.getX() < p.getX()) dir = -1;
				
				rangeX *= dir;
				if(b.canflyCheck()) {
					b.moveTo(p.getX() + rangeX, p.getY(), p.getZ());
				} else {
					b.moveTo(p.getX() + rangeX, p.getY());
				}
				// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
				if(Translate.distance(b.getX(), b.getY(), p.getX(), p.getY()) < 2500) {
					if(b.isTargetBind()) p.stay();
				}
			}
			return true;
		}

		if(b.currentEvent != null && b.currentEvent.getPriority() != EventPriority.LOW) return false;

		Body found = null;
		int minDistance = b.getEyesight();
		// 自分が発情していてつがいが既にいるなら優先して向かう
		if(b.isExciting() && b.partner != null && !b.partner.isDead() && !b.isRaper()) {
			found = b.partner;
			minDistance = Translate.distance(b.getX(), b.getY(), found.getX(), found.getY());
		}
		else if(b.isExciting() && b.isRaper() && b.toSukkiri && b.getMoveTarget() != null) {
			// レイパーですっきり中なら続けて同ターゲットに
			found = (Body)b.getMoveTarget();
			minDistance = Translate.distance(b.getX(), b.getY(), found.getX(), found.getY());
		} else {
			// 全ゆっくりに対してチェック
			for (Body p: Terrarium.bodyList) {
				// 自分同士のチェックは無意味なのでスキップ
				if (p == b) continue;
				// 相手が浮いてたらスキップ
				if(!b.canflyCheck() && p.getZ() != 0) continue;
				// れみりゃが通常種に近づいた場合
				/// 暫定処理 さくやは例外
				if(p.getType() != Sakuya.type) {
					if((b.getType() == Remirya.type || b.getType() == Fran.type) && p.getPanicType() == null) {
						if(!p.isDead() && !p.isPredatorType() && !b.isFamily(p)) {
							// 最高高度の半分以下なら相手が認識
							if(b.getZ() < Translate.getFlyHeightLimit()) {
								if (!Terrarium.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(), Terrarium.MAP_BODY[Body.AgeState.ADULT.ordinal()])) {
									p.setPanic(true, Body.PanicType.REMIRYA);
									p.setMessage(MessagePool.getMessage(p, MessagePool.Action.EscapeFromRemirya));
								}
							}
						}
					}
				}
				// 相手が死体で燃えていたら引火チェック
				if(p.isDead() && p.getPanicType() == PanicType.BURN) {
					int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
					if (dist <= Translate.distance(0, 0, b.getStep() * 2, b.getStep() * 2)) {
						b.giveFire();
						return true;
					}
				} else if (b.isExciting()) {
					// 自分が通常の発情なら成体以外はスキップ
					if (!b.isRaper()) {
						if (p.isDead() || !p.isAdult() || p.isChild(b) || p.isParent(b)) {
							continue;
						}
					} else {
						// レイパーの場合はレイパー以外を狙う
						if (p.isDead() || p.isUnBirth() || (p.isRaper() && p.isExciting())) {
							continue;
						}
					}
				} else if (p.isDead() && (!p.hasOkazari() || b.isIdiot())) {
					// 自分が馬鹿で相手がおかざりなしの死体なら食料扱いなのでスキップ
					continue;
				}
				int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
				if (minDistance > dist) {
					// 自分がゲスでなく、相手との間に壁があればスキップ
					if (!b.isRude()) {
						if (Terrarium.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(), Terrarium.MAP_BODY[b.getAgeState().ordinal()])) {
							continue;
						}
					}
					// 相手発見確定
					found = p;
					minDistance = dist;
				}
			}
		}
		
		// 目標が定まったら移動セット
		if (found != null) {
			int mz = 0;
			// 飛行種はZも移動可能
			if(b.canflyCheck()) {
				mz = found.getZ(); 
			}
			
			// ゆっくり同士が重ならないように目標地点は体のサイズを考慮
			int colX = calcCollisionX(b, found);
			
			// 相手が死体でなければ
			if (!found.isDead()) {
				// 自分が発情していればすっきりに向かう
				if (b.isExciting()) {
					b.moveToSukkiri(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(true);
					ret = true;
				}
				else if (!found.hasOkazari() && b.getOkazariType() == OkazariType.DEFAULT && b.isRude()
						&& !b.isDamaged() && !found.isUnBirth() && b.currentEvent == null) {
					// おかざりのないゆっくりなら制裁を呼びかける
					if (rnd.nextInt(20) == 0) {
						if (!b.isTalking()) {
							EventLogic.addWorldEvent(new HateNoOkazariEvent(b, found, null, 10), b, MessagePool.getMessage(b, MessagePool.Action.HateYukkuri));
						}
						ret = true;
					}
				}
				else if (b.isAdult() && !found.isAdult() && found.isDirty() && (found.isChild(b) || b.isMotherhood(found))) {
					// 相手が汚れた子供ならぺろぺろしに向かう
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(false);
					ret = true;
				}
				else if (b.isChild(found) && !b.isAdult() && b.isDirty()) {
					// 自分が汚れた子供なら家族のところへ向かう
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(true);
					ret = true;
				}
				else if (found.isPartner(b)) {
					// ランダムでつがいのところへ向かう
					if (rnd.nextInt(200) == 0) {
						b.moveToBody(found, found.getX() + colX, found.getY(), mz);
						b.setTargetBind(false);
						ret = true;
					}
				}
				else if (!b.isAdult() && b.isSister(found)) {
					// ランダムで姉妹のところへ向かう
					if (rnd.nextInt(200) == 0) {
						b.moveToBody(found, found.getX() + colX, found.getY(), mz);
						b.setTargetBind(false);
						ret = true;
					}
				}
			} else {
				// 死体相手の行動
				if (b.isExciting()) {
					// すっきり
					b.moveToSukkiri(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(false);
					ret = true;						
				}
				else if (rnd.nextInt(10) == 0) {
					// 家族の死体に嘆く
					if (b.isAdult()) {
						if (b.isParent(found) || b.isPartner(found)) {
							b.moveToBody(found, found.getX() + colX, found.getY(), mz);
							b.setTargetBind(false);
							ret = true;
						} else {
							b.lookTo(found.getX() + colX, found.getY());
						}
					} else {
						if (b.isSister(found)) {
							b.moveToBody(found, found.getX() + colX, found.getY(), mz);
							b.setTargetBind(false);
							ret = true;
						} else {
							b.runAway(found.getX() + colX, found.getY());
						}
					}
					// フィールドの死体に怯える
					if (!b.isTalking()) {
						if(b.isPredatorType() && found.isPredatorType() || !b.isPredatorType()) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Scare));
							b.setHappiness(Body.Happiness.SAD);
						}
					}
				}
			}
		}
		return ret;
	}
	
	// 体同士が触れる位置のX座標を求める
	public static final int calcCollisionX(Body from, Body to) {
		int colX = Translate.invertX((int)((from.getCollisionX() + to.getCollisionX()) * 0.6f), to.getY());
		colX = Translate.transSize(colX);
		
		// お互いの位置から右と左最短距離を選択
		int dir = 1;
		if(from.getX() < to.getX()) dir = -1;
		colX *= dir;

		return colX;
	}
}


