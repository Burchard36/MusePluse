package com.burchard36.musepluse.resource;

/**
 * Enum for SongQuality for converting videos to ogg files with ffmpeg
 *
 * HORRIBLE = 16k
 * LOW = 32k
 * MEDIUM = 64k
 * HIGH = 96k
 * ULTRA = 128k
 */
public enum SongQuality {

    HORRIBLE,
    LOW,
    MEDIUM,
    HIGH,
    ULTRA;

    public static int getQualityNumber(final SongQuality quality) {
        switch (quality) {
            case HORRIBLE -> {
                return 8;
            }
            case LOW -> {
                return 16;
            }
            case MEDIUM -> {
                return 32;
            }
            case HIGH -> {
                return 48;
            }
            case ULTRA -> {
                return 64;
            }
        }
        return 0;
    }
}
