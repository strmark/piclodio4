package nl.oradev.piclodio.payload;

public class PlayerRequest {
    private String status;
    private Long webradio;
    private Long autoStopMinutes;
    private String url;

    public PlayerRequest() {
    }

    public PlayerRequest(String status, Long webradio) {
        super();
        this.status = status;
        this.webradio = webradio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getAutoStopMinutes() {
        return autoStopMinutes;
    }

    public void setAutoStopMinutes(Long autoStopMinutes) {
        this.autoStopMinutes = autoStopMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getWebradio() {
        return webradio;
    }

    public void setWebradio(Long webradio) {
        this.webradio = webradio;
    }
}
