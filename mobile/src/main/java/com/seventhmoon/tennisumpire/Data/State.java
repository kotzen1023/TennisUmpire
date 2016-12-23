package com.seventhmoon.tennisumpire.Data;

public class State {
    private byte current_set;
    private boolean isServe;
    private boolean isInTiebreak;
    private boolean isFinish;
    private byte setsUp;
    private byte setsDown;

    private byte set_1_game_up;
    private byte set_1_game_down;
    private byte set_1_point_up;
    private byte set_1_point_down;
    private byte set_1_tiebreak_point_up;
    private byte set_1_tiebreak_point_down;
    //private boolean set_1_serve;

    private byte set_2_game_up;
    private byte set_2_game_down;
    private byte set_2_point_up;
    private byte set_2_point_down;
    private byte set_2_tiebreak_point_up;
    private byte set_2_tiebreak_point_down;
    //private boolean set_2_serve;

    private byte set_3_game_up;
    private byte set_3_game_down;
    private byte set_3_point_up;
    private byte set_3_point_down;
    private byte set_3_tiebreak_point_up;
    private byte set_3_tiebreak_point_down;
    //private boolean set_3_serve;

    private byte set_4_game_up;
    private byte set_4_game_down;
    private byte set_4_point_up;
    private byte set_4_point_down;
    private byte set_4_tiebreak_point_up;
    private byte set_4_tiebreak_point_down;
    //private boolean set_4_serve;

    private byte set_5_game_up;
    private byte set_5_game_down;
    private byte set_5_point_up;
    private byte set_5_point_down;
    private byte set_5_tiebreak_point_up;
    private byte set_5_tiebreak_point_down;
    //private boolean set_5_serve;

    public byte getCurrent_set() {
        return current_set;
    }

    public void setCurrent_set(byte current_set) {
        this.current_set = current_set;
    }

    public boolean isServe() {
        return isServe;
    }

    public void setServe(boolean isServe) {
        this.isServe = isServe;
    }

    public boolean isInTiebreak() {
        return isInTiebreak;
    }

