package nl.oradev.piclodio.model;

public class Player {

    private String playerStatus;

    public String getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(String playerStatus) {
        this.playerStatus = playerStatus;
    }

    public Player(String playerStatus) {
        this.playerStatus = playerStatus;
    }


}
