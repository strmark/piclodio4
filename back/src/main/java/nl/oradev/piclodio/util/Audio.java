package nl.oradev.piclodio.util;

import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;

public class Audio {

    private static String hardwareDescription= "hw:0";  // "hw:1"
    private static String hardwareItem = "PCM"; // "Speaker"

    private Audio(){
    }

    public static void setSpeakerOutputVolume(float value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException(
                    "VolumeTransfer can only be set to a value from 0 to 1. Given value is illegal: " + value);
        }
        Line line = getSpeakerOutputLine();
        if (line != null) {
            boolean opened = open(line);
            try {
                FloatControl control = getVolumeControl(line);
                if (control != null) {
                    control.setValue(value);
                } else {
                    throw new NullPointerException("VolumeTransfer control not found in speaker port: " + toString(line));
                }
            } finally {
                if (opened) {
                    line.close();
                }
            }
        } else {
            throw new NullPointerException("Speaker output port not found");
        }
    }

    public static Float getSpeakerOutputVolume() {
        Line line = getSpeakerOutputLine();
        if (line != null) {
            boolean opened = open(line);
            try {
                FloatControl control = getVolumeControl(line);
                if (control != null) {
                    return control.getValue();
                }
            } finally {
                if (opened) {
                    line.close();
                }
            }
        }
        return null;
    }

    public static void setSpeakerOutputMute(boolean value) {
        Line line = getSpeakerOutputLine();
        if (line != null) {
            boolean opened = open(line);
            try {
                BooleanControl control = getMuteControl(line);
                if (control != null) {
                    control.setValue(value);
                } else {
                    throw new NullPointerException("Mute control not found in speaker port: " + toString(line));
                }
            } finally {
                if (opened) {
                    line.close();
                }
            }
        } else {
            throw new NullPointerException("Speaker output port not found");
        }
    }

    public static Boolean getSpeakerOutputMute() {
        Line line = getSpeakerOutputLine();
        if (line != null) {
            boolean opened = open(line);
            try {
                BooleanControl control = getMuteControl(line);
                if (control != null) {
                    return control.getValue();
                }
            } finally {
                if (opened) {
                    line.close();
                }
            }
        }
        return false;
    }

    public static Line getSpeakerOutputLine() {
        for (Mixer mixer : getMixers()) {
            if (mixer.getMixerInfo().getName().contains(hardwareDescription)) {
                for (Line line : getAvailableOutputLines(mixer)) {
                    if (line.getLineInfo().toString().contains(hardwareItem)) {
                        return line;
                    }
                }
            }
        }
        return null;
    }

    public static FloatControl getVolumeControl(Line line) {
        if (line.isOpen()) {
            return (FloatControl) findControl(FloatControl.Type.VOLUME, line.getControls());
        } else {
            throw new IllegalStateException("Line is closed: " + toString(line));
        }
    }

    public static BooleanControl getMuteControl(Line line) {
        if (line.isOpen()) {
            return (BooleanControl) findControl(BooleanControl.Type.MUTE, line.getControls());
        } else {
            throw new IllegalStateException("Line is closed: " + toString(line));
        }
    }

    private static Control findControl(Type type, Control... controls) {
        if (controls == null || controls.length == 0) {
            return null;
        }
        for (Control control : controls) {
            if (control.getType().equals(type))  {
                return control;
            }
            if (control instanceof CompoundControl) {
                CompoundControl compoundControl = (CompoundControl) control;
                Control member = findControl(type, compoundControl.getMemberControls());
                if (member != null) {
                    return member;
                }
            }
        }
        return null;
    }

    public static List<Mixer> getMixers() {
        Info[] infos = AudioSystem.getMixerInfo();
        List<Mixer> mixers = new ArrayList<>(infos.length);
        for (Info info : infos) {
            mixers.add(AudioSystem.getMixer(info));
        }
        return mixers;
    }

    public static List<Line> getAvailableOutputLines(Mixer mixer) {
        return getAvailableLines(mixer, mixer.getTargetLineInfo());
    }

    public static List<Line> getAvailableInputLines(Mixer mixer) {
        return getAvailableLines(mixer, mixer.getSourceLineInfo());
    }

    private static List<Line> getAvailableLines(Mixer mixer, Line.Info[] lineInfos) {
        List<Line> lines = new ArrayList<>(lineInfos.length);
        for (Line.Info lineInfo : lineInfos) {
            Line line;
            line = getLineIfAvailable(mixer, lineInfo);
            if (line != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static Line getLineIfAvailable(Mixer mixer, Line.Info lineInfo) {
        try {
            return mixer.getLine(lineInfo);
        } catch (LineUnavailableException ex) {
            return null;
        }
    }

    public static boolean open(Line line) {
        if (!line.isOpen()) {
            try {
                line.open();
                return true;
            } catch (LineUnavailableException ex) {
                return false;
            }
        } else {
            return true;
        }
    }

    public static String toString(Control control) {
        if (control != null) {
            return control.toString() + " (" + control.getType().toString() + ")";
        } else {
            return null;
        }
    }

    public static String toString(Line line) {
        if (line != null) {
            return line.getLineInfo().toString();
        } else {
            return null;
        }
    }

    public static String toString(Mixer mixer) {
        if (mixer == null) {
            return null;
        } else {
            Info info = mixer.getMixerInfo();
            return info.getName() + " (" + info.getDescription() + ")" + (mixer.isOpen() ? " [open]" : " [closed]");
        }
    }

}
