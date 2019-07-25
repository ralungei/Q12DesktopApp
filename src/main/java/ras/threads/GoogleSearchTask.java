package ras.threads;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ras.utils.ProbabilityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleSearchTask implements Runnable {

    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    private static final int numOfSearches = 30;

    // TEXT TO BE SEARCHED
    // ENTITIES IN TEXT TO BE COMPARED
    // IF NO ENTITIES TEXT TO BE COMPARED
    // VALUES TO BE INCREASED

    private AtomicReference<ArrayList<Integer>> results;

    private String searchTerm;
    private AtomicInteger searchedProb;

    private String firstAnswerText;
    private String secondAnswerText;
    private String thirdAnswerText;

    private AtomicReference<ArrayList<String>> questionEntities;
    private AtomicReference<ArrayList<String>> firstAnswerEntities;
    private AtomicReference<ArrayList<String>> secondAnswerEntities;
    private AtomicReference<ArrayList<String>> thirdAnswerEntities;

    private AtomicInteger firstAnswerProb;
    private AtomicInteger secAnswerProb;
    private AtomicInteger thirdAnswerProb;

    private Semaphore toUISem;

    GoogleSearchTask(Semaphore toUISem,
                     AtomicReference<ArrayList<Integer>> results,
                     String searchTerm,
                     AtomicInteger searchedProb,
                     String firstAnswerText,
                     String secondAnswerText,
                     String thirdAnswerText,
                     AtomicInteger firstAnswerProb,
                     AtomicInteger secAnswerProb,
                     AtomicInteger thirdAnswerProb,
                     AtomicReference<ArrayList<String>> questionEntities,
                     AtomicReference<ArrayList<String>> firstAnswerEntities,
                     AtomicReference<ArrayList<String>> secondAnswerEntities,
                     AtomicReference<ArrayList<String>> thirdAnswerEntities) {

        this.results = results;

        this.toUISem = toUISem;

        this.searchTerm = searchTerm;
        this.searchedProb = searchedProb;

        this.firstAnswerText = firstAnswerText;
        this.secondAnswerText = secondAnswerText;
        this.thirdAnswerText = thirdAnswerText;

        this.questionEntities = questionEntities;
        this.firstAnswerEntities = firstAnswerEntities;
        this.secondAnswerEntities = secondAnswerEntities;
        this.thirdAnswerEntities = thirdAnswerEntities;

        this.firstAnswerProb = firstAnswerProb;
        this.secAnswerProb = secAnswerProb;
        this.thirdAnswerProb = thirdAnswerProb;
    }

    @Override
    public void run() {
        Document result = googleFullQASearch();
        if(result != null)
            count(result);
        System.out.println("Result -> " + firstAnswerProb + " " + secAnswerProb + " " + thirdAnswerProb);


        if(results.get().isEmpty()){
            results.get().add(firstAnswerProb.get());
            results.get().add(secAnswerProb.get());
            results.get().add(thirdAnswerProb.get());
        }
        else {
            results.get().set(0, firstAnswerProb.get() + results.get().get(0));
            results.get().set(1, secAnswerProb.get() + results.get().get(1));
            results.get().set(2, thirdAnswerProb.get() + results.get().get(2));
        }

        System.out.println("Google Search Task " + searchTerm + " releasing UI Semaphore");
        toUISem.release();
    }


    /**
     * Perform the search in google and analyze the results
     */
    private Document googleFullQASearch() {
        System.out.println("Iniciando búsqueda de pregunta en Google.");

        String searchURL = GOOGLE_SEARCH_URL + "?q=" + this.searchTerm + "&num=" + numOfSearches;

        System.out.println(searchURL);

        Document doc;
        try {
//			String userAgent = "Mozilla/5.0 Chrome/26.0.1410.64 Safari/537.31";
            String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36";
            doc = Jsoup.connect(searchURL).userAgent(userAgent).followRedirects(true).get();
            System.out.println("Búsqueda en Google finalizada.");

            return doc;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void count(Document doc){
        //xpdopen
        Elements results = doc.select(".st");
        Elements topResults = doc.select(".xpdopen");

        for (int j = 0; j < topResults.size(); j++) {
            Element result = topResults.get(j);
            String content = result.text();
            countEntireAnswersInSearchResult(content, j);
        }

        for (int i = 0; i < results.size(); i++) {
            Element result = results.get(i);
            String content = result.text();
            countEntireAnswersInSearchResult(content, i);
        }
    }

    private void countEntireAnswersInSearchResult(String searchContent, int numResultado) {
        //  Si es Full Question Full Answers los tokens son las respuestas completas (Chequear si hay entities de respuestas = NO HAY, Chequear si hay entidades de pregunta = NO HAY)
        //  Si es Ent Question Full Answers los tokens son las entidades de la pregunta (Chequear si hay entities de pregunta = SI HAY)
        //  Si es Full Question Ent Answers los tokens son las entidades de las respuestas (Chequear si hay entities de respuestas = SI HAY)

        List<String> tokens = new ArrayList<>();


        if(questionEntities != null){
            // Hay entidades de pregunta
            // ENT Q FULL A SEARCH
            tokens = questionEntities.get();
        }
        else{
            if(firstAnswerEntities != null && secondAnswerEntities!= null && thirdAnswerEntities!= null) {
                // FULL Q ENT A
                tokens.addAll(firstAnswerEntities.get());
                tokens.addAll(secondAnswerEntities.get());
                tokens.addAll(thirdAnswerEntities.get());
            }
            else {
                // FULL Q FULL A SEARCH
                tokens.add(firstAnswerText);
                tokens.add(secondAnswerText);
                tokens.add(thirdAnswerText);
            }
        }


        String stripped;
        for(int i = 0; i < tokens.size(); i++){
            stripped = org.apache.commons.lang3.StringUtils.stripAccents(tokens.get(i));
            if(!tokens.get(i).equals(stripped) && !tokens.contains(stripped))
                tokens.add(stripped);
        }

        System.out.println("The final tokens are " + tokens);

        String patternString = "\\b?(" + StringUtils.join(tokens, "|") + ")?\\b";

        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(searchContent);

        while (matcher.find()) {
            //System.out.println("Matcher group in GoogleSearchTask is : " + matcher.group());
            int weighing = ProbabilityUtils.weigh(numResultado);

            if(searchedProb != null && !questionEntities.get().isEmpty()) {
                // ESTOY BUSCANDO UNA RESPUESTA DETERMINADA Y HE ENCONTRADO ENTIDADES DE LA PREGUNTA DENTRO
                searchedProb.set(searchedProb.get() + weighing);
            }
            else{
                // ESTOY BUSCANDO UNA PREGUNTA Y HE ENCONTRADO ALGUN TOKEN DE LA RESPUESTA, LUEGO DEBO CHEQUEAR A QUIEN PERTENECE EL TOKEN
                // SI EL TOKEN ENCONTRADO COINCIDE AL 100% CON UNO DE LOS TEXTOS DE RESPUESTAS SIMPLEMENTE DEBO USAR ESTO
                // SI EL TOKEN ENCONTRADO NO COINCIDE CON NINGUNO DE LOS TEXTOS SE COMPRUEBAN LAS ENTIDADES
                if (StringUtils.stripAccents(matcher.group().toLowerCase()).equals(StringUtils.stripAccents(firstAnswerText.toLowerCase())) || (firstAnswerEntities != null && !firstAnswerEntities.get().isEmpty() && firstAnswerEntities.get().contains(matcher.group())))
                    firstAnswerProb.set(firstAnswerProb.get() + weighing);
                else if (StringUtils.stripAccents(matcher.group().toLowerCase()).equals(StringUtils.stripAccents(secondAnswerText.toLowerCase())) || matcher.group().equals(org.apache.commons.lang3.StringUtils.stripAccents(secondAnswerText)) || (secondAnswerEntities != null && !secondAnswerEntities.get().isEmpty() && secondAnswerEntities.get().contains(matcher.group())))
                    secAnswerProb.set(secAnswerProb.get() + weighing);
                else if (StringUtils.stripAccents(matcher.group().toLowerCase()).equals(StringUtils.stripAccents(thirdAnswerText.toLowerCase())) || (thirdAnswerEntities != null && !thirdAnswerEntities.get().isEmpty() && thirdAnswerEntities.get().contains(matcher.group())))
                    thirdAnswerProb.set(thirdAnswerProb.get() + weighing);
            }


        }
    }






}
