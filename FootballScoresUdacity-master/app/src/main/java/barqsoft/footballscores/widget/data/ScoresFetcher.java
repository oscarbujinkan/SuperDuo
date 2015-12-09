package barqsoft.footballscores.widget.data;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.widget.models.Scores;
import barqsoft.footballscores.widget.utils.WidgetUtils;

/**
 * Created by oscarfuentes on 04-11-15.
 */
public class ScoresFetcher {

    private Context mContext;
    private String LOG_TAG="ScoresFetcher";
    final String SOCCER_SEASON = "soccerseason";
    final String SELF = "self";
    final String MATCH_DATE = "date";
    final String HOME_TEAM = "homeTeamName";
    final String AWAY_TEAM = "awayTeamName";
    final String RESULT = "result";
    final String HOME_GOALS = "goalsHomeTeam";
    final String AWAY_GOALS = "goalsAwayTeam";
    final String MATCH_DAY = "matchday";

    public ScoresFetcher(Context ctx){
        mContext=ctx;
    }

    public void getScores(final ScoresCallback callback){
        new AsyncTask<Void, Void, ArrayList<Scores>>() {
            @Override
            protected ArrayList<Scores> doInBackground(Void... params) {
                //Creating fetch URL
                final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
                final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
                //final String QUERY_MATCH_DAY = "matchday";

                Uri fetch_build = Uri.parse(BASE_URL).buildUpon().build();
                //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString()); //log spam
                HttpURLConnection m_connection = null;
                BufferedReader reader = null;
                String JSON_data = null;
                //Opening Connection
                try {
                    URL fetch = new URL(fetch_build.toString());
                    m_connection = (HttpURLConnection) fetch.openConnection();
                    m_connection.setRequestMethod("GET");
                    m_connection.addRequestProperty("X-Auth-Token",mContext.getString(R.string.api_key));
                    m_connection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = m_connection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    JSON_data = buffer.toString();
                }
                catch (Exception e)
                {
                    Log.e(LOG_TAG, "Exception here" + e.getMessage());
                }
                finally {
                    if(m_connection != null)
                    {
                        m_connection.disconnect();
                    }
                    if (reader != null)
                    {
                        try {
                            reader.close();
                        }
                        catch (IOException e)
                        {
                            Log.e(LOG_TAG,"Error Closing Stream");
                        }
                    }
                }
                try {
                    if (JSON_data != null) {
                        //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                        JSONArray matches = new JSONObject(JSON_data).getJSONArray("fixtures");
                        return parseScore(matches);
                    }
                }
                catch(Exception e)
                {
                    Log.e(LOG_TAG,e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Scores> scores) {
                if(scores!=null){
                    callback.onSuccess(scores);
                }else{
                    callback.onFail();
                }

            }
        }.execute();

    }

    private ArrayList<Scores> parseScore(JSONArray matches){

        ArrayList<Scores> scoresList=new ArrayList<Scores>();
        if(matches!=null){
            for (int j = 0; j < matches.length(); j++) {

                Scores scores = new Scores();
                JSONObject e = null;
                try {
                    e = matches.getJSONObject(j);
                    scores.away_crest = e.has(AWAY_TEAM) ? e.getString(AWAY_TEAM) : "";
                    scores.away_name = e.has(AWAY_TEAM) ? e.getString(AWAY_TEAM) : "";
                    scores.date = e.has(MATCH_DATE) ? e.getString(MATCH_DATE) : "";
                    scores.match_time = e.has(MATCH_DATE) ? WidgetUtils.matchTime(e.getString(MATCH_DATE)): "";
                    scores.home_crest = e.has(HOME_TEAM) ? e.getString(HOME_TEAM) : "";
                    scores.home_name = e.has(HOME_TEAM) ? e.getString(HOME_TEAM) : "";
                    JSONObject result= e.has(RESULT) ? (JSONObject) e.get(RESULT) : null;
                    if(result!=null) {
                        scores.score = Utilies.getScores(result.has(HOME_GOALS) ? result.getInt(HOME_GOALS) : 0, result.has(AWAY_GOALS) ? result.getInt(AWAY_GOALS) : 0);
                    }

                    if(WidgetUtils.isToday(scores.date)) {
                        scoresList.add(scores);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            if(scoresList.size()>0){
                return scoresList;
            }
        }
        return null;
    }

    public interface ScoresCallback{
        void onSuccess(ArrayList<Scores> scores);
        void onFail();
    }
}
