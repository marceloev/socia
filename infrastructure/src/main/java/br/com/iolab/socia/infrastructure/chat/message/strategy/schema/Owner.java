package br.com.iolab.socia.infrastructure.chat.message.strategy.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Owner {
    @JsonProperty("socia") SOCIA,
    @JsonProperty("user") USER,
    @JsonProperty("third_party") THIRD_PARTY
}
