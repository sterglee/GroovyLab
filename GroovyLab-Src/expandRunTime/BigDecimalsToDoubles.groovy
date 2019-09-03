
// this package expands the run time for GroovyLab
package expandRunTime 

import org.codehaus.groovy.ast.ClassCodeExpressionTransformer
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.customizers.CompilationCustomizer

public class ConstantTransformer extends ClassCodeExpressionTransformer {

    private final SourceUnit unit;

    ConstantTransformer(final SourceUnit unit) {
        this.unit = unit
    }

    public Expression transform(Expression exp) {
        if (exp instanceof ConstantExpression && exp.type == ClassHelper.BigDecimal_TYPE) {
            def expression = new ConstantExpression(exp.value as Double, true)
//            assert expression.type == ClassHelper.double_TYPE
            return expression
        }
        return exp.transformExpression(this)


}

    @Override
    protected SourceUnit getSourceUnit() {
        unit
    }
}
