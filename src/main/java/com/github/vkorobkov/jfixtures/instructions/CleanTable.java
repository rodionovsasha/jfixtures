package com.github.vkorobkov.jfixtures.instructions;

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CleanTable implements Instruction {
    private final String table;
    private final CleanMethod cleanMethod;

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this, this.cleanMethod);
    }
}
