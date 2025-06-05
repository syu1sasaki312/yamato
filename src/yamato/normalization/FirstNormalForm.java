package yamato.normalization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yamato.common.General;
import yamato.common.TResult;
import yamato.file.Reader;
import yamato.file.Writer;

/****
 * 第一正規化処理
 * 
 * ・Tsvデータ（入力ファイル）は、以下に示す仕様に準拠したデータであることを前提とします。
 *   したがって、データの妥当性チェックは本処理では行いません。
 *   　- データ内の列数は全ての行で同じです。例えば、1行目が3列(タブ文字が2つ)の場合、何行目であっても3列になります。
 *   　- 列の最大数は5です。
 *   　- セルに含まれる値の数(半角コロンで区切られる数)は最大10個です。
 *   　- セルに含まれる文字数は0文字以上10000文字以下です。(区切り文字の半角コロンを含む)
 *   　- セルに含まれる文字はASCII印字可能文字です。(0x20-0x7e)
 * 
 * ・以下の条件に基づき、パフォーマンスを考慮して1行毎の読み込み・書き込みを行います。
 *     - 行数に上限はありません。
 *
 */

public class FirstNormalForm {

	/****
	 * 実行処理
	 * @param inputFile  入力ファイル
	 * @param outputFile 出力ファイル
	 * @return True：正常, False：異常
	 */
	public boolean exec(String inputFile, String outputFile) {
		General.consoleLog(General.getMethodName() + "...開始");

		boolean rtn = true;
		Reader reader = new Reader();
		Writer writer = new Writer();

		try {
			// ファイルオープン処理(Reader)
			if (!reader.openFile(inputFile)) {
				// Error
				rtn = false;
				General.consoleLog("ファイルオープン処理(Reader)に失敗しました。");
				return rtn;
			}

			// ファイルオープン処理(writer)
			if (!writer.openFile(outputFile)) {
				// Error
				rtn = false;
				General.consoleLog("ファイルオープン処理(writer)に失敗しました。");
				return rtn;
			}

			// ファイル行数分ループ
			while (true) {

				// ファイルリード処理(1行毎)
				TResult<String> rs = reader.readFile();
				if (rs.error) {
					// Error
					rtn = false;
					General.consoleLog("ファイルリード処理に失敗しました。");
					return rtn;
				}

				if (rs.result == null) {
					// END OF FILE
					break;
				}

				String line = rs.result;
				General.consoleLog("LINE[" + line + "]");

				// セル分割処理
				List<List<String>> cells = splitCells(line);

				// 組み合わせ取得処理
				List<String> ls = getCombinations(cells);

				// ファイルライト処理(1行毎)
				if (!writer.WriteFile(ls)) {
					// Error
					rtn = false;
					General.consoleLog("ファイルライト処理に失敗しました。");
					return rtn;
				}
			}

		} catch (Exception e) {
			// Error
			rtn = false;
			General.consoleLog(e);
			General.consoleLog("想定外のエラーが発生しました。。");

		} finally {

			// ファイルクローズ処理(Reader)
			if (!reader.closeFile()) {
				// Error
				rtn = false;
				General.consoleLog("ファイルクローズ処理(Reader)に失敗しました。");
			}

			// ファイルクローズ処理(writer)
			if (!writer.closeFile()) {
				// Error
				rtn = false;
				General.consoleLog("ファイルクローズ処理(writer)に失敗しました。");
			}

			General.consoleLog(General.getMethodName() + "...終了");
		}

		return rtn;
	}

	/****
	 * セル分割処理
	 *
	 * タブ区切り文字（\t）で行を分割し、各セル内をコロン（:）で分割します。
	 * 各セル内に複数の値が含まれている場合、そのすべてをリストに保持します。
	 *
	 * 例：
	 * 入力行： "A:B\tC\tD:E"
	 * → 出力：
	 * 　 タブで分割：["A:B", "C", "D:E"]
	 * 　 各セルをコロンで分割： [["A", "B"], ["C"], ["D", "E"]]
	 *
	 * @param line タブ区切り形式の1行の文字列
	 * @return 各セルごとにコロンで分割された文字列のリストのリスト
	 */
	private List<List<String>> splitCells(String line) {
		List<List<String>> rtn = new ArrayList<>();
		// タブ分割
		List<String> cells = General.split(line, General.CHAR_TAB);
		// コロン分割
		for (String cell : cells) {
			rtn.add(General.split(cell, General.CHAR_COLON));
		}
		return rtn;
	}

	/****
	 * 組み合わせ取得処理
	 *
	 * splitCellsメソッドで取得した複数の文字列リストから、組み合わせをすべて列挙します。
	 * 例：
	 * 入力：splitCells = [["A", "B"], ["C"], ["D", "E"]]
	 * → 出力（タブ区切り）：
	 *    A\tC\tD
	 *    A\tC\tE
	 *    B\tC\tD
	 *    B\tC\tE
	 * ※出力値は1行の文字列として返され、各要素の組み合わせをタブ区切りで出力します。
	 *
	 * @param cells 各セルごとの文字列のリスト（例： [["A", "B"], ["C"], ["D", "E"]]）
	 * @return すべての組み合わせ（1行ずつタブ区切りの文字列として返される）
	 */
	private List<String> getCombinations(List<List<String>> cells) {

		List<String> rtn = new ArrayList<String>();

		// インデックス初期化
		// [0, 0, 0]
		int[] cur = new int[cells.size()];
		Arrays.fill(cur, 0);

		// 組み合わせを列挙するループ
		// [0, 0, 0] → ["A","C","D"]
		// [0, 0, 1] → ["A","C","E"]
		// [1, 0, 0] → ["B","C","D"]
		// [1, 0, 1] → ["B","C","E"]
		while (true) {

			// 現在の組み合わせを生成
			List<String> row = new ArrayList<String>();
			for (int i = 0; i < cells.size(); i++) {
				List<String> ls = cells.get(i);
				String val = ls.get(cur[i]);
				row.add(val);
			}

			// 1行の文字列に変換し追加
			rtn.add(String.join(General.CHAR_TAB, row));

			// 一番右の列（末尾）からインデックスを進める
			int index = cells.size() - 1;

			while (index >= 0) {
				cur[index]++;
				if (cur[index] < cells.get(index).size()) {
					break;
				}
				cur[index] = 0;
				index--;
			}

			if (index < 0) {
				// 組み合わせ終了
				break;
			}
		}

		return rtn;
	}
}
