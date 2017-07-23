package com.example.aviv.wikirandom.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.aviv.wikirandom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends Activity
{
    private ListView leaderboardTable;
    private FirebaseDatabase database;
    private DatabaseReference leaderboardRef;
    private CellAdapter cellAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_leaderboard);

        database = FirebaseDatabase.getInstance();
        leaderboardRef = database.getReference("leaderboard");

        leaderboardTable = (ListView) findViewById(R.id.leaderboardTable);
        final List<SingleCell> leaderboardList = new ArrayList<SingleCell>();

        // Here we retrieve the top 10 user from firebase, ordered by their key "score"
        leaderboardRef.orderByChild("score").limitToLast(10).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                leaderboardList.clear();
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();

                for (DataSnapshot user : users)
                {
                    SingleCell singleCell = user.getValue(SingleCell.class);
                    leaderboardList.add(singleCell);
                }

                Collections.reverse(leaderboardList);

                cellAdapter = new CellAdapter(LeaderboardActivity.this, leaderboardList);
                cellAdapter.notifyDataSetChanged();
                leaderboardTable.setAdapter(cellAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }
}

// This class represents a single cell in the listView of the leaderboards
class SingleCell
{
    private ImageView positionImage;
    private String nickname;
    private int score;

    public SingleCell()
    {
    }

    public SingleCell(@Nullable ImageView positionImage, String nickname, int score)
    {
        this.positionImage = positionImage;
        this.nickname = nickname;
        this.score = score;
    }

    public ImageView getPositionImage()
    {
        return positionImage;
    }

    public String getNickname()
    {
        return nickname;
    }

    public int getScore()
    {
        return score;
    }
}

// This adapter purpose is to "create" the view of each line in the leaderboards, using a custom configuration settings.
class CellAdapter extends BaseAdapter
{
    Context context;
    List<SingleCell> leadersList;

    public CellAdapter(Context context, List<SingleCell> leadersList)
    {
        this.context = context;
        this.leadersList = leadersList;
    }

    @Override
    public int getCount()
    {
        return leadersList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return leadersList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View cell = layoutInflater.inflate(R.layout.leader_cell, parent, false);

        ImageView positionImage = (ImageView) cell.findViewById(R.id.positionImage);
        TextView nicknameTV = (TextView) cell.findViewById(R.id.nicknameTV);
        TextView scoreTV = (TextView) cell.findViewById(R.id.scoreTV);

        SingleCell temp = leadersList.get(position);

        // this switch purpose is to check whether the user position is one of the top 3, and act accordingly
        switch (position)
        {
            case 0: positionImage.setImageResource(R.drawable.gold_medal);
                break;
            case 1: positionImage.setImageResource(R.drawable.silver_medal);
                break;
            case 2: positionImage.setImageResource(R.drawable.bronze_medal);
                break;
            default: positionImage.setImageResource(R.drawable.blank_medal);
                break;
        }

        nicknameTV.setText(temp.getNickname());
        scoreTV.setText("" + temp.getScore());

        return cell;
    }
}