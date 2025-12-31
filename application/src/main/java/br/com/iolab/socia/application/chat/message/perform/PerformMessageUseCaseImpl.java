package br.com.iolab.socia.application.chat.message.perform;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PerformMessageUseCaseImpl extends PerformMessageUseCase {
    @Override
    protected void perform (@NonNull PerformMessageUseCase.Input input) {
        throw new RuntimeException("Not implemented");
    }
}
