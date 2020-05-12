package nl.oradev.piclodio.dto;

public class WebradioDTO {
    private Long id;
    private String name;
    private String url;
    private boolean isDefault;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

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
