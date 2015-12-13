package twitter;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import twitter4j.Status;
import twitter4j.auth.Authorization;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.PropertyConfiguration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by Light on 12/12/15.
 */
public class TwitterStreamer {


    private static final String[] FILTERS = {"Disease","disease", "Enfermedad", "enfermedad", "Ebola", "ebola"};

    public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("Twitter Streamer Beca Colaboracion UCM");

        if (args.length > 0)
            conf.setMaster(args[0]);
        else
            conf.setMaster("local");

        JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(30000)); //Each 30 secs

        JavaDStream<Status> tweets = TwitterUtils.createStream(ssc, readTwitterCredentials("twitter.properties"),
                FILTERS);

        process(tweets);
        ssc.start();
        ssc.awaitTermination();
    }

    private static void process(JavaDStream tweets) {
        JavaDStream<String> statuses = tweets.map(
                new Function<Status, String>() {
                    public String call(Status status) {
                        return status.getText();
                    }
                }
        );
        statuses.dstream().saveAsTextFiles("twitter_stream","txt");
    }

    private static Authorization readTwitterCredentials(String file) {
        Properties twitterProps = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            twitterProps.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new OAuthAuthorization(new PropertyConfiguration(twitterProps));
    }
}
