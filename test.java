package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;

import net.sourceforge.tess4j.Tesseract;

public class test {
    public static void main(String[] args) {
        // OpenCVを使って入力画像を読み込みます
        String imagePath = "C:/Users/mshr1/Pictures/UniteResult/result10.jpg";
        Mat image = opencv_imgcodecs.imread(imagePath);

        // 輪郭を検出します
        Mat grayImage = new Mat();
        MatVector contours = new MatVector();
        opencv_imgproc.cvtColor(image, grayImage, opencv_imgproc.CV_BGR2GRAY);
        opencv_imgproc.findContours(grayImage, contours, opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);

        // Tesseractエンジンを初期化します
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata"); // 言語トレーニングデータがあるディレクトリのパスを設定します

        // OCRの言語を設定します（複数の言語を個別に設定します）
        String[] languages = {"eng"}; // 画像内のテキストの言語に合わせて設定してください
        for (String language : languages) {
            tesseract.setLanguage(language);
        }

        // 読み取り位置の可視化を行います
        try {
            // 画像上の各輪郭を処理します
            for (int i = 0; i < contours.size(); i++) {
                // 輪郭を囲む矩形を取得します
                Rect boundingRect = opencv_imgproc.boundingRect(contours.get(i));

                // 矩形を画像上に描画します
                opencv_imgproc.rectangle(image, boundingRect, new Scalar(0, 255, 0, 0)); // 緑色の矩形を描画します
            }

            // 一時ファイルとして画像を保存します（可視化された画像）
            File tempFile = new File("temp_image_with_boxes.png");
            opencv_imgcodecs.imwrite(tempFile.getAbsolutePath(), image);

            // 可視化された画像上でOCRを実行します
            String result = tesseract.doOCR(tempFile);
            System.out.println("OCR結果:");
            System.out.println(result);
            
            // OCR結果をテキストファイルに保存します
            saveTextToFile(result, "output.txt");

        } catch (Exception e) {
            System.err.println("OCRの実行中にエラーが発生しました: " + e.getMessage());
        }
    }

    private static void saveTextToFile(String text, String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(text);
            writer.close();
            System.out.println("OCR結果をファイルに保存しました: " + filePath);
        } catch (IOException e) {
            System.err.println("ファイルへの保存中にエラーが発生しました: " + e.getMessage());
        }
    }
}