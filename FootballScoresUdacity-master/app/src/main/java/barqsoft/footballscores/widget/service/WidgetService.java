package barqsoft.footballscores.widget.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.widget.WidgetRemoteViewsFactory;

/**
 * Created by oscarfuentes on 04-11-15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetService extends RemoteViewsService
{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        return (new WidgetRemoteViewsFactory(this.getApplicationContext(), intent));
    }

}