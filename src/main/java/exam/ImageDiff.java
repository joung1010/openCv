package exam;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageDiff {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Mat img1 = Imgcodecs.imread("C:\\openCv\\src\\main\\resources\\images\\template.jpg", Imgcodecs.IMREAD_GRAYSCALE);
        Mat img2 = Imgcodecs.imread("C:\\openCv\\src\\main\\resources\\images\\target2.jpg", Imgcodecs.IMREAD_GRAYSCALE);

        // 이미지 크기 조정
        Mat resizedImage1 = new Mat();
        Imgproc.resize(img1, resizedImage1, new Size(img2.width(), img2.height()));

        // 두 이미지 간의 차이 계산
        Mat diff = new Mat();
        Core.absdiff(resizedImage1, img2, diff);
        // 차이 이미지 이진화
        Imgproc.threshold(diff, diff, 1, 255, Imgproc.THRESH_BINARY);

        // 차이가 있는지 여부를 판단
        int nonZeroCount = Core.countNonZero(diff);
        System.out.println("nonZeroCount = " + nonZeroCount);

        // 차이 비율 계산
        double totalPixels = img1.size().area();
        System.out.println("totalPixels = " + totalPixels);
        double diffPercentage = (double) nonZeroCount / totalPixels;
        System.out.println("diffPercentage = " + diffPercentage);
        // 임계값 설정 (예: 1%)
        double threshold = 0.01;

        if (diffPercentage < threshold) {
            System.out.println("Images are considered the same");
        } else {
            System.out.println("Images are different");
        }
    }
}

