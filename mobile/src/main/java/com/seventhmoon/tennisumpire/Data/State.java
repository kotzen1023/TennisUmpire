package com.seventhmoon.tennisumpire.Data;

public class State {
    private byte current_set;
    private boolean isServe;
    private boolean isInTiebreak;
    private boolean isFinish;
    private boolean isSecondServe;
    private boolean isInBreakPoint;
    private byte setsUp;
    private byte setsDown;
    private long duration;

    private byte aceCountUp;
    private byte aceCountDown;
    private short firstServeUp;
    private short firstServeDown;
    private short firstServeMissUp;
    private short firstServeMissDown;
    private short secondServeUp;
    private short secondServeDown;
    private byte breakPointUp;
    private byte breakPointDown;
    private byte breakPointMissUp;
    private byte breakPointMissDown;

    private short firstServeWonUp;
    private short firstServeWonDown;
    private short firstServeLostUp;
    private short firstServeLostDown;
    private short secondServeWonUp;
    private short secondServeWonDown;
    private short secondServeLostUp;
    private short secondServeLostDown;

    private byte doubleFaultUp;
    private byte doubleFaultDown;
    private short unforcedErrorUp;
    private short unforcedErrorDown;
    private short forehandWinnerUp;
    private short forehandWinnerDown;
    private short backhandWinnerUp;
    private short backhandWinnerDown;
    private short forehandVolleyUp;
    private short forehandVolleyDown;
    private short backhandVolleyUp;
    private short backhandVolleyDown;
    private byte foulToLoseUp;
    private byte foulToLoseDown;

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

    public boolean isSecondServe() {
        return isSecondServe;
    }

    public void setSecondServe(boolean isSecondServe) {
        this.isSecondServe = isSecondServe;
    }

    public boolean isInBreakPoint() {
        return isInBreakPoint;
    }

