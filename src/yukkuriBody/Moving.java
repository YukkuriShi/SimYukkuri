package src.yukkuriBody;

import src.EventPacket;
import src.MessagePool;
import src.Terrarium;
import src.item.Bed;
import src.item.Food;
import src.object.Obj;
import src.object.ObjEX;
import src.system.Translate;
import src.yukkuriBody.ConstantValues.AgeState;
import src.yukkuriBody.ConstantValues.Damage;
import src.yukkuriBody.ConstantValues.Direction;
import src.yukkuriBody.ConstantValues.FootBake;
import src.yukkuriBody.ConstantValues.Happiness;
import src.yukkuriBody.ConstantValues.Hunger;
import src.yukkuriBody.ConstantValues.Intelligence;
import src.yukkuriLogic.ToiletLogic;


public class Moving {
	public void moveToFood(Body body, Obj target, Food.type type, int toX, int toY) {
		moveToFood(body, target, type, toX, toY, 0);
	}

	public static void moveToFood(Body body, Obj target, Food.type type, int toX, int toY, int toZ) {
		body.clearActions();
		body.setToFood(true);
		body.moveTarget = target;
		body.moveTo(toX, toY, toZ);
	}

	public static void moveToSukkiri(Body body, Obj target, int toX, int toY) {
		moveToSukkiri(body,target, toX, toY, 0);
	}

	public static void moveToSukkiri(Body body, Obj target, int toX, int toY, int toZ) {
		body.clearActions();
		body.toSukkiri = true;
		body.moveTarget = target;
		body.moveTo(toX, toY, toZ);
	}

	public static void moveToToilet(Body body, Obj target, int toX, int toY) {
		moveToToilet(body, target, toX, toY, 0);
	}

	public static void moveToToilet(Body body, Obj target, int toX, int toY, int toZ) {
		body.clearActions();
		body.setToShit(true);
		body.moveTarget = target;
		body.moveTo(toX, toY, toZ);
	}

	public void moveToBed(Body body, Obj target, int toX, int toY) {
		moveToBed(body, target, toX, toY, 0);
	}

	public static void moveToBed(Body body, Obj target, int toX, int toY, int toZ) {
		body.clearActions();
		body.setToBed(true);
		body.moveTarget = target;
		body.moveTo(toX, toY, toZ);
	}

	public void moveToBody(Body body, Obj target, int toX, int toY) {
		moveToBody(body, target, toX, toY, 0);
	}

	public static void moveToBody(Body body, Obj target, int toX, int toY, int toZ) {
		body.clearActions();
		body.toBody = true;
		body.moveTarget = target;
		body.moveTo(toX, toY, toZ);
	}

	public static void moveToEvent(Body body, EventPacket e, int toX, int toY) {
		moveToEvent(body, e, toX, toY, 0);
	}

	public static void moveToEvent(Body body, EventPacket e, int toX, int toY, int toZ)
	{
		if (body.dead) {
			return;
		}
		e.toX = toX;
		e.toY = toY;
		e.toZ = toZ;
		body.destX = Math.max(0, Math.min(toX, Terrarium.MAX_X));
		body.destY = Math.max(0, Math.min(toY, Terrarium.MAX_Y));
		body.destZ = Math.max(0, Math.min(toZ, Terrarium.MAX_Z));
	}
	
