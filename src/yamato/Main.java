package yamato;

import java.io.File;
import java.util.List;

import yamato.common.General;
import yamato.common.TResult;
import yamato.normalization.Denormalize;
import yamato.normalization.FirstNormalForm;

public class Main {

	/****
	 * メイン処理
	 *
	 * このメソッドは、指定された処理タイプに応じて第一正規化または逆変換を実行します。
	 * @param args コマンドライン引数
	 *        args[0] = Tsvファイル格納場所
	 *        args[1] = 処理タイプ（例："1nf", "dem"）
	 * @return
	 */
	public static void main(String[] args) {
		General.consoleLog(General.getMethodName() + "...開始");
		try {
			// コマンドライン引数取得
			String inputDir = General.getArrValue(args, 0);
			String type = General.getArrValue(args, 1);

			if (!General.TYPE_1NF.equals(type) && 
				!General.TYPE_DEM.equals(type)) {
				// Error
				General.consoleLog("処理タイプが不正です。: " + type);
				return;
			}

			// Tsvファイル取得処理
			TResult<List<String>> rs = General.getTsvFiles(inputDir);
			if (rs.error) {
				// Error
				General.consoleLog("Tsvファイル取得処理に失敗しました。: " + inputDir);
				return;
			}

			// ファイル無し
			if (rs.result.size() == 0) {
				General.consoleLog("Tsvファイルが見つかりませんでした。: " + inputDir);
				return;
			}

			// ディレクトリ作成処理(出力先)
			String outputDir = inputDir + File.separator + General.RESULT_DIR;
			if (!General.createDir(outputDir)) {
				// Error
				General.consoleLog("ディレクトリ作成処理(出力先)に失敗しました。: " + outputDir);
				return;
			}

			// ファイル数分ループ
			for (String inputFile : rs.result) {

				General.consoleLog("FILE[" + inputFile + "]");

				// ファイルパス作成(出力先)
				String fname = General.getFileName(inputFile);
				String outputFile = outputDir + File.separator + fname;

				if (General.TYPE_1NF.equals(type)) {

					// 第一正規化処理
					FirstNormalForm fnf = new FirstNormalForm();
					if (!fnf.exec(inputFile, outputFile)) {
						// Error
						General.consoleLog("第一正規化処理に失敗しました。: " + inputFile);
					}

				} else {

					// 第一正規化逆変換処理
					Denormalize dem = new Denormalize();
					if (!dem.exec(inputFile, outputFile)) {
						// Error
						General.consoleLog("第一正規化逆変換処理に失敗しました。: " + inputFile);
					}

				}

			}

		} catch (Exception e) {
			General.consoleLog(e);
			General.consoleLog("想定外のエラーが発生しました。");

		} finally {
			General.consoleLog(General.getMethodName() + "...終了");

		}

		return;

	}
}
