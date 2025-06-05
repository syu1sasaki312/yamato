package yamato.file;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import yamato.common.General;

/****
 * ファイル書込
 */
public class Writer {

	// 文字出力ストリーム
	private BufferedWriter mBufWriter = null;

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
			this.mBufWriter = Files.newBufferedWriter(path, cSet);
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
	 * ファイルライト処理
	 * @param lines 行出力(複数)
	 * @return True：正常, False：異常
	 */
	public boolean WriteFile(List<String> lines) {
		boolean rtn = true;
		try {
			for (String line : lines) {
				mBufWriter.write(line);
				mBufWriter.newLine();
			}
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
	 * ファイルライト処理
	 * @param lines 行出力
	 * @return True：正常, False：異常
	 */
	public boolean WriteFile(String line) {
		boolean rtn = true;
		try {
			mBufWriter.write(line);
			mBufWriter.newLine();
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
	 * ファイルクローズ処理
	 * @return True：正常, False：異常
	 */
	public boolean closeFile() {
		boolean rtn = true;
		try {
			if (this.mBufWriter != null) {
				this.mBufWriter.close();
			}
		} catch (Exception e) {
			// Error
			General.consoleLog(e);
			rtn = false;
		} finally {
			this.mBufWriter = null;
		}
		return rtn;
	}

}
