package com.example.aviv.wikirandom.Model;

/**
 * Created by Team Hurrange on 5/7/2017.
 */

// this class describes each User in our fireBase database
public class User
{
    private String nickname;
    private int score;
    private String userID;

    public User()
    {
    }

    public User(String nickname, String userID)
    {
        this.nickname = nickname;
        this.score = 0;
        this.userID = userID;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }
}