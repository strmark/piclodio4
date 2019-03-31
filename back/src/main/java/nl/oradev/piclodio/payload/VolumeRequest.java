package nl.oradev.piclodio.payload;

public class VolumeRequest {
    private String volume;

    public VolumeRequest() {
    }

    public VolumeRequest(String volume) {
        super();
        this.volume = volume;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
