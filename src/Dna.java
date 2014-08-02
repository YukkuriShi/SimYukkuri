package src;
import src.yukkuri.Tarinai;



/****************************************
 *  ゆっくりの遺伝子情報
 */
public class Dna implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public int type;				// 種別
	public Body.Attitude attitude;	// 性格
	public Body.Intelligence intelligence;	// 知能
	public boolean raperChild;		// レイプでできた子か

	public Dna() {
		type = Tarinai.type;
		attitude = Body.Attitude.AVERAGE;
		intelligence = Body.Intelligence.AVERAGE;
		raperChild = false;
	}

	public Dna(int t, Body.Attitude att, Body.Intelligence intel, boolean rape) {
		type = t;
		attitude = att;
		intelligence = intel;
		raperChild = rape;
	}
}