    public void setInBreakPoint(boolean isInBreakPoint) {
        this.isInBreakPoint = isInBreakPoint;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public byte getAceCountUp() {
        return  aceCountUp;
    }

    public void setAceCountUp(byte aceCountUp) {
        this.aceCountUp = aceCountUp;
    }

    public byte getAceCountDown() {
        return  aceCountDown;
    }

    public void setAceCountDown(byte aceCountDown) {
        this.aceCountDown = aceCountDown;
    }

    public short getFirstServeUp() {
        return firstServeUp;
    }

    public void setFirstServeUp(short firstServeUp) {
        this.firstServeUp = firstServeUp;
    }

    public short getFirstServeDown() {
        return firstServeDown;
    }

    public void setFirstServeDown(short firstServeDown) {
        this.firstServeDown = firstServeDown;
    }

    public short getFirstServeMissUp() {
        return firstServeMissUp;
    }

    public void setFirstServeMissUp(short firstServeMissUp) {
        this.firstServeMissUp = firstServeMissUp;
    }

    public short getFirstServeMissDown() {
        return firstServeMissDown;
    }

    public void setFirstServeMissDown(short firstServeMissDown) {
        this.firstServeMissDown = firstServeMissDown;
    }

    public short getSecondServeUp() {
        return secondServeUp;
    }

    public void setSecondServeUp(short secondServeUp) {
        this.secondServeUp = secondServeUp;
    }

    public short getSecondServeDown() {
        return secondServeDown;
    }

    public void setSecondServeDown(short secondServeDown) {
        this.secondServeDown = secondServeDown;
    }

    public byte getBreakPointUp() {
        return breakPointUp;
    }

    public void setBreakPointUp(byte breakPointUp) {
        this.breakPointUp = breakPointUp;
    }

    public byte getBreakPointDown() {
        return breakPointDown;
    }

    public void setBreakPointDown(byte breakPointDown) {
        this.breakPointDown = breakPointDown;
    }

    public byte getBreakPointMissUp() {
        return breakPointMissUp;
    }

    public void setBreakPointMissUp(byte breakPointMissUp) {
        this.breakPointMissUp = breakPointMissUp;
    }

    public byte getBreakPointMissDown() {
        return breakPointMissDown;
    }

    public void setBreakPointMissDown(byte breakPointMissDown) {
        this.breakPointMissDown = breakPointMissDown;
    }

    //serve won
    public short getFirstServeWonUp() {
        return firstServeWonUp;
    }

    public void setFirstServeWonUp(short firstServeWonUp) {
        this.firstServeWonUp = firstServeWonUp;
    }

    public short getFirstServeWonDown() {
        return firstServeWonDown;
    }

    public void setFirstServeWonDown(short firstServeWonDown) {
        this.firstServeWonDown = firstServeWonDown;
    }

    public short getFirstServeLostUp() {
        return firstServeLostUp;
    }

    public void setFirstServeLostUp(short firstServeLostUp) {
        this.firstServeLostUp = firstServeLostUp;
    }

    public short getFirstServeLostDown() {
        return firstServeLostDown;
    }

    public void setFirstServeLostDown(short firstServeLostDown) {
        this.firstServeLostDown = firstServeLostDown;
    }

    public short getSecondServeWonUp() {
        return secondServeWonUp;
    }

    public void setSecondServeWonUp(short secondServeWonUp) {
        this.secondServeWonUp = secondServeWonUp;
    }

    public short getSecondServeWonDown() {
        return secondServeWonDown;
    }

    public void setSecondServeWonDown(short secondServeWonDown) {
        this.secondServeWonDown = secondServeWonDown;
    }

    public short getSecondServeLostUp() {
        return secondServeLostUp;
    }

    public void setSecondServeLostUp(short secondServeLostUp) {
        this.secondServeLostUp = secondServeLostUp;
    }

    public short getSecondServeLostDown() {
        return secondServeLostDown;
    }

    public void setSecondServeLostDown(short secondServeLostDown) {
        this.secondServeLostDown = secondServeLostDown;
    }

    public byte getDoubleFaultUp() {
        return doubleFaultUp;
    }

    public void setDoubleFaultUp(byte doubleFaultUp) {
        this.doubleFaultUp = doubleFaultUp;
    }

    public byte getDoubleFaultDown() {
        return doubleFaultDown;
    }

    public void setDoubleFaultDown(byte doubleFaultDown) {
        this.doubleFaultDown = doubleFaultDown;
    }

    public short getUnforceErrorUp() {
        return unforcedErrorUp;
    }

    public void setUnforceErrorUp(short unforcedErrorUp) {
        this.unforcedErrorUp = unforcedErrorUp;
    }

    public short getUnforceErrorDown() {
        return unforcedErrorDown;
    }

    public void setUnforceErrorDown(short unforcedErrorDown) {
        this.unforcedErrorDown = unforcedErrorDown;
    }

    public short getForehandWinnerUp() {
        return forehandWinnerUp;
    }

    public void setForehandWinnerUp(short forehandWinnerUp) {
        this.forehandWinnerUp = forehandWinnerUp;
    }

    public short getForehandWinnerDown() {
        return forehandWinnerDown;
    }

    public void setForehandWinnerDown(short forehandWinnerDown) {
        this.forehandWinnerDown = forehandWinnerDown;
    }

    public short getBackhandWinnerUp() {
        return backhandWinnerUp;
    }

    public void setBackhandWinnerUp(short backhandWinnerUp) {
        this.backhandWinnerUp = backhandWinnerUp;
    }

    public short getBackhandWinnerDown() {
        return backhandWinnerDown;
    }

    public void setBackhandWinnerDown(short backhandWinnerDown) {
        this.backhandWinnerDown = backhandWinnerDown;
    }

    public short getForehandVolleyUp() {
        return forehandVolleyUp;
    }

    public void setForehandVolleyUp(short forehandVolleyUp) {
        this.forehandVolleyUp = forehandVolleyUp;
    }

    public short getForehandVolleyDown() {
        return forehandVolleyDown;
    }

    public void setForehandVolleyDown(short forehandVolleyDown) {
        this.forehandVolleyDown = forehandVolleyDown;
    }

    public short getBackhandVolleyUp() {
        return backhandVolleyUp;
    }

    public void setBackhandVolleyUp(short backhandVolleyUp) {
        this.backhandVolleyUp = backhandVolleyUp;
    }

    public short getBackhandVolleyDown() {
        return backhandVolleyDown;
    }

    public void setBackhandVolleyDown(short backhandVolleyDown) {
        this.backhandVolleyDown = backhandVolleyDown;
    }

    public byte getFoulToLoseUp() {
        return foulToLoseUp;
    }

    public void setFoulToLoseUp(byte foulToLoseUp) {
        this.foulToLoseUp = foulToLoseUp;
    }

    public byte getFoulToLoseDown() {
        return foulToLoseDown;
    }

    public void setFoulToLoseDown(byte foulToLoseDown) {
        this.foulToLoseDown = foulToLoseDown;
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
