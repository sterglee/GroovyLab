# Introduction #

`This page presents benchmarking code that can be executed in Glab. Also, we give our execution times, for comparison. `


# Binary Trees - Static Compilation #



@groovy.transform.CompileStatic


== Binary Trees  - Groovy 1.8.2 ==
{{{

public class BinaryTrees {

private final static int minDepth = 4;

public static void main(String[] args){
        final long millis = System.currentTimeMillis();

        int n = 20;
if (args.length > 0) n = Integer.parseInt(args[0]);

int maxDepth = (minDepth + 2 > n) ? minDepth + 2 : n;
int stretchDepth = maxDepth + 1;

int check = (TreeNode.bottomUpTree(0,stretchDepth)).itemCheck();
System.out.println("stretch tree of depth "+stretchDepth+"\t check: " + check);

TreeNode longLivedTree = TreeNode.bottomUpTree(0,maxDepth);

for (int depth=minDepth; depth<=maxDepth; depth+=2){
int iterations = 1 << (maxDepth - depth + minDepth);
check = 0;

for (int i=1; i<=iterations; i++){
check += (TreeNode.bottomUpTree(i,depth)).itemCheck();
check += (TreeNode.bottomUpTree(-i,depth)).itemCheck();
}
System.out.println((iterations*2) + "\t trees of depth " + depth + "\t check: " + check);
}
System.out.println("long lived tree of depth " + maxDepth + "\t check: "+ longLivedTree.itemCheck());

        long total = System.currentTimeMillis() - millis;
System.out.println("[Binary Trees-" + System.getProperty("project.name")+ " Benchmark Result: " + total + "]");
}


private static class TreeNode
{
private TreeNode left, right;
private int item;

TreeNode(int item){
this.item = item;
}

private static TreeNode bottomUpTree(int item, int depth){
if (depth>0){
return new TreeNode(
bottomUpTree(2*item-1, depth-1)
, bottomUpTree(2*item, depth-1)
, item
);
}
else {
return new TreeNode(item);
}
}

TreeNode(TreeNode left, TreeNode right, int item){
this.left = left;
this.right = right;
this.item = item;
}

private int itemCheck(){
// if necessary deallocate here
if (left==null)
                return item;
else {
                return item + left.itemCheck() - right.itemCheck();
            }
}
}
}

/*
stretch tree of depth 21	 check: -1
2097152	 trees of depth 4	 check: -2097152
524288	 trees of depth 6	 check: -524288
131072	 trees of depth 8	 check: -131072
32768	 trees of depth 10	 check: -32768
8192	 trees of depth 12	 check: -8192
2048	 trees of depth 14	 check: -2048
512	 trees of depth 16	 check: -512
128	 trees of depth 18	 check: -128
32	 trees of depth 20	 check: -32
long lived tree of depth 20	 check: -1
[Binary Trees-null Benchmark Result: 9909]

*/

// WITH NON-STATIC THE SAME CODE
stretch tree of depth 21	 check: -1
2097152	 trees of depth 4	 check: -2097152
524288	 trees of depth 6	 check: -524288
131072	 trees of depth 8	 check: -131072
32768	 trees of depth 10	 check: -32768
8192	 trees of depth 12	 check: -8192
2048	 trees of depth 14	 check: -2048
512	 trees of depth 16	 check: -512
128	 trees of depth 18	 check: -128
32	 trees of depth 20	 check: -32
long lived tree of depth 20	 check: -1
[Binary Trees-null Benchmark Result: 59492]


// WITH NON-STATIC AND INDY THE SAME CODE


}}}


== Fib - Groovy++ ==

{{{
@Typed class Fib {
    static int fibStaticTernary (int n) {
        n >= 2 ? fibStaticTernary(n-1) + fibStaticTernary(n-2) : 1
    }

    static int fibStaticIf (int n) {
        if(n >= 2) fibStaticIf(n-1) + fibStaticIf(n-2) else 1
    }

    int fibTernary (int n) {
        n >= 2 ? fibTernary(n-1) + fibTernary(n-2) : 1
    }

    int fibIf (int n) {
        if(n >= 2) fibIf(n-1) + fibIf(n-2) else 1
    }

    public static void main(String[] args) {
        def start = System.currentTimeMillis()
        Fib.fibStaticTernary(40)
        println("Groovypp(static ternary): ${System.currentTimeMillis() - start}ms")

        start = System.currentTimeMillis()
        Fib.fibStaticIf(40)
        println("Groovypp(static if): ${System.currentTimeMillis() - start}ms")

        start = System.currentTimeMillis()
        new Fib().fibTernary(40)
        println("Groovypp(instance ternary): ${System.currentTimeMillis() - start}ms")

        start = System.currentTimeMillis()
        new Fib().fibIf(40)
        println("Groovypp(instance if): ${System.currentTimeMillis() - start}ms")
    }
}

/*

Groovypp(static ternary): 912ms
Groovypp(static if): 909ms
Groovypp(instance ternary): 928ms
Groovypp(instance if): 927ms
*/
}}}

== Fib - Groovy 1.8.2 ==
{{{
class Fib {
    static int fibStaticTernary (int n) {
        n >= 2 ? fibStaticTernary(n-1) + fibStaticTernary(n-2) : 1
    }

    static int fibStaticIf (int n) {
        if(n >= 2) fibStaticIf(n-1) + fibStaticIf(n-2) else 1
    }

    int fibTernary (int n) {
        n >= 2 ? fibTernary(n-1) + fibTernary(n-2) : 1
    }

    int fibIf (int n) {
        if(n >= 2) fibIf(n-1) + fibIf(n-2) else 1
    }

    public static void main(String[] args) {
        def start = System.currentTimeMillis()
        Fib.fibStaticTernary(40)
        println("Groovy(static ternary): ${System.currentTimeMillis() - start}ms")

        start = System.currentTimeMillis()
        Fib.fibStaticIf(40)
        println("Groovy(static if): ${System.currentTimeMillis() - start}ms")

        start = System.currentTimeMillis()
        new Fib().fibTernary(40)
        println("Groovy(instance ternary): ${System.currentTimeMillis() - start}ms")

        start = System.currentTimeMillis()
        new Fib().fibIf(40)
        println("Groovy(instance if): ${System.currentTimeMillis() - start}ms")
    }
}

/*
 Groovy(static ternary): 16335ms
Groovy(static if): 16045ms
Groovy(instance ternary): 35477ms
Groovy(instance if): 35751ms

*/
}}}```