	public static void moveBody(Body body, boolean dontMove) {
		if (body.pinnedDontMove || body.burnedDontMove)
		{
			return;
		}
		if (body.grabbed || body.linkParent != null) {
			// if grabbed, it cannot move.
			body.falldownDamage = 0;
			return;
		}

		// faced with poo-poo multiple time. became very sad
		if(body.shitStress > 200)
		{
			body.setHappiness(Happiness.VERY_SAD);
			body.setMessage(MessagePool.getMessage(body, MessagePool.Action.HateShit), false);
			body.clearActions();
			body.stay();
			body.shitStress -= 40;
			body.shitPanicEscape = true;
			return;
		}
		if(body.shitStress < 125 && body.shitPanicEscape == true)
			body.shitPanicEscape = false;
		
		checkFalling(body);

		body.x = Math.max(0, body.x);
		body.x = Math.min(body.x, Terrarium.MAX_X);
		body.y = Math.max(0, body.y);
		body.y = Math.min(body.y, Terrarium.MAX_Y);
		body.z = Math.max(0, body.z);
		body.z = Math.min(body.z, Terrarium.MAX_Z);

		if (dontMove || body.lockmove) {
			return;
		}

		// moving
		int step = body.STEP[body.getBodyAgeState().ordinal()];
		if (body.hasBabyOrStalk() || body.hungryState == Hunger.VERY || body.damageState == Damage.VERY
				||body.isOnFire() || body.isSick() || body.isFeelPain() || body.getFootBakeLevel() == FootBake.MIDIUM
				|| (body.isFlyingType() && !body.canflyCheck())) {
			step /= 2;
			if (step == 0) {
				step = 1;
			}
		}

		int freq = body.STEP[AgeState.ADULT.ordinal()] / step;
		if (body.getAge() % freq != 0) {
			return;
		}
		
		// calculate x direction
		if (body.destX >= 0) {
			body.dirX = decideDirection(body.x, body.destX, 0);
			if (body.dirX == 0) {
				body.destX = -1;
			}
		} else {
			if (body.countX++ >= body.sameDest * body.STEP[body.getBodyAgeState().ordinal()]) {
				body.countX = 0;
				body.dirX = randomDirection(body.rnd.nextBoolean(), body.dirX);			
				if (!body.hasOkazari() && (body.isSad() || body.isVerySad())) {
					if (!body.isTalking() && body.rnd.nextInt(10) == 0) {
						body.setMessage(MessagePool.getMessage(body, MessagePool.Action.NoAccessory));
					}
				}
			}
		}
		// calculate y direction
		if (body.destY >= 0) {
			body.dirY = decideDirection(body.y, body.destY, 0);
			if (body.dirY == 0) {
				body.destY = -1;
			}
		} else {
			if (body.countY++ >= body.sameDest * body.STEP[body.getBodyAgeState().ordinal()]) {
				body.countY = 0;
				body.dirY = randomDirection(body.rnd.nextBoolean(), body.dirY);
				if (!body.hasOkazari() && (body.isSad() || body.isVerySad())) {
					if (!body.isTalking() && body.rnd.nextInt(10) == 0) {
						body.setMessage(MessagePool.getMessage(body, MessagePool.Action.NoAccessory));
					}
				}
			}
		}
		// calculate z direction
		if(body.canflyCheck()) {
			if (body.destZ >= 0) {
				body.dirZ = decideDirection(body.z, body.destZ, 0);
				if (body.dirZ == 0) {
					body.destZ = -1;
				}
			}
			// 逶ｮ讓吶′辟｡縺代ｌ縺ｰ鬮伜ｺｦ繧剃ｿ昴▽繧医≧縺ｫ遘ｻ蜍�
			if(body.moveTarget == null && body.currentEvent == null) {
				body.destZ = Translate.getFlyHeightLimit();
			}
		}

		// move to the direction
		step = 1;
		if(body.isRaper() && body.isExciting()) step = 2;
		
		int vecX = body.dirX * step * body.speed / 100;
		int vecY = body.dirY * step * body.speed / 100;
		int vecZ = body.dirZ * step * body.speed / 100;
	
		// 譏守｢ｺ縺ｪ逶ｮ逧�慍縺後≠繧句�蜷医�陦後″驕弱℃繧偵メ繧ｧ繝�け
		if(body.moveTarget != null || body.currentEvent != null) {
			if(body.destX != -1) {
				if(body.dirX < 0) {
					if((body.x + vecX) < body.destX) {
						body.x = body.destX;
					} else body.x += vecX;
				} else if(body.dirX > 0) {
					if((body.x + vecX) > body.destX) {
						body.x = body.destX;
					} else body.x += vecX;
				}
			}

			if(body.destY != -1) {
				if(body.dirY < 0) {
					if((body.y + vecY) < body.destY) {
						body.y = body.destY;
					} else body.y += vecY;
				} else if(body.dirY > 0) {
					if((body.y + vecY) > body.destY) {
						body.y = body.destY;
					} else body.y += vecY;
				}
			}

			if(body.canflyCheck() && body.destZ != -1) {
				if(body.dirZ < 0) {
					if((body.z + vecZ) < body.destZ) {
						body.z = body.destZ;
					} else body.z += vecZ;
				} else if(body.dirZ > 0) {
					if((body.z + vecZ) > body.destZ) {
						body.z = body.destZ;
					} else body.z += vecZ;
				}
			}
		} else {
			body.x += vecX;
			body.y += vecY;
			body.z += vecZ;
		}
		
		// if yukkuri going towards a shit redirect to another direction
		// added by kirisame
		int nearestShitDistance = ToiletLogic.getMinimumShitDistance(body);
		if(nearestShitDistance < 1000 && nearestShitDistance != 0 && !body.isToFood() && !body.wantToShit() && !body.shitPanicEscape)
		{
			if(body.previousShitDistance > nearestShitDistance)
			{
				body.setMessage(MessagePool.getMessage(body, MessagePool.Action.HateShit), false);
				body.stay();
				body.dirX *= (body.rnd.nextBoolean() ? 1 : -1);
				body.dirY *= (body.rnd.nextBoolean() ? 1 : -1);
				body.destX = -1;
				body.destY = -1;
				body.staying = false;
				body.shitStress += 10*Obj.TICK;
			}
		}
		else
			if(!body.staying && body.shitStress > 0)
				body.shitStress -= Obj.TICK;
		
		body.previousShitDistance = nearestShitDistance;
		
		// 螢√メ繧ｧ繝�け
		if (Terrarium.onBarrier(body.x, body.y, body.getW() >> 2, body.getH() >> 3, Terrarium.MAP_BODY[body.getBodyAgeState().ordinal()])) {
			body.x -= vecX;
			body.y -= vecY;
			body.z -= vecZ;
			// 鬢｡蟄占┻縺ｯ螢√↓縺ｲ縺｣縺九°繧�
			if (body.getIntelligence() == Intelligence.FOOL && ((body.destX >= 0) || (body.destY >= 0) || (body.destZ >= 0))) {
				body.blockedCount++;
				if (body.blockedCount > body.BLOCKEDLIMIT) {
					body.dirX *= -1;
					body.dirY *= -1;
					body.destX = -1;
					body.destY = -1;
					body.clearActions();
					body.setHappiness(Happiness.VERY_SAD);
				}
				else if (body.blockedCount > body.BLOCKEDLIMIT/2) {
					if (body.isRude()) {
						body.setAngry();
					} else {
						body.exciting = false;
						body.setHappiness(Happiness.SAD);
					}
				}
				if (!body.isTalking()) {
					body.setMessage(MessagePool.getMessage(body, MessagePool.Action.BlockedByWall));
				}
			} else {
				body.dirX = randomDirection(body.rnd.nextBoolean(), body.dirX);
				body.dirY = randomDirection(body.rnd.nextBoolean(), body.dirY);
			}
		} else {
			body.blockedCount = Math.max(0, body.blockedCount - 1);
		}
		if (body.x < 0) {
			body.x = 0;
			body.dirX = 1;
		} else if (body.x > Terrarium.MAX_X) {
			body.x = Terrarium.MAX_X;
			body.dirX = -1;
		}
		if (body.y < 0) {
			body.y = 0;
			body.dirY = 1;
		} else if (body.y > Terrarium.MAX_Y) {
			body.y = Terrarium.MAX_Y;
			body.dirY = -1;
		}
		if (body.z < 0) {
			body.z = 0;
		} else if (body.z > Terrarium.MAX_Z) {
			body.z = Terrarium.MAX_Z;
		}
		// update direction of the face
		if (body.dirX == -1) {
			body.direction = Direction.LEFT;
		} else if (body.dirX == 1) {
			body.direction = Direction.RIGHT;
		}
	}
	
	
	private static boolean checkFalling(Body body)
	{
		if (body.vx != 0) {
			body.x += body.vx;
			if (Terrarium.onBarrier(body.x, body.y, body.getW() >> 2, body.getH() >> 3, Terrarium.MAP_BODY[body.getBodyAgeState().ordinal()])) {
				body.x -= body.vx;
			}
			else if (body.x < 0) {
				body.falldownDamage += Math.abs(body.vx);
				body.x = 0;
				body.vx = 0;
			}
			else if (body.x > Terrarium.MAX_X) {
				body.falldownDamage += Math.abs(body.vx);
				body.x = Terrarium.MAX_X;
				body.vx = 0;
			}
		}

		if (body.vy != 0) {
			body.y += body.vy;
			if (Terrarium.onBarrier(body.x, body.y, body.getW() >> 2, body.getH() >> 3, Terrarium.MAP_BODY[body.getBodyAgeState().ordinal()])) {
				body.y -= body.vy;
			}
			else if (body.y < 0) {
				body.falldownDamage += Math.abs(body.vy);
				body.y = 0;
				body.vy = 0;
				body.dirY = 1;
			}
			else if (body.y > Terrarium.MAX_Y) {
				body.falldownDamage += Math.abs(body.vy);
				body.y = Terrarium.MAX_Y;
				body.vy = 0;
				body.dirY = -1;
			}
		}
		// 鬟幄｡後〒縺阪ｋ繧�▲縺上ｊ縺ｯvz縺ｫ繧医ｋ螟門鴨莉･螟悶〒縺ｯ鬮伜ｺｦ繧剃ｿ昴▽
		if (body.vz != 0 || (!body.canflyCheck() && body.z != 0)) {
			body.falldownDamage = (body.vz > 0 ? body.falldownDamage : 0);
			// if falling down, it cannot move to x-y axis
			body.vz += 1;
			body.z -= body.vz;
			body.falldownDamage += (body.vz > 0 ? body.vz : 0);
			if (body.z <= 0) {
				body.falldownDamage += Math.abs(body.vy);
				body.z = 0;
				body.vz = 0;
				body.vy = 0;
				body.vx = 0;
				int jumpLevel[] = {2, 2, 1};
				int damageCut = 1;
				if (body.falldownDamage >= 8/jumpLevel[body.getBodyAgeState().ordinal()]) {
					for (ObjEX bd: Bed.objEXList) {
						if ( (bd.getX() - bd.getW() >> 2) <= (body.getX()) && (body.getX()) <= (bd.getX() + bd.getW() >> 2) ){
							if ( (bd.getY() - bd.getH()*2/5 )<=(body.getY()) && (body.getY())<=(bd.getY()) ){
								damageCut = 4;
								break;
							}
						}
					}
					body.strike(body.falldownDamage*100*24*3/Terrarium.MAX_Z/damageCut);
					if (body.dead) {
						if (!body.silent) {
							body.setMessage(MessagePool.getMessage(body, MessagePool.Action.Dying));
							body.stay();
						}
						body.crashed = true;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public static int randomDirection(Boolean rand,int curDir) {
		switch (curDir) {
		case 0:
			curDir = (rand ? 1 : -1);
			break;
		case 1:
			curDir = (rand ? 0 : curDir);
			break;
		case -1:
			curDir = (rand ? 0 : curDir);
			break;
		}
		return curDir;
	}
	
	private static int decideDirection(int curPos, int destPos, int range) {
		if (destPos - curPos > range) {
			return 1;
		}
		else if (curPos - destPos > range) {
			return -1;
		}
		return 0;
	}
}
