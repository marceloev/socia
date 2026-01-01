package br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.enums;

import br.com.iolab.socia.domain.chat.message.resource.types.MessageResourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static br.com.iolab.socia.domain.chat.message.resource.types.MessageResourceType.*;

@Getter
@AllArgsConstructor
public enum FileContentType {
    APPLICATION_OCTET_STREAM("application/octet-stream", "", DOCUMENT),
    IMAGE_JPEG("image/jpeg", ".jpeg", IMAGE),
    IMAGE_JPG("image/jpg", ".jpg", IMAGE),
    IMAGE_PNG("image/png", ".png", IMAGE),
    IMAGE_GIF("image/gif", ".gif", IMAGE),
    IMAGE_WEBP("image/webp", ".webp", IMAGE),
    IMAGE_SVG("image/svg+xml", ".svg", IMAGE),
    IMAGE_BMP("image/bmp", ".bmp", IMAGE),
    IMAGE_TIFF("image/tiff", ".tiff", IMAGE),
    IMAGE_ICO("image/vnd.microsoft.icon", ".ico", IMAGE),
    IMAGE_AVIF("image/avif", ".avif", IMAGE),
    APPLICATION_PDF("application/pdf", ".pdf", DOCUMENT),
    TEXT_PLAIN("text/plain", ".txt", DOCUMENT),
    TEXT_CSV("text/csv", ".csv", DOCUMENT),
    APPLICATION_RTF("application/rtf", ".rtf", DOCUMENT),
    APPLICATION_MSWORD("application/msword", ".doc", DOCUMENT),
    APPLICATION_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx", DOCUMENT),
    APPLICATION_MSEXCEL("application/vnd.ms-excel", ".xls", DOCUMENT),
    APPLICATION_XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx", DOCUMENT),
    APPLICATION_MSPOWERPOINT("application/vnd.ms-powerpoint", ".ppt", DOCUMENT),
    APPLICATION_PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx", DOCUMENT),
    APPLICATION_ODT("application/vnd.oasis.opendocument.text", ".odt", DOCUMENT),
    APPLICATION_ODS("application/vnd.oasis.opendocument.spreadsheet", ".ods", DOCUMENT),
    APPLICATION_ODP("application/vnd.oasis.opendocument.presentation", ".odp", DOCUMENT),
    APPLICATION_XML("application/xml", ".xml", DOCUMENT),
    TEXT_XML("text/xml", ".xml", DOCUMENT),
    TEXT_HTML("text/html", ".html", DOCUMENT),
    TEXT_CSS("text/css", ".css", DOCUMENT),
    APPLICATION_JSON("application/json", ".json", DOCUMENT),
    TEXT_JAVASCRIPT("text/javascript", ".js", DOCUMENT),
    APPLICATION_ZIP("application/zip", ".zip", DOCUMENT),
    APPLICATION_GZIP("application/gzip", ".gz", DOCUMENT),
    APPLICATION_RAR("application/vnd.rar", ".rar", DOCUMENT),
    APPLICATION_7Z("application/x-7z-compressed", ".7z", DOCUMENT),
    AUDIO_MPEG("audio/mpeg", ".mp3", AUDIO),
    AUDIO_WAV("audio/wav", ".wav", AUDIO),
    AUDIO_OGG("audio/ogg", ".ogg", AUDIO),
    AUDIO_WEBM("audio/webm", ".webm", AUDIO),
    AUDIO_AAC("audio/aac", ".aac", AUDIO),
    VIDEO_MP4("video/mp4", ".mp4", VIDEO),
    VIDEO_MPEG("video/mpeg", ".mpeg", VIDEO),
    VIDEO_OGG("video/ogg", ".ogv", VIDEO),
    VIDEO_WEBM("video/webm", ".webm", VIDEO),
    VIDEO_QUICKTIME("video/quicktime", ".mov", VIDEO);

    private final String value;
    private final String extension;
    private final MessageResourceType resourceType;

    public static FileContentType fromExtension(final String extension) {
        return Arrays.stream(FileContentType.values())
                .filter(ct -> Objects.equals(ct.extension, extension))
                .findAny()
                .orElse(null);
    }

    public static FileContentType fromFileName(final String fileName) {
        return FileContentType.fromExtension(
                Optional.ofNullable(fileName)
                        .filter(f -> f.contains("."))
                        .map(f -> f.substring(f.lastIndexOf(".")))
                        .orElse("")
        );
    }
}