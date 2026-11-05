package io.shodo.asgard.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * RFC 9457 problem details enriched with HAL-style hypermedia links,
 * so a client knows what went wrong and where it can go next.
 */
public record ApiError(String type,
                       String title,
                       int status,
                       String detail,
                       String instance,
                       @JsonProperty("_links") Map<String, Link> links) {

    public record Link(String href, String method, String title) {
    }
}
