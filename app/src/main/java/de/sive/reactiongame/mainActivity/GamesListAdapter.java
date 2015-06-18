package de.sive.reactiongame.mainActivity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.sive.reactiongame.R;

/**
 * This class is an ListviewAdapter to pass the games data to the Listview
 *
 * @author Michael Landreh
 */
public class GamesListAdapter extends BaseAdapter {


    Context context;
    List<GameRow> rowItem;

    public GamesListAdapter(Context context, List<GameRow> rowItem) {
        this.context = context;
        this.rowItem = rowItem;
    }

    @Override
    public int getCount() {
        return rowItem.size();
    }

    @Override
    public Object getItem(int position) {

        return rowItem.get(position);
    }

    @Override
    public long getItemId(int position) {

        return rowItem.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.fragment_main_games_item, null);
        }

        TextView opponentView = (TextView) convertView.findViewById(R.id.opponent);
        TextView pointsOpponentView = (TextView) convertView.findViewById(R.id.points_opponent);

        GameRow row_pos = rowItem.get(position);
        // setting the image resource and title

        opponentView.setText(row_pos.getOpponent());
        pointsOpponentView.setText(row_pos.getPointsOpponent());

        return convertView;

    }
}
