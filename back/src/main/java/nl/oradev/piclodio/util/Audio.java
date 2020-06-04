package nl.oradev.piclodio.util;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Audio {

    private static final String HARDWARE_DESCRIPTION = "hw:0";  // "hw:1"
    private static final String HARDWARE_ITEM = "PCM"; // "Speaker"

    private Audio() {
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

    public static Line getSpeakerOutputLine() {
        return getMixers()
                .stream()
                .filter(mixer -> mixer.getMixerInfo().getName().contains(HARDWARE_DESCRIPTION))
                .map(mixer -> getAvailableOutputLines(mixer)
                        .stream()
                        .filter(line -> line.getLineInfo().toString().contains(HARDWARE_ITEM))
                        .findFirst()
                        .orElse(null))
                .findFirst()
                .orElse(null);
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
        return Arrays.stream(controls)
                .map(control -> getControl(type, control))
                .findFirst()
                .orElse(null);
    }

    private static Control getControl(Type type, Control control) {
        if (Objects.equals(control.getType(), type)) {
            return control;
        }
        if (control instanceof CompoundControl) {
            CompoundControl compoundControl = (CompoundControl) control;
            Control member = findControl(type, compoundControl.getMemberControls());
            if (member != null) {
                return member;
            }
        }
        return null;
    }

    public static List<Mixer> getMixers() {
        return Arrays.stream(AudioSystem.getMixerInfo())
                .map(AudioSystem::getMixer)
                .collect(Collectors.toList());
    }

    public static List<Line> getAvailableOutputLines(Mixer mixer) {
        return getAvailableLines(mixer, mixer.getTargetLineInfo());
    }

    public static List<Line> getAvailableInputLines(Mixer mixer) {
        return getAvailableLines(mixer, mixer.getSourceLineInfo());
    }

    private static List<Line> getAvailableLines(Mixer mixer, Line.Info[] lineInfos) {
        return Arrays.stream(lineInfos)
                .map(lineInfo -> getLineIfAvailable(mixer, lineInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static Line getLineIfAvailable(Mixer mixer, Line.Info lineInfo) {
        try {
            return mixer.getLine(lineInfo);
        } catch (LineUnavailableException ex) {
            return null;
        }
    }

    public static boolean open(Line line) {
        if (line.isOpen()) {
            return true;
        } else {
            try {
                line.open();
                return true;
            } catch (LineUnavailableException ex) {
                return false;
            }
        }
    }

    public static String toString(Line line) {
        return (line != null) ? (line.getLineInfo().toString()) : null;
    }

}
