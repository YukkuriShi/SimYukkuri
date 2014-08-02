package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/*****************************************************
	全キャラのメッセージ管理
*/

public class MessagePool
{
	// アクション名定義
	public enum Action {
		WantFood,		// 餌発見
		NoFood,		// 餌なし
		Excite,		// 発情
		Relax,		// リラックス
		FuriFuri,		// 尻振り
		Wakeup,		// 起床
		Sleep,		// 睡眠
		Scream,		// 悲鳴
		Scare,		// 怯え
		Alarm,		// 警戒
		Dying,		// 瀕死
		Dead,		// 死亡
		Eating,		// 食事
		EatingShit,		// 食事(うんうん)
		EatingBitter,		// 食事(毒)
		Full,		// 満腹
		Healing,		// 回復
		Sukkiri,		// すっきり
		RaperSukkiri,	// レイパーとすっきり
		Birth,		// 誕生
		Shit,		// 排泄
		Shit2,		// 排泄2
		SuriSuri,		// すりすり
		PeroPero,		// ぺろぺろ
		Breed,		// 出産
		Breed2,		// 産後
		HateShit,		// うんうん発見
		Hungry,		// 空腹
		NoAccessory,		// おかざり没収
		Flying,		// 持ち上げ
		SadnessForChild,		// 子供死亡
		SadnessForPartner,		// つがい死亡
		SadnessForEldersister,		// 姉死亡
		SadnessForSister,		// 妹死亡
		BlockedByWall,		// 壁発見
		GetTreasure,		// おもちゃ発見
		LostTreasure,		// おもちゃ紛失
		Inflation,		// 膨張
		AbuseBaby,		// 子虐待
		Nobinobi,		// のびのび
		NearToBirth,		// 出産前
		PoisonDamage,		// 毒
		CantShit,		// 排泄不可
		NoPregnancy,	// 妊娠失敗
		Fear,			// 恐怖でパニック状態	
		Burning,		// 火がついてる状態
		Wet,			// 水をかけられた
		AnalSealed,		// あにゃる閉鎖
		NeedleStick,	// 針が刺さった時
		NeedleScream,	// 針が刺さっている時
		ExtractingNeedlePartner, // つがいの針をぐーりぐりする
		ExtractingNeedleChild, // 子の針をぐーりぐりする
		NeedlePain,		// 被ぐーりぐり時
		NeedleRemove,	// 針を抜いた時
		WantAmaama,		// あまあま要求
		CantMove,		// 移動不能1
		CantMove2,		// 移動不能2
		Pull,			// 引っ張る
		Pull2,			// 引っ張る2
		Press,			// つぶす
		Press2,			// つぶす2
		FindAmaama,		// あまあま発見
		EatingAmaama,	// あまあま食事
		Vomit,			// 吐餡
		Dying2,			// 汎用、瀕死継続
		Surprise,		// 汎用、驚き
		RevengeAttack,	// 汎用、他のゆっくりに何かされたときに対する攻撃、反撃
		BraidCut,		// おさげ、尻尾、羽をちぎる
		EatenByBody,	// 汎用、他のゆっくりに生きたまま食べられる
		EatenByBody2,	// 汎用、他のゆっくりに生きたまま吸われる
		EscapeFromRemirya, // れみりゃから逃げる
		// イベント系メッセージ
		HateYukkuri,		// おかざりのないゆっくり発見
		ConcernAboutPartner, // つがいを気遣う
		ConcernAboutChild, // 子を気遣う
		ConcernAboutEldersister, // 姉を気遣う
		ConcernAboutSister, // 妹を気遣う
		RootForPartner,	// つがい出産時		
		FirstGreeting,	// 子への最初の挨拶
		ScareRapist,		// レイパー怯え
		AttackRapist,	// レイパーに攻撃
		CounterRapist,	// レイパーに反撃命令
		YukkuringSui,	// ゆっくりしているすぃーを見て
		WantingSui,		// すぃーを欲しがる
		WantingSuiPartner,		// すぃーを欲しがっているゆっくりのパートナー
		WantingSuiParent,	// すぃーを欲しがっているゆっくりの親
		FindSui,		// すぃーを見つける
		GetSui,			// すぃーを自分のものにする
		FindGetSui,			// 自分のすぃーにのりにいく
		FindGetSuiOtner,	// 自分以外のすぃーにのりにいく
		WantRideSuiOtner,	// 自分以外のすぃーにのりにいくときに呼ぶ
		RideSui,		// すぃーにのる
		RidingSui,		// すぃーにのっている
		DrivingSui,		// すぃーを運転する
		DrivingSuiPartner,		// すぃーを運転するパートナーをみて（自分はのっていない）
		DrivingSuiChild,	// すぃーを運転するこどもをみて（自分はのっていない）
		DrivingSuiPAPA,	// すぃーを運転する父親をみて（自分はのっていない）
		DrivingSuiMAMA,	// すぃーを運転する母親をみて（自分はのっていない）
		DrivingSuiOldSister,	// すぃーを運転する姉をみて（自分はのっていない）
		DrivingSuiYoungSister,	// すぃーを運転する妹をみて（自分はのっていない）
		RideOffSui,		// すぃーから降りる
		hasSui,			// 持っているすぃーを自慢する
		hasSuiPartner,	// 持っているすぃーを自慢されるパートナー
		hasSuiChild,	// 子どもの持っているすぃーを自慢される親
		hasSuiPAPAChild,	// 父親の持っているすぃーを自慢されるこども
		hasSuiMAMAChild,	// 母親の持っているすぃーを自慢されるこども
		hasSuiOldSister,	// 姉の持っているすぃーを自慢される
		hasSuiYoungSister,	// 妹の持っているすぃーを自慢される
	}

