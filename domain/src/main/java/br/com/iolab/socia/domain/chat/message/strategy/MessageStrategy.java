package br.com.iolab.socia.domain.chat.message.strategy;

import br.com.iolab.socia.domain.chat.message.strategy.perform.PerformMessageStrategyInput;
import br.com.iolab.socia.domain.chat.message.strategy.perform.PerformMessageStrategyOutput;
import lombok.NonNull;

public interface MessageStrategy {
    @NonNull
    PerformMessageStrategyOutput perform (@NonNull PerformMessageStrategyInput input);
}
