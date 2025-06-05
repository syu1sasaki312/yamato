package yamato.normalization;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import yamato.common.General;
import yamato.common.TResult;
import yamato.file.Reader;
import yamato.file.Writer;

/****
 * 第一正規化逆変換処理
 * ・Tsvデータ（入力ファイル）は、以下に示す仕様に準拠したデータであることを前提とします。
 *   したがって、データの妥当性チェックは本処理では行いません。
 *   また、本処理では出力結果の行順の対応は行っておりません。
 *   
 *   　- 列数は2で統一。1列目がキーで2列目が値とします。
 *     - 行の最大数は1000です。
 *     - グループ化される値の数は最大10個です。
 *     - セルに含まれる文字数は0文字以上100文字以下です。
 *     - セルに含まれる文字はASCII印字可能文字です。(0x20-0x7e)
 */
public class Denormalize {

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

		// グループ値：KEY毎に値をリストで保持します
		Map<String, List<String>> groupMap = new LinkedHashMap<String, List<String>>();

		try {
			// ファイルオープン処理(Reader)
			if (!reader.openFile(inputFile)) {
				// Error
				rtn = false;
				General.consoleLog("ファイルオープン処理(Reader)に失敗しました。");
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

				// グループ値追加処理
				putGroupMap(line, groupMap);

			}

			// ファイルオープン処理(writer)
			if (!writer.openFile(outputFile)) {
				// Error
				rtn = false;
				General.consoleLog("ファイルオープン処理(writer)に失敗しました。");
				return rtn;
			}

			// グループ値数分ループ
			for (Map.Entry<String, List<String>> entry : groupMap.entrySet()) {

				// 出力行作成処理
				String line = createOutputLine(entry);

				// ファイルライト処理(1行毎)
				if (!writer.WriteFile(line)) {
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

	/**
	 * グループ値追加処理  
	 * 
	 * 入力された1行のTSVデータ（タブ区切り）を、キーと値に分けてMapに追加します。  
	 * ＜例＞  
	 * 入力行：A1\tZ3
	 * ⇒ groupMap の内容：  
	 *   A1 → ["Z1", "Z2", ★"Z3"]  
	 *   A2 → ["X1"]
	 * 
	 * @param line 入力されたTsvの1行（例："A1\tZ3"）
	 * @param groupMap KEYごとに値をリスト化して保持するマップ
	 */
	private void putGroupMap(String line, Map<String, List<String>> groupMap) {
		List<String> ls = General.split(line, General.CHAR_TAB);

		String key = ls.size() > 0 ? ls.get(0) : "";
		String val = ls.size() > 1 ? ls.get(ls.size() - 1) : "";

		if (!groupMap.containsKey(key)) {
			groupMap.put(key, new ArrayList<String>());
		}

		List<String> values = groupMap.get(key);
		values.add(val);

		return;
	}

	/**
	 * 出力行作成処理  
	 * 
	 * 指定されたエントリ（キーと値のリスト）から、1行の出力文字列を作成します。  
	 * ＜例＞  
	 * entry = "A1" → ["Z1", "Z2", "Z3"]  
	 * 出力：A1\tZ1:Z2:Z3
	 * 
	 * @param entry 出力対象のキーと値のリスト
	 * @return 出力行（タブとコロンで整形された文字列）
	 */
	private String createOutputLine(Map.Entry<String, List<String>> entry) {
		String key = entry.getKey();
		List<String> ls = entry.getValue();

		StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append(General.CHAR_TAB);
		sb.append(String.join(General.CHAR_COLON, ls));

		return sb.toString();
	}

}
