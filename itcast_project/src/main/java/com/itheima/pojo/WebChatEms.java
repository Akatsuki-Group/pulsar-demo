package com.itheima.pojo;

public class WebChatEms {
    private Integer id;
    private String session_id;
    private String ip;
    private String create_time;
    private String area;
    private String origin_channel;
    private String seo_source;
    private String sid;
    private Integer msg_count;

    public WebChatEms() {
    }

    public WebChatEms(Integer id, String session_id, String ip, String create_time, String area, String origin_channel, String seo_source, String sid, Integer msg_count) {
        this.id = id;
        this.session_id = session_id;
        this.ip = ip;
        this.create_time = create_time;
        this.area = area;
        this.origin_channel = origin_channel;
        this.seo_source = seo_source;
        this.sid = sid;
        this.msg_count = msg_count;
    }

    public Integer getMsg_count() {
        return msg_count;
    }

    public void setMsg_count(Integer msg_count) {
        this.msg_count = msg_count;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
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

    public String getSeo_source() {
        return seo_source;
    }

    public void setSeo_source(String seo_source) {
        this.seo_source = seo_source;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }


    @Override
    public String toString() {
        return "WebChatEms{" +
                "id=" + id +
                ", session_id='" + session_id + '\'' +
                ", ip='" + ip + '\'' +
                ", create_time='" + create_time + '\'' +
                ", area='" + area + '\'' +
                ", origin_channel='" + origin_channel + '\'' +
                ", seo_source='" + seo_source + '\'' +
                ", sid='" + sid + '\'' +
                ", msg_count=" + msg_count +
                '}';
    }
}
