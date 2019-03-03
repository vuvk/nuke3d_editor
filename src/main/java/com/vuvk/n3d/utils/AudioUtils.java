/**
    Utilities for convert formats of audio files (Nuke3D Editor)
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
package com.vuvk.n3d.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.EncoderProgressListener;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class AudioUtils {
    /*
     * audio formats and codec
     */
    public static enum AudioFormat {  
        WAV("wav", "libwavpack"),      
        OGG("ogg", "libvorbis"),
        MP3("mp3", "libmp3lame"),
        FLAC("flac", "flac");
        
        private String abbreviation;
        private String codec;
        
        AudioFormat(String abbreviation, String codec) {
            this.abbreviation = abbreviation;
            this.codec = codec;
        }

        String getAbbreviation() {
            return abbreviation;
        }
        
        String getCodec() {
            return abbreviation;
        }
    }
    
    /** Минимальный битрейт.
     *  minimal bitrate. */
    public static final int GOOD_BITRATE = 256000;
    /** Количество каналов для стерео.
     *  count of channels (stereo). */
    public static final int STEREO = 2; 
    /** Количество каналов для моно.
     *  count of channels (mono) */
    public static final int MONO = 1; 
    /** Частота дискретизации с хорошим качеством.
     *  sample rate for good quality. */
    public static final int GOOD_SAMPLE_RATE = 44100;
    
    /** Logger object */
    private static final Logger LOG = Logger.getLogger(AudioUtils.class.getName());
    
    /**
     * Конвертирование аудио-файла в файл указанного формата с установками по умолчанию.
     * @param source Исходный файл
     * @param target Целевой файл
     * @param outputAudioFormat Формат выходного файла
     * @return true в случае успеха
     */
    public static final boolean convert(File source, File target, AudioFormat outputAudioFormat) {
        return convert(source, target, 
                       outputAudioFormat, null, 
                       GOOD_BITRATE, STEREO, GOOD_SAMPLE_RATE);
    }
    
    /**
     * Конвертирование аудио-файла в файл указанного формата с установками по умолчанию и отслеживанием прогресса.
     * @param source Исходный файл
     * @param target Целевой файл
     * @param outputAudioFormat Формат выходного файла
     * @param listener Слушатель для отслеживания процесса конвертации
     * @return true в случае успеха
     */
    public static final boolean convert(File source, File target, 
                                        AudioFormat outputAudioFormat,
                                        EncoderProgressListener listener) {
        return convert(source, target, 
                       outputAudioFormat, listener, 
                       GOOD_BITRATE, STEREO, GOOD_SAMPLE_RATE);
    }
    
    /**
     * Конвертирование аудио-файла в файл указанного формата.
     * @param source Исходный файл
     * @param target Целевой файл
     * @param outputAudioFormat Формат выходного файла
     * @param bitrate Битрейт (б/с)
     * @param channels Количество каналов. 2 - стерео, 1 - моно
     * @param sampleRate Частота дискредитации
     * @return true в случае успеха
     */
    public static final boolean convert(File source, File target, 
                                        AudioFormat outputAudioFormat, 
                                        int bitrate, int channels, int sampleRate) {
        return convert(source, target, 
                       outputAudioFormat, null, 
                       bitrate, channels, sampleRate);
    }
    
    /**
     * Конвертирование аудио-файла в файл указанного формата с отслеживанием прогресса.
     * @param source Исходный файл
     * @param target Целевой файл
     * @param outputAudioFormat Формат выходного файла
     * @param listener Слушатель для отслеживания процесса конвертации (может быть null)
     * @param bitrate Битрейт (б/с)
     * @param channels Количество каналов. 2 - стерео, 1 - моно
     * @param sampleRate Частота дискредитации
     * @return true в случае успеха
     */
    public static final boolean convert(File source, File target, 
                                        AudioFormat outputAudioFormat, 
                                        EncoderProgressListener listener,
                                        int bitrate, int channels, int sampleRate) {
        Encoder encoder = new Encoder();
        
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setCodec(outputAudioFormat.getCodec());
        audioAttributes.setBitRate(bitrate);
        audioAttributes.setChannels(channels);
        audioAttributes.setSamplingRate(sampleRate);
        
        EncodingAttributes encodeAttributes = new EncodingAttributes(); 
        encodeAttributes.setFormat(outputAudioFormat.getAbbreviation());
        encodeAttributes.setAudioAttributes(audioAttributes);
        
        try {
            encoder.encode(new MultimediaObject(source), target, encodeAttributes, listener);
        } catch (IllegalArgumentException | EncoderException ex) {
            LOG.log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
            return false;
        }
        
        return true;       
    }
    
    private AudioUtils() {}
}
