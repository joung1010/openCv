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
        Mat resizedImage2 = new Mat();
        Imgproc.resize(img1, resizedImage1, new Size(img2.width(), img2.height()));

        // 두 이미지 간의 차이 계산
        Mat diff = new Mat();
        Core.absdiff(resizedImage1, img2, diff);
        // 차이가 있는 픽셀을 이진화하여 강조
        Core.subtract(new Mat(diff.size(), CvType.CV_8UC1, new Scalar(255)), diff, diff);
        // 차이 이미지 이진화
        Imgproc.threshold(diff, diff, 1, 255, Imgproc.THRESH_BINARY);

        // 차이가 있는지 여부를 판단
        int nonZeroCount = Core.countNonZero(diff);
        if (nonZeroCount > 0) {
            System.out.println("Images are different");
        } else {
            System.out.println("Images are the same");
        }
    }
}

