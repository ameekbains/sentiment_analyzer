import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;

public class InstagramSentimentAnalyzer {
    private static final String INSTAGRAM_API_URL = "https://graph.instagram.com";
    private String accessToken;
    private StanfordCoreNLP sentimentPipeline;

    public InstagramSentimentAnalyzer(String accessToken) {
        this.accessToken = accessToken;
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        this.sentimentPipeline = new StanfordCoreNLP(props);
    }

    public List<String> fetchCommentsForPost(String postId) {
        List<String> comments = new ArrayList<>();
        try {
            String nextPageUrl = INSTAGRAM_API_URL + "/" + postId + "/comments?fields=text&access_token=" + accessToken;
            while (nextPageUrl != null) {
                URL url = new URL(nextPageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonSB = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonSB.append(line);
                }
                reader.close();
                
                JSONObject jsonObject = new JSONObject(jsonSB.toString());
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject comment = data.getJSONObject(i);
                    comments.add(comment.getString("text"));
                }
                
                JSONObject paging = jsonObject.optJSONObject("paging");
                nextPageUrl = null;
                if (paging != null && paging.has("next")) {
                    nextPageUrl = paging.getString("next");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

    public String analyzeSentiment(String text) {
        String sentimentResult = "Unknown";
        Annotation annotation = new Annotation(text);
        this.sentimentPipeline.annotate(annotation);
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
            sentimentResult = convertSentimentToString(sentiment);
            break; // Only analyze sentiment of the first sentence; consider analyzing all sentences.
        }
        return sentimentResult;
    }

    private String convertSentimentToString(int sentiment) {
        switch (sentiment) {
            case 0:
            case 1:
                return "Negative";
            case 2:
                return "Neutral";
            case 3:
            case 4:
                return "Positive";
            default:
                return "Unknown";
        }
    }

    public static void main(String[] args) {
        // You must replace "YOUR_ACCESS_TOKEN" with your actual Instagram Graph API access token
        // and "SPECIFIC_POST_ID" with the ID of the Instagram post you're interested in.
        String accessToken = "YOUR_ACCESS_TOKEN";
        String postId = "SPECIFIC_POST_ID";
        
        InstagramSentimentAnalyzer analyzer = new InstagramSentimentAnalyzer(accessToken);
        List<String> comments = analyzer.fetchCommentsForPost(postId);

        if (!comments.isEmpty()) {
            System.out.println("Analyzing comments sentiment...");
            for (String comment : comments) {
                String sentiment = analyzer.analyzeSentiment(comment);
                System.out.println("Comment: \"" + comment + "\" | Sentiment: " + sentiment);
            }
        } else {
            System.out.println("No comments found for the post with ID: " + postId);
        }
    }
}