package com.burchard36.libs.ffmpeg;

import java.io.File;
import java.util.function.Consumer;

public record FFTask(File from, File to, Consumer<File> callback) {
}
