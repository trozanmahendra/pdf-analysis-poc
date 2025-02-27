package in.clarivate.pdfanalysis.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DigitalPdfAnalyserService {
    //Loading an existing document
    public String pdfAnalyser(String pdfPath) throws IOException {
        File file = new File(pdfPath);
        PDDocument document = Loader.loadPDF(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
//        pdfStripper.setStartPage(1);
//        pdfStripper.setEndPage(1);
        String text = pdfStripper.getText(document);
        System.out.println(text.replace("\n",""));
        String[] inIdStrings = text.split("\\(\\d{2,3}\\)");//         "\\(\\d{2}\\)"
        List<String> list = extractMatches(text,"\\(\\d{2,3}\\)");
        PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
        List<String> bookmarks = new ArrayList<>();
        List<String> strings = printBookmark(outline, "", bookmarks);
        List<String> inIds = Arrays.stream(inIdStrings)
                .filter(str -> !(str.trim().startsWith("*") && str.trim().endsWith("*")))
                .map(str -> str.replace("\n","").replace("\r","")+"\n")
                .toList();
        List<String> concatedList = concat(list,inIds);
        List<String> simplifiedConcatedList = splitAndAppend(text,"\\(\\d{2}\\)")
                .stream()
                .filter(str -> !(str.trim().startsWith("*") && str.trim().endsWith("*")))
                .map(str -> str.replace("\n","").replace("\r","")+"\n")
                .toList();
        document.close();
        return ("\n------------------------------All Bookmarks in pdf %s---------------------------\n%s\n-----------------INID CODES--------------------------------------\n%s")
                .formatted(Path.of(pdfPath).getFileName(),strings.stream().map(s -> "\n"+s).toList(),concatedList);
    }


    public List<String> pdfAnalyserToGetInidCodes(String pdfPath) throws IOException {
        File file = new File(pdfPath);
        PDDocument document = Loader.loadPDF(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
//        pdfStripper.setStartPage(1);
//        pdfStripper.setEndPage(1);
        String text = pdfStripper.getText(document);
        System.out.println(text.replace("\n",""));
        String[] inIdStrings = text.split("\\(\\d{2,3}\\)");//         "\\(\\d{2}\\)"
        List<String> list = extractMatches(text,"\\(\\d{2,3}\\)");
        List<String> inIds = Arrays.stream(inIdStrings)
                .filter(str -> !(str.trim().startsWith("*") && str.trim().endsWith("*")))
                .map(str -> str.replace("\n","").replace("\r",""))
                .toList();
        List<String> concatedList = concat(list,inIds);
        document.close();
        return (concatedList);
    }
    public Map<String,List<String>> pdfAnalyserToGetInidCodesMap(String pdfPath) throws IOException {
        File file = new File(pdfPath);
        PDDocument document = Loader.loadPDF(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
//        pdfStripper.setStartPage(1);
//        pdfStripper.setEndPage(1);
        String text = pdfStripper.getText(document);
        System.out.println(text.replace("\n",""));
        String[] inIdStrings = text.split("\\(\\d{2,3}\\)");//         "\\(\\d{2}\\)"
        List<String> list = extractMatches(text,"\\(\\d{2,3}\\)");
        List<String> inIds = Arrays.stream(inIdStrings)
                .filter(str -> !(str.trim().startsWith("*") && str.trim().endsWith("*")))
                .map(str -> str.replace("\n","").replace("\r",""))
                .toList();
        Map<String,List<String>> map = createMapOfInIdCodesAndTheirValueList(list,inIds);
        document.close();
        return (map);
    }

    public List<String> pdfAnalyserToGetBookmarks(String pdfPath) throws IOException {
        File file = new File(pdfPath);
        PDDocument document = Loader.loadPDF(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
//        pdfStripper.setStartPage(1);
//        pdfStripper.setEndPage(1);
        String text = pdfStripper.getText(document);
        System.out.println(text.replace("\n",""));
        PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
        List<String> bookmarks = new ArrayList<>();
        List<String> strings = printBookmark(outline, "", bookmarks);
        document.close();
        return (strings);
    }
    public List<String> printBookmark(PDOutlineNode bookmark, String indentation, List<String> bookMarks) {
        PDOutlineItem current = bookmark.getFirstChild();

        while (current != null) {
            System.out.println(indentation + current.getTitle());
            bookMarks.add(current.getTitle());
            printBookmark(current, indentation + "    ",bookMarks);
            current = current.getNextSibling();
        }
        return bookMarks;
    }
    public static ArrayList<String> extractMatches(String text, String regex) {
        // Compile the regular expression
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        // Use an ArrayList to store the matches
        ArrayList<String> matches = new ArrayList<>();

        // Iterate through matches and add them to the list
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    public List<String> concat(List<String> strings1,List<String> strings2){
        if(strings1.size() != strings2.size()) return  null;
        List<String> resultStrings = new ArrayList<>();
        for(int i=0;i<strings1.size();i++){
            String res = strings1.get(i)+" "+strings2.get(i);
            resultStrings.add(res);
        }
        return resultStrings;
    }
    public Map<String,List<String>> createMapOfInIdCodesAndTheirValueList(List<String> inid,List<String> value){
        System.out.println("inids"+inid);
        System.out.println();
        System.out.println("value"+value);
        if(inid.size() != value.size()) return  null;
        Map<String,List<String>> inidCodesMap = new LinkedHashMap<>();
        for(int i=0;i<inid.size();i++){
            if(inidCodesMap.containsKey(inid.get(i))){
                List<String> valList = inidCodesMap.get(inid.get(i));
                valList.add(value.get(i));
                inidCodesMap.put(inid.get(i),valList);
            }else{
                List<String> valList = new ArrayList<>();
                valList.add(value.get(i));
                inidCodesMap.put(inid.get(i),valList);
            }
        }
        return inidCodesMap;
    }

    public static ArrayList<String> splitAndAppend(String text, String regex) {
        // Compile the regular expression
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        // Use an ArrayList to store the modified strings
        ArrayList<String> result = new ArrayList<>();

        int lastIndex = 0;
        while (matcher.find()) {
            // Add the part before the match
            result.add(text.substring(lastIndex, matcher.start()));

            // Add the matched string along with the string that immediately follows it
            result.add(matcher.group() + text.substring(matcher.end(), Math.min(matcher.end() + 1, text.length())));

            lastIndex = matcher.end();
        }

        // Add the remaining part of the string
        if (lastIndex < text.length()) {
            result.add(text.substring(lastIndex));
        }

        return result;
    }
}
