package com.ysxsoft.gkpf.bean.response;

import java.util.ArrayList;
import java.util.List;

public class CacheResponse {
    private Object score;
    private boolean isConfirmed;

    public Object getScore() {
        return score;
    }

    public void setScore(Object score) {
        this.score = score;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
}
