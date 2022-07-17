package com.itheima.pojo;

public class WebChatTextEms {

    private  Integer id;
    private  String from_url;

    public WebChatTextEms() {
    }

    public WebChatTextEms(Integer id, String from_url) {
        this.id = id;
        this.from_url = from_url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFrom_url() {
        return from_url;
    }

    public void setFrom_url(String from_url) {
        this.from_url = from_url;
    }

    @Override
    public String toString() {
        return "WebChatTextEms{" +
                "id=" + id +
                ", from_url='" + from_url + '\'' +
                '}';
    }
}
