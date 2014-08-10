package src;
import src.yukkuri.Tarinai;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.Intelligence;
import src.yukkuriBody.ConstantValues.*;



/****************************************
 *  ゆっくりの遺伝子情報
 */
public class Dna implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public int type;				// 種別
	public Attitude attitude;	// 性格
	public Intelligence intelligence;	// 知能
	public boolean raperChild;		// レイプでできた子か

	public Dna() {
		type = Tarinai.type;
		attitude = Attitude.AVERAGE;
		intelligence = Intelligence.AVERAGE;
		raperChild = false;
	}

	public Dna(int t, Attitude att, Intelligence intel, boolean rape) {
		type = t;
		attitude = att;
		intelligence = intel;
		raperChild = rape;
	}
}