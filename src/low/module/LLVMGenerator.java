package low.module;

import ast.ASTNode;
import low.module.builders.LLVMValue;

import java.util.List;


    public class LLVMGenerator {
        private final LLVisitorMain visitor;

        public LLVMGenerator(LLVisitorMain visitor) {
            this.visitor = visitor;
        }

        public LLVisitorMain getVisitor() {
            return visitor;
        }

        public String generate(List<ASTNode> ast) {
            StringBuilder llvm = new StringBuilder();
            for (ASTNode node : ast) {
                LLVMValue val = node.accept(visitor);
                if (val != null && val.getCode() != null && !val.getCode().isBlank()) {
                    llvm.append(val.getCode());
                }
            }

            return llvm.toString();
        }

    }
