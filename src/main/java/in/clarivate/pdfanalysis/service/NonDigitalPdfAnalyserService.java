package in.clarivate.pdfanalysis.service;

import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import org.springframework.stereotype.Service;
import org.bytedeco.javacpp.*;

import static org.bytedeco.leptonica.global.leptonica.pixDestroy;
import static org.bytedeco.leptonica.global.leptonica.pixRead;

//@Service
public class NonDigitalPdfAnalyserService {
    public void getDigitalizedPdfContent(){
        BytePointer outText;

        TessBaseAPI api = new TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (api.Init(null, "eng") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

        // Open input image with leptonica library
//        PIX image = pixRead(args.length > 0 ? args[0] : "/usr/src/tesseract/testing/phototest.tif");
//        api.SetImage(image);
//        // Get OCR result
//        outText = api.GetUTF8Text();
//        System.out.println("OCR output:\n" + outText.getString());
//
//        // Destroy used object and release memory
//        api.End();
//        outText.deallocate();
//        pixDestroy(image);

    }
}
