package nl.oradev.piclodio.dto;

public record WebradioDTO(
        Long id,
        String name,
        String url,
        boolean isDefault) {
}
