package yamato.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/****
 * 汎用クラス
 */
public class General {

	// 処理タイプ(第一正規化)
	public final static String TYPE_1NF = "1nf";
	// 処理タイプ(第一正規化逆変換)
	public final static String TYPE_DEM = "dem";
	// 処理結果ファイル格納場所
	public final static String RESULT_DIR = "result";
	// Tsvファイル拡張子
	public final static String TSV_SUFFIX = ".tsv";
	// 文字コードセット
	public final static String FILE_CHARSET = "UTF-8";
	// タブ文字
	public final static String CHAR_TAB = "\t";
	// コロン文字
	public final static String CHAR_COLON = ":";

	/****
	 * ディレクトリ作成処理
	 * @param dPath ディレクトリパス
	 * @return True：正常, True：異常
	 */
	public static boolean createDir(String dPath) {
		boolean rtn = true;
		General.consoleLog(General.getMethodName() + "...開始");
		Path path = Paths.get(dPath);
		try {
			if (Files.notExists(path)) {
				Files.createDirectories(path);
			}
		} catch (Exception e) {
			// Error
			rtn = false;
			General.consoleLog(e);
		} finally {
			General.consoleLog(General.getMethodName() + "...終了");
		}
		return rtn;

	}

	/****
	 * Tsvファイル取得処理
	 * Tsvファイルのファイルパスを取得します。
	 * @param dPath ディレクトリパス
	 * @return 
	 * 　　正常：TResult.result = Tsvファイルのファイルパスリスト
	 *           TResult.error  = false 
	 * 　　異常：TResult.result = NULL
	 *           TResult.error  = true
	 */
	public static TResult<List<String>> getTsvFiles(String dPath) {
		TResult<List<String>> rtn = new TResult<List<String>>();
		General.consoleLog(General.getMethodName() + "...開始");
		// try-with-resources構文
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dPath))) {
			List<String> ls = new ArrayList<String>();
			for (Path path : stream) {
				if (!Files.isRegularFile(path)) {
					// 通常ファイル以外
					continue;
				}
				String name = path.toString();
				if (!name.toLowerCase().endsWith(General.TSV_SUFFIX)) {
					// Tsvファイル以外
					continue;
				}
				ls.add(name);
			}
			rtn.result = ls;
		} catch (Exception e) {
			// Error
			rtn.error = true;
			General.consoleLog(e);
		} finally {
			General.consoleLog(General.getMethodName() + "...終了");
		}
		return rtn;
	}

	/****
	 * 文字列分割処理
	 * 文字列を区切り文字で分割します。空文字列も含めます。
	 * @param value 文字列
	 * @param regex 区切り文字
	 * @return 文字列を分割した値のリスト
	 */
	public static List<String> split(String value, String regex) {
		String[] arr = value.split(regex, -1);
		return Arrays.asList(arr);
	}

	/****
	 * ファイル名取得処理
	 * ファイルパスからファイル名を取得します。
	 * @param fPath ファイルパス
	 * @return ファイル名
	 */
	public static String getFileName(String fPath) {
		Path path = Paths.get(fPath);
		return path.getFileName().toString();
	}

	/****
	 * 配列値取得処理
	 * 指定した位置の値を取得します。
	 * 配列が、NULL or Empty or 範囲外の場合は、空を返却します。
	 * @param arr 配列
	 * @param index 位置
	 * @return 配列値
	 */
	public static String getArrValue(String[] arr, int index) {
		if (arr != null && index >= 0 && index < arr.length) {
			return arr[index];
		}
		return "";
	}

	/****
	 * Log情報出力処理
	 * @param throwable 例外
	 */
	public static void consoleLog(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		consoleLog(sw.toString());
	}

	/****
	 * Log情報出力処理
	 * @param log 出力文字列
	 */
	public static void consoleLog(String log) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss:SSS");
		String buf = now.format(fmt);
		System.out.println(buf + General.CHAR_TAB + log);
	}

	/****
	 * Log情報取得処理
	 * 呼び出し元のクラスとメソッド情報を取得します。
	 * @return Log情報
	 */
	public static String getMethodName() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		int callerIndex = 2;
		if (stackTrace.length > callerIndex) {
			StackTraceElement elem = stackTrace[callerIndex];
			return elem.getClassName() + "#" + elem.getMethodName() + "()";
		}
		return "UnknownClass#UnknownMethod";
	}

}
