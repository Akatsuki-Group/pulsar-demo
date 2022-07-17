package com.itheima.pojo;

public class PulsarTopicPojo {
    private Integer id;
    private String sid;
    private String ip;
    private String session_id;
    private String create_time;
    private String yearInfo;
    private String monthInfo;
    private String dayInfo;
    private String hourInfo;
    private String seo_source;
    private String area;
    private String origin_channel;
    private Integer msg_count;
    private  String from_url;

    public PulsarTopicPojo() {
    }

    public PulsarTopicPojo(Integer id, String sid, String ip, String session_id, String create_time, String yearInfo, String monthInfo, String dayInfo, String hourInfo, String seo_source, String area, String origin_channel, Integer msg_count, String from_url) {
        this.id = id;
        this.sid = sid;
        this.ip = ip;
        this.session_id = session_id;
        this.create_time = create_time;
        this.yearInfo = yearInfo;
        this.monthInfo = monthInfo;
        this.dayInfo = dayInfo;
        this.hourInfo = hourInfo;
        this.seo_source = seo_source;
        this.area = area;
        this.origin_channel = origin_channel;
        this.msg_count = msg_count;
        this.from_url = from_url;
    }

    public void setData(Integer id, String sid, String ip, String session_id, String create_time, String yearInfo, String monthInfo, String dayInfo, String hourInfo, String seo_source, String area, String origin_channel, Integer msg_count, String from_url) {
        this.id = id;
        this.sid = sid;
        this.ip = ip;
        this.session_id = session_id;
        this.create_time = create_time;
        this.yearInfo = yearInfo;
        this.monthInfo = monthInfo;
        this.dayInfo = dayInfo;
        this.hourInfo = hourInfo;
        this.seo_source = seo_source;
        this.area = area;
        this.origin_channel = origin_channel;
        this.msg_count = msg_count;
        this.from_url = from_url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
    public String getYearInfo() {
        return yearInfo;
    }

    public void setYearInfo(String yearInfo) {
        this.yearInfo = yearInfo;
    }

    public String getMonthInfo() {
        return monthInfo;
    }

    public void setMonthInfo(String monthInfo) {
        this.monthInfo = monthInfo;
    }

    public String getDayInfo() {
        return dayInfo;
    }

    public void setDayInfo(String dayInfo) {
        this.dayInfo = dayInfo;
    }

    public String getHourInfo() {
        return hourInfo;
    }

    public void setHourInfo(String hourInfo) {
        this.hourInfo = hourInfo;
    }

    public String getSeo_source() {
        return seo_source;
    }

    public void setSeo_source(String seo_source) {
        this.seo_source = seo_source;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOrigin_channel() {
        return origin_channel;
    }

    public void setOrigin_channel(String origin_channel) {
        this.origin_channel = origin_channel;
    }

    public Integer getMsg_count() {
        return msg_count;
    }

    public void setMsg_count(Integer msg_count) {
        this.msg_count = msg_count;
    }

    public String getFrom_url() {
        return from_url;
    }

    public void setFrom_url(String from_url) {
        this.from_url = from_url;
    }

    @Override
    public String toString() {
        return "PulsarTopicPojo{" +
                "id=" + id +
                ", sid='" + sid + '\'' +
                ", ip='" + ip + '\'' +
                ", session_id='" + session_id + '\'' +
                ", create_time='" + create_time + '\'' +
                ", yearInfo='" + yearInfo + '\'' +
                ", monthInfo='" + monthInfo + '\'' +
                ", dayInfo='" + dayInfo + '\'' +
                ", hourInfo='" + hourInfo + '\'' +
                ", seo_source='" + seo_source + '\'' +
                ", area='" + area + '\'' +
                ", origin_channel='" + origin_channel + '\'' +
                ", msg_count=" + msg_count +
                ", from_url='" + from_url + '\'' +
                '}';
    }
}
