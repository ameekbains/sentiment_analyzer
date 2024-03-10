# sentiment_analyzer
The Instagram Sentiment Analyzer is a Java program designed to fetch comments from a specific Instagram post and analyze the sentiment of those comments using the Stanford CoreNLP library. This tool provides insights into the overall sentiment expressed in the comments of an Instagram post.
#Requirements
Java Development Kit (JDK) 8 or later
Access to the Instagram Graph API
Stanford CoreNLP library

Setup
Obtain Instagram API Access Token: You need a valid access token to interact with the Instagram Graph API. Obtain an access token from the Instagram Developer website.

Install Stanford CoreNLP: Download the Stanford CoreNLP library from the official website and include it in your project.

Usage
Replace the placeholder "YOUR_ACCESS_TOKEN" in the main method of InstagramSentimentAnalyzer class with your actual Instagram Graph API access token.

Replace the placeholder "SPECIFIC_POST_ID" with the ID of the Instagram post you want to analyze.

Compile and run the InstagramSentimentAnalyzer class. This will fetch comments from the specified Instagram post and analyze their sentiment.

Notes
The sentiment analysis is based on the Stanford CoreNLP library and may not always accurately represent the sentiment expressed in the comments.
Ensure that your Instagram access token has the necessary permissions to access comment data.