    public void setInTiebreak(boolean isInTiebreak) {
        this.isInTiebreak = isInTiebreak;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public byte getSetsUp() {
        return setsUp;
    }

    public void setSetsUp(byte setsUp) {
        this.setsUp = setsUp;
    }

    public byte getSetsDown() {
        return setsDown;
    }

    public void setSetsDown(byte setsDown) {
        this.setsDown = setsDown;
    }

    public byte getSet_game_up(byte set) {
        byte ret = 0;
        switch (set) {
            case 1:
                ret = set_1_game_up;
                break;
            case 2:
                ret = set_2_game_up;
                break;
            case 3:
                ret = set_3_game_up;
                break;
            case 4:
                ret = set_4_game_up;
                break;
            case 5:
                ret = set_5_game_up;
                break;
        }

        return ret;
    }

    public void setSet_game_up(byte set, byte game_up) {
        switch (set) {
            case 1:
                this.set_1_game_up = game_up;
                break;
            case 2:
                this.set_2_game_up = game_up;
                break;
            case 3:
                this.set_3_game_up = game_up;
                break;
            case 4:
                this.set_4_game_up = game_up;
                break;
            case 5:
                this.set_5_game_up = game_up;
                break;

        }
    }

    public byte getSet_game_down(byte set) {
        byte ret = 0;
        switch (set) {
            case 1:
                ret = set_1_game_down;
                break;
            case 2:
                ret = set_2_game_down;
                break;
            case 3:
                ret = set_3_game_down;
                break;
            case 4:
                ret = set_4_game_down;
                break;
            case 5:
                ret = set_5_game_down;
                break;
        }

        return ret;
    }

    public void setSet_game_down(byte set, byte game_down) {
        switch (set) {
            case 1:
                this.set_1_game_down = game_down;
                break;
            case 2:
                this.set_2_game_down = game_down;
                break;
            case 3:
                this.set_3_game_down = game_down;
                break;
            case 4:
                this.set_4_game_down = game_down;
                break;
            case 5:
                this.set_5_game_down = game_down;
                break;

        }
        //this.set_1_game_up = set_1_game_up;
    }

    public byte getSet_point_up(byte set) {
        byte ret = 0;
        switch (set) {
            case 1:
                ret = set_1_point_up;
                break;
            case 2:
                ret = set_2_point_up;
                break;
            case 3:
                ret = set_3_point_up;
                break;
            case 4:
                ret = set_4_point_up;
                break;
            case 5:
                ret = set_5_point_up;
                break;
        }

        return ret;
    }

    public void  setSet_point_up(byte set, byte point_up) {
        switch (set) {
            case 1:
                this.set_1_point_up = point_up;
                break;
            case 2:
                this.set_2_point_up = point_up;
                break;
            case 3:
                this.set_3_point_up = point_up;
                break;
            case 4:
                this.set_4_point_up = point_up;
                break;
            case 5:
                this.set_5_point_up = point_up;
                break;

        }
    }

    public byte getSet_point_down(byte set) {
        byte ret = 0;
        switch (set) {
            case 1:
                ret = set_1_point_down;
                break;
            case 2:
                ret = set_2_point_down;
                break;
            case 3:
                ret = set_3_point_down;
                break;
            case 4:
                ret = set_4_point_down;
                break;
            case 5:
                ret = set_5_point_down;
                break;
        }

        return ret;
    }

    public void setSet_point_down(byte set, byte point_down) {
        switch (set) {
            case 1:
                this.set_1_point_down = point_down;
                break;
            case 2:
                this.set_2_point_down = point_down;
                break;
            case 3:
                this.set_3_point_down = point_down;
                break;
            case 4:
                this.set_4_point_down = point_down;
                break;
            case 5:
                this.set_5_point_down = point_down;
                break;

        }
    }

    public byte getSet_tiebreak_point_up(byte set) {
        byte ret = 0;
        switch (set) {
            case 1:
                ret = set_1_tiebreak_point_up;
                break;
            case 2:
                ret = set_2_tiebreak_point_up;
                break;
            case 3:
                ret = set_3_tiebreak_point_up;
                break;
            case 4:
                ret = set_4_tiebreak_point_up;
                break;
            case 5:
                ret = set_5_tiebreak_point_up;
                break;
        }

        return ret;
    }

    public void setSet_tiebreak_point_up(byte set, byte tiebreak_point_up) {
        switch (set) {
            case 1:
                this.set_1_tiebreak_point_up = tiebreak_point_up;
                break;
            case 2:
                this.set_2_tiebreak_point_up = tiebreak_point_up;
                break;
            case 3:
                this.set_3_tiebreak_point_up = tiebreak_point_up;
                break;
            case 4:
                this.set_4_tiebreak_point_up = tiebreak_point_up;
                break;
            case 5:
                this.set_5_tiebreak_point_up = tiebreak_point_up;
                break;

        }
    }

    public byte getSet_tiebreak_point_down(byte set) {
        byte ret = 0;
        switch (set) {
            case 1:
                ret = set_1_tiebreak_point_down;
                break;
            case 2:
                ret = set_2_tiebreak_point_down;
                break;
            case 3:
                ret = set_3_tiebreak_point_down;
                break;
            case 4:
                ret = set_4_tiebreak_point_down;
                break;
            case 5:
                ret = set_5_tiebreak_point_down;
                break;
        }

        return ret;
    }

    public void setSet_tiebreak_point_down(byte set, byte tiebreak_point_down) {
        switch (set) {
            case 1:
                this.set_1_tiebreak_point_down = tiebreak_point_down;
                break;
            case 2:
                this.set_2_tiebreak_point_down = tiebreak_point_down;
                break;
            case 3:
                this.set_3_tiebreak_point_down = tiebreak_point_down;
                break;
            case 4:
                this.set_4_tiebreak_point_down = tiebreak_point_down;
                break;
            case 5:
                this.set_5_tiebreak_point_down = tiebreak_point_down;
                break;

        }
    }
}
