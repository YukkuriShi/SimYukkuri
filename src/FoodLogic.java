package src;

import java.awt.SecondaryLoop;
import java.util.Random;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import src.Body.Happiness;
import src.Body.Hunger;
import src.event.FlyingEatEvent;
import src.item.Food;
import src.yukkuri.Sakuya;


/***************************************************
	餌関係の処理
 */
public class FoodLogic {
	
	private static final Random rnd = new Random();
	private final static int CLEAN_FOOD_LIMIT = 3000;

	// フィールド内から餌候補の検索と移動、捕食処理
	public static final boolean checkFood(Body b) {

		if (b.isExciting() || b.isScare() || b.isSick() || b.isFeelHardPain() || b.nearToBirth()) {
			return false;
		}
		if(b.isRaper() && b.isExciting()) {
			return false;
		}
		if(b.currentEvent != null && b.currentEvent.getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}
		// トイレに行きたくなったら餌行動をキャンセル
		if (!b.isRude() && !b.isIdiot() && b.wantToShit() && !b.isSoHungry()) {
			b.clearActions();
			return false;
		}

		if(b.toFood)
			return goToFood(b);
		else if (!b.isEating())
			return findFood(b);
		else 
			return false;
	}
	
	private static boolean findFood(Body b)
	{		
		boolean[] forceEat = {false};
		Obj found = null;
		if(b.isSoHungry())
			b.cleanFood = true;

		// フィールドの餌検索
		if(b.isIdiot() || (b.getFootBakeLevel() == Body.FootBake.CRITICAL && !b.canflyCheck())) {
			// 足りないゆ、完全足焼き用
			found = searchFoodNearlest(b, forceEat);
		} else {
			if(b.isPredatorType()) {
				// 捕食種用
				found = searchFoodPredetor(b, forceEat);
			} else {
				// 通常種用
				if(b.cleanFood)
					found = searchFoodStanderd(b, forceEat,false);
				else
					found = searchFoodStanderd(b, forceEat, true);
			}
		}

		if (found != null) {
			b.cleanFood = true;
			// 発見した餌まで移動
			if (b.isHungry() || forceEat[0]) {
				int mz = 0;
				if(b.canflyCheck()) mz = found.getZ();

				// go to nearest food
				if (found instanceof Food) {
					if(((Food)found).getFoodType() == Food.type.SWEETS1 || ((Food)found).getFoodType() == Food.type.SWEETS2) {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.FindAmaama));
					} else {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantFood));
					}
					b.moveToFood(found, ((Food)found).getFoodType(), found.getX(), found.getY(), mz);
				}
				else if (found instanceof Shit){
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.NoFood), false);
					b.moveToFood(found, Food.type.SHIT, found.getX(), found.getY(), mz);
				}
				else if (found instanceof Body) {
					b.moveToFood(found, Food.type.BODY, found.getX(), found.getY(), mz);
				}
				else if (found instanceof Stalk) {//Food
					b.moveToFood(found, Food.type.STALK, found.getX(), found.getY(), mz);
				}
				else if (found instanceof Vomit){
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.NoFood), false);
					b.moveToFood(found, Food.type.VOMIT, found.getX(), found.getY(), mz);
				}
			return true;
			}
		} else {
			// フィールドに何もなかったらメッセージ
			if (b.isHungry()) {
				if (!b.isTalking() && (rnd.nextInt(10) == 0)) {
					b.toFood = false;
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.NoFood), false);
					b.stay();
					b.setHappiness(Body.Happiness.SAD);
				}
			}
		}
		return false;
	}

	private static boolean goToFood(Body b)
	{
		if(b.toFood && b.moveTarget != null) {
			// 途中で消されてたら他の餌候補を探す
			if(b.moveTarget.removed) {
				b.clearActions();
				return false;
			}
			// 食べることができなかったら他の餌候補を探す
			if(!b.canflyCheck() && b.moveTarget.getZ() != 0) {
				b.clearActions();
				return false;
			}
			if ((b.getStep() * b.getStep() + 2) >= Translate.distance(b.getX(), b.getY(), b.moveTarget.getX(), b.moveTarget.getY())) {
				boolean sweets = false;
				// 食べる処理
				if (!b.isTalking()) {
					if (b.moveTarget instanceof Food) {
						Food f = (Food)b.moveTarget;
						if(f.isEmpty()) {
							b.clearActions();
							return false;
						}
						
						if(ToiletLogic.getMinimumShitDistance(b) < 1000 && !b.isSoHungry() && !b.isEating())
						{
							b.clearActions();
							b.setHappiness(Happiness.SAD);
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateShit), false);
							b.cleanFood = false;
							
							return false;
						}
						b.cleanFood = true;
						
						eatFood(b, f.getFoodType(), Math.min(b.getEatAmount(), f.getAmount()));
						f.eatFood(Math.min(b.getEatAmount(), f.getAmount()));
						if(f.getFoodType() == Food.type.STALK && f.isEmpty()) f.remove();
						if(f.getFoodType() == Food.type.SWEETS1 || f.getFoodType() == Food.type.SWEETS2) {
							sweets = true;
						}
					}
					else if (b.moveTarget instanceof Shit) {
						Shit f = (Shit)b.moveTarget;
						eatFood(b, Food.type.SHIT, b.getEatAmount());
						f.eatShit(b.getEatAmount());						
					}
					else if (b.moveTarget instanceof Body) {
						Body f = (Body)b.moveTarget;
						if(!f.isDead()) {
							if(b.isPredatorType()) {
								// 捕食行動
								if(b.canflyCheck() && b.getAgeState().ordinal() > f.getAgeState().ordinal()) {
									// 空中での捕食は特殊なのでイベントで処理
									b.clearActions();
									EventLogic.addBodyEvent(b, new FlyingEatEvent(b, f, null, 1), null, null);
								} else {
									eatFood(b, Food.type.BODY, Math.min(b.getEatAmount(), f.getAmount()));
									f.eatBody(Math.min(b.getEatAmount(), f.getAmount()), b);
									if (f.isSick()) b.setSick();
								}
							} else {
								// 捕食種以外は生き返ってたらキャンセル
								b.clearActions();
								return false;
							}
						} else {
							// 死体食べ
							eatFood(b, Food.type.BODY, Math.min(b.getEatAmount(), f.getAmount()));
							f.eatBody(Math.min(b.getEatAmount(), f.getAmount()));
							if (f.isSick()) b.setSick();
						}
					}
					else if (b.moveTarget instanceof Stalk) {
						Stalk s = (Stalk)b.moveTarget;
						eatFood(b, Food.type.STALK, Math.min(b.getEatAmount(), s.getAmount()));
						s.eatStalk(Math.min(b.getEatAmount(), s.getAmount()));
					}
					else if (b.moveTarget instanceof Vomit) {
						Vomit f = (Vomit)b.moveTarget;
						eatFood(b, Food.type.VOMIT, b.getEatAmount());
						f.eatVomit(b.getEatAmount());						
					}
					// 満腹用セリフ
					if (b.isFull()) {
						if(sweets) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), false);
							b.eating = true;
							b.stay();
						} else {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Full), false);
							b.stay();
							b.clearActions();
						}
					}
				}
			}
			else
			{
				if(b.canflyCheck()) b.moveTo(b.moveTarget.getX(), b.moveTarget.getY(), 0);
				else b.moveTo(b.moveTarget.getX(), b.moveTarget.getY(), b.moveTarget.getZ());
			}
			return true;
		}
		return false;
	}
	
	
	private static final Obj searchFoodNearlest(Body b, boolean[] forceEat) {
		Obj found = null;
		int minDistance = b.getEyesight();
		int wallMode = b.getAgeState().ordinal();
	
		forceEat[0] = false;

		if(b.isFull()) return null;

		// 飛行可能なら壁以外は通過可能
		if(b.canflyCheck()) {
			wallMode = Body.AgeState.ADULT.ordinal();
		}

		// フィールドの餌検索
		for (ObjEX oex: Food.objEXList) {
			Food f = (Food)oex;

			if (f.isEmpty()) {
				continue;
			}
			int distance = Translate.distance(b.getX(), b.getY(), f.getX(), f.getY());
			if (minDistance > distance) {
				if (Terrarium.acrossBarrier(b.getX(), b.getY(), f.getX(), f.getY(), Terrarium.MAP_BODY[wallMode])) {
					continue;
				}
				found = f;
				minDistance = distance;
			}
		}
		for (ObjEX s:Stalk.objEXList){
			if (((Stalk)s).getPlantYukkuri() != null) {
				continue;
			}
			int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
			if (minDistance > distance) {
				if (Terrarium.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(), Terrarium.MAP_BODY[wallMode])) {
					continue;
				}
				found = s;
				minDistance = distance;
			}
		}
		for (Vomit v: Terrarium.vomitList) {
			int distance = Translate.distance(b.getX(), b.getY(), v.getX(), v.getY());
			if (minDistance > distance) {
				if (Terrarium.acrossBarrier(b.getX(), b.getY(), v.getX(), v.getY(), Terrarium.MAP_BODY[wallMode])) {
					continue;
				}
				found = v;
				minDistance = distance;
			}
		}
		for (Body d: Terrarium.bodyList) {
			if (b == d) continue;
			if (!d.isDead()) continue;
			if(b.isbindStalk()) continue;

			int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());
			if (minDistance > distance) {
				if (Terrarium.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(), Terrarium.MAP_BODY[wallMode])) {
					continue;
				}
				found = d;
				minDistance = distance;
			}			
		}
		for (Shit s: Terrarium.shitList) {
			int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
			if (minDistance > distance) {
				if (Terrarium.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(), Terrarium.MAP_BODY[wallMode])) {
					continue;
				}
				found = s;
				minDistance = distance;
			}
		}
		return found;
	}
	
	// 餌検索B
	// 一般用
	private static final Obj searchFoodStanderd(Body b, boolean[] forceEat,boolean secondFoodSource) {
		Obj found = null;
		int minDistance = b.getEyesight();
		int looks = -1000;
		int wallMode = b.getAgeState().ordinal();

		forceEat[0] = false;

		// 飛行可能なら壁以外は通過可能
		if(b.canflyCheck()) {
			wallMode = Body.AgeState.ADULT.ordinal();
		}

		// フィールドの餌検索
		for (ObjEX oex: Food.objEXList) {
			Food f = (Food)oex;
			
			if (f.isEmpty()) {
				continue;
			}
			int distance = Translate.distance(b.getX(), b.getY(), f.getX(), f.getY());
			if (minDistance > distance) {
				if (Terrarium.acrossBarrier(b.getX(), b.getY(), f.getX(), f.getY(), Terrarium.MAP_BODY[wallMode])) {
					continue;
				}
				
				boolean flag = false;
				
				switch(f.getFoodType()) {
					// 普通のフード
					default:
						// 空腹なら食べる
						if(!b.isFull()) flag = true;
						break;
					// 噛み砕いた茎
					case STALK:
						// 赤ゆなら食べる
						if(b.isBaby()) {
							flag = true;
							forceEat[0] = true;
						}
						// ゲスで空腹なら食べる
						else if(b.isRude() && b.isHungry()) flag = true;
						// 普通でも飢餓状態なら食べる
						else if(!b.isRude() && b.isTooHungry()) flag = true;
						
						break;
					// あまあま
					case SWEETS1:
					case SWEETS2:
						// 空腹なら食べる
						if(!b.isFull()) flag = true;
						// 普通以下なら満腹でも食べに行く
						else if(b.isRude() || b.isNormal()) {
							flag = true;
							forceEat[0] = true;
						}
						break;
					// 生ゴミ
					case WASTE:
						// 飢餓状態かバカ舌なら食べる
						if(b.isTooHungry() || b.getTangType() == Body.TangType.POOR) flag = true;
						break;
				}
				
				// 候補の中から最も価値の高いもの、近いものを食べに行く
				if(flag) {
					if(looks <= f.getLooks()) {
						if(secondFoodSource && Translate.distance(b.getX(), b.getY(), f.getX(), f.getY()) < 1000)
							continue;
						else
						{
							found = f;
							minDistance = distance;
							looks = f.getLooks();
						}
					}
				}
			}
		}
		
		if(found == null && b.isFull()) return found;

		// 非常食検索
		for (ObjEX s:Stalk.objEXList){
			if (((Stalk)s).getPlantYukkuri() != null) {
				continue;
			}
			int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
			if (minDistance > distance) {
				if (Terrarium.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(), Terrarium.MAP_BODY[wallMode])) {
					continue;
				}
				found = s;
				minDistance = distance;
			}
		}
		if(found == null) {
			for (Vomit v: Terrarium.vomitList) {
				int distance = Translate.distance(b.getX(), b.getY(), v.getX(), v.getY());
				if (minDistance > distance) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), v.getX(), v.getY(), Terrarium.MAP_BODY[wallMode])) {
						continue;
					}
					found = v;
					minDistance = distance;
				}
			}
		}
		if(found == null) {
			for (Body d: Terrarium.bodyList) {
				if(b == d) continue;
				if(!d.isDead()) continue;
				if(b.isbindStalk()) continue;
				if(b.getAttitude() != Body.Attitude.SUPER_SHITHEAD && d.hasOkazari()) continue;

				int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());
				if (minDistance > distance) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(), Terrarium.MAP_BODY[wallMode])) {
						continue;
					}
					found = d;
					minDistance = distance;
				}			
			}
		}
		if(found == null) {
			for (Shit s: Terrarium.shitList) {
				if (!b.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
				if (minDistance > distance) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(), Terrarium.MAP_BODY[wallMode])) {
						continue;
					}
					found = s;
					minDistance = distance;
				}
			}
		}
		return found;
	}

	// 餌検索C
	// 捕食種用
	private static final Obj searchFoodPredetor(Body b, boolean[] forceEat) {
		Obj found = null;
		Obj found2 = null;	// 副候補
		Obj found3 = null;	// 死体候補
		int minDistance = b.getEyesight();
		int minDistance2 = minDistance;
		int minDistance3 = minDistance;
		int size = b.getAgeState().ordinal();
		int looks = -1000;
		int wallMode = b.getAgeState().ordinal();

		forceEat[0] = false;
		
		// 飛行可能なら壁以外は通過可能
		if(b.canflyCheck()) {
			wallMode = Body.AgeState.ADULT.ordinal();
		}

		// ゆっくりから検索
		for (Body d: Terrarium.bodyList) {
			if(b == d) continue;
			
			if(!d.isDead()) {
				
				// 捕食種は食べない
				if(d.isPredatorType()) continue;
				// 家族は食べない
				if(b.isFamily(d)) continue;
				// 自分が飛べなかったら空中のは食べない
				if(!b.canflyCheck() && d.getZ() != 0) continue;
				/// 暫定処理 さくやは食べない
				if(d.getType() == Sakuya.type) continue;

				int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());

				if(d.getAgeState().ordinal() < b.getAgeState().ordinal()) {
					// 自分より小さい相手の場合
					if (minDistance > distance || d.getAgeState().ordinal() < size) {
						if (Terrarium.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(), Terrarium.MAP_BODY[wallMode])) {
							continue;
						}
						found = d;
						minDistance = distance;
						size = d.getAgeState().ordinal();
					}			
				} else {
					// 自分より同等以上の相手
					if (minDistance2 > distance) {
						if (Terrarium.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(), Terrarium.MAP_BODY[wallMode])) {
							continue;
						}
						found2 = d;
						minDistance2 = distance;
					}
				}
			} else {
				// 死体は第三候補
				
				// ゲス以外は家族の死体は食べない
				if(!b.isRude() && d.hasOkazari() && b.isFamily(d)) continue;
				
				int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());
				if (minDistance3 > distance) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(), Terrarium.MAP_BODY[wallMode])) {
						continue;
					}
					found3 = d;
					minDistance3 = distance;
				}			
			}
		}
		
		// 自分より小さい相手がいなかったら副目標にする
		if(found == null) found = found2;

		if(found == null) {
			// フィールドの餌検索
			for (ObjEX oex: Food.objEXList) {
				Food f = (Food)oex;
				
				if (f.isEmpty()) {
					continue;
				}
				int distance = Translate.distance(b.getX(), b.getY(), f.getX(), f.getY());
				if (minDistance > distance) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), f.getX(), f.getY(), Terrarium.MAP_BODY[wallMode])) {
						continue;
					}
					
					boolean flag = false;
					
					switch(f.getFoodType()) {
						// 普通のフード
						default:
							// 空腹なら食べる
							if(!b.isFull()) flag = true;
							break;
						// 噛み砕いた茎
						case STALK:
							// 赤ゆなら食べる
							if(b.isBaby()) {
								flag = true;
								forceEat[0] = true;
							}
							// ゲスで空腹なら食べる
							else if(b.isRude() && b.isHungry()) flag = true;
							// 普通でも飢餓状態なら食べる
							else if(!b.isRude() && b.isTooHungry()) flag = true;
							
							break;
						// あまあま
						case SWEETS1:
						case SWEETS2:
							// 空腹なら食べる
							if(!b.isFull()) flag = true;
							// 普通以下なら満腹でも食べに行く
							else if(b.isRude() || b.isNormal()) {
								flag = true;
								forceEat[0] = true;
							}
							break;
						// 生ゴミ
						case WASTE:
							// 飢餓状態かバカ舌なら食べる
							if(b.isTooHungry() || b.getTangType() == Body.TangType.POOR) flag = true;
							break;
					}
					
					// 候補の中から最も価値の高いもの、近いものを食べに行く
					if(flag) {
						if(looks <= f.getLooks()) {
							found = f;
							minDistance = distance;
							looks = f.getLooks();
						}
					}
				}
			}
		}
		
		if(found == null && b.isFull()) return found;

		// 非常食検索
		for (ObjEX s:Stalk.objEXList){
			if (((Stalk)s).getPlantYukkuri() != null) {
				continue;
			}
			int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
			if (minDistance > distance) {
				if (Terrarium.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(), Terrarium.MAP_BODY[wallMode])) {
					continue;
				}
				found = s;
				minDistance = distance;
			}
		}
		// 死体を見つけていたら食べる
		if(found == null) found = found3;

		if(found == null) {
			for (Vomit v: Terrarium.vomitList) {
				int distance = Translate.distance(b.getX(), b.getY(), v.getX(), v.getY());
				if (minDistance > distance) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), v.getX(), v.getY(), Terrarium.MAP_BODY[wallMode])) {
						continue;
					}
					found = v;
					minDistance = distance;
				}
			}
		}
		if(found == null) {
			for (Shit s: Terrarium.shitList) {
				if (!b.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
				if (minDistance > distance) {
					if (Terrarium.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(), Terrarium.MAP_BODY[wallMode])) {
						continue;
					}
					found = s;
					minDistance = distance;
				}
			}
		}
		return found;
	}

	// 食事処理
	public static final void eatFood(Body b, Food.type foodType, int amount) {
		if (b.isDead()) {
			return;
		}

		// 餌タイプ別のリアクションとステータス変化
		switch(b.getTangType()) {
			// バカ舌
			case POOR:
				poorEating(b, foodType);
			break;
			// 普通
			case NORMAL:
				normalEating(b, foodType);
			break;
			// 美食
			case GOURMET:
				gourmetEating(b, foodType);
			break;
		}
		// 食事実行
		int eatAmount = Math.min(b.getEatAmount(), amount);
		b.eatFood(eatAmount);
		b.checkTang();
	}
	
	// バカ舌状態でのリアクション
	private static final void poorEating(Body b, Food.type type) {
		switch(type) {
		case SHIT:
			b.setHappiness(Body.Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(100);
			b.tang -= 20;
		break;
		case BITTER:
			if(!b.likeBitterFood) {
				b.strike(Body.NEEDLE * 3);
				b.setHappiness(Body.Happiness.SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				b.strike = true;
				b.rapidShit();
				b.addStress(250);
			} else {
				b.setHappiness(Body.Happiness.VERY_HAPPY);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-500);
				b.tang += 200;
			}
		break;
		case LEMONPOP:
			b.setHappiness(Body.Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.forceToSleep();
			b.addStress(-500);
			b.tang += 50;
		break;
		case HOT:
			if(!b.likeHotFood) {
				b.strike(Body.HAMMER >> 1);
				b.setHappiness(Body.Happiness.SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				b.strike = true;
				b.addStress(500);
			} else {
				b.setHappiness(Body.Happiness.VERY_HAPPY);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-500);
				b.tang += 200;
			}
		break;
		case VIYUGRA:
			b.setHappiness(Body.Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.rapidExcitingDiscipline();
			if ( b.superRapist == false && rnd.nextInt(10) == 0) {
				b.superRapist = true;
				b.rapist = true;
			}
		break;
		case BODY:
			b.setHappiness(Body.Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), Body.HOLDMESSAGE, true, true);
			b.addStress(-500);
			b.tang += 50;
		break;
		case STALK:
			b.setHappiness(Body.Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-500);
			b.addDamage(-500);
		break;
		case SWEETS1:
			b.setHappiness(Body.Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), Body.HOLDMESSAGE, true, true);
			b.setStress(0);
			b.tang += 200;
		break;
		case SWEETS2:
			b.setHappiness(Body.Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), Body.HOLDMESSAGE, true, true);
			b.setStress(0);
			b.tang += 1000;
		break;
		case WASTE:
			b.setHappiness(Body.Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.dirtyPeriod += Body.TICK * 4;
			b.addStress(-100);
			b.tang -= 30;
		break;
		case VOMIT:
			b.setHappiness(Body.Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), Body.HOLDMESSAGE, true, true);
			b.setStress(0);
			b.tang += 100;
		break;
		default:
			b.setHappiness(Body.Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), Body.HOLDMESSAGE, true, true);
			b.addStress(-300);
			b.tang += 100;
		break;
		}
	}

	// 普通状態でのリアクション
	private static final void normalEating(Body b, Food.type type) {
		switch(type) {
		case SHIT:
			b.setHappiness(Body.Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingShit));
			b.eatingShit = true;
			b.addStress(200);
			b.tang -= 10;
		break;
		case BITTER:
			if(!b.likeBitterFood) {
				b.strike(Body.NEEDLE * 6);
				b.setHappiness(Body.Happiness.SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				b.strike = true;
				b.rapidShit();
				b.addStress(300);
			} else {
				b.setHappiness(Body.Happiness.HAPPY);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-200);
				b.tang += 20;
			}
		break;
		case LEMONPOP:
			b.setHappiness(Body.Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.forceToSleep();
			b.addStress(-200);
			b.tang += 20;
		break;
		case HOT:
			if(!b.likeHotFood) {
				b.strike(Body.HAMMER);
				b.setHappiness(Body.Happiness.VERY_SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				b.strike = true;
				b.addStress(800);
			} else {
				b.setHappiness(Body.Happiness.HAPPY);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-200);
				b.tang += 20;
			}
		break;
		case VIYUGRA:
			b.setHappiness(Body.Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.rapidExcitingDiscipline();
			if ( b.superRapist == false && rnd.nextInt(10) == 0) {
				b.superRapist = true;
				b.rapist = true;
			}
		break;
		case BODY:
			b.setHappiness(Body.Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
			b.tang += 10;
		break;
		case STALK:
			b.setHappiness(Body.Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-200);
			b.addDamage(-500);
		break;
		case SWEETS1:
			b.setHappiness(Body.Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), Body.HOLDMESSAGE, true, true);
			b.setStress(0);
			b.tang += 100;
		break;
		case SWEETS2:
			b.setHappiness(Body.Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), Body.HOLDMESSAGE, true, true);
			b.setStress(0);
			b.tang += 1000;
		break;
		case WASTE:
			b.setHappiness(Body.Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
			b.strike = true;
			b.dirtyPeriod += Body.TICK * 4;
			b.addStress(100);
			b.tang -= 30;
		break;
		case VOMIT:
			b.setHappiness(Body.Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
			b.tang += 50;
		break;
		default:
			b.setHappiness(Body.Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
		break;
		}
	}

	// 肥え状態でのリアクション
	private static final void gourmetEating(Body b, Food.type type) {
		switch(type) {
		case SHIT:
			b.setHappiness(Body.Happiness.VERY_SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingShit));
			b.eatingShit = true;
			b.addStress(500);
			b.tang -= 20;
		break;
		case BITTER:
			if(!b.likeBitterFood) {
				b.strike(Body.NEEDLE * 10);
				b.setHappiness(Body.Happiness.VERY_SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingShit));
				b.rapidShit();
				b.addStress(300);
			} else {
				b.setHappiness(Body.Happiness.AVERAGE);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-100);
				b.tang -= 20;
			}
		break;
		case LEMONPOP:
			b.setHappiness(Body.Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.forceToSleep();
			b.addStress(-100);
			b.tang -= 20;
		break;
		case HOT:
			if(!b.likeHotFood) {
				b.strike(Body.HAMMER * 2);
				b.setHappiness(Body.Happiness.VERY_SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				b.strike = true;
				b.addStress(1200);
			} else {
				b.setHappiness(Body.Happiness.AVERAGE);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-100);
				b.tang -= 20;
			}
		break;
		case VIYUGRA:
			b.setHappiness(Body.Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.rapidExcitingDiscipline();
			if ( b.superRapist == false && rnd.nextInt(10) == 0) {
				b.superRapist = true;
				b.rapist = true;
			}
		break;
		case BODY:
			b.setHappiness(Body.Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
			b.strike = true;
			b.addStress(100);
			b.tang -= 30;
		break;
		case STALK:
			b.setHappiness(Body.Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
			b.addDamage(-500);
		break;
		case SWEETS1:
			b.setHappiness(Body.Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama));
			b.setStress(0);
			b.tang += 100;
		break;
		case SWEETS2:
			b.setHappiness(Body.Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama));
			b.setStress(0);
			b.tang += 1000;
		break;
		case WASTE:
			b.setHappiness(Body.Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
			b.strike = true;
			b.dirtyPeriod += Body.TICK * 4;
			b.addStress(500);
			b.tang -= 50;
		break;
		case VOMIT:
			b.setHappiness(Body.Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-50);
			b.tang += 50;
		break;
		default:
			b.setHappiness(Body.Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(100);
			b.tang -= 10;
		break;
		}
	}
}


