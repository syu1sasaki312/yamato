package yamato.file;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import yamato.common.General;
import yamato.common.TResult;

/****
 * ファイル読込
 */
public class Reader {

	// 文字入力ストリーム
	private BufferedReader mBufReader = null;

	/****
	 * ファイルオープン処理
	 * @param fPath ファイルパス
	 * @return True：正常, False：異常
	 */
	public boolean openFile(String fPath) {
		boolean rtn = true;
		try {
			Charset cSet = Charset.forName(General.FILE_CHARSET);
			Path path = Paths.get(fPath);
			this.mBufReader = Files.newBufferedReader(path, cSet);
		} catch (Exception e) {
			// Error
			General.consoleLog(e);
			rtn = false;
		} finally {
			// UnProcessed
		}
		return rtn;
	}

	/****
	 * ファイルリード処理
	 * @return 
	 * 　　正常：TResult.result = 行データ
	 *           TResult.error  = False 
	 * 　　異常：TResult.result = NULL
	 *           TResult.error  = True
	 */
	public TResult<String> readFile() {
		TResult<String> rtn = new TResult<String>();
		try {
			rtn.result = this.mBufReader.readLine();
		} catch (Exception e) {
			// Error
			General.consoleLog(e);
			rtn.error = true;
		} finally {
			// UnProcessed
		}
		return rtn;
	}

	/****
	 * ファイルクローズ処理
	 * @return True：正常, False：異常
	 */
	public boolean closeFile() {
		boolean rtn = true;
		try {
			if (this.mBufReader != null) {
				this.mBufReader.close();
			}
		} catch (Exception e) {
			// Error
			General.consoleLog(e);
			rtn = false;
		} finally {
			this.mBufReader = null;
		}
		return rtn;
	}

}
