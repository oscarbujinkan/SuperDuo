package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.widget.data.ScoresFetcher;
import barqsoft.footballscores.widget.models.Scores;

/**
 * Created by oscarfuentes on 04-11-15.
 */
public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
    private Context context = null;
    private int appWidgetId;

    private List<Scores> mScores = new ArrayList<Scores>();
    private ScoresFetcher mScoreFetcher;
    AppWidgetManager mAppWidgetManager;

    public WidgetRemoteViewsFactory(Context context, Intent intent)
    {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        //Log.d("AppWidgetId", String.valueOf(appWidgetId));
        mScoreFetcher=new ScoresFetcher(context);
        mAppWidgetManager = AppWidgetManager.getInstance(context);
    }

    private void updateWidgetListView()
    {
        mScoreFetcher.getScores(new ScoresFetcher.ScoresCallback() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onSuccess(ArrayList<Scores> scores) {
                mScores = scores;
                mAppWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.scores_list);

            }

            @Override
            public void onFail() {

            }
        });

    }

    @Override
    public int getCount()
    {
        return mScores.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public RemoteViews getLoadingView()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position)
    {
//        Log.d("WidgetCreatingView", "WidgetCreatingView");
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.scores_list_item);

//        Log.d("Loading", widgetList.get(position));
        remoteView.setTextViewText(R.id.home_name, mScores.get(position).home_name);
        remoteView.setTextViewText(R.id.away_name, mScores.get(position).away_name);
        remoteView.setTextViewText(R.id.score_textview, mScores.get(position).score);
        remoteView.setTextViewText(R.id.data_textview, mScores.get(position).match_time);
        remoteView.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                mScores.get(position).home_crest));
        remoteView.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                mScores.get(position).away_crest));

        return remoteView;
    }

    @Override
    public int getViewTypeCount()
    {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public boolean hasStableIds()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged()
    {
        // TODO Auto-generated method stub
//        updateWidgetListView();
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        mScores.clear();
    }
}
