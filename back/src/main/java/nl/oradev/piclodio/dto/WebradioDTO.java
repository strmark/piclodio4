package nl.oradev.piclodio.dto;

public class WebradioDTO {
    private Long id;
    private String name;
    private String url;
    private boolean isDefault;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
