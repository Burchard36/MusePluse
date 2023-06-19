package com.burchard36.musepluse.youtube;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.File;
import java.util.function.Consumer;

/**
 * Simple record class for holding pasued OGG Conversions
 *
 * this is typically only used if FFMPEG is not installed on the server
 *
 * @param builder {@link FFmpegBuilder} the builder that holds the task to run
 * @param convertedFile {@link File} the file that will get converted
 * @param callback {@link Consumer<File>} the callback from the origin call
 */
public record PausedOGGConversion(FFmpegBuilder builder, File convertedFile, Consumer<File> callback) {


}
