package src;



/*****************************************************
	ゆっくり同士や環境とのメッセージ伝達を行うためのイベントパックの抽象クラス
	EventPacketにデータをセットして環境に対してアクションを起こす場合はTerrarium.eventListへ、
	親兄弟に呼びかけるにはBody.eventListへ追加する
	
	イベントの登録はゆっくりからだけでなくコマンド、アイテムのロジックなどどこからでも可能
	
	追加用の処理は基本EventLogicにあるものを使う
*/

abstract public class EventPacket implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	// イベント優先度。餌、睡眠などの標準処理に対する優先度
	// ふりふり、のびのび < LOW < 食事、トイレ、睡眠など標準行動 < MIDDLE < かび、高ダメージなど生命の危険状態 < HIGH
	public static enum EventPriority {
		LOW,						// 食事、トイレ、睡眠等でキャンセルされる低優先
		MIDDLE,						// ダメージ50%以上でキャンセルされる標準優先
		HIGH,						// 生きてる限りはイベントを実行しようとする
	}
	
	// updateメソッドの戻り値
	// 移動完了以外の条件でexecuteに移行させたい場合はFORCE_EXECを、
	// イベントを中断したい場合はABORTを返す
	public static enum UpdateState {
		FORCE_EXEC,
		ABORT
	}
	
	// イベントを発生させる側で設定する項目
	protected Body from;			// イベントを発した個体
	protected Body to;				// 特定の対象に向けた場合はその個体
	protected Obj target;			// イベント対象
	protected int count;			// イベント有効期間

	// イベントに参加する側で設定する項目
	protected EventPriority priority = EventPriority.LOW;	// 優先度
	protected int toX;				// 移動目標
	protected int toY;
	protected int toZ;

	public EventPacket(Body f, Body t, Obj tgt, int cnt) {
		from = f;
		to = t;
		target = tgt;
		count = cnt;
	}
	
	public boolean countDown() {
//		if(count == NO_LIMIT) return false;
		count--;
		if(count == 0) return true;
		return false;
	}
	
	public EventPriority getPriority() {
		return priority;
	}
	
	public void setFrom(Body b) {
		from = b;
	}

	public void setTo(Body b) {
		to = b;
	}

	public void setTarget(Obj o) {
		target = o;
	}

	// 内部ステータスを書き換えて終了など下の複雑な挙動を必要としないイベントはこのメソッドをオーバーライドする
	// trueを返すとイベントは終了してcheckEventResponse以降は呼ばれない
	// また、このメソッドは例外的にイベント実行中でも呼ばれるので困る場合は
	// このメソッド内でBody.currentEventがnullかチェックすること
	public boolean simpleEventAction(Body b) {
		return false;
	}

	// イベント参加有無を返す
	// ワールドイベントの場合はイベント発行した本人に対してもチェックが発生するので
	// 処理を避けたければここでチェックを忘れず行う
	abstract public boolean checkEventResponse(Body b);

	// イベント開始動作
	// checkEventResponseでtrueを返した場合に一度だけ呼ばれる
	// 主に移動先の設定などに使用
	// イベント用の移動はBody.moveToEvent()を使用する
	abstract public void start(Body b);

	// 毎フレーム呼ばれるメソッド
	// startで移動先を指定すれば勝手に移動して完了後にexecuteが呼ばれるので
	// 普通は必要ないが特別に何かしたい場合はこれをオーバーライドする
	// 例えば対象のstay()を呼び続けることで相手の動きを止めておくことができる
	// また、ここでUpdateState.ABORTを返すとイベントを終了させたり
	// UpdateState.FORCE_EXECで強制的にexecuteへ移行できる
	// 移動中に対象がremoveされたら困る場合などはここでチェックしてABORTなどを行う
	public UpdateState update(Body b) {
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベントが終了し通常動作へ戻る
	// falseを返している間は毎フレームupdateとexecuteが呼ばれるので
	// 自前でステート管理すれば小芝居的なものも可能
	abstract public boolean execute(Body b);

	// イベント終了時に呼ばれるメソッド
	// 完了、中断問わず終了時に一度だけ呼ばれる
	// 外部からのアクションによってclearActions()が発生してイベントが消される場合も呼ばれる
	// 普通は必要ないが後始末の処理が必要な場合はこれをオーバーライドする
	public void end(Body b) {
		return;
	}
}

