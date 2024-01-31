package exam;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class SSIMCalculator {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /*    public static void main(String[] args) {
            Mat img1 = Imgcodecs.imread("path/to/image1.jpg", Imgcodecs.IMREAD_GRAYSCALE);
            Mat img2 = Imgcodecs.imread("path/to/image2.jpg", Imgcodecs.IMREAD_GRAYSCALE);

            double ssimValue = calculateSSIM(img1, img2);
            System.out.println("SSIM: " + ssimValue);
        }*/
    public static double calculateSSIM(Mat img1, Mat img2) {
        double C1 = 6.5025, C2 = 58.5225;

        Mat mean1 = new Mat();
        Mat mean2 = new Mat();
        Imgproc.GaussianBlur(img1, mean1, new Size(11, 11), 1.5);
        Imgproc.GaussianBlur(img2, mean2, new Size(11, 11), 1.5);

        Mat variance1 = new Mat();
        Mat variance2 = new Mat();
        Mat covariance = new Mat();
        Imgproc.GaussianBlur(img1.mul(img1), variance1, new Size(11, 11), 1.5);
        Core.subtract(variance1, mean1.mul(mean1), variance1);
        Imgproc.GaussianBlur(img2.mul(img2), variance2, new Size(11, 11), 1.5);
        Core.subtract(variance2, mean2.mul(mean2), variance2);
        Imgproc.GaussianBlur(img1.mul(img2), covariance, new Size(11, 11), 1.5);
        Core.subtract(covariance, mean1.mul(mean2), covariance);

        Mat numerator = new Mat();
        Mat denominator = new Mat();
        Mat ssimMap = new Mat();

        Core.multiply(mean1, mean2, numerator, 2);
        Core.add(covariance, new Scalar(C2), covariance);
        Core.multiply(numerator, covariance, numerator); // Numerator

        Core.multiply(mean1, mean1, denominator);
        Core.multiply(mean2, mean2, covariance);
        Core.add(denominator, covariance, denominator);
        Core.multiply(denominator, new Scalar(C1 + C2), denominator); // Denominator

        Core.divide(numerator, denominator, ssimMap);
        Scalar mssim = Core.mean(ssimMap);
        return mssim.val[0];
    }

    public static String extractResourceToTempFile(String resourcePath, String prefix, String suffix) throws Exception {
        InputStream inputStream = SSIMCalculator.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new Exception("Resource not found: " + resourcePath);
        }
        File tempFile = File.createTempFile(prefix, suffix);
        tempFile.deleteOnExit();
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile.getAbsolutePath();
    }

    public static void main(String[] args) throws Exception{
        // 템플릿 이미지와 타겟 이미지 로드
        String templateImagePath = extractResourceToTempFile("/images/template.jpg", "template", ".jpg");
        String targetImagePath = extractResourceToTempFile("/images/target6.jpg", "target", ".jpg");

        Mat templateImage = Imgcodecs.imread(templateImagePath, Imgcodecs.IMREAD_GRAYSCALE);

        Mat targetImage = Imgcodecs.imread(targetImagePath, Imgcodecs.IMREAD_GRAYSCALE);

        // 타겟 이미지 크기 조정
        Mat resizedImage = new Mat();
        Size sz = new Size(templateImage.width(), templateImage.height());
        // 이미지 사이즈 조정
        Imgproc.resize(targetImage, resizedImage, sz, 0, 0, Imgproc.INTER_CUBIC);

        // 두 이미지에 Gaussian Blur 적용
//        Imgproc.GaussianBlur(templateImage, templateImage, new Size(3, 3), 0.5);
//        Imgproc.GaussianBlur(resizedImage, resizedImage, new Size(3, 3), 0.5);

        Mat normalizedTemplateImage = new Mat();
        Core.normalize(templateImage, normalizedTemplateImage, 0, 255, Core.NORM_MINMAX, -1, new Mat());

        Mat normalizedTargetImage = new Mat();
        Core.normalize(resizedImage, normalizedTargetImage, 0, 255, Core.NORM_MINMAX, -1, new Mat());



        // SSIM 계산
        double ssim = calculateSSIM(normalizedTemplateImage, normalizedTargetImage);
        System.out.printf("SSIM: %f\n", ssim);
    }
}
