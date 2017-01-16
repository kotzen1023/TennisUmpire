package com.seventhmoon.tennisumpire.Data;

public class CurrentStatItem {
    private String title;
    private String statUp;
    private String statDown;

    public CurrentStatItem(String title, String statUp, String statDown)
    {
        super();
        this.title = title;
        this.statUp = statUp;
        this.statDown = statDown;
    }

    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatUp() {
        return statUp;
    }

    public void setStatUp(String statUp) {
        this.statUp = statUp;
    }

    public String getStatDown() {
        return statDown;
    }

    public void setStatDown(String statDown) {
        this.statDown = statDown;
    }
}
