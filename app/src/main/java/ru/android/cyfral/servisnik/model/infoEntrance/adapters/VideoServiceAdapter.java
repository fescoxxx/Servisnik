package ru.android.cyfral.servisnik.model.infoEntrance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.infoEntrance.VideoService;

public class VideoServiceAdapter  extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<VideoService> objects;


    public VideoServiceAdapter(Context context,  List<VideoService> listVideoService) {
        ctx = context;
        objects = listVideoService;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.row_item_video_service, parent, false);
        }
        VideoService videoService = getVideoService(position);

        ((TextView) view.findViewById(R.id.video_service_title)).setText(videoService.getTitle());
        ((TextView) view.findViewById(R.id.video_service_comment)).setText(videoService.getBody());
        return view;
    }

    // по позиции
    VideoService getVideoService(int position) {
        return objects.get(position);
    }
}
