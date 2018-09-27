package com.google.android.exoplayer2.VO;

public class DetailVO {
    private String idx;
    private String order_seq;
    private String start_loc_msec;
    private String end_loc_msec;
    private String start_loc;
    private String end_loc;
    private String foreign_sentence;
    private String kor_sentence;
    private String tot_time;
    private String file_name;

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getIdx() {
        return idx;
    }

    public void setOrder_seq(String order_seq) {
        this.order_seq = order_seq;
    }

    public String getOrder_seq() {
        return order_seq;
    }

    public void setStart_loc_msec(String start_loc_msec) {
        this.start_loc_msec = start_loc_msec;
    }

    public String getStart_loc_msec() {
        return start_loc_msec;
    }

    public void setEnd_loc_msec(String end_loc_msec) {
        this.end_loc_msec = end_loc_msec;
    }

    public String getEnd_loc_msec() {
        return end_loc_msec;
    }

    public void setStart_loc(String start_loc) {
        this.start_loc = start_loc;
    }

    public String getStart_loc() {
        return start_loc;
    }

    public void setEnd_loc(String end_loc) {
        this.end_loc = end_loc;
    }

    public String getEnd_loc() {
        return end_loc;
    }

    public void setForeign_sentence(String foreign_sentence) {
        this.foreign_sentence = foreign_sentence;
    }

    public String getForeign_sentence() {
        return foreign_sentence;
    }

    public void setKor_sentence(String kor_sentence) {
        this.kor_sentence = kor_sentence;
    }

    public String getKor_sentence() {
        return kor_sentence;
    }

    public void setTot_time(String tot_time) {
        this.tot_time = tot_time;
    }

    public String getTot_time() {
        return tot_time;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_name() {
        return file_name;
    }
}
