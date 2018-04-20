package com.github.vkorobkov.jfixtures.result;

import com.github.vkorobkov.jfixtures.instructions.Instruction;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

@Getter
public class Result {
    private final Collection<Instruction> instructions;

    public Result(Collection<Instruction> instructions) {
        this.instructions = Collections.unmodifiableCollection(instructions);
    }
}
