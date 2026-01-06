package br.com.iolab.socia.domain.chat.message.strategy;

import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.strategy.perform.PerformMessageStrategyInput;
import lombok.NonNull;

public interface MessageStrategy {
    @NonNull
    Message perform (@NonNull PerformMessageStrategyInput input);
}
