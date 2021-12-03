package nl.oradev.piclodio.dto;

public record AlarmDTO(
        Long id,
        String name,
        boolean monday,
        boolean tuesday,
        boolean wednesday,
        boolean thursday,
        boolean friday,
        boolean saturday,
        boolean sunday,
        int hour,
        int minute,
        int autoStopMinutes,
        boolean active,
        long webradio) {
}

