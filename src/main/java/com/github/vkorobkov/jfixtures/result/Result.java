package com.github.vkorobkov.jfixtures.result;

import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.instructions.InstructionVisitor;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

@Getter
public class Result {
    private final Collection<Instruction> instructions;

    public Result(Collection<Instruction> instructions) {
        this.instructions = Collections.unmodifiableCollection(instructions);
    }

    public void visit(InstructionVisitor visitor) {
        instructions.forEach(instruction -> instruction.accept(visitor));
    }
}
