package in.clarivate.pdfanalysis.controller;

import in.clarivate.pdfanalysis.service.DigitalPdfAnalyserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/dact")
public class PdfAnalyserController {

    @Autowired
    private DigitalPdfAnalyserService digitalPdfAnalyserService;

    @PostMapping ("/getPdfData")
    public ResponseEntity<String> getTextFromPdf(@RequestBody String  pdfPath){

        String response;
        try {
            response = digitalPdfAnalyserService.pdfAnalyser(pdfPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