	// 埋め込み文字定義
	public enum Replace {
		name,
		name2,
		partner,
		dummy
	}

	private static Random rnd = new Random();
	private static HashMap<String, MessageMap>[] pool_j = null;

	// 全メッセージ読み込み
	@SuppressWarnings("unchecked")
	public static final void loadMessage(ClassLoader loader)
	{
		BufferedReader br = null;
		
		YukkuriUtil.YukkuriType[] yk = YukkuriUtil.YukkuriType.values();
		pool_j = new HashMap[yk.length];

		for(int i = 0; i < yk.length; i++)
		{
			pool_j[i] = new HashMap<String, MessageMap>();
			// 汎用メッセージ
			br = ModLoader.openMessageFile(loader, yk[i].messageFileName + "_j.txt");
			try {
				readMessageMap(br, pool_j[i]);
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// イベントメッセージ
			br = ModLoader.openMessageFile(loader, yk[i].messageFileName + "_ev_j.txt");
			try {
				readMessageMap(br, pool_j[i]);
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 1ファイル読み込み
	private static final void readMessageMap(BufferedReader br, HashMap<String, MessageMap> map) throws IOException
	{
		String actName = null;
		MessageMap act = null;
		String line = null;
		ArrayList<String> msg = null;
		boolean[] flags = null;
		String tagName = null;

		do
		{
			line = br.readLine();
			if(line != null) line = line.trim();
			
			if(line == null || "".equals(line)) continue;
			
			// 先頭文字判定
			String head1 = line.substring(0, 1);
			if("#".equals(head1)) continue;

			if("[".equals(head1)) // アクション
			{
				String head2 = line.substring(1, 2);
				// アクションを閉じる
				if("/".equals(head2))
				{
					if(actName != null && act != null)
					{
						map.put(actName, act);
						actName = null;
						act = null;
						msg = null;
						flags = null;
						tagName = null;
					}
					continue;
				}
				// アクション開始
				int st = line.indexOf("[") + 1;
				int ed = line.indexOf("]");
				actName = line.substring(st, ed);
			}
			else if("<".equals(head1)) // サブタグ
			{
				if(actName == null) continue;
				
				// 現在までのメッセージを登録
				if(msg != null && msg.size() > 0)
				{
					String key = createTagKey(flags);
					if(key != null)
					{
						act.map.put(key, (String[]) msg.toArray(new String[]{}));
					}
					msg = new ArrayList<String>();
				}
				
				// タグを閉じる
				String head2 = line.substring(1, 2);
				if("/".equals(head2))
				{
					int st = line.indexOf("/") + 1;
					int ed = line.indexOf(">");
					tagName = line.substring(st, ed);
					MessageMap.Tag tag = MessageMap.Tag.valueOf(tagName);
					flags[tag.ordinal()] = false;
					
					// normal, rudeタグの区切り
					if(MessageMap.Tag.normal.equals(tag) || MessageMap.Tag.rude.equals(tag))
					{
						flags = null;
						msg = null;
					}
				}
				else
				{
					// タグ開始
					int st = line.indexOf("<") + 1;
					int ed = line.indexOf(">");
					tagName = line.substring(st, ed);

					MessageMap.Tag tag = MessageMap.Tag.valueOf(tagName);
					if(tag != null)
					{
						if(MessageMap.Tag.normal.equals(tag) || MessageMap.Tag.rude.equals(tag))
						{
							if(act == null)
							{
								act = new MessageMap();
							}
							msg = new ArrayList<String>();
							flags = new boolean[MessageMap.Tag.values().length];
							if(MessageMap.Tag.rude.equals(tag))
							{
								act.rudeFlag = true;
							} else {
								act.normalFlag = true;
							}
						}
						flags[tag.ordinal()] = true;
						if(flags[MessageMap.Tag.normal.ordinal()])
						{
							act.normalTag[tag.ordinal()] = true;
						}
						else if(flags[MessageMap.Tag.rude.ordinal()])
						{
							act.rudeTag[tag.ordinal()] = true;
						}
					}
				}
			}
			else // セリフ
			{
				msg.add(line);
			}
		}while(line != null);
	}
	
	// フラグからマップキー作成
	private static final String createTagKey(boolean[] flags)
	{
		StringBuffer key = new StringBuffer("");
		MessageMap.Tag[] tags = MessageMap.Tag.values(); 

		for(int i = 0; i < tags.length; i++)
		{
			if(flags[i])
			{
				key.append(tags[i].name());
				key.append("_");
			}
		}
		if(key.length() == 0) return null;
		
		return key.toString();
	}
	
	// メッセージ取得
	public static final String getMessage(Body body, Action action)
	{
		HashMap<String, MessageMap> map = null;
		MessageMap act = null;
		String name = "";
		String name2 = "";
		String partnerName = "";
		
		map = pool_j[body.msgType.ordinal()];
		name = body.getNameJ();
		name2 = body.getNameJ2();
		if(body.partner != null) partnerName = body.partner.getNameJ();
		
		if(map == null) return "NO MESSAGE FILE";

		act = map.get(action.name());

		if(act == null) return "NO ACTION [" + action.name() + "]";

		boolean[] flags = null;
		StringBuilder key = null;
		
		// ゲスチェック
		if((body.isRude() && act.rudeFlag) || !act.normalFlag)
		{
			flags = act.rudeTag;
			key = new StringBuilder(MessageMap.Tag.rude.name() + "_");
		}
		else
		{
			flags = act.normalTag;
			key = new StringBuilder(MessageMap.Tag.normal.name() + "_");
		}
		// 年齢チェック
		if(body.mindAgeState == Body.AgeState.BABY && flags[MessageMap.Tag.baby.ordinal()])
		{
			key.append(MessageMap.Tag.baby.name() + "_");
		}
		else if(body.mindAgeState == Body.AgeState.CHILD && flags[MessageMap.Tag.child.ordinal()])
		{
			key.append(MessageMap.Tag.child.name() + "_");
		}
		else if(body.mindAgeState == Body.AgeState.ADULT && flags[MessageMap.Tag.adult.ordinal()])
		{
			key.append(MessageMap.Tag.adult.name() + "_");
		}
		// ダメージチェック
		if(body.isDamaged() && flags[MessageMap.Tag.damage.ordinal()])
		{
			key.append(MessageMap.Tag.damage.name() + "_");
		}
		// 足焼きチェック
		if(body.getFootBakeLevel() == Body.FootBake.CRITICAL && flags[MessageMap.Tag.footbake.ordinal()])
		{
			key.append(MessageMap.Tag.footbake.name() + "_");
		}
		// おくるみチェック
		if(body.hasPants() && flags[MessageMap.Tag.pants.ordinal()])
		{
			key.append(MessageMap.Tag.pants.name() + "_");
		}
		
		String[] msg = act.map.get(key.toString());

		if(msg == null) return "NO TAG <" + key.toString() + ">";
		
		StringBuffer ret = new StringBuffer(msg[rnd.nextInt(msg.length)]);
		// 埋め込み文字の置き換え
		if(ret.indexOf("%") != -1)
		{
			if(ret.indexOf("%" + Replace.dummy.name()) != -1) return null;
			int st;
//			do
//			{
				st = ret.indexOf("%" + Replace.name.name());
				if(st != -1)
				{
					try {
						ret.replace(st, st + Replace.name.name().length() + 1, name);
					}catch(StringIndexOutOfBoundsException e) {System.out.println("["+action+"]"+ret+"("+st+")"+"name");}
				}
//			}while(st != -1);
//			do
//			{
				st = ret.indexOf("%" + Replace.name2.name());
				if(st != -1)
				{
					try {
						ret.replace(st, st + Replace.name2.name().length() + 1, name2);
					}catch(StringIndexOutOfBoundsException e) {System.out.println("["+action+"]"+ret+"("+st+")"+"name2");}
				}
//			}while(st != -1);
//			do
//			{
				st = ret.indexOf("%" + Replace.partner.name());
				if(st != -1)
				{
					try {
						ret.replace(st, st + Replace.partner.name().length() + 1, partnerName);
					}catch(StringIndexOutOfBoundsException e) {System.out.println("["+action+"]"+ret+"("+st+")"+"partnerName");}
				}
//			}while(st != -1);
		}

		return ret.toString();
	}

/*
	private static final void test(ClassLoader loader)
	{
		try {
			InputStream is = loader.getResourceAsStream("data/marisa_j.xls");
			Workbook wb;
			wb = WorkbookFactory.create(is);
			Sheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(0);
			Cell cell = row.getCell(0);
			System.out.println(cell.getStringCellValue());
		} catch (InvalidFormatException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
*/
}

