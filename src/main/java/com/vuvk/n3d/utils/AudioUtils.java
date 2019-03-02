/**
    Utilities for Encoding/Decoding audio files (Nuke3D Editor)
    Copyright (C) 2019 Anton "Vuvk" Shcherbatykh <vuvk69@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/**
 * https://github.com/a-schild/jave2
 */
package com.vuvk.n3d.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.DefaultFFMPEGLocator;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class AudioUtils {
    /*
     * audio formats
     */
    private static final String OGG_FORMAT = "ogg";
    private static final String MP3_FORMAT = "mp3";
    private static final String WAV_FORMAT = "wav";
    
    /** Минимальный битрейт.
     *  minimal bitrate. */
    public static final int BITRATE = 256000;
    /** Количество каналов для стерео.
     *  count of channels (stereo). */
    public static final int STEREO = 2; 
    /** Количество каналов для моно.
     *  count of channels (mono) */
    public static final int MONO = 1; 
    /** Частота дискретизации с хорошим качеством.
     *  sample rate for good quality. */
    public static final int GOOD_SAMPLE_RATE = 44100;

    /**
     * Используемый кодек для конверсии в ogg.
     * Codecs to be used.
     */
    private static final String OGG_CODEC = "libvorbis";
    
    /** Logger object */
    private static final Logger LOG = Logger.getLogger(AudioUtils.class.getName());
    
    
    /**
     * Конвертирование файла mp3 в файл формата ogg c установками по умолчанию.
     * @param source Исходный файл
     * @param target Целевой файл
     */
    public static void convertToOgg(File source, File target) {
        convertToOgg(source, target, BITRATE, STEREO, GOOD_SAMPLE_RATE);
    }
    /**
     * Конвертирование файла mp3 в файл формата ogg.
     * @param source Исходный файл
     * @param target Целевой файл
     * @param bitrate Битрейт (б/с)
     * @param channels Количество каналов. 2 - стерео, 1 - моно
     * @param sampleRate Частота дискредитации
     */
    public static void convertToOgg(File source, File target, int bitrate, int channels, int sampleRate) {
        Encoder encoder = new Encoder();
        
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setCodec(OGG_CODEC);
        audioAttributes.setBitRate(bitrate);
        audioAttributes.setChannels(channels);
        audioAttributes.setSamplingRate(sampleRate);
        
        EncodingAttributes encodeAttributes = new EncodingAttributes(); 
        encodeAttributes.setFormat(OGG_FORMAT);
        encodeAttributes.setAudioAttributes(audioAttributes);
        
        try {
            encoder.encode(new MultimediaObject(source), target, encodeAttributes);
        } catch (IllegalArgumentException | EncoderException ex) {
            LOG.log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
        }
    }
    
    private AudioUtils() {}
